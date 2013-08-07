/*
 *  Description of ContextItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 27.04.2012
 */
package eu.dime.model.context;

import eu.dime.model.GenItem;
import eu.dime.model.JSONItem;
import eu.dime.model.context.ContextData;
import eu.dime.model.context.ContextEntity;
import eu.dime.model.context.ContextDataFactory;
import eu.dime.model.context.ContextSource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.sstl.StringEnumMap;

/**
 *
 * @author Simon Thiel
 */
public class ContextItem extends GenItem {

    public static enum CONTEXT_ITEM_FIELDS {
        TIMESTAMP, EXPIRES, SCOPE, ENTITY, CONTEXTSOURCE, DATAPART
    };
    public static final StringEnumMap<CONTEXT_ITEM_FIELDS> ContextItemFieldMap = new StringEnumMap<CONTEXT_ITEM_FIELDS>(CONTEXT_ITEM_FIELDS.class, CONTEXT_ITEM_FIELDS.values(),
		    new String[]{"timestamp", "expires", "scope", "entity", "source", "dataPart"});
    private final String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
    private long timestamp = -1;
    private long expires = -1;
    private String scope = "";
    private ContextEntity entity = new ContextEntity();
    private ContextSource contextSource = new ContextSource();
    private Map<String, ContextData> dataPart = new HashMap<String, ContextData>();

    public ContextItem() { }

    public ContextItem(String guid) {
        super(guid);
    }

    @Override
    protected void wipeItemForItem() {
        //contextItem
        timestamp = -1;
        expires = -1;
        scope = "";
        entity.wipeItem();
        contextSource.wipeItem();
        dataPart.clear();
    }

    @Override
    protected GenItem getCloneForItem() {
        ContextItem result = new ContextItem();
        //context item
        result.timestamp = this.timestamp;
        result.expires = this.expires;
        result.scope = this.scope;
        result.entity = this.entity.getClone();
        result.contextSource = this.contextSource.getClone();
        //get a copy of data part
        for (Map.Entry<String, ContextData> entry : this.dataPart.entrySet()) {
            result.dataPart.put(entry.getKey(), entry.getValue().getClone());
        }
        return result;
    }

    private long getTimeStampFromString(String dateStr) {
        try {
        	dateStr = dateStr.substring(0,dateStr.length()-3) + dateStr.substring(dateStr.length()-2,dateStr.length());
            return dateFormat.parse(dateStr).getTime();
        } catch (ParseException ex) {
            Logger.getLogger(ContextItem.class.getName()).log(Level.WARNING, "Unparseable date:" + dateStr);
        }
        return -1;
    }
    
    public String getFormattedTimestamp(long millis) {
//    	long timezoneOffset = TimeZone.getDefault().getOffset(millis);
//    	Date date = new Date(millis - timezoneOffset);
    	String result = dateFormat.format(new Date(millis));
    	result = result.substring(0, result.length()-2) + ":" + result.substring(result.length()-2, result.length());
    	return result;
    }

    @Override
    public void readJSONObjectForItem(JSONObject jsonObject) {        
        //ContextItem
        this.timestamp = getTimeStampFromString(getStringValueOfJSONO(jsonObject, ContextItemFieldMap.get(CONTEXT_ITEM_FIELDS.TIMESTAMP)));
        this.expires = getTimeStampFromString(getStringValueOfJSONO(jsonObject, ContextItemFieldMap.get(CONTEXT_ITEM_FIELDS.EXPIRES)));
        this.scope = getStringValueOfJSONO(jsonObject, ContextItemFieldMap.get(CONTEXT_ITEM_FIELDS.SCOPE));
        this.entity.readJSONObject(getItemOfJSONO(jsonObject, ContextItemFieldMap.get(CONTEXT_ITEM_FIELDS.ENTITY)));
        this.contextSource.readJSONObject(getItemOfJSONO(jsonObject, ContextItemFieldMap.get(CONTEXT_ITEM_FIELDS.CONTEXTSOURCE)));
        //data
        JSONObject dataEntries = getItemOfJSONO(jsonObject, ContextItemFieldMap.get(CONTEXT_ITEM_FIELDS.DATAPART));
        if (dataEntries != null) {
            for (Map.Entry<String, JSONObject> entry : dataEntries) {
                dataPart.put(entry.getKey(), ContextDataFactory.createContextData(entry.getKey(), dataEntries/*entry.getValue()*/));
            }
        }
    }

    protected JSONObject getJSONValue(String value, CONTEXT_ITEM_FIELDS field) {
        return getJSONValue(value, ContextItemFieldMap.get(field));
    }

    @SuppressWarnings("rawtypes")
	protected JSONObject getJSONObject(JSONItem item, CONTEXT_ITEM_FIELDS field) {
        return getJSONObject(item, ContextItemFieldMap.get(field));
    }

    @Override
    public JSONObject createJSONObjectForItem(JSONObject newJSONObject) {
        //ContextItem
        newJSONObject.addChild(getJSONValue(getFormattedTimestamp(this.timestamp), CONTEXT_ITEM_FIELDS.TIMESTAMP));
        newJSONObject.addChild(getJSONValue(getFormattedTimestamp(this.expires), CONTEXT_ITEM_FIELDS.EXPIRES));
        newJSONObject.addChild(getJSONValue(this.scope, CONTEXT_ITEM_FIELDS.SCOPE));
        newJSONObject.addChild(getJSONObject(this.entity, CONTEXT_ITEM_FIELDS.ENTITY));
        newJSONObject.addChild(getJSONObject(this.contextSource, CONTEXT_ITEM_FIELDS.CONTEXTSOURCE));
        //data
        JSONObject dataEntries = new JSONObject(ContextItemFieldMap.get(CONTEXT_ITEM_FIELDS.DATAPART));
        for (Map.Entry<String, ContextData> entry : dataPart.entrySet()) {
            dataEntries.addChild(getJSONObject(entry.getValue(), entry.getKey()));
        }
        newJSONObject.addChild(dataEntries);
        return newJSONObject;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        this.changed = true;
    }

    /**
     * @return the expires
     */
    public long getExpires() {
        return expires;
    }

    /**
     * @param expires the expires to set
     */
    public void setExpires(long expires) {
        this.expires = expires;
        this.changed = true;
    }

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
        this.changed = true;
    }

    /**
     * @return the entity
     */
    public ContextEntity getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(ContextEntity entity) {
        this.entity = entity;
        this.changed = true;
    }

    /**
     * @return the contextSource
     */
    public ContextSource getContextSource() {
        this.changed = true; // set changed = true since its not clear whether the items have been changed after returning them
        return contextSource;
    }

    /**
     * @param contextSource the contextSource to set
     */
    public void setContextSource(ContextSource contextSource) {
        this.contextSource = contextSource;
        this.changed = true;
    }

    /**
     * @return the dataPart
     */
    public ContextData getDataPart(String key) {
        this.changed = true; // set changed = true since its not clear whether the items have been changed after returning them
        return dataPart.get(key);
    }

    /**
     * @param dataPart the dataPart to set
     */
    public void addDataPart(String key, ContextData dataPart) {
        this.changed = true; 
        this.dataPart.put(key, dataPart);
    }
    
}