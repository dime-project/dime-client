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

    public final static String USERNAME_TAG = "username";
    public final static String PASSWORD_TAG = "password";
    public final static String ROLE_TAG = "role";
    public final static String ENABLED_TAG = "enabled";
     
    private String username;
    private String password = "";
    private String role = "";    
    private boolean enabled = false;    

    public AuthItem(String username) {
        this.username = username;
    }
    
    public AuthItem() {
        this.username = null; 
    }
    
    @Override
    public AuthItem getClone() {
        AuthItem result = new AuthItem(this.username);        
        result.password = this.password;
        result.role = this.role;    
        result.enabled = this.enabled;
        return result;
    }

    @Override
    protected void wipeItem() {
        username = "";
        password = "";
        role = "";    
        enabled = false;
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject newJSONObject = new JSONObject("0");
        newJSONObject.addChild(getJSONValue(this.username, USERNAME_TAG));
        newJSONObject.addChild(getJSONValue(this.password, PASSWORD_TAG));
        newJSONObject.addChild(getJSONValue(this.role, ROLE_TAG));
        newJSONObject.addChild(getJSONValue(this.enabled, ENABLED_TAG));        
        return newJSONObject;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
         // clean up first
        wipeItem();

        this.username = getStringValueOfJSONO(jsonObject, USERNAME_TAG);
        this.password = getStringValueOfJSONO(jsonObject, PASSWORD_TAG);
        this.role = getStringValueOfJSONO(jsonObject, ROLE_TAG);
        this.enabled = getBooleanValueOfJSONO(jsonObject, ENABLED_TAG);
    }

    public String getKey() {
        return username;
    }
    
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
