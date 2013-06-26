/*
 * 
 */
package eu.dime.control;

import eu.dime.model.GenItem;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simon
 */
public class DummyLoadingViewHandler extends AbstractLoadingViewHandler {

	private int retries = 0;

    public void showLoadingView() {
        retries = 0;
    	try {
            waitForLoading();
        } catch (TimeOutWhileLoadingException ex) {
        	Logger.getLogger(DummyLoadingViewHandler.class.getName()).log(Level.SEVERE, "Timeout while Loading", ex);
        }
    }

    public GenItem showCreateLoadingView(String oldGUID) {
        return waitForCreateItem(oldGUID);
    }

    @Override
    protected void handleTimeOutNotified() {
    	try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            
        }
    }

    public boolean doContinueAfterTimeOut() {
    	boolean continueAfterTimeOut = false;
    	if(retries < 3) {
    		retries++;
    		continueAfterTimeOut = true;
    	}
        return continueAfterTimeOut;
    }
    
}
