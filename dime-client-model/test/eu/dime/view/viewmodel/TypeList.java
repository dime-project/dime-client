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
