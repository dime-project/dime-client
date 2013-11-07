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
package eu.dime.simpleps.api;

import eu.dime.simpleps.api.callhandler.AdHocGroupCallHandler;
import eu.dime.simpleps.api.callhandler.AdvisoryRequestHandler;
import eu.dime.simpleps.api.callhandler.CallHandler;
import eu.dime.simpleps.api.callhandler.CallHandlerManager;
import eu.dime.simpleps.api.callhandler.CollectionCallHandler;
import eu.dime.simpleps.api.callhandler.CreateItemCallHandler;
import eu.dime.simpleps.api.callhandler.DIME_HANDLER_PARAMS;
import eu.dime.simpleps.api.callhandler.DebugCallHandler;
import eu.dime.simpleps.api.callhandler.DumpCallHandler;
import eu.dime.simpleps.api.callhandler.FileUploadCallHandler;
import eu.dime.simpleps.api.callhandler.GlobalCollectionCallHandler;
import eu.dime.simpleps.api.callhandler.ParamsMap;
import eu.dime.simpleps.api.callhandler.SharedToAgentCallHandler;
import eu.dime.simpleps.api.callhandler.SingleItemCallHandler;
import eu.dime.simpleps.api.callhandler.SituationCallHandler;
import eu.dime.simpleps.api.callhandler.UploadCallHandler;
import eu.dime.simpleps.api.callhandler.UserCallHandler;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import sit.web.ServiceEndpointHelper;
import sit.web.WebRequest;

/**
 *
 * @author simon
 */
public class HandleGenericHostCalls extends sit.web.ServiceEndpoint {

    private final CallHandlerManager handlerManager = new CallHandlerManager();
    
    public HandleGenericHostCalls(String endpointName) {
        super(ServiceEndpointHelper.replaceBackSlashes(endpointName), true);
        registerHandler();
    }
    
    private void registerHandler(){
        //ATTENTION the order of the handlers matters - keep more specific handlers before generic handlers
        handlerManager.register(new DebugCallHandler());
        handlerManager.register(new UserCallHandler());
        handlerManager.register(new FileUploadCallHandler());
        handlerManager.register(new SharedToAgentCallHandler());
        handlerManager.register(new CollectionCallHandler());
        handlerManager.register(new GlobalCollectionCallHandler());
        handlerManager.register(new AdHocGroupCallHandler());
        handlerManager.register(new DumpCallHandler());
        handlerManager.register(new UploadCallHandler());    
        handlerManager.register(new AdvisoryRequestHandler());    
        handlerManager.register(new SingleItemCallHandler());
        handlerManager.register(new CreateItemCallHandler());
        handlerManager.register(new SituationCallHandler());
    }
    
    String testManager(String postPath){
        String[] pathParts = postPath.split("/");
        CallHandler ch = handlerManager.getHandlerForCall(pathParts);
        if (ch==null){
            return postPath + ": not found";
        }//else
        ParamsMap params = ch.createParamsMap(pathParts);
        StringBuilder paramsStr = new StringBuilder("\n");
        for (Map.Entry<DIME_HANDLER_PARAMS, String> entry : params.entrySet()){
            paramsStr.append(entry.getKey().toString()).append(": ").append(entry.getValue()).append("\n");
        }
        return postPath +": "+ ch.getName()+paramsStr;
    }
    
    @Override
    public byte[] handleCall(WebRequest wr) {
    	wr.fname = ServiceEndpointHelper.replaceBackSlashes(wr.fname);
        Logger.getLogger(HandleGenericHostCalls.class.getName()).log(Level.INFO, "handling call:" +wr.httpCommand+" "+ wr.fname);
        String postPath = ServiceEndpointHelper.getSubPath(wr.fname, endpointName);
        String[] pathParts = postPath.split("/");      
        CallHandler callHandler = handlerManager.getHandlerForCall(pathParts);
        if (callHandler!=null){
            ParamsMap params = callHandler.createParamsMap(pathParts);
            return callHandler.handleCall(wr, params).getBytes();
        }
        //no call handle found
        return EndpointHelper.logAccessErrorAndPrepareErrorMessage(HandleGenericHostCalls.class, "no handler found for call!"+wr.toString(), "unknown", "unknown").getBytes();
    }

    @Override
    public String getMimeType() {
        return "application/json";
    }
    
      /**
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        HandleGenericHostCalls instance = new HandleGenericHostCalls("blubb");
        try { //wait a bit to print register log first (HACK)
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(HandleGenericHostCalls.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(instance.testManager("hoster/type/owner/@all"));
        System.out.println(instance.testManager("hoster/type/owner/d"));
        System.out.println(instance.testManager("hoster/type/owner/@all/blib"));
        System.out.println(instance.testManager("hoster/type/@all"));
        System.out.println(instance.testManager("hoster/type/owner/"));
        System.out.println(instance.testManager("hoster/type/owner/@sharedTo/agentId/itemId"));
        System.out.println(instance.testManager("9702325/@dump"));
        System.out.println(instance.testManager("9702325/system/ping"));
        System.out.println(instance.testManager("9702325/@createAdHoc"));
        System.out.println(instance.testManager("9702325/resource/crawler"));
        System.out.println(instance.testManager("ana02/livepost/@me/@all/shared"));
    }
    
}
