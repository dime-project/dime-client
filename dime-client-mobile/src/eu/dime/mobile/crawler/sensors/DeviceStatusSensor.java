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

package eu.dime.mobile.crawler.sensors;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import eu.dime.mobile.crawler.Constants;
import eu.dime.mobile.crawler.ContextCrawler;
import eu.dime.mobile.crawler.Defaults;
import eu.dime.mobile.helper.ContextHelper;
import eu.dime.model.context.ContextItem;
import eu.dime.model.context.constants.Scopes;

public class DeviceStatusSensor implements IContextSensor, Runnable {
	
	private static DeviceStatusSensor sensor = null;
	
	private ContextCrawler crawlerRef = null;
	
	private int scanPeriod = Defaults.DEFAULT_STATUS_SCAN;
	
	private PowerManager pm = null;
	private TelephonyManager tm = null;
	
	private String datePattern = "yyyy-MM-dd'T'HH:mm:ssZ";
	private SimpleDateFormat dateFormat = null;
	private String lastActive = "";
	
	
	private Thread thread = null;
	private boolean running = false;
	
	public static DeviceStatusSensor getInstance(ContextCrawler crawler) {
		if (sensor == null) sensor = new DeviceStatusSensor(crawler);
		return sensor;
	}
	
	public DeviceStatusSensor(ContextCrawler crawler) {
		this.crawlerRef = crawler;
		this.pm = (PowerManager)this.crawlerRef.applicationContext.getSystemService(Context.POWER_SERVICE);
		this.tm = (TelephonyManager)this.crawlerRef.applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
		this.dateFormat = new SimpleDateFormat(this.datePattern);
	}

	@Override
	public void run() {
		while (true) {		
			if (running) { 
				ContextItem status = ContextHelper.createContextData(Scopes.SCOPE_STATUS,getContext(),scanPeriod*2);
				this.crawlerRef.updateContext(Scopes.SCOPE_STATUS,status);
				Log.i(Constants.LOG_STATUS_TAG,"Updating STATUS context");
			}	 
			synchronized (this) { 
				try {
					wait(scanPeriod * 1000);
				} catch (InterruptedException e) {
					Log.e(Constants.LOG_STATUS_TAG,e.toString());
				}
			}
		}	
	}

	@Override
	public void start() {
		if (!running) {
			Log.i(Constants.LOG_TAG,"STATUS sensor started");
			running = true;
			if (thread == null) {
				thread = new Thread(this);
			    thread.start();
			} 
		}
	}

	@Override
	public void stop() {
		if (running) {
			running = false;
			Log.i(Constants.LOG_TAG,"STATUS sensor stopped");
		}
	}

	@Override
	public void setScanPeriod(int seconds) {
		this.scanPeriod = seconds;
	}

	@Override
	public Map<String, Object> getContext() {
		HashMap<String,Object> context = new HashMap<String, Object>();
		context.put(Scopes.SCOPE_STATUS_KEEPALIVE,true);
		context.put(Scopes.SCOPE_STATUS_IS_ALIVE, isAlive());
		context.put(Scopes.SCOPE_STATUS_LAST_ACTIVE, this.lastActive);
		return context;
	}

	private boolean isAlive() {
		if (pm.isScreenOn()) {
			this.lastActive = formatDate(System.currentTimeMillis());
			return true;
		} else {
			if (this.tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK
				|| this.tm.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
				this.lastActive = formatDate(System.currentTimeMillis());
				return true;
			}
		}
		return false;
	}

	private String formatDate(long time) {
		String tmp = this.dateFormat.format(time);
		return tmp.substring(0,tmp.length()-2) + ":" + tmp.substring(tmp.length()-2);
	}

	

}