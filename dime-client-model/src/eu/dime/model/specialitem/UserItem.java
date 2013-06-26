package eu.dime.model.specialitem;

import sit.json.JSONObject;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;

public class UserItem extends JSONItem<UserItem> {

    public final static String USERNAME_TAG = "username";
    public final static String PASSWORD_TAG = "password";
    public final static String NICKNAME_TAG = "nickname";
    public final static String FIRSTNAME_TAG = "firstname";
    public final static String LASTNAME_TAG = "lastname";
    public final static String CHECKBOX_AGREED_TAG = "checkbox_agree";
    public final static String EMAIL_TAG = "emailAddress";
    
    private String username;
    private String password;
    private String nickname;
    private String firstname;
    private String lastname;
    private boolean checkbox_agreed;
    private String emailAddress;

    public UserItem() {
        wipeItem();
    }

    @Override
    protected final void wipeItem() {
        this.username = "";
        this.password = "";
        this.nickname = "";
        this.firstname = "";
        this.lastname = "";
        this.checkbox_agreed = false;
        this.emailAddress = "";
    }

    @Override
    public UserItem getClone() {
        UserItem result = new UserItem();
        result.username = this.username;
        result.password = this.password;
        result.nickname = this.nickname;
        result.firstname = this.firstname;
        result.lastname = this.lastname;
        result.checkbox_agreed = this.checkbox_agreed;
        result.emailAddress = this.emailAddress;
        return result;
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(username, USERNAME_TAG));
        result.addChild(getJSONValue(password, PASSWORD_TAG));
        result.addChild(getJSONValue(nickname, NICKNAME_TAG));
        result.addChild(getJSONValue(firstname, FIRSTNAME_TAG));
        result.addChild(getJSONValue(lastname, LASTNAME_TAG));
        result.addChild(getJSONValue(checkbox_agreed, CHECKBOX_AGREED_TAG));
        result.addChild(getJSONValue(emailAddress, EMAIL_TAG));
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        // clean up first
        wipeItem();
        this.username = getStringValueOfJSONO(jsonObject, USERNAME_TAG);
        this.password = getStringValueOfJSONO(jsonObject, PASSWORD_TAG);
        this.nickname = getStringValueOfJSONO(jsonObject, NICKNAME_TAG);
        this.firstname = getStringValueOfJSONO(jsonObject, FIRSTNAME_TAG);
        this.lastname = getStringValueOfJSONO(jsonObject, LASTNAME_TAG);
        this.checkbox_agreed = getBooleanValueOfJSONO(jsonObject, CHECKBOX_AGREED_TAG);
        this.emailAddress = getStringValueOfJSONO(jsonObject, EMAIL_TAG);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public boolean isCheckbox_agreed() {
        return checkbox_agreed;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setCheckbox_agreed(boolean checkbox_agreed) {
        this.checkbox_agreed = checkbox_agreed;
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
