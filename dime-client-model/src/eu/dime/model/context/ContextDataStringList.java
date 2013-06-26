/*
 *  Description of ContextDataStringList
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 03.05.2012
 */
package eu.dime.model.context;

import java.util.List;
import java.util.Vector;
import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public class ContextDataStringList extends ContextData {

    private List<String> list = new Vector();
    private String tag ;

    public ContextDataStringList(String tag) {
        this.tag = tag;
    }

    
    
    
    @Override
    public ContextData getClone() {
        ContextDataStringList result = new ContextDataStringList(this.tag);
        result.list = new Vector(list);
        return result;
    }

    @Override
    public void wipeItem() {
        list.clear();
    }

    @Override
    public JSONObject createJSONObject() {
        return getJSONCollection(list, tag);
               

    }

    @Override
    public void readJSONObject(JSONObject jsonObject){
        list = getStringListOfJSONCollection(jsonObject);
    }

    public void addString(String myStr) {
        list.add(myStr);
    }
    

}
