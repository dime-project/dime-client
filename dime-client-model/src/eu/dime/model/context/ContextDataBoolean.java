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

public class ContextDataBoolean extends ContextData {
	
	private Boolean value;
    private String tag;
    
    public ContextDataBoolean(String tag) {
        this.tag = tag;
    }

	@Override
	public ContextData getClone() {
		ContextDataBoolean result = new ContextDataBoolean(this.tag);
		result.value = new Boolean(value);
		return result;
	}

	@Override
	public void wipeItem() {
		value = new Boolean(false);
	}

	@Override
	public JSONObject createJSONObject() {
		return getJSONValue(value,tag);
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) {
		value = getBooleanValueOfJSONO(jsonObject,tag);
	}
	
	public void setValue(Boolean str) {
		this.value = str;
	}

	public Boolean getValue() {
		return this.value;
	}
}