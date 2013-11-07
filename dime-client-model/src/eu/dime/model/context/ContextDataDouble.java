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
