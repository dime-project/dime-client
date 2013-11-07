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

import eu.dime.model.CreateItemFailedException;
import eu.dime.model.GenItem;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.ItemFactory;
import eu.dime.model.LoadingAbortedRuntimeException;
import eu.dime.model.Model;
import eu.dime.model.ModelConfiguration;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.StaticTestData;
import eu.dime.model.TYPES;
import eu.dime.model.acl.ACLHelper;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.advisory.AdvisoryItem;
import eu.dime.model.storage.InitStorageFailedException;
import eu.dime.restapi.DimeHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.json.JSONPathAccessException;
import sit.tools.ExceptionHelper;

/**
 *
 * @author simon
 */
public class DatabaseAccess {

    static final DimeHelper dimeHelper = new DimeHelper();
    
    private static final Random rnd = new Random();

    public DatabaseAccess() {
    }

    public static void init(boolean persistence) throws InitStorageFailedException {
        Model.getInstance().updateSettings(new ModelConfiguration(DimeHelper.DEFAULT_HOSTNAME, DimeHelper.DEFAULT_PORT, false,
                StaticTestData.DEFAULT_MAIN_SAID, StaticTestData.JUAN_USERNAME, StaticTestData.JUAN_PASSWORD, persistence, false, false));
        new Thread(PSNotificationsManager.getInstance()).start();
    }

    public static JSONObject getAllJSONItems(ModelRequestContext mrContext, TYPES type) {
        List<GenItem> result = Model.getInstance().getAllItems(mrContext, type);
        if (result == null) { //no results found return empty list
            result = new ArrayList<GenItem>();
        }
        return dimeHelper.packResponse(result);
    }

    public static JSONObject getAllAllJSONItems(ModelRequestContext mrContext, TYPES type) {

        List<GenItem> result = Model.getInstance().getAllAllItems(mrContext, type);
        if (result == null) { //no results found return empty list
            result = new ArrayList<GenItem>();
        }

        return dimeHelper.packResponse(result);
    }

    public static JSONObject getJSONItem(ModelRequestContext mrContext, TYPES type, String guid) {
        GenItem result = Model.getInstance().getItem(mrContext, type, guid);
        if (result == null) {
            Logger.getLogger(DatabaseAccess.class.getName()).log(Level.WARNING, "unable to retrieve item "
                    + guid + " (" + ModelHelper.getNameOfType(type) + ") - Item not in the storage!");
            return null;
        }
        return dimeHelper.packResponse(result);
    }

    public static JSONObject createItem(ModelRequestContext mrContext, TYPES type, JSONObject request) throws CreateItemFailedException {
        String errorMessage = null;
        try {
            JSONObject newObj = dimeHelper.getFirstEntryOfRequest(request);
            GenItem item = ItemFactory.createNewItemByJSON(UUID.randomUUID().toString(), type, newObj);
            item = Model.getInstance().createItem(mrContext, item);
            sendNotification(mrContext, NotificationItem.OPERATION_CREATE, item);
            return dimeHelper.packResponse(item);
        } catch (InvalidJSONItemException ex) {
            Logger.getLogger(DatabaseAccess.class.getName()).log(Level.SEVERE, null, ex);
            errorMessage = ExceptionHelper.stackTraceToString(ex, "Server Error");
        }
        return dimeHelper.createAccessErrorResponse(type, "undefined", errorMessage);
    }

    /**
     * overrides the GUID set in the json with the guid given as parameter
     *
     * @param mrContext
     * @param type
     * @param guid
     * @param request
     * @return
     */
    public static JSONObject updateItem(ModelRequestContext mrContext, TYPES type, String guid, JSONObject request) {
        String errorMessage = null;
        try {
            JSONObject newObj = dimeHelper.getFirstEntryOfRequest(request);
            GenItem item = ItemFactory.createNewItemByJSON(type, newObj);
            item.readJSONObject(newObj, guid); //redundant call?!?
            if (item.getMType() == type) {
                GenItem oldItem = null;
                try {
                    oldItem = Model.getInstance().getItem(mrContext, item.getMType(), item.getGuid());
                } catch (LoadingAbortedRuntimeException ex) {
                    //item is not existing or loading failed for some reason
                    Logger.getLogger(DatabaseAccess.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
                }
                Model.getInstance().updateItem(mrContext, item);
                //check for (ACL) changes against existing item
                checkSharingAndTriggerSharingOperation(mrContext, oldItem, item);
                sendNotification(mrContext, NotificationItem.OPERATION_UPDATE, item);
                return dimeHelper.packResponse(item);
            }//else
            errorMessage = "Type missmatch: " + ModelHelper.getNameOfType(type) + " <->" + ModelHelper.getNameOfType(item.getMType());
        } catch (InvalidJSONItemException ex) {
            Logger.getLogger(DatabaseAccess.class.getName()).log(Level.SEVERE, null, ex);
            errorMessage = ExceptionHelper.stackTraceToString(ex, "Server Error");
        }
        return dimeHelper.createAccessErrorResponse(type, "undefined", errorMessage);
    }

    public static JSONObject removeItem(ModelRequestContext mrContext, TYPES type, String guid) {
        GenItem result = Model.getInstance().removeItem(mrContext, guid, type);
        //don't create notifications on deleting notifications!!!
        if (type != TYPES.NOTIFICATION) {
            sendNotification(mrContext, NotificationItem.OPERATION_DELETE, result);
        }
        return dimeHelper.packResponse(result);
    }

    public static void shutdownStorage() {
        PSNotificationsManager.getInstance().stop();
        Model.getInstance().shutdownStorage();
    }

    public static void sendNotification(ModelRequestContext mrContext, String operation, GenItem item) {
        NotificationItem notification = (NotificationItem) ItemFactory.createNewItemByType(UUID.randomUUID().toString(), TYPES.NOTIFICATION);
        String userId = Model.ME_OWNER;
        try {
            //try a cast 
            DisplayableItem dItem = (DisplayableItem) item;
            userId = dItem.getUserId();
        } catch (ClassCastException ex) {
            //we don't have an displayble item
        }
        notification.setOperation(operation);
        notification.setElement(item.getGuid(), item.getType(), userId);
        PSNotificationsManager.getInstance().addNotification(mrContext.hoster, notification);
    }

    public static String getAdvisories(ModelRequestContext mrContext, JSONObject advRequest) {
        try {
            //TODO currently fake implementation
            ArrayList<JSONObject> result = new ArrayList<JSONObject>();
            for (String wType: AdvisoryItem.WARNING_TYPES) {
                AdvisoryItem aItem = ItemFactory.createNewAdvisoryItem(rnd.nextFloat(), wType);
                result.add(aItem.createJSONObject());
            }
            return dimeHelper.packResponseOfJSON(result).toJson();
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(DatabaseAccess.class.getName()).log(Level.SEVERE, null, ex);
            return dimeHelper.createAccessErrorResponse("advisoryrequest", "undefined", ex.getMessage()).toJson();
        }
    }

    public static String getAllSharedItemsForAgent(ModelRequestContext mrContext, TYPES type, String agentGUID) {
        List<GenItem> result = ModelHelper.getItemsDirectlySharedToAgent(mrContext, agentGUID, type);
        return dimeHelper.packResponse(result).toJson();
    }

    private static void checkSharingAndTriggerSharingOperation(ModelRequestContext mrContext, GenItem oldItem, GenItem newItem) {
        if (oldItem == null) {
            return; //nothing to be done
        }
        if (oldItem.getAccessingAgents().equals(newItem.getAccessingAgents())) {
            return; //nothing changed
        }
        DataBaseACLDiffDelegate diffProc = new DataBaseACLDiffDelegate(mrContext, newItem);
        ACLHelper.diffACL(oldItem.getAccessingAgents(), newItem.getAccessingAgents(), diffProc);
    }
}
