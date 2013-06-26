/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.view.viewmodel;

import eu.dime.model.displayable.PersonItem;

/**
 *
 * @author simon
 */
public class OwnerComboListItem {
    public PersonItem personItem;
    public String guid;

    public OwnerComboListItem(PersonItem personItem, String guid) {
        this.personItem = personItem;
        this.guid = guid;
    }

    @Override
    public String toString() {
        if (personItem == null) {
            return guid;
        }
        return personItem.getGuid() + " (" + personItem.getName() + ")";
    }
    
}
