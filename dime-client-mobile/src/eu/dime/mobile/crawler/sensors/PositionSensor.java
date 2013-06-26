package eu.dime.mobile.crawler.sensors;

import java.util.HashMap;

import java.util.Map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import eu.dime.model.context.constants.Scopes;

import eu.dime.mobile.crawler.Constants;
import eu.dime.mobile.crawler.ContextCrawler;
import eu.dime.mobile.crawler.Defaults;
import eu.dime.mobile.helper.ContextHelper;
import eu.dime.mobile.helper.interfaces.IContextSensor;
import eu.dime.model.context.ContextItem;

public class PositionSensor implements IContextSensor, LocationListener {
	
	private static PositionSensor sensor = null;
	
	private ContextCrawler crawlerRef = null;
	
	private int scanPeriod = Defaults.DEFAULT_POS_SCAN;
	
	private LocationManager lm;
	private Location currentLocation;
	private boolean running = false;
	private boolean isOnRequest = false;
	
	public static PositionSensor getInstance(ContextCrawler crawler) {
		if (sensor == null) sensor = new PositionSensor(crawler);
		return sensor;
	}
	
	public PositionSensor(ContextCrawler crawler) {
		this.crawlerRef = crawler;
	}

	@Override
	public void start() {
		if (!running) {
			Log.i(Constants.LOG_TAG,"POS sensor started");
			running = true;
			lm = (LocationManager)this.crawlerRef.applicationContext.getSystemService(Context.LOCATION_SERVICE);
			initLocationService();
		}
	}

	@Override
	public void stop() {
		if (running) {
			lm.removeUpdates(this);
			Log.i(Constants.LOG_POS_TAG,"Location update subscription removed");
			running = false;
			Log.i(Constants.LOG_TAG,"POS sensor stopped");
		}
	}
	
	private void initLocationService() {
		if (this.scanPeriod == Constants.POS_ON_REQUEST) {
			isOnRequest = true;
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this,Looper.getMainLooper());
			//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
		} else {
			isOnRequest = false;
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,this.scanPeriod * 1000,200,this,Looper.getMainLooper());
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,this.scanPeriod * 1000,50,this,Looper.getMainLooper());
		}
		Log.i(Constants.LOG_POS_TAG,"Location update subscription requested");
	}

	@Override
	public void setScanPeriod(int seconds) {
		this.scanPeriod = seconds;
		if (lm != null) {
			initLocationService();
		}
	}

	@Override
	public Map<String, Object> getContext() {
		Map<String,Object> context = new HashMap<String, Object>();
		if (currentLocation != null) {
			context.put(Scopes.SCOPE_POSITION_LAT,currentLocation.getLatitude());
			context.put(Scopes.SCOPE_POSITION_LON,currentLocation.getLongitude());
			context.put(Scopes.SCOPE_POSITION_ACC,currentLocation.getAccuracy());
			if (currentLocation.getProvider().equalsIgnoreCase(LocationManager.NETWORK_PROVIDER))
				context.put(Scopes.SCOPE_POSITION_LOCMODE,Constants.LOC_MODE_NETWORK);
			else if (currentLocation.getProvider().equalsIgnoreCase(LocationManager.GPS_PROVIDER))
				context.put(Scopes.SCOPE_POSITION_LOCMODE,Constants.LOC_MODE_GPS);
		}
		return context;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(Constants.LOG_POS_TAG,"onLocationChanged: " + location.getProvider());
		currentLocation = location;
		ContextItem data = ContextHelper.createContextData(Scopes.SCOPE_POSITION,getContext(),this.scanPeriod);
		if (isOnRequest) {
			lm.removeUpdates(this);
			Log.i(Constants.LOG_POS_TAG,"Location update subscription removed");
		} else {
			this.crawlerRef.updateContext(Scopes.SCOPE_POSITION,data);
		}
	}

	@Override
	public void onProviderDisabled(String provider) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

}
