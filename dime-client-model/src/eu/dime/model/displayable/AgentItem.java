/*
 *  Description of AgentItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 12.06.2012
 */
package eu.dime.model.displayable;

import eu.dime.model.GenItemI;

/**
 * marker interface for agents
 * @author Simon Thiel
 */
public interface AgentItem extends GenItemI {
	
    public Double getTrustLevel();
    public void setTrustLevel(Double trustLevel);
    
}
