/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.storage;

import eu.dime.control.LoadingHandler;
import eu.dime.model.TYPES;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author simon
 */
class LoadingListeners {
    
    private class MemoryKey{
        String hoster;
        String owner;

        public MemoryKey(String hoster, String owner) {
            this.hoster = hoster;
            this.owner = owner;
        }
               
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 41 * hash + (this.hoster != null ? this.hoster.hashCode() : 0);
            hash = 41 * hash + (this.owner != null ? this.owner.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MemoryKey other = (MemoryKey) obj;
            if ((this.hoster == null) ? (other.hoster != null) : !this.hoster.equals(other.hoster)) {
                return false;
            }
            if ((this.owner == null) ? (other.owner != null) : !this.owner.equals(other.owner)) {
                return false;
            }
            return true;
        }
        
    }
    private class MemoryListeners extends HashMap<TYPES, List<LoadingHandler>>{}
    
    private HashMap<MemoryKey, MemoryListeners> listeners = new HashMap();

   
    private MemoryListeners getMemoryListeners(String hoster, String owner){
        return getMemoryListeners(new MemoryKey(hoster,owner));       
    }
    
    private MemoryListeners getMemoryListeners(MemoryKey memKey){
        if (!listeners.containsKey(memKey)){     //lazy instatiation                    
            listeners.put(memKey, new MemoryListeners());
        }
        return listeners.get(memKey);        
    }
    

    synchronized Iterable<Entry<TYPES, List<LoadingHandler>>> getListenersForMemory(DimeMemory dimeMemory) {       
        
        MemoryListeners memListeners = getMemoryListeners(dimeMemory.getHoster(), dimeMemory.getOwner());        
        return memListeners.entrySet();
    }

    synchronized void putAndAdd(LoadListenerKey listenerKey, LoadingHandler listener) {
        MemoryListeners memListeners = getMemoryListeners(listenerKey.getHoster(), listenerKey.getOwner());        
        
        
        
        if (!memListeners.containsKey(listenerKey.getType())){ //create if not exists
            memListeners.put(listenerKey.getType(), new ArrayList<LoadingHandler>());
        }
        
        List<LoadingHandler> handlerList = memListeners.get(listenerKey.getType());
        
        if (!handlerList.contains(listener)){
            handlerList.add(listener);
        }
    }
    
    synchronized void cleanUpForMemory(DimeMemory dimeMemory) {
        
        //clean up entries with no loading handler waiting
        Iterator<Map.Entry<TYPES, List<LoadingHandler>>> iter = 
                getMemoryListeners(dimeMemory.getHoster(), dimeMemory.getOwner()).entrySet().iterator();
        while (iter.hasNext()) {
            if (iter.next().getValue().isEmpty()) {
                iter.remove();
            }
        }
    }
}
