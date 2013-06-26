package eu.dime.mobile.datamining.utility;

public class Constants {

	public final static String SETTINGS_START_ON_STARTUP = "startServiceOnDeviceBoot";
	
	// File Crawling Settings keys
	public final static String SETTINGS_LAST_CRAWLING_TIMESTAMP_KEY = "LastCrawlingTimestamp";
	
	public final static String SETTINGS_CRAWLING_INTERVAL = "crawlingInterval";
	
	public final static String SETTINGS_CRAWL_DOCUMENTS = "crawlDocuments";
	
	public final static String SETTINGS_CRAWL_VIDEO = "crawlVideos";
	
	public final static String SETTINGS_CRAWL_AUDIO = "crawlAudio";
	
	public final static String SETTINGS_CRAWL_IMAGES = "crawlImages";
	
	public final static String SETTINGS_CRAWL_CONTACTS = "crawlContacts";
	
	public final static String SETTINGS_MAX_FILE_SIZE = "maxFileSize";
	
	
	// IP [or] main domain; examples: "123.123.123.123" [or] www.personalserver.org
	// no protocol, colon, slashes or port number
	public final static String SETTINGS_PS_HOST = "personalServerHost";
	
	// protocol: http:// [or] https:// (colon and slashes must be included)
	public final static String SETTINGS_PS_PROTOCOL = "personalServerProtocol";
	
	public final static String SETTINGS_PS_PORT = "personalServerPort";
	
	public final static String SETTINGS_PS_USER = "personalServerUser";
	
	public final static String SETTINGS_PS_PASSWORD = "personalServerPassword";
	
		
	// Allowed file extensions for each file type
	public static final String[] DOCUMENT_EXTENSIONS = {".doc", ".pdf", ".odt", ".xls", ".xlsx"};
	public static final String[] AUDIO_EXTENSIONS    = {".mp3", ".wav", ".ogg", ".wma", ".flac"};
	public static final String[] VIDEO_EXTENSIONS    = {".wmv", ".avi", ".mov", ".divx", ".mpg", ".3gp", ".mkv", ".mpeg"};
	public static final String[] IMAGE_EXTENSIONS    = {".jpg", ".jpeg", ".gif", ".bmp"};
}
