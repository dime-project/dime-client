/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.view.viewmodel;

import java.util.List;
import javax.swing.DefaultListModel;

/**
 *
 * @author simon
 */
public class ItemDetailItemsListModel extends DefaultListModel<String> {

    public ItemDetailItemsListModel() {
        
    }
    
    public ItemDetailItemsListModel(List<String> itemIds) {
        for (String itemId : itemIds){
            this.addElement(itemId);
        }
    }

    

}
