package eu.dime.mobile.datamining.utility;

public class Defaults {

	// Default settings values
	public static final long DEFAULT_LAST_CRAWL = 0L;
	
	public static final boolean DEFAULT_START_ON_STARTUP = true;
	
	// Default interval: every 24 hours 
	public static final long DEFAULT_CRAWLING_INTERVAL = 1000*60*60*24; //miliseconds
	
	public static final boolean DEFAULT_CRAWL_VIDEO     = false;
	public static final boolean DEFAULT_CRAWL_AUDIO     = false;
	public static final boolean DEFAULT_CRAWL_DOCUMENTS = false;
	public static final boolean DEFAULT_CRAWL_IMAGES    = true;
	
	public static final boolean DEFAULT_CRAWL_CONTACTS  = true;
	
	public static final String DEFAULT_PERSONAL_SERVER_URL = "172.20.100.127";
	
	public static final String DEFAULT_PERSONAL_SERVER_PORT = "8080"; 
	
	public static final String DEFAULT_PERSONAL_SERVER_PROTOCOL = "http://";
	
	public static final String DEFAULT_PERSONAL_SERVER_USER = "guest";
	
	public static final String DEFAULT_PERSONAL_SERVER_PASSWORD = "dimepass4guest";
	
	public static final long DEFAULT_MAX_FILE_SIZE = Long.MAX_VALUE;
	
}
