/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.storage;

import eu.dime.control.CacheLoadedHandler;
import eu.dime.control.LoadingHandler;
import eu.dime.model.TYPES;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simon
 */
public class MemoryWorker implements Runnable, CacheLoadedHandler {

    private final long MAX_WORKER_DELAY = 50;
    private boolean stopping = false;
    
    /**
     * listeners waiting for "loading ready notification".
     */
    private LoadingListeners listeners = new LoadingListeners();
    private final Object listenerLock = new Object();
    private final DimeStorage dimeStorage;
    private final Object storageLock = new Object();
	private boolean paused = false;

    public MemoryWorker(final DimeStorage dimeStorage) {
        this.dimeStorage = dimeStorage;
    }

    public void registerOnCacheLoad(LoadingHandler listener, LoadListenerKey listenerKey) {
        synchronized (listenerLock) {
            listeners.putAndAdd(listenerKey, listener);
        }
    }

    private void notiyListeners(DimeMemory dimeMemory) {
        synchronized (listenerLock) {
            //walk for all types
            for (Entry<TYPES, List<LoadingHandler>> typeListeners : listeners.getListenersForMemory(dimeMemory)) {
                //notify in case the type waiting for was fetched successfully
                if (dimeMemory.getFetchRequests().wasFetched(typeListeners.getKey())) {
                    //notify all listeners
                    Iterator<LoadingHandler> iter = typeListeners.getValue().iterator();
                    while (iter.hasNext()) {
                        LoadingHandler listener = iter.next();
                        listener.notifyLoadingDone();
                        iter.remove();
                    }
                } else if (dimeMemory.getFetchRequests().checkForFailedOrTimedOut(typeListeners.getKey())) {
                    //notify all listener about time-out
                    Iterator<LoadingHandler> iter = typeListeners.getValue().iterator();
                    while (iter.hasNext()) {
                        LoadingHandler listener = iter.next();
                        listener.notifyLoadingTimeOut();
                        iter.remove();
                    }
                }
            }
            listeners.cleanUpForMemory(dimeMemory);
        }
    }

    private void handleMemory(DimeMemory dimeMemory) {
    	synchronized (storageLock) {
    		if (this.paused){
    			return;
    		}
            dimeMemory.storeDirtyEntries();
            dimeMemory.fetchRequestedTypes();
            notiyListeners(dimeMemory);			
		}
    }

    //continously store dump in case something was changed
    public void run() {
        while (!stopping) {
            //iterate through all hosters and owners
            for (DimeHosterStorage hoster : dimeStorage.values()) {
                for (DimeMemory dimeMemory : hoster.values()) {
                    try {
                        handleMemory(dimeMemory);
                    } catch (RuntimeException ex) {
                        Logger.getLogger(MemoryWorker.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    } catch (Exception ex){
                        Logger.getLogger(MemoryWorker.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }
            try {
                Thread.sleep(MAX_WORKER_DELAY);
            } catch (InterruptedException ex) {
                Logger.getLogger(DimeMemory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void stop() {
        stopping = true;
    }
    
    public void setPaused(boolean paused){
    	synchronized (storageLock) {
    		this.paused=paused;
    	}
    }
    
}
