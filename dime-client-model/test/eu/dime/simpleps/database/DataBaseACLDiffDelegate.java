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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.simpleps.database;

import eu.dime.control.LoadingViewHandler;
import eu.dime.model.CreateItemFailedException;
import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.TYPES;
import eu.dime.model.acl.ACLHelper;
import eu.dime.model.acl.ACLPackage;
import eu.dime.model.acl.ACLPerson;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.usernotification.UNEntryRefToItem;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;
import eu.dime.simpleps.SaidRegistry;
import eu.dime.simpleps.SimplePSHelper;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author simon
 */
public class DataBaseACLDiffDelegate implements ACLHelper.ACLDiffDelegate {

	private ModelRequestContext mrContext;
	private GenItem newItem;

	public DataBaseACLDiffDelegate(ModelRequestContext mrContext, GenItem newItem) {
		this.mrContext = mrContext;
		this.newItem = newItem;
	}

	public void handleAddedPackage(ACLPackage aclPackage) {
		for (ACLPerson person : aclPackage.getPersons()) {
			handleAddedPerson(aclPackage.getSaidSender(), person);
		}
		for (String groupId : aclPackage.getGroups()) {
			handleAddedGroup(aclPackage.getSaidSender(), groupId);
		}
		for (String serviceId : aclPackage.getServices()) {
			handleAddedService(aclPackage.getSaidSender(), serviceId);
		}
	}

	public void handleRemovedPackage(ACLPackage aclPackage) {
		for (ACLPerson person : aclPackage.getPersons()) {
			handleRemovedPerson(aclPackage.getSaidSender(), person);
		}
		for (String groupId : aclPackage.getGroups()) {
			handleRemovedGroup(aclPackage.getSaidSender(), groupId);
		}
		for (String serviceId : aclPackage.getServices()) {
			handleRemovedService(aclPackage.getSaidSender(), serviceId);
		}
	}

	public void handleAddedPerson(String said, ACLPerson person) {

		String saidReceiver = person.getSaidReceiver();
		if (saidReceiver == null) {
			saidReceiver = ModelHelper.getDefaultReceiverSaidForPerson(mrContext, person.getPersonId());
		}
		try {
			createSharedItemForReceiver(mrContext.hoster, said, newItem, saidReceiver, mrContext.lvHandler);
		} catch (CreateItemFailedException ex) {
			Logger.getLogger(DataBaseACLDiffDelegate.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public void handleAddedGroup(String said, String groupGuid) {
		GroupItem group = (GroupItem) Model.getInstance().getItem(mrContext, TYPES.GROUP, groupGuid);
		for (String personId : group.getItems()) {
			ACLPerson person = new ACLPerson(personId, null);
			handleAddedPerson(said, person);
		}
	}

	public void handleAddedService(String said, String serviceGuid) {
		Logger.getLogger(DataBaseACLDiffDelegate.class.getName()).log(Level.WARNING, "Not supported yet.");
	}

	public void handleRemovedPerson(String said, ACLPerson person) {
		String saidReceiver = person.getSaidReceiver();
		if (saidReceiver == null) {
			saidReceiver = ModelHelper.getDefaultReceiverSaidForPerson(mrContext, person.getPersonId());
		}
		try {
			removeSharedItemForReceiver(mrContext.hoster, said, newItem, saidReceiver, mrContext.lvHandler);
		} catch (CreateItemFailedException ex) {
			Logger.getLogger(DataBaseACLDiffDelegate.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void handleRemovedGroup(String said, String groupGuid) {
		GroupItem group = (GroupItem) Model.getInstance().getItem(mrContext, TYPES.GROUP, groupGuid);
		for (String personId : group.getItems()) {
			ACLPerson person = new ACLPerson(personId, null);
			handleRemovedPerson(said, person);
		}
	}

	public void handleRemovedService(String said, String serviceGuid) {
		Logger.getLogger(DataBaseACLDiffDelegate.class.getName()).log(Level.WARNING, "Not supported yet.");
	}

	private String getHosterReceiver(String hosterSender, String saidReceiver, LoadingViewHandler lvHandler) {
		// find hosterReceiver
		String result = SaidRegistry.findHosterIdByServiceAccount(lvHandler, saidReceiver, hosterSender);
		if (result == null) {
			Logger.getLogger(DataBaseACLDiffDelegate.class.getName()).log(Level.SEVERE, "hoster id not found for said:" + saidReceiver);
		}
		return result;
	}

	private String getRemoteSenderGuid(String hosterSender, String saidSender, String hosterReceiver, String saidReceiver, LoadingViewHandler lvHandler) throws CreateItemFailedException {
		// get appropriate user profile and person id of sender at receiver
		ModelRequestContext receiverAtMeContext = new ModelRequestContext(hosterReceiver, Model.ME_OWNER, lvHandler);
		String remoteSenderGUID = ModelHelper.findPersonGuidByServiceAccount(receiverAtMeContext, saidSender);
		if ((remoteSenderGUID == null) || (remoteSenderGUID.length() == 0)) {
			Logger.getLogger(DatabaseAccess.class.getName()).log(Level.WARNING,	"user profile with said(sender):" + saidSender + " not found for hoster:" + hosterReceiver + " (said(receiver): " + saidReceiver + ")\n generating new one");
			// FIXME : - generate user profile on the fly
			ProfileItem senderProfileAtRemote = SimplePSHelper.createContactEntryAndPublicProfileForRemotePerson(lvHandler, hosterSender, saidSender, hosterReceiver);
			remoteSenderGUID = senderProfileAtRemote.getUserId();
		}
		Logger.getLogger(DatabaseAccess.class.getName()).log(Level.INFO, "found userId for user profile with said(sender):" + saidSender + ":\n" + remoteSenderGUID);
		return remoteSenderGUID;
	}

	private boolean createSharedItemForReceiver(String hosterSender, String saidSender, GenItem itemSender, String saidReceiver, LoadingViewHandler lvHandler) throws CreateItemFailedException {
		// find hosterReceiver
		String hosterReceiver = getHosterReceiver(hosterSender, saidReceiver, lvHandler);
		if (hosterReceiver == null) {
			return false;
		}
		String remoteSenderGUID = getRemoteSenderGuid(hosterSender, saidSender, hosterReceiver, saidReceiver, lvHandler);
		// create the shared item for the receiver
		ModelRequestContext receiverShareContext = new ModelRequestContext(hosterReceiver, remoteSenderGUID, lvHandler);
		try {
			GenItem itemReceiver = itemSender.getClone();
            //update userId
			if (ModelHelper.isDisplayableItem(itemReceiver.getMType())) {
				((DisplayableItem) itemReceiver).setUserId(remoteSenderGUID);
			}
			itemReceiver = Model.getInstance().createItem(receiverShareContext, itemReceiver);
			// send notification to receiver
			DatabaseAccess.sendNotification(new ModelRequestContext(hosterReceiver, Model.ME_OWNER, lvHandler), NotificationItem.OPERATION_CREATE, itemReceiver);
			// create user notification
            if (ModelHelper.isDisplayableItem(itemReceiver.getMType())) {
                UserNotificationItem userNotification = ItemFactory.createRefItemUserNotification((DisplayableItem)itemReceiver, UNEntryRefToItem.OPERATION_SHARED);
                userNotification = (UserNotificationItem) Model.getInstance().createItem(receiverShareContext, userNotification);
                DatabaseAccess.sendNotification(new ModelRequestContext(hosterReceiver, Model.ME_OWNER, lvHandler), NotificationItem.OPERATION_CREATE, userNotification);
            }
		} catch (CreateItemFailedException ex) {
			Logger.getLogger(DatabaseAccess.class.getName()).log(Level.SEVERE, "sharing failed for hoster:" + hosterReceiver + " (said(receiver): " + saidReceiver + ")" + " owner(remote-guid of sender):" + remoteSenderGUID + " (said(sender):" + saidSender + ")", ex);
			return false;
		}
		return true;
	}

	private boolean removeSharedItemForReceiver(String hosterSender, String saidSender, GenItem itemSender, String saidReceiver, LoadingViewHandler lvHandler) throws CreateItemFailedException {
		// find hosterReceiver
		String hosterReceiver = getHosterReceiver(hosterSender, saidReceiver, lvHandler);
		if (hosterReceiver == null) {
			return false;
		}
		String remoteSenderGUID = getRemoteSenderGuid(hosterSender, saidSender, hosterReceiver, saidReceiver, lvHandler);
		// create the shared item for the receiver
		ModelRequestContext receiverShareContext = new ModelRequestContext(hosterReceiver, remoteSenderGUID, lvHandler);
		GenItem itemReceiver = Model.getInstance().getItem(receiverShareContext, itemSender.getMType(), itemSender.getGuid());
		if (itemReceiver == null) {
			Logger.getLogger(DataBaseACLDiffDelegate.class.getName()).log(Level.SEVERE, "cannot find shared item at receiver - unshare aborted!");
			return false;
		}
		itemReceiver = Model.getInstance().removeItem(receiverShareContext, itemReceiver.getGuid(), itemReceiver.getMType());
		// send notification to receiver
		DatabaseAccess.sendNotification(new ModelRequestContext(hosterReceiver, Model.ME_OWNER, lvHandler), NotificationItem.OPERATION_DELETE, itemReceiver);
		return true;
	}
	
}
