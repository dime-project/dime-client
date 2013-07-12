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
