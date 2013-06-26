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
public final class AccountItem extends DisplayableItem {

    private final static String SERVICE_ADAPTER_GUID_TAG = "serviceadapterguid";
    private final static String IS_ACTIVE_TAG = "isActive";
    public final static String SETTINGS_TAG = "settings";
    
    private String serviceAdapterGUID = null;
    private boolean isActive;
    private List<AccountSettingsItem> settings = new Vector<AccountSettingsItem>();

	public AccountItem() {
        wipeItemForDisplayItem();
    }

    public AccountItem(String guid) {
        super(guid);
        wipeItemForDisplayItem();
    }
    
    @Override
    protected void wipeItemForDisplayItem() {
        this.serviceAdapterGUID = "";
        this.isActive = false;
        this.settings.clear();
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        AccountItem result = new AccountItem();
        result.serviceAdapterGUID = this.serviceAdapterGUID;
        result.isActive = this.isActive;
        result.settings = this.settings;
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
        this.serviceAdapterGUID = getStringValueOfJSONO(jsonObject, SERVICE_ADAPTER_GUID_TAG);
        this.isActive = getBooleanValueOfJSONO(jsonObject, IS_ACTIVE_TAG);
        List<JSONObject> children = getItemsOfJSONO(jsonObject, SETTINGS_TAG);
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
         newJSONObject.addChild(getJSONValue(this.serviceAdapterGUID, SERVICE_ADAPTER_GUID_TAG));
         newJSONObject.addChild(getJSONValue(isActive, IS_ACTIVE_TAG));
         JSONObject child = new JSONObject(SETTINGS_TAG);
         for (AccountSettingsItem setting : settings) {
				child.addItem(setting.createJSONObject());
		 }
         newJSONObject.addChild(child);
         return newJSONObject;
    }

    /**
     * @return the serviceAdapterGUID
     */
    public String getServiceAdapterGUID() {
        return serviceAdapterGUID;
    }

    /**
     * @param serviceAdapterGUID the serviceAdapterGUID to set
     */
    public void setServiceAdapterGUID(String serviceAdapterGUID) {
        this.serviceAdapterGUID = serviceAdapterGUID;
    }

    public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
    
	public List<AccountSettingsItem> getSettings() {
		return settings;
	}

	public void setSettings(List<AccountSettingsItem> settings) {
		this.settings = settings;
	}
	
}
