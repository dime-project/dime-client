/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.specialitem.usernotification;

import eu.dime.model.GenItem;
import eu.dime.model.InvalidJSONItemException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.json.JSONPathAccessException;
import sit.sstl.StrictSITEnumMap;

/**
 * specified in
 * https://confluence.deri.ie:8443/display/digitalme/UserNotifications
 *
 *
 * "guid":"186c569e-9cdf-487d-ac81-650b2a828933", "type":"usernotification",
 * "created":1338824999, "lastModified":1338824999, "name":"text that is shown
 * in notification bar; less then 10 chars", "read":true/false, //read by the
 * user "unType":"one of the types specified below", "unEntry":{ //type specific
 * object }
 *
 * @author simon
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class UserNotificationItem extends GenItem {

    private static final String CREATED_TAG = "created";
    private static final String LAST_MODIFIED_TAG = "lastModified";
    private static final String NAME_TAG = "name";
    private static final String USERID_TAG = "userId";
    private static final String READ_TAG = "read";
    private static final String UN_TYPE_TAG = "unType";
    private static final String UN_ENTRY_TAG = "unEntry";
	public static final StrictSITEnumMap<UN_TYPE, UNTypeEntry> UNTypesMap = new StrictSITEnumMap<UN_TYPE, UNTypeEntry>(UN_TYPE.class, new UNTypeEntry[]{
                new UNTypeEntry<UNEntryMergeRecommendation>(UN_TYPE.MERGE_RECOMMENDATION, "merge_recommendation", UNEntryMergeRecommendation.class),
                new UNTypeEntry<UNEntrySituationRecommendation>(UN_TYPE.SITUATION_RECOMMENDATION, "situation_recommendation", UNEntrySituationRecommendation.class),
                new UNTypeEntry<UNEntryAdhocGroupRecommendation>(UN_TYPE.ADHOC_GROUP_RECOMMENDATION, "adhoc_group_recommendation", UNEntryAdhocGroupRecommendation.class),
                new UNTypeEntry<UNEntryRefToItem>(UN_TYPE.REF_TO_ITEM, "ref_to_item", UNEntryRefToItem.class),
                new UNTypeEntry<UNEntryMessage>(UN_TYPE.MESSAGE, "message", UNEntryMessage.class)
            });
    private long created;
    private long lastModified;
    private String name;
    private String userId;
    private boolean read;
    private UnEntry unEntry;

    public UserNotificationItem() {
        this.wipeItemForItem();
    }

    @Override
    protected final void wipeItemForItem() {
        created = System.currentTimeMillis();
        lastModified = created;
        name = "";
        userId="";
        read = false;
        unEntry = null;
    }

    @Override
    protected GenItem getCloneForItem() {
        UserNotificationItem result = new UserNotificationItem();
        result.created = this.created;
        result.lastModified = this.lastModified;
        result.name = this.name;
        result.userId=this.userId;
        result.read = this.read;
        if (this.unEntry == null) {
            result.unEntry = null;
        } else {
            result.unEntry = this.unEntry.getClone();
        }
        return result;
    }

    @Override
    public void readJSONObjectForItem(JSONObject jsonObject) {
        //cleanup first
        this.wipeItemForItem();
        this.created = getLongValueOfJSONO(jsonObject, CREATED_TAG);
        this.lastModified = getLongValueOfJSONO(jsonObject, LAST_MODIFIED_TAG);
        this.name = getStringValueOfJSONO(jsonObject, NAME_TAG);
        this.userId = getStringValueOfJSONO(jsonObject, USERID_TAG);
        this.read = getBooleanValueOfJSONO(jsonObject, READ_TAG);
        UN_TYPE unType = getUNTypeFromString(getStringValueOfJSONO(jsonObject, UN_TYPE_TAG));
        if (unType != UN_TYPE.UNDEFINED) {
            this.unEntry = UNTypesMap.get(unType).getInstance();
            try {
                this.unEntry.readJSONObject(jsonObject.getChild(UN_ENTRY_TAG));
            } catch (InvalidJSONItemException ex) {
                Logger.getLogger(UserNotificationItem.class.getName()).log(Level.SEVERE, null, ex);

            } catch (JSONPathAccessException ex) {
                Logger.getLogger(UserNotificationItem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    protected JSONObject createJSONObjectForItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(created, CREATED_TAG));
        newJSONObject.addChild(getJSONValue(lastModified, LAST_MODIFIED_TAG));
        newJSONObject.addChild(getJSONValue(name, NAME_TAG));
        newJSONObject.addChild(getJSONValue(userId, USERID_TAG));
        newJSONObject.addChild(getJSONValue(read, READ_TAG));
        if (unEntry == null) {
            newJSONObject.addChild(getJSONValue("undefined", UN_TYPE_TAG));
            newJSONObject.addChild(new JSONObject(UN_ENTRY_TAG));
        } else { 
            newJSONObject.addChild(getJSONValue(getStringFromUNType(unEntry.getUnType()), UN_TYPE_TAG)); 
            newJSONObject.addChild(getJSONObject(unEntry, UN_ENTRY_TAG));
        }
        return newJSONObject;
    }

	public static UN_TYPE getUNTypeFromString(String unTypeStr) {
        for (UN_TYPE unType : UN_TYPE.values()) {
            UNTypeEntry<UnEntry> entry = UNTypesMap.get(unType);
            if (entry.tag.equals(unTypeStr)) {
                return unType;
            }
        }
        return UN_TYPE.UNDEFINED;
    }

    public static String getStringFromUNType(UN_TYPE unType) {
        return UNTypesMap.get(unType).tag;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public long getCreated() {
        return created;
    }

    public long getLastModified() {
        return lastModified;
    }

    public String getName() {
        return name;
    }

    public UN_TYPE getUnType() {
        return unEntry.getUnType();
    }

    public UnEntry getUnEntry() {
        return unEntry;
    }

    public void setUnEntry(UnEntry unEntry) {
        this.unEntry = unEntry;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
}
