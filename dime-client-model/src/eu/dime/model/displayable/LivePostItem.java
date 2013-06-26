/*
 *  Description of PersonItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.05.2012
 */
package eu.dime.model.displayable;

import eu.dime.restapi.DimeHelper;
import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public final class LivePostItem extends DisplayableItem implements ShareableItem {
	
	public static final String PRIVACY_LEVEL_TAG = "nao:privacyLevel";
    public static final String TEXT_TAG = "text";
    public static final String TIMESTAMP_TAG = "dlpo:timestamp";
    public static final String CREATOR_TAG = "nao:creator";
    public static final String CREATED_TAG = "created";
    private Double privacyLevel = DimeHelper.DEFAULT_INITIAL_PRIVACY_LEVEL;
    private String text = "";
    private long timeStamp = -1;
    private long created = System.currentTimeMillis();
    private String creator = "";

    public LivePostItem() {
    }

    public LivePostItem(String guid) {
        super(guid);
    }

    @Override
    protected void wipeItemForDisplayItem() {
    	this.privacyLevel = DimeHelper.DEFAULT_INITIAL_PRIVACY_LEVEL;
        this.text = "";
        this.timeStamp = -1;
        this.created=System.currentTimeMillis();
        this.creator = "";
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        LivePostItem result = new LivePostItem();
        result.privacyLevel = this.privacyLevel;
        result.text = this.text;
        result.timeStamp = this.timeStamp;
        result.created=this.created;
        result.creator = this.creator;
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
        this.text = getStringValueOfJSONO(jsonObject, TEXT_TAG);        
        this.timeStamp = getLongValueOfJSONO(jsonObject, TIMESTAMP_TAG);
        this.created = getLongValueOfJSONO(jsonObject, CREATED_TAG);
        this.creator = getStringValueOfJSONO(jsonObject, CREATOR_TAG);
        this.setPrivacyLevel(getDoubleValueOfJSONO(jsonObject, PRIVACY_LEVEL_TAG));
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(text, TEXT_TAG));
        newJSONObject.addChild(getJSONValue((long)timeStamp, TIMESTAMP_TAG));
        newJSONObject.addChild(getJSONValue((long)created, CREATED_TAG));
        newJSONObject.addChild(getJSONValue(creator, CREATOR_TAG));
        newJSONObject.addChild(getJSONValue(getPrivacyLevel(), PRIVACY_LEVEL_TAG));
        return newJSONObject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.changed = true;
        this.text = text;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.changed = true;
        this.timeStamp = timeStamp;
    }

    @Override
    public Double getPrivacyLevel() {
		return privacyLevel;
	}

    @Override
	public void setPrivacyLevel(Double privacyLevel) {
		changed = true;
		this.privacyLevel = privacyLevel;
	}

}
