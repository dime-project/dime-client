/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.simpleps.api;

import java.util.logging.Level;
import java.util.logging.Logger;
import sit.web.HttpConstants;
import sit.web.WebRequest;



/**
 *
 * 
 * 
 * @author simon
 */
public class HandlePingCallService extends sit.web.ServiceEndpoint   {

    
                            
    // endpoint should be /dime-communications/web/access/auth/
    public HandlePingCallService(String endpointName) {
        super(endpointName, true);
    }

    
    
    @Override
    public byte[] handleCall(WebRequest wr) {
        
        Logger.getLogger(HandleUserCalls.class.getName()).log(Level.INFO, "handling call:" +wr.httpCommand+" "+ wr.fname);

                
        if (wr.httpCommand.equals(HttpConstants.HTTP_COMMAND_GET)){
            
            return "pong".getBytes();
        }
        return  "Request not supported, yet".getBytes();
    }

    @Override
    public String toString() {
        return super.toString();
    }
   
    
    
    
}
