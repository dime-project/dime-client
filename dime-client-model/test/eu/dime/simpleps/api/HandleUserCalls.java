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

import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.restapi.DimeHelper;
import eu.dime.simpleps.SaidRegistry;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.json.JSONPathAccessException;
import sit.web.HttpConstants;
import sit.web.ServiceEndpointHelper;
import sit.web.WebRequest;



/**
 *
 * @author simon
 */
public class HandleUserCalls extends sit.web.ServiceEndpoint   {

    private DimeHelper dimeHelper = new DimeHelper();
    
    // endpoint should be dime-communications/api/dime/user/
    public HandleUserCalls(String endpointName) {
        super(endpointName, true);
    }

    
    
    @Override
    public byte[] handleCall(WebRequest wr) {
        wr.fname = ServiceEndpointHelper.replaceBackSlashes(wr.fname);
        Logger.getLogger(HandleUserCalls.class.getName()).log(Level.INFO, "handling call:" +wr.httpCommand+" "+ wr.fname);

        String postPath = ServiceEndpointHelper.getSubPath(wr.fname, endpointName);
        String[] pathParts = postPath.split("/");      
        
        
        if ((wr.httpCommand.equals(HttpConstants.HTTP_COMMAND_GET))        
            && ("@all".equals(pathParts[pathParts.length-1]))){
            
            DummyLoadingViewHandler lvh = new DummyLoadingViewHandler();
            Set<String> hosters = SaidRegistry.updateAndGetAllHosters(lvh);
            
            ArrayList<JSONObject> entry = new ArrayList();
            for (String hoster : hosters){
                JSONObject hostEntry = new JSONObject("", hoster, true);
                entry.add(hostEntry);
            }
            try {
                return dimeHelper.packResponseOfJSON(entry).toJson().getBytes();
            } catch (JSONPathAccessException ex) {
                return EndpointHelper.
                        logAccessExceptionAndPrepareErrorMessage(HandleUserCalls.class, 
                        ex, wr, "undefined", "undefined").getBytes();
            }
        }
        return  "Request not supported, yet".getBytes();
    }

    @Override
    public String toString() {
        return super.toString();
    }
   
    
    
    
}
