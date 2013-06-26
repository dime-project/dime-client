/*
 *  Description of PSNotificationsManager
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 04.06.2012
 */
package eu.dime.simpleps.database;

import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.control.LoadingViewHandler;
import eu.dime.model.GenItem;
import eu.dime.model.specialitem.NotificationItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PSNotificationsManager
 */
@SuppressWarnings({"unchecked","rawtypes", "unused"})
public class PSNotificationsManager implements Runnable {

    private static final PSNotificationsManager instance = new PSNotificationsManager(); // singelton

    public static PSNotificationsManager getInstance() {
        return instance;
    }
	private LoadingViewHandler lvh = new DummyLoadingViewHandler(); //FIXME
	private Map<String, List<NotificationItem> > allNotifications = new HashMap();
    private boolean stopping = false;
    /**
     * stores the oldest starting from time for each client
     */
    private Map<String, Long> lastRequestTime = new HashMap();

    private PSNotificationsManager() {
    }
    
    private List<NotificationItem> getHosterNotifications(String hoster) {
        List<NotificationItem> result = allNotifications.get(hoster);
        if (result==null){ //lazy creation
            result= new Vector();
            allNotifications.put(hoster, result);
        }
        return result;
    }

    /**
     * returns a list of notifications (clones) for a specific hoster
     * that have not yet been sent to the client.
     * 
     * @param hoster
     * @param clientId
     * @param startingFrom
     * @return 
     */
    public synchronized List<GenItem> getNotification(String hoster, String clientId, long startingFrom) {
        //update log on lastRequest
        lastRequestTime.put(clientId, System.currentTimeMillis());
        List<NotificationItem> hosterNotifications = getHosterNotifications(hoster);
        List<GenItem> result = new ArrayList();
        for (NotificationItem notification : hosterNotifications) {
            if ((!notification.wasSentTo(clientId))) {
                notification.addSentTo(clientId);                
                result.add(notification.getClone()); //return clone to the outside
            }
        }
        return result;
    }
    
    public synchronized void addNotification(String hoster, NotificationItem notification) {
        getHosterNotifications(hoster).add(notification);
    }

    public void run() {
        while (!stopping) {
            try {
                Thread.sleep(1000 * 20);//awake all 20 seconds
            } catch (InterruptedException ex) {
                Logger.getLogger(PSNotificationsManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void stop() {
        this.stopping = true;
    }
    
}
