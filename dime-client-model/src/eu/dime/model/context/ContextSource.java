/*
 *  Description of ContextSource
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 03.05.2012
 */
package eu.dime.model.context;

import eu.dime.model.JSONItem;
import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public class ContextSource extends JSONItem<ContextSource> {

    public final static String ID_FIELD = "id";
    public final static String VERSION_FIELD = "v";
    public String id = "";
    public String version = "";

    public ContextSource() {
    }

    @Override
    public ContextSource getClone() {
        ContextSource result = new ContextSource();
        result.id = this.id;
        result.version = this.version;
        return result;
    }

    @Override
    public void wipeItem() {
        id = "";
        version = "";
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(id, ID_FIELD));
        result.addChild(getJSONValue(version, VERSION_FIELD));
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject){
        if (jsonObject==null){
            return;
        }
        this.id = getStringValueOfJSONO(jsonObject, ID_FIELD);
        this.version = getStringValueOfJSONO(jsonObject, VERSION_FIELD);
    }
}
