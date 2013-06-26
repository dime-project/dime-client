package eu.dime.mobile.datamining.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.Arrays;

public class Settings {

	// Crawling settings
	 boolean startCrawlingServiceOnStartup; // start the service when the device is booted
	 long lastCrawlTimestamp = 0L; // when was the last time the crawling was executed?
	 long crawlInterval; 				 // how often is the crawler executed, in miliseconds
	 boolean crawlDocuments = false;
	 boolean crawlAudio = false;
	 boolean crawlVideo = false;
	 boolean crawlImages = false;
	
	 long maxFileSize = Integer.MAX_VALUE;
	
	 boolean crawlContacts = false;
	
	// PS settings
	 String personalServerHost;
	 String personalServerProtocol;
	 String personalServerPort;
	 String username;
	 String password;
	
	static final ArrayList<String> extensionsToCheck = new ArrayList<String>();
	

	public  void readPreferences(Context context){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		// PS preferences
		personalServerHost = preferences.getString(Constants.SETTINGS_PS_HOST, Defaults.DEFAULT_PERSONAL_SERVER_URL);		
		personalServerProtocol = preferences.getString(Constants.SETTINGS_PS_PROTOCOL, Defaults.DEFAULT_PERSONAL_SERVER_PROTOCOL);		
		personalServerPort = preferences.getString(Constants.SETTINGS_PS_PORT, Defaults.DEFAULT_PERSONAL_SERVER_PORT); 		
		username = preferences.getString(Constants.SETTINGS_PS_USER, Defaults.DEFAULT_PERSONAL_SERVER_USER);		
		password = preferences.getString(Constants.SETTINGS_PS_PASSWORD, Defaults.DEFAULT_PERSONAL_SERVER_PASSWORD);		
		
		// crawling preferences
		startCrawlingServiceOnStartup = preferences.getBoolean(Constants.SETTINGS_START_ON_STARTUP, Defaults.DEFAULT_START_ON_STARTUP);
		lastCrawlTimestamp = preferences.getLong(Constants.SETTINGS_LAST_CRAWLING_TIMESTAMP_KEY, Defaults.DEFAULT_LAST_CRAWL);		
		crawlInterval = preferences.getLong(Constants.SETTINGS_CRAWLING_INTERVAL, Defaults.DEFAULT_CRAWLING_INTERVAL);
		
		if (crawlInterval < 60000)
			crawlInterval = Defaults.DEFAULT_CRAWLING_INTERVAL;
		
		crawlContacts = preferences.getBoolean(Constants.SETTINGS_CRAWL_CONTACTS, Defaults.DEFAULT_CRAWL_CONTACTS);
		
		// Check which kind(s) of documents we'll crawl 
		crawlDocuments     = preferences.getBoolean(Constants.SETTINGS_CRAWL_DOCUMENTS, Defaults.DEFAULT_CRAWL_DOCUMENTS);
		crawlAudio         = preferences.getBoolean(Constants.SETTINGS_CRAWL_AUDIO, Defaults.DEFAULT_CRAWL_AUDIO);
		crawlVideo         = preferences.getBoolean(Constants.SETTINGS_CRAWL_VIDEO, Defaults.DEFAULT_CRAWL_VIDEO);
		crawlImages        = preferences.getBoolean(Constants.SETTINGS_CRAWL_IMAGES, Defaults.DEFAULT_CRAWL_IMAGES);
								
		extensionsToCheck.clear();
		
		if (crawlDocuments)
			extensionsToCheck.addAll(Arrays.asList(Constants.DOCUMENT_EXTENSIONS));
		
		if (crawlAudio)
			extensionsToCheck.addAll(Arrays.asList(Constants.AUDIO_EXTENSIONS));
		
		if (crawlImages)
			extensionsToCheck.addAll(Arrays.asList(Constants.IMAGE_EXTENSIONS));
		
		if (crawlVideo)
			extensionsToCheck.addAll(Arrays.asList(Constants.VIDEO_EXTENSIONS));
		
		maxFileSize = preferences.getLong(Constants.SETTINGS_MAX_FILE_SIZE, Defaults.DEFAULT_MAX_FILE_SIZE);
		
		
	}
	
	public void savePreferences(Context context){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor preferencesEditor = preferences.edit();
		
		// crawling preferences
		preferencesEditor.putBoolean(Constants.SETTINGS_START_ON_STARTUP, startCrawlingServiceOnStartup);
		preferencesEditor.putLong(Constants.SETTINGS_LAST_CRAWLING_TIMESTAMP_KEY, lastCrawlTimestamp);
		preferencesEditor.putLong(Constants.SETTINGS_CRAWLING_INTERVAL, crawlInterval);
		preferencesEditor.putBoolean(Constants.SETTINGS_CRAWL_AUDIO, crawlAudio);
		preferencesEditor.putBoolean(Constants.SETTINGS_CRAWL_VIDEO, crawlVideo);
		preferencesEditor.putBoolean(Constants.SETTINGS_CRAWL_IMAGES, crawlImages);
		preferencesEditor.putBoolean(Constants.SETTINGS_CRAWL_DOCUMENTS, crawlDocuments);
		preferencesEditor.putLong(Constants.SETTINGS_MAX_FILE_SIZE, maxFileSize);
		
		// PS preferences
		preferencesEditor.putString(Constants.SETTINGS_PS_HOST, personalServerHost);
		preferencesEditor.putString(Constants.SETTINGS_PS_PROTOCOL, personalServerProtocol);
		preferencesEditor.putString(Constants.SETTINGS_PS_PORT, personalServerPort);
		preferencesEditor.putString(Constants.SETTINGS_PS_USER, username);
		preferencesEditor.putString(Constants.SETTINGS_PS_PASSWORD, password);
		
		preferencesEditor.commit();
	}
}
