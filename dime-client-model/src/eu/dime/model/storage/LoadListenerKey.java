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
