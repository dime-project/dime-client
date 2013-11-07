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

import eu.dime.model.GenItem;
import eu.dime.model.specialitem.NotificationItem;

import java.util.Vector;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author simon
 */
public class NotificationTableModel implements TableModel {

    private Vector<NotificationItem> notifications = new Vector();    
    private Vector<TableModelListener> listeners = new Vector();
  
    final static String [] COLUMN_NAMES = new String[]{
        "GUID", "Item Type", "Operation"
    };
    
    
    public synchronized void add(NotificationItem notification){
        notifications.add(0, notification);
        for (TableModelListener l : listeners){
            l.tableChanged(new TableModelEvent(this));
        }
    }
    
    public int getRowCount() {
        return notifications.size();
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
        
        NotificationItem notification = notifications.get(rowIndex);
        
        
        // "GUID", "Item Type", "Operation"
        if (columnIndex==0){
            return notification.getGuid();
        }
        if (columnIndex==1){
            return notification.getElement().getType();
        }
        if (columnIndex==2){
            return notification.getOperation();
        }
        return "";
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    public synchronized GenItem getItem(int sRow) {
        return notifications.get(sRow);
    }
    
}
