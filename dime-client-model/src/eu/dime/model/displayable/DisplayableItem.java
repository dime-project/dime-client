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
 *  Description of DisplayableItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 27.04.2012
 */
package eu.dime.model.displayable;

import eu.dime.model.GenItem;
import java.util.List;
import java.util.Vector;
import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public abstract class DisplayableItem extends GenItem {

    protected String name = "";
    protected String imageUrl = "";
    protected String userId = "";
    protected List<String> items = new Vector<String>();
    private long lastUpdated = System.currentTimeMillis();    

    public DisplayableItem() { }

    public DisplayableItem(String guid) {
        super(guid);
    }

    protected abstract void wipeItemForDisplayItem();

    protected void wipeItemForItem() {        
        name = "";
        imageUrl = "";        
        userId = "";
        items.clear();   
        lastUpdated = System.currentTimeMillis();
        wipeItemForDisplayItem();
    }
    
    protected abstract DisplayableItem getCloneForDisplayItem();
    
    @Override
    protected GenItem getCloneForItem() {
        DisplayableItem result = getCloneForDisplayItem();
        result.lastUpdated = this.lastUpdated;
        result.name = this.name;
        result.imageUrl = this.imageUrl;
        result.userId = this.userId;
        result.items = new Vector<String>(this.items);
        return result;
    }
    
    public abstract void readJSONObjectForDisplayItem(JSONObject jsonObject);

    public void readJSONObjectForItem(JSONObject jsonObject) {
        this.lastUpdated = getLongValueOfJSONO(jsonObject,GenItemFieldMap.get(GEN_ITEM_FIELDS.LAST_UPDATE));
        this.name = getStringValueOfJSONO(jsonObject, GenItemFieldMap.get(GEN_ITEM_FIELDS.NAME));
        this.imageUrl = getStringValueOfJSONO(jsonObject, GenItemFieldMap.get(GEN_ITEM_FIELDS.IMAGEURL));
        this.userId = getStringValueOfJSONO(jsonObject, GenItemFieldMap.get(GEN_ITEM_FIELDS.USER_ID));
        this.items.addAll(getStringListOfJSONObject(jsonObject, GenItemFieldMap.get(GEN_ITEM_FIELDS.ITEMS)));  
        
         //call display-item version of this method
        readJSONObjectForDisplayItem(jsonObject);
    }
    
    protected abstract JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject);
    

    protected JSONObject createJSONObjectForItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(this.lastUpdated, GenItemFieldMap.get(GEN_ITEM_FIELDS.LAST_UPDATE)));
        newJSONObject.addChild(getJSONValue(this.name, GEN_ITEM_FIELDS.NAME));
        newJSONObject.addChild(getJSONValue(this.imageUrl, GEN_ITEM_FIELDS.IMAGEURL));
        newJSONObject.addChild(getJSONValue(this.userId, GEN_ITEM_FIELDS.USER_ID));
        newJSONObject.addChild(getJSONCollection(this.items, GEN_ITEM_FIELDS.ITEMS));
        //call item version of this method
        newJSONObject = createJSONObjectForDisplayItem(newJSONObject);   
        return newJSONObject;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.changed = true;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.changed = true;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String pid) {
        this.userId = pid;
        this.changed = true;
    }

    /**
     * returns the items of the object
     * @return
     */
    public List<String> getItems() {
        this.changed = true; //we need to set changed = true since its not clear whether the items have been changed after returning them
        return items;
    }
    
    /**
     * returns true if the items collection of the DisplayableItem contains an 
     * item equals to the given item (which normally is the guid of a child)
     * @param item (typically the guid of a child item)
     * @return 
     */
    public boolean containsItem(String item){
        return this.items.contains(item);
    }

    public void setItems(List<String> items) {
        this.changed = true;
        this.items = items;
    }


    @Override
    public String toString() {
        String result = "[" + guid + "]" + name + "(" + getType() + ")";
        if (!items.isEmpty()) {
            result += "contains " + items.size() + " items";
        }
        return result;
    }
    
    public void removeItems(List<String> guids){
    	for (String guid : guids) {
			removeItem(guid);
		}
    }

    public boolean removeItem(String guid) {
        return items.remove(guid);
    }
    
    public void addItems(List<String> guids) {
    	for (String guid : guids) {
    		if(!containsItem(guid)) {
    			addItem(guid);
    		}
		}
    }

    public boolean addItem(String guid) {
        return items.add(guid);
    }

    /**
     * @return the lastUpdated
     */
    public long getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @param lastUpdated the lastUpdated to set
     */
    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
  
}
