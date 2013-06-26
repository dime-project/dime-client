package eu.dime.model;

import eu.dime.model.acl.ACL;
import eu.dime.model.acl.AgentNotFoundInACLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.json.JSONPathAccessException;
import sit.sstl.ObjectWithKey;
import sit.sstl.StringEnumMap;

public abstract class GenItem extends JSONItem<GenItem> implements ObjectWithKey<String>, GenItemI {

    public static enum GEN_ITEM_FIELDS {
        GUID, LAST_UPDATE, NAME, IMAGEURL, TYPE, USER_ID, ITEMS, ACCESSING_AGENTS, DENIED_AGENTS
    };
    
    public static final StringEnumMap<GEN_ITEM_FIELDS> GenItemFieldMap =
            new StringEnumMap<GEN_ITEM_FIELDS>(GEN_ITEM_FIELDS.class, GEN_ITEM_FIELDS.values(),
            new String[]{"guid", "lastModified", "name", "imageUrl",
                "type", "userId", "items", "nao:includes", "nao:excludes"});
            
    // ATTENTION - when changing member fields make sure to update all building methods
    // (e.g. copy constructor, create json etc.)
    /**
     * changed: this flag is used to decide whether the source is required to be
     * re-calculated when calling getJSONObject
     * TODO - bad design since it's very hard to guarantee all overriding classes setting changed properly
     * however, for performance reasons it makes a lot of sense not to generate the JSONObject everytime getSource is called!!
     */
    protected boolean changed = true;
    protected TYPES modelType;
    protected String guid;
    protected JSONObject source = null;
    
    protected ACL acl = null; //access control list contains users able to access this item

    public GenItem() {
    }

    public GenItem(String guid) {
        this.guid = guid;
    }

    public String getKey() {
        return guid;
    }
    
    protected void setACL(ACL acl){
        this.acl = acl;
    }
    
    /**
     * WARNING - only for model internal use - make sure you do not change the ACL from outside
     * This is specially since in some cases: additional functions (e.g. RestApi calls are required)
     * @return 
     */
    public ACL getAccessingAgents() {
        if (acl==null){
            acl = new ACL(); //lacy construction   
            changed=true;
        }
        return acl;
    }
    
    public void addAccessingAgent(String senderSAID, String agentGuid, TYPES agentType, String saidReceiver) {
        if (acl==null){
            acl = new ACL(); //lacy construction            
        }
        changed=true;
        acl.addAccessingAgent(senderSAID, agentGuid, agentType, saidReceiver);
    }
    
    public void removeAccessingAgent(String senderSAID, String agentGuid, TYPES agentType) throws AgentNotFoundInACLException {
        if (acl==null){
            throw new AgentNotFoundInACLException(agentGuid, agentType, senderSAID); 
        }
        changed=true;
        acl.removeAccessingAgent(senderSAID, agentGuid, agentType);
    }
    
    public void removeAccessingAgent(String agentGuid, TYPES agentType) throws Exception {
        if (acl==null){
            throw new Exception(); 
        }
        changed=true;
        acl.removeAccessingAgent(agentGuid, agentType);
    }
    
    protected boolean hasDirectAccess(String agentGuid){
        if (acl==null){
            acl = new ACL(); //lacy construction
            changed=true;
        }
        return acl.agentHasDirectAccess(agentGuid);
    }

    protected abstract void wipeItemForItem();

    public final void wipeItem() {
        changed = true;
        guid = null;
        modelType = null;
        source = null;
        acl=null;

        //call item version of this method
        wipeItemForItem();
    }

    protected abstract GenItem getCloneForItem();
   
    
    public final GenItem getClone() {
        //call item version of this method
        GenItem result = getCloneForItem();

        result.modelType = this.modelType;
        result.guid = this.guid;
        result.source = null;
        if (this.acl!=null){
            result.acl = this.acl.getClone();
        }

        return result;
    }

    public abstract void readJSONObjectForItem(JSONObject jsonObject);

    public final void readJSONObject(JSONObject jsonObject, String guid) throws InvalidJSONItemException {
    	
        // clean up first
        wipeItem();
        
        if (guid==null){ //we need to read the guid
            // read the json
            this.guid = getStringValueOfJSONO(jsonObject, GenItemFieldMap.get(GEN_ITEM_FIELDS.GUID));
            if (this.guid.length() == 0) {
                throw new InvalidJSONItemException("InvalidJSONItem: guid field missing!");
            }
            this.source = jsonObject;
        }else{
            this.guid = guid;            
        }

        String typeStr = getStringValueOfJSONO(jsonObject, GenItemFieldMap.get(GEN_ITEM_FIELDS.TYPE));
        if (typeStr.length() == 0) {
            throw new InvalidJSONItemException("InvalidJSONItem: type field missing!");
        }
        try {
            setMTypeFromString(typeStr);
        } catch (ModelTypeNotFoundException ex) {
            throw new InvalidJSONItemException("InvalidJSONItem: unknown type:" + typeStr);
        }

        //if shareable add ACL information
        if (ModelHelper.isShareable(modelType)){
            acl = new ACL();
            try {
                acl.readJSONObject(jsonObject.getChild(GenItemFieldMap.get(GEN_ITEM_FIELDS.ACCESSING_AGENTS)));
            } catch (JSONPathAccessException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                    "unable to access key:" + GenItemFieldMap.get(GEN_ITEM_FIELDS.ACCESSING_AGENTS) + " in json");
                Logger.getLogger(this.getClass().getName()).log(Level.FINE, "JSON:\n" + jsonObject.toString());                
            }
            
        }
        
        //call item version of this method
        readJSONObjectForItem(jsonObject);
        
        this.changed = (guid!=null); //in case the guid was set from the parameter the json-object is dirty
    }

    public final void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        this.readJSONObject(jsonObject, null);
    }

    protected abstract JSONObject createJSONObjectForItem(JSONObject newJSONObject);

    public final JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(this.guid, GEN_ITEM_FIELDS.GUID));
        result.addChild(getJSONValue(this.getType(), GEN_ITEM_FIELDS.TYPE));
        //call item version of this method
        result = createJSONObjectForItem(result);
        //if shareable add ACL information
        if (ModelHelper.isShareable(modelType)){
            if (acl==null){ //lazy creation of acl
                acl = new ACL();                
            }
            result.addChild(acl.createJSONObject());
            result.addChild(getJSONCollection(new ArrayList<String>(), GEN_ITEM_FIELDS.DENIED_AGENTS));
        }
        
        return result;
    }

    protected void setMTypeFromString(String type) throws ModelTypeNotFoundException {
        TYPES myMType = ModelHelper.getMTypeFromString(type);

        this.modelType = myMType;
        this.changed = true;
    }

    public String getType() {
        try {
            return Model.TypeMap.get(modelType).type;
        } catch (NullPointerException ex) {
            throw new RuntimeException("unable to find type: " + modelType, ex);
        }
    }

    public void setType(String type) throws ModelTypeNotFoundException {
        setMTypeFromString(type);
        this.changed = true;
    }

    public void setMType(TYPES type) {
        this.modelType = type;
        this.changed = true;
    }

    public String getGuid() {
        return guid;
    }

    public TYPES getMType() {
        return modelType;
    }



    public JSONObject getJSONObject() {
        if ((!changed) && (source != null)) {
            return source;
        }
        source = createJSONObject();
        return source;
    }

    protected JSONObject getJSONValue(String value, GEN_ITEM_FIELDS field) {
        return getJSONValue(value, GenItemFieldMap.get(field));
    }

    protected JSONObject getJSONCollection(Iterable<String> values, GEN_ITEM_FIELDS field) {

        return getJSONCollection(values, GenItemFieldMap.get(field));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.guid != null ? this.guid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GenItem other = (GenItem) obj;
        if ((this.guid == null) ? (other.guid != null) : !this.guid.equals(other.guid)) {
            return false;
        }
        return true;
    }

    
    /**
     * setter for guid - should only be used by the factory method changing the
     * guid of an existing item is typically not a good idea
     *
     * @param guid
     */
    protected void setGuid(String guid) {
        this.guid = guid;
    }
    
}
