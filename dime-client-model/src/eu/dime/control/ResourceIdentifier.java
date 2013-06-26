/*
 *  Description of Class
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 06.06.2012
 */
package eu.dime.control;

import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import sit.sstl.ObjectWithKey;

/**
 * Class
 * 
 */
public class ResourceIdentifier implements ObjectWithKey<String> {
    public final String hoster;
    public final String owner;
    public final TYPES type;
    public final String path;
    

    public ResourceIdentifier(String hoster, String owner, TYPES type) {
        this.hoster = hoster;
        this.owner = owner;
        this.type = type;
        this.path = hoster + "/" + ModelHelper.getNameOfType(type) + "/" + owner;
    }

    public String getKey() {
        return path;
    }

 

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.path != null ? this.path.hashCode() : 0);
        return hash;
    }

    
}
