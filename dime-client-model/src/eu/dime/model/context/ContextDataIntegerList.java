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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import sit.json.JSONObject;

public class ContextDataIntegerList extends ContextData {
	
	private List<Integer> list = new Vector();
    private String tag ;

    public ContextDataIntegerList(String tag) {
        this.tag = tag;
    }

	@Override
	public ContextData getClone() {
		ContextDataIntegerList result = new ContextDataIntegerList(this.tag);
        result.list = new Vector(list);
        return result;
	}

	@Override
	public void wipeItem() {
		list.clear();
	}

	@Override
	public JSONObject createJSONObject() {
		return getJSONCollectionFromIntegers(list,tag);
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) {
		list = getIntegerListOfJSONCollection(jsonObject);
	}
	
	public void addInt(Integer i) {
		list.add(i);
	}

}
