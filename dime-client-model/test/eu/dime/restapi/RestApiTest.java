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

import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.model.CALLTYPES;
import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.ItemGenerator;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.ModelTypeNotFoundException;
import eu.dime.model.StaticTestData;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;
import eu.dime.restapi.TestCollectionRestApi.TestReference;
import eu.dime.restapi.TestCollectionRestApi.TestResult;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.io.FileHelper;
import sit.json.JSONObject;
import sit.web.MimeTypes;
import sit.web.client.HttpHelper;

/**
 *
 * @author simon
 */
public class RestApiTest {

    private List<NotificationItem> notifications = new ArrayList<NotificationItem>();
    private enum OutputType {WIKI, CSV}
    private List<TestWithNotification> testsWithNotifications = new ArrayList<TestWithNotification>();
    private TestCollectionRestApi testCollection = null;
    private ItemGenerator itemGenerator = new ItemGenerator();
    public final static int SERVER_OFFLINE = -1;
    public final static int TEST_CRASHED = Integer.MAX_VALUE;
    public Boolean dimeServerAvailable;
    private final RestApiConfiguration conf;
    private static DimeHelper dimeHelper = new DimeHelper();
    
    /**
     * test labels defining the order and names of the tests
     *
     * TODO add tests: * get
     *
     * @all after create -> check new item inserted
     *
     * @share action
     * @merge action
     */
    public enum TL {
    	T0_GET_DUMP,
        T1_GET_ALL, 
        T2_GET_ITEM_FIRST_ITEM, 
        T3_UPDATE_ITEM_FIRST_ITEM, 
        T4_CREATE_ITEM_NEW_ITEM, 
        T5_GET_ITEM_NEW_ITEM,
        T6_UPDATE_ITEM_NEW_ITEM, 
        T7_DELTE_ITEM_NEW_ITEM;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    };
    
    public RestApiTest(RestApiConfiguration conf, boolean firstCheckServerIsOnline) {
        testCollection = new TestCollectionRestApi();
        if (firstCheckServerIsOnline) {
            this.dimeServerAvailable = null;
        } else {
            this.dimeServerAvailable = true;
        }
        this.conf = conf;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //TODO: integrate hoster and owner into command line arguments
        String hoster = StaticTestData.DEFAULT_MAIN_SAID;
        //String hoster = "9702325";
        String owner = Model.ME_OWNER;
        RestApiConfiguration conf = new RestApiConfiguration(DimeHelper.DEFAULT_HOSTNAME, 8080, false, DimeHelper.DIME_OWNER_AUTH_TOKEN);
        Logger.getLogger(RestApiTest.class.getName()).log(Level.INFO, "initating test ... ");
        for (String param : args) {
            Logger.getLogger(RestApiTest.class.getName()).log(Level.INFO, param);
            if (param.equals("-v")) {
                continue; //verboseLevel = 1;
            }
            if (param.equals("-vv")) {
                continue; //verboseLevel = 2;
            }
            if (param.equals("--sendCometPost")) {
                new RestApiTest(conf, false).sendCometPost();
                return;
            } else if (param.equals("--simpleTest")) {
                Logger.getLogger(RestApiTest.class.getName()).log(Level.INFO, "Test resulted with " + new RestApiTest(conf, false).doSimpleTest(hoster, owner) + " error(s)!");
                return;
            } else if (param.equals("--help")) {
                Logger.getLogger(RestApiTest.class.getName()).log(Level.INFO, "\n\nRestAPITest\n\n");
                return;
            } else {
                Logger.getLogger(RestApiTest.class.getName()).log(Level.INFO, "Test for " + param + " resulted with "
                        + new RestApiTest(conf, false).doTest(hoster, owner, ModelHelper.lookupType(param)) + " error(s)!");
                return;
            }
        }
        Logger.getLogger(RestApiTest.class.getName()).log(Level.INFO, "Test resulted with " + new RestApiTest(conf, false).doFullTest(hoster, owner) + " error(s)!");
    }
    
    public int doSimpleTest(String hoster, String owner) {
        if (!checkDimeServerAvailable(hoster)) {
            Logger.getLogger(RestApiTest.class.getName()).log(Level.WARNING, "RestApiTest skipped - di.me Server is offline !!! (ERROR)");
            return SERVER_OFFLINE;
        }
        int errors = 0;
        errors += doTest(hoster, owner, TYPES.PERSON);
        errors += doTest(hoster, owner, TYPES.GROUP);
        return errors;
    }

    public int doFullTest(String hoster, String owner) {
    	OutputType ot = OutputType.CSV;
        if (!checkDimeServerAvailable(hoster)) {
            Logger.getLogger(RestApiTest.class.getName()).log(Level.WARNING, "RestApiTest skipped - di.me Server is offline !!! (ERROR)");
            return SERVER_OFFLINE;
        }
        testsWithNotifications.clear();
        notifications.clear();
        int errors = 0;
        for (TYPES type : TYPES.values()) {
            errors += doTest(hoster, owner, type);
        }
        try {
            Thread.sleep(5000);
            Map<TestReference, TestResult> tests = testCollection.getTestCollection();
            for (TestWithNotification twn : testsWithNotifications) {
                TestResult testResult = tests.get(twn.getTestReference());
                String result = "false";
                if (notifications.size() > 0) {
                    for (NotificationItem nItem : notifications) {
                        if (nItem.getElement().getGuid().equals(twn.getGuid())
                                && ((nItem.getOperation().equalsIgnoreCase(NotificationItem.OPERATION_CREATE) && twn.getTestReference().callType.equals(CALLTYPES.AT_ITEM_POST_NEW))
                                || (nItem.getOperation().equalsIgnoreCase(NotificationItem.OPERATION_UPDATE) && twn.getTestReference().callType.equals(CALLTYPES.AT_ITEM_POST_UPDATE))
                                || (nItem.getOperation().equalsIgnoreCase(NotificationItem.OPERATION_DELETE) && twn.getTestReference().callType.equals(CALLTYPES.AT_ITEM_DELETE)))) {
                            result = "true";
                            break;
                        }
                    }
                }
                testResult.setNotificationSent(result);
            }
            Logger.getLogger(RestApiTest.class.getName()).log(Level.INFO, "NUMBER of NEW notifications: " + notifications.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        deliverEvaluation(ot);
        return errors;
    }
    
    /**
     * calls a sequence of tests with the various calls for the given type as
     * defined in TL (test label)
     *
     *
     * @param hoster
     * @param owner
     * @param type
     * @return
     * @throws DimeServerNotOnlineException
     */
    public int doTest(String hoster, String owner, TYPES type) {
        try {
            return executeTest(hoster, owner, type);
        } catch (DimeServerNotOnlineException ex) {
            return SERVER_OFFLINE;
        } catch (Exception ex) {
            Logger.getLogger(RestApiTest.class.getName()).log(Level.SEVERE, null, ex);
            return TEST_CRASHED;
        }
    }

	private int executeTest(String hoster, String owner, TYPES type) throws DimeServerNotOnlineException, GetAllCrashedException {
        if (!checkDimeServerAvailable(hoster)) throw new DimeServerNotOnlineException();
        int errors = 0;
        Vector<GenItem> allItems = null;
        switch (type) {
        case LIVESTREAM: case DEVICE: case NOTIFICATION: //not supported at the moment
			break;
        case EVALUATION:
        	if(executeSingleTest(TL.T4_CREATE_ITEM_NEW_ITEM, hoster, owner, type, ItemFactory.createNewEvaluationItem("0.9", "not set", "not set", "not set", Arrays.asList("not set")), allItems)) errors++; //just do POST call
			break;
        case CONTEXT:
			if(executeSingleTest(TL.T4_CREATE_ITEM_NEW_ITEM, hoster, owner, type, ItemFactory.createNewItemByType(type), allItems)) errors++; //just do POST call
			break;
		case PLACE: case ACTIVITY: case SERVICEADAPTER:
			if(executeSingleTest(TL.T1_GET_ALL, hoster, owner, type, null, allItems)) errors++;
			break;
		case ACCOUNT: case USERNOTIFICATION: case SITUATION:
			if(executeSingleTest(TL.T1_GET_ALL, hoster, owner, type, null, allItems)) {
        		throw new GetAllCrashedException();
        	} else {
        		allItems = RestApiAccess.getAllItems(hoster, owner, type, conf);
        	    if(allItems.size() > 0) {
        	    	if(executeSingleTest(TL.T3_UPDATE_ITEM_FIRST_ITEM, hoster, owner, type, allItems.get(0), allItems)) errors++;
        	    }
        	}
			break;
		default:
			if(executeSingleTest(TL.T1_GET_ALL, hoster, owner, type, null, null)) {
        		throw new GetAllCrashedException();
        	} else {
        		allItems = RestApiAccess.getAllItems(hoster, owner, type, conf);
        		if(allItems.isEmpty()) {
        			//skip T2 and T3 since there are referencing to the first item in list
        		} else {
        			GenItem firstItem = allItems.get(0);
        			if(executeSingleTest(TL.T2_GET_ITEM_FIRST_ITEM, hoster, owner, type, firstItem, allItems)) errors++;
        			if(executeSingleTest(TL.T3_UPDATE_ITEM_FIRST_ITEM, hoster, owner, type, firstItem, allItems)) errors++;
        		}
        		GenItem newItem;
        		GenItem resultItem;
        	    if (ModelHelper.isDisplayableItem(type)) {
        	        newItem = itemGenerator.generateRandomItem(new ModelRequestContext(hoster, owner, new DummyLoadingViewHandler()), type);
        	    } else {
        	        newItem = ItemFactory.createNewItemByType(type);
        	    }
        	    resultItem = executeCreateItemTest(hoster, owner, type, newItem, allItems);
        	    if(resultItem != null) {
	        		allItems = RestApiAccess.getAllItems(hoster, owner, type, conf);
	        		if(executeSingleTest(TL.T5_GET_ITEM_NEW_ITEM, hoster, owner, type, resultItem, allItems)) errors++;
	        		if(executeSingleTest(TL.T6_UPDATE_ITEM_NEW_ITEM, hoster, owner, type, resultItem, allItems)) errors++;
	        		if(executeSingleTest(TL.T7_DELTE_ITEM_NEW_ITEM, hoster, owner, type, resultItem, allItems)) errors++;
        	    } else {
        	    	errors++;
        	    }
        	}
			break;
		}
        return errors;
    }
	
	private GenItem executeCreateItemTest(String hoster, String owner, TYPES type, GenItem item, Vector<GenItem> itemsBeforeCall) {
		GenItem resultItem = null;
		JSONResponseContainer myResponse = new JSONResponseContainer();
		TestResult testResult = new TestResult();
		CALLTYPES callType = null;
		String remark = "";
		long startTime = System.nanoTime();
		resultItem = RestApiAccess.postItemNew(hoster, owner, type, item, myResponse, conf);
		remark = getRemarksForItem(resultItem, owner, type, item, true);
		callType = CALLTYPES.AT_ITEM_POST_NEW;
		long endTime = System.nanoTime();
		testResult.setNeededTime(endTime - startTime);
		testResult.setRemark(remark);
		testResult.setPlausible(checkPlausibilityForSingleTest(hoster, owner, type, callType, itemsBeforeCall, resultItem, item));
		testResult.setResponse(myResponse.jsonResponse);
		TestReference testReference = new TestReference(hoster, owner, TL.T4_CREATE_ITEM_NEW_ITEM, type, callType);
		testsWithNotifications.add(new TestWithNotification(testReference, resultItem.getGuid()));
		testCollection.addResponse(testReference, testResult);
		return resultItem;
	}
	
	private boolean executeSingleTest(TL tl, String hoster, String owner, TYPES type, GenItem item, Vector<GenItem> itemsBeforeCall) {
		GenItem resultItem = null;
		JSONResponseContainer myResponse = new JSONResponseContainer();
		TestResult testResult = new TestResult();
		CALLTYPES callType = null;
		String remark = "";
		long startTime = System.nanoTime();
		switch (tl) {
		case T0_GET_DUMP:
			callType = CALLTYPES.DUMP;
			break;
		case T1_GET_ALL:
			RestApiAccess.getAllItems(hoster, owner, type, myResponse, conf);
			callType = CALLTYPES.AT_ALL_GET;
			break;
		case T2_GET_ITEM_FIRST_ITEM:
			resultItem = RestApiAccess.getItem(hoster, owner, type, item.getGuid(), myResponse, conf);
			remark = getRemarksForItem(resultItem, owner, type);
			callType = CALLTYPES.AT_ITEM_GET;
			break;
		case T3_UPDATE_ITEM_FIRST_ITEM:
			resultItem = RestApiAccess.postItemUpdate(hoster, owner, type, item, myResponse, conf);
	        remark = getRemarksForItem(resultItem, owner, type, item, false);
	        callType = CALLTYPES.AT_ITEM_POST_UPDATE;
			break;
		case T4_CREATE_ITEM_NEW_ITEM:
			resultItem = RestApiAccess.postItemNew(hoster, owner, type, item, myResponse, conf);
			remark = getRemarksForItem(resultItem, owner, type, item, true);
			callType = CALLTYPES.AT_ITEM_POST_NEW;
			break;
		case T5_GET_ITEM_NEW_ITEM:
			resultItem = RestApiAccess.getItem(hoster, owner, type, item.getGuid(), myResponse, conf);
			remark = getRemarksForItem(resultItem, owner, type);
			callType = CALLTYPES.AT_ITEM_GET;
			break;
		case T6_UPDATE_ITEM_NEW_ITEM:
			resultItem = RestApiAccess.postItemUpdate(hoster, owner, type, item, myResponse, conf);
	        remark = getRemarksForItem(resultItem, owner, type, item, false);
			callType = CALLTYPES.AT_ITEM_POST_UPDATE;
			break;
		case T7_DELTE_ITEM_NEW_ITEM:
			resultItem = RestApiAccess.removeItem(hoster, owner, type, item.getGuid(), myResponse, conf);
			callType = CALLTYPES.AT_ITEM_DELETE;
			break;
		}
		long endTime = System.nanoTime();
		testResult.setNeededTime(endTime - startTime);
		testResult.setRemark(remark);
		testResult.setPlausible(checkPlausibilityForSingleTest(hoster, owner, type, callType, itemsBeforeCall, resultItem, item));
		testResult.setResponse(myResponse.jsonResponse);
		TestReference testReference = new TestReference(hoster, owner, tl, type, callType);
		if(callType.equals(CALLTYPES.AT_ITEM_POST_UPDATE) || callType.equals(CALLTYPES.AT_ITEM_POST_NEW)) {
			if(resultItem != null) testsWithNotifications.add(new TestWithNotification(testReference, resultItem.getGuid()));
		} else if(callType.equals(CALLTYPES.AT_ITEM_DELETE)) {
			if(item != null) testsWithNotifications.add(new TestWithNotification(testReference, item.getGuid()));
		}
		testCollection.addResponse(testReference, testResult);
		return ((myResponse.jsonResponse == null) || myResponse.jsonResponse.hasError());
	}

    private boolean checkPlausibilityForSingleTest(String hoster, String owner, TYPES type, CALLTYPES callType, Vector<GenItem> itemsBeforeCall, GenItem newItem, GenItem oldItem) {
    	boolean isPlausible = true;
    	try {
    		switch (callType) {
    		case AT_ITEM_POST_NEW:
    			isPlausible = (RestApiAccess.getAllItems(hoster, owner, type, conf).size() - itemsBeforeCall.size() == 1);
    			break;
    		case AT_ITEM_DELETE:
    			isPlausible = (itemsBeforeCall.size() - RestApiAccess.getAllItems(hoster, owner, type, conf).size() == 1);
    			break;
    		case AT_ITEM_GET:
    			
    			break;
    		case AT_ITEM_POST_UPDATE:
    			
    			break;
    		default:
    			break;
    		}
		} catch (Exception e) {
		}
		return isPlausible;
	}

    public boolean checkDimeServerAvailable(String hoster) {
        if (dimeServerAvailable == null) {
            try {
                dimeServerAvailable = dimeHelper.dimeServerIsAuthenticated(hoster, conf);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dimeServerAvailable;
    }

    private void deliverEvaluation(OutputType type) {
        FileHelper fh = new FileHelper();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        Calendar cal = Calendar.getInstance();
        try {
        	switch (type) {
			case WIKI:
				fh.writeToFile("wiki-restapitest" + dateFormat.format(cal.getTime()) + ".txt", testCollection.toWikiDoc());
				break;
			case CSV:
				fh.writeToFile("result-restapitest"+ dateFormat.format(cal.getTime()) + ".csv", testCollection.toString());
				break;
			default:
				break;
			}
        } catch (IOException ex) {
            Logger.getLogger(RestApiTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Do some more checks on the item
     *
     * @param firstItem
     * @return
     */
    private String getRemarksForItem(GenItem item, String owner, TYPES type) {
        if (item == null) {
            return "item is null!;";
        }
        String result = "";
        if (item.getGuid().length() == 0) {
            result += "guid is missing;";
        }
        try {
            if (ModelHelper.getMTypeFromString(item.getType()) != type) {
                result += "type mismatch: " + item.getType() + "!=" + ModelHelper.getNameOfType(type) + ";";
            }
        } catch (ModelTypeNotFoundException ex) {
            result += ex.getMessage();
        }
        if (ModelHelper.isDisplayableItem(item.getMType())) {
            DisplayableItem dItem = (DisplayableItem) item;
            if (dItem.getName().length() == 0) {
                result += "name is missing; ";
            }
            if (dItem.getImageUrl().length() == 0) {
                result += "imageUrl is missing; ";
            }
            if (!dItem.getUserId().equals(owner)) {
                result += "userId mismatch: " + dItem.getUserId() + "!=" + owner + "; ";
            }
            if (dItem.getLastUpdated() - System.currentTimeMillis() > 5000) {
                result += "lastUpdated not set!; ";
            }
            if (item.getMType() == TYPES.PROFILE) {
                ProfileItem pItem = (ProfileItem) item;
                if (pItem.getServiceAccountId().length() == 0) {
                    result += "serviceAccountId not set!; ";
                }
            }
            if (item.getMType() == TYPES.RESOURCE) {
                ResourceItem rItem = (ResourceItem) item;
                if ((rItem.getName().endsWith("jpg") || rItem.getName().endsWith("jpeg") || rItem.getName().endsWith("png") || rItem.getName().endsWith("bmp")) && rItem.getImageUrl().length() > 0) {
                    result += "imageUrl correct? " + rItem.getDownloadUrl() + "; "; //TODO check correctness of imageUrl
                }
                if (rItem.getDownloadUrl().length() == 0) {
                    result += "downloadUrl not set!; ";
                } else {
                    result += "downloadUrl correct? " + rItem.getDownloadUrl() + "; "; //TODO check correctness of downloadUrl
                }
                if (rItem.getMimeType().length() == 0) {
                    result += "mimeType not set!; ";
                } else if (!MimeTypes.isMimeType(rItem.getMimeType())) {
                    result += "invalid mimeType? " + rItem.getMimeType() + "; ";
                }
            }
            if (item.getMType() == TYPES.PROFILE) {
                ProfileItem pItem = (ProfileItem) item;
                if (pItem.isEditable()) {
                    result += "isEditable not set for " + pItem.getName() + "?; ";
                }
            }
            if (item.getMType() == TYPES.USERNOTIFICATION) {
            	UserNotificationItem nItem = (UserNotificationItem) item;
            	if(nItem.getCreated() == -1){
            		result += "created not set!";
            	}
            }
        }
        return result;
    }

    /**
     * Do some more checks on the item
     *
     * @param firstItem
     * @return
     */
    private String getRemarksForItem(GenItem item, String owner, TYPES type, GenItem compareItem, boolean wasCreateCall) {
        if (item == null) {
            return "item is null!;";
        }
        if (compareItem == null) {
            return "compare item is null!;";
        }
        String result = getRemarksForItem(item, owner, type);
        if (wasCreateCall) {
            if (item.getGuid().equals(compareItem.getGuid())) {
                result += "guid was not changed;";
            }
        } else {
            if (!item.getGuid().equals(compareItem.getGuid())) {
                result += "guid changed;";
            }
        }
        if (ModelHelper.isDisplayableItem(item.getMType())) {
            DisplayableItem dItem = (DisplayableItem) item;
            DisplayableItem dCompareItem = (DisplayableItem) compareItem;
            if (!dItem.getName().equals(dCompareItem.getName())) {
                result += "name changed: " + dCompareItem.getName() + " => " + dItem.getName() + ";";
            }
            if (!dItem.getImageUrl().equals(dCompareItem.getImageUrl())) {
                result += "imageUrl changed: " + dCompareItem.getImageUrl() + " => " + dItem.getImageUrl() + ";";
            }
            if (!dItem.getUserId().equals(dCompareItem.getUserId())) {
                result += "userId changed: " + dCompareItem.getUserId() + " => " + dItem.getUserId() + ";";
            }
            if(!dItem.getItems().equals(dCompareItem.getItems())) {
            	result += "items changed: " + dCompareItem.getItems() + " => " + dItem.getItems() + ";";
            }
            if(!(dItem.getLastUpdated() == dCompareItem.getLastUpdated())) {
            	result += "lastUpdated changed: " + dCompareItem.getLastUpdated() + " => " + dItem.getLastUpdated() + ";";
            }
            if(!dItem.getAccessingAgents().equals(dCompareItem.getAccessingAgents())) {
            	result += "accessingAgents changed: " + dCompareItem.getAccessingAgents() + " => " + dItem.getAccessingAgents() + ";";
            }
        }
        return result;
    }

    private void sendCometPost() {
        try {
            String payload = "message=\"hello world!\"";
            HttpHelper httpHelper = new HttpHelper(DimeHelper.DIME_SSL_CERT_TYPE);
            JSONResponse result = new JSONResponse(httpHelper.doHTTPRequest("POST", conf.hostName, conf.port, "/dime-communications/push/@comet/all", payload, JSONObject.JSON_MIME_TYPE, conf.isHttps, conf.authToken));
            RestApiAccess.handleResponse(result);
        } catch (MalformedURLException ex) {
            Logger.getLogger(RestApiTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(RestApiTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RestApiTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class TestWithNotification {

        private TestReference testReference = null;
        private String guid = "";

        public TestWithNotification(TestReference testReference, String guid) {
            this.testReference = testReference;
            this.guid = guid;
        }

        public String getGuid() {
            return guid;
        }

        public TestReference getTestReference() {
            return testReference;
        }
    }
    
}
