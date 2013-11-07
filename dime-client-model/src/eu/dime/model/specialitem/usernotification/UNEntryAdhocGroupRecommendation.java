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
package eu.dime.model.specialitem.usernotification;

import eu.dime.model.InvalidJSONItemException;
import sit.json.JSONObject;

/**
 *
 * @author simon
 */
public class UNEntryAdhocGroupRecommendation extends UnEntry {

    public static final String GUID = "guid";
    public static final String CREATOR = "nao:creator";
    public static final String NAME = "name";
    private String guid;
    private String name;
    private String creator;

    public UNEntryAdhocGroupRecommendation() {
        wipeItem();
    }

    @Override
    public UnEntry getClone() {
        UNEntryAdhocGroupRecommendation newUnEntry = new UNEntryAdhocGroupRecommendation();
        newUnEntry.guid = this.guid;
        newUnEntry.name = this.name;
        newUnEntry.creator = this.creator;
        return newUnEntry;
    }

    @Override
    protected final void wipeItem() {
        this.guid = "";
        this.name = "";
        this.creator = "";
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject newJSONObject = new JSONObject("0");
        newJSONObject.addChild(getJSONValue(guid, GUID));
        newJSONObject.addChild(getJSONValue(name, NAME));
        newJSONObject.addChild(getJSONValue(creator, CREATOR));
        return newJSONObject;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        wipeItem();
        this.guid = getStringValueOfJSONO(jsonObject, GUID);
        this.name = getStringValueOfJSONO(jsonObject, NAME);
        this.creator = getStringValueOfJSONO(jsonObject, CREATOR);
    }

    public String getGuid() {
        return guid;
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    @Override
    public UN_TYPE getUnType() {
        return UN_TYPE.ADHOC_GROUP_RECOMMENDATION;
    }
}
