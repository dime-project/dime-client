package eu.dime.model;
 
import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.control.LoadingViewHandler;
import eu.dime.control.ResourceIdentifier;
import eu.dime.model.specialitem.EvaluationItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;
import eu.dime.control.NotificationListener;
import eu.dime.control.NotificationManager;
import eu.dime.model.context.ContextItem;
import eu.dime.model.displayable.AccountItem;
import eu.dime.model.displayable.ActivityItem;
import eu.dime.model.displayable.DataboxItem;
import eu.dime.model.displayable.DeviceItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.EventItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.LiveStreamItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.PlaceItem;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.model.displayable.ServiceAdapterItem;
import eu.dime.model.displayable.SituationItem;
import eu.dime.model.storage.CacheFailException;
import eu.dime.model.storage.DimeHosterStorage;
import eu.dime.model.storage.DimeMemory;
import eu.dime.model.storage.DimeMemory.StorageStat;
import eu.dime.model.storage.DimeStorage;
import eu.dime.model.storage.InitStorageFailedException;
import eu.dime.model.storage.LoadListenerKey;
import eu.dime.restapi.DimeHelper;
import eu.dime.restapi.RestApiConfiguration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.sstl.HashMapSet;
import sit.sstl.StrictSITEnumMap;

/**
 * singleton, re-entrant? entry-point to DimeMemory and all functions are
 * thread-safe to be called
 *
 * @author Simon Thiel
 */
@SuppressWarnings({"rawtypes"})
public class Model implements NotificationListener{

    public final static String ME_OWNER = "@me";

	static final StrictSITEnumMap<TYPES, PSMapEntry> TypeMap = new StrictSITEnumMap<TYPES, PSMapEntry>(
            TYPES.class,
            new PSMapEntry[]{
                new PSMapEntry<GroupItem>(TYPES.GROUP, "group", GroupItem.class, null, TYPES.PERSON, "group", false),
                new PSMapEntry<PersonItem>(TYPES.PERSON, "person", PersonItem.class, TYPES.GROUP, null, "person", false),
                new PSMapEntry<DataboxItem>(TYPES.DATABOX, "databox", DataboxItem.class, null, TYPES.RESOURCE, "databox", true),
                new PSMapEntry<ResourceItem>(TYPES.RESOURCE, "resource", ResourceItem.class, TYPES.DATABOX, null, "resource", true),
                new PSMapEntry<LiveStreamItem>(TYPES.LIVESTREAM, "livestream", LiveStreamItem.class, null, TYPES.LIVEPOST, "livestream", true),
                new PSMapEntry<LivePostItem>(TYPES.LIVEPOST, "livepost", LivePostItem.class, TYPES.LIVESTREAM, null, "livepost", true),
                new PSMapEntry<ProfileItem>(TYPES.PROFILE, "profile", ProfileItem.class, null, TYPES.PROFILEATTRIBUTE, "profile", true),
                new PSMapEntry<ProfileAttributeItem>(TYPES.PROFILEATTRIBUTE, "profileattribute", ProfileAttributeItem.class, TYPES.PROFILE, null, "profileattribute", false),
                new PSMapEntry<SituationItem>(TYPES.SITUATION, "situation", SituationItem.class, null, null, "situation", false),
                new PSMapEntry<EventItem>(TYPES.EVENT, "event", EventItem.class, null, null, "event", false),
                new PSMapEntry<ServiceAdapterItem>(TYPES.SERVICEADAPTER, "serviceadapter", ServiceAdapterItem.class, null, null, "serviceadapter", false),
                new PSMapEntry<DeviceItem>(TYPES.DEVICE, "device", DeviceItem.class, null, null, "device", false),
                new PSMapEntry<ContextItem>(TYPES.CONTEXT, "context", ContextItem.class, null, null, "context", false),
                new PSMapEntry<EvaluationItem>(TYPES.EVALUATION, "evaluation", EvaluationItem.class, null, null, "evaluation", false),
                new PSMapEntry<PlaceItem>(TYPES.PLACE, "place", PlaceItem.class, null, null, "place", false),
                new PSMapEntry<AccountItem>(TYPES.ACCOUNT, "account", AccountItem.class, null, null, "account", false),
                new PSMapEntry<ActivityItem>(TYPES.ACTIVITY, "activity", ActivityItem.class, null, null, "activity", false),
                new PSMapEntry<NotificationItem>(TYPES.NOTIFICATION, "notification", NotificationItem.class, null, null, "notification", false),
                new PSMapEntry<UserNotificationItem>(TYPES.USERNOTIFICATION, "usernotification", UserNotificationItem.class, null , null, "usernotification", false)
            }); 
     static final StrictSITEnumMap<CALLTYPES, CallTypeEntry> CallTypeMap = new StrictSITEnumMap<CALLTYPES, CallTypeEntry>( 
            CALLTYPES.class,
            new CallTypeEntry[]{
                new CallTypeEntry(CALLTYPES.AT_ALL_GET, "AT_ALL_GET", "@all"),
                new CallTypeEntry(CALLTYPES.AT_GLOBAL_ALL_GET, "AT_GLOBAL_ALL_GET", "@all"),
                new CallTypeEntry(CALLTYPES.AT_ITEM_GET, "AT_ITEM_GET", ""),
                new CallTypeEntry(CALLTYPES.AT_ITEM_POST_NEW, "AT_ITEM_POST_NEW", ""),
                new CallTypeEntry(CALLTYPES.AT_ITEM_POST_UPDATE, "AT_ITEM_POST_UPDATE", ""),
                new CallTypeEntry(CALLTYPES.AT_ITEM_DELETE, "AT_ITEM_DELETE", ""),
                new CallTypeEntry(CALLTYPES.COMET, "COMET", "@comet"),
                new CallTypeEntry(CALLTYPES.DUMP, "DUMP", "@dump"),
                new CallTypeEntry(CALLTYPES.MERGE, "MERGE","@merge"),
                new CallTypeEntry(CALLTYPES.AUTH_ALL_GET, "AUTH_ALL_GET","auth/@all"),
                new CallTypeEntry(CALLTYPES.AUTH_POST, "AUTH_POST","auth"),
                new CallTypeEntry(CALLTYPES.CRAWLER, "CRAWLER","crawler"),
                new CallTypeEntry(CALLTYPES.USER_ALL_GET, "USER_ALL_GET","@all"),
                new CallTypeEntry(CALLTYPES.REGISTER_USER, "REGISTER_USER", "")
            });
    private static final Model instance = new Model();
    //####################################################
    // object part of model
    //####################################################
    private final DimeStorage storage = new DimeStorage();
    private ModelContext context = new ModelContext(storage, getDummyModelConfiguration(), new HashMapSet<String, ResourceIdentifier>());

    public Model() { }

    public static Model getInstance() {
        return Model.instance;
    }
    
    private ModelConfiguration getDummyModelConfiguration() {
    	return new ModelConfiguration(DimeHelper.DEFAULT_HOSTNAME, DimeHelper.DEFAULT_PORT, 
                true, StaticTestData.DEFAULT_MAIN_SAID,"" , "" ,false, false, false);
    }

    /**
     * when called with a changed configuration Model gets re-initialised!
     *
     * * in case the previous setting was persistence == true the model tries
     * to store the data beforehand in case the new setting is persistence ==
     * true the storage will try to load data when created (attention lazy
     * instantiation might delay the actual loading of the storage)
     *
     * Calling this function concurrently with other operations might not be
     * safe!!!
     *
     * @param configuration
     * @throws InitStorageFailedException  
     */
    public synchronized void updateSettings(ModelConfiguration configuration) throws InitStorageFailedException {
        if (!context.getConfiguration().equals(configuration)) { //only update/reset etc. if something changed - ignore otherwise
            //first stop notification manager
            if(NotificationManager.isRunning()) {                
                NotificationManager.stop();
            }
            context.setConfiguration(configuration);
            resetStorage();
            //control notification manager            
            if (configuration.fetchNotifications) {
                NotificationManager.start(configuration.mainSAID);
                NotificationManager.registerFirstLevel(this); //registering several times is no problem, since the listeners are handled in a set
            }
        }
    }
    
    public synchronized ModelConfiguration getSettings() {
        return context.getConfiguration();
    }
    
    public synchronized RestApiConfiguration getRestApiConfiguration(){
        return context.getRestApiConfiguration();
    }
    
    public synchronized void notificationReceived(String fromHoster, NotificationItem item) {
        if (item.getElement().getUserId() == null || item.getElement().getUserId().length() == 0){
            Logger.getLogger(Model.class.getName()).log(Level.WARNING, "received invalid notification: " + item.getOperation() + " " + item.getElement().getType());
        } else {
	        DimeMemory ownerStorage = context.getOwnerStorage(fromHoster, item.getElement().getUserId());
	        if(ownerStorage != null) ownerStorage.handleNotification(item);
        }
    }
    
    public void restartModel() {
    	try {
			updateSettings(getDummyModelConfiguration());
		} catch (InitStorageFailedException e) {
			e.printStackTrace();
		}
    }

    /**
     * calling this function concurrently with other operations is not safe!!!
     *
     * @throws InitStorageFailedException
     */
    public synchronized void resetStorage() throws InitStorageFailedException {
        //clean up - re-init database
        context.resetStorage();
    }

    /**
     * calling this function concurrently with other operations is not safe!!!
     */
    public synchronized void shutdownStorage() {
        for (DimeHosterStorage hoster : context.getStorage().values()) {
            for (DimeMemory owner : hoster.values()) {
                if (owner != null) {
                    owner.shutdownStorage();
                }
            }
        }
    }

    /**
     * returns a copy of the specific item identified by guid, type, hoster and owner
     * @param mrContext
     * @param type
     * @param guid
     * @return 
     */
    public GenItem getItem(ModelRequestContext mrContext, TYPES type, String guid) {
        DimeMemory myStorage = context.getOwnerStorage(mrContext.hoster, mrContext.owner);
        while(true){
            try {
                return myStorage.getItem(type, guid);
            } catch (CacheFailException ex) {
                context.registerOnCacheLoad(mrContext.lvHandler, new LoadListenerKey(mrContext.hoster, mrContext.owner, type));
                mrContext.lvHandler.showLoadingView(); //we wait for the notification that the requested type has been received
                if (!mrContext.lvHandler.doContinueAfterTimeOut()){
                    throw new LoadingAbortedRuntimeException("Aborted to load after timeout!");
                }
            }
        }        
    }

    /**
     * Returns a copy of all of the given type for a specific hoster and owner.
     * @param mrContext
     * @param type
     * @return 
     */
    public List<GenItem> getAllItems(ModelRequestContext mrContext,  TYPES type) {
        DimeMemory myStorage = context.getOwnerStorage(mrContext.hoster, mrContext.owner);                
        while(true){
            try {
                return myStorage.getAllItems(type);
            } catch (CacheFailException ex) {
                context.registerOnCacheLoad(mrContext.lvHandler, new LoadListenerKey(mrContext.hoster, mrContext.owner, type));
                mrContext.lvHandler.showLoadingView(); //we wait for the notification that the requested type has been received
                if (!mrContext.lvHandler.doContinueAfterTimeOut()){
                    throw new LoadingAbortedRuntimeException("Aborted to load after timeout!");
                }
            }
        }        
    }
    
    public List<GenItem> refreshAllItems(ModelRequestContext mrContext,  TYPES type) {
        DimeMemory myStorage = context.getOwnerStorage(mrContext.hoster, mrContext.owner);
        myStorage.reload(type);
        return getAllItems(mrContext, type);
    }

    /**
     * return all known persons of the user + the users userId
     * @param mrContext - the owner field of mrContext gets ignored since the owners are all persons of the user own PS
     * @return 
     */
    public List<String> getAllOwners(ModelRequestContext mrContext) {
        List<String> result = new Vector<String>();
        for (GenItem item : getAllItems(new ModelRequestContext(mrContext.hoster,ME_OWNER, mrContext.lvHandler),  TYPES.PERSON)) {
            result.add(item.getGuid());
        }
        result.add(Model.ME_OWNER);
        return result;
    }

    /**
     * returns a copy of all items for the specified type and hoster 
     * (including shared items with
     * different owners then Model.ME_OWNER)
     *
     * @param mrContext -- the owner field gets ignored
     * @param type
     * @return
     */
    public List<GenItem> getAllAllItems(ModelRequestContext mrContext, TYPES type) {
        List<GenItem> result = new Vector<GenItem>();
        for (String userId : getAllOwners(mrContext)) {
            result.addAll(getAllItems(new ModelRequestContext(mrContext.hoster, userId, mrContext.lvHandler), type));
        }
        return result;
    }

    /**
     * blocking asynchronous creation of (a copy) of the give item
     * be sure to continue with the returned item, since it can differ from the passed one 
     * e.g. for remote creates
     * 
     * in case the create item failed an exception is thrown
     * 
     * @param mrContext
     * @param item
     * @return
     * @throws CreateItemFailedException  
     */
    public GenItem createItem(ModelRequestContext mrContext, GenItem item) throws CreateItemFailedException {
        DimeMemory myStorage = context.getOwnerStorage(mrContext.hoster, mrContext.owner);         
        ModelHelper.updateLastUpdatedIfDisplayable(item);
        myStorage.createItem(item, mrContext.lvHandler);
        GenItem result = mrContext.lvHandler.showCreateLoadingView(item.getGuid());
        if (result==null){
            throw new CreateItemFailedException("Creating item failed! Item:\n"+ item.getJSONObject().toString());
        }
        return result;
    }

    public void updateItem(ModelRequestContext mrContext, GenItem item) {
        DimeMemory myStorage = context.getOwnerStorage(mrContext.hoster, mrContext.owner);
        ModelHelper.updateLastUpdatedIfDisplayable(item);
        myStorage.updateItem(item);
    }
    
    public GenItem removeItem(ModelRequestContext mrContext, String guid, TYPES type) {
        //first update all parents (groups, databoxes ..) - in case it's not a displayable item we will retrieve an empty list
        for (DisplayableItem parent : ModelHelper.getParentsOfDisplayableItem(mrContext, guid, type)){
            parent.removeItem(guid);
            this.updateItem(mrContext, parent);
        }
        //actually trigger to remove the item
        DimeMemory myStorage = context.getOwnerStorage(mrContext.hoster, mrContext.owner);
        return myStorage.removeItem(guid, type);
    }

    public DimeHosterStorage getDump(ModelRequestContext mrContext) {
        DimeHosterStorage result = new DimeHosterStorage();
        for (String userId : getAllOwners(mrContext)) {
            result.put(userId, context.getOwnerStorage(mrContext.hoster, userId));
        }
        return result;
    }

    /**
     * merges the persons with the guids as specified in the list. returns the merge-to person
     * @param mrContext
     * @param personGuids
     * @return 
     */
    public GenItem mergeItems(ModelRequestContext mrContext, TYPES type, List<String> itemGuids){
        if (itemGuids.size()<2){
            throw new RuntimeException("merge requires at least two items");
        }
        DimeMemory myStorage = context.getOwnerStorage(mrContext.hoster, mrContext.owner);
        GenItem firstItem = getItem(mrContext, type, itemGuids.get(0));
        try {
            myStorage.mergeItem(firstItem, itemGuids, mrContext.lvHandler);
            return mrContext.lvHandler.showCreateLoadingView(firstItem.getGuid());
        } catch (Exception ex){
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * using this makes only sense in case more then one hoster is using the model.
     * 
     * Attention: it might be illegitimate to access the space of another hoster
     * (or even to find out which hosters are available)
     * however, at the moment there is no security solution provided to prohibit this
     *      
     * Attention: for persistence==true - this call will not return available hosters
     * stored that have not been accessed within the ongoing session - since the model
     * does only try to access them on demand
     * 
     * @return list of hoster-ids  available
     */
    public Set<String> getAllHosters() {
        HashSet<String> result = new HashSet<String>();
        for (Map.Entry<String, DimeHosterStorage> entry: context.getStorage().entrySet()){
            result.add(entry.getKey());
        }
        return result;        
    }
    
    public static class ModelStat{
        public long totalEntries=0;
        public int hosters=0;
        public double ownersPerHoster=0.0;
        public Map<String, StorageStat> storageStatPerOwnerAtHoster = new HashMap<String, StorageStat>();
    }
    
    public ModelStat calcModelStat(){
        LoadingViewHandler lvh = new DummyLoadingViewHandler();
        ModelStat result = new ModelStat();
        long ownersTotal=0;
        for (String hoster: getAllHosters()){
            result.hosters++;
            ModelRequestContext mrc = new ModelRequestContext(hoster, Model.ME_OWNER, lvh);
            for (String owner: getAllOwners(mrc)){
                ownersTotal++;
                DimeMemory myStorage = context.getOwnerStorage(hoster, owner);
                StorageStat myStorageStat = myStorage.calcStorageStat();
                result.storageStatPerOwnerAtHoster.put(owner+'@'+hoster, myStorageStat);
                result.totalEntries+=myStorageStat.totalEntries;
            }
        }
        result.ownersPerHoster=(result.hosters>0)?(ownersTotal/result.hosters):0.0;
        return result;
    }
    
}
