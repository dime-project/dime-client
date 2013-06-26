package eu.dime.mobile.helper.interfaces;

import java.util.Map;

public interface IContextSensor {
	
	public void start();
	
	public void stop();
	
	public void setScanPeriod(int seconds);
	
	public Map<String,Object> getContext();

}
