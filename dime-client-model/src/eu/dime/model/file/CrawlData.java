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

package eu.dime.model.file;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

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
    
    private final File file;

    public CrawlData(File file) {
        this.file = file;
    }

    public MultipartContainer getMPC() throws IOException{
        System.out.println("file :"+this.file);
        String sha1Hash = null;
        try {
        	sha1Hash = HashHelper.getSHA1FromFile(file);
        } catch(Exception ex){ 
        	System.out.println("ex: "+ex);
        }
        MultipartContainer mpc = new MultipartContainer();
        String uri =  "urn:uuid:" + UUID.randomUUID();
        mpc.addPart(new MPFileEntry(TYPES.BINARY, HttpConstants.MIME_APPLICATION_OCTETSTREAM, "file", file));
        mpc.addPart(new MPTextEntry(TYPES.TEXT, "text/plain", "uri",uri));
        mpc.addPart(new MPTextEntry(TYPES.TEXT, "text/plain", "hash", sha1Hash));
        mpc.addPart(new CrawlMetaData(file, sha1Hash).getMPEntry(uri));
        mpc.addPart(new MPTextEntry(TYPES.TEXT, "text/plain", "syntax", "application/x-turtle"));
        System.out.println("multipart: "+mpc.getContentLength());
        return mpc;
    }
	    
}
