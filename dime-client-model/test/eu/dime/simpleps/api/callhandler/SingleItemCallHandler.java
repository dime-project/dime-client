/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.simpleps.api.callhandler;

import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.TYPES;
import eu.dime.model.context.ContextDataString;
import eu.dime.model.context.ContextEntity;
import eu.dime.model.context.ContextItem;
import eu.dime.model.context.constants.Scopes;
import eu.dime.restapi.DimeHelper;
import eu.dime.simpleps.api.EndpointHelper;
import eu.dime.simpleps.database.DatabaseAccess;
import sit.json.JSONParseException;
import sit.json.JSONParser;
import sit.web.HttpConstants;
import sit.web.WebRequest;

/**
 *
 * @author simon
 */
public class SingleItemCallHandler  extends CallHandler{
      
    
    private final JSONParser parser = new JSONParser();
    
    // hack to manage currentPlace GET call
    private DimeHelper dimeHelper = new DimeHelper();
    private String[] places = {"Conference Booth", "Office", "Home"};
    private String[] ids = {"ametic:123", "ametic:456", "ametic:789"};
	private int nextPlaceIndex = 0;

    @Override
    public DIME_HANDLER_PARAMS[] getSignature() {
         // dime-communications/api/dime/rest/9702325/group/0653332e-a16a-43b6-ab2e-23890612c492/0653332e-a16a-43b6-ab2e-23890612c492

        return new DIME_HANDLER_PARAMS[]{
                DIME_HANDLER_PARAMS.HOSTER, 
                DIME_HANDLER_PARAMS.TYPE, 
                DIME_HANDLER_PARAMS.OWNER,
                DIME_HANDLER_PARAMS.ITEMID
        };
    }

    @Override
    public String handleCall(WebRequest wr, ParamsMap params) {
        
        ModelRequestContext mrc = getMRC(params);
        TYPES type = null;
        
        try {
            
            type = getMType(params);
            
            if (wr.httpCommand.equals(HttpConstants.HTTP_COMMAND_GET)) {
                
                //HACK for current place
                if ((type==TYPES.CONTEXT) && 
                        (getItemId(params).equals(Scopes.SCOPE_CURRENT_PLACE))) {
                    //return EndpointHelper.logAccessErrorAndPrepareErrorMessage(SingleItemCallHandler.class,
                    //        "unable to handle call:" + wr.fname, Scopes.SCOPE_CURRENT_PLACE, type);
                	
                	ContextItem item = (ContextItem) ItemFactory.createNewItemByType(TYPES.CONTEXT);
        			item.setScope("currentPlace");
        			ContextEntity e = new ContextEntity();
        			e.id = mrc.owner;
        			e.type = "useraname";
        			item.setEntity(e);
        			ContextDataString pname = new ContextDataString("placeName");
        			pname.setValue(places[nextPlaceIndex]);
        			item.addDataPart("placeName",pname);
        			
        			ContextDataString pid = new ContextDataString("placeId");
        			pid.setValue(ids[nextPlaceIndex]);
        			item.addDataPart("placeId",pid);
        			
        			nextPlaceIndex++;
        			if (nextPlaceIndex == places.length) nextPlaceIndex = 0;
        			
        			GenItem newItem = Model.getInstance().createItem(mrc, item);
        			
        			return dimeHelper.packResponse(newItem).toString();
                }
                //END HACK for current place
                
                return DatabaseAccess.getJSONItem(mrc, type, getItemId(params)).toJson();
            }//else
            if (wr.httpCommand.equals(HttpConstants.HTTP_COMMAND_POST)) {
                try {
                    return DatabaseAccess.updateItem(mrc, type, getItemId(params),
                            parser.parseJSON(wr.getBodyAsString())).toJson();
                } catch (JSONParseException ex) {
                    return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(SingleItemCallHandler.class, ex, wr, type, getItemId(params));
                }
            }//else
            if (wr.httpCommand.equals(HttpConstants.HTTP_COMMAND_DELETE)) {
                String result = DatabaseAccess.removeItem(mrc, type, getItemId(params)).toJson();
                return result;
            }//else
        } catch (Exception ex) {
            return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(SingleItemCallHandler.class, ex, wr, type, "unknown");
        }
        return EndpointHelper.logAccessErrorAndPrepareErrorMessage(SingleItemCallHandler.class,
                "unable to handle call:" + wr.fname, "unknown", type);
    }

    
    @Override
    public String getName() {
        return this.getClass().toString();
    }
    
}
