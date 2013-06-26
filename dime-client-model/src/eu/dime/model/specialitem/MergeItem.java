/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.specialitem;

import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.json.JSONPathAccessException;

/**
 *
 * @author simon
 */
public class MergeItem extends JSONItem<MergeItem> {
    
    /*
     * 
     * "guid":"<UUID for transaction reference>",
        "items": [
          [<personID1>,<personID2>,<personID3>],
          [<personID4>,<personID6>]
        ]
     */

    
    public static final String GUID_TAG = "guid";
    public static final String MERGED_ITEMS_TAG = "items";
    
    protected String guid;
    private List<List<String>> mergedItems = new Vector();
    
    public MergeItem(String guid) {
        this.guid = guid;
    }

    @Override
    public MergeItem getClone() {
        MergeItem result = new MergeItem(guid);
        for (List<String> person : mergedItems){
            result.mergedItems.add(new Vector<String>(person));
        }
        
        return result;
    }

    @Override
    protected void wipeItem() {
        this.guid = "";
        mergedItems.clear();
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(this.guid, GUID_TAG));   
        
        //merged persons
        JSONObject mergedItemsJSON = new JSONObject(MERGED_ITEMS_TAG);
        
        for (List<String> itemGuids : mergedItems){
            JSONObject itemJSON = new JSONObject("");
            for (String itemGuid: itemGuids){
                itemJSON.addItem(new JSONObject("", itemGuid, true));
            }
            mergedItemsJSON.addItem(itemJSON);
        }
        
        result.addChild(mergedItemsJSON);
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        // clean up first
        wipeItem();

        // read the json
        this.guid = getStringValueOfJSONO(jsonObject, GUID_TAG);
        if (guid==null || guid.length() == 0) {
            throw new InvalidJSONItemException("InvalidJSONItem: guid field missing!");
        }
        try {
            JSONObject mergedItemsJSON = jsonObject.getChild(MERGED_ITEMS_TAG);
            for (Entry<String, JSONObject> itemJson :  mergedItemsJSON){
                Vector<String> itemGuids = new Vector();
                for (Entry<String, JSONObject> itemGuidJSON :  itemJson.getValue()){
                    itemGuids.add(itemGuidJSON.getValue().getValue());
                }
                mergedItems.add(itemGuids);
            }
            
            
            
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(MergeItem.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    public void addMergedItem(String[] itemGuids){
        Vector<String> myItemGuids = new Vector(itemGuids.length);
        for (String guid : itemGuids){
            myItemGuids.add(guid);                    
        }
        mergedItems.add(myItemGuids);
    }
    
    public void addMergedItem(List<String> itemGuids){
        mergedItems.add(itemGuids);
    }

}
