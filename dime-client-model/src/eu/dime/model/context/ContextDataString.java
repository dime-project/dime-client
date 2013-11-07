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

import java.util.List;
import java.util.Vector;

import sit.json.JSONObject;

public class ContextDataString extends ContextData {
	
	private String value = new String();
    private String tag;
    
    public ContextDataString(String tag) {
        this.tag = tag;
    }

	@Override
	public ContextData getClone() {
		ContextDataString result = new ContextDataString(this.tag);
		result.value = new String(value);
		return result;
	}

	@Override
	public void wipeItem() {
		this.value = new String();
	}

	@Override
	public JSONObject createJSONObject() {
		return getJSONValue(value,tag);
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) {
		value = getStringValueOfJSONO(jsonObject,tag);
	}
	
	public void setValue(String str) {
		this.value = str;
	}

	public String getValue() {
		return this.value;
	}
}
