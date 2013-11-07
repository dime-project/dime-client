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

import eu.dime.model.specialitem.MergeItem;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONParseException;
import sit.json.JSONParser;

/**
 *
 * @author simon
 */
public class MergeItemTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MergeItem mergeItem = new MergeItem(UUID.randomUUID().toString());
        Vector<String> personGuids;
        for (int j = 0; j < 2; j++) {
            personGuids = new Vector<String>();
            for (int i = 0; i < 2; i++) {
                personGuids.add(UUID.randomUUID().toString());
            }
            mergeItem.addMergedItem(personGuids);
        }


        Logger.getLogger(MergeItemTest.class.getName()).log(Level.INFO, "json:\n"
                + mergeItem.createJSONObject().toString());
        try {
            MergeItem newMergeItem = new MergeItem("blub");
            newMergeItem.readJSONObject(new JSONParser().parseJSON(mergeItem.createJSONObject().toJson()));
            
            Logger.getLogger(MergeItemTest.class.getName()).log(Level.INFO, "match "+
                    mergeItem.createJSONObject().toJson().equals(newMergeItem.createJSONObject().toJson()));
            
        } catch (InvalidJSONItemException ex) {
            Logger.getLogger(MergeItemTest.class.getName()).log(Level.SEVERE, null, ex);

        } catch (JSONParseException ex) {
            Logger.getLogger(MergeItemTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
