package eu.dime.restapi;

import eu.dime.model.specialitem.AuthItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.UserItem;
import eu.dime.model.CALLTYPES;
import eu.dime.model.GenItem;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.ItemFactory;
import eu.dime.model.ModelConfiguration;
import eu.dime.model.specialitem.MergeItem;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.specialitem.SearchResultItem;
import eu.dime.model.specialitem.advisory.AdvisoryItem;
import eu.dime.model.specialitem.advisory.AdvisoryRequestItem;
import eu.dime.model.TYPES;
import eu.dime.model.storage.DimeHosterStorage;
import eu.dime.model.storage.DimeMemory;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.json.JSONPathAccessException;
import sit.web.client.HttpHelper;

public class RestApiAccess {

    /**
     * avoid to create an object
     */
    private RestApiAccess() {
    }
    private static DimeHelper dimeHelper = new DimeHelper();

    private static GenItem jsonToGenItem(TYPES type, JSONObject jsonObject) {
        try {
            GenItem result = ItemFactory.createNewItemByJSON(type, jsonObject);
            return result;

        } catch (InvalidJSONItemException ex) {
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.INFO, "json:\n" + jsonObject.toString());
        }
        return null;
    }

    public static GenItem getItem(String hoster, String owner, TYPES type, String guid, RestApiConfiguration conf) {
        return getItem(hoster, owner, type, guid, new JSONResponseContainer(), conf);
    }

    protected static GenItem getItem(String hoster, String owner, TYPES type, String guid, JSONResponseContainer resultContainer, RestApiConfiguration conf) {
        if (guid == null) {
            return null;
        }
        JSONResponse result = dimeHelper.doDIMEJSONGET(ModelHelper.getPath(CALLTYPES.AT_ITEM_GET, hoster, owner, type, guid), "", conf);
        result = handleResponse(result);
        resultContainer.jsonResponse = result; //save a copy (ref) for testing purposes
        if (resultIsValid(result)) {
            return jsonToGenItem(type, result.replyObjects.get(0));
        }
        return null;
    }

    public static DimeHosterStorage getDump(String hoster, RestApiConfiguration conf) {
        try {
            return getDump(hoster, new JSONResponseContainer(), conf);
        } catch (Exception ex) {
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return new DimeHosterStorage();
    }

    public static DimeHosterStorage getDump(String hoster, JSONResponseContainer resultContainer, RestApiConfiguration conf) {
        DimeHosterStorage result = new DimeHosterStorage();
        JSONResponse response = null;
        response = (dimeHelper.doDIMEJSONGET(ModelHelper.getPath(CALLTYPES.DUMP, hoster, Model.ME_OWNER, null, ""), "", conf));
        response = handleResponse(response);
        resultContainer.jsonResponse = response; //save a copy (ref) for testing purposes
        if ((response == null) || (response.hasError())) {
            //we have not been able to fetch something
            return result;
        }
        JSONObject dump = response.replyObjects.get(0);
        for (Entry<String, JSONObject> ownerStorageJSON : dump) { // in the reply objects we have the DimeMemories of the various owners
            DimeMemory ownerStorage = new DimeMemory(hoster, ownerStorageJSON.getKey(), conf);
            result.put(ownerStorageJSON.getKey(), ownerStorage);
            for (TYPES type : TYPES.values()) {
                try {
                    JSONObject typeJSON = ownerStorageJSON.getValue().getChild(ModelHelper.getNameOfType(type));

                    for (JSONObject jsonObject : typeJSON.getItems()) {
                        GenItem item = jsonToGenItem(type, jsonObject);
                        if (item != null) {
                            ownerStorage.updateItem(item); //TODO this is bad, in case the model is accessing a remote source, since this will cause an update of all items in the model!!!!
                        }
                    }
                } catch (JSONPathAccessException ex) {
                    Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
        return result;
    }

    public static Vector<GenItem> getAllItems(String hoster, String owner, TYPES type, RestApiConfiguration conf) {
        return getAllItems(hoster, owner, type, new JSONResponseContainer(), conf);
    }

    protected static Vector<GenItem> getAllItems(String hoster, String owner, TYPES type, JSONResponseContainer resultContainer, RestApiConfiguration conf) {
        Vector<GenItem> result = new Vector<GenItem>();
        JSONResponse response = null;
        response = (dimeHelper.doDIMEJSONGET(ModelHelper.getPath(CALLTYPES.AT_ALL_GET, hoster, owner, type, null), "", conf));
        response = handleResponse(response);
        resultContainer.jsonResponse = response; //save a copy (ref) for testing purposes
        if ((response == null) || (response.hasError())) {
            //we have not been able to fetch something
            return null;
        }
        for (JSONObject jsonObject : response.replyObjects) {
            GenItem item = jsonToGenItem(type, jsonObject);
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }
    
    protected static Vector<GenItem> getItemsOfAllOwners(String hoster, TYPES type, RestApiConfiguration conf) {
    	return getItemsOfAllOwners(hoster, type, new JSONResponseContainer(), conf);
    }
    
    protected static Vector<GenItem> getItemsOfAllOwners(String hoster, TYPES type, JSONResponseContainer resultContainer, RestApiConfiguration conf) {
        Vector<GenItem> result = new Vector<GenItem>();
        JSONResponse response = null;
        response = (dimeHelper.doDIMEJSONGET(ModelHelper.getPath(CALLTYPES.AT_GLOBAL_ALL_GET, hoster, "", type, null), "", conf));
        response = handleResponse(response);
        resultContainer.jsonResponse = response; //save a copy (ref) for testing purposes
        if ((response == null) || (response.hasError())) {
            //we have not been able to fetch something
            return null;
        }
        for (JSONObject jsonObject : response.replyObjects) {
            GenItem item = jsonToGenItem(type, jsonObject);
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }

    public static GenItem postItemUpdate(String hoster, String owner, TYPES type, GenItem item, RestApiConfiguration conf) {
        return postItemUpdate(hoster, owner, type, item, new JSONResponseContainer(), conf);
    }

    protected static GenItem postItemUpdate(String hoster, String owner, TYPES type, GenItem item, JSONResponseContainer resultContainer, RestApiConfiguration conf) {
        if (item == null) {
            return null;
        }
        String payload;
        try {
            payload = dimeHelper.getEnvelopeForPayload(DimeEnvelope.ENVELOPE_TYPE.REQUEST, item.getJSONObject()).toJson();
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
        JSONResponse result = dimeHelper.doDIMEJSONPOST(ModelHelper.getPath(CALLTYPES.AT_ITEM_POST_UPDATE, hoster, owner, type, item.getGuid()), payload, conf);
        result = handleResponse(result);
        resultContainer.jsonResponse = result; //save a copy (ref) for testing purposes
        if ((result != null) && (!result.hasError()) && (!result.replyObjects.isEmpty())) {
            return jsonToGenItem(type, result.replyObjects.get(0));
        }
        return null;
    }

    public static GenItem postItemNew(String hoster, String owner, TYPES type, GenItem item, RestApiConfiguration conf) {
        return postItemNew(hoster, owner, type, item, new JSONResponseContainer(), conf);
    }

    protected static GenItem postItemNew(String hoster, String owner, TYPES type, GenItem item, JSONResponseContainer resultContainer, RestApiConfiguration conf) {
        if (item == null) {
            return null;
        }
        String payload;
        try {
            payload = dimeHelper.getEnvelopeForPayload(DimeEnvelope.ENVELOPE_TYPE.REQUEST, item.getJSONObject()).toJson();
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
        JSONResponse result = dimeHelper.doDIMEJSONPOST(ModelHelper.getPath(CALLTYPES.AT_ITEM_POST_NEW, hoster, owner, type, ""), payload, conf);
        result = handleResponse(result);
        resultContainer.jsonResponse = result; //save a copy (ref) for testing purposes
        if ((result != null) && (!result.hasError()) && (!result.replyObjects.isEmpty())) {
            return jsonToGenItem(type, result.replyObjects.get(0));
        }
        Logger.getLogger(RestApiAccess.class.getName()).log(Level.WARNING, "POST create for item" + item.getGuid() + " (" + item.getType() + ") failed!");
        return null;
    }

    public static GenItem removeItem(String hoster, String owner, TYPES type, String guid, RestApiConfiguration conf) {
        return removeItem(hoster, owner, type, guid, new JSONResponseContainer(), conf);
    }

    protected static GenItem removeItem(String hoster, String owner, TYPES type, String guid, JSONResponseContainer resultContainer, RestApiConfiguration conf) {
        JSONResponse result = dimeHelper.doDIMEJSONDELETE(ModelHelper.getPath(CALLTYPES.AT_ITEM_DELETE, hoster, owner, type, guid), conf);
        result = handleResponse(result);
        resultContainer.jsonResponse = result; //save a copy (ref) for testing purposes
        if ((result != null) && (!result.hasError()) && (result.replyObjects != null) && (!result.replyObjects.isEmpty())) {
            return jsonToGenItem(type, result.replyObjects.get(0));
        }
        return null;
    }

    public static List<NotificationItem> getCometCall(String hoster, RestApiConfiguration conf) {
        return getCometCall(hoster, new JSONResponseContainer(), conf);
    }

    public static List<NotificationItem> getCometCall(String hoster, JSONResponseContainer resultContainer, RestApiConfiguration conf) {
        return getCometCall(hoster, resultContainer, conf, DimeHelper.clientId);
    }

    public static List<NotificationItem> getCometCall(String hoster, RestApiConfiguration conf, String clientId) {
        return getCometCall(hoster, new JSONResponseContainer(), conf, clientId);
    }

    public static List<NotificationItem> getCometCall(String hoster, JSONResponseContainer resultContainer, RestApiConfiguration conf, String clientId) {
        List<NotificationItem> result = new Vector<NotificationItem>();
        String subPath = ModelHelper.getPath(CALLTYPES.COMET, hoster, "", null, clientId);
        JSONResponse jResult = dimeHelper.doDIMEJSONGET(subPath, "", conf);
        jResult = handleResponse(jResult);
        resultContainer.jsonResponse = jResult; //save a copy (ref) for testing purposes
        if ((result != null) && (!jResult.hasError()) && (!jResult.replyObjects.isEmpty())) {
            try {
                for (JSONObject jsonO : jResult.replyObjects) {
                    result.add((NotificationItem) jsonToGenItem(TYPES.NOTIFICATION, jsonO));
                }
            } catch (ClassCastException ex) {
                Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            } catch (NullPointerException ex) {
                Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return result;
    }

    public static GenItem postItemMerge(String hoster, String owner, TYPES type, MergeItem mergeItem, RestApiConfiguration conf) {
        String payload;
        try {
            payload = dimeHelper.getEnvelopeForPayload(DimeEnvelope.ENVELOPE_TYPE.REQUEST, mergeItem.createJSONObject()).toJson();
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
        JSONResponse result = dimeHelper.doDIMEJSONPOST(ModelHelper.getPath(CALLTYPES.MERGE, hoster, owner, type, null), payload, conf);
        result = handleResponse(result);
        if ((result != null) && (!result.hasError()) && (!result.replyObjects.isEmpty())) {
            return jsonToGenItem(type, result.replyObjects.get(0));
        }
        return null;
    }

    public static AuthItem getAuthItem(String hoster, RestApiConfiguration conf) {
        JSONResponse response = null;
        response = (dimeHelper.doDIMEJSONGET(ModelHelper.getPath(CALLTYPES.AUTH_GET, hoster, null, null, null), "", conf));
        response = handleResponse(response);
        if ((response == null) || (response.hasError())) {
            //we have not been able to fetch something
            return null;
        }
        return ItemFactory.createNewAuthItem(response.replyObjects.get(0));
    }

    public static boolean postAuthItem(String hoster, AuthItem item, RestApiConfiguration conf) {
        String payload;
        try {
            payload = dimeHelper.getEnvelopeForPayload(DimeEnvelope.ENVELOPE_TYPE.REQUEST, item.createJSONObject()).toJson();
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
        JSONResponse result = dimeHelper.doDIMEJSONPOST(ModelHelper.getPath(CALLTYPES.AUTH_POST, hoster, null, null, item.getUsername()), payload, conf);
        result.parseJSONResponse();
        return !result.isErrorMessage;
    }

    public static List<String> getAllHosters(RestApiConfiguration conf) {
        Vector<String> result = new Vector<String>();
        JSONResponse response = null;
        response = (dimeHelper.doDIMEJSONGET(DimeHelper.DIME_ADMIN_PATH + "@all", "", conf));
        response = handleResponse(response);
        if ((response == null) || (response.hasError())) {
            //we have not been able to fetch something
            return result;
        }
        for (JSONObject jsonObject : response.replyObjects) {
            result.add(jsonObject.getValue());
        }
        return result;
    }

    public static List<SearchResultItem> searchGlobal(String hoster, String query, RestApiConfiguration conf) {
        Vector<SearchResultItem> result = new Vector<SearchResultItem>();
        JSONResponse response = null;
        response = (dimeHelper.doDIMEJSONGET(DimeHelper.DIME_BASIC_PATH
                + HttpHelper.encodeString(hoster)
                + "/search?query="
                + HttpHelper.encodeString(query), "", conf));
        response = handleResponse(response);
        if ((response == null) || (response.hasError())) {
            //we have not been able to fetch something
            return null;
        }
        for (JSONObject jsonObject : response.replyObjects) {
            result.add(ItemFactory.createNewSearchResultItem(jsonObject));
        }
        return result;
    }

    public static boolean addPublicPerson(String hoster, SearchResultItem contact, RestApiConfiguration conf) {
        String payload;
        try {
            payload = dimeHelper.getEnvelopeForPayload(DimeEnvelope.ENVELOPE_TYPE.REQUEST, contact.createJSONObject()).toJson();
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        JSONResponse response = (dimeHelper.doDIMEJSONPOST(DimeHelper.DIME_BASIC_PATH
                + HttpHelper.encodeString(hoster)
                + "/person/addcontact", payload, conf));
        response.parseJSONResponse();
        return !response.isErrorMessage;
    }

    public static List<AdvisoryItem> requestSharingAdvisories(String hoster, AdvisoryRequestItem ari, RestApiConfiguration conf) {
        Vector<AdvisoryItem> result = new Vector<AdvisoryItem>();
        String payload;
        try {
            payload = dimeHelper.getEnvelopeForPayload(DimeEnvelope.ENVELOPE_TYPE.REQUEST, ari.createJSONObject()).toJson();
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        JSONResponse response = (dimeHelper.doDIMEJSONPOST(DimeHelper.DIME_BASIC_PATH
                + HttpHelper.encodeString(hoster)
                + DimeHelper.ADVISORY_REQUEST_SUBPATH, payload, conf));
        response.parseJSONResponse();
        if ((response == null) || (response.hasError())) {
            //we have not been able to fetch something
            return null;
        }
        for (JSONObject jsonObject : response.replyObjects) {
            result.add(ItemFactory.createNewAdvisoryItem(jsonObject));
        }
        return result;
    }

    public static ModelConfiguration registerNewUserCall(UserItem user, String hostname, int port, boolean useHTTPS) {
    	ModelConfiguration conf = new ModelConfiguration(hostname, port, useHTTPS, user.getUsername(), user.getUsername(), user.getPassword(), false, true, true);
        String payload;
        try {
            payload = dimeHelper.getEnvelopeForPayload(DimeEnvelope.ENVELOPE_TYPE.REQUEST, user.createJSONObject()).toJson();
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        Logger.getLogger(RestApiAccess.class.getName()).log(Level.INFO, payload);
        JSONResponse response = (dimeHelper.doDIMEJSONPOST(DimeHelper.USER_REGISTRATION_ENDPOINT, payload, conf.restApiConfiguration));
        response.parseJSONResponse();
        if ((response != null) && !(response.hasError())) {
            try {
        		UserItem userItem = new UserItem();
        		userItem.readJSONObject(response.replyObjects.firstElement());
				if(!userItem.getUsername().equals(user.getUsername())) {
					throw new InvalidJSONItemException("");
				}
            } catch (InvalidJSONItemException e) {
            	Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, null, e);
                conf = null;
			}
        }
        return conf;
    }

    protected static JSONResponse handleResponse(JSONResponse response) {
        if (response == null) {
            return null;
        }
        response.parseJSONResponse();
        if (response.hasError()) {
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.SEVERE, "Error:\n" + response.toString());
        }
        return response;
    }

    private static boolean resultIsValid(JSONResponse result) {
        if ((result != null) && (!result.hasError()) && (!result.replyObjects.isEmpty())) {
            return true;
        }
        if (result == null) {
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.WARNING, "response is null:\n");
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.FINE, "response is null:\n");
            return false;
        }
        if (result.hasError()) {
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.WARNING, "response has error:\n" + result.toShortString());
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.FINE, result.toString());
            return false;
        }
        if (result.replyObjects.isEmpty()) {
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.WARNING, "response contains empty entry field:\n" + result.toShortString());
            Logger.getLogger(RestApiAccess.class.getName()).log(Level.FINE, result.toString());
        }
        return false;
    }
}
