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
