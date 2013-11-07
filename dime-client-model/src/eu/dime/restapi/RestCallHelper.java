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
 *  Description of RestCallHelper
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 13.04.2012
 */
package eu.dime.restapi;

import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;

/**
 *
 * @author Simon Thiel
 */
public class RestCallHelper {

    public final static String FILE_PREFIX = "di.me.debug/dime_rest_api_call-";
    public final static String FILE_POSTFIX = ".json";
    public final static String CALL_POSTFIX = "-CALL" + FILE_POSTFIX;
    public final static String RESPONSE_POSTFIX = "-RESPONSE" + FILE_POSTFIX;

    public static String getCallFileName(TYPES type, String callName) {
        return FILE_PREFIX + ModelHelper.getPathNameOfType(type) + "_" + callName + CALL_POSTFIX;
    }

    public static String getResponseFileName(TYPES type, String callName) {
        return FILE_PREFIX + ModelHelper.getPathNameOfType(type) + "_" + callName + RESPONSE_POSTFIX;
    }
}
