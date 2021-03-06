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

package eu.dime.restapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.dime.control.MultiHosterNotificationManager;
import eu.dime.control.NotificationListener;
import eu.dime.model.CALLTYPES;
import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelConfiguration;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelTypeNotFoundException;
import eu.dime.model.StaticTestData;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.AgentItem;
import eu.dime.model.displayable.DataboxItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.SearchResultItem;
import eu.dime.model.specialitem.UserItem;
import eu.dime.model.specialitem.usernotification.UNEntryRefToItem;
import eu.dime.model.specialitem.usernotification.UN_TYPE;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;
import eu.dime.model.storage.InitStorageFailedException;
import eu.dime.restapi.TestCollectionShareItem.TestReference;
import eu.dime.restapi.TestCollectionShareItem.TestResult;
import sit.io.FileHelper;

public class ShareItemTest implements NotificationListener {

	public final static int SERVER_OFFLINE = -1;
	public final static int TEST_CRASHED = Integer.MAX_VALUE;
	public final static String USER_PREFIX = "dummyaccount";
	private static DimeHelper dimeHelper = new DimeHelper();
	private TestCollectionShareItem testCollection = new TestCollectionShareItem();
	private HashMap<String, List<NotificationItem>> notifications = new HashMap<String, List<NotificationItem>>();

	public enum TL {
		T1_REGISTER_USER_A, T2_REGISTER_USER_B, T3_ADD_B_TO_A_VIA_DUD, T3_CREATE_LIVEPOST_AT_A, T4_SHARE_LIVEPOST_A_TO_B, T5_CREATE_RESOURCE_AT_A, T6_CREATE_DATABOX_AT_A_WITH_ASSIGNED_RESOURCE, T7_SHARE_DATABOX_WITH_ASSIGNED_RESSOURCE_A_TO_B, T12_CREATE_GROUP_AT_A_WITH_ASSIGNED_PERSON, T13_CREATE_LIVEPOST_AT_B, T14_SHARE_LIVEPOST_B_TO_A_VIA_GROUP;

		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	};

	/**
	 * @param args
	 * the command line arguments
	 */
	public static void main(String[] args) {
		Logger.getLogger(ShareItemTest.class.getName()).log(Level.INFO, "initating test ... ");
		new ShareItemTest().runFullShareItemTest();
	}
	
	public void runFullShareItemTest() {
		Logger.getLogger(RestApiTest.class.getName()).log(Level.INFO, "Test resulted with " + doTest() + " error(s)!");
		System.exit(0);
	}
	
	/**
	 * calls a sequence of tests with the various calls for the given type
	 * as defined in TL (test label)
	 */
	public int doTest() {
		try {
			MultiHosterNotificationManager.start();
			return executeTest();
		} catch (DimeServerNotOnlineException ex) {
			return SERVER_OFFLINE;
		} catch (Exception ex) {
			Logger.getLogger(RestApiTest.class.getName()).log(Level.SEVERE, null, ex);
			return TEST_CRASHED;
		} finally {
			MultiHosterNotificationManager.stop();
			deliverEvaluation();
		}
	}


	public int executeTest() throws DimeServerNotOnlineException, GetAllCrashedException, InitStorageFailedException, Exception {
		int errors = 0;
		ModelConfiguration confA = executeRegisterUserTest(TL.T1_REGISTER_USER_A, DimeHelper.DEFAULT_HOSTNAME);
		MultiHosterNotificationManager.register(confA.mainSAID, 1, confA.restApiConfiguration, this);
		ModelConfiguration confB = executeRegisterUserTest(TL.T2_REGISTER_USER_B, DimeHelper.DEFAULT_HOSTNAME);
		MultiHosterNotificationManager.register(confB.mainSAID, 1, confB.restApiConfiguration, this);
		ProfileItem senderA = getDefaultProfileForSharing(confA);
		if (senderA == null) {
			throw new Exception("No default profile for A");
		}
		AgentItem receiverB = executeAddPersonViaDudTest(confA, confB.username);
		if (receiverB == null) {
			throw new Exception("Could not add person B");
		}
		LivePostItem livepost1 = (LivePostItem) executeCreateItemTest(TL.T3_CREATE_LIVEPOST_AT_A, confA, TYPES.LIVEPOST, null);
		if (livepost1 != null) {
			if (executeShareItemTest(TL.T4_SHARE_LIVEPOST_A_TO_B, confA, confB, livepost1, receiverB, senderA)) {
				errors++;
			}
		} else {
			errors++;
		}
		ResourceItem resource = (ResourceItem) executeCreateItemTest(TL.T5_CREATE_RESOURCE_AT_A, confA, TYPES.RESOURCE, null);
		DataboxItem databox = (DataboxItem) executeCreateItemTest(TL.T6_CREATE_DATABOX_AT_A_WITH_ASSIGNED_RESOURCE, confA, TYPES.DATABOX, Arrays.asList(resource.getGuid()));
		if (databox != null && databox.getItems().contains(resource.getGuid())) {
			if (executeShareItemTest(TL.T7_SHARE_DATABOX_WITH_ASSIGNED_RESSOURCE_A_TO_B, confA, confB, databox, receiverB, senderA)) {
				errors++;
			}
		} else {
			errors++;
		}
		ProfileItem senderB = getDefaultProfileForSharing(confB);
		if (senderB == null) {
			throw new Exception("No default profile for B");
		}
		
		PersonItem personAatB = executeIsPersonInListTest(confB, USER_PREFIX);
		if (personAatB == null) {
			throw new Exception("Could not find A at B");
		}
		GroupItem group = (GroupItem) executeCreateItemTest(TL.T12_CREATE_GROUP_AT_A_WITH_ASSIGNED_PERSON, confB, TYPES.GROUP, Arrays.asList(personAatB.getGuid()));
		if(group != null && group.containsItem(personAatB.getGuid())) {
			LivePostItem livepost2 = (LivePostItem) executeCreateItemTest(TL.T13_CREATE_LIVEPOST_AT_B, confB, TYPES.LIVEPOST, null);
			if (livepost2 != null) {
				if (executeShareItemTest(TL.T14_SHARE_LIVEPOST_B_TO_A_VIA_GROUP, confB, confA, livepost2, group, senderB)) {
					errors++;
				}
			} else {
				errors++;
			}
		} else {
			errors++;
		}
		return errors;
	}

	@SuppressWarnings("unchecked")
	private ProfileItem getDefaultProfileForSharing(ModelConfiguration conf) {
		ProfileItem result = null;
		List<ProfileItem> profiles = (List<ProfileItem>) (Object) RestApiAccess.getAllItems(conf.mainSAID, Model.ME_OWNER, TYPES.PROFILE, conf.restApiConfiguration);
		if(profiles != null) {
			for (ProfileItem profile : profiles) {
				if(profile.getName().toLowerCase().contains("business") && profile.supportsSharing()) {
					result = profile;
				}
			}
		}
		return result;
	}

	private PersonItem executeIsPersonInListTest(ModelConfiguration conf, String username) {
		PersonItem result = null;
		List<GenItem> personsOfB = RestApiAccess.getAllItems(conf.mainSAID, Model.ME_OWNER, TYPES.PERSON, conf.restApiConfiguration);
		if(personsOfB != null) {
			for (GenItem person : personsOfB) {
				if (((DisplayableItem) person).getName().contains(username)) {
					result = (PersonItem) person;
				}
			}
		}
		return result;
	}

	private ModelConfiguration executeRegisterUserTest(TL tl, String hostname) throws MalformedURLException, ProtocolException, IOException, DimeServerNotOnlineException {
		boolean isPlausible = false;
		CALLTYPES callType = CALLTYPES.REGISTER_USER;
		String remark = "";
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replaceAll("[^A-Za-z0-9]", "");
		String name = USER_PREFIX + uuid;
		Logger.getLogger(ShareItemTest.class.getName()).log(Level.INFO, tl.toString() + ": " + name);
		String firstname = name + "first";
		String lastname = name + "last";
		String emailAddress = USER_PREFIX + "@test.de";
		UserItem user = ItemFactory.createNewUserItem(name, StaticTestData.DEFAULT_PASSWORD, name, firstname, lastname, true, emailAddress);
		long startTime = System.nanoTime();
		ModelConfiguration conf = RestApiAccess.registerNewUserCall(user, hostname, DimeHelper.DEFAULT_PORT, DimeHelper.DEFAULT_USE_HTTPS);
		long endTime = System.nanoTime();
		if (!dimeHelper.dimeServerIsAuthenticated(conf.mainSAID, conf.restApiConfiguration)) {
			throw new DimeServerNotOnlineException();
		} else {
			Vector<GenItem> profiles = RestApiAccess.getAllItems(conf.mainSAID, Model.ME_OWNER, TYPES.PROFILE, conf.restApiConfiguration);
			if (profiles != null) {
				if (profiles.size() > 0) {
					isPlausible = true;
				}
				remark = profiles.size() + " profiles in list!";
			}
		}
		TestResult testResult = new TestResult(isPlausible, remark, endTime - startTime);
		TestReference testReference = new TestReference(conf.mainSAID, Model.ME_OWNER, tl, null, callType);
		testCollection.addResponse(testReference, testResult);
		return conf;
	}

	@SuppressWarnings("unchecked")
	private AgentItem executeAddPersonViaDudTest(ModelConfiguration conf, String username) throws InterruptedException, ModelTypeNotFoundException {
		boolean isPlausible = false;
		AgentItem result = null;
		String remark = "";
		List<SearchResultItem> searchResults = RestApiAccess.searchGlobal(conf.mainSAID, username, conf.restApiConfiguration);
		long startTime = System.nanoTime();
		if (searchResults.size() == 1) {
			RestApiAccess.addPublicPerson(conf.mainSAID, searchResults.get(0), conf.restApiConfiguration);
		}
		long endTime = System.nanoTime();
		hasNotificationBeenReceived(conf, endTime, ModelHelper.getStringType(TYPES.PERSON), username, remark);
		List<PersonItem> persons = (List<PersonItem>) (Object) RestApiAccess.getAllItems(conf.mainSAID, Model.ME_OWNER, TYPES.PERSON, conf.restApiConfiguration);
		if (persons.size() > 0) {
			remark = persons.size() + " persons in list (";
			for (PersonItem personItem : persons) {
				if (personItem.getName().contains(username)) {
					result = personItem;
					isPlausible = true;
				}
				remark += personItem.getName() + ", ";
			}
			remark = remark.substring(0, remark.length() - 2);
			remark += ")!";
		}
		TestResult testResult = new TestResult(isPlausible, remark, endTime - startTime);
		TestReference testReference = new TestReference(conf.mainSAID, Model.ME_OWNER, TL.T3_ADD_B_TO_A_VIA_DUD, TYPES.PERSON, CALLTYPES.AT_ITEM_POST_NEW);
		testCollection.addResponse(testReference, testResult);
		return result;
	}

	//TODO also check upload resource
	private DisplayableItem executeCreateItemTest(TL tl, ModelConfiguration conf, TYPES type, List<String> itemsTodAdd) throws ModelTypeNotFoundException {
		JSONResponseContainer myResponse = new JSONResponseContainer();
		boolean isPlausible = false;
		DisplayableItem resultItem = null;
		DisplayableItem item = null;
		String remark = "";
		item = ItemFactory.createNewDisplayableItemByType(type, "Testname");
		if(itemsTodAdd != null) item.addItems(itemsTodAdd);
		List<GenItem> itemsBeforeCall = RestApiAccess.getAllItems(conf.mainSAID, Model.ME_OWNER, type, conf.restApiConfiguration);
		long startTime = System.nanoTime();
		resultItem = (DisplayableItem) RestApiAccess.postItemNew(conf.mainSAID, Model.ME_OWNER, type, item, myResponse, conf.restApiConfiguration);
		long endTime = System.nanoTime();
		hasNotificationBeenReceived(conf, endTime, resultItem.getType(), resultItem.getName(), remark);
		List<GenItem> itemsAfterCall = RestApiAccess.getAllItems(conf.mainSAID, Model.ME_OWNER, type, conf.restApiConfiguration);
		if(!itemsAfterCall.contains(resultItem)) {
			remark += "itemsaftercall doesn�t contains item!";
		}
		if(item.getGuid().equals(resultItem.getGuid())) remark += "guid not changed!";
		isPlausible = (!myResponse.jsonResponse.callFailed && itemsAfterCall.size() - itemsBeforeCall.size() == 1) ? true : false;
		TestResult testResult = new TestResult(isPlausible, remark, endTime - startTime);
		TestReference testReference = new TestReference(conf.mainSAID, Model.ME_OWNER, tl, type, CALLTYPES.AT_ITEM_POST_NEW);
		testCollection.addResponse(testReference, testResult);
		return resultItem;
	}

	@SuppressWarnings("unchecked")
	private boolean executeShareItemTest(TL tl, ModelConfiguration confSender, ModelConfiguration confReceiver, DisplayableItem item, AgentItem agent, ProfileItem senderProfile) throws InterruptedException, ModelTypeNotFoundException {
		boolean isPlausible = false;
		String remark = "";
		item.addAccessingAgent(senderProfile.getServiceAccountId(), agent.getGuid(), agent.getMType(), null);
		long startTime = System.nanoTime();
		RestApiAccess.postItemUpdate(confSender.mainSAID, Model.ME_OWNER, item.getMType(), item, confSender.restApiConfiguration);
		long endTime = System.nanoTime();
		if(hasNotificationBeenReceived(confReceiver, endTime, item.getType(), item.getName(), remark)) {
			JSONResponseContainer responseNotifications = new JSONResponseContainer();
			List<UserNotificationItem> userNotifications = (List<UserNotificationItem>) (Object) RestApiAccess.getAllItems(confReceiver.mainSAID, Model.ME_OWNER, TYPES.USERNOTIFICATION, responseNotifications, confReceiver.restApiConfiguration);
			if (responseNotifications.jsonResponse.callFailed || userNotifications == null) {
				remark += "could not get usernotifications on receiver side!";
			} else if (userNotifications.size() == 0) {
				remark += "no usernotifications at receiver!";
			} else {
				for (UserNotificationItem genItem : userNotifications) {
					if (genItem.getUnType().equals(UN_TYPE.REF_TO_ITEM)) {
						UNEntryRefToItem entry = (UNEntryRefToItem) genItem.getUnEntry();
						if(entry.getOperation().equals(UNEntryRefToItem.OPERATION_SHARED)) {
							remark += "shared notification: " + entry.getName() + ";";
						}
					}
				}
			}
			JSONResponseContainer responseItems = new JSONResponseContainer();
			Vector<GenItem> itemsAtReceiver = RestApiAccess.getItemsOfAllOwners(confReceiver.mainSAID, item.getMType(), responseItems, confReceiver.restApiConfiguration);
			if (responseItems.jsonResponse.callFailed || itemsAtReceiver == null) {
				remark += "could not get items on receiver side!";
			} else if (itemsAtReceiver.size() == 0) {
				remark += "no item at receiver created!";
			} else {
				for (GenItem genItem : itemsAtReceiver) {
					if (((DisplayableItem) genItem).getName().equals(((DisplayableItem) item).getName())) {
						isPlausible = true;
					}
				}
				if(!isPlausible) {
					remark += "item not found!";
				}
			}
			boolean childFound = false;
			if(ModelHelper.isParentable(item)) {
				JSONResponseContainer responseChildrenItems = new JSONResponseContainer();
				Vector<GenItem> childrenItemsAtReceiver = RestApiAccess.getItemsOfAllOwners(confReceiver.mainSAID, ModelHelper.getChildType(item.getMType()), responseChildrenItems, confReceiver.restApiConfiguration);
				if (responseChildrenItems.jsonResponse.callFailed || childrenItemsAtReceiver == null) {
					remark += "could not get children on receiver side!";
				} else if (childrenItemsAtReceiver.size() == 0) {
					remark += "no child at receiver created!";
				} else {
					for (GenItem genItem : childrenItemsAtReceiver) {
						if (((DisplayableItem) genItem).getName().equals(((DisplayableItem) item).getName())) {
							childFound = true;
						}
					}
					if(!childFound) {
						remark += "child not found!";
						isPlausible = false;
					}
				}
			}
		}
		TestResult testResult = new TestResult(isPlausible, remark, endTime - startTime);
		TestReference testReference = new TestReference(confSender.mainSAID, Model.ME_OWNER, tl, item.getMType(), CALLTYPES.AT_ITEM_POST_UPDATE);
		testCollection.addResponse(testReference, testResult);
		return isPlausible;
	}
	
	private void deliverEvaluation() {
		FileHelper fh = new FileHelper();
		try {
			fh.writeToFile("result-sharetest.csv", testCollection.toString());
		} catch (IOException ex) {
			Logger.getLogger(RestApiTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean hasNotificationBeenReceived(ModelConfiguration conf, long endTime, String type, String name, String remark) throws ModelTypeNotFoundException {
		boolean notificationReceived = false;
		while ((notificationReceived || (endTime + 5000000000l < System.nanoTime()))) {
			if(notifications.containsKey(conf.mainSAID) && notifications.get(conf.mainSAID) != null) {
				List<DisplayableItem> items = (List<DisplayableItem>) (Object) RestApiAccess.getAllItems(conf.mainSAID, Model.ME_OWNER, ModelHelper.getMTypeFromString(type), null, conf.restApiConfiguration);
				for (NotificationItem notificationItem : notifications.get(conf.mainSAID)) {
					if(notificationItem.getOperation().equals(NotificationItem.OPERATION_CREATE) && notificationItem.getElement().getType().equals(type)) {
						for (DisplayableItem displayableItem : items) {
							if(displayableItem.getName().toLowerCase().contains(name.toLowerCase())) {
								notificationReceived = true;
							}
						}
					}
				}
			}
		}
		remark += (notificationReceived) ? "system notification received!" : "no system notification received!";
		return notificationReceived;
	}

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(notifications.containsKey(fromHoster)) {
			notifications.get(fromHoster).add(item);
		} else {
			notifications.put(fromHoster, new ArrayList<NotificationItem>(Arrays.asList(item)));
		}
	}

}
