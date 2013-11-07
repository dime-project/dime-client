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

package eu.dime.restapi;
import eu.dime.model.CALLTYPES;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.io.FileHelperI;
import sit.json.JSONObject;
import sit.json.JSONParseException;
import sit.json.JSONParser;
import sit.web.client.HTTPResponse;
/**
 *
 * @author Simon Thiel
 */
public class RestCallCollection {
   
    private TYPES type;
    private Hashtable<CALLTYPES, HTTPResponse> responses = new Hashtable();
    
    public RestCallCollection(TYPES type) {
        this.type = type;
    }

    public void addResponse(CALLTYPES type, HTTPResponse response){
        if ((type==null) || (response==null)){
            Logger.getLogger(RestCallCollection.class.getName()).log(Level.INFO, "Response of type:"+this.type+":"+type+" was "+response);
            return;
        }
        responses.put(type, response);
    }

    private JSONParser parser = new JSONParser();
    private String jsonyfy(String jsonText) {

        try {
            JSONObject jsonOject = parser.parseJSON(jsonText);
            return jsonOject.toString();
        } catch (JSONParseException ex) {
            Logger.getLogger(RestCallCollection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jsonText;
    }


    public void writeToFile(FileHelperI fh) throws IOException {
   
        for (Entry<CALLTYPES, HTTPResponse> entry: responses.entrySet()){
            String fileName;
            // 1. write call if not empty
            if (entry.getValue().payload.length != 0){
                fileName = RestCallHelper.getCallFileName(type, ModelHelper.getCallName(entry.getKey()));
                fh.writeToFile(fileName, jsonyfy(entry.getValue().getPayloadAsString()));
            }
            // 2. write result
            fileName = RestCallHelper.getResponseFileName(type, ModelHelper.getCallName(entry.getKey()));  
            fh.writeToFile(fileName, jsonyfy(entry.getValue().reply));            
        }
    }
    

}
