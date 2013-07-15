/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model;

import eu.dime.control.MultiHosterNotificationManager;
import eu.dime.control.NotificationListener;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.restapi.RestApiAccess;
import eu.dime.restapi.RestApiConfiguration;
import eu.dime.restapi.RestCallHelper;
import eu.dime.view.viewmodel.Configuration;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.web.client.HttpHelper;
import sun.security.krb5.internal.HostAddress;



/**
 *
 * @author simon
 */
public class MultiHosterNotificationManagerTest implements NotificationListener {




    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            MultiHosterNotificationManager.start();
            
            String token = HttpHelper.getBase64UserNamePwdToken("dummyuserwednesday03", "hello");

            RestApiConfiguration conf = new RestApiConfiguration("localhost",8443, true,token);
            MultiHosterNotificationManager.register("dummyuserwednesday03", 2, conf, new MultiHosterNotificationManagerTest());
            Logger.getLogger(MultiHosterNotificationManagerTest.class.getName()).log(Level.INFO, "waiting for notifications ...");
//
//            //empty queue
//            while(System.in.available()!=-1){
//                System.in.read();
//            }

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
