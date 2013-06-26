/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.simpleps.api;

import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.restapi.DimeHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.tools.ExceptionHelper;
import sit.web.WebRequest;

/**
 *
 * @author simon
 */
public class EndpointHelper {
    
    public static String prepareEndpoint(String endpoint) {
        if (!endpoint.isEmpty() && endpoint.charAt(0) == '/') { //HACK - remove / at the start of URL
            return endpoint.substring(1);
        }
        return endpoint;
    }

    public static String logAccessErrorAndPrepareErrorMessage(Class exceptionClass, String errMessage, String guid, TYPES type) {
        return logAccessErrorAndPrepareErrorMessage(exceptionClass, errMessage, guid, ModelHelper.getNameOfType(type));
    }
    
    public static String logAccessErrorAndPrepareErrorMessage(Class exceptionClass, String errMessage, String guid, String type) {

        Logger.getLogger(exceptionClass.getName()).log(Level.SEVERE, errMessage);
        return new DimeHelper().createAccessErrorResponse(type, guid,
                "ERROR: " + exceptionClass.getName() +": "+ errMessage).toJson();
    }

    public static String logAccessExceptionAndPrepareErrorMessage(Class exceptionClass, Exception ex, WebRequest wr, TYPES type, String guid) {
        return logAccessExceptionAndPrepareErrorMessage(exceptionClass, ex, wr, ModelHelper.getNameOfType(type), guid);
    }
    
    public static String logAccessExceptionAndPrepareErrorMessage(Class exceptionClass, Exception ex, WebRequest wr, String type, String guid) {

        String errMessage;
        if (ex != null) {
            errMessage = ExceptionHelper.stackTraceToString(ex, "Server Error caused by:"+ex.getClass().getName());
        } else {
            errMessage = "Server Error when handling request:\n" + wr.toString();
        }

        return logAccessErrorAndPrepareErrorMessage(exceptionClass, errMessage, guid, type);
    }
}
