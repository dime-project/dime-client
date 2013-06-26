/*
 *  Description of ContextData
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
public abstract class ContextData extends JSONItem<ContextData> {

    public abstract ContextData getClone();

    public abstract void wipeItem();

    public abstract JSONObject createJSONObject();

    public abstract void readJSONObject(JSONObject jsonObject);
}
