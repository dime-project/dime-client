package eu.dime.model.file;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import sit.web.MimeTypes;
import sit.web.multipart.MPTextEntry;
import sit.web.multipart.TYPES;

/**
 *
 * @author simon
 */
public class CrawlMetaData {

    private static final String P_SPACE = "      ";
    private static final String I_SPACE = "       ";
    private static final String LP = "\n";
    private static final String METADATA_PART_NAME_TAG = "metadata";
    private static final String PREFIX = "@prefix nfo:     <http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#> .\n@prefix nie:     <http://www.semanticdesktop.org/ontologies/2007/01/19/nie#> .\n\n";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss.SSS");
    private static final SimpleDateFormat timeZoneFormatter = new SimpleDateFormat("Z");

    /**
     * Format a Java date as a XSD dateTime. Example:
     * 2011-09-16T13:32:54.000+01:00
     *
     * @param date
     * @return
     */
    private static String formatDate(Date date) {

        String timezone = timeZoneFormatter.format(date); // : -0800

        // convert timezone format to -08:00
        timezone = timezone.substring(0, 3) + ":" + timezone.substring(3);

        return dateFormatter.format(date) + "T" + timeFormatter.format(date) + timezone;
    }
    
    private final File file;
    private final String sha1Hash;

    public CrawlMetaData(File file, String sha1Hash) {
        this.file = file;
        this.sha1Hash = sha1Hash;
    }

    

    private void addToPayload(StringBuilder payload, String name, String value, String schema, boolean useQuotes,  boolean finalEntry) {
        
        String quotes=useQuotes?"\"":"";
        
        payload.append(P_SPACE).append(name).append(I_SPACE)
                .append(quotes)
                .append(value).append(quotes).append(schema)
                .append(finalEntry?" .":" ;").append(LP);
    }
    
    
    private String createMetaString(File myFile, String mimeType, String hash){
        
        
        URI uRI = myFile.toURI();

        
        String container = "";
        if (myFile.getParent() != null) {
            container = new File(myFile.getParent()).toURI().toString();
        }
        String lastModified = formatDate(new Date(myFile.lastModified()));
        
        StringBuilder payload = new StringBuilder();

        payload.append(PREFIX);        
        payload.append("<").append(uRI.toString()).append(">").append(LP);

        addToPayload(payload, "a", "nfo:FileDataObject", "", false, false);
        if (mimeType!=null && mimeType.length()>0){
            addToPayload(payload, "nie:mimeType", mimeType, "", true, false);
        }
        addToPayload(payload, "nfo:belongsToContainer", container, "", true, false);
        addToPayload(payload, "nfo:fileLastModified", lastModified, "^^<http://www.w3.org/2001/XMLSchema#dateTime>", true, false);
        addToPayload(payload, "nfo:fileName", myFile.getName(), "", true, false);
        addToPayload(payload, "nfo:fileSize", myFile.length()+"", "^^<http://www.w3.org/2001/XMLSchema#long>", true, false);
        if (hash!=null && hash.length()>0){
            addToPayload(payload, "nfo:hashValue", hash, "", true ,true);
        }
        
        return payload.toString();
    }

    public MPTextEntry getMPEntry() {

        String mimeType = MimeTypes.getMimeTypeFromFileName(file.getName());
        String payload = createMetaString(file, mimeType, sha1Hash);

        
        return new MPTextEntry(TYPES.TEXT, mimeType, METADATA_PART_NAME_TAG, payload);
        
    }
}
