/*
 *  Description of Settings
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 02.07.2012
 */
package eu.dime.mobile.settings;

import android.content.Context;
import android.content.SharedPreferences;
import eu.dime.model.ModelConfiguration;
import eu.dime.restapi.DimeHelper;
import eu.dime.restapi.RestApiConfiguration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.tools.CryptHelper;
import sit.web.client.HttpHelper;

/**
 * Settings
 * 
 */
public class Settings {
	
    private static final String LOGIN_PREF_ACCEPTED_TAG = "LOGIN_PREF_ACCEPTED";
    private static final String LOGIN_PREF_REMEMBER_TAG = "LOGIN_PREF_REMEMBER";
    private static final String DIME_SETTINGS_TAG = "DIME_SETTINGS";
    private static final String PS_CRAWL_CONTEXT_TAG = "PS_CRAWL_CONTEXT";
    private static final String PS_IP_ADDRESS_TAG = "PS_IP_ADDRESS";
    private static final String PS_KEY_TAG = "PS_KEY";
    private static final String PS_PORT_TAG = "PS_PORT";
    private static final String PS_USER_NAME_TAG = "PS_USER_NAME";
    private static final String PS_USE_HTTPS_TAG = "PS_USE_HTTPS";
    private static final String PS_OVERRIDE_DNS = "PS_OVERRIDE_DNS";
    private static final String IS_DIALOG_INFO_AREA_DISPLAYED = "IS_DIALOG_INFO_AREA_DISPLAYED";
    private static final String SET_PREF_ACCEPTED_TAG = "SET_PREF_ACCEPTED_TAG";
    private final static boolean USE_REST_API = true;
    private final static boolean RETRIEVE_NOTIFICATIONS = true;
    private final Context appContext;
    private String hostname;
    private String username; //== mainSAID
    private String password="";
    private int port;
    private boolean useHTTPS;
    private boolean receiveNotifications = RETRIEVE_NOTIFICATIONS;
    private boolean crawlingContext;
    private boolean loginPrefAccepted;
    private boolean loginPrefRemembered;
    private boolean overrideDNS;
    private boolean isDialogInfoAreaDisplayed;
    private boolean isSetPrefAccepted;

    public Settings(Context appContext) {
        this.appContext = appContext;
        initSettingsFromAndroid();
    }
    
    private void initSettingsFromAndroid(){
        SharedPreferences androidSettings = appContext.getSharedPreferences(DIME_SETTINGS_TAG, Context.MODE_PRIVATE);
        hostname = androidSettings.getString(PS_IP_ADDRESS_TAG, DimeHelper.DEFAULT_HOSTNAME);
        port = androidSettings.getInt(PS_PORT_TAG, DimeHelper.DEFAULT_PORT);
        useHTTPS = androidSettings.getBoolean(PS_USE_HTTPS_TAG, DimeHelper.DEFAULT_USE_HTTPS);
        crawlingContext = androidSettings.getBoolean(PS_CRAWL_CONTEXT_TAG, false);               
        username = androidSettings.getString(PS_USER_NAME_TAG,  "");
        overrideDNS = androidSettings.getBoolean(PS_OVERRIDE_DNS, false);       
        try {
            password = CryptHelper.getUnObscuredContent(username, androidSettings.getString(PS_KEY_TAG, ""));
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        loginPrefAccepted = androidSettings.getBoolean(LOGIN_PREF_ACCEPTED_TAG, false);       
        loginPrefRemembered = androidSettings.getBoolean(LOGIN_PREF_REMEMBER_TAG, false);
        isDialogInfoAreaDisplayed = androidSettings.getBoolean(IS_DIALOG_INFO_AREA_DISPLAYED, false);
        isSetPrefAccepted = androidSettings.getBoolean(SET_PREF_ACCEPTED_TAG, false);
    }

    private void storeSettings(){
        SharedPreferences androidSettings = appContext.getSharedPreferences(DIME_SETTINGS_TAG, Context.MODE_PRIVATE);
        androidSettings.edit().putString(PS_IP_ADDRESS_TAG, hostname).commit();
        androidSettings.edit().putInt(PS_PORT_TAG, port).commit();
        androidSettings.edit().putBoolean(PS_USE_HTTPS_TAG, useHTTPS).commit();
        androidSettings.edit().putBoolean(PS_CRAWL_CONTEXT_TAG, crawlingContext).commit();
        androidSettings.edit().putBoolean(PS_OVERRIDE_DNS, overrideDNS).commit();
        androidSettings.edit().putString(PS_USER_NAME_TAG, username).commit();
        if(loginPrefRemembered) {
            androidSettings.edit().putString(PS_KEY_TAG, CryptHelper.getObscuredContent(username, password)).commit();
        } else {
            androidSettings.edit().putString(PS_KEY_TAG, CryptHelper.getObscuredContent(username, "")).commit();
        }
        androidSettings.edit().putBoolean(LOGIN_PREF_ACCEPTED_TAG, loginPrefAccepted).commit();
        androidSettings.edit().putBoolean(LOGIN_PREF_REMEMBER_TAG, loginPrefRemembered).commit();
        androidSettings.edit().putBoolean(SET_PREF_ACCEPTED_TAG, isSetPrefAccepted).commit();
    }
 
    /**
     * @param loginPrefAccepted the loginPrefAccepted to set
     */
    public synchronized void setLoginPrefAccepted(boolean loginPrefAccepted) {
        this.loginPrefAccepted = loginPrefAccepted;
        storeSettings();
    }

    /**
     * @param loginPrefRemembered the loginPrefRemembered to set
     */
    public synchronized void setLoginPrefRemembered(boolean loginPrefRemembered) {
        this.loginPrefRemembered = loginPrefRemembered;
        storeSettings();
    }

    public synchronized void setUserNamePassword(String username, String password, boolean loginPrefAccepted, boolean loginPrefRemembered) {
        this.username = username;
        this.password = password;
        this.loginPrefAccepted = loginPrefAccepted;
        this.loginPrefRemembered = loginPrefRemembered;
        storeSettings();
    }
    
    public void setOverrideDNS(boolean over){
    	this.overrideDNS = over;
        storeSettings();
    }
    
    public boolean getOverrideDNS(){
    	return overrideDNS;
    }

	public boolean isDialogInfoAreaDisplayed() {
		return isDialogInfoAreaDisplayed;
	}

	public void setDialogInfoAreaDisplayed(boolean isDialogInfoAreaDisplayed) {
		this.isDialogInfoAreaDisplayed = isDialogInfoAreaDisplayed;
		SharedPreferences androidSettings = appContext.getSharedPreferences(DIME_SETTINGS_TAG, Context.MODE_PRIVATE);
		androidSettings.edit().putBoolean(IS_DIALOG_INFO_AREA_DISPLAYED, isDialogInfoAreaDisplayed).commit();
	}

	public boolean isSetPrefAccepted() {
		return isSetPrefAccepted;
	}

	public void setSetPrefAccepted(boolean isSetPrefAccepted) {
		this.isSetPrefAccepted = isSetPrefAccepted;
		SharedPreferences androidSettings = appContext.getSharedPreferences(DIME_SETTINGS_TAG, Context.MODE_PRIVATE);
		androidSettings.edit().putBoolean(SET_PREF_ACCEPTED_TAG, isSetPrefAccepted).commit();
	}

    public synchronized ModelConfiguration getModelConfiguration() {
        return new ModelConfiguration(hostname, port, useHTTPS, username,username, password, false, USE_REST_API, RETRIEVE_NOTIFICATIONS);
    }
    
    public synchronized RestApiConfiguration getRestApiConfiguration() {
        return new RestApiConfiguration(hostname, port, useHTTPS, getAuthToken());
    }
    
    /**
     * @return the hostname
     */
    public synchronized String getHostname() {
        return hostname;
    }

    /**
     * @return the port
     */
    public synchronized int getPort() {
        return port;
    }

    /**
     * @return the useHTTPS
     */
    public synchronized boolean isUseHTTPS() {
        return useHTTPS;
    }

    /**
     * @return the crawlingContext
     */
    public synchronized boolean isCrawlingContext() {
        return crawlingContext;
    }

    /**
     * @return the appContext
     */
    public synchronized Context getAppContext() {
        return appContext;
    }

    /**
     * @return the mainSAID
     */
    public synchronized String getMainSAID() {
        return username;
    }

    /**
     * @return the password
     */
    public synchronized String getPassword() {
        return password;
    }

    /**
     * @return the receiveNotifications
     */
    public synchronized boolean isReceiveNotifications() {
        return receiveNotifications;
    }

    /**
     * @return the username
     */
    public synchronized String getUsername() {
        return username;
    }
    
   /**
     * @return the loginPrefAccepted
     */
    public synchronized boolean isLoginPrefAccepted() {
        return loginPrefAccepted;
    }

    /**
     * @return the loginPrefRemembered
     */
    public synchronized boolean isLoginPrefRemembered() {
        return loginPrefRemembered;
    }

    /**
     * @param hostname the hostname to set
     */
    public synchronized void setHostname(String hostname) {
        this.hostname = hostname;
        storeSettings();
    }

    /**
     * @param username the username to set
     */
    public synchronized void setUsername(String username) {
        this.username = username;
        storeSettings();
    }

    /**
     * @param password the password to set
     */
    public synchronized void setPassword(String password) {
        this.password = password;
        storeSettings();
    }

    /**
     * @param port the port to set
     */
    public synchronized void setPort(int port) {
        this.port = port;
        storeSettings();
    }

    /**
     * @param useHTTPS the useHTTPS to set
     */
    public synchronized void setUseHTTPS(boolean useHTTPS) {
        this.useHTTPS = useHTTPS;
        storeSettings();
    }

    /**
     * @param receiveNotifications the receiveNotifications to set
     */
    public synchronized void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
        storeSettings();
    }
    
    public String getAuthToken() {
    	return HttpHelper.getBase64UserNamePwdToken(username, password);
    }

    /**
     * @param crawlingContext the crawlingContext to set
     */
    public synchronized void setCrawlingContext(boolean crawlingContext) {
        this.crawlingContext = crawlingContext;
        storeSettings();
    }
	
	@Override
    public synchronized String toString() {
        return  "hostname: " + hostname
                + "\nmainSAID: " + username
                + "\nusername: " + username
                + "\npassword: " + password
                + "\nport: " + port
                + "\nuseHTTPS: " + useHTTPS
                + "\noverrideDNS: " + overrideDNS
                + "\nreceiveNotifications: " + receiveNotifications
                + "\ncrawlingContext: " + crawlingContext
                + "\nloginPrefAccepted: " + loginPrefAccepted
                + "\nloginPrefRemembered: " + loginPrefRemembered;
    }
    
}
