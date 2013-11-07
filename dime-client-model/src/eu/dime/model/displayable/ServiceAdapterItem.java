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

import java.util.List;
import java.util.Vector;

import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.specialitem.AccountSettingsItem;

import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public final class ServiceAdapterItem extends DisplayableItem {

    public static final String AUTH_URL_TAG = "authUrl";
    public static final String STATUS_TAG = "status";
    public static final String IS_CONFIGURABLE_TAG = "isConfigurable";
    public static final String DESCRIPTION_TAG = "description";
    private String authUrl; //if empty, AccountItem needs to be created by the clients
    private String status;
    private boolean isConfigurable;
    private List<AccountSettingsItem> settings = new Vector<AccountSettingsItem>();
    private String description;

	public ServiceAdapterItem() {
        wipeItemForDisplayItem();
    }

    public ServiceAdapterItem(String guid) {
        super(guid);
        wipeItemForDisplayItem();
    }
    
    @Override
    protected final void wipeItemForDisplayItem() {
        authUrl = "";
        isConfigurable = false;
        description = "";
        settings.clear();
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        ServiceAdapterItem result = new ServiceAdapterItem();
        result.authUrl = this.authUrl;
        result.isConfigurable = this.isConfigurable;
        result.description = this.description;
        result.settings = this.settings;
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
    	// clean up first
        wipeItemForDisplayItem();

        // read the json
        this.authUrl = getStringValueOfJSONO(jsonObject, AUTH_URL_TAG);
        this.status = getStringValueOfJSONO(jsonObject, STATUS_TAG);
        this.isConfigurable = getBooleanValueOfJSONO(jsonObject, IS_CONFIGURABLE_TAG);
        this.description = getStringValueOfJSONO(jsonObject, DESCRIPTION_TAG);
        List<JSONObject> children = getItemsOfJSONO(jsonObject, AccountItem.SETTINGS_TAG);
        for (JSONObject child : children) {
			try {
				AccountSettingsItem asi = new AccountSettingsItem();
				asi.readJSONObject(child);
				this.settings.add(asi.getClone());
			} catch (InvalidJSONItemException e) {
				e.printStackTrace();
			}
		}
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(authUrl, AUTH_URL_TAG));
        newJSONObject.addChild(getJSONValue(status, STATUS_TAG));
        newJSONObject.addChild(getJSONValue(isConfigurable, IS_CONFIGURABLE_TAG));
        newJSONObject.addChild(getJSONValue(description, DESCRIPTION_TAG));
       	JSONObject child = new JSONObject(AccountItem.SETTINGS_TAG);
       	for (AccountSettingsItem setting : settings) {
				child.addItem(setting.createJSONObject());
		}
       	newJSONObject.addChild(child);
        return newJSONObject;
    }

    /**
     * @return the authUrl
     */
    public String getAuthUrl() {
        return authUrl;
    }

    /**
     * @param authUrl the authUrl to set
     */
    public void setAuthUrl(String authUrl) {
        changed = true;
        this.authUrl = authUrl;
    }
    
    public boolean isConfigurable() {
		return isConfigurable;
	}
    
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        changed = true;
        this.status = status;
    }
    
    public List<AccountSettingsItem> getSettings() {
		return settings;
	}
    
    public String getDescription() {
		return description;
	}

}
