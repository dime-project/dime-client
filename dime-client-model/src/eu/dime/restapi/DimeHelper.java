package eu.dime.restapi;

import eu.dime.model.GenItem;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.restapi.DimeEnvelope.ENVELOPE_TYPE;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.json.JSONPathAccessException;
import sit.web.HttpConstants;
import sit.web.client.HTTPResponse;
import sit.web.client.HttpHelper;

/**
 *
 * @author simon
 */
public class DimeHelper {

    public final static String API_VERSION = "0.9";
    //public static final String DIME_DNS_SERVER = "91.126.187.167";
    public static final String DIME_DNS_SERVER = "dimedns.bdigital.org";
    private final static String DIME_ROOT_PATH = "/dime-communications/";//"/dime/";
    public final static String DIME_BASIC_PATH = DIME_ROOT_PATH + "api/dime/rest/";
    public final static String DIME_NOTIFICATION_PATH = DIME_ROOT_PATH + "push/";
    public final static String DIME_ADMIN_PATH = DIME_ROOT_PATH + "api/dime/user/";
    public final static String DIME_NOTIFICATION_PUSH_PARM_DEVICE_ID = "deviceGUID";
    public final static String DIME_NOTIFICATION_PUSH_PARM_STARTING_FROM = "startingFrom";
    public final static String DIME_TEST_FUNCTION = "ping";    
    public final static String DIME_TEST_PATH = DIME_ROOT_PATH+"api/dime/system/"+DIME_TEST_FUNCTION; ///dime-communications/web/access/auth/@me
    public final static String DIME_AUTH_USER_REQUEST_PATH = DIME_ROOT_PATH+"web/access/auth/@me";
    public final static String DIME_QUESTIONAIRE_PATH = DIME_ROOT_PATH + "web/access/questionaire?lang=en";
    public final static String DIME_USAGE_TERMS_PATH = DIME_ROOT_PATH + "web/access/conditions";
    public final static String DIME_PRIVACY_POLICY_PATH = DIME_ROOT_PATH + "web/access/privacypolicy";
    public final static String DIME_HOWTO_PATH = DIME_ROOT_PATH + "static/ui/dime/howto.html";
    public static final String ADVISORY_PATH = "advisory";
    public static final String ADVISORY_REQUEST_ENDPOINT = "@request";    
    public static final String ADVISORY_REQUEST_SUBPATH = "/"+ADVISORY_PATH+"/"+ADVISORY_REQUEST_ENDPOINT;
    public static final String USER_REGISTRATION_ENDPOINT = DIME_ROOT_PATH + "api/dime/user";
    public final static String JSON_MIME_TYPE = JSONObject.JSON_MIME_TYPE;    
    public final static String DIME_OWNER_AUTH_TOKEN = "b3duZXI6ZGltZXBhc3M0b3duZXI=";
    public final static String DIME_SSL_CERT_TYPE = "TLS";
    public final static String DEFAULT_HOSTNAME = "dime.hci.iao.fraunhofer.de";
    public final static int DEFAULT_PORT = 8443;
    public final static boolean DEFAULT_USE_HTTPS = true;
    public final static String clientId = UUID.randomUUID().toString();
    public final static long systemStartTime = System.currentTimeMillis();
    public final static double DEFAULT_INITIAL_TRUST_LEVEL = 0.5;
    public final static double DEFAULT_INITIAL_PRIVACY_LEVEL = 0.5;
    
    public static String getBaseHostURl(RestApiConfiguration conf) {
        String myPath = conf.isHttps ? "https://" : "http://";
        return myPath + conf.hostName + ":" + conf.port;
    }
    
    public static String getAbsoluteUrlString(String relativeUrl, RestApiConfiguration conf){
        if (relativeUrl.length()>0 && relativeUrl.charAt(0)!='/'){
            relativeUrl = "/"+relativeUrl;
        }
        return getBaseHostURl(conf)+relativeUrl;
    }
    
    private HttpHelper httpHelper = new HttpHelper(DIME_SSL_CERT_TYPE);

    private JSONResponse createErrorResponse(String method, String path, String payload, String errorMessage) {
        JSONResponse result = new JSONResponse(method + " " + path, payload);
        result.callFailed = true;
        result.setMessage(errorMessage);
        return result;
    }

    public byte[] doGETFile(String path, String mimeType, RestApiConfiguration conf) {
        try {
            HTTPResponse result = httpHelper.doHTTPRequest(HttpConstants.HTTP_COMMAND_GET, conf.hostName, conf.port, path, "", mimeType, conf.isHttps, conf.authToken);
            return result.reply.getBytes();
        } catch (MalformedURLException ex) {
            Logger.getLogger(DimeHelper.class.getName()).log(Level.SEVERE, "", ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(DimeHelper.class.getName()).log(Level.SEVERE, "", ex);
        } catch (IOException ex) {
            Logger.getLogger(DimeHelper.class.getName()).log(Level.SEVERE, "", ex);
        }
        return new byte[0];
    }
    
    public JSONResponse doDIMEJSONGET(String path, String payload, RestApiConfiguration conf) {
        String errorMessage = "";
        try {
            return new JSONResponse(httpHelper.doHTTPRequest(HttpConstants.HTTP_COMMAND_GET, conf.hostName, conf.port, path, payload, JSON_MIME_TYPE, conf.isHttps, conf.authToken));
        } catch (MalformedURLException ex) {
            errorMessage = ex.getMessage();
        } catch (ProtocolException ex) {
            errorMessage = ex.getMessage();
        } catch (IOException ex) {
            errorMessage = ex.getMessage();
        }
        String myPath = DimeHelper.getBaseHostURl(conf) + path;
        return createErrorResponse("GET", myPath, payload, errorMessage);

    }

    public JSONResponse doDIMEJSONPOST(String path, String jsonData, RestApiConfiguration conf) {
        String errorMessage = "";
        try {
            return new JSONResponse(httpHelper.doHTTPRequest(HttpConstants.HTTP_COMMAND_POST, conf.hostName, conf.port, path, jsonData, JSON_MIME_TYPE, conf.isHttps, conf.authToken));
        } catch (MalformedURLException ex) {
            errorMessage = ex.getMessage();
        } catch (ProtocolException ex) {
            errorMessage = ex.getMessage();
        } catch (IOException ex) {
            errorMessage = ex.getMessage();
        }
        String myPath = DimeHelper.getBaseHostURl(conf) + path;
        return createErrorResponse(HttpConstants.HTTP_COMMAND_POST, myPath, jsonData, errorMessage);
    }

    public JSONResponse doDIMEJSONDELETE(String path, RestApiConfiguration conf) {
        String errorMessage = "";
        try {
            return new JSONResponse(httpHelper.doHTTPRequest(HttpConstants.HTTP_COMMAND_DELETE, conf.hostName, conf.port, path, "", JSON_MIME_TYPE, conf.isHttps, conf.authToken));
        } catch (MalformedURLException ex) {
            errorMessage = ex.getMessage();
        } catch (ProtocolException ex) {
            errorMessage = ex.getMessage();
        } catch (IOException ex) {
            errorMessage = ex.getMessage();
        }
        String myPath = DimeHelper.getBaseHostURl(conf) + path;
        return createErrorResponse(HttpConstants.HTTP_COMMAND_DELETE, myPath, "", errorMessage);
    }

    public boolean dimeServerIsAlive(RestApiConfiguration conf) {
        try {
            HTTPResponse response = httpHelper.doHTTPRequest(HttpConstants.HTTP_COMMAND_GET, conf.hostName, conf.port, DIME_TEST_PATH, "", JSON_MIME_TYPE, conf.isHttps, conf.authToken);
            return response != null && response.code == 200;
        } catch (MalformedURLException ex) {
            Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, "MalformedURLException:" + ex.getMessage());
        } catch (ProtocolException ex) {
            Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, "ProtocolException:" + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, "IOException:" + ex.getMessage());
        }
        return false;
    }
    
    public String getServerVersion(RestApiConfiguration conf) {
        JSONResponse response = doDIMEJSONGET(DIME_TEST_PATH, "", conf);
        RestApiAccess.handleResponse(response);
        if ((response == null) || (response.hasError())) {
            //we have not been able to fetch something
            return "error";
        }
        return response.getApiVersion();
    }

    public boolean dimeServerIsAuthenticated(String hoster, RestApiConfiguration conf) throws MalformedURLException, ProtocolException, IOException {
        HTTPResponse response = httpHelper.doHTTPRequest(HttpConstants.HTTP_COMMAND_GET, conf.hostName, conf.port, DIME_AUTH_USER_REQUEST_PATH, "", JSON_MIME_TYPE, conf.isHttps, conf.authToken);
        Logger.getLogger(DimeHelper.class.getName()).log(Level.INFO, "Authentication result: " + response.reply);
        return hoster.equals(response.reply);
    }

    public JSONObject getEnvelopeForPayload(ENVELOPE_TYPE type, JSONObject payload) throws JSONPathAccessException {
        if (!payload.getKey().equals("0")) {
            throw new JSONPathAccessException("Unexpected key for payload. key == " + payload.getKey());
        }
        JSONObject entry = new JSONObject(DimeEnvelope.ENTRY);
        entry.addItem(payload);
        return getEnvelopeForEntry(type, entry);
    }

    public JSONObject getEnvelopeForEntry(ENVELOPE_TYPE type, JSONObject entry) throws JSONPathAccessException {
        DimeEnvelope envelope = new DimeEnvelope(type);
        envelope.setEntry(entry);
        return envelope.getEnvelope();
    }

    public JSONObject packRequest(GenItem item) {
        try {
            JSONObject payload = item.getJSONObject();
            payload.setKey("0");
            return getEnvelopeForPayload(ENVELOPE_TYPE.REQUEST, payload);
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(DimeHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JSONObject packResponse(GenItem item) {
        try {
            return packResponse(item.getJSONObject());
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(DimeHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JSONObject packResponse(JSONObject payload) throws JSONPathAccessException {
        return getEnvelopeForPayload(ENVELOPE_TYPE.RESPONSE, payload);
    }

    public JSONObject packResponse(Collection<GenItem> values) {
        JSONObject entry = new JSONObject(DimeEnvelope.ENTRY);
        //make sure entry is shown as an array - even when empty
        entry.setType(JSONObject.JSON_TYPE_COLLECTION);
        for (GenItem item : values) {
            entry.addItem(item.getJSONObject());
        }
        try {
            return getEnvelopeForEntry(ENVELOPE_TYPE.RESPONSE, entry);
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(DimeHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public JSONObject packResponseOfJSON(Collection<JSONObject> values) throws JSONPathAccessException {
        JSONObject entry = new JSONObject(DimeEnvelope.ENTRY);
        //make sure entry is shown as an array - even when empty
        entry.setType(JSONObject.JSON_TYPE_COLLECTION);
        for (JSONObject jsonObject : values) {
            entry.addItem(jsonObject);
        }
        return getEnvelopeForEntry(ENVELOPE_TYPE.RESPONSE, entry);
    }

    public JSONObject createAccessErrorResponse(TYPES type, String guid, String message) {        
        return createAccessErrorResponse(ModelHelper.getNameOfType(type), guid, message);
    }
    
    public JSONObject createAccessErrorResponse(String type, String guid, String message) {
        DimeEnvelope envelope = new DimeEnvelope(ENVELOPE_TYPE.RESPONSE);
        envelope.setStatus(DimeEnvelope.STATUS_ERROR);        
        envelope.setMessage("Error when accessing " + guid + " (" + type + ") Message: " + message);
        return envelope.getEnvelope();
    }

    public JSONObject getFirstEntryOfRequest(JSONObject request) {
        try {
            DimeEnvelope envelope = new DimeEnvelope(ENVELOPE_TYPE.REQUEST, request);
            JSONObject resultItems = envelope.getEntry();
            return resultItems.getItems().firstElement();
        } catch (NoSuchElementException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "", ex);
            Logger.getLogger(DimeHelper.class.getName()).log(Level.WARNING, "request:\n" + request);
        }
        return null;
    }
    
}
