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
