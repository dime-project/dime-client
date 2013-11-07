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
