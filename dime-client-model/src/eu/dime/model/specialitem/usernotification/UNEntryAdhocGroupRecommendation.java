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
public class UNEntryAdhocGroupRecommendation extends UnEntry {

    public static final String GUID = "guid";
    public static final String CREATOR = "nao:creator";
    public static final String NAME = "name";
    private String guid;
    private String name;
    private String creator;

    public UNEntryAdhocGroupRecommendation() {
        wipeItem();
    }

    @Override
    public UnEntry getClone() {
        UNEntryAdhocGroupRecommendation newUnEntry = new UNEntryAdhocGroupRecommendation();
        newUnEntry.guid = this.guid;
        newUnEntry.name = this.name;
        newUnEntry.creator = this.creator;
        return newUnEntry;
    }

    @Override
    protected final void wipeItem() {
        this.guid = "";
        this.name = "";
        this.creator = "";
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject newJSONObject = new JSONObject("0");
        newJSONObject.addChild(getJSONValue(guid, GUID));
        newJSONObject.addChild(getJSONValue(name, NAME));
        newJSONObject.addChild(getJSONValue(creator, CREATOR));
        return newJSONObject;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        wipeItem();
        this.guid = getStringValueOfJSONO(jsonObject, GUID);
        this.name = getStringValueOfJSONO(jsonObject, NAME);
        this.creator = getStringValueOfJSONO(jsonObject, CREATOR);
    }

    public String getGuid() {
        return guid;
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    @Override
    public UN_TYPE getUnType() {
        return UN_TYPE.ADHOC_GROUP_RECOMMENDATION;
    }
}
