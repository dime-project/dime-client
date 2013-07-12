package eu.dime.model.specialitem.advisory;

import java.util.List;
import java.util.Vector;

import sit.json.JSONObject;
import eu.dime.model.InvalidJSONItemException;

public class WarningAgentNotValidForSharing extends WarningAttributesObject {
	
	public static final String AGENTS_NOT_VALID_FOR_SHARING_TAG = "agentsNotValidForSharing";
	public static final String PARENT_GROUP_TAG = "parentGroup";
	
	private List<String> agentsNotValidForSharing;
	private String parentGroup;
	
	public WarningAgentNotValidForSharing() {
		wipeItem();
	}

	@Override
	public WarningAttributesObject getClone() {
		WarningAgentNotValidForSharing clone = new WarningAgentNotValidForSharing();
		clone.setAgentsNotValidForSharing(agentsNotValidForSharing);
		clone.setParentGroup(parentGroup);
		return clone;
	}

	@Override
	protected void wipeItem() {
		this.agentsNotValidForSharing = new Vector<String>();
		this.setParentGroup("");
	}

	@Override
	public JSONObject createJSONObject() {
		JSONObject result = new JSONObject("0");
        result.addChild(getJSONCollection(agentsNotValidForSharing, AGENTS_NOT_VALID_FOR_SHARING_TAG));
        result.addChild(getJSONValue(parentGroup, PARENT_GROUP_TAG));
		return result;
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
		// clean up first
        wipeItem();
        // read the json
        this.agentsNotValidForSharing = getStringListOfJSONObject(jsonObject, AGENTS_NOT_VALID_FOR_SHARING_TAG);
        this.parentGroup = getStringValueOfJSONO(jsonObject, PARENT_GROUP_TAG);
	}

	public List<String> getAgentsNotValidForSharing() {
		return agentsNotValidForSharing;
	}

	public void setAgentsNotValidForSharing(List<String> agentsNotValidForSharing) {
		this.agentsNotValidForSharing = agentsNotValidForSharing;
	}

	public String getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(String parentGroup) {
		this.parentGroup = parentGroup;
	}

}
