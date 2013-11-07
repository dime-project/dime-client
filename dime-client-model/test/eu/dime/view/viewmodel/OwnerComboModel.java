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
