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

import eu.dime.model.specialitem.AuthItem;
import eu.dime.restapi.DimeHelper;
import sit.json.JSONPathAccessException;
import sit.web.HttpConstants;
import sit.web.WebRequest;

/**
 *
 * @author simon
 */
public class UserCallHandler extends CallHandler {

    @Override
    public DIME_HANDLER_PARAMS[] getSignature() {
        // dime-communications/api/dime/rest/9702325/@dump
        return new DIME_HANDLER_PARAMS[]{
                    DIME_HANDLER_PARAMS.HOSTER,
                    DIME_HANDLER_PARAMS.AUTH,
                    DIME_HANDLER_PARAMS.OWNER
                };
    }

    @Override
    public String handleCall(WebRequest wr, ParamsMap params) {
    	String result = "";
		AuthItem auth = new AuthItem();
		auth.setEnabled(true);
		auth.setEvaluationDataCapturingEnabled(false);
		auth.setEvaluationId("some-hashed-tenant-id");
		auth.setPassword("some-password");
		auth.setUsername("ana02");
    	if (wr.httpCommand.equals(HttpConstants.HTTP_COMMAND_GET)) {
           
    	}
    	if (wr.httpCommand.equals(HttpConstants.HTTP_COMMAND_POST)) {
            
        }//else
    	try {
			result = new DimeHelper().packResponse(auth.createJSONObject()).toJson();
		} catch (JSONPathAccessException e) {
			
		}
        return result;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

}
