package eu.dime.simpleps.api.callhandler;

import eu.dime.model.Model;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.restapi.DimeHelper;
import eu.dime.simpleps.api.EndpointHelper;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.web.MimeTypes;
import sit.web.ServiceComponent;
import sit.web.ServiceComponents;
import sit.web.ServiceEndpointHelper;
import sit.web.WebRequest;

public class FileUploadCallHandler extends CallHandler {

    
    static final DimeHelper dimeHelper = new DimeHelper();
    
    @Override
    public DIME_HANDLER_PARAMS[] getSignature() {
        // dime-communications/api/dime/rest/9702325/resource/@me/@uploadFile
        return new DIME_HANDLER_PARAMS[]{
                    DIME_HANDLER_PARAMS.HOSTER,
                    DIME_HANDLER_PARAMS.RESOURCE_TYPE,        
                    DIME_HANDLER_PARAMS.OWNER, 
                    DIME_HANDLER_PARAMS.UPLOAD_FILE
                };
    }

    @Override
    public String handleCall(WebRequest wr, ParamsMap params) {

        Logger.getLogger(SituationCallHandler.class.getName()).log(Level.INFO, "Received "+wr.httpCommand+" @uploadFile call");

        params.put(DIME_HANDLER_PARAMS.OWNER, Model.ME_OWNER);
        
        try {
            String filename = getValueFromParams(wr, "qqfile");
            
            if (filename==null || filename.length()==0){
                Logger.getLogger(FileUploadCallHandler.class.getName()).log(Level.INFO, "filename not specified - generating");
                
                filename = UUID.randomUUID().toString()+".jpg";
            }
            
            Logger.getLogger(FileUploadCallHandler.class.getName()).log(Level.INFO, "mimetype:\n"+wr.headerItems.get("X-Mime-Type:".toUpperCase()));
            String mimeType = MimeTypes.getMimeTypeFromFileName(filename);//TODO extract set mimetype from call header

            ResourceItem item = UploadCallHandler.handleUploadedFile(getHoster(params),filename, wr.body, mimeType);                    
            JSONObject result = new JSONObject("root");
            result.addChild(new JSONObject("success", "true", false));
            result.addChild(new JSONObject("guid", item.getGuid(), true));
            result.addChild(new JSONObject("imageUrl", item.getImageUrl(), true));
            
            return (dimeHelper.packResponse(item).toJson());

        } catch (Exception ex) {
            return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(SituationCallHandler.class, ex, wr, "create ad-hoc-group", "unknown");
        }

    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    private String getValueFromParams(WebRequest wr, String key) throws UnsupportedEncodingException {
        if (key == null) {
            return null;
        }

        ServiceComponents params = ServiceEndpointHelper.extractNameValues(wr.param);
        for (ServiceComponent sc : params) {
            if (key.equals(sc.getA())) {
                return sc.getB();
            }
        }
        return null;
    }
}
