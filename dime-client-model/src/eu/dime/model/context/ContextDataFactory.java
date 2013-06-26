/*
 *  Description of ContextDataFactory
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 03.05.2012
 */
package eu.dime.model.context;

import eu.dime.model.context.constants.Scopes;
import java.util.HashSet;
import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public class ContextDataFactory {

    private final static HashSet<String> STRING_LIST_ITEMS = new HashSet() {

        {
            add(Scopes.SCOPE_WF_NAMES);
            add(Scopes.SCOPE_WF_LIST);
        }
    };
    private final static HashSet<String> STRING_ITEMS = new HashSet() {

        {
            add(Scopes.SCOPE_BT_LIST);
            add(Scopes.SCOPE_BT_LOCAL);
            add(Scopes.SCOPE_POSITION_LOCMODE);
            add(Scopes.SCOPE_CURRENT_EVENT_ID);
            add(Scopes.SCOPE_CURRENT_EVENT_NAME);
            add(Scopes.SCOPE_CURRENT_PLACE_ID);
            add(Scopes.SCOPE_CURRENT_PLACE_NAME);
            add(Scopes.SCOPE_CURRENT_SITUATION);
            add(Scopes.SCOPE_STATUS_LAST_ACTIVE);
        }
    };
    private final static HashSet<String> INT_LIST_ITEMS = new HashSet() {

        {
            add(Scopes.SCOPE_WF_SIGNALS);
        }
    };
    private final static HashSet<String> DOUBLE_ITEMS = new HashSet() {

        {
            add(Scopes.SCOPE_POSITION_LAT);
            add(Scopes.SCOPE_POSITION_LON);
        }
    };
    private final static HashSet<String> FLOAT_ITEMS = new HashSet() {

        {
            add(Scopes.SCOPE_POSITION_ACC);
        }
    };
    
    private final static HashSet<String> BOOLEAN_ITEMS = new HashSet() {

        {
            add(Scopes.SCOPE_STATUS_KEEPALIVE);
            add(Scopes.SCOPE_STATUS_IS_ALIVE);
        }
    };

    public static ContextData createContextData(String key) {
        if (STRING_LIST_ITEMS.contains(key)) {
            return new ContextDataStringList(key);
        } else if (STRING_ITEMS.contains(key)) {
            return new ContextDataString(key);
        } else if (INT_LIST_ITEMS.contains(key)) {
            return new ContextDataIntegerList(key);
        } else if (DOUBLE_ITEMS.contains(key)) {
            return new ContextDataDouble(key);
        } else if (FLOAT_ITEMS.contains(key)) {
            return new ContextDataFloat(key);
        } else if (BOOLEAN_ITEMS.contains(key)) {
            return new ContextDataBoolean(key);
        }

        throw new UnsupportedOperationException("Not yet implemented! key:"+key);
    }

    public static ContextData createContextData(String key, JSONObject contextDataJSONObject) {

        ContextData result = createContextData(key);

        result.readJSONObject(contextDataJSONObject);
        return result;

    }
}
