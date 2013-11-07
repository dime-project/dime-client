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
package eu.dime.model;

import eu.dime.model.displayable.LivePostItem;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONParseException;
import sit.json.JSONParser;

/**
 *
 * @author simon
 */
public class LivePostiItem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LivePostItem lpi = (LivePostItem) ItemFactory.createNewItemByType(TYPES.LIVEPOST);
        lpi.setTimeStamp(System.currentTimeMillis());
        System.out.println(lpi.getClone().getJSONObject().toString());
        
        try {
            System.out.println(
                    new JSONParser().parseJSON(lpi.getClone().getJSONObject().toJson()).toJson().equals(lpi.getJSONObject().toJson())
                    );
        } catch (JSONParseException ex) {
            Logger.getLogger(LivePostiItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
