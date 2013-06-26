/*
 *  Description of CallTypeEntry
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 13.04.2012
 */
package eu.dime.model;

import sit.sstl.StrictSITEnumContainer;


/**
 *
 * @author Simon Thiel
 */
public class CallTypeEntry implements StrictSITEnumContainer<CALLTYPES>{

    
    public final CALLTYPES type;
    public final String callName;
    public final String functionName;

    public CallTypeEntry(CALLTYPES type, String callName, String functionName) {
        this.type = type;
        this.callName = callName;
        this.functionName = functionName;
        if (functionName==null){
            throw new RuntimeException("invalid function name == null!");
        }
    }


    public CALLTYPES getEnumType() {
        return this.type;
    }

    
    
}
