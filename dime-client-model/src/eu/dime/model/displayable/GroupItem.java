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
public final class GroupItem extends DisplayableItem implements AgentItem {

    public static final String GROUP_TYPE_TAG = "nao:creator";
    public static final String TRUST_LEVEL_TAG = "nao:trustLevel";
    public final static String[] VALID_GROUP_TYPE_VALUES = new String[]{"urn:auto-generated", "service-generated", "user-generated"};
    private Double trustLevel = DimeHelper.DEFAULT_INITIAL_TRUST_LEVEL;
    private String groupType;

    public GroupItem() {
        wipeItemForDisplayItem();
    }

    public GroupItem(String guid) {
        super(guid);
        wipeItemForDisplayItem();
    }

    @Override
    protected final void wipeItemForDisplayItem() {
        trustLevel = DimeHelper.DEFAULT_INITIAL_TRUST_LEVEL;
        groupType = "";
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        GroupItem result = new GroupItem();
        result.trustLevel = this.trustLevel;
        result.groupType = this.groupType;
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
        this.trustLevel = getDoubleValueOfJSONO(jsonObject, TRUST_LEVEL_TAG);
        this.groupType = getStringValueOfJSONO(jsonObject, GROUP_TYPE_TAG);
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(trustLevel, TRUST_LEVEL_TAG));
        newJSONObject.addChild(getJSONValue(groupType, GROUP_TYPE_TAG));
        return newJSONObject;
    }

    public Double getTrustLevel() {
        return trustLevel;
    }

    public void setTrustLevel(Double trustLevel) {
        this.changed = true;
        this.trustLevel = trustLevel;
    }

    /**
     * @return the groupType
     */
    public String getGroupType() {
        return groupType;
    }

    /**
     * @param groupType the groupType to set
     */
    public void setGroupType(String groupType) {
        this.changed = true;
        this.groupType = groupType;
    }

    public boolean isEditable() {
        boolean editable = (groupType.equals(VALID_GROUP_TYPE_VALUES[0]) || groupType.equals(VALID_GROUP_TYPE_VALUES[1])) ? false : true;
        return editable;
    }
    
}
