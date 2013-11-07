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

package eu.dime.simpleps.api.callhandler;

import eu.dime.model.ModelRequestContext;
import eu.dime.model.ModelTypeNotFoundException;
import eu.dime.model.TYPES;
import eu.dime.restapi.DimeHelper;
import eu.dime.simpleps.api.EndpointHelper;
import eu.dime.simpleps.database.DatabaseAccess;
import sit.web.ServiceEndpointHelper;
import sit.web.WebRequest;

/**
 *
 * @author simon
 */
public class SharedToAgentCallHandler extends CallHandler {

    public static final String SHARED_WITH_AGENT_URL_PARAM = "sharedWithAgent";
    
    @Override
    public DIME_HANDLER_PARAMS[] getSignature() {

        //dime-communications/api/dime/rest/ana02/livepost/@me/@all/shared?sharedWithAgent=d2f480f0-c174-4275-8ced-dcf4dd39e34b
        // dime-communications/api/dime/rest/9702325/group/0653332e-a16a-43b6-ab2e-23890612c492/@all
        return new DIME_HANDLER_PARAMS[]{
                    DIME_HANDLER_PARAMS.HOSTER,
                    DIME_HANDLER_PARAMS.TYPE,
                    DIME_HANDLER_PARAMS.OWNER,
                    DIME_HANDLER_PARAMS.AT_ALL,
                    DIME_HANDLER_PARAMS.SHAREDTOAGENT                
                };
    }
    
    @Override
    public String handleCall(WebRequest wr, ParamsMap params) {
        TYPES type = null;
        String endpointName = this.getName();
        try {
            ModelRequestContext mrc = getMRC(params);
            type = getMType(params);
            
            if (wr.httpCommand.equals("GET")) {
                
                String agentGUID = ServiceEndpointHelper.getURLParamForKeyFromWR(wr, SHARED_WITH_AGENT_URL_PARAM);
                if (agentGUID == null) {
                    return EndpointHelper.logAccessErrorAndPrepareErrorMessage(this.getClass(), "Error while handling request: agentGUID==null\n" + wr.toString(), "", type);
                }
                
                
                String result = DatabaseAccess.getAllSharedItemsForAgent(mrc, type, agentGUID);
                
                return result;
            }//else
            DimeHelper dimeHelper = new DimeHelper();
            return dimeHelper.createAccessErrorResponse(type, endpointName, "Error while handling request...\n" + wr.toString()).toJson();
        } catch (ModelTypeNotFoundException ex) {
            return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(this.getClass(), ex, wr, type, endpointName);
        } catch (Exception ex) {
            return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(this.getClass(), ex, wr, type, endpointName);
            
        }
        
    }
    
    @Override
    public String getName() {
        return this.getClass().toString();
    }
}
