package eu.dime.restapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.dime.model.CALLTYPES;
import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelConfiguration;
import eu.dime.model.ModelHelper;
import eu.dime.model.StaticTestData;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.AccountItem;
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

public class ShareItemTest {

	public final static int SERVER_OFFLINE = -1;
	public final static int TEST_CRASHED = Integer.MAX_VALUE;
	private static DimeHelper dimeHelper = new DimeHelper();
	private TestCollectionShareItem testCollection = new TestCollectionShareItem();
	private ShareTestThread test;

	public enum TL {
		T1_REGISTER_USER_A, T2_REGISTER_USER_B, T3_ADD_B_TO_A_VIA_DUD, T3_CREATE_LIVEPOST_AT_A, T4_SHARE_LIVEPOST_A_TO_B, T5_CREATE_RESOURCE_AT_A, T6_CREATE_DATABOX_AT_A_WITH_ASSIGNED_RESOURCE, T7_SHARE_DATABOX_WITH_ASSIGNED_RESSOURCE_A_TO_B, T12_CREATE_GROUP_AT_A_WITH_ASSIGNED_PERSON, T13_CREATE_LIVEPOST_AT_B, T14_SHARE_LIVEPOST_B_TO_A_VIA_GROUP;

		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	};

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		Logger.getLogger(ShareItemTest.class.getName()).log(Level.INFO, "initating test ... ");
		new ShareItemTest().runFullShareItemTest();
	}
	
	public void runFullShareItemTest() {
		test = new ShareTestThread();
		test.run();
	}

	public class ShareTestThread implements Runnable {

		@Override
		public void run() {
			Logger.getLogger(RestApiTest.class.getName()).log(Level.INFO, "Test resulted with " + doTest() + " error(s)!");
			System.exit(0);
		}

		/**
		 * calls a sequence of tests with the various calls for the given type
		 * as defined in TL (test label)
		 */
		public int doTest() {
			
			try {
				return executeTest();
			} catch (DimeServerNotOnlineException ex) {
				return SERVER_OFFLINE;
			} catch (Exception ex) {
				Logger.getLogger(RestApiTest.class.getName()).log(Level.SEVERE, null, ex);
				return TEST_CRASHED;
			} finally {
				deliverEvaluation();
			}
		}

		public int executeTest() throws DimeServerNotOnlineException, GetAllCrashedException, InitStorageFailedException, Exception {
			int errors = 0;
			ModelConfiguration confA = executeRegisterUserTest(TL.T1_REGISTER_USER_A);
			ModelConfiguration confB = executeRegisterUserTest(TL.T2_REGISTER_USER_B);
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
			
			PersonItem personAatB = executeIsPersonInListTest(confB, StaticTestData.SHARETEST_FIRSTNAMES[0]);
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
			List<AccountItem> accounts = (List<AccountItem>) (Object) RestApiAccess.getAllItems(conf.mainSAID, Model.ME_OWNER, TYPES.ACCOUNT, conf.restApiConfiguration);
			for (ProfileItem profile : profiles) {
				if(profile.getName().contains("Public")) {
					for (AccountItem accountItem : accounts) {
						if(accountItem.getGuid().equals(profile.getServiceAccountId()) && accountItem.getServiceAdapterGUID().equals("di.me")) {
							result = profile;
						}
					}
				}
			}
			return result;
		}

		private PersonItem executeIsPersonInListTest(ModelConfiguration conf, String username) {
			PersonItem result = null;
			List<GenItem> personsOfB = RestApiAccess.getAllItems(conf.mainSAID, Model.ME_OWNER, TYPES.PERSON, conf.restApiConfiguration);
			for (GenItem person : personsOfB) {
				if (((DisplayableItem) person).getName().contains(username)) {
					result = (PersonItem) person;
				}
			}
			return result;
		}

		private ModelConfiguration executeRegisterUserTest(TL tl) throws MalformedURLException, ProtocolException, IOException, DimeServerNotOnlineException {
			boolean isPlausible = false;
			CALLTYPES callType = CALLTYPES.REGISTER_USER;
			String remark = "";
			String uuid = UUID.randomUUID().toString();
			Logger.getLogger(ShareItemTest.class.getName()).log(Level.INFO, tl.toString() + ": " + uuid);
			int i = (tl.equals(TL.T1_REGISTER_USER_A)) ? 0 : 1;
			String firstname = StaticTestData.SHARETEST_FIRSTNAMES[i];
			String lastname = StaticTestData.SHARETEST_LASTNAMES[i];
			String emailAddress = StaticTestData.SHARETEST_LASTNAMES[i] + "@test.de";
			UserItem user = ItemFactory.createNewUserItem(uuid, StaticTestData.DEFAULT_PASSWORD, uuid, firstname, lastname, true, emailAddress);
			long startTime = System.nanoTime();
			ModelConfiguration conf = RestApiAccess.registerNewUserCall(user, DimeHelper.DEFAULT_HOSTNAME, DimeHelper.DEFAULT_PORT, DimeHelper.DEFAULT_USE_HTTPS);
			long endTime = System.nanoTime();
			if (!dimeHelper.dimeServerIsAuthenticated(conf.mainSAID, conf.restApiConfiguration)) {
				throw new DimeServerNotOnlineException();
			} else {
				Vector<GenItem> services = RestApiAccess.getAllItems(conf.mainSAID, Model.ME_OWNER, TYPES.PROFILE, conf.restApiConfiguration);
				if (services != null) {
					if (services.size() > 0) {
						isPlausible = true;
					}
					remark = services.size() + " profiles in list!";
				}
			}
			TestResult testResult = new TestResult(isPlausible, remark, endTime - startTime);
			TestReference testReference = new TestReference(conf.restApiConfiguration.hostName, Model.ME_OWNER, tl, null, callType);
			testCollection.addResponse(testReference, testResult);
			return conf;
		}

		@SuppressWarnings("unchecked")
		private AgentItem executeAddPersonViaDudTest(ModelConfiguration conf, String username) throws InterruptedException {
			boolean isPlausible = false;
			AgentItem result = null;
			String remark = "";
			List<SearchResultItem> searchResults = RestApiAccess.searchGlobal(conf.mainSAID, username, conf.restApiConfiguration);
			long startTime = System.nanoTime();
			if (searchResults.size() == 1) {
				RestApiAccess.addPublicPerson(conf.mainSAID, searchResults.get(0), conf.restApiConfiguration);
			}
			long endTime = System.nanoTime();
			hasNotificationBeenReceived(conf, endTime, ModelHelper.getStringType(TYPES.PERSON), remark);
			List<PersonItem> persons = (List<PersonItem>) (Object) RestApiAccess.getAllItems(conf.mainSAID, Model.ME_OWNER, TYPES.PERSON, conf.restApiConfiguration);
			if (persons.size() > 0) {
				remark = persons.size() + " persons in list (";
				for (PersonItem personItem : persons) {
					if (personItem.getName().contains(StaticTestData.SHARETEST_FIRSTNAMES[1])) {
						result = personItem;
						isPlausible = true;
					}
					remark += personItem.getName() + ", ";
				}
				remark = remark.substring(0, remark.length() - 2);
				remark += ")!";
			}
			TestResult testResult = new TestResult(isPlausible, remark, endTime - startTime);
			TestReference testReference = new TestReference(conf.mainSAID, Model.ME_OWNER, TL.T3_ADD_B_TO_A_VIA_DUD, TYPES.PERSON, CALLTYPES.REGISTER_USER);
			testCollection.addResponse(testReference, testResult);
			return result;
		}

		//TODO also check upload resource
		private DisplayableItem executeCreateItemTest(TL tl, ModelConfiguration conf, TYPES type, List<String> itemsTodAdd) {
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
			hasNotificationBeenReceived(conf, endTime, item.getType(), remark);
			List<GenItem> itemsAfterCall = RestApiAccess.getAllItems(conf.mainSAID, Model.ME_OWNER, type, conf.restApiConfiguration);
			if(!itemsAfterCall.contains(resultItem)) {
				remark += "itemsaftercall doesn´t contains item!";
			}
			if(item.getGuid().equals(resultItem.getGuid())) remark += "guid not changed!";
			isPlausible = (!myResponse.jsonResponse.callFailed && itemsAfterCall.size() - itemsBeforeCall.size() == 1) ? true : false;
			TestResult testResult = new TestResult(isPlausible, remark, endTime - startTime);
			TestReference testReference = new TestReference(conf.mainSAID, Model.ME_OWNER, tl, type, CALLTYPES.AT_ITEM_POST_NEW);
			testCollection.addResponse(testReference, testResult);
			return resultItem;
		}

		@SuppressWarnings("unchecked")
		private boolean executeShareItemTest(TL tl, ModelConfiguration confSender, ModelConfiguration confReceiver, GenItem item, AgentItem agent, ProfileItem senderProfile) throws InterruptedException {
			boolean isPlausible = false;
			String remark = "";
			item.addAccessingAgent(senderProfile.getServiceAccountId(), agent.getGuid(), agent.getMType(), null);
			long startTime = System.nanoTime();
			RestApiAccess.postItemUpdate(confSender.mainSAID, Model.ME_OWNER, item.getMType(), item, confSender.restApiConfiguration);
			long endTime = System.nanoTime();
			if(hasNotificationBeenReceived(confReceiver, endTime, item.getType(), remark)) {
				remark += "notification received on receiver side";
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
		
		private boolean hasNotificationBeenReceived(ModelConfiguration conf, long endTime, String type, String remark) {
			boolean notificationReceived = false;
			while ((notificationReceived || (endTime + 5000000000l < System.nanoTime()))) {
				List<NotificationItem> notifications = RestApiAccess.getCometCall(conf.mainSAID, conf.restApiConfiguration);
				for (NotificationItem notificationItem : notifications) {
					if(notificationItem.getOperation().equals(NotificationItem.OPERATION_CREATE) && notificationItem.getElement().getType().equals(type)) {
						notificationReceived = true;
					}
				}
			}
			remark += (notificationReceived) ? "system notification received!" : "no system notification received!";
			return notificationReceived;
		}

	}

}
