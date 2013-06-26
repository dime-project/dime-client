/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.specialitem;

import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;
import sit.json.JSONObject;

/**
 *
 * @author simon
 */
public class SearchResultItem  extends JSONItem<SearchResultItem> {
    
    /*
        {
            "surname": "crash",
            "name": "test",
            "nickname": "dummy",
            "said": "test-dummy-urn-1236712352"
        },
    */
    
    public static final String SURNAME_TAG = "surname";
    public static final String NAME_TAG = "name";
    public static final String NICKNAME_TAG = "nickname";
    public static final String SAID_TAG = "said";
    
    private String surname;
    private String name; 
    private String nickname;
    private String said;

    public SearchResultItem() {
        this.wipeItem();
    }
    
    @Override
    public SearchResultItem getClone() {
        SearchResultItem result = new SearchResultItem();
        result.surname = this.surname;
        result.name = this.name;
        result.nickname = this.nickname;
        result.said = this.said;
        return result;
    }

    @Override
    protected final void wipeItem() {
        this.surname= "";
        this.name = "";
        this.nickname = "";
        this.said = "";
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject newJSONObject = new JSONObject("0");
        newJSONObject.addChild(getJSONValue(this.surname, SURNAME_TAG));
        newJSONObject.addChild(getJSONValue(this.name, NAME_TAG));
        newJSONObject.addChild(getJSONValue(this.nickname, NICKNAME_TAG));
        newJSONObject.addChild(getJSONValue(this.said, SAID_TAG));
        return newJSONObject;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        // clean up first
        wipeItem();
        this.surname = getStringValueOfJSONO(jsonObject, SURNAME_TAG);
        this.name = getStringValueOfJSONO(jsonObject, NAME_TAG);
        this.nickname = getStringValueOfJSONO(jsonObject, NICKNAME_TAG);
        this.said = getStringValueOfJSONO(jsonObject, SAID_TAG);
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return the said
     */
    public String getSaid() {
        return said;
    }

    /**
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param nickname the nickname to set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @param said the said to set
     */
    public void setSaid(String said) {
        this.said = said;
    }
    
}
