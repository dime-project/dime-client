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
