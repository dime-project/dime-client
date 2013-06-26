/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.acl;

/**
 *
 * @author simon
 */
public class ACLHelper {

    
    public interface ACLDiffDelegate{
        
        void handleAddedPackage(ACLPackage aclPackage);
        void handleRemovedPackage(ACLPackage aclPackage);
        void handleAddedPerson(String said, ACLPerson person);
        void handleAddedGroup(String said, String groupGuid);
        void handleAddedService(String said, String serviceGuid);
        void handleRemovedPerson(String said, ACLPerson person);
        void handleRemovedGroup(String said, String groupGuid);
        void handleRemovedService(String said, String serviceGuid);
        
    }
    
    
    public static void diffACL(ACL oldAcl, ACL newAcl, ACLDiffDelegate diffProc){



      for (ACLPackage oldPackage : oldAcl) {
            if (!newAcl.hasPackage(oldPackage.getSaidSender())) {
                diffProc.handleRemovedPackage(oldPackage);
            } else {
                diffPackages(oldPackage, newAcl.getAclPackage(oldPackage.getSaidSender()), diffProc);
            }
        }
        //check the other way round 
        for (ACLPackage newPackage : newAcl) {
            if (!oldAcl.hasPackage(newPackage.getSaidSender())) {
                diffProc.handleAddedPackage(newPackage);
            }
        }
    }
    
    
    private static void diffPackages(ACLPackage oldPackage, ACLPackage newPackage, ACLDiffDelegate diffProc) {
        if (oldPackage.equals(newPackage)){
            return; //nothing changed here
        }
        //persons
        for (ACLPerson person: oldPackage.getPersons()){
            if (!newPackage.containsPerson(person)){
                diffProc.handleRemovedPerson(oldPackage.getSaidSender(), person);
            }
        }
        for (ACLPerson person: newPackage.getPersons()){
            if (!oldPackage.containsPerson(person)){
                diffProc.handleAddedPerson(newPackage.getSaidSender(), person);
            }
        }
        //groups
        for (String group: oldPackage.getGroups()){
            if (!newPackage.containsGroup(group)){
                diffProc.handleRemovedGroup(oldPackage.getSaidSender(), group);
            }
        }
        for (String group: newPackage.getGroups()){
            if (!oldPackage.containsGroup(group)){
                diffProc.handleAddedGroup(newPackage.getSaidSender(), group);
            }
        }
        //services
        for (String service: oldPackage.getServices()){
            if (!newPackage.containsService(service)){
                diffProc.handleRemovedService(oldPackage.getSaidSender(), service);
            }
        }
        for (String service: newPackage.getServices()){
            if (!oldPackage.containsService(service)){
                diffProc.handleAddedService(newPackage.getSaidSender(), service);
            }
        }
        
    }

}
