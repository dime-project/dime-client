package eu.dime.model.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import sit.json.JSONObject;

public class ContextDataIntegerList extends ContextData {
	
	private List<Integer> list = new Vector();
    private String tag ;

    public ContextDataIntegerList(String tag) {
        this.tag = tag;
    }

	@Override
	public ContextData getClone() {
		ContextDataIntegerList result = new ContextDataIntegerList(this.tag);
        result.list = new Vector(list);
        return result;
	}

	@Override
	public void wipeItem() {
		list.clear();
	}

	@Override
	public JSONObject createJSONObject() {
		return getJSONCollectionFromIntegers(list,tag);
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) {
		list = getIntegerListOfJSONCollection(jsonObject);
	}
	
	public void addInt(Integer i) {
		list.add(i);
	}

}
