/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.specialitem.usernotification;

import eu.dime.model.InvalidJSONItemException;
import sit.json.JSONObject;

/**
 *
 * @author simon
 */
public class UNEntryMessage extends UnEntry {

    public static final String MESSAGE = "message";
    public static final String LINK = "link";
    private String message;
    private String link;

    public UNEntryMessage() {
        wipeItem();
    }

    @Override
    public UnEntry getClone() {
        UNEntryMessage entry = new UNEntryMessage();
        entry.message = this.message;
        entry.link = this.link;
        return entry;
    }

    @Override
    protected final void wipeItem() {
        this.message = "";
        this.link = "";
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(message, MESSAGE));
        result.addChild(getJSONValue(link, LINK));
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        wipeItem();
        this.message = getStringValueOfJSONO(jsonObject, MESSAGE);
        this.link = getStringValueOfJSONO(jsonObject, LINK);
    }

    public String getMessage() {
        return message;
    }

    public String getLink() {
        return link;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }
    
    @Override
    public UN_TYPE getUnType() {
        return UN_TYPE.MESSAGE;
    }
    
    
}
