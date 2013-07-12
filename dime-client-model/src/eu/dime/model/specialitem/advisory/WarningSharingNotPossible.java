package eu.dime.model.specialitem.advisory;

import sit.json.JSONObject;
import eu.dime.model.InvalidJSONItemException;

public class WarningSharingNotPossible extends WarningAttributesObject {

	@Override
	public WarningAttributesObject getClone() {
		return new WarningSharingNotPossible();
	}

	@Override
	protected void wipeItem() {

	}

	@Override
	public JSONObject createJSONObject() {
		return new JSONObject("0");
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {

	}

}
