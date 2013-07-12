package eu.dime.mobile.crawler;

import android.content.Context;
import eu.dime.model.context.ContextItem;

public interface IContextCrawler {
	
	public void setApplicationContext(Context context);
	
	public void start();
	
	public void stop();
	
	public void setConfigParam(String param, String value);
	
	public void enableSensor(String sensor, boolean value);
	
	public ContextItem getCurrentContext(String scope);
	
	public void updateContext(String scope, ContextItem data);

}
