package eu.dime.mobile.crawler;

import java.util.HashMap;
import java.util.Map;

import eu.dime.model.context.constants.Scopes;

public class Defaults {
	
	public static final int DEFAULT_BT_SCAN		= 60;  // seconds 
	public static final int DEFAULT_WF_SCAN		= 60;  // seconds
	//public static final int DEFAULT_POS_SCAN	= Constants.POS_ON_REQUEST;  // seconds
	public static final int DEFAULT_POS_SCAN	= 180;  // seconds
	public static final int DEFAULT_STATUS_SCAN	= 180;  // seconds
	
	public static final int DEFAULT_BT_UPDATE		= DEFAULT_BT_SCAN;  // seconds 
	public static final int DEFAULT_WF_UPDATE		= 150;  // seconds 
	public static final int DEFAULT_POS_UPDATE		= 180;  // seconds 
	public static final int DEFAULT_STATUS_UPDATE	= DEFAULT_STATUS_SCAN;  // seconds 
	
	
	public static final int DEFAULT_VALIDITY	= 600;  // seconds 
	
	public static Map<String,Integer> VALIDITY = new HashMap<String,Integer>();
	static {
		VALIDITY.put(Scopes.SCOPE_BT,Integer.valueOf((int)1.25*DEFAULT_BT_UPDATE));
		VALIDITY.put(Scopes.SCOPE_WF,Integer.valueOf((int)1.25*DEFAULT_WF_UPDATE));
		VALIDITY.put(Scopes.SCOPE_POSITION,Integer.valueOf((int)1.25*DEFAULT_POS_UPDATE));
	}

}
