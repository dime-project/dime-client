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

import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.SituationItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.simpleps.api.EndpointHelper;
import eu.dime.simpleps.database.DatabaseAccess;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.web.WebRequest;

public class SituationCallHandler extends CallHandler {
	
	private String[] situations = {"@Conference", "@Office", "@Home"};
	private int nextSituationIndex = 0;
	private String currentSituationGuid = null; 

	@Override
	public DIME_HANDLER_PARAMS[] getSignature() {
		// dime-communications/api/dime/rest/9702325/@createSituation
        return new DIME_HANDLER_PARAMS[]{
                    DIME_HANDLER_PARAMS.HOSTER,
                    DIME_HANDLER_PARAMS.CREATE_SITUATION
                };
	}

	@Override
	public String handleCall(WebRequest wr, ParamsMap params) {
		
		Logger.getLogger(SituationCallHandler.class.getName()).log(Level.INFO, "Received @createSituation call");
		
		params.put(DIME_HANDLER_PARAMS.OWNER, Model.ME_OWNER);
		ModelRequestContext mrc = getMRC(params);
		
		String situation = situations[nextSituationIndex];
		nextSituationIndex++;
		if (nextSituationIndex == situations.length) nextSituationIndex = 0;
		
		try {
			SituationItem item = (SituationItem) ItemFactory.createNewItemByType(TYPES.SITUATION);
			item.setActive(true);
			item.setName(situation);
			item.setUserId(Model.ME_OWNER);
			
			GenItem newItem = Model.getInstance().createItem(mrc, item);
			
			if (currentSituationGuid == null) currentSituationGuid = newItem.getGuid();
			else {
				Model.getInstance().removeItem(mrc,currentSituationGuid,TYPES.SITUATION);
				currentSituationGuid = newItem.getGuid();
			}
			
			DatabaseAccess.sendNotification(mrc, NotificationItem.OPERATION_CREATE, newItem);
			
			JSONObject result = new JSONObject("root");
	        result.addChild(new JSONObject("message", "created new situation", true));
	        result.addChild(newItem.getJSONObject());
	        return (result.toString());
	        
		} catch (Exception ex) {
            return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(SituationCallHandler.class, ex, wr, "create ad-hoc-group" , "unknown");
        }
        
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

}
