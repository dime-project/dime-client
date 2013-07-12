package eu.dime.model.specialitem;

import java.util.List;
import sit.json.JSONObject;
import eu.dime.model.GenItem;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;

public class EvaluationInvolvedItem extends JSONItem<EvaluationInvolvedItem> {
	
	public static final String PROFILE_TAG = "profile";
	public static final String PROFILEATTRIBUTE_TAG = "profileattribute";
	public static final String PERSON_TAG = "person";
	public static final String GROUP_TAG = "group";
	public static final String DATABOX_TAG = "databox";
	public static final String LIVEPOST_TAG = "livepost";
	public static final String ACCOUNT_TAG = "account";
	
	private int profile = 0;
	private int profileattribute = 0;
	private int person = 0;
	private int group = 0;
	private int databox = 0;
	private int livepost = 0;
	private int account = 0;
	
	public EvaluationInvolvedItem() {
		wipeItem();
	}
	
	public EvaluationInvolvedItem(List<GenItem> items) {
		if(items != null && items.size() > 0) {
			for (GenItem genItem : items) {
				switch (genItem.getMType()) {
				case PROFILE:
					profile++;
					break;
				case PROFILEATTRIBUTE:
					profileattribute++;
					break;
				case PERSON:
					person++;
					break;
				case GROUP:
					group++;
					break;
				case DATABOX:
					databox++;
					break;
				case LIVEPOST:
					livepost++;
					break;
				case ACCOUNT:
					account++;
					break;
				default:
					break;
				}
			}
		} else {
			wipeItem();
		}
	}

	@Override
	public EvaluationInvolvedItem getClone() {
		EvaluationInvolvedItem result = new EvaluationInvolvedItem();
		result.profile = this.profile;
		result.profileattribute = this.profileattribute;
		result.person = this.person;
		result.group = this.group;
		result.databox = this.databox;
		result.livepost = this.livepost;
		result.account = this.account;
		return result;
	}
	
	@Override
	protected void wipeItem() {
		this.profile = 0;
		this.profileattribute = 0;
		this.person = 0;
		this.group = 0;
		this.databox = 0;
		this.livepost = 0;
		this.account = 0;
	}
	
	@Override
	public JSONObject createJSONObject() {
		JSONObject result = new JSONObject(EvaluationItem.INVOLVED_ITEMS_TAG);
		result.addChild(getJSONValue(this.profile, PROFILE_TAG));
		result.addChild(getJSONValue(this.profileattribute, PROFILEATTRIBUTE_TAG));
		result.addChild(getJSONValue(this.person, PERSON_TAG));
		result.addChild(getJSONValue(this.group, GROUP_TAG));
		result.addChild(getJSONValue(this.databox, DATABOX_TAG));
		result.addChild(getJSONValue(this.livepost, LIVEPOST_TAG));
		result.addChild(getJSONValue(this.account, ACCOUNT_TAG));
		return result;
	}
	
	@Override
	public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
		// clean up first
        wipeItem();
        // read the json
		this.profile = getIntegerValueOfJSONO(jsonObject, PROFILE_TAG);
		this.profileattribute = getIntegerValueOfJSONO(jsonObject, PROFILEATTRIBUTE_TAG);
		this.person = getIntegerValueOfJSONO(jsonObject, PERSON_TAG);
		this.group = getIntegerValueOfJSONO(jsonObject, GROUP_TAG);
		this.databox = getIntegerValueOfJSONO(jsonObject, DATABOX_TAG);
		this.livepost = getIntegerValueOfJSONO(jsonObject, LIVEPOST_TAG);
		this.account = getIntegerValueOfJSONO(jsonObject, ACCOUNT_TAG);
	}
	
}
