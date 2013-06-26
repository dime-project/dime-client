/*
 *  Description of PersonItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.05.2012
 */
package eu.dime.model.displayable;

import java.util.List;
import java.util.Vector;
import sit.json.JSONObject;
import sit.sstl.StringEnumMap;

/**
 *
 * @author Simon Thiel
 */
public final class EventItem extends DisplayableItem {

    public static enum EVENT_ITEM_FIELDS {

        DT_START, DT_END, DESCRIPTION, HAS_LOCATION, ATTENDEE, EVENT_STATUS
    };
    public static final StringEnumMap<EVENT_ITEM_FIELDS> EventItemFieldMap =
            new StringEnumMap(EVENT_ITEM_FIELDS.class, EVENT_ITEM_FIELDS.values(),
            new String[]{"ncal:dtstart", "ncal:dtend", "nao:description", "pimo:hasLocation", "pimo:attendee", "ncal:eventStatus"});
    private Long dtstart;
    private Long dtend;
    private String description;
    private String hasLocation;
    private List<String> attendee;
    private String eventStatus;

    public EventItem() {
        wipeItemForDisplayItem();
    }

    public EventItem(String guid) {
        super(guid);
        wipeItemForDisplayItem();
    }

    @Override
    protected final void wipeItemForDisplayItem() {
        dtstart = new Long(-1);
        dtend = new Long(-1);
        description = "";
        hasLocation = "";
        attendee = new Vector();
        eventStatus = "";
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        EventItem result = new EventItem();
        result.dtstart = this.dtstart;
        result.dtend = this.dtend;
        result.description = this.description;
        
        result.hasLocation = this.hasLocation;
        result.attendee = new Vector(this.attendee);
        result.eventStatus = this.eventStatus;
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
        
        this.dtstart = getLongValueOfJSONO(jsonObject, EventItemFieldMap.get(EVENT_ITEM_FIELDS.DT_START));
        this.dtend = getLongValueOfJSONO(jsonObject, EventItemFieldMap.get(EVENT_ITEM_FIELDS.DT_END));
        this.description = getStringValueOfJSONO(jsonObject, EventItemFieldMap.get(EVENT_ITEM_FIELDS.DESCRIPTION));
        
        this.hasLocation = getStringValueOfJSONO(jsonObject, EventItemFieldMap.get(EVENT_ITEM_FIELDS.HAS_LOCATION));
        this.attendee = getStringListOfJSONObject(jsonObject, EventItemFieldMap.get(EVENT_ITEM_FIELDS.ATTENDEE));
        this.eventStatus = getStringValueOfJSONO(jsonObject, EventItemFieldMap.get(EVENT_ITEM_FIELDS.EVENT_STATUS));
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {

        newJSONObject.addChild(getJSONValue(dtstart, EventItemFieldMap.get(EVENT_ITEM_FIELDS.DT_START)));
        newJSONObject.addChild(getJSONValue(dtend, EventItemFieldMap.get(EVENT_ITEM_FIELDS.DT_END)));
        newJSONObject.addChild(getJSONValue(description, EventItemFieldMap.get(EVENT_ITEM_FIELDS.DESCRIPTION)));
        
        newJSONObject.addChild(getJSONValue(hasLocation, EventItemFieldMap.get(EVENT_ITEM_FIELDS.HAS_LOCATION)));
        newJSONObject.addChild(getJSONCollection(attendee, EventItemFieldMap.get(EVENT_ITEM_FIELDS.ATTENDEE)));
        newJSONObject.addChild(getJSONValue(eventStatus, EventItemFieldMap.get(EVENT_ITEM_FIELDS.EVENT_STATUS)));

        return newJSONObject;
    }

    /**
     * @return the dtstart
     */
    public Long getDtstart() {
        return dtstart;
    }

    /**
     * @return the dtend
     */
    public Long getDtend() {
        return dtend;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the hasLocation
     */
    public String getHasLocation() {
        return hasLocation;
    }

    /**
     * @return the attendee
     */
    public List<String> getAttendee() {
        changed = true;
        return attendee;
    }

    /**
     * @return the eventStatus
     */
    public String getEventStatus() {
        return eventStatus;
    }

    /**
     * @param dtstart the dtstart to set
     */
    public void setDtstart(Long dtstart) {
        changed = true;
        this.dtstart = dtstart;
    }

    /**
     * @param dtend the dtend to set
     */
    public void setDtend(Long dtend) {
        changed = true;
        this.dtend = dtend;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        changed = true;
        this.description = description;
    }

    /**
     * @param hasLocation the hasLocation to set
     */
    public void setHasLocation(String hasLocation) {
        changed = true;
        this.hasLocation = hasLocation;
    }

    /**
     * @param attendee the attendee to set
     */
    public void setAttendee(List<String> attendee) {
        changed = true;
        this.attendee = attendee;
    }

    /**
     * @param eventStatus the eventStatus to set
     */
    public void setEventStatus(String eventStatus) {
        changed = true;
        this.eventStatus = eventStatus;
    }
}
