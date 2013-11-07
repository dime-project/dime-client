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
