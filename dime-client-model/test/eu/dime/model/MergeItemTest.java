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
