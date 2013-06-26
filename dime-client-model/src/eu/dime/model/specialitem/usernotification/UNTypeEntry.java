/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.specialitem.usernotification;

import java.util.logging.Level;
import java.util.logging.Logger;
import sit.sstl.StrictSITEnumContainer;

/**
 *
 * @param <UTYPE> 
 * @author simon
 */
public class UNTypeEntry<UTYPE extends UnEntry> implements StrictSITEnumContainer {


    private UN_TYPE unType;
    public final String tag;
    public final Class unClass;

    public UNTypeEntry(UN_TYPE unType, String tag, Class unClass) {
        this.unType = unType;
        this.tag = tag;
        this.unClass = unClass;
    }
    
    
    public Enum getEnumType() {
        return unType;
    }
    
    public UnEntry getInstance(){         
        try {
            return (UnEntry) unClass.newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(UNTypeEntry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(UNTypeEntry.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
}
