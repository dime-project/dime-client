/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.specialitem.usernotification;

import eu.dime.model.GenItem;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelTypeNotFoundException;
import eu.dime.model.TYPES;
import sit.json.JSONObject;

/**
 *
 * @author simon
 */
public class UNEntryRefToItem extends UnEntry {

    public static final String GUID = "guid";
    public static final String TYPE = "type";
    public static final String USER_ID = "userId";
    public static final String NAME = "name";
    public static final String OPERATION = "operation";
    public static final String OPERATION_SHARED = "shared";
    public static final String OPERATION_UNSHARED = "unshared";
    public static final String OPERATION_INC_TRUST = "inc_trust";
    public static final String OPERATION_DEC_TRUST = "dec_trust";
    public static final String OPERATION_INC_PRIV = "inc_priv";
    public static final String OPERATION_DEC_PRIV = "dec_priv";
    private String guid;
    private String type;
    private String userId;
    private String name;
    private String operation;


    public UNEntryRefToItem() {
        wipeItem();
    }

    public UNEntryRefToItem(GenItem itemReceiver, String userId, String name, String operation) {
        this.guid = itemReceiver.getGuid();
        this.type = itemReceiver.getType();
        this.userId = userId;
        this.name = name;
        this.operation = operation;
    }

    @Override
    public UnEntry getClone() {
        UNEntryRefToItem entry = new UNEntryRefToItem();
        entry.guid = this.guid;
        entry.type = this.type;
        entry.userId = this.userId;
        entry.name = this.name;
        entry.operation = this.operation;
        return entry;
    }

    @Override
    protected final void wipeItem() {
        guid = "";
        type = "";
        userId = "";
        name = "";
        operation = "";
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(guid, GUID));
        result.addChild(getJSONValue(operation, OPERATION));
        result.addChild(getJSONValue(type, TYPE));
        result.addChild(getJSONValue(userId, USER_ID));
        result.addChild(getJSONValue(name, NAME));
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        this.guid = getStringValueOfJSONO(jsonObject, GUID);
        this.type = getStringValueOfJSONO(jsonObject, TYPE);
        this.userId = getStringValueOfJSONO(jsonObject, USER_ID);
        this.name = getStringValueOfJSONO(jsonObject, NAME);
        this.operation = getStringValueOfJSONO(jsonObject, OPERATION);
    }

    public String getGuid() {
        return guid;
    }

    public TYPES getType() throws ModelTypeNotFoundException {
        return ModelHelper.getMTypeFromString(this.type);
    }
    
    public String getStringType() {
    	return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getOperation() {
        return operation;
    }

    /**
     * @param guid the guid to set
     */
    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * @param type the type to set
     */
    public void setType(TYPES type) {
        this.type = ModelHelper.getStringType(type);
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    @Override
    public UN_TYPE getUnType() {
        return UN_TYPE.REF_TO_ITEM;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
