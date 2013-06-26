/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.acl;

import eu.dime.model.TYPES;

/**
 *
 * @author simon
 */
public class AgentNotFoundInACLException extends Exception {

    public AgentNotFoundInACLException(String agentGuid, TYPES agentType, String senderSAID) {
        super("Cannot find agent in ACL (agentGUID, agentType, senderSAID):"+agentGuid+", "+agentType+", "+senderSAID);
    }
    
}
