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
