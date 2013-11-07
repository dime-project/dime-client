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
public class SilentLoadingViewHandler extends AbstractLoadingViewHandler {
    
    private static final int FIX_NUMBER_OF_RETRIES = 4;
	
    private boolean continueAfterTimeOut = true;
    private int retries = 0;

    public void showLoadingView() {
    	try {
            waitForLoading();
        } catch (TimeOutWhileLoadingException ex) {
        	Logger.getLogger(SilentLoadingViewHandler.class.getName()).log(Level.SEVERE, "Timeout while Loading", ex);
        }
    }

    public GenItem showCreateLoadingView(String oldGUID) {
        return waitForCreateItem(oldGUID);
    }

    @Override
    protected void handleTimeOutNotified() {

    	if(retries < FIX_NUMBER_OF_RETRIES) {
    		retries++;
    		continueAfterTimeOut = true;
    	}else{

            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {

            }
        }
    }

    public boolean doContinueAfterTimeOut() {
    	
        return continueAfterTimeOut;
    }    
}
