/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.specialitem.advisory;

import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;
import java.util.List;
import java.util.Vector;
import sit.json.JSONObject;

/**
 *
 * @author christian
 */
public class AdvisoryRequestItem extends JSONItem<AdvisoryRequestItem> {

    public static final String AGENT_GUIDS_TAG = "agentGuids";
    public static final String SHAREABLE_ITEMS = "shareableItems";
    public static final String PROFILE_GUID = "profileGuid";
    
    protected String profileGuid;
    protected List<String> agentGuids = new Vector<String>();
    protected List<String> shareableItems = new Vector<String>();
    
    public AdvisoryRequestItem(JSONObject jsonObject) throws InvalidJSONItemException {
        this.readJSONObject(jsonObject);
    }

    public AdvisoryRequestItem(String profileGuid, List<String> agentGuids, List<String> shareableItems) {
		this.profileGuid = profileGuid;
		this.agentGuids = agentGuids;
		this.shareableItems = shareableItems;
	}

	@Override
    public AdvisoryRequestItem getClone() {
        return new AdvisoryRequestItem(profileGuid, agentGuids, shareableItems);
    }

    @Override
    protected void wipeItem() {
    	this.profileGuid = "";
    	this.agentGuids = new Vector<String>();
    	this.shareableItems = new Vector<String>();
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(this.profileGuid, PROFILE_GUID));
        result.addChild(getJSONCollection(this.agentGuids, AGENT_GUIDS_TAG));
        result.addChild(getJSONCollection(this.shareableItems, SHAREABLE_ITEMS));
        return result;
    }

    @Override
    final public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        // clean up first
        wipeItem();

        // read the json
        this.profileGuid = getStringValueOfJSONO(jsonObject, PROFILE_GUID);
        this.agentGuids = getStringListOfJSONObject(jsonObject, AGENT_GUIDS_TAG);
        this.shareableItems = getStringListOfJSONObject(jsonObject, SHAREABLE_ITEMS);
    }
}
