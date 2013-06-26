/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.view.viewmodel;

import eu.dime.model.GenItem;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.OperationNotSupportedException;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import sit.sstl.Pair;

/**
 *
 * @author simon
 */
public class TypeSpecificFieldsListModel implements TableModel {

    
    
    class ItemField{
        private final Method getter;
        private final Method setter;
        private final GenItem item;

        public ItemField(Method getter, Method setter, GenItem item) {
            this.getter = getter;
            
            if ((setter!=null)
                && (setter.getParameterTypes().length==1)
                    && (setter.getParameterTypes()[0].equals(String.class))){
                this.setter = setter;
            }else{            
                this.setter = null;
            }
            this.item = item;
        }
        
        
        
        String getName(){
            return getter.getName();
        }
        
        String getValue(){
            try {
                return getter.invoke(item)+"";
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TypeSpecificFieldsListModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(TypeSpecificFieldsListModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(TypeSpecificFieldsListModel.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "";
        }
        
        void setValue(String value){
            try {
                if (setter==null){                
                    Logger.getLogger(ItemField.class.getName()).log(Level.SEVERE, "unable to writer data for "+getName());
                    return;
                }
                setter.invoke(item, value);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(TypeSpecificFieldsListModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(TypeSpecificFieldsListModel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(TypeSpecificFieldsListModel.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
        
        boolean hasSetter(){
            return setter!=null;
        }
    }

    Vector<ItemField> fields = new Vector();

    public TypeSpecificFieldsListModel() {
    }

    public TypeSpecificFieldsListModel(GenItem item) {
        try {
            for (PropertyDescriptor propertyDescriptor :
                    Introspector.getBeanInfo(item.getClass()).getPropertyDescriptors()) {
                
                Method getter = propertyDescriptor.getReadMethod();
                Method setter = propertyDescriptor.getWriteMethod();
                
                if (isToBeIgnored(getter.getName())){
                    continue;
                }
                
                ItemField itemField = new ItemField(getter, setter, item);
                fields.add(itemField);                
            }
        } catch (IntrospectionException ex) {
            Logger.getLogger(TypeSpecificFieldsListModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int getRowCount() {
        return fields.size();
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return "Name";
        } else if (columnIndex == 1) {
            return "Value";
        }
        return "";
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex==0){
            return false;
        }
        ItemField field = fields.get(rowIndex);
        return field.hasSetter();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        ItemField field = fields.get(rowIndex);

        if (columnIndex == 0) {
            return field.getName();
        }
        return field.getValue();
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ItemField field = fields.get(rowIndex);
        if (columnIndex == 0) {
            return;
        }
        field.setValue((String)aValue);
    }

    public void addTableModelListener(TableModelListener l) {
    }

    public void removeTableModelListener(TableModelListener l) {
    }
    
    private boolean isToBeIgnored(String name) {
        if (name.equals("getClone")){
            return true;
        }
        if (name.equals("getClass")){
            return true;
        }
        return false;
    }
}
