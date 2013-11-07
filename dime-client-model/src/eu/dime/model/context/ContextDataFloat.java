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

public class ContextDataFloat extends ContextData {
	
	private Float value;
    private String tag;
    
    public ContextDataFloat(String tag) {
        this.tag = tag;
    }

	@Override
	public ContextData getClone() {
		ContextDataFloat result = new ContextDataFloat(this.tag);
		result.value = new Float(value);
		return result;
	}

	@Override
	public void wipeItem() {
		value = new Float(-1);
	}

	@Override
	public JSONObject createJSONObject() {
		return getJSONValue(value,tag);
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) {
		value = getFloatValueOfJSONO(jsonObject,tag);
	}
	
	public void setValue(Float str) {
		this.value = str;
	}
	
	public Float getValue() {
		return this.value;
	}

}
