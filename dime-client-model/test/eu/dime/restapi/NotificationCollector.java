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
 *  Description of NotificationCollector
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 04.06.2012
 */
package eu.dime.restapi;


import eu.dime.control.NotificationListener;
import eu.dime.control.NotificationManager;
import eu.dime.model.StaticTestData;
import eu.dime.model.specialitem.NotificationItem;

/**
 * NotificationCollector
 * 
 */
public class NotificationCollector implements NotificationListener {
    
    private void start() {
       NotificationManager.registerSecondLevel(this);
       NotificationManager.start(StaticTestData.DEFAULT_MAIN_SAID);
    }
    
    public void notificationReceived(String fromHoster, NotificationItem item) {
             System.out.println("----------------------------------------------------------");
                System.out.println(item.getJSONObject().toString());
                System.out.println("----------------------------------------------------------");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new NotificationCollector().start();
    }

}
