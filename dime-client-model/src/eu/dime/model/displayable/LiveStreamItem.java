/*
 *  Description of PersonItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.05.2012
 */
package eu.dime.model.displayable;

import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public final class LiveStreamItem extends DisplayableItem{

    
    public LiveStreamItem() {
    }

    public LiveStreamItem(String guid) {
        super(guid);
    }

    
    
    @Override
    protected void wipeItemForDisplayItem() {
       //nothing to be done here
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        LiveStreamItem result = new LiveStreamItem();
        
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
        //nothing to be done here
        
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
       
        return newJSONObject;
    }

}
