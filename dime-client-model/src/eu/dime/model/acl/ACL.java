/*
 */
package eu.dime.model.acl;

import eu.dime.model.GenItem;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;
import eu.dime.model.TYPES;
import java.util.ArrayList;
import java.util.Iterator;
import sit.json.JSONObject;
import sit.sstl.HashMapSet;

/**
 *
 * @author simon
 * 
 * 
 * "nao:includes": [
                     {
                              "saidSender":"jdaspojsd39023",
                              "groups": [groupId, ...],
                              "persons": [
                               {"personId":"392a", "saidReceiver":”fweo634ifwo”  ? optional using default/active said instead
                               },
                               {...}
                                 ] ,
                              "services": ["serviceId",...]
                          },
                          {
                          ...
                          }               
                        ],
 * 
 */
public class ACL extends JSONItem<ACL> implements Iterable<ACLPackage>{
    protected HashMapSet<String, ACLPackage> aclPackages = new HashMapSet();

   

    @Override
    public ACL getClone() {
        ACL result = new ACL();
        for (ACLPackage aclPackage:this.aclPackages){
            result.aclPackages.add(aclPackage.getClone());
        }
        return result;
    }

    @Override
    protected void wipeItem() {
        this.aclPackages.clear();
    }

    @Override
    public JSONObject createJSONObject() {        
        JSONObject result = new JSONObject(GenItem.GenItemFieldMap.get(GenItem.GEN_ITEM_FIELDS.ACCESSING_AGENTS));
        for (ACLPackage aclPackage:this.aclPackages){
            result.addItem(aclPackage.createJSONObject());
        }
        result.setType(JSONObject.JSON_TYPE_COLLECTION); //in case it is empty make sure acl is displayed as array
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        // clean up first
        wipeItem();
        
        //read packages
        for (JSONObject packageO: jsonObject.getItems()){
            ACLPackage aclPackage = new ACLPackage();
            aclPackage.readJSONObject(packageO);
            aclPackages.add(aclPackage);            
        }
    }

    public Iterable<String> getAgentGuids() {
        ArrayList<String> result = new ArrayList();
        for (ACLPackage aclPackage:this.aclPackages){
            result.addAll(aclPackage.getAgentGuids());
        }
        return result;
    }
    
    public Iterable<ACLPackage> getACLPackages(){
        return aclPackages;
    }

    public void addAccessingAgent(String senderSAID, String agentGuid, TYPES agentType, String saidReceiver) {
        ACLPackage aclPackage = aclPackages.get(senderSAID);
        if (aclPackage==null){//package is not existing yet
            aclPackage = new ACLPackage(senderSAID);
            aclPackages.add(aclPackage);
        }
        
        aclPackage.addAgent(agentGuid, agentType, saidReceiver);
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ACL other = (ACL) obj;
        if (this.aclPackages != other.aclPackages && (this.aclPackages == null || !this.aclPackages.equals(other.aclPackages))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.aclPackages != null ? this.aclPackages.hashCode() : 0);
        return hash;
    }

    public void removeAccessingAgent(String senderSAID, String agentGuid, TYPES agentType) throws AgentNotFoundInACLException {
        ACLPackage aclPackage = aclPackages.get(senderSAID);
        if (aclPackage==null){
            throw new AgentNotFoundInACLException(agentGuid, agentType, senderSAID); 
        }
        aclPackage.removeAccessingAgent(agentGuid, agentType);
    }
    
    public void removeAccessingAgent(String agentGuid, TYPES agentType) throws AgentNotFoundInACLException {
    	for (ACLPackage aclPackage:aclPackages){
            if (aclPackage.agentHasDirectAccess(agentGuid)){
            	aclPackage.removeAccessingAgent(agentGuid, agentType);
            }            
        }
    }

     public boolean agentHasDirectAccess(String agentGuid) {
        for (ACLPackage aclPackage:aclPackages){
            if (aclPackage.agentHasDirectAccess(agentGuid)){
                return true;
            }            
        }
        return false;
    }
    
    public Iterator<ACLPackage> iterator() {
        return aclPackages.iterator();
    }

    boolean hasPackage(String saidSender) {
        return aclPackages.contains(saidSender);
    }
    
    ACLPackage getAclPackage(String saidString){
        return aclPackages.get(saidString);
    }

    public boolean isEmpty() {
        return aclPackages.size()==0;
    }
    
    
    
}
