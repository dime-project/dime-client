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
 *  Description of PersonItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.05.2012
 */
package eu.dime.model.displayable;

import eu.dime.restapi.DimeHelper;
import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public final class DataboxItem extends DisplayableItem implements ShareableItem {

    public static final String PRIVACY_LEVEL_TAG = "nao:privacyLevel";
    private Double privacyLevel = DimeHelper.DEFAULT_INITIAL_PRIVACY_LEVEL;
    
    public DataboxItem() { }

    public DataboxItem(String guid) {
        super(guid);
    }
    
    @Override
    protected void wipeItemForDisplayItem() {
        this.privacyLevel = DimeHelper.DEFAULT_INITIAL_PRIVACY_LEVEL;
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        DataboxItem result = new DataboxItem();
        result.setPrivacyLevel(this.getPrivacyLevel());
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
        this.setPrivacyLevel(getDoubleValueOfJSONO(jsonObject, PRIVACY_LEVEL_TAG));
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(getPrivacyLevel(), PRIVACY_LEVEL_TAG));
        return newJSONObject;
    }

	public Double getPrivacyLevel() {
		return privacyLevel;
	}

	public void setPrivacyLevel(Double privacyLevel) {
		changed = true;
		this.privacyLevel = privacyLevel;
	}

}
