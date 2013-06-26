package eu.dime.model.storage;

import eu.dime.control.LoadingHandler;
import eu.dime.model.CALLTYPES;
import eu.dime.model.GenItem;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.ItemFactory;
import eu.dime.model.specialitem.MergeItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelTypeNotFoundException;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.restapi.RestApiAccess;
import eu.dime.restapi.RestApiConfiguration;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.io.FileHelper;
import sit.json.JSONObject;
import sit.json.JSONParseException;
import sit.json.JSONParser;
import sit.json.JSONPathAccessException;
import sit.sstl.HashMapSet;

/**
 * Thread-safe store for di.me GenItems
 *
 * @author simon
 */
public class DimeMemory {

    private static final int MAX_TRIES_FOR_COMMIT = 3;
    private final RestApiConfiguration restConf;
    private HashMapSet<TYPES, DimeTable> storage = new HashMapSet<TYPES, DimeTable>();
    public static final String path = "dime/dime.mem/";
    private final Object dirtyItemsLock = new Object();
    private List<ManagedItem> dirtyItems = new Vector<ManagedItem>();
    private Map<String, LoadingHandler> produceItemListeners = new HashMap<String, LoadingHandler>();
    private final Object produceItemListenerLock = new Object();
    private boolean persistence = false;
    boolean remoteAccess = false;
    private FileHelper fh = new FileHelper();
    private String hoster = null;
    private String owner = null;
    private RequestManager fetchRequests = null;

    public DimeMemory(String hoster, String owner, RestApiConfiguration restConf) {
        this.restConf = restConf;
        if (hoster == null) {
            throw new RuntimeException("Illegal hoster! hoster:" + hoster + " owner:" + owner);
        }
        if (owner == null || owner.length() == 0) {
            throw new RuntimeException("Illegal owner! hoster:" + hoster + " owner:" + owner);
        }
        this.hoster = hoster;
        this.owner = owner;
    }

    /**
     * initializes the DimeMemory and sets persistence settings when called all
     * existing data is gets dropped. In case the DimeMemory was already
     * initialized with persistence==true this function tries to safe the data
     * first before doing re-initializing
     *
     * TODO: add storage path as additional parameter (path)
     *
     * @param hoster
     * @param owner
     * @param persistence
     * @param remoteAccess
     * @throws InitStorageFailedException
     */
    public synchronized void initStorage(String hoster, String owner, boolean persistence, boolean remoteAccess) throws InitStorageFailedException {
        synchronized (dirtyItemsLock) {
            // ############################################
            // storing done - now init new storage
            // ############################################
            this.persistence = persistence;
            this.remoteAccess = remoteAccess;
            this.hoster = hoster;
            this.owner = owner;
            // clean old storage
            storage.clear();
            dirtyItems.clear();// should be empty anyway - but to be sure
            fetchRequests = new RequestManager();
            if (persistence) {
                // load old storage
                Logger.getLogger(DimeMemory.class.getName()).log(Level.INFO, "loading memory storage");
                // check for path and create it if necessary
                fh.createDirectoriesIfNotExist(path);
                // load storage for all types
                for (TYPES type : TYPES.values()) {
                    loadStorageForType(type);
                }
            }
        }// synchronize dirtyItemsLock
    }

    public synchronized void shutdownStorage() {
        storeDirtyEntries();
    }

    /**
     * returns true if fetch triggerRequest was triggered
     *
     * @param type
     * @return
     */
    private boolean triggerFetchRequest(TYPES type) {
        if (!remoteAccess) {
            return false;
        }
        return fetchRequests.triggerRequest(type);
    }

    /**
     * drops and reloads DimeTable for given type in case remoteAccess == true:
     * table will be added to requested resources in case persistence == true:
     * the corresponding file will be loaded again
     *
     * @param type
     */
    public synchronized void reload(TYPES type) {
        if (remoteAccess) {
            fetchRequests.markForRefetch(type);
        }
        synchronized (dirtyItemsLock) {
            storeDirtyEntries(); // call storeDirtyEntries inside of the lock to make sure all open operations have been completed
            storage.remove(type);
            if (persistence) {
                loadStorageForType(type);
            }
        }
    }

    /**
     * for handling updates from the service this function will not create a
     * dirty entry
     *
     * ATTENTION TODO notifications will not work properly in persistence mode!!!!
     *
     * @param notification
     */
    public void handleNotification(NotificationItem notification) {
        try {
            TYPES elementType = ModelHelper.getMTypeFromString(notification.getElement().getType());
            if ((notification.getOperation().equals(NotificationItem.OPERATION_CREATE)) || (notification.getOperation().equals(NotificationItem.OPERATION_UPDATE))) {
                fetchRequests.markForRefetch(elementType);
            } else if (notification.getOperation().equals(NotificationItem.OPERATION_DELETE)) {
                synchronized (dirtyItemsLock) {
                    getCreateTypeTable(elementType).remove(notification.getElement().getGuid());
                }
            }
        } catch (ModelTypeNotFoundException ex) {
            Logger.getLogger(DimeMemory.class.getName()).log(Level.SEVERE, "unable to handle notification for element type:" + notification.getElement().getType());
        }
    }

    private void notifyProduceItemListeners(GenItem item, String oldGuid) {
        synchronized (produceItemListenerLock) {
            LoadingHandler listener = produceItemListeners.remove(oldGuid);
            if (listener != null) {
                listener.notifyItemCreated(item, oldGuid);
            }
        }
    }

    private void registerOnItemProduction(LoadingHandler listener, String oldGuid) {
        synchronized (produceItemListenerLock) {
            produceItemListeners.put(oldGuid, listener);
        }
    }
    
    /**
     * returns a copy of the DimeTable containing all items of the type given.
     *
     * @param type
     * @return
     * @throws CacheFailException
     */
    public synchronized List<GenItem> getAllItems(TYPES type) throws CacheFailException {
        if (triggerFetchRequest(type)) {
            throw new CacheFailException();
        }
        DimeTable table = storage.get(type);
        if (table != null) {
            return getCopyOfItems(table);
        }
        throw new CacheFailException();
    }
    
    public synchronized void createItem(GenItem item, LoadingHandler listener) {
        Logger.getLogger(DimeMemory.class.getName()).log(Level.FINE, "create item:" + item.getGuid());
        if (listener == null) {
            throw new RuntimeException("listener must not be null!");
        }
        registerOnItemProduction(listener, item.getGuid());
        synchronized (dirtyItemsLock) {
            dirtyItems.add(new ManagedItem(item, CALLTYPES.AT_ITEM_POST_NEW));
        }
    }
    
    public synchronized GenItem getItem(TYPES type, String guid) throws CacheFailException {
        if (triggerFetchRequest(type)) {
            throw new CacheFailException();
        }
        DimeTable table = storage.get(type);
        if (table != null) {
            GenItem item = table.get(guid);
            if (item != null) {
                return item.getClone();
            }
            return null; // item not found even when cache valid
        }
        throw new CacheFailException();
    }

    public synchronized void updateItem(GenItem item) {
        Logger.getLogger(DimeMemory.class.getName()).log(Level.FINE, "update item:" + item.getGuid());
        GenItem myItem = item.getClone(); // store a copy of the item transfered
        synchronized (dirtyItemsLock) {
            getCreateTypeTable(myItem.getMType()).add(myItem);
            dirtyItems.add(new ManagedItem(item, CALLTYPES.AT_ITEM_POST_UPDATE));
        }
    }
    
    public synchronized GenItem removeItem(String guid, TYPES type) {
        synchronized (dirtyItemsLock) {
            DimeTable table = getCreateTypeTable(type);
            GenItem result = table.remove(guid);
            dirtyItems.add(new ManagedItem(result, CALLTYPES.AT_ITEM_DELETE));
            return result.getClone();
        }
    }

    public synchronized void mergeItem(GenItem firstItem, List<String> itemGuids, LoadingHandler listener) {
        if (listener == null) throw new RuntimeException("listener must not be null!");
        if (itemGuids.size() < 2) throw new RuntimeException("merge requires at least two items");
        if (!firstItem.getGuid().equals(itemGuids.get(0))) throw new RuntimeException("first GUID does not match firstItem.getGuid()");
        registerOnItemProduction(listener, firstItem.getGuid());
        synchronized (dirtyItemsLock) {
            // add special managed item with first item and the guid list of all items
            dirtyItems.add(new ManagedItem(firstItem, itemGuids.toArray(new String[itemGuids.size()])));
        }
    }

    /**
     * creates table for type !!Attention!! dirty lock and dirty marker needs to
     * be set by calling function if required!!
     *
     * @param type
     * @return
     */
    private DimeTable getCreateTypeTable(TYPES type) {
        if (!storage.contains(type)) {
        	storage.add(new DimeTable(type));
        }
        return storage.get(type);
    }

    private List<GenItem> getCopyOfItems(Iterable<GenItem> items) {
        Vector<GenItem> result = new Vector<GenItem>();
        for (GenItem item : items) {
            result.add(item.getClone());
        }
        return result;
    }

    private JSONObject compileTypeStorageObject(DimeTable typeStore) {
        JSONObject result = new JSONObject("root");
        JSONObject jsonStore = new JSONObject(ModelHelper.getStringType(typeStore.getType()));
        result.addChild(jsonStore);
        for (GenItem entry : typeStore) {
            jsonStore.addItem(entry.getJSONObject());
        }
        return result;
    }

    private String genFileName(TYPES type) {
        String result = hoster + "." + ModelHelper.getStringType(type) + "." + owner + ".json";
        result = fh.getValidFileName(result, "_");
        result = path + result;
        if (result.length() > 240) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Filename too long --> might cause problems on some OS:\n" + result);
            // TODO genFileName: handle too long filenames properly
        }
        return result;
    }

    private synchronized void updateParents(TYPES type, String[] guidList) {
        TYPES parentType = ModelHelper.getParentType(type);
        if (parentType == null) {
            return;
        }// else
        DimeTable table = getCreateTypeTable(type);
        String newGuid = guidList[0];
        for (GenItem genParent : table) {
            DisplayableItem parent = (DisplayableItem) genParent;
            // check the remaining items - counting from 1
            for (int i = 1; i < guidList.length; i++) {
                if (parent.containsItem(guidList[i])) {
                    if (!parent.containsItem(newGuid)) {
                        parent.addItem(newGuid);
                    }
                    parent.removeItem(guidList[i]);
                }
            }
        }
        if (persistence) { // TODO check potential synchronization issues
            storeType(type);
        }
    }

    private synchronized DisplayableItem localMergeItems(String[] guidList, TYPES type) {
        DimeTable table = getCreateTypeTable(type);
        DisplayableItem result = (DisplayableItem) table.get(guidList[0]);
        // merge the remaining items - counting from 1
        for (int i = 1; i < guidList.length; i++) {
            DisplayableItem parentItem = (DisplayableItem) table.get(guidList[i]);
            for (String childItem : parentItem.getItems()) {
                if (!result.containsItem(childItem)) {
                    result.addItem(childItem);
                }
            }
            table.remove(parentItem.getGuid());
        }
        updateParents(type, guidList);
        return result;
    }

    private GenItem commitItem(ManagedItem item) {
        if (!remoteAccess) {
            if (item.operation != CALLTYPES.MERGE) {
                return item.item.getClone(); // return a clone for making sure it is independent from the caller
            } else { // MERGE - OPERATION
                return localMergeItems(item.guidList, item.item.getMType());
            }
        }
        if (item.operation == CALLTYPES.AT_ITEM_POST_NEW) {
            return RestApiAccess.postItemNew(hoster, owner, item.item.getMType(), item.item, restConf);
        } else if (item.operation == CALLTYPES.AT_ITEM_POST_UPDATE) {
            return RestApiAccess.postItemUpdate(hoster, owner, item.item.getMType(), item.item, restConf);
        } else if (item.operation == CALLTYPES.AT_ITEM_DELETE) {
            return RestApiAccess.removeItem(hoster, owner, item.item.getMType(), item.item.getGuid(), restConf);
        } else if (item.operation == CALLTYPES.MERGE) {
            MergeItem mergeItem = new MergeItem(item.item.getGuid());
            mergeItem.addMergedItem(item.guidList);
            // update parents will be done by the remote node -- trigger
            // refetch(TODO: better check this)
            if (ModelHelper.hasParent(item.item.getMType())) {// TODO accessing ModelHelper from here is not nice style
                fetchRequests.markForRefetch(ModelHelper.getParentType(item.item.getMType()));
            }
            return RestApiAccess.postItemMerge(hoster, owner, item.item.getMType(), mergeItem, restConf);
        }
        throw new RuntimeException("Operation not supported: " + item.operation);
    }

    private void storeType(TYPES type) {
        if (!persistence) {
            return;
        }
        try {
            String filename = genFileName(type);
            JSONObject typeStorageObject = compileTypeStorageObject(storage.get(type));
            fh.writeToFile(filename, typeStorageObject.toJson());
            Logger.getLogger(DimeMemory.class.getName()).log(Level.INFO, "stored " + filename);
        } catch (IOException ex) {
            Logger.getLogger(DimeMemory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadStorageForType(TYPES type) {
        JSONParser parser = new JSONParser();
        // first clean storage for type
        storage.add(new DimeTable(type));
        String fileName = genFileName(type);
        if (fh.fileExists(fileName)) {
            try {
                JSONObject typeStorageObject = parser.parseJSON(fh.readFromTextFile(fileName));
                for (JSONObject entry : typeStorageObject.getChild(ModelHelper.getStringType(type)).getItems()) {
                    GenItem item = ItemFactory.createNewItemByType(type);
                    item.readJSONObject(entry);
                    if (item.getGuid() != null) {
                        storage.get(type).add(item);
                    } else {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "found item without guid in " + fileName);
                    }
                }
            } catch (InvalidJSONItemException ex) {
                Logger.getLogger(DimeMemory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONPathAccessException ex) {
                Logger.getLogger(DimeMemory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DimeMemory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONParseException ex) {
                Logger.getLogger(DimeMemory.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            // System.out.println("file not found: " + fileName);
        }
    }

    private void sortMItemsByType(List<ManagedItem> items) {
        Collections.sort(items, new Comparator<ManagedItem>() {
            public int compare(ManagedItem item1, ManagedItem item2) {
                return item1.item.getType().compareTo(item2.item.getType());
            }
        });
    }

    /**
     *
     */
    synchronized void storeDirtyEntries() {
        synchronized (dirtyItemsLock) {
            sortMItemsByType(dirtyItems); // sort items by type - so we can call storeType only once for each type ...
            TYPES currType = null;
            Iterator<ManagedItem> iter = dirtyItems.iterator();
            while (iter.hasNext()) {
                ManagedItem currItem = iter.next();
                DimeTable myTable = getCreateTypeTable(currItem.item.getMType());
                // handle failed commits
                if (currItem.getFailCount() >= MAX_TRIES_FOR_COMMIT) {
                    Logger.getLogger(DimeMemory.class.getName()).log(Level.WARNING, "COMMIT for item " + currItem.item.getGuid() 
                            + " (" + currItem.item.getType() + ") failed after " + currItem.getFailCount() + " tries. Giving up!");
                    notifyProduceItemListeners(null, currItem.item.getGuid());
                    iter.remove();
                    continue;
                }
                // handle remote
                GenItem commitedItem = commitItem(currItem);
                if (commitedItem == null) {// something went wrong - let's try
                    // again later
                    currItem.incFailCount();
                    continue;
                }
                if (currItem.operation == CALLTYPES.MERGE) { // special action for merge operation
                    // remove all old items
                    for (String myGuid : currItem.guidList) {
                        myTable.remove(myGuid);
                    }
                }
                if (!commitedItem.getGuid().equals(currItem.item.getGuid())) { // we got a new guid!!! delete old entry
                    myTable.remove(currItem.item.getGuid());
                }
                if (currItem.operation != CALLTYPES.AT_ITEM_DELETE) { // for delete -> don't add it again
                    // update the item
                    myTable.add(commitedItem); // since create item is not adding the items - its important to add the here in any case !!!
                }
                // handle persistence
                if (commitedItem.getMType() != currType) {
                    currType = commitedItem.getMType();
                    storeType(currType);
                }
                // notifiy listeners
                notifyProduceItemListeners(commitedItem, currItem.item.getGuid());
                iter.remove();
            }
        }
    }

    void fetchRequestedTypes() {
        if (!remoteAccess) {
            return;
        }
        // fetch all requested resources
        while (fetchRequests.hasOpenRequests()) {
            TYPES requestedType = fetchRequests.getNextRequest();
            synchronized (this) { // IMPORTANT - make sure that the lock is released after a single call of getAll otherwise the the "this" object might be blocked for quite a long time
                List<GenItem> result = RestApiAccess.getAllItems(hoster, owner, requestedType, restConf);
                // result == null --> api-call was not successfull!!
                // TODO - better handle this via exceptions?
                if (result == null) {
                    fetchRequests.updateRequestFailed(requestedType);
                    continue;
                }// else
                DimeTable table = getCreateTypeTable(requestedType);
                for (GenItem item : result) {
                    table.add(item);
                }
                // update fetchRequests
                fetchRequests.updateRequestSucceeded(requestedType);
            }
        }
    }

    boolean workToBeDone() {
        synchronized (dirtyItemsLock) {
            if (!dirtyItems.isEmpty()) {
                return true;
            }
        }
        return fetchRequests.hasOpenRequests();
    }

    /**
     * @return the fetchRequests
     */
    RequestManager getFetchRequests() {
        return fetchRequests;
    }

    /**
     * @return the hoster
     */
    public String getHoster() {
        return hoster;
    }

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }
    
    public static class StorageStat {
        public long totalEntries=0;
        public Map<String, Integer> entriesPerType = new HashMap<String, Integer>();
    }
    
    public StorageStat calcStorageStat() {
        StorageStat result = new StorageStat();
        for (DimeTable table: storage){
            int entries = table.size();
            result.totalEntries+=entries;
            result.entriesPerType.put(table.getType()+"", entries);
        }
        return result;
    }
    
}
