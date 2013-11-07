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

import eu.dime.model.ModelRequestContext;
import eu.dime.model.TYPES;
import eu.dime.simpleps.api.EndpointHelper;
import eu.dime.simpleps.database.DatabaseAccess;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONParseException;
import sit.json.JSONParser;
import sit.web.HttpConstants;
import sit.web.WebRequest;

/**
 *
 * @author simon
 */
public class CreateItemCallHandler extends CallHandler {

    private final JSONParser parser = new JSONParser();

    @Override
    public DIME_HANDLER_PARAMS[] getSignature() {
        // dime-communications/api/dime/rest/9702325/group/0653332e-a16a-43b6-ab2e-23890612c492/
        return new DIME_HANDLER_PARAMS[]{
                    DIME_HANDLER_PARAMS.HOSTER,
                    DIME_HANDLER_PARAMS.TYPE,
                    DIME_HANDLER_PARAMS.OWNER
                };
    }

    @Override
    public String handleCall(WebRequest wr, ParamsMap params) {
        ModelRequestContext mrc = getMRC(params);
        TYPES type = null;
        try {
            type = getMType(params);
            if (wr.httpCommand.equalsIgnoreCase(HttpConstants.HTTP_COMMAND_POST)) {
                try {
                    String result = DatabaseAccess.createItem(mrc, type, parser.parseJSON(wr.getBodyAsString())).toJson();
                    Logger.getLogger(CreateItemCallHandler.class.getName()).log(Level.INFO, "response:\n" + result);
                    return result;
                } catch (JSONParseException ex) {
                    return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(CreateItemCallHandler.class, ex, wr, type, "unknown");
                }
            }//else                
            return EndpointHelper.logAccessErrorAndPrepareErrorMessage(CreateItemCallHandler.class, "unsupported HTTP command for create calls:" + wr.httpCommand, "unknown", type);
        } catch (Exception ex) {
            return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(CreateItemCallHandler.class, ex, wr, type, "unknown");
        }
    }

    @Override
    public String getName() {
        return this.getClass().toString();
    }
    
}
