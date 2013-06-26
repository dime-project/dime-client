package eu.dime.restapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.control.NotificationListener;
import eu.dime.control.NotificationManager;
import eu.dime.model.CALLTYPES;
import eu.dime.model.CreateItemFailedException;
import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.ItemGenerator;
import eu.dime.model.Model;
import eu.dime.model.ModelConfiguration;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.SharingNotSupportedForSAIDException;
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
import eu.dime.model.displayable.ShareableItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.SearchResultItem;
import eu.dime.model.specialitem.UserItem;
import eu.dime.model.storage.InitStorageFailedException;
import eu.dime.restapi.TestCollectionShareItem.TestReference;
import eu.dime.restapi.TestCollectionShareItem.TestResult;
import sit.io.FileHelper;

public class ShareItemTest implements NotificationListener {

	public final static int SERVER_OFFLINE = -1;
	public final static int TEST_CRASHED = Integer.MAX_VALUE;
	private ItemGenerator itemGenerator = new ItemGenerator();
	private static DimeHelper dimeHelper = new DimeHelper();
	private TestCollectionShareItem testCollection = new TestCollectionShareItem();
	private ShareTestThread test;
	private Object createItemLock = new Object();

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
		NotificationManager.registerSecondLevel(this);
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
			Model.getInstance().updateSettings(confA);
			ModelRequestContext mrcA = new ModelRequestContext(confA.mainSAID, Model.ME_OWNER, new DummyLoadingViewHandler());
			ProfileItem senderA = ModelHelper.getDefaultProfileForSharing(mrcA);
			if (senderA == null)
				throw new Exception("No default profile for A");
			AgentItem receiverB = executeAddPersonViaDudTest(mrcA, confB.username);
			if (receiverB == null)
				throw new Exception("Could not add person B");
			LivePostItem livepost1 = (LivePostItem) executeCreateItemTest(TL.T3_CREATE_LIVEPOST_AT_A, mrcA, TYPES.LIVEPOST);
			if (livepost1 != null) {
				if (executeShareItemTest(TL.T4_SHARE_LIVEPOST_A_TO_B, mrcA, confB, (ShareableItem) livepost1, receiverB, senderA))
					errors++;
			} else {
				errors++;
			}
			ResourceItem resource = (ResourceItem) executeCreateItemTest(TL.T5_CREATE_RESOURCE_AT_A, mrcA, TYPES.RESOURCE);
			DataboxItem databox = (DataboxItem) executeCreateItemTest(TL.T6_CREATE_DATABOX_AT_A_WITH_ASSIGNED_RESOURCE, mrcA, TYPES.DATABOX);
			if (databox != null && databox.getItems().contains(resource.getGuid())) {
				if (executeShareItemTest(TL.T7_SHARE_DATABOX_WITH_ASSIGNED_RESSOURCE_A_TO_B, mrcA, confB, (ShareableItem) databox, receiverB, senderA))
					errors++;
			} else {
				errors++;
			}
			Model.getInstance().updateSettings(confB);
			ModelRequestContext mrcB = new ModelRequestContext(confB.mainSAID, Model.ME_OWNER, new DummyLoadingViewHandler());
			ProfileItem senderB = ModelHelper.getDefaultProfileForSharing(mrcB);
			if (senderB == null)
				throw new Exception("No default profile for B");
			List<GenItem> personsOfB = Model.getInstance().getAllItems(mrcB, TYPES.PERSON);
			PersonItem personAatB = null;
			for (GenItem person : personsOfB) {
				if (((DisplayableItem) person).getName().contains(StaticTestData.SHARETEST_FIRSTNAMES[1])) {
					personAatB = (PersonItem) person;
				}
			}
			if (personAatB == null)
				throw new Exception("Could not find A at B");
			GroupItem group = (GroupItem) executeCreateItemTest(TL.T12_CREATE_GROUP_AT_A_WITH_ASSIGNED_PERSON, mrcB, TYPES.GROUP);
			LivePostItem livepost2 = (LivePostItem) executeCreateItemTest(TL.T13_CREATE_LIVEPOST_AT_B, mrcB, TYPES.LIVEPOST);
			if (livepost2 != null) {
				if (executeShareItemTest(TL.T14_SHARE_LIVEPOST_B_TO_A_VIA_GROUP, mrcB, confA, (ShareableItem) livepost2, group, senderB))
					errors++;
			} else {
				errors++;
			}
			return errors;
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

		private AgentItem executeAddPersonViaDudTest(ModelRequestContext mrc, String username) throws InterruptedException {
			boolean isPlausible = false;
			AgentItem result = null;
			String remark = "";
			List<SearchResultItem> searchResults = ModelHelper.searchGlobaly(mrc, username);
			long startTime = System.nanoTime();
			if (searchResults.size() == 1) {
				ModelHelper.addPublicContact(mrc, searchResults.get(0));
			}
			long endTime = System.nanoTime();
			synchronized (createItemLock) {
				while(endTime + 2000000 > System.nanoTime()){
					createItemLock.wait();
					Logger.getLogger(RestApiTest.class.getName()).log(Level.SEVERE, "test", "test");
				}
			}
			List<PersonItem> persons = ModelHelper.getAllPersons(mrc);
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
			TestReference testReference = new TestReference(mrc.hoster, mrc.owner, TL.T3_ADD_B_TO_A_VIA_DUD, TYPES.PERSON, CALLTYPES.REGISTER_USER);
			testCollection.addResponse(testReference, testResult);
			return result;
		}

		private DisplayableItem executeCreateItemTest(TL tl, ModelRequestContext mrc, TYPES type) {
			boolean isPlausible = false;
			DisplayableItem resultItem = null;
			DisplayableItem item = null;
			String remark = "";
			item = itemGenerator.generateRandomItem(mrc, type);
			long startTime = System.nanoTime();
			List<DisplayableItem> itemsBeforeCall = ModelHelper.getAllDisplayableItems(mrc, type);
			try {
				resultItem = (DisplayableItem) Model.getInstance().createItem(mrc, item);
			} catch (CreateItemFailedException e) {
				e.printStackTrace();
			}
			List<DisplayableItem> itemsAfterCall = ModelHelper.getAllDisplayableItems(mrc, type);
			long endTime = System.nanoTime();
			isPlausible = (itemsAfterCall.size() - itemsBeforeCall.size() == 1) ? true : false;
			TestResult testResult = new TestResult(isPlausible, remark, endTime - startTime);
			TestReference testReference = new TestReference(mrc.hoster, mrc.owner, tl, type, CALLTYPES.AT_ITEM_POST_NEW);
			testCollection.addResponse(testReference, testResult);
			return resultItem;
		}

		private boolean executeShareItemTest(TL tl, ModelRequestContext mrcSender, ModelConfiguration confReceiver, ShareableItem item, AgentItem agent, ProfileItem senderProfile) throws InterruptedException {
			JSONResponseContainer myResponse = new JSONResponseContainer();
			boolean isPlausible = false;
			String remark = "";
			long startTime = System.nanoTime();
			try {
				ModelHelper.shareItemToAgent(mrcSender, (GenItem) item, agent, senderProfile);
			} catch (SharingNotSupportedForSAIDException e) {
				e.printStackTrace();
			}
			long endTime = System.nanoTime();
			synchronized (createItemLock) {
				while(endTime + 2000000 > System.nanoTime()){
					createItemLock.wait();
					Logger.getLogger(RestApiTest.class.getName()).log(Level.SEVERE, "test2", "test2");
				}
			}
			TYPES type;
			switch (tl) {
			case T4_SHARE_LIVEPOST_A_TO_B:
				type = TYPES.LIVEPOST;
				break;
			case T7_SHARE_DATABOX_WITH_ASSIGNED_RESSOURCE_A_TO_B:
				type = TYPES.DATABOX;
				// FIXME also get ressources!!!!!
				break;
			case T14_SHARE_LIVEPOST_B_TO_A_VIA_GROUP:
				type = TYPES.LIVEPOST;
				break;
			default:
				type = TYPES.RESOURCE;
				break;
			}
			Vector<GenItem> itemsAtReceiver = RestApiAccess.getItemsOfAllOwners(confReceiver.mainSAID, type, myResponse, confReceiver.restApiConfiguration);
			if (myResponse.jsonResponse.callFailed || itemsAtReceiver == null) {
				remark = "could not get items on receiver side!";
			} else if (itemsAtReceiver.size() == 0) {
				remark = "no item at receiver created!";
			} else {
				remark = itemsAtReceiver.size() + " items in list (";
				for (GenItem genItem : itemsAtReceiver) {
					if (((DisplayableItem) genItem).getName().equals(((DisplayableItem) item).getName())) {
						isPlausible = true;
					}
					remark += ((DisplayableItem) genItem).getName() + ", ";
				}
				remark = remark.substring(0, remark.length() - 2);
				remark += ")!";
			}
			TestResult testResult = new TestResult(isPlausible, remark, endTime - startTime);
			TestReference testReference = new TestReference(mrcSender.hoster, mrcSender.owner, tl, item.getMType(), CALLTYPES.AT_ITEM_POST_UPDATE);
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

	}

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		synchronized (createItemLock) {
			createItemLock.notify();
		}
	}

}
