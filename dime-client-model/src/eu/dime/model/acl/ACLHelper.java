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
