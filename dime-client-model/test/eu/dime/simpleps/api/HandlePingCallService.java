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
