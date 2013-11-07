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
 *  Description of NotificationManager
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 05.06.2012
 */
package eu.dime.control;

import eu.dime.model.Model;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.restapi.RestApiAccess;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NotificationManager
 *
 */
public class NotificationManager implements Runnable {
	
    public static final int MINIMUM_NOTIFICATION_TIMEOUT = 1000;
    private static final long NOTIFICATION_MANAGER_SLEEP_TIME = 100;
    private static final long TIME_OUT_UNTIL_KILLING_THREAD = 110;
    private static final long NOTIFICATION_MANAGER_SLEEP_EXTRA_DELAY_TIME = 5000;
    private static final Object singeltonMonitor = new Object();
    private static final Set<NotificationListener> firstLevelListeners = new HashSet<NotificationListener>();
    private static final Set<NotificationListener> secondLevelListeners = new HashSet<NotificationListener>();
    private static NotificationManager instance = null;
    private static Thread notificationManagerThread = null;
    private boolean stopped = false;
    private String hoster;    
    private double sleepFactor = 1; 
    
    private NotificationManager(String hoster) {
        this.hoster = hoster;
    }
    
    public void run() {
        while (!hasStopped()) {
            try {                
                manageNotifications();
                Thread.sleep(NOTIFICATION_MANAGER_SLEEP_TIME);
            } catch (ConcurrentModificationException ex) {
            	Logger.getLogger(NotificationManager.class.getName()).log(Level.WARNING, "NotificationManager: Listeners changed while trying to send notification!");
			} catch (InterruptedException ex) {
                Logger.getLogger(NotificationManager.class.getName()).log(Level.WARNING, "NotificationManager Thread was interrupted - stopping now!");
                this.stopped = true;
            }
        }
    }

    /**
     * must not be synchronized, since the comet call is blocking
     */
    private void manageNotifications() throws InterruptedException, ConcurrentModificationException {
        long timeStamp = System.currentTimeMillis();
        //calling blocking call getCometCall
        List<NotificationItem> notifications = RestApiAccess.getCometCall(hoster, Model.getInstance().getRestApiConfiguration());
        if (notifications.isEmpty() && timeStamp + MINIMUM_NOTIFICATION_TIMEOUT > System.currentTimeMillis()){
            Thread.sleep(NOTIFICATION_MANAGER_SLEEP_EXTRA_DELAY_TIME * (int) sleepFactor);
            if ((NOTIFICATION_MANAGER_SLEEP_EXTRA_DELAY_TIME * sleepFactor) < 3600 * 1000){ //keep sleep time beyond one hour
                sleepFactor += 0.5; // increase sleep factor by 0.5 every time
            }
        } else {
            sleepFactor = 1;
        }
        synchronized (singeltonMonitor) {
        	//Model needs to be refreshed first otherwise registered views are loading old cache data
        	for (NotificationItem item : notifications) {
	            for (NotificationListener listener : NotificationManager.firstLevelListeners) {
	                listener.notificationReceived(hoster, item);
	            }
	        }
	        for (NotificationItem item : notifications) {
	            for (NotificationListener listener : NotificationManager.secondLevelListeners) {
	                listener.notificationReceived(hoster, item);
	            }
	        }
        }
    }

    public static void start(String hoster) {
        synchronized (singeltonMonitor) {
            if (instance != null) {
                throw new RuntimeException("NotificationManager already running!");
            }
            instance = new NotificationManager(hoster);
            instance.setUnStop();
            notificationManagerThread = new Thread(instance);
            notificationManagerThread.start();
        }
    }

    public static void stop() {
        synchronized (singeltonMonitor) {
            if (instance == null) { //NotificationManager is not running!
                return;
            }
            instance.setStop();
            try {
                notificationManagerThread.join(TIME_OUT_UNTIL_KILLING_THREAD);
                if (notificationManagerThread.isAlive()) {
                    notificationManagerThread.interrupt();
                }
                instance = null;
            } catch (InterruptedException ex) {
                Logger.getLogger(NotificationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void registerFirstLevel(NotificationListener listener) {
    	synchronized (singeltonMonitor) {
    		firstLevelListeners.add(listener);	
    	}
    }
    
    public static void unregisterFirstLevel(NotificationListener listener) {
    	synchronized (singeltonMonitor) {
    		firstLevelListeners.remove(listener);
    	}
    }
    

    public static void registerSecondLevel(NotificationListener listener) {
    	synchronized (singeltonMonitor) {	    
    		secondLevelListeners.add(listener);
    	}
    }
    
    public static void unregisterSecondLevel(NotificationListener listener) {
    	synchronized (singeltonMonitor) {
    		secondLevelListeners.remove(listener);
    	}
    }
    
    public static boolean isRunning() {
        synchronized (singeltonMonitor) {
            return (instance != null);
        }
    }

    private synchronized void setStop() {
        stopped = true;
    }

     private synchronized void setUnStop() {
        stopped = false;
    }
    
    private synchronized boolean hasStopped() {
        return stopped;
    }
    
}