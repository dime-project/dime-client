/*
 *  Description of PersonItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.05.2012
 */
package eu.dime.model.displayable;

import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public final class ProfileItem extends DisplayableItem{

    public static final String SERVICE_ACCOUNT_ID_TAG = "said";
    public static final String IS_EDITABLE_TAG = "editable";
    
    private String serviceAccountId = "";
    private boolean editable = true;

    public ProfileItem() {
    }

    public ProfileItem(String guid) {
        super(guid);
    }
    
    @Override
    protected void wipeItemForDisplayItem() {
        serviceAccountId = "";
        editable = false;
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        ProfileItem result = new ProfileItem();
        result.serviceAccountId = this.serviceAccountId;
        result.editable = this.editable;
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
        this.serviceAccountId = getStringValueOfJSONO(jsonObject, SERVICE_ACCOUNT_ID_TAG);
        this.editable = getBooleanValueOfJSONO(jsonObject, IS_EDITABLE_TAG);
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(serviceAccountId, SERVICE_ACCOUNT_ID_TAG));
        newJSONObject.addChild(getJSONValue(editable, IS_EDITABLE_TAG));
        return newJSONObject;
    }

    /**
     * @return the serviceAccountId
     */
    public String getServiceAccountId() {
        return serviceAccountId;
    }
    
    public void setEditable(boolean editable) {
		this.editable = editable;
    }


    /**
    * sets the service-account-id - use with care.
    * changing the service-account-id of an already shared-to profile might lead
    * to inconsistencies between different users sharing items
    * 
    * @param serviceAccountId the serviceAccountId to set
    */
    public void setServiceAccountId(String serviceAccountId) {
        this.serviceAccountId = serviceAccountId;
    }


    public boolean isEditable() {
        return editable;
    }


}
