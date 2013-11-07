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

package eu.dime.model.specialitem;

import sit.json.JSONObject;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;

public class AccountSettingsItem extends JSONItem<AccountSettingsItem> {
	
	public static final String NAME_TAG = "name";
	public static final String TYPE_TAG = "fieldtype";
	public static final String MANDATORY_TAG = "mandatory";
	public static final String VALUE_TAG = "value";
	
	public enum ACCOUNT_SETTINGS_TYPES {
		STRING, PASSWORD, ACCOUNT, BOOLEAN, LINK;
		@Override 
		public String toString() {
			String s = super.toString();
			return s.substring(0).toLowerCase();
		}
	}
	
	private String name;
    private String type;
    private boolean mandatory = false;
    private String value;
    
    public AccountSettingsItem() {
    	wipeItem();
    }
    
    public AccountSettingsItem(String name, String type, boolean mandatory, String value) {
    	this.name = name;
    	this.type = type;
    	this.mandatory = mandatory;
    	this.value = value;
    }

	@Override
	public AccountSettingsItem getClone() {
		AccountSettingsItem result = new AccountSettingsItem();
		result.name = this.name;
		result.type = this.type;
		result.mandatory = this.mandatory;
		result.value = this.value;
		return result;
	}

	@Override
	protected void wipeItem() {
		this.name = "";
		this.type = "";
		this.mandatory = false;
		this.value = "";
	}

	@Override
	public JSONObject createJSONObject() {
		JSONObject result = new JSONObject("0");
		result.addChild(getJSONValue(name, NAME_TAG));
		result.addChild(getJSONValue(type, TYPE_TAG));
		result.addChild(getJSONValue(mandatory, MANDATORY_TAG));
		result.addChild(getJSONValue(value, VALUE_TAG));
		return result;
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
		// clean up first
        wipeItem();

        // read the json
		this.name = getStringValueOfJSONO(jsonObject, NAME_TAG);
		this.type = getStringValueOfJSONO(jsonObject, TYPE_TAG);
		this.mandatory = getBooleanValueOfJSONO(jsonObject, MANDATORY_TAG);
		this.value = getStringValueOfJSONO(jsonObject, VALUE_TAG);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
