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

import java.util.HashMap;
import java.util.Map;

import eu.dime.model.context.constants.Scopes;
import eu.dime.mobile.crawler.Constants;
import eu.dime.mobile.crawler.ContextCrawler;
import eu.dime.mobile.crawler.Defaults;
import eu.dime.mobile.helper.ContextHelper;
import eu.dime.model.context.ContextItem;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class BTSensor extends BroadcastReceiver implements IContextSensor, Runnable {
	
	private static BTSensor sensor = null;
	
	private ContextCrawler crawlerRef = null;
	
	private int scanPeriod = Defaults.DEFAULT_BT_SCAN;
	private BluetoothAdapter bta = null;
	
	private Thread thread = null;
	private boolean running = false;
	
	private String localAddress = null;
	private String devices = "";
	private String currentScan = "";
	private HashMap<String, String> actualDevices = new HashMap<String, String>();
	
	public static BTSensor getInstance(ContextCrawler crawler) {
		if (sensor == null) sensor = new BTSensor(crawler);
		return sensor;
	}
	
	public BTSensor(ContextCrawler crawler) {
		this.crawlerRef = crawler;
	}
	
	public void setScanPeriod(int seconds) {
		this.scanPeriod = seconds;
	}

	public void start() {
		
		if (!running) {
		
			Log.i(Constants.LOG_TAG,"BT sensor started");
			
			
			this.crawlerRef.applicationContext.registerReceiver(this, new IntentFilter(
					BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
			this.crawlerRef.applicationContext.registerReceiver(this, new IntentFilter(
					BluetoothAdapter.ACTION_DISCOVERY_STARTED));
			this.crawlerRef.applicationContext.registerReceiver(this, new IntentFilter(
					BluetoothDevice.ACTION_FOUND));
                        
            initBluetooth();
            
            currentScan = "";
            devices = "";
			actualDevices.clear();
			
            running = true;
            
			if (thread == null) {
				thread = new Thread(this);
			    thread.start();
			} 
			
			
		}
	}
        
    private boolean initBluetooth() {
        
        if (this.bta == null) {
            this.bta = BluetoothAdapter.getDefaultAdapter();
        }
        if ((this.bta != null) && this.bta.isEnabled()) {
            if (this.localAddress == null && (this.bta.getAddress() != null)) { //set address if necessary
                this.localAddress = this.bta.getAddress().replaceAll(":", "").toUpperCase();
            }
            return true;
        }
        return false;
    }

	public void stop() {
		if (running) {
			running = false;
			this.crawlerRef.applicationContext.unregisterReceiver(this);
			Log.i(Constants.LOG_TAG,"BT sensor stopped");
		}
	}

	public Map<String, Object> getContext() {
		HashMap<String,Object> context = new HashMap<String, Object>();
		context.put(Scopes.SCOPE_BT_LOCAL,this.localAddress);
		context.put(Scopes.SCOPE_BT_LIST,this.devices);
		return context;
	}

	@Override
	public void onReceive(Context arg0, Intent i) {
		
		if (!running) return;
		
		if (i.getAction().compareTo(BluetoothAdapter.ACTION_DISCOVERY_STARTED) == 0) {
			Log.v(Constants.LOG_BT_TAG,"Intent received: " + i.getAction());
			currentScan = "";
			actualDevices.clear();
		} else if (i.getAction().compareTo(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) == 0) {
			Log.v(Constants.LOG_BT_TAG,"Intent received: " + i.getAction());
			devices = currentScan;
			ContextItem bt = ContextHelper.createContextData(Scopes.SCOPE_BT,getContext(),scanPeriod*2);
			this.crawlerRef.updateContext(Scopes.SCOPE_BT,bt);
		} else if (i.getAction().compareTo(BluetoothDevice.ACTION_FOUND) == 0) {
			BluetoothDevice device = i.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (device != null) {			
				if (!actualDevices.containsKey(device.getAddress())) {
					actualDevices.put(device.getAddress(), "");
					Log.v(Constants.LOG_BT_TAG,"Intent received: " + i.getAction() + ": " + device.getAddress());
					if (!currentScan.equalsIgnoreCase("")) {
						currentScan += ";";
                    }
					currentScan += device.getAddress().replaceAll(":", "").toUpperCase();
				}			
			} else {
				Log.d(Constants.LOG_BT_TAG,"No BT device found");
            }
		}
		
	}

	public void run() {
		
		while (true) {
			
			if (running) { 
				Log.d(Constants.LOG_BT_TAG,"BT scan started");
				
				try {
					if (initBluetooth()) {
						bta.startDiscovery();                                        					
					}             
				} catch (Exception e) {
					Log.e(Constants.LOG_BT_TAG,e.toString());
				}
			}
			 
			synchronized (this) { //TODO check synchronized waiting --> potential deadlock!
				try {
					wait(scanPeriod * 1000);
				} catch (InterruptedException e) {
					Log.e(Constants.LOG_BT_TAG,e.toString());
				}
			}
			
		}	
	}

}
