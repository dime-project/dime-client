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
 *  Description of DimeTable
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 12.06.2012
 */
package eu.dime.model.storage;

import eu.dime.model.GenItem;
import eu.dime.model.TYPES;
import sit.sstl.HashMapSet;
import sit.sstl.ObjectWithKey;

/**
 * DimeTable
 * 
 */
public class DimeTable extends HashMapSet<String, GenItem> implements ObjectWithKey<TYPES> {

    private final TYPES type;

    public DimeTable(TYPES type) {
        this.type = type;
    }
    
    public TYPES getKey() {
        return type;
    }

    public TYPES getType() {
        return type;
    }

}
