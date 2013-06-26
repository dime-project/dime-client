/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.acl;

import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;
import eu.dime.model.TYPES;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.json.JSONPathAccessException;
import sit.sstl.HashMapSet;
import sit.sstl.ObjectWithKey;

/**
 *
 * @author simon
 *
 * {
 * "saidSender":"jdaspojsd39023", "groups": [groupId, ...], "persons": [
 * {"personId":"392a", "saidReceiver":”fweo634ifwo” ? optional using
 * default/active said instead }, {...} ] , "services": ["serviceId",...] },
 *
 */
public class ACLPackage extends JSONItem<ACLPackage> implements ObjectWithKey<String> {

    private static final String SAID_SENDER_TAG="saidSender";
    private static final String  GROUPS_TAG="groups";
    private static final String  PERSONS_TAG="persons";
    private static final String  SERVICES_TAG="services";
    
    
    private String saidSender;
    private HashSet<String> groups = new HashSet();
    private HashMapSet<String, ACLPerson> persons = new HashMapSet();
    private HashSet<String> services = new HashSet();
    


    public ACLPackage(String saidSender) {
        this.saidSender = saidSender;
    }

    ACLPackage() {
        this.saidSender = null;
    }

    @Override
    public ACLPackage getClone() {
        ACLPackage result = new ACLPackage(this.getSaidSender());
        for (String group : groups) {
            result.groups.add(group);
        }
        for (ACLPerson person : persons) {
            result.persons.add(person.getClone());
        }
        for (String service : services) {
            result.services.add(service);
        }
        return result;
    }

    @Override
    protected void wipeItem() {
        saidSender = null;
        groups.clear();
        persons.clear();
        services.clear();
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("root");
                
        result.addChild(getJSONValue(getSaidSender(), SAID_SENDER_TAG));
        result.addChild(getJSONCollection(groups, GROUPS_TAG));
        JSONObject personsO = new JSONObject(PERSONS_TAG);
        for (ACLPerson person: persons){
            personsO.addItem(person.createJSONObject());
        }
        result.addChild(personsO);
        result.addChild(getJSONCollection(services, SERVICES_TAG));
        
        return result;
        
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        wipeItem();
        
        saidSender = getStringValueOfJSONO(jsonObject, SAID_SENDER_TAG);
        readStringListOfJSONObjectIntoCollection(jsonObject, GROUPS_TAG, groups);
        try {
            for (JSONObject personO: jsonObject.getChild(PERSONS_TAG).getItems()){
                ACLPerson person = new ACLPerson();
                person.readJSONObject(personO);
                persons.add(person);
            }
        } catch (JSONPathAccessException ex) {
             Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                    "unable to access key:" + PERSONS_TAG + " in json");
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "JSON:\n" 
                    + jsonObject.toString());        
        }
        
        readStringListOfJSONObjectIntoCollection(jsonObject, SERVICES_TAG, services);
    }

    boolean agentHasDirectAccess(String agentGuid) {
        if (groups.contains(agentGuid)){
            return true;
        }
        if (persons.contains(agentGuid)){
            return true;
        }
        if (services.contains(agentGuid)){
            return true;
        }
        return false;
    }

    Collection<String> getAgentGuids() {
        ArrayList<String> result = new ArrayList();
        result.addAll(groups);
        for (ACLPerson person: persons){
            result.add(person.getPersonId());
        }
        return result;
    }

    /**
     * @return the saidSender
     */
    public String getSaidSender() {
        return saidSender;
    }

    /**
     * @return the groups
     */
    public Iterable<String> getGroups() {
        return groups;
    }

    /**
     * @return the persons
     */
    public Iterable<ACLPerson> getPersons() {
        return persons;
    }

    /**
     * @return the services
     */
    public Iterable<String> getServices() {
        return services;
    }

    public String getKey() {
        return saidSender;
    }

    void addAgent(String agentGuid, TYPES agentType, String saidReceiver) {
        if (agentType==TYPES.PERSON){
            ACLPerson personEntry = persons.get(agentGuid);
            if(personEntry==null){
                personEntry=new ACLPerson(agentGuid, saidReceiver);
                persons.add(personEntry);
            }                        
            personEntry.setSaidReceiver(saidReceiver);
            
        }else if (agentType==TYPES.GROUP){
            groups.add(agentGuid);
        }else if (agentType==TYPES.ACCOUNT){
            services.add(agentGuid);
        }else{
            throw new RuntimeException("unsupported agent type: "+agentType);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ACLPackage other = (ACLPackage) obj;
        if ((this.saidSender == null) ? (other.saidSender != null) : !this.saidSender.equals(other.saidSender)) {
            return false;
        }
        if (this.groups != other.groups && (this.groups == null || !this.groups.equals(other.groups))) {
            return false;
        }
        if (this.persons != other.persons && (this.persons == null || !this.persons.equals(other.persons))) {
            return false;
        }
        if (this.services != other.services && (this.services == null || !this.services.equals(other.services))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.saidSender != null ? this.saidSender.hashCode() : 0);
        hash = 79 * hash + (this.groups != null ? this.groups.hashCode() : 0);
        hash = 79 * hash + (this.persons != null ? this.persons.hashCode() : 0);
        hash = 79 * hash + (this.services != null ? this.services.hashCode() : 0);
        return hash;
    }

    void removeAccessingAgent(String agentGuid, TYPES agentType) throws AgentNotFoundInACLException {
        boolean agentFound=false;
        
        if (agentType==TYPES.PERSON){
            Iterator<ACLPerson>iter=persons.iterator();
            while (iter.hasNext()){
                ACLPerson person = iter.next();
                if (person.getPersonId().equals(agentGuid)){
                    agentFound=true;
                    iter.remove();
                }
            }
        }else if (agentType==TYPES.GROUP){
            agentFound = groups.remove(agentGuid);
        }else if(agentType==TYPES.ACCOUNT){
            agentFound = services.remove(agentGuid);
        }
        
        if (!agentFound){
            throw new AgentNotFoundInACLException(agentGuid, agentType, this.saidSender); 
        }
    }

    boolean containsPerson(ACLPerson person) {
        return persons.contains(person);
    }
    
    boolean containsGroup(String groupGuid){
        return groups.contains(groupGuid);
    }
    
    boolean containsService(String serviceGuid){
        return services.contains(serviceGuid);
    }
    
    

  }
