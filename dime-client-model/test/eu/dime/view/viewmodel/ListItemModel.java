/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.view.viewmodel;

import eu.dime.model.GenItem;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author simon
 */
public class ListItemModel implements ListModel {
    
    
    private List<GenItem> items;

    public ListItemModel(List<GenItem> allItems) {
        items = allItems;
    }

    public int getSize() {
        return items.size();
    }

    public Object getElementAt(int index) {
        return items.get(index);
    }

    public void addListDataListener(ListDataListener l) {
        
    }

    public void removeListDataListener(ListDataListener l) {
        
    }
    
}
