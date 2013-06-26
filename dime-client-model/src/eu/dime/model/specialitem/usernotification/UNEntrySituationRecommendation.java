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
public class UNEntrySituationRecommendation extends UnEntry {

    public static final String GUID_TAG = "guid";
    public static final String SCORE_TAG = "nao:score";
    private String guid;
    private double score;

    public UNEntrySituationRecommendation() {
        wipeItem();
    }

    @Override
    public UnEntry getClone() {
        UNEntrySituationRecommendation entry = new UNEntrySituationRecommendation();
        entry.guid = this.guid;
        entry.score = this.score;
        return entry;
    }

    @Override
    protected final void wipeItem() {
        this.guid = "";
        this.score = 0.0d;
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(guid, GUID_TAG));
        result.addChild(getJSONValue(score, SCORE_TAG));
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        this.guid = getStringValueOfJSONO(jsonObject, GUID_TAG);
        this.score = getDoubleValueOfJSONO(jsonObject, SCORE_TAG);
    }

    public String getGuid() {
        return guid;
    }
    
    @Override
    public UN_TYPE getUnType() {
        return UN_TYPE.SITUATION_RECOMMENDATION;
    }

	public double getScore() {
		return score;
	}
    
}
