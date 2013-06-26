/*
 *  Description of ModelContext
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 06.06.2012
 */
package eu.dime.model;

import eu.dime.control.LoadingViewHandler;
import eu.dime.control.ResourceIdentifier;
import eu.dime.model.storage.DimeHosterStorage;
import eu.dime.model.storage.DimeMemory;
import eu.dime.model.storage.DimeStorage;
import eu.dime.model.storage.InitStorageFailedException;
import eu.dime.model.storage.LoadListenerKey;
import eu.dime.model.storage.MemoryWorker;
import eu.dime.restapi.RestApiConfiguration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.sstl.HashMapSet;

/**
 * ModelContext
 *
 */
public class ModelContext {

    private DimeStorage storage;
    private ModelConfiguration configuration;// = new ModelConfiguration(false, false, "127.0.0.1", false, false, "");
    private MemoryWorker memoryWorker=null;
    private Thread memoryWorkerThread;

    public ModelContext(DimeStorage storage, ModelConfiguration configuration, HashMapSet<String, ResourceIdentifier> fetchedResources) {
        this.storage = storage;
        this.configuration = configuration;
        initMemoryWorkerThread(storage);
    }
    
    private void initMemoryWorkerThread(DimeStorage storage) {
        //check whether already running
        if (memoryWorker!=null){
            memoryWorker.stop();
        }
        memoryWorker = new MemoryWorker(storage);
        memoryWorkerThread = new Thread(memoryWorker);
        memoryWorkerThread.start();
    }

    /**
     * @return the storage
     */
    public DimeStorage getStorage() {
        return storage;
    }

    /**
     * @return the configuration
     */
    public ModelConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration the configuration to set
     */
    public void setConfiguration(ModelConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * when called userStorage gets re-initialised! - calling this function
     * concurrently with other operations is not safe!!!
     *
     * @param userStorage
     * @param configuration 
     * @throws InitStorageFailedException
     */
    protected synchronized void updateSettingsOfOwnerStorage(String hoster, String owner, DimeMemory userStorage, ModelConfiguration configuration) throws InitStorageFailedException {
        if (userStorage == null) {
            userStorage = new DimeMemory(hoster, owner, getRestApiConfiguration()); 
        }
        //clean up - re-init storage
        userStorage.initStorage(hoster, owner, configuration.persistence, configuration.accessRemoteRestAPI);
    }

    public DimeMemory getOwnerStorage(String hoster, String owner) {
        DimeMemory result = null;
        try {
            try {
                result = storage.get(hoster).get(owner);
                if (result != null) {
                    return result;
                }
            } catch (NullPointerException ex) {
                Logger.getLogger(getClass().getName()).log(Level.FINE, "storage of " + hoster + " - " + owner + " not existing");
            }
            
            //else - storage is not yet existing
            
            //first make sure the hoster is provided ... or create one
            if (!storage.containsKey(hoster)) {
                storage.put(hoster, new DimeHosterStorage());
            }
            //create new owner storage
            result = new DimeMemory(hoster, owner, getRestApiConfiguration());
            
            //initialize owner storage
            updateSettingsOfOwnerStorage(hoster, owner, result, configuration);
            
            //put owner storage to the hoster
            storage.get(hoster).put(owner, result);
            Logger.getLogger(getClass().getName()).log(Level.FINE, "created new storage for " + hoster + " - " + owner);
            return result;
            
        } catch (InitStorageFailedException ex) {
            Logger.getLogger(ModelContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }
    /**
     * @param storage the storage to set
     */
    public void setStorage(DimeStorage storage) {
        this.storage = storage;
        
    }
    
     /**
     * Re-initialises the storage according to the configuration set.
     *
     * In case of persistence == true all data gets stored and loaded again In
     * case of persistence == false all data stored in the model is lost
     *
     * This function gets also called with @updateSettingsOfOwnerStorage(ModelConfiguration
     * configuration)
     *
     * Calling this function concurrently with other operations is not safe!!!
     */
    public synchronized void resetStorage() throws InitStorageFailedException {
        for (Map.Entry<String, DimeHosterStorage> hoster : storage.entrySet()) {
            for (Map.Entry<String, DimeMemory> owner : hoster.getValue().entrySet()) {
                updateSettingsOfOwnerStorage(hoster.getKey(), owner.getKey(), owner.getValue(), configuration);
            }
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Initialization of Storage completed! Configuration:\n" + configuration.toString());
    }

    /**
     * @return the restApiConfiguration
     */
    public RestApiConfiguration getRestApiConfiguration() {
        return configuration.getRestApiConfiguration();
    }

    void registerOnCacheLoad(LoadingViewHandler lvHandler, LoadListenerKey loadListenerKey) {
        memoryWorker.registerOnCacheLoad(lvHandler, loadListenerKey);
    }
    
}
