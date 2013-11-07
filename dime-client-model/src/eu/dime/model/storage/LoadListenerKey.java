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
package eu.dime.model.storage;

import eu.dime.model.TYPES;

/**
 *
 * @author simon
 */
public class LoadListenerKey {
    private String hoster;
    private String owner;
    private TYPES type;

    public LoadListenerKey(String hoster, String owner, TYPES type) {
        this.hoster = hoster;
        this.owner = owner;
        this.type = type;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.hoster != null ? this.hoster.hashCode() : 0);
        hash = 61 * hash + (this.owner != null ? this.owner.hashCode() : 0);
        hash = 61 * hash + (this.type != null ? this.type.hashCode() : 0);
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
        final LoadListenerKey other = (LoadListenerKey) obj;
        if ((this.hoster == null) ? (other.hoster != null) : !this.hoster.equals(other.hoster)) {
            return false;
        }
        if ((this.owner == null) ? (other.owner != null) : !this.owner.equals(other.owner)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    /**
     * @return the hoster
     */
    public String getHoster() {
        return hoster;
    }

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @return the type
     */
    public TYPES getType() {
        return type;
    }

    /**
     * @param hoster the hoster to set
     */
    public void setHoster(String hoster) {
        this.hoster = hoster;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @param type the type to set
     */
    public void setType(TYPES type) {
        this.type = type;
    }
    
}
