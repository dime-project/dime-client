/*
 *  Description of DimeEnvelope
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 11.04.2012
 */
package eu.dime.restapi;

import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.json.JSONPathAccessException;
import sit.sstl.StringEnumMap;

/**
 *
 * @author Simon Thiel
 */
public class DimeEnvelope {
    public static final String CODE = "code";
    public static final String DATA = "data";
    public static final String ENTRY = "entry";
    public static final String ITEMSPERPAGE = "itemsPerPage";
    public static final String META = "meta";
    public static final String STARTINDEX = "startIndex";
    public static final String STATUS = "status";
    public static final String MESSAGE = "msg";

    public final static String STATUS_OK = "ok";
    public final static String STATUS_ERROR = "error";
    public static final String TIMEREF = "timeRef";
    public static final String TOTALRESULTS = "totalResults";
    public static final String V_VERSION = "v";





    public static enum ENVELOPE_TYPE  {REQUEST, RESPONSE};

    public static final StringEnumMap<ENVELOPE_TYPE> EnvelopeTypeMap = new StringEnumMap(
            ENVELOPE_TYPE.class,
            ENVELOPE_TYPE.values(),
            new String[]{
        "request", "response"
    });

    public final static String[] requestEntryPath = {EnvelopeTypeMap.get(ENVELOPE_TYPE.REQUEST), DATA, ENTRY};
    public final static String[] responseEntryPath = {EnvelopeTypeMap.get(ENVELOPE_TYPE.RESPONSE), DATA, ENTRY};

    private ENVELOPE_TYPE type;   
    private JSONObject envelope = null;

    //"{\"request\":{\"meta\":{},\"data\":{\"startIndex\":0,\"itemsPerPage\":1,\"totalResults\":1,\"entry\":[";


    public DimeEnvelope(ENVELOPE_TYPE type) {
        this.type = type;
        this.envelope = createEmptyEnvelope(type);
    }

     DimeEnvelope(ENVELOPE_TYPE type, JSONObject envelope) {
        this.type = type;
        this.envelope = envelope;
    }

    private String[] getDataPath(){
        return new String[]{EnvelopeTypeMap.get(type), DATA};
    }

    private String[] getEntryPath(){
        return new String[]{EnvelopeTypeMap.get(type), DATA, ENTRY};
    }

    private String[] getItemsPerPagePath(){
        return new String[]{EnvelopeTypeMap.get(type), DATA, ITEMSPERPAGE};
    }

    private String[] getTotalResultsPath(){
        return new String[]{EnvelopeTypeMap.get(type), DATA, TOTALRESULTS};
    }


    private String[] getStatusPath(){
        return new String[]{EnvelopeTypeMap.get(type), META, STATUS};
    }

    private String[] getMessagePath(){
        return new String[]{EnvelopeTypeMap.get(type), META, MESSAGE};
    }

    private JSONObject createEmptyEnvelope(ENVELOPE_TYPE type){
        JSONObject result = new JSONObject("root");
        JSONObject envelopeType = new JSONObject(EnvelopeTypeMap.get(type));
        result.addChild(envelopeType);

        //meta
        JSONObject meta = new JSONObject(META);
        meta.addChild(new JSONObject(V_VERSION, DimeHelper.API_VERSION, true));
        meta.addChild(new JSONObject( STATUS, STATUS_OK, true));
        meta.addChild(new JSONObject(CODE,"200", false));
        meta.addChild(new JSONObject(TIMEREF, System.currentTimeMillis()+"", false));
        meta.addChild(new JSONObject(MESSAGE, "", true));
        envelopeType.addChild(meta);

        //data
        JSONObject data = new JSONObject(DATA);
        data.addChild(new JSONObject(STARTINDEX,"0", false));
        data.addChild(new JSONObject(ITEMSPERPAGE, ""+0, false));
        data.addChild(new JSONObject(TOTALRESULTS, ""+0, false));
        JSONObject entry = new JSONObject(ENTRY);
        entry.setType(JSONObject.JSON_TYPE_COLLECTION);
        data.addChild(entry);
        envelopeType.addChild(data);
        
        return result;
    }

    /**
     * @return the entry
     */
    public JSONObject getEntry() {
        try {
            return envelope.getChild(getEntryPath());
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(DimeEnvelope.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @param entry the payload to set as entry JSONObject into the data
     * part of the envelope
     * must be of type JSON_TYPE_COLLECTION
     */
    public void setEntry(JSONObject entry) throws JSONPathAccessException {
        if (!entry.getKey().equals(ENTRY)){
            throw new JSONPathAccessException("entry object is not of type entry. entry key="
                    +entry.getKey()+" - entry:\n"+entry.toString());
        }
        int itemsSize = (entry!=null)?entry.getItemsSize():0;
                
                
        JSONObject data = envelope.getChild(getDataPath());
        data.addChild(new JSONObject(STARTINDEX,"0", false));
        data.addChild(new JSONObject(ITEMSPERPAGE, ""+itemsSize, false));
        data.addChild(new JSONObject(TOTALRESULTS, ""+itemsSize, false));

        data.addChild(entry);
    }

    public JSONObject getEnvelope(){
        return envelope;
    }

    public void setStatus(String status){
        try {
            envelope.getChild(getStatusPath()).setValue(status, true);
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(DimeEnvelope.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setMessage(String message){
        try {
            envelope.getChild(getMessagePath()).setValue(message, true);
        } catch (JSONPathAccessException ex) {
            Logger.getLogger(DimeEnvelope.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
