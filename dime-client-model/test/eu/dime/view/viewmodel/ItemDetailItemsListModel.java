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
