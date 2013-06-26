/*
 *  Description of PersonItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.05.2012
 */
package eu.dime.model.displayable;

import eu.dime.restapi.DimeHelper;
import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public final class PersonItem extends DisplayableItem implements AgentItem{

    public static final String TRUST_LEVEL_TAG = "nao:trustLevel";
    public static final String DEFAULT_PROFILE_TAG = "defProfile";
    
    private Double trustLevel = DimeHelper.DEFAULT_INITIAL_TRUST_LEVEL;
    private String defaultProfileGuid = "";
    
    public PersonItem() {
        wipeItemForDisplayItem();
    }

    public PersonItem(String guid) {
        super(guid);
        wipeItemForDisplayItem();
    }

    
    
    @Override
    protected final void wipeItemForDisplayItem() {
        trustLevel = DimeHelper.DEFAULT_INITIAL_TRUST_LEVEL;
        defaultProfileGuid = "";
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        PersonItem result = new PersonItem();
        result.trustLevel = this.trustLevel;
        result.defaultProfileGuid = this.defaultProfileGuid;
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
        this.trustLevel = getDoubleValueOfJSONO(jsonObject, TRUST_LEVEL_TAG);
        this.defaultProfileGuid = getStringValueOfJSONO(jsonObject, DEFAULT_PROFILE_TAG);
        
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(trustLevel, TRUST_LEVEL_TAG));
        newJSONObject.addChild(getJSONValue(defaultProfileGuid, DEFAULT_PROFILE_TAG));
        return newJSONObject;
    }

    public void setTrustLevel(double trustLevel) {
        changed=true;
        this.trustLevel = trustLevel;
    }

    public Double getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(Double trustLevel) {
        changed = true;
        this.trustLevel = trustLevel;
    }

    /**
     * @return the defaultProfileGuid
     */
    public String getDefaultProfileGuid() {
        return defaultProfileGuid;
    }

    /**
     * @param defaultProfileGuid the defaultProfileGuid to set
     */
    public void setDefaultProfileGuid(String defaultProfileGuid) {
        this.changed=true;
        this.defaultProfileGuid = defaultProfileGuid;
    }

    
}
