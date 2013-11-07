/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

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
