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
