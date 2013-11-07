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
 *  Description of DisplayableItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 27.04.2012
 */
package eu.dime.model.specialitem;

import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;
import sit.json.JSONObject;
import sit.sstl.ObjectWithKey;

/**
 *
 * @author Simon Thiel
 */
public class AuthItem extends JSONItem<AuthItem> implements ObjectWithKey<String> {

	public final static String TYPE_TAG = "type";
	public final static String ROLE_TAG = "role";
	public final static String SAID_TAG = "said";
    public final static String USERNAME_TAG = "username";
    public final static String PASSWORD_TAG = "password";
    public final static String ENABLED_TAG = "enabled";
    public final static String UI_LANGUAGE_TAG = "uiLanguage";
    public final static String EVALUATION_DATA_CAPTURING_ENABLED_TAG = "evaluationDataCapturingEnabled";
    public final static String EVALUATION_ID_TAG = "evaluationId";
    public final static String USER_STATUS_FLAG_TAG = "userStatusFlag"; // 0==initialized; 1==loggedInWebUI at least once
    
    private String type = "";
    private int role = 1;
    private String said = "";
    private String username = "";
    private String password = "";      
    private boolean enabled = false;
    private String uiLanguage = "en";
    private boolean evaluationDataCapturingEnabled = false;
    private String evaluationId = "";
    private int userStatusFlag = 0;
    
    public AuthItem() {
        wipeItem();
    }
    
    @Override
    public AuthItem getClone() {
        AuthItem result = new AuthItem();
        result.type = this.type;
        result.role = this.role;
        result.said = this.said;
        result.username = this.username;
        result.password = this.password;
        result.enabled = this.enabled;
        result.uiLanguage = this.uiLanguage;
        result.evaluationDataCapturingEnabled = this.evaluationDataCapturingEnabled;
        result.evaluationId = this.evaluationId;
        result.userStatusFlag = this.userStatusFlag;
        return result;
    }

    @Override
    protected void wipeItem() {
    	type = "auth";
    	role = 1;
    	said = "";
        username = "";
        password = "";  
        enabled = false;
        uiLanguage = "en";
        evaluationDataCapturingEnabled = false;
        evaluationId = "";
        userStatusFlag = 0;
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject newJSONObject = new JSONObject("0");
        newJSONObject.addChild(getJSONValue(this.type, TYPE_TAG));
        newJSONObject.addChild(getJSONValue(this.role, ROLE_TAG));
        newJSONObject.addChild(getJSONValue(this.said, SAID_TAG));
        newJSONObject.addChild(getJSONValue(this.username, USERNAME_TAG));
        newJSONObject.addChild(getJSONValue(this.password, PASSWORD_TAG));
        newJSONObject.addChild(getJSONValue(this.enabled, ENABLED_TAG));
        newJSONObject.addChild(getJSONValue(this.uiLanguage, UI_LANGUAGE_TAG));
        newJSONObject.addChild(getJSONValue(this.evaluationDataCapturingEnabled, EVALUATION_DATA_CAPTURING_ENABLED_TAG));
        newJSONObject.addChild(getJSONValue(this.evaluationId, EVALUATION_ID_TAG));
        newJSONObject.addChild(getJSONValue(this.userStatusFlag, USER_STATUS_FLAG_TAG));
        return newJSONObject;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
         // clean up first
        wipeItem();

        this.type = getStringValueOfJSONO(jsonObject, TYPE_TAG);
        this.role = getIntegerValueOfJSONO(jsonObject, ROLE_TAG);
        this.said = getStringValueOfJSONO(jsonObject, SAID_TAG);
        this.username = getStringValueOfJSONO(jsonObject, USERNAME_TAG);
        this.password = getStringValueOfJSONO(jsonObject, PASSWORD_TAG);        
        this.enabled = getBooleanValueOfJSONO(jsonObject, ENABLED_TAG);
        this.uiLanguage = getStringValueOfJSONO(jsonObject, UI_LANGUAGE_TAG);
        this.evaluationDataCapturingEnabled = getBooleanValueOfJSONO(jsonObject, EVALUATION_DATA_CAPTURING_ENABLED_TAG);
        this.evaluationId = getStringValueOfJSONO(jsonObject, EVALUATION_ID_TAG);
        this.userStatusFlag = getIntegerValueOfJSONO(jsonObject, USER_STATUS_FLAG_TAG);
    }

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getSaid() {
		return said;
	}

	public void setSaid(String said) {
		this.said = said;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getUiLanguage() {
		return uiLanguage;
	}

	public void setUiLanguage(String uiLanguage) {
		this.uiLanguage = uiLanguage;
	}

	public boolean isEvaluationDataCapturingEnabled() {
		return evaluationDataCapturingEnabled;
	}

	public void setEvaluationDataCapturingEnabled(boolean evaluationDataCapturingEnabled) {
		this.evaluationDataCapturingEnabled = evaluationDataCapturingEnabled;
	}

	public String getEvaluationId() {
		return evaluationId;
	}

	public void setEvaluationId(String evaluationId) {
		this.evaluationId = evaluationId;
	}

	public int getUserStatusFlag() {
		return userStatusFlag;
	}

	public void setUserStatusFlag(int userStatusFlag) {
		this.userStatusFlag = userStatusFlag;
	}

	public String getKey() {
        return username;
    }
    
}
