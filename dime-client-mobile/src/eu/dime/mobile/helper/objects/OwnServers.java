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

package eu.dime.mobile.helper.objects;

import java.util.ArrayList;
import java.util.List;
import sit.json.JSONObject;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;

public class OwnServers extends JSONItem<OwnServers> {
	
	private static final String OWN_SERVERS = "OWN_SERVERS";
	
	List<String> ownServers = new ArrayList<String>();

	@Override
	public OwnServers getClone() {
		return null;
	}

	@Override
	protected void wipeItem() {
		ownServers = new ArrayList<String>();
	}

	@Override
	public JSONObject createJSONObject() {
		JSONObject result = new JSONObject("0");
		result.addChild(getJSONCollection(this.ownServers, OWN_SERVERS));
		return result;
	}

	@Override
	public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
		this.ownServers.addAll(getStringListOfJSONObject(jsonObject, OWN_SERVERS));
	}
	
	public void addOwnServer(String server) {
		ownServers.add(server);
	}
	
	public boolean removeOwnServer(String server) {
		return ownServers.remove(server);
	}
	
	public List<String> getOwnServers() {
		return ownServers;
	}

}
