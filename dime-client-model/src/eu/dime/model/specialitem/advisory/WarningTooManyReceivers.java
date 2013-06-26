package eu.dime.model.specialitem.advisory;

import sit.json.JSONObject;
import eu.dime.model.InvalidJSONItemException;

public class WarningTooManyReceivers extends WarningAttributesObject {
	
	public static final String NUMBER_OF_RECEIVERS = "numberOfReceivers";
	
	private int numberOfReceivers = 0;
	
	public WarningTooManyReceivers() {
        wipeItem();
    }
	
	public WarningTooManyReceivers(int numberOfReceivers) {
        this.numberOfReceivers = numberOfReceivers;
    }

    @Override
    public WarningTooManyReceivers getClone() {
        return new WarningTooManyReceivers(numberOfReceivers);
    }

	@Override
	protected void wipeItem() {
		numberOfReceivers = 0;
	}

	@Override
	public JSONObject createJSONObject() {
		JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(numberOfReceivers, NUMBER_OF_RECEIVERS));
        return result;
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
		// clean up first
        wipeItem();
        // read the json
        this.numberOfReceivers = getIntegerValueOfJSONO(jsonObject, NUMBER_OF_RECEIVERS);
	}

	public int getNumberOfReceivers() {
		return numberOfReceivers;
	}

}
