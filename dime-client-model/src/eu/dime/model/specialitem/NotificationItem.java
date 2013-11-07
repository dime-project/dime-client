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
 *  Description of PersonItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.05.2012
 */
package eu.dime.model.specialitem;

import eu.dime.model.GenItem;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public class NotificationItem extends GenItem {

    public static final String OPERATION_TAG = "operation";
    public static final String CREATED_TAG = "created";
    public static final String ELEMENT_TAG = "element";
    public static final String OPERATION_CREATE = "create";
    public static final String OPERATION_UPDATE = "update";
    public static final String OPERATION_DELETE = "remove";

    private String operation;
    private long created;
    private NotificationElement element;
    private List<String> sentTo = new Vector<String>();  //TODO deprecated in PS API, need to change it also for SimplePS

    public NotificationItem() {
        wipeItemForItem();
    }

    public NotificationItem(String guid) {
        super(guid);
        wipeItemForItem();
    }

    @Override
    protected final void wipeItemForItem() {
        operation = "undefined";
        created = System.currentTimeMillis();
        element = new NotificationElement();
        sentTo.clear();
    }

    @Override
    protected GenItem getCloneForItem() {
        NotificationItem result = new NotificationItem();
        result.operation = this.operation;
        result.created = this.created;
        result.element = this.element.getClone();
        result.sentTo = new Vector(this.sentTo);

        return result;
    }

    @Override
    public void readJSONObjectForItem(JSONObject jsonObject) {
        this.operation = getStringValueOfJSONO(jsonObject, OPERATION_TAG);
        this.created = getLongValueOfJSONO(jsonObject, CREATED_TAG);
        try {
            this.element.readJSONObject(getItemOfJSONO(jsonObject, ELEMENT_TAG));
        } catch (InvalidJSONItemException ex) {
            Logger.getLogger(NotificationItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected JSONObject createJSONObjectForItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(operation, OPERATION_TAG));
        newJSONObject.addChild(getJSONValue(created, CREATED_TAG));
        newJSONObject.addChild(element.createJSONObject());
        return newJSONObject;
    }

    public class NotificationElement extends JSONItem<NotificationElement> {

        protected String guid;
        private String type;
        private String userId;

        public NotificationElement() {
            wipeItem();
        }

        public NotificationElement(String guid, String type, String userId) {
            this.guid = guid;
            this.type = type;
            this.userId = userId;
        }

        @Override
        public NotificationElement getClone() {
            NotificationElement result = new NotificationElement();
            result.guid = this.guid;
            result.type = this.type;
            result.userId = this.userId;
            return result;
        }

        @Override
        protected final void wipeItem() {
            this.guid = "";
            this.type = "";
            this.userId = "";
        }

        @Override
        public JSONObject createJSONObject() {
            JSONObject result = new JSONObject(ELEMENT_TAG);
            result.addChild(NotificationItem.this.getJSONValue(this.guid, GEN_ITEM_FIELDS.GUID));
            result.addChild(NotificationItem.this.getJSONValue(this.type, GEN_ITEM_FIELDS.TYPE));
            result.addChild(NotificationItem.this.getJSONValue(this.userId, GEN_ITEM_FIELDS.USER_ID));
            return result;
        }

        @Override
        public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
            this.guid = getStringValueOfJSONO(jsonObject, GenItemFieldMap.get(GEN_ITEM_FIELDS.GUID));
            this.type = getStringValueOfJSONO(jsonObject, GenItemFieldMap.get(GEN_ITEM_FIELDS.TYPE));
            this.userId = getStringValueOfJSONO(jsonObject, GenItemFieldMap.get(GEN_ITEM_FIELDS.USER_ID));
        }

        public String getGuid() {
            return guid;
        }

        public String getType() {
            return type;
        }

        public String getUserId() {
            return userId;
        }
    }

    public NotificationElement getElement() {
        changed = true;
        return element;
    }

    public void setElement(String guid, String type, String userId) {
        this.element = new NotificationElement(guid, type, userId);
    }

    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(String operation) {
        this.changed = true;
        this.operation = operation;
    }

    public boolean wasSentTo(String deviceGUID) {
        return sentTo.contains(deviceGUID);
    }

    public void addSentTo(String deviceGUID) {
        if (!sentTo.contains(deviceGUID)) { //only add it once
            sentTo.add(deviceGUID);
        }
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
