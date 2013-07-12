/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.specialitem.advisory;

import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;

import java.util.Map;

import sit.json.JSONObject;

/**
 *
 * @author christian
 */
public class AdvisoryItem extends JSONItem<AdvisoryItem> { 

    public static final String WARNING_LEVEL = "warningLevel";
    public static final String WARNING_TYPE = "type";
    public static final String ATTRIBUTES = "attributes";
    public static final String[] WARNING_TYPES = new String[]{"untrusted", "disjunct_groups", "unshared_profile", "too_many_resources", "too_many_receivers", "agent_not_valid_for_sharing", "sharing_not_possible"};

    protected double warningLevel;
    protected String warningType;
    protected WarningAttributesObject attributes;

    public AdvisoryItem() {
        wipeItem();
    }

    public AdvisoryItem(double warningLevel, String warningType, WarningAttributesObject attributes) {
        this.warningLevel = warningLevel;
        this.warningType = warningType;
        this.attributes = attributes;
    }

    @Override
    public AdvisoryItem getClone() {
        return new AdvisoryItem(warningLevel, warningType, attributes);
    }

    @Override
    protected final void wipeItem() {
        this.warningLevel = 0.0d;
        this.warningType = "";
        this.attributes = null;
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(this.warningLevel, AdvisoryItem.WARNING_LEVEL));
        result.addChild(getJSONValue(this.warningType, AdvisoryItem.WARNING_TYPE));
        if(attributes != null) { 
        	JSONObject attribute = this.attributes.createJSONObject(); 
	        //keep advisory item flat
	        for (Map.Entry<String, JSONObject> attrChild : attribute) {
	            result.addChild(attrChild.getValue());
	        }
        }
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        // clean up first
        wipeItem();
        // read the json
        this.warningLevel = getDoubleValueOfJSONO(jsonObject, AdvisoryItem.WARNING_LEVEL);
        this.warningType = getStringValueOfJSONO(jsonObject, AdvisoryItem.WARNING_TYPE);
        this.attributes = getNewAttribObjByType(warningType);
        if (this.attributes!=null){
            this.attributes.readJSONObject(jsonObject);
        }
    }

    public WarningAttributesObject getAttributes() {
        return attributes;
    }

    public double getWarningLevel() {
        return warningLevel;
    }

    public String getWarningType() {
        return warningType;
    }
    
    public static WarningAttributesObject getNewAttribObjByType(String myWarningType) {
        if (myWarningType.equals(WARNING_TYPES[0])) {
            return new WarningAttributesUntrusted();
        } else if (myWarningType.equals(WARNING_TYPES[1])) {
            return new WarningAttributesDisjunctGroups();
        } else if (myWarningType.equals(WARNING_TYPES[2])) {
            return new WarningAttributesProfileNotShared();
        } else if (myWarningType.equals(WARNING_TYPES[3])) {
        	return new WarningTooManyResources();
        } else if(myWarningType.equals(WARNING_TYPES[4])) {
        	return new WarningTooManyReceivers();
        } else if (myWarningType.equals(WARNING_TYPES[5])) {
        	return new WarningAgentNotValidForSharing();
        } else if (myWarningType.equals(WARNING_TYPES[6])) {
        	return new WarningSharingNotPossible();
        }
        return null;
    }
    
}
