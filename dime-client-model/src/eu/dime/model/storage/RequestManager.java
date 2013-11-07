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

import eu.dime.model.TYPES;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 *
 * @author simon
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class RequestManager {

    public final static long REQUEST_TIME_OUT = 3000; //in millisec
	private ArrayList<TYPES> requested = new ArrayList();
    /**
     * index used to make getNextRequest returning requests following a loop through
     * the requested Set
     */
    private int requestedIndex = 0;
	private TreeSet<TYPES> fetched = new TreeSet();
    private TreeSet<TYPES> failed = new TreeSet();
    private HashMap<TYPES, Long> requestTimeStamp = new HashMap();
    
    /**
     * returns true if fetch type was added to requested
     *
     * @param type
     * @return
     */
    synchronized boolean triggerRequest(TYPES type) {
        if (fetched.contains(type)){
            return false;        
        }        
        //remove from failed - since we will try again now 
        failed.remove(type);
        //only add it once - so the timeStamp will point to the first request
        if (!requested.contains(type)){            
            requested.add(type);
            requestTimeStamp.put(type, System.currentTimeMillis());
        }
        return true;
    }
   
    synchronized void markForRefetch(TYPES type) {
        fetched.remove(type);
        triggerRequest(type);
    }

    synchronized boolean hasOpenRequests() {
        return !requested.isEmpty();
    }

    synchronized boolean wasFetched(TYPES type) {
        return fetched.contains(type);
    }

    synchronized boolean checkForFailedOrTimedOut(TYPES type) {
        if (failed.contains(type)){
            return true;
        }        
        Long timeStamp = requestTimeStamp.get(type);
        if (timeStamp == null){ //no time out for non-existing items
            return false;
        }
        if (System.currentTimeMillis() > timeStamp + REQUEST_TIME_OUT) { //time-out detected - shift to failed
            updateRequestFailed(type);
            return true;
        }
        return false;
    }

    synchronized TYPES getNextRequest() {
        if (requested.isEmpty()){
            return null;
        }
        requestedIndex++;
        if (requestedIndex >= requested.size()){
            requestedIndex = 0;
        }                
        return requested.get(requestedIndex);
    }

    synchronized void updateRequestFailed(TYPES type) {
        requested.remove(type);
        requestTimeStamp.remove(type);
        failed.add(type);
    }

    synchronized void updateRequestSucceeded(TYPES type) {
        requested.remove(type);
        requestTimeStamp.remove(type);
        fetched.add(type);
    }
    
}
