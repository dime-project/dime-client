/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.view.viewmodel;

import eu.dime.model.displayable.PersonItem;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author simon
 */
public class OwnerComboModel implements ComboBoxModel<OwnerComboListItem>{
    
    private final List<OwnerComboListItem> comboListItems = new Vector();
    private OwnerComboListItem selectedItem;
    
    public OwnerComboModel(List<PersonItem> persons) {
        
        this.comboListItems.add(new OwnerComboListItem(null, "@me"));
        selectedItem=this.comboListItems.get(0);            
        
        for(PersonItem person: persons){
            
            this.comboListItems.add(new OwnerComboListItem(person, person.getGuid()));
        }
        
    }

    public void setSelectedItem(Object anItem) {
        try{
            this.selectedItem = (OwnerComboListItem) anItem;
            
            
        }catch (ClassCastException ex){
            Logger.getLogger(OwnerComboModel.class.getName()).log(Level.SEVERE, ex.getMessage(), ex); 
        }
    }

    public OwnerComboListItem getSelectedItem() {
        return selectedItem;
    }

    public int getSize() {
        return comboListItems.size();
    }

    public OwnerComboListItem getElementAt(int index) {
        return comboListItems.get(index);
    }

    public void addListDataListener(ListDataListener l) {
        
    }

    public void removeListDataListener(ListDataListener l) {
        
    }
    
}
