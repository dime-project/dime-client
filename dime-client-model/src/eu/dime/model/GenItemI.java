/*
 *  Description of GenItemI
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 12.06.2012
 */
package eu.dime.model;

import sit.sstl.ObjectWithKey;

/**
 *
 * @author Simon Thiel
 */
public interface GenItemI extends ObjectWithKey<String> {
	
    public TYPES getMType();
    public String getGuid();
    
}
