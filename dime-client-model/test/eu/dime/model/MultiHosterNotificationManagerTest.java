/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model;

import eu.dime.control.MultiHosterNotificationManager;
import eu.dime.control.NotificationListener;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.restapi.RestApiConfiguration;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.web.client.HttpHelper;



/**
 *
 * @author simon
 */
public class MultiHosterNotificationManagerTest implements NotificationListener {

    private static void registerUser(String userName, String password){

            String token = HttpHelper.getBase64UserNamePwdToken(userName, password);

            RestApiConfiguration conf = new RestApiConfiguration("localhost",8443, true,token);
            MultiHosterNotificationManager.register(userName, 2, conf, new MultiHosterNotificationManagerTest());
            
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            MultiHosterNotificationManager.start();

            registerUser("dummyusermoday11", "dummyusermoday11");
            registerUser("dummyusermoday12", "dummyusermoday12");
            
            Logger.getLogger(MultiHosterNotificationManagerTest.class.getName()).log(Level.INFO, "waiting for notifications ...");

            while(true){
                try {

                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MultiHosterNotificationManagerTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            
        } catch (Exception ex) {
            Logger.getLogger(MultiHosterNotificationManagerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        MultiHosterNotificationManager.stop();
        System.exit(0);

    }

    public void notificationReceived(String fromHoster, NotificationItem item) {
        Logger.getLogger(MultiHosterNotificationManagerTest.class.getName()).log(Level.INFO, "received message for hoster: "+fromHoster
                + item.createJSONObject().toString()
                );
    }

    

}
