package eu.dime.mobile.helper.objects;

import java.util.ArrayList;
import java.util.List;
import sit.json.JSONObject;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;

public class OwnServers extends JSONItem<OwnServers> {
	
	private static final String OWN_SERVERS = "OWN_SERVERS";
	
	List<String> ownServers = new ArrayList<String>();

	@Override
	public OwnServers getClone() {
		return null;
	}

	@Override
	protected void wipeItem() {
		ownServers = new ArrayList<String>();
	}

	@Override
	public JSONObject createJSONObject() {
		JSONObject result = new JSONObject("0");
		result.addChild(getJSONCollection(this.ownServers, OWN_SERVERS));
		return result;
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
		this.ownServers.addAll(getStringListOfJSONObject(jsonObject, OWN_SERVERS));
	}
	
	public void addOwnServer(String server) {
		ownServers.add(server);
	}
	
	public boolean removeOwnServer(String server) {
		return ownServers.remove(server);
	}
	
	public List<String> getOwnServers() {
		return ownServers;
	}

}
