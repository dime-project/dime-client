/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.simpleps.api.callhandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simon
 */
public class CallHandlerManager implements Iterable<CallHandler> {
    private Vector<CallHandler> callHandlers = new Vector();
    
    public void register(CallHandler callHandler){
        logCallHandler(callHandler);
        
        callHandlers.add(callHandler);
    }

    
    
    public Iterator<CallHandler> iterator() {
        return callHandlers.iterator();
    }
    
    public CallHandler getHandlerForCall(String[] postPath){
        //get a copy to sort out the non-fitting callhandlers
        ArrayList<CallHandler> resultSet = new ArrayList(callHandlers);
        
        //sort out - handles with too long signature
        Iterator<CallHandler> iter = resultSet.iterator();
        while (iter.hasNext()){
            CallHandler handler = iter.next();
            if (handler.getSignature().length!=postPath.length){
                iter.remove();
            }            
        }
        
        //sort out non-fitting handlers
        for (int i=0; i<postPath.length;i++){
            String pathSeg = postPath[i];
            
            iter = resultSet.iterator();
            while (iter.hasNext()){
             CallHandler handler = iter.next();
             
                if ((handler.getSignature().length<=i)
                    || (!signatureElementFits(handler.getSignature()[i], pathSeg))){
                    iter.remove();
                    continue;
                }//else
            }            
        }
        if (!resultSet.isEmpty()){
            return resultSet.get(0);
        }
        
        return null;
    }

    private boolean signatureElementFits(DIME_HANDLER_PARAMS sigType, String pathSeg) {
        String sigPattern = CallHandler.DIME_HANDLE_PARAMS_MAP.get(sigType).getSigPattern();
        
        
        if(sigPattern.equals(DimeHandleParamsEntry.SIG_PATTERN_WILDCARD)){
            return true;
        }
        if (sigPattern.equals(pathSeg)){
            return true;
        }
        return false;
        
    }

    private void logCallHandler(CallHandler callHandler) {
        Logger.getLogger(CallHandlerManager.class.getName()).log(Level.INFO, 
                "\nregistering:"+callHandler.getName()+" sig:" +callHandler.getSignatureString());
    }
    
    
    
}
