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
