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

import eu.dime.model.specialitem.NotificationItem;
import eu.dime.restapi.RestApiAccess;
import eu.dime.restapi.RestApiConfiguration;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NotificationManager
 *
 */
public class MultiHosterNotificationManager {

    public static final int MINIMUM_NOTIFICATION_TIMEOUT = 1000;
    private static final long NOTIFICATION_MANAGER_SLEEP_TIME = 100;
    private static final long TIME_OUT_UNTIL_KILLING_THREAD = 110;
    private static final long NOTIFICATION_MANAGER_SLEEP_EXTRA_DELAY_TIME = 5000;
    private static final Object singeltonMonitor = new Object();
    private static MultiHosterNotificationManager instance = null;

    class NotificationUpdater implements Runnable {

        private HosterEntry hosterEntry;
        private boolean stopped = false;
        private double sleepFactor = 1;
        private String hoster;
        private RestApiConfiguration configuration;
        private final String clientId = UUID.randomUUID().toString();

        public NotificationUpdater(HosterEntry hosterEntry, String hoster, RestApiConfiguration configuration) {
            this.hosterEntry = hosterEntry;
            this.hoster = hoster;
            this.configuration = configuration;
        }

        public void run() {
            while (!hasStopped()) {
                try {
                    manageNotifications();
                    Thread.sleep(NOTIFICATION_MANAGER_SLEEP_TIME);
                } catch (ConcurrentModificationException ex) {
                    Logger.getLogger(MultiHosterNotificationManager.class.getName()).log(Level.WARNING, "NotificationManager: Listeners changed while trying to send notification!");
                } catch (InterruptedException ex) {
                    Logger.getLogger(MultiHosterNotificationManager.class.getName()).log(Level.WARNING, "NotificationManager Thread was interrupted - stopping now!");
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
            List<NotificationItem> notifications = RestApiAccess.getCometCall(hoster, configuration, clientId);
            if (notifications.isEmpty() && timeStamp + MINIMUM_NOTIFICATION_TIMEOUT > System.currentTimeMillis()) {
                Thread.sleep(NOTIFICATION_MANAGER_SLEEP_EXTRA_DELAY_TIME * (int) sleepFactor);
                if ((NOTIFICATION_MANAGER_SLEEP_EXTRA_DELAY_TIME * sleepFactor) < 3600 * 1000) { //keep sleep time beyond one hour
                    sleepFactor += 0.5; // increase sleep factor by 0.5 every time
                }
            } else {
                sleepFactor = 1;
            }
            hosterEntry.notifyListeners(notifications);

        }

        private synchronized boolean hasStopped() {
            return stopped;
        }

        private synchronized void setStop() {
            this.stopped = true;
        }
    }

    class HosterEntry {

        private String hoster;
        private SortedMap<Integer, Set<NotificationListener>> listeners = new TreeMap();
        private final Thread thread;
        private NotificationUpdater updater;

        public HosterEntry(String hoster, RestApiConfiguration configuration) {
            this.hoster = hoster;
            updater = new NotificationUpdater(this, hoster, configuration);
            thread = new Thread(updater);
            thread.start();
        }

        synchronized boolean addListener(int level, NotificationListener listener) {
            if (!listeners.containsKey(level)) {
                listeners.put(level, new HashSet());
            }
            return listeners.get(level).add(listener);

        }

        synchronized boolean removeListener(int level, NotificationListener listener) {
            if (!listeners.containsKey(level)) {
                Logger.getLogger(HosterEntry.class.getName()).log(Level.SEVERE, "cannot find listener for level " + level);
                return false;
            }
            return listeners.get(level).remove(listener);
        }

        synchronized void stop() {
            updater.setStop();
            try {
                thread.join(TIME_OUT_UNTIL_KILLING_THREAD);
                if (thread.isAlive()) {
                    thread.interrupt();
                }
                updater = null;
            } catch (InterruptedException ex) {
                Logger.getLogger(MultiHosterNotificationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /**
         * sends notifications to listeners according to their level, so that listeners of level 1 first gets all notifications
         * before listener of level 2 gets any notification.
         * @param notifications
         */
        synchronized void notifyListeners(List<NotificationItem> notifications) {

            for (Set<NotificationListener> myListeners : listeners.values()) {
                for (NotificationItem item : notifications) {
                    for (NotificationListener listener : myListeners) {
                        listener.notificationReceived(hoster, item);
                    }
                }
            }

        }
    }
    private Map<String, HosterEntry> entries = new HashMap();

    public static void start() {
        synchronized (singeltonMonitor) {
            if (MultiHosterNotificationManager.instance != null) {
                throw new RuntimeException("MultiHosterNotificationManager already running!");
            }
            MultiHosterNotificationManager.instance = new MultiHosterNotificationManager();
        }
    }

    public static void stop() {
        synchronized (singeltonMonitor) {
            if (instance == null) { //NotificationManager is not running!
                return;
            }
            for (HosterEntry entry : instance.entries.values()) {
                entry.stop();
            }
            instance.entries.clear();
        }
    }

    public static void register(String hoster, int level, RestApiConfiguration configuration, NotificationListener listener) {
        synchronized (singeltonMonitor) {
            instance.getAndCreate(hoster, configuration).addListener(level, listener);
        }
    }

    public static void unregister(String hoster, int level, NotificationListener listener) {
        synchronized (singeltonMonitor) {
            instance.getHosterEntry(hoster).removeListener(level, listener);
        }
    }

    private synchronized HosterEntry getHosterEntry(String hoster) {

        return entries.get(hoster);
    }

    private synchronized HosterEntry getAndCreate(String hoster, RestApiConfiguration configuration) {
        if (!entries.containsKey(hoster)) {
            entries.put(hoster, new HosterEntry(hoster, configuration));
        }
        return getHosterEntry(hoster);
    }
}