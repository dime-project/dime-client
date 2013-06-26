package eu.dime.model.context;

import sit.json.JSONObject;

public class ContextDataFloat extends ContextData {
	
	private Float value;
    private String tag;
    
    public ContextDataFloat(String tag) {
        this.tag = tag;
    }

	@Override
	public ContextData getClone() {
		ContextDataFloat result = new ContextDataFloat(this.tag);
		result.value = new Float(value);
		return result;
	}

	@Override
	public void wipeItem() {
		value = new Float(-1);
	}

	@Override
	public JSONObject createJSONObject() {
		return getJSONValue(value,tag);
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) {
		value = getFloatValueOfJSONO(jsonObject,tag);
	}
	
	public void setValue(Float str) {
		this.value = str;
	}
	
	public Float getValue() {
		return this.value;
	}

}
