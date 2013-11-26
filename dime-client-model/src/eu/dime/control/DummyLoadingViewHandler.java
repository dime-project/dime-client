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


    public void showLoadingView() {
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
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            
        }
    }

    public boolean doContinueAfterTimeOut() {
    	
        return true;
    }
    
}
