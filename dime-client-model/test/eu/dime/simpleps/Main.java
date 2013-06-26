package eu.dime.simpleps;

import eu.dime.model.storage.InitStorageFailedException;
import eu.dime.restapi.DimeHelper;
import eu.dime.simpleps.api.EndpointHelper;
import eu.dime.simpleps.api.HandleAuthCall;
import eu.dime.simpleps.api.HandleCometCallService;
import eu.dime.simpleps.api.HandleGenericHostCalls;
import eu.dime.simpleps.api.HandlePingCallService;
import eu.dime.simpleps.api.HandleUserCalls;
import eu.dime.simpleps.database.DatabaseAccess;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import sit.threads.ShutdownHookDelegation;
import sit.threads.ThreadHelper;
import sit.web.ServiceEndpoints;
import sit.web.WebServer;

/**
 *
 * @author simon
 */
public class Main {
    

    public Main() {
        
    }
    
    
    private void registerServices() {

        //register generic call handler for ../api/dime/rest/...
        String endpoint = EndpointHelper.prepareEndpoint(DimeHelper.DIME_BASIC_PATH);
        Logger.getLogger(Main.class.getName()).log(
                Level.INFO, "registering HandleGenericHostCalls at " + endpoint);       
        HandleGenericHostCalls genService = new HandleGenericHostCalls(endpoint);
        ServiceEndpoints.getInstance().addEndpoint(genService);
        
        //register comet call for ../push/..
        endpoint = EndpointHelper.prepareEndpoint(DimeHelper.DIME_NOTIFICATION_PATH);
        Logger.getLogger(Main.class.getName()).log(
                Level.INFO, "registering HandleCometCallService at " + endpoint); 
        HandleCometCallService cometService = new HandleCometCallService(endpoint);
        ServiceEndpoints.getInstance().addEndpoint(cometService);
        
        //register user call for ../api/dime/user/..
        endpoint = EndpointHelper.prepareEndpoint(DimeHelper.DIME_ADMIN_PATH);
        Logger.getLogger(Main.class.getName()).log(
                Level.INFO, "registering HandleUserCalls at " + endpoint); 
        HandleUserCalls adminService = new HandleUserCalls(endpoint);        
        ServiceEndpoints.getInstance().addEndpoint(adminService);
        
        //register auth call  /dime-communications/web/access/auth/@me
        endpoint = EndpointHelper.prepareEndpoint("/dime-communications/web/access/auth/");
        Logger.getLogger(Main.class.getName()).log(
                Level.INFO, "registering HandleUserCalls at " + endpoint); 
        HandleAuthCall authService = new HandleAuthCall(endpoint);        
        ServiceEndpoints.getInstance().addEndpoint(authService);
        
        //register ping  /dime-communications/api/dime/system/ping
        endpoint = EndpointHelper.prepareEndpoint("/dime-communications/api/dime/system/ping");
        Logger.getLogger(Main.class.getName()).log(
                Level.INFO, "registering HandleUserCalls at " + endpoint); 
        HandlePingCallService pingService = new HandlePingCallService(endpoint);        
        ServiceEndpoints.getInstance().addEndpoint(pingService);
        
    }
    

    

    private void start(boolean  persistence) throws InitStorageFailedException {
        
        // load logging property file property
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream(new File("resources/simpleps.logging.properties")));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        DatabaseAccess.init(persistence);
        
        
        registerServices();
        


        ThreadHelper.registerShutdownHook(new ShutdownHookDelegation() {

            @Override
            public void executeOnShutdown() {
                DatabaseAccess.shutdownStorage();
            }
        });



        WebServer.getInstance().setRoot(new File("resources/www"));
        WebServer.getInstance().setPermitDirectoryListing(true);
        WebServer.getInstance().setPort(8080);
        Thread webThread = new Thread(WebServer.getInstance());
        webThread.start();
        
       
    }

    /**
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            
            boolean persistence = true;
            new Main().start(persistence);
        } catch (InitStorageFailedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
