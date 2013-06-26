package eu.dime.model.context;

import java.util.List;
import java.util.Vector;

import sit.json.JSONObject;

public class ContextDataString extends ContextData {
	
	private String value = new String();
    private String tag;
    
    public ContextDataString(String tag) {
        this.tag = tag;
    }

	@Override
	public ContextData getClone() {
		ContextDataString result = new ContextDataString(this.tag);
		result.value = new String(value);
		return result;
	}

	@Override
	public void wipeItem() {
		this.value = new String();
	}

	@Override
	public JSONObject createJSONObject() {
		return getJSONValue(value,tag);
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) {
		value = getStringValueOfJSONO(jsonObject,tag);
	}
	
	public void setValue(String str) {
		this.value = str;
	}

	public String getValue() {
		return this.value;
	}
}
