/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.control;

import eu.dime.model.GenItem;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simon
 */
public abstract class AbstractLoadingViewHandler implements LoadingViewHandler {

    protected static final long LOADING_VIEW_HANDLER_POLL_SLEEP_TIME = 20;
    
    private boolean notified = false;
    private boolean connectionTimedOutOnLoading = false;
    private final Object notifierLock = new Object();
    private Map<String, GenItem> createdItems = new HashMap<String, GenItem>();
    
    protected void waitForLoading() throws TimeOutWhileLoadingException {
        while(true) { // client-side timeout already set in RequestManager
            synchronized(notifierLock) {
                if (notified==true){
                    notified = false;
                    return;
                }
                if (connectionTimedOutOnLoading) {
                    handleTimeOutNotified();
                    connectionTimedOutOnLoading=false;
                    throw new TimeOutWhileLoadingException();
                }
            }
            try {
                Thread.sleep(LOADING_VIEW_HANDLER_POLL_SLEEP_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(AbstractLoadingViewHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void notifyLoadingDone() {
        synchronized(notifierLock){
            notified = true;
        }
    }
    
    protected abstract void handleTimeOutNotified();

    /**
     * ATTENTION: item can be null --> create failed
     * @param oldGUID
     * @return 
     */
    protected GenItem waitForCreateItem(String oldGUID) {
        while(true) { //todo add timeout
            synchronized(notifierLock) {
                if (createdItems.containsKey(oldGUID)){ //use contains - since the value can also be null in case of failure
                    return createdItems.remove(oldGUID);                
                }
            }
            try {
                Thread.sleep(LOADING_VIEW_HANDLER_POLL_SLEEP_TIME);
            } catch (InterruptedException ex) {
                Logger.getLogger(AbstractLoadingViewHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * ATTENTION: item can be null --> create failed
     * @param item
     * @param oldGuid 
     */
    public void notifyItemCreated(GenItem item, String oldGuid) {
         synchronized(notifierLock){
            createdItems.put(oldGuid, item);
        }
    }

    /**
     * 
     */
    public void notifyLoadingTimeOut() {
        synchronized(notifierLock){
            this.connectionTimedOutOnLoading = true;
        }
    }
    
}
