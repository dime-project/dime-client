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

package eu.dime.restapi;

import java.nio.charset.Charset;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.json.JSONParseException;
import sit.json.JSONParser;
import sit.json.JSONPathAccessException;
import sit.web.client.HTTPResponse;

/**
 * TODO move this to the di.me project where it belongs to!!!!
 *
 * @author simon
 */
public class JSONResponse { 

    public JSONObject entryObject = null;
    public Vector<JSONObject> replyObjects = null;
    public static final String[] dataPath = {"response", "data"};
    public static final String[] entryPath = {"response", "data", "entry"};
    public static final String[] statusPath = {"response", "meta", "status"};
    public static final String[] versionsPath = {"response", "meta", "v"};
    public boolean isErrorMessage = false;
    public boolean callFailed = false;
    public boolean noDataEntryFound = false;
    public boolean noDataFound = false;
    private HTTPResponse httpResponse;

    public JSONResponse(HTTPResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public JSONResponse(String call, String payload) {
        this.httpResponse = new HTTPResponse(call, payload.getBytes(), Charset.defaultCharset());
    }

    public void parseJSONResponse() {
        try {
            JSONParser parser = new JSONParser();
            entryObject = parser.parseJSON(httpResponse.reply);

        } catch (JSONParseException ex) {

            Logger.getLogger(JSONResponse.class.getName()).log(Level.FINE, null, ex);
            isErrorMessage = true;
            return;
        }

        try {
            JSONObject status = entryObject.getChild(statusPath);
            if (!status.getValue().equalsIgnoreCase("OK")) {
                isErrorMessage = true;
                return;
            }
        } catch (JSONPathAccessException ex) {

            Logger.getLogger(JSONResponse.class.getName()).log(Level.FINE, null, ex);
            isErrorMessage = true;
            return;
        }


        try {
            entryObject.getChild(dataPath);
        } catch (JSONPathAccessException ex) {
            noDataFound = true;
            return;
        }

        try {
            entryObject.getChild(entryPath);
        } catch (JSONPathAccessException ex) {
            noDataEntryFound = true;
            return;
        }

        try {
            replyObjects = entryObject.getChild(entryPath).getItems();
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(JSONResponse.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private String getErrorReasonString() {
        if (callFailed) {
            return "failure when calling endpoint";
        }//else
        if (isErrorMessage) {
            return "error message or undefined status";
        }//else
        if (noDataFound) {
            return "data element missing";
        }
        if (noDataEntryFound) {
            return "data.entry element missing";
        }
        if (replyObjects.isEmpty()) {
            return "data.entry element is empty";
        }
        return "";

    }

    public String toShortString() {
        if (!hasError()) {
            return httpResponse.call + " - OK - received " + replyObjects.size() + " item(s)";
        } else {
            return httpResponse.call + " - ERROR - " + getErrorReasonString();
        }
    }

    public boolean hasError() {
        return (callFailed || isErrorMessage || noDataEntryFound); //noDataFound not used
    }

    void setMessage(String errorMessage) {
        httpResponse.message = errorMessage;
    }

    String getReply() {
        return httpResponse.reply;
    }

    String getCall() {
        return httpResponse.call;
    }

    int getCode() {
        return httpResponse.code;
    }

    String getPayload() {
        return httpResponse.getPayloadAsString();
    }
    
    public String getApiVersion() {
    	String version = "error";
    	if(entryObject != null) {
    		try {
				version = entryObject.getChild(versionsPath).getValue();
			} catch (JSONPathAccessException e) {
				//silently catch exception
			}
    	}
    	return version;
    }

    @Override
    public String toString() {
        String result = "";
        if (httpResponse != null) {
            result += httpResponse.toString();
        } else {
            result = "httpResponse == null";
        }
        if (entryObject != null) {
            result += "\nEntry:\n" + entryObject.toString();
        }
        return result;
    }
}
