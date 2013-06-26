/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.restapi;

import eu.dime.model.GenItem;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.ItemFactory;
import eu.dime.model.TYPES;
import eu.dime.model.acl.AgentNotFoundInACLException;
import eu.dime.model.displayable.ResourceItem;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONParseException;
import sit.json.JSONParser;

/**
 *
 * @author simon
 */
public class ACLTest {
    
    
    private static void removeAndLog(GenItem item,String id, TYPES type) throws AgentNotFoundInACLException{
        item.removeAccessingAgent("said_"+id,"agent_"+id, type);
        item.removeAccessingAgent("said_all","agent_"+id, type);
        Logger.getLogger(ACLTest.class.getName()).log(Level.INFO, "removed "
                +id+" ("+type+")"+item.getJSONObject().toString());
        
    }
    
    private static void addAndLog(GenItem item,String id, TYPES type){
        item.addAccessingAgent("said_"+id,"agent_"+id, type, "rsaid_"+id);
        item.addAccessingAgent("said_all","agent_"+id, type, "rsaid_"+id);
        Logger.getLogger(ACLTest.class.getName()).log(Level.INFO, "added "
                +id+" ("+type+")"+item.getJSONObject().toString());
        
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ResourceItem resource = (ResourceItem) ItemFactory.createNewItemByType(TYPES.RESOURCE);
            int i=0;
            addAndLog(resource,""+i++, TYPES.PERSON);
            addAndLog(resource,""+i, TYPES.PERSON);
            addAndLog(resource,""+i++, TYPES.PERSON);
            
            
            addAndLog(resource,""+i, TYPES.PERSON);            
            removeAndLog(resource,""+i++, TYPES.PERSON);
            
            addAndLog(resource,""+i++, TYPES.GROUP);
            addAndLog(resource,""+i, TYPES.GROUP);
            addAndLog(resource,""+i++, TYPES.GROUP);
            
            addAndLog(resource,""+i, TYPES.GROUP);
            removeAndLog(resource,""+i++, TYPES.GROUP);
            
            addAndLog(resource,""+i++, TYPES.ACCOUNT);
            addAndLog(resource,""+i++, TYPES.ACCOUNT);
            
            addAndLog(resource,""+i, TYPES.ACCOUNT);
            removeAndLog(resource,""+i++, TYPES.ACCOUNT);
            
            //Logger.getLogger(ACLTest.class.getName()).log(Level.INFO, 
            ResourceItem reparsed = 
            (ResourceItem) ItemFactory.createNewItemByJSON(TYPES.RESOURCE, 
                    (new JSONParser()).parseJSON(resource.getJSONObject().toJson()));
            Logger.getLogger(ACLTest.class.getName()).log(Level.INFO, reparsed.getJSONObject().toString());
            boolean different = !resource.getJSONObject().toJson().equals(reparsed.getJSONObject().toJson());
            Logger.getLogger(ACLTest.class.getName()).log(Level.INFO, "different:"+different);
             
             
             
        } catch (AgentNotFoundInACLException ex) {
            Logger.getLogger(ACLTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidJSONItemException ex) {
            Logger.getLogger(ACLTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONParseException ex) {
            Logger.getLogger(ACLTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
