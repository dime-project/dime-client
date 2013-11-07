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

package eu.dime.mobile.crawler;

import android.content.Context;
import android.util.Log;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.crawler.sensors.BTSensor;
import eu.dime.mobile.crawler.sensors.DeviceStatusSensor;
import eu.dime.mobile.crawler.sensors.IContextSensor;
import eu.dime.mobile.crawler.sensors.PositionSensor;
import eu.dime.mobile.crawler.sensors.WFSensor;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ContextHelper;
import eu.dime.model.GenItem;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.TYPES;
import eu.dime.model.context.ContextItem;
import eu.dime.model.context.constants.Scopes;
import eu.dime.restapi.DimeHelper;
import eu.dime.restapi.JSONResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextCrawler implements IContextCrawler {
	
	public Context applicationContext = null;
	private boolean isRunning = false;
	private Map<String,IContextSensor> sensors = null;
	
	
	private DimeHelper helper = new DimeHelper();
	
	public ContextCrawler() {
		this.sensors = new HashMap<String, IContextSensor>();
	}

	public void start() {
		if (isRunning) return;
		Set<String> keys = this.sensors.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			IContextSensor sensor = this.sensors.get(it.next());
			sensor.start();
		}
		isRunning = true;
	}

	public void stop() {
		if (!isRunning) return;
		Set<String> keys = this.sensors.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			IContextSensor sensor = this.sensors.get(it.next());
			sensor.stop();
		}
		isRunning = false;
	}

	public void setConfigParam(String param, String value) {
		if (param.startsWith(Constants.PAR_SCAN_PREFIX)) {
			String scope = param.substring(Constants.PAR_SCAN_PREFIX.length(),param.length());
			IContextSensor sensor = this.sensors.get(scope);
			if (sensor != null) {
				sensor.setScanPeriod(Integer.parseInt(value));
			}
		}
	}
	
	@Override
	public void enableSensor(String sensor, boolean value) {
		if (value) {
			if (sensor.equalsIgnoreCase(Scopes.SCOPE_BT)) 
				this.sensors.put(Scopes.SCOPE_BT,BTSensor.getInstance(this));
			else if (sensor.equalsIgnoreCase(Scopes.SCOPE_WF)) 
				this.sensors.put(Scopes.SCOPE_WF,WFSensor.getInstance(this));
			else if (sensor.equalsIgnoreCase(Scopes.SCOPE_POSITION)) 
				this.sensors.put(Scopes.SCOPE_POSITION,PositionSensor.getInstance(this));
			else if (sensor.equalsIgnoreCase(Scopes.SCOPE_STATUS)) 
				this.sensors.put(Scopes.SCOPE_STATUS,DeviceStatusSensor.getInstance(this));
			else Log.w(Constants.LOG_TAG,"Sensor " + sensor + " not available");
		} else {
			IContextSensor s = null;
			if (sensor.equalsIgnoreCase(Scopes.SCOPE_BT)) {
				s = BTSensor.getInstance(this);
				s.stop();
			} else if (sensor.equalsIgnoreCase(Scopes.SCOPE_WF)) {
				s = WFSensor.getInstance(this);
				s.stop();
			} else if (sensor.equalsIgnoreCase(Scopes.SCOPE_POSITION)) {
				s = PositionSensor.getInstance(this);
				s.stop();
			} else if (sensor.equalsIgnoreCase(Scopes.SCOPE_STATUS)) {
				s = DeviceStatusSensor.getInstance(this);
				s.stop();
			} else Log.w(Constants.LOG_TAG,"Sensor " + sensor + " not available");
		}
	}

	public void setApplicationContext(Context context) {
		this.applicationContext = context;
	}

	@Override
	public void updateContext(String scopes, ContextItem data) {
		Log.i(Constants.LOG_TAG,"Firing context data.. (" + scopes + ")");
		AndroidModelHelper.updateContextAsynchronously(data);
	}

	@Override
	public ContextItem getCurrentContext(String scope) {
		IContextSensor sensor = this.sensors.get(scope);
		if (sensor != null) {
			Map<String,Object> context = sensor.getContext();
			Integer validity = Defaults.VALIDITY.get(scope);
			if (validity == null) validity = Integer.valueOf(Defaults.DEFAULT_VALIDITY);
			return ContextHelper.createContextData(scope,context,validity.intValue());
		} else {
			Log.d(Constants.LOG_TAG,"No local sensors support scope --> invoking PS for " + scope + "..");
			JSONResponse result = helper.doDIMEJSONGET(DimeHelper.DIME_BASIC_PATH 
                                + DimeClient.getUserMainSaid() + "/context/" + Model.ME_OWNER + "/" 
                                + Scopes.SCOPE_CURRENT_PLACE, "", DimeClient.getSettings().getRestApiConfiguration());
			if (result == null) {
	            return null;
	        }
	        result.parseJSONResponse();
	        if (result.hasError()) {
	            Logger.getLogger(ContextCrawler.class.getName()).log(Level.SEVERE, "Error:\n" + result.toString());
	        }
	        if ((result != null) && (!result.hasError()) && (!result.replyObjects.isEmpty())) {
	        	try {
	                GenItem item = ItemFactory.createNewItemByJSON(TYPES.CONTEXT, result.replyObjects.get(0));
	                return (ContextItem)item;
	            } catch (InvalidJSONItemException ex) {
	                Logger.getLogger(ContextCrawler.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
	                Logger.getLogger(ContextCrawler.class.getName()).log(Level.INFO, "json:\n" + result.replyObjects.get(0).toString());
	            }
	            return null;
	        } else return null;
		}
	}

}
