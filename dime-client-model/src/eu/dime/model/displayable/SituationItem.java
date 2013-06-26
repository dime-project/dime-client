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
//    private double confidence;
//    private String place_reference;
//    private String event_reference;

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
//        setConfidence(-1);
//        setPlaceReference("");
//        setEventReference("");
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        SituationItem result = new SituationItem();
        result.setCreator(this.getCreator());
        result.setActive(this.isActive());
//        result.setConfidence(this.getConfidence());
//        result.setPlaceReference(this.getPlaceReference());
//        result.setEventReference(this.getEventReference());
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
        this.setCreator(getStringValueOfJSONO(jsonObject, SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.CREATOR)));
        this.setActive(getBooleanValueOfJSONO(jsonObject, SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.ACTIVE)));
        this.setScore(getDoubleValueOfJSONO(jsonObject, SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.SCORE)));
//        this.setConfidence(getDoubleValueOfJSONO(jsonObject, SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.CONFIDENCE)));
//        this.setPlaceReference(getStringValueOfJSONO(jsonObject, SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.PLACE_REFERENCE)));
//        this.setEventReference(getStringValueOfJSONO(jsonObject, SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.EVENT_REFERENCE)));
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(getCreator(), SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.CREATOR)));
        newJSONObject.addChild(getJSONValue(isActive(), SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.ACTIVE)));
        newJSONObject.addChild(getJSONValue(getScore(), SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.SCORE)));
//        newJSONObject.addChild(getJSONValue(getConfidence(), SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.CONFIDENCE)));
//        newJSONObject.addChild(getJSONValue(getPlaceReference(), SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.PLACE_REFERENCE)));
//        newJSONObject.addChild(getJSONValue(getEventReference(), SituationItemFieldMap.get(SITUATION_ITEM_FIELDS.EVENT_REFERENCE)));

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

//    public double getConfidence() {
//        return confidence;
//    }
//
//    public void setConfidence(double confidence) {
//        changed = true;
//        this.confidence = confidence;
//    }
//
//    public String getPlaceReference() {
//        return place_reference;
//    }
//
//    public void setPlaceReference(String place_reference) {
//        this.changed = true;
//        this.place_reference = place_reference;
//    }
//
//    public String getEventReference() {
//        return event_reference;
//    }
//
//    public void setEventReference(String event_reference) {
//        this.changed = true;
//        this.event_reference = event_reference;
//    }
}
