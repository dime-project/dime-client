/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.view.viewmodel;

import eu.dime.model.GenItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.specialitem.NotificationItem;

import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author simon
 */
public class ChildItemTableModel implements TableModel {

    private final List<DisplayableItem> childItems;

    
    final static String [] COLUMN_NAMES = new String[]{
        "GUID", "Name", "Type"
    };
    
    public ChildItemTableModel(List<DisplayableItem> childItems) {
        this.childItems = childItems;
    }

   
    
     public int getRowCount() {
        return childItems.size();
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public String getColumnName(int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        
        DisplayableItem item = childItems.get(rowIndex);
        
        
        // "GUID", "Item Type", "Operation"
        if (columnIndex==0){
            return item.getGuid();
        }
        if (columnIndex==1){
            return item.getName();
        }
        if (columnIndex==2){
            return item.getType();
        }
        return "";
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addTableModelListener(TableModelListener l) {
        
    }

    public void removeTableModelListener(TableModelListener l) {
        
    }

    public DisplayableItem getItem(int sRow) {
        return childItems.get(sRow);
    }
}
