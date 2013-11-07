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
package eu.dime.simpleps.api.callhandler;

import eu.dime.model.Model;
import eu.dime.model.ModelRequestContext;
import eu.dime.simpleps.api.EndpointHelper;
import eu.dime.simpleps.database.DatabaseAccess;
import sit.json.JSONParser;
import sit.web.HttpConstants;
import sit.web.WebRequest;

/**
 *
 * @author simon
 */
public class AdvisoryRequestHandler extends CallHandler {

    private final JSONParser parser = new JSONParser();   

    @Override
    public DIME_HANDLER_PARAMS[] getSignature() {
        //POST dime-communications/api/dime/rest/{mainSaId}/advisory/@request
        return new DIME_HANDLER_PARAMS[]{
                    DIME_HANDLER_PARAMS.HOSTER,
                    DIME_HANDLER_PARAMS.ADVISORY,
                    DIME_HANDLER_PARAMS.ADVISORY_ENDPOINT,
                };
    }
    
   @Override
    public String handleCall(WebRequest wr, ParamsMap params) {
        ModelRequestContext mrc = getMRC(params);        
        mrc = new ModelRequestContext(mrc.hoster, Model.ME_OWNER, mrc.lvHandler);
        try {
            if (wr.httpCommand.equals(HttpConstants.HTTP_COMMAND_POST)) {
                return DatabaseAccess.getAdvisories(mrc, parser.parseJSON(wr.getBodyAsString()));
            }//else
        } catch (Exception ex) {
            return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(AdvisoryRequestHandler.class, ex, wr, "advisoryrequest", "unknown");
        }
        return EndpointHelper.logAccessErrorAndPrepareErrorMessage(AdvisoryRequestHandler.class, "unable to handle call:" + wr.fname, "unknown", "advisoryrequest");
    }

    @Override
    public String getName() {
        return this.getClass().toString();
    }
    
}
