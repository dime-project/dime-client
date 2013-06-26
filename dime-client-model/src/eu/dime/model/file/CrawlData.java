package eu.dime.model.file;

import java.io.File;
import java.io.IOException;
import sit.tools.HashHelper;
import sit.web.HttpConstants;
import sit.web.multipart.MPFileEntry;
import sit.web.multipart.MPTextEntry;
import sit.web.multipart.MultipartContainer;
import sit.web.multipart.TYPES;

/**
 * Contains all the known data for a resource, output of the
 * crawling process.
 *  
 * @author Ismael Rivera (ismael.rivera@deri.org)
 */
public class CrawlData {
    
    private static final String FILE_PART_NAME_TAG = "file";
    private static final String URI_PART_NAME_TAG = "uri";    
   
    
    private final File file;

    public CrawlData(File file) {
        this.file = file;
    }

    public MultipartContainer getMPC() throws IOException{
        
        
        String sha1Hash = HashHelper.getSHA1FromFile(file);
        
        MultipartContainer mpc = new MultipartContainer();
        
        
        mpc.addPart(new MPFileEntry(TYPES.BINARY, HttpConstants.MIME_APPLICATION_OCTETSTREAM, "file", file));
        mpc.addPart(new MPTextEntry(TYPES.TEXT, "text/plain", "uri", file.toURI().toString()));
        mpc.addPart(new MPTextEntry(TYPES.TEXT, "text/plain", "hash", sha1Hash));
        mpc.addPart(new CrawlMetaData(file, sha1Hash).getMPEntry());
        mpc.addPart(new MPTextEntry(TYPES.TEXT, "text/plain", "syntax", "application/x-turtle"));
        
        return mpc;
    }
	
	    
}
