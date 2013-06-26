package eu.dime.model.specialitem.advisory;

import sit.json.JSONObject;
import eu.dime.model.InvalidJSONItemException;

public class WarningTooManyResources extends WarningAttributesObject {
	
	public static final String NUMBER_OF_RESOURCES = "numberOfResources";
	
	private int numberOfResources = 0;
	
	public WarningTooManyResources() {
        wipeItem();
    }
	
	public WarningTooManyResources(int numberOfResources) {
        this.numberOfResources = numberOfResources;
    }

    @Override
    public WarningTooManyResources getClone() {
        return new WarningTooManyResources(numberOfResources);
    }

	@Override
	protected void wipeItem() {
		numberOfResources = 0;
	}

	@Override
	public JSONObject createJSONObject() {
		JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(numberOfResources, NUMBER_OF_RESOURCES));
        return result;
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
		// clean up first
        wipeItem();
        // read the json
        this.numberOfResources = getIntegerValueOfJSONO(jsonObject, NUMBER_OF_RESOURCES);
	}

	public int getNumberOfResources() {
		return numberOfResources;
	}

}
