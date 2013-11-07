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

import java.util.List;
import java.util.Vector;
import sit.json.JSONObject;
import eu.dime.model.InvalidJSONItemException;

public class WarningAttributesProfileNotShared extends WarningAttributesObject {
	
	public static final String PERSON_GUIDS = "personGuids";
	
	private List<String> personGuids;

    public WarningAttributesProfileNotShared() {
        wipeItem();
    }
    
    public WarningAttributesProfileNotShared(List<String> personGuids) {
        this.personGuids = personGuids;
    }

    @Override
    public WarningAttributesProfileNotShared getClone() {
        return new WarningAttributesProfileNotShared(personGuids);
    }

    @Override
    protected final void wipeItem() {
        this.personGuids = new Vector<String>();
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONCollection(personGuids, PERSON_GUIDS));
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        // clean up first
        wipeItem();

        // read the json
        this.personGuids = getStringListOfJSONObject(jsonObject, PERSON_GUIDS);
    }

    public List<String> getPersonGuids() {
        return personGuids;
    }

}
