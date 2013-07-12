/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.simpleps.api.callhandler;

import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.model.CALLTYPES;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.ModelTypeNotFoundException;
import eu.dime.model.TYPES;
import eu.dime.restapi.DimeHelper;
import sit.sstl.StrictSITEnumMap;
import sit.web.WebRequest;

/**
 *
 * @author simon
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class CallHandler {
    
	public static final StrictSITEnumMap<DIME_HANDLER_PARAMS, DimeHandleParamsEntry> DIME_HANDLE_PARAMS_MAP 
            = new StrictSITEnumMap(DIME_HANDLER_PARAMS.class, new DimeHandleParamsEntry[]{
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.HOSTER, DimeHandleParamsEntry.SIG_PATTERN_WILDCARD),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.TYPE, DimeHandleParamsEntry.SIG_PATTERN_WILDCARD),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.OWNER, DimeHandleParamsEntry.SIG_PATTERN_WILDCARD),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.USERID, DimeHandleParamsEntry.SIG_PATTERN_WILDCARD),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.ITEMID, DimeHandleParamsEntry.SIG_PATTERN_WILDCARD),    
            
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.SYSTEM, "system"),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.PING, DimeHelper.DIME_TEST_FUNCTION),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.MERGE, ModelHelper.getRestFunctionName(CALLTYPES.MERGE)),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.AT_ALL, ModelHelper.getRestFunctionName(CALLTYPES.AT_ALL_GET)),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.DUMP, ModelHelper.getRestFunctionName(CALLTYPES.DUMP)),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.DUMMY, "dummy"),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.AUTH, "user"),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.CRAWLER, ModelHelper.getRestFunctionName(CALLTYPES.CRAWLER)),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.AGENTID, DimeHandleParamsEntry.SIG_PATTERN_WILDCARD),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.CREATE_AD_HOC, "@createAdHoc"),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.CREATE_SITUATION, "@createSituation"),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.UPLOAD_FILE, "@uploadFile"),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.SHAREDTOAGENT, "shared"),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.RESOURCE_TYPE, ModelHelper.getPathNameOfType(TYPES.RESOURCE)),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.ADVISORY, DimeHelper.ADVISORY_PATH ),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.ADVISORY_ENDPOINT, DimeHelper.ADVISORY_REQUEST_ENDPOINT ),
            new DimeHandleParamsEntry(DIME_HANDLER_PARAMS.DEBUG, "@debug" )
            });
            

    //protected final String [] SIGNATURE;

    
    // example for constructor
    //    public CallHandler() {
    //        this.SIGNATURE = new String [] {"[hoster]", "[type]", "[owner]","@all"};
    //    }
    
    protected final DummyLoadingViewHandler lvh = new DummyLoadingViewHandler();
    
    protected ModelRequestContext getMRC(String hoster, String owner){
        return new ModelRequestContext(hoster, owner, lvh);
    }
    
    protected ModelRequestContext getMRC(ParamsMap params) {
        return getMRC(getHoster(params), getOwner(params));
    }
    
    protected String getHoster(ParamsMap params){
        return params.get(DIME_HANDLER_PARAMS.HOSTER);
    }
    
    protected String getOwner(ParamsMap params){
        return params.get(DIME_HANDLER_PARAMS.OWNER);
    }
    
    protected String getType(ParamsMap params){
        return params.get(DIME_HANDLER_PARAMS.TYPE);
    }
    
    protected TYPES getMType(ParamsMap params) throws ModelTypeNotFoundException{
        return ModelHelper.getMTypeFromString(params.get(DIME_HANDLER_PARAMS.TYPE));
    }
    
    protected String getUserId(ParamsMap params){
        return params.get(DIME_HANDLER_PARAMS.USERID);
    }
    
    protected boolean userIdIsAtMe(ParamsMap params){
        return params.get(DIME_HANDLER_PARAMS.USERID).equals(Model.ME_OWNER);
    }
    
    protected String getItemId(ParamsMap params){
        return params.get(DIME_HANDLER_PARAMS.ITEMID);
    }
    
    protected String getAgentId(ParamsMap params){
        return params.get(DIME_HANDLER_PARAMS.AGENTID);
    }
    
    /**
     * Signature states the pattern that is required to match the url path beyond the prefix.
     * the entries of the array are separated with "/" in the URL string
     * 
     * valid entries are
     * 
     * "<identifier>" e.g. "process"
     * DimeHandleParamsEntry.SIG_PATTERN_WILDCARD - wild card
     * "[<paramname>]" - name of a parameter that will be provided to the caller as part of the params object - e.g. [userid], [type] ...
     
     */    
    public abstract DIME_HANDLER_PARAMS[]getSignature();
    
    public abstract String handleCall(WebRequest wr, ParamsMap params);
    
    public abstract String getName();

    public String getSignatureString() {
        StringBuilder result = new StringBuilder();
        DIME_HANDLER_PARAMS [] signature = this.getSignature();
        for (DIME_HANDLER_PARAMS sig : signature){
            DimeHandleParamsEntry sigEntry =DIME_HANDLE_PARAMS_MAP.get(sig);
            result.append(sigEntry.getSigPattern()).append("/");
        }
        return result.toString();        
    }
    
    public ParamsMap createParamsMap(String[] pathParts){
        ParamsMap result = new ParamsMap();
        DIME_HANDLER_PARAMS [] signature = this.getSignature();
        if (signature.length!=pathParts.length){
            throw new RuntimeException("lenght of pathParts does not fit to signature lenght!");
        }
        for (int i=0;i<pathParts.length;i++){            
            result.put(signature[i], pathParts[i]);
        }
        return result;
    }
    
}
