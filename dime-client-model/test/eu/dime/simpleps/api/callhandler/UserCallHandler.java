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
