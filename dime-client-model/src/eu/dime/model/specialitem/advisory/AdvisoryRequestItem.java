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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.specialitem.advisory;

import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;
import java.util.List;
import java.util.Vector;
import sit.json.JSONObject;

/**
 *
 * @author christian
 */
public class AdvisoryRequestItem extends JSONItem<AdvisoryRequestItem> {

    public static final String AGENT_GUIDS_TAG = "agentGuids";
    public static final String SHAREABLE_ITEMS = "shareableItems";
    public static final String PROFILE_GUID = "profileGuid";
    
    protected String profileGuid;
    protected List<String> agentGuids = new Vector<String>();
    protected List<String> shareableItems = new Vector<String>();
    
    public AdvisoryRequestItem(JSONObject jsonObject) throws InvalidJSONItemException {
        this.readJSONObject(jsonObject);
    }

    public AdvisoryRequestItem(String profileGuid, List<String> agentGuids, List<String> shareableItems) {
		this.profileGuid = profileGuid;
		this.agentGuids = agentGuids;
		this.shareableItems = shareableItems;
	}

	@Override
    public AdvisoryRequestItem getClone() {
        return new AdvisoryRequestItem(profileGuid, agentGuids, shareableItems);
    }

    @Override
    protected void wipeItem() {
    	this.profileGuid = "";
    	this.agentGuids = new Vector<String>();
    	this.shareableItems = new Vector<String>();
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(this.profileGuid, PROFILE_GUID));
        result.addChild(getJSONCollection(this.agentGuids, AGENT_GUIDS_TAG));
        result.addChild(getJSONCollection(this.shareableItems, SHAREABLE_ITEMS));
        return result;
    }

    @Override
    final public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        // clean up first
        wipeItem();

        // read the json
        this.profileGuid = getStringValueOfJSONO(jsonObject, PROFILE_GUID);
        this.agentGuids = getStringListOfJSONObject(jsonObject, AGENT_GUIDS_TAG);
        this.shareableItems = getStringListOfJSONObject(jsonObject, SHAREABLE_ITEMS);
    }
}
