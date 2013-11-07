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
 *  Description of SituationItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.05.2012
 */
package eu.dime.model.displayable;

import sit.json.JSONObject;
import sit.sstl.StringEnumMap;

/**
 *
 * @author Simon Thiel
 */
public final class SituationItem extends DisplayableItem {

    // see https://confluence.deri.ie:8443/display/digitalme/REST+API+Specification+V0.9#RESTAPISpecificationV0.9-Situation
    public static enum SITUATION_ITEM_FIELDS {
        CREATOR, ACTIVE, CONFIDENCE, SCORE, PLACE_REFERENCE, EVENT_REFERENCE
    };
    public static final StringEnumMap<SITUATION_ITEM_FIELDS> SituationItemFieldMap =
            new StringEnumMap<SITUATION_ITEM_FIELDS>(SITUATION_ITEM_FIELDS.class, SITUATION_ITEM_FIELDS.values(),
            new String[]{"nao:creator", "active", "confidence", "nao:score", "placeReference", "eventReference"});
    private String creator;
    private boolean active;
    private double score;

    public SituationItem() {
        wipeItemForDisplayItem();
    }

    public SituationItem(String guid) {
        super(guid);
        wipeItemForDisplayItem();
    }

    @Override
    protected final void wipeItemForDisplayItem() {
        setCreator("");
        setActive(false);
        setScore(0.0d);
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        SituationItem result = new SituationItem();
        result.setCreator(this.getCreator());
        result.setActive(this.isActive());
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
        this.setCreator(getStringValueOfJSONO(jsonObject, SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.CREATOR)));
        this.setActive(getBooleanValueOfJSONO(jsonObject, SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.ACTIVE)));
        this.setScore(getDoubleValueOfJSONO(jsonObject, SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.SCORE)));
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(getCreator(), SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.CREATOR)));
        newJSONObject.addChild(getJSONValue(isActive(), SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.ACTIVE)));
        newJSONObject.addChild(getJSONValue(getScore(), SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.SCORE)));
        return newJSONObject;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        changed = true;
        this.creator = creator;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        changed = true;
        this.active = active;
    }

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		changed = true;
		this.score = score;
	}

}
