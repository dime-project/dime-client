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
 *  Description of ProfileAttributeCategoriesEntry
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 06.07.2012
 */
package eu.dime.model.displayable;

import eu.dime.model.displayable.ProfileAttributeItem.VALUE_CATEGORIES;
import sit.sstl.StrictSITEnumContainer;

/**
 * ProfileAttributeCategoriesEntry
 * 
 */
public class ProfileAttributeCategoriesEntry implements StrictSITEnumContainer<VALUE_CATEGORIES>{

    public final VALUE_CATEGORIES category;
    public final String name;
    public final String caption;
    public final String [] keys;
    public final String [] labels;

    public ProfileAttributeCategoriesEntry(VALUE_CATEGORIES category, String name, String caption, String[] keys, String[] labels) {
        this.category = category;
        this.name = name;
        this.caption = caption;
        this.keys = keys;
        this.labels = labels;
    }

    public VALUE_CATEGORIES getEnumType() {
        return category;
    }
    
}
