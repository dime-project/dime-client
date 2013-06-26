/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.view.viewmodel;

import eu.dime.model.ModelHelper;
import eu.dime.model.ModelTypeNotFoundException;
import eu.dime.model.TYPES;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author simon
 */
public class TypeList implements ComboBoxModel<String> {

    private TYPES selectedType = TYPES.values()[0];
    private HashSet<ListDataListener> listeners = new HashSet();
    
    public void setSelectedItem(Object anItem) {
        String myItem = (String) anItem;
        
        for (TYPES type: TYPES.values()){
            if (type.toString().equals(myItem)){
                selectedType=type;
                return;
            }
        }
        
         selectedType = TYPES.values()[0];
    }

    public Object getSelectedItem() {
        return selectedType.toString();
    }

    public int getSize() {
         return TYPES.values().length;
    }

    public String getElementAt(int index) {
         return TYPES.values()[index].toString();
    }

    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    public TYPES getSelectedType() {
        return selectedType;
    }
    
}
