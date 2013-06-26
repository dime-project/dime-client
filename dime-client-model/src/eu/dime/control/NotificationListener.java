/*
 *  Description of NotificationListener
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 05.06.2012
 */
package eu.dime.control;

import eu.dime.model.specialitem.NotificationItem;

/**
 * NotificationListener
 * 
 */
public interface NotificationListener {

    public void notificationReceived(String fromHoster, NotificationItem item);
}
