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

import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.JSONItem;
import sit.json.JSONObject;
import sit.sstl.ObjectWithKey;

/**
 *
 * @author simon
 * 
 * {"personId":"392a", "saidReceiver":”fweo634ifwo”  ? optional using default/active said instead
                               }
 * 
 */
public class ACLPerson extends JSONItem<ACLPerson> implements ObjectWithKey<String> {
    
    private final static String PERSON_ID_TAG = "personId";
    private final static String SAID_RECEIVER_TAG = "saidReceiver";
    
    private String personId;
    private String saidReceiver;

    public ACLPerson(String personId, String saidReceiver) {
        this.personId = personId;
        this.saidReceiver = saidReceiver;
    }

    protected ACLPerson() {
        personId=null;
        saidReceiver=null;
    }

    @Override
    public ACLPerson getClone() {
        return new ACLPerson(personId, saidReceiver);
    }

    @Override
    protected void wipeItem() {
        personId=null;
        saidReceiver=null;
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("root");
        result.addChild(getJSONValue(personId, PERSON_ID_TAG));
        result.addChild(getJSONValue(saidReceiver, SAID_RECEIVER_TAG));
        
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        personId=getStringValueOfJSONO(jsonObject, PERSON_ID_TAG);
        saidReceiver=getStringValueOfJSONO(jsonObject, SAID_RECEIVER_TAG);
    }

    /**
     * @return the personId
     */
    public String getPersonId() {
        return personId;
    }

    /**
     * @return the saidReceiver
     */
    public String getSaidReceiver() {
        return saidReceiver;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ACLPerson other = (ACLPerson) obj;
        if ((this.personId == null) ? (other.personId != null) : !this.personId.equals(other.personId)) {
            return false;
        }
        if ((this.saidReceiver == null) ? (other.saidReceiver != null) : !this.saidReceiver.equals(other.saidReceiver)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.personId != null ? this.personId.hashCode() : 0);
        hash = 79 * hash + (this.saidReceiver != null ? this.saidReceiver.hashCode() : 0);
        return hash;
    }

    public String getKey() {
        return personId;
    }

    void setSaidReceiver(String saidReceiver) {
        this.saidReceiver=saidReceiver;
    }
    
    
    
    
}
