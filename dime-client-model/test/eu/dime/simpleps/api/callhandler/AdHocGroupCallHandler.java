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

import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.simpleps.api.EndpointHelper;
import eu.dime.simpleps.database.DatabaseAccess;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.web.WebRequest;

/**
 * 
 * @author simon
 */
public class AdHocGroupCallHandler extends CallHandler {

	@Override
	public DIME_HANDLER_PARAMS[] getSignature() {
		// dime-communications/api/dime/rest/9702325/@createAdHoc
		return new DIME_HANDLER_PARAMS[] { DIME_HANDLER_PARAMS.HOSTER, DIME_HANDLER_PARAMS.CREATE_AD_HOC };
	}

	@Override
	public String handleCall(WebRequest wr, ParamsMap params) {
		params.put(DIME_HANDLER_PARAMS.OWNER, Model.ME_OWNER);
		ModelRequestContext mrc = getMRC(params);
		try {
			GroupItem item = (GroupItem) ItemFactory.createNewItemByType(TYPES.GROUP);
			item.setGroupType(GroupItem.VALID_GROUP_TYPE_VALUES[0]);
			item.setName("Ad-hoc Group " + UUID.randomUUID().getMostSignificantBits());
			item.setUserId(Model.ME_OWNER);
			String[] memberGUIDs = getAdHocGroupMembers(mrc);
			for (String memberGUID : memberGUIDs) {
				item.addItem(memberGUID);
			}
			GenItem newItem = Model.getInstance().createItem(mrc, item);
			DatabaseAccess.sendNotification(mrc, NotificationItem.OPERATION_CREATE, newItem);
			JSONObject result = new JSONObject("root");
			result.addChild(new JSONObject("message", "created ad-hoc-group:",true));
			result.addChild(item.getJSONObject());
			return (result.toString());
		} catch (Exception ex) {
			return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(AdHocGroupCallHandler.class, ex, wr, "create ad-hoc-group", "unknown");
		}
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}
	
	private final static String[] memberNames = { "Amman Carmen", "GrÃ¶pke Peter", "Hock Klaus", "Baumann Markus" };

	private String[] getAdHocGroupMembers(ModelRequestContext mrc) {
		ArrayList<String> result = new ArrayList<String>();
		for (String name : memberNames) {
			try {
				PersonItem item = (PersonItem) ModelHelper.getAgentByName(mrc, name);
				if (item == null) {
					Logger.getLogger(AdHocGroupCallHandler.class.getName()).log(Level.SEVERE, "cannot find person with name:" + name);
				} else {
					result.add(item.getGuid());
				}
			} catch (ClassCastException ex) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING,ex.getMessage(), ex);
			} catch (Exception ex) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING,ex.getMessage(), ex);
			}
		}
		return result.toArray(new String[result.size()]);
	}
	
}
