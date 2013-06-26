/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.simpleps.api.callhandler;

import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.model.GenItem;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.TYPES;
import eu.dime.model.storage.CacheFailException;
import eu.dime.model.storage.DimeHosterStorage;
import eu.dime.model.storage.DimeMemory;
import eu.dime.restapi.DimeHelper;
import eu.dime.simpleps.api.EndpointHelper;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.json.JSONPathAccessException;
import sit.web.WebRequest;

/**
 *
 * @author simon
 */
public class DumpCallHandler extends CallHandler {

    @Override
    public DIME_HANDLER_PARAMS[] getSignature() {
        // dime-communications/api/dime/rest/9702325/@dump
        return new DIME_HANDLER_PARAMS[]{
                    DIME_HANDLER_PARAMS.HOSTER,
                    DIME_HANDLER_PARAMS.DUMP
                };
    }

    @Override
    public String handleCall(WebRequest wr, ParamsMap params) {
        DimeHelper dimeHelper = new DimeHelper();
        try {
            DimeHosterStorage dump = Model.getInstance().getDump(new ModelRequestContext(getHoster(params),
                    Model.ME_OWNER, new DummyLoadingViewHandler()));
            JSONObject dumpObject = new JSONObject("0");
            for (Map.Entry<String, DimeMemory> ownerStorage : dump.entrySet()) {
                JSONObject ownerStorageObj = new JSONObject(ownerStorage.getKey());
                dumpObject.addChild(ownerStorageObj);

                for (TYPES type : TYPES.values()) {
                    JSONObject typeObj = new JSONObject(ModelHelper.getNameOfType(type));
                    ownerStorageObj.addChild(typeObj);

                    for (GenItem item : ownerStorage.getValue().getAllItems(type)) {
                        typeObj.addItem(item.getJSONObject());

                    }

                }
            }
            return dimeHelper.packResponse(dumpObject).toJson();
        } catch (CacheFailException ex) {
            Logger.getLogger(DumpCallHandler.class.getName()).log(Level.SEVERE, null, ex); //This should not happen for non-remote accessing server
            return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(DumpCallHandler.class, ex, wr,  "unknown", "unknown");
        } catch (JSONPathAccessException ex) {
            return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(DumpCallHandler.class, ex, wr,  "unknown", "unknown");
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
    
}
