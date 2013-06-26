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
 * @author Christian Knecht
 */
public final class ActivityItem extends DisplayableItem {

    // see https://confluence.deri.ie:8443/display/digitalme/REST+API+Specification+V0.9#RESTAPISpecificationV0.9-GeneralstructureforJSONmessages
    public static enum ACTIVITY_ITEM_FIELDS {
        DESCRIPTION, CALORIESEXPENDED, DISTANCECOVERED, DURATION
    };
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static final StringEnumMap<ACTIVITY_ITEM_FIELDS> SituationItemFieldMap =
            new StringEnumMap(ACTIVITY_ITEM_FIELDS.class, ACTIVITY_ITEM_FIELDS.values(),
            new String[]{"description", "caloriesExpended", "distanceCovered", "duration"
    });
    
    private String description;
    private int caloriesExpended;
    private int distanceCovered;
    private long duration;

    public ActivityItem() {
        wipeItemForDisplayItem();
    }

    public ActivityItem(String guid) {
        super(guid);
        wipeItemForDisplayItem();
    }

    @Override
    protected final void wipeItemForDisplayItem() {
        setDescription("");
        setCaloriesExpended(0);
        setDistanceCovered(0);
        setDuration(0);
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        ActivityItem result = new ActivityItem();
        result.setDescription(this.getDescription());
        result.setCaloriesExpended(this.getCaloriesExpended());
        result.setDistanceCovered(this.getDistanceCovered());
        result.setDuration(this.getDuration());
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
        this.setDescription(getStringValueOfJSONO(jsonObject, SituationItemFieldMap.get(ACTIVITY_ITEM_FIELDS.DESCRIPTION)));
        this.setCaloriesExpended(getIntegerValueOfJSONO(jsonObject, SituationItemFieldMap.get(ACTIVITY_ITEM_FIELDS.CALORIESEXPENDED)));
        this.setDistanceCovered(getIntegerValueOfJSONO(jsonObject, SituationItemFieldMap.get(ACTIVITY_ITEM_FIELDS.DISTANCECOVERED)));
        this.setDuration(getLongValueOfJSONO(jsonObject, SituationItemFieldMap.get(ACTIVITY_ITEM_FIELDS.DURATION)));
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(getDescription(), SituationItemFieldMap.get(ACTIVITY_ITEM_FIELDS.DESCRIPTION)));
        newJSONObject.addChild(getJSONValue(getCaloriesExpended(), SituationItemFieldMap.get(ACTIVITY_ITEM_FIELDS.CALORIESEXPENDED)));
        newJSONObject.addChild(getJSONValue(getDistanceCovered(), SituationItemFieldMap.get(ACTIVITY_ITEM_FIELDS.DISTANCECOVERED)));
        newJSONObject.addChild(getJSONValue(getDuration(), SituationItemFieldMap.get(ACTIVITY_ITEM_FIELDS.DURATION)));
        return newJSONObject;
    }

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the caloriesExpended
	 */
	public int getCaloriesExpended() {
		return caloriesExpended;
	}

	/**
	 * @param caloriesExpended the caloriesExpended to set
	 */
	public void setCaloriesExpended(int caloriesExpended) {
		this.caloriesExpended = caloriesExpended;
	}

	/**
	 * @return the distanceCovered
	 */
	public int getDistanceCovered() {
		return distanceCovered;
	}

	/**
	 * @param distanceCovered the distanceCovered to set
	 */
	public void setDistanceCovered(int distanceCovered) {
		this.distanceCovered = distanceCovered;
	}

	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}
}
