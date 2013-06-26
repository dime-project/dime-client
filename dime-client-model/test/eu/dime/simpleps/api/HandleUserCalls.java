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
