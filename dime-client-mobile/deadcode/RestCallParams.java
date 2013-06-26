/*
 *  Description of Class
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 06.06.2012
 */
package eu.dime.mobile.control;

import eu.dime.model.CALLTYPES;
import eu.dime.model.GenItem;
import eu.dime.model.TYPES;

/**
 * Class
 * 
 */
public class RestCallParams {
    CALLTYPES operation;
    String hoster;
    String owner;
    TYPES type;
    String guid;
    GenItem item;

    public RestCallParams(CALLTYPES operation, String hoster, String owner, TYPES type, String guid, GenItem item) {
        this.operation = operation;
        this.hoster = hoster;
        this.owner = owner;
        this.type = type;
        this.guid = guid;
        this.item = item;
    }

}
