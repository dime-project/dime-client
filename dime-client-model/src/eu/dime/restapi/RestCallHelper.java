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
