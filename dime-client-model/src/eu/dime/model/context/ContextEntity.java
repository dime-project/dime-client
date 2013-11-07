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
 *  Description of ContextEntity
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 03.05.2012
 */
package eu.dime.model.context;

import eu.dime.model.JSONItem;
import eu.dime.model.ModelTypeNotFoundException;
import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public class ContextEntity extends JSONItem<ContextEntity> {

    public final static String ID_FIELD = "id";
    public final static String TYPE_FIELD = "type";
    public String id = "";
    public String type = "";

    public ContextEntity() {
    }

    @Override
    public ContextEntity getClone() {
        ContextEntity result = new ContextEntity();
        result.id = this.id;
        result.type = this.type;
        return result;
    }

    @Override
    public void wipeItem() {
        this.id = "";
        this.type = "";
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(id, ID_FIELD));
        result.addChild(getJSONValue(type, TYPE_FIELD));
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject)  {
        if (jsonObject==null){
            return;
        }
        this.id = getStringValueOfJSONO(jsonObject, ID_FIELD);
        this.type = getStringValueOfJSONO(jsonObject, TYPE_FIELD);
    }
}
