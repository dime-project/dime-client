package eu.dime.model.context;

import sit.json.JSONObject;

public class ContextDataBoolean extends ContextData {
	
	private Boolean value;
    private String tag;
    
    public ContextDataBoolean(String tag) {
        this.tag = tag;
    }

	@Override
	public ContextData getClone() {
		ContextDataBoolean result = new ContextDataBoolean(this.tag);
		result.value = new Boolean(value);
		return result;
	}

	@Override
	public void wipeItem() {
		value = new Boolean(false);
	}

	@Override
	public JSONObject createJSONObject() {
		return getJSONValue(value,tag);
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) {
		value = getBooleanValueOfJSONO(jsonObject,tag);
	}
	
	public void setValue(Boolean str) {
		this.value = str;
	}

	public Boolean getValue() {
		return this.value;
	}
}