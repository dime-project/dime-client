package eu.dime.simpleps.api;

import eu.dime.model.GenItem;
import eu.dime.model.TYPES;
import eu.dime.restapi.DimeHelper;
import eu.dime.simpleps.database.PSNotificationsManager;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.web.ServiceComponent;
import sit.web.ServiceComponents;
import sit.web.ServiceEndpointHelper;
import sit.web.WebRequest;

/**
 *
 * @author simon
 */
public class HandleCometCallService extends sit.web.ServiceEndpoint {

    private final long NOTIFICATION_CHECK_SLEEP_TIME = 100;
    private static final Object waitNotificationLock = new Object();
    private final DimeHelper dimeHelper = new DimeHelper();

    //private Vector<Long> threadStack = new Vector();
    // endpoint = dime-communications/push/
    public HandleCometCallService(String endpoint) {
        super(ServiceEndpointHelper.replaceBackSlashes(endpoint), true);

    }

    @Override
    public byte[] handleCall(WebRequest wr) {
        try {
            //dime-communications/push/9702325/@comet
            //https://dime-communications/push/{said_hoster}/{id_device}/@comet

            wr.fname = ServiceEndpointHelper.replaceBackSlashes(wr.fname);
            Logger.getLogger(HandleCometCallService.class.getName()).log(Level.INFO, "handling call:" + wr.fname);

            String postPath = ServiceEndpointHelper.getSubPath(wr.fname, endpointName);
            String[] pathParts = postPath.split("/");

            if ((pathParts.length != 3)
                    || (!pathParts[2].equals("@comet"))) {
                return EndpointHelper.logAccessErrorAndPrepareErrorMessage(HandleCometCallService.class,
                        "invalid comet call - cannot be handled!", "", TYPES.NOTIFICATION).getBytes();
            }

            String hoster = pathParts[0];

            String clientId = pathParts[1];
            
            long startingFrom = Long.parseLong(getValueFromParams(wr, DimeHelper.DIME_NOTIFICATION_PUSH_PARM_STARTING_FROM));
            

            List<GenItem> notifications = this.waitAndGetNextNotifications(clientId, hoster, startingFrom);       
           return dimeHelper.packResponse(notifications).toJson().getBytes();
         
        } catch (UnsupportedEncodingException ex) {
            return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(HandleCometCallService.class, ex, 
                    wr, TYPES.NOTIFICATION, "undefined").getBytes();
        }


    }

    @Override
    public String getMimeType() {
        return "application/json";
    }
    
    private String getValueFromParams(WebRequest wr, String key) throws UnsupportedEncodingException{
        if (key==null){
            return null;
        }
        
        ServiceComponents params =  ServiceEndpointHelper.extractNameValues(wr.param);
        for (ServiceComponent sc : params){
            if (key.equals(sc.getA())){
                return sc.getB();
            }
        }
        return null;
    }

    

    
    
    
    private List<GenItem>  waitAndGetNextNotifications(String clientId, String hoster, long startingFromTime) {

        List<GenItem> result;
        

        result = PSNotificationsManager.getInstance().getNotification(hoster, clientId, startingFromTime);
        if (!result.isEmpty()){
            return result;
        }//else

        //queue all incoming notification requests here
        //when new notification available they will all be served following their client id...        
        synchronized (HandleCometCallService.waitNotificationLock) {

            while (true) {

                result = PSNotificationsManager.getInstance().getNotification(hoster, clientId, startingFromTime);
                if (!result.isEmpty()) {
                    return result;
                }

                try {
                    Thread.sleep(NOTIFICATION_CHECK_SLEEP_TIME);
                } catch (InterruptedException ex) {
                    Logger.getLogger(HandleCometCallService.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }


        }
    }
}
