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
