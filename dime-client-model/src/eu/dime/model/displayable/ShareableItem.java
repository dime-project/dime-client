/*
 *  Description of ShareableItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 30.10.2012
 */
package eu.dime.model.displayable;

import eu.dime.model.GenItemI;

/**
 * interface for shareable items
 * @author Simon Thiel
 */
public interface ShareableItem extends GenItemI {
	
	public Double getPrivacyLevel();
	public void setPrivacyLevel(Double privacyLevel);
	
}
