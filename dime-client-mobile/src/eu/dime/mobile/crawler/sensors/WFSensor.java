package eu.dime.mobile.crawler.sensors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import eu.dime.model.context.constants.Scopes;
import eu.dime.mobile.crawler.Constants;
import eu.dime.mobile.crawler.ContextCrawler;
import eu.dime.mobile.crawler.Defaults;
import eu.dime.mobile.crawler.data.WiFi;
import eu.dime.mobile.helper.ContextHelper;
import eu.dime.model.context.ContextItem;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WFSensor extends BroadcastReceiver implements IContextSensor, Runnable {
	
	private static WFSensor sensor = null;
	
	private ContextCrawler crawlerRef = null;
	
	private int scanPeriod = Defaults.DEFAULT_WF_SCAN;
	private WifiManager wfm = null;
	private HashMap<String,WiFi> activeScans = new HashMap<String, WiFi>();
	private List<WiFi> filteredScans = new ArrayList<WiFi>();
	private boolean isFirst = true;
	private long lastFiredTimestamp;
	
	private Thread thread = null;
	private boolean running = false;
	
	public static WFSensor getInstance(ContextCrawler crawler) {
		if (sensor == null) sensor = new WFSensor(crawler);
		return sensor;
	}
	
	public WFSensor(ContextCrawler crawler) {
		this.crawlerRef = crawler;
	}

	public void start() {
		
		if (!running) {	
			Log.i(Constants.LOG_TAG,"WF sensor started");
			
			this.wfm = (WifiManager)this.crawlerRef.applicationContext.getSystemService(Context.WIFI_SERVICE);
			this.crawlerRef.applicationContext.registerReceiver(this, new IntentFilter(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			
			activeScans.clear();
			isFirst = true;
			
			running = true;
			if (thread == null) {
				thread = new Thread(this);
				thread.start();
			}
		}
		
	}

	public void stop() {
		if (running) {
			running = false;
			this.crawlerRef.applicationContext.unregisterReceiver(this);
			Log.i(Constants.LOG_TAG,"WF sensor stopped");
		}
	}

	public void setScanPeriod(int seconds) {
		this.scanPeriod = seconds;
	}

	public Map<String, Object> getContext() {
		Map<String, Object> context = new HashMap<String, Object>();
		LinkedList<String> wfNames = new LinkedList<String>();
		LinkedList<String> wfList = new LinkedList<String>();
		LinkedList<Integer> wfSignals = new LinkedList<Integer>();
		Iterator<WiFi> it = filteredScans.iterator();
		while (it.hasNext()) {
			WiFi ap = it.next();
			wfNames.add(ap.getSsid());
			String wfAddr = ap.getMacAddress().replaceAll(":","");
			wfList.add(wfAddr.toUpperCase());
			wfSignals.add(ap.getSignalStrength());
		}
		context.put(Scopes.SCOPE_WF_NAMES,wfNames);
		context.put(Scopes.SCOPE_WF_LIST,wfList);
		context.put(Scopes.SCOPE_WF_SIGNALS,wfSignals);
		return context;
	}

	public void run() {
		
		while (true) {
			
			if (running) {
				Log.d(Constants.LOG_WF_TAG,"WF scan started");
				
				try {
					this.wfm.startScan();
				} catch (Exception e) {
					Log.e(Constants.LOG_WF_TAG,e.toString());
				}
			}
			
			synchronized (this) {
				try {
					wait(scanPeriod * 1000);
				} catch (InterruptedException e) {
					Log.e(Constants.LOG_WF_TAG,e.toString());
				}
			}
		}	
	}

	@Override
	public void onReceive(Context arg0, Intent i) {
		
		if (!running) return;
		
		Log.v(Constants.LOG_WF_TAG,"Intent received: " + i.getAction());
		long now = System.currentTimeMillis();
		List<ScanResult> results = wfm.getScanResults();
		
		if (isFirst) {
			isFirst = false;
			lastFiredTimestamp = now;
			filteredScans = convertScanResults(results);
			ContextItem wf = ContextHelper.createContextData(Scopes.SCOPE_WF,getContext(),scanPeriod*2);
			this.crawlerRef.updateContext(Scopes.SCOPE_WF,wf);
		} else {
			// add last scan to active scans
			addToActiveScans(results);
			logActiveScans();
			if ((now - lastFiredTimestamp) > (Defaults.DEFAULT_WF_UPDATE * 0.9 * 1000)) {
				filteredScans = filterActiveResults();
				logFilteredScans();
				lastFiredTimestamp = now;
				activeScans.clear();
				ContextItem wf = ContextHelper.createContextData(Scopes.SCOPE_WF,getContext(),scanPeriod*2);
				this.crawlerRef.updateContext(Scopes.SCOPE_WF,wf);
			} 
		}
		
	}
	
	private List<WiFi> convertScanResults(List<ScanResult> results) {
		List<WiFi> aps = new ArrayList<WiFi>();
		Iterator<ScanResult> it = results.iterator();
		while (it.hasNext()) {
			ScanResult res = it.next();
			aps.add(new WiFi(res.BSSID,res.SSID,res.level,1));
		}
		return aps;
	}
	
	private void addToActiveScans(List<ScanResult> results) {
		// this method add last scan to active scans
		if (results.size() == 0) Log.v(Constants.LOG_WF_TAG,"Empty WF scan received");
		Iterator<ScanResult> it = results.iterator();
		while (it.hasNext()) {
			ScanResult res = it.next();
			String mac = res.BSSID;
			WiFi ap = activeScans.get(mac);
			Log.v(Constants.LOG_WF_TAG,"AP read: " + mac + " - ss: " + res.level);
			if (ap == null) {
				ap = new WiFi(mac,res.SSID,(-1)*res.level,1);
				Log.v(Constants.LOG_WF_TAG,"AP added: " + mac + " - ss: " + ap.getSignalStrength() + " occ: " + ap.getOccourrences());
				activeScans.put(mac,ap);
			} else {
				int sumss = ap.getSignalStrength() + (-1)*res.level; 
				ap.setSignalStrength(sumss);
				ap.setOccourrences(ap.getOccourrences() + 1);
				Log.v(Constants.LOG_WF_TAG,"AP updated: " + mac + " - ss: " + ap.getSignalStrength() + " occ: " + ap.getOccourrences());
				activeScans.put(mac,ap);
			}
		}
	}
	
	private List<WiFi> filterActiveResults() {
		Set<Entry<String, WiFi>> entries = activeScans.entrySet();
		Iterator<Entry<String, WiFi>> it = entries.iterator();
		List<WiFi> filteredAps = new ArrayList<WiFi>();
		while (it.hasNext()) {
			Entry<String, WiFi> ap = it.next();
			String mac = ap.getKey();
			WiFi data = ap.getValue();
			filteredAps.add(new WiFi(mac,data.getSsid(),(-1)*data.getSignalStrength()/data.getOccourrences(),data.getOccourrences()));
		}
		return filteredAps;
	}
	
	private void logActiveScans() {
		Log.d(Constants.LOG_WF_TAG,"####################");
		Log.d(Constants.LOG_WF_TAG,"Active scans");
		Set<Entry<String, WiFi>> entries = activeScans.entrySet();
		Iterator<Entry<String, WiFi>> it = entries.iterator();
		while (it.hasNext()) {
			Entry<String, WiFi> ap = it.next();
			String mac = ap.getKey();
			WiFi data = ap.getValue();
			Log.d(Constants.LOG_WF_TAG,mac + " - ss: " + data.getSignalStrength() + " occ: " + data.getOccourrences());
		}
		Log.d(Constants.LOG_WF_TAG,"####################");
	}
	
	private void logFilteredScans() {
		Log.d(Constants.LOG_WF_TAG,"####################");
		Log.d(Constants.LOG_WF_TAG,"Filtered scans");
		Iterator<WiFi> it = filteredScans.iterator();
		while (it.hasNext()) {
			WiFi ap = it.next();
			Log.d(Constants.LOG_WF_TAG,ap.getMacAddress() + " - ss: " + ap.getSignalStrength() + " (" + ap.getOccourrences() + ")");
		}		
		Log.d(Constants.LOG_WF_TAG,"####################");
	}

}