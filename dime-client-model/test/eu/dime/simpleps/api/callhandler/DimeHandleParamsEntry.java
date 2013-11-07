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
package eu.dime.simpleps.api.callhandler;

import sit.sstl.StrictSITEnumContainer;

/**
 *
 * @author simon
 */
public class DimeHandleParamsEntry implements StrictSITEnumContainer<DIME_HANDLER_PARAMS>{

    public final static String SIG_PATTERN_WILDCARD = "*";
    
    
    private final DIME_HANDLER_PARAMS paramType;
    private final String sigPattern;
    
    

    public DimeHandleParamsEntry(DIME_HANDLER_PARAMS paramType, String sigPattern) {
        this.paramType = paramType;
        this.sigPattern = sigPattern;
        if (sigPattern.indexOf("/")>-1){
            throw new RuntimeException("SigPatterns must end within \"/\" boundaries\nsigPattern:"+sigPattern);
        }
    }
    
    
    public DIME_HANDLER_PARAMS getEnumType() {
        return paramType;
    }

    /**
     * @return the sigPattern
     */
    public String getSigPattern() {
        return sigPattern;
    }
    

    
}
