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

import eu.dime.model.Model;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.ModelTypeNotFoundException;
import eu.dime.model.TYPES;
import eu.dime.restapi.DimeHelper;
import eu.dime.simpleps.api.EndpointHelper;
import eu.dime.simpleps.database.DatabaseAccess;
import sit.web.WebRequest;

/**
 * 
 * @author simon
 */
public class GlobalCollectionCallHandler extends CallHandler {

	@Override
	public DIME_HANDLER_PARAMS[] getSignature() {
		// dime-communications/api/dime/rest/9702325/group/@all
		return new DIME_HANDLER_PARAMS[] { DIME_HANDLER_PARAMS.HOSTER,
				DIME_HANDLER_PARAMS.TYPE, DIME_HANDLER_PARAMS.AT_ALL };
	}

	@Override
	public String handleCall(WebRequest wr, ParamsMap params) {
		TYPES type = null;
		String endpointName = this.getName();
		try {
			ModelRequestContext mrc = getMRC(params);
			mrc = new ModelRequestContext(mrc.hoster, Model.ME_OWNER, mrc.lvHandler);
			type = getMType(params);
			if (wr.httpCommand.equals("GET")) {
				String result = DatabaseAccess.getAllAllJSONItems(mrc, type).toJson();
				return result;
			}// else
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
