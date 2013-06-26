package eu.dime.model.context;

import sit.json.JSONObject;

public class ContextDataDouble extends ContextData {
	
	private Double value;
    private String tag;
    
    public ContextDataDouble(String tag) {
        this.tag = tag;
    }

	@Override
	public ContextData getClone() {
		ContextDataDouble result = new ContextDataDouble(this.tag);
		result.value = new Double(value);
		return result;
	}

	@Override
	public void wipeItem() {
		value = new Double(-1.0);
	}

	@Override
	public JSONObject createJSONObject() {
		return getJSONValue(value,tag);
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) {
		value = getDoubleValueOfJSONO(jsonObject,tag);
	}
	
	public void setValue(Double str) {
		this.value = str;
	}

	public Double getValue() {
		return this.value;
	}
}
