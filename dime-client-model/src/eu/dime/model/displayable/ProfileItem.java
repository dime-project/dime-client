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
public final class ProfileItem extends DisplayableItem implements ShareableItem {

    public static final String SERVICE_ACCOUNT_ID_TAG = "said";
    public static final String IS_EDITABLE_TAG = "editable";
    public static final String PRIVACY_VALUE_TAG = "nao:privacyLevel";
    public static final String SUPPORTS_SHARING_TAG = "supportsSharing";
    
    private Double privacyLevel;
    private String serviceAccountId = "";
    private boolean editable = true;
    private boolean supportsSharing = false;

    public ProfileItem() {
    }

    public ProfileItem(String guid) {
        super(guid);
    }
    
    @Override
    protected void wipeItemForDisplayItem() {
    	privacyLevel = DimeHelper.DEFAULT_INITIAL_PRIVACY_LEVEL;
        serviceAccountId = "";
        editable = false;
        supportsSharing = true;
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        ProfileItem result = new ProfileItem();
        result.privacyLevel = this.privacyLevel;
        result.serviceAccountId = this.serviceAccountId;
        result.editable = this.editable;
        result.supportsSharing = this.supportsSharing;
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
    	this.privacyLevel = getDoubleValueOfJSONO(jsonObject, PRIVACY_VALUE_TAG);
        this.serviceAccountId = getStringValueOfJSONO(jsonObject, SERVICE_ACCOUNT_ID_TAG);
        this.editable = getBooleanValueOfJSONO(jsonObject, IS_EDITABLE_TAG);
        this.supportsSharing = getBooleanValueOfJSONO(jsonObject, SUPPORTS_SHARING_TAG);
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
    	newJSONObject.addChild(getJSONValue(privacyLevel, PRIVACY_VALUE_TAG)); 
        newJSONObject.addChild(getJSONValue(serviceAccountId, SERVICE_ACCOUNT_ID_TAG));
        newJSONObject.addChild(getJSONValue(editable, IS_EDITABLE_TAG));
        newJSONObject.addChild(getJSONValue(supportsSharing, SUPPORTS_SHARING_TAG));
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

	@Override
	public Double getPrivacyLevel() {
		return privacyLevel;
	}

	@Override
	public void setPrivacyLevel(Double privacyLevel) {
		this.privacyLevel = privacyLevel;
	}
	
	public boolean supportsSharing() {
		return supportsSharing;
	}

	public void setSupportsSharing(boolean supportsSharing) {
		this.supportsSharing = supportsSharing;
	}

}
