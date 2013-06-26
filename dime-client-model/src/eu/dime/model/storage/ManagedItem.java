/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.storage;

import eu.dime.model.CALLTYPES;
import eu.dime.model.GenItem;

/**
 *
 * @author simon
 */
public class ManagedItem {
    public final GenItem item;    
    public final CALLTYPES operation;
    public final String[] guidList;
    
    /**
     * gets increased when commit is failing - for tracking commit actions
     */
    private int failCount = 0; 

    public ManagedItem(GenItem item, CALLTYPES operation) {
        this.item = item;
        this.operation = operation;
        this.guidList = null;
    }

    /**
     * constructor for merge operations
     * @param item
     * @param guidList 
     */
    public ManagedItem(GenItem item, String[] guidList) {
        this.item = item;
        this.operation = CALLTYPES.MERGE;
        this.guidList = guidList;
    }
    
    

    void incFailCount() {
        failCount++;
    }
    
    int getFailCount(){
        return failCount;
    }
    
  
}
