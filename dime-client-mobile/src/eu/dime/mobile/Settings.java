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

/*
 *  Description of Settings
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 02.07.2012
 */
package eu.dime.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import eu.dime.mobile.helper.objects.OwnServers;
import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.ModelConfiguration;
import eu.dime.model.specialitem.AuthItem;
import eu.dime.restapi.DimeHelper;
import eu.dime.restapi.RestApiConfiguration;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONParseException;
import sit.json.JSONParser;
import sit.tools.CryptHelper;
import sit.web.client.HttpHelper;

/**
 * Settings
 * 
 */
public class Settings {
	
    private static final String LOGIN_PREF_REMEMBER_TAG = "LOGIN_PREF_REMEMBER";
    private static final String DIME_SETTINGS_TAG = "DIME_SETTINGS";
    private static final String PS_CRAWL_CONTEXT_TAG = "PS_CRAWL_CONTEXT";
    private static final String PS_IP_ADDRESS_TAG = "PS_IP_ADDRESS";
    private static final String PS_KEY_TAG = "PS_KEY";
    private static final String PS_PORT_TAG = "PS_PORT";
    private static final String PS_USER_NAME_TAG = "PS_USER_NAME";
    private static final String PS_USE_HTTPS_TAG = "PS_USE_HTTPS";
    private static final String IS_DIALOG_INFO_AREA_DISPLAYED = "IS_DIALOG_INFO_AREA_DISPLAYED";
    private static final String OWN_SERVERS = "OWN_SERVERS";
    
    private final static boolean USE_REST_API = true;
    private final static boolean RETRIEVE_NOTIFICATIONS = true;
    
    private final Context appContext;
    private boolean receiveNotifications = RETRIEVE_NOTIFICATIONS;
    
    //Settings coming from the personal server 
    private AuthItem authItem;
    private String clientVersion;
    private String serverVersion;
    
    //Settings stored in the android data storage
    private String hostname;
    private String username; //== mainSAID
    private String password="";
    private int port;
    private boolean useHTTPS;
    private boolean loginPrefRemembered;
    private boolean crawlingContext;
    private boolean isDialogInfoAreaDisplayed;
    private OwnServers ownServers;

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
        username = androidSettings.getString(PS_USER_NAME_TAG, "");
        ownServers = new OwnServers();
        try {
			ownServers.readJSONObject(new JSONParser().parseJSON(androidSettings.getString(OWN_SERVERS, "")));
		} catch (InvalidJSONItemException e) { } catch (JSONParseException e) { }
        try {
        	password = CryptHelper.getUnObscuredContent(username, androidSettings.getString(PS_KEY_TAG, ""));
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        loginPrefRemembered = androidSettings.getBoolean(LOGIN_PREF_REMEMBER_TAG, false);
        isDialogInfoAreaDisplayed = androidSettings.getBoolean(IS_DIALOG_INFO_AREA_DISPLAYED, false);
    }

    private void storeSettings(){
        SharedPreferences androidSettings = appContext.getSharedPreferences(DIME_SETTINGS_TAG, Context.MODE_PRIVATE);
        androidSettings.edit().putString(PS_IP_ADDRESS_TAG, hostname).commit();
        androidSettings.edit().putInt(PS_PORT_TAG, port).commit();
        androidSettings.edit().putBoolean(PS_USE_HTTPS_TAG, useHTTPS).commit();
        androidSettings.edit().putBoolean(PS_CRAWL_CONTEXT_TAG, crawlingContext).commit();
        androidSettings.edit().putString(PS_USER_NAME_TAG, username).commit();
        androidSettings.edit().putString(OWN_SERVERS, ownServers.createJSONObject().toString()).commit();
        if(loginPrefRemembered) {
            androidSettings.edit().putString(PS_KEY_TAG, CryptHelper.getObscuredContent(username, password)).commit();
        } else {
            androidSettings.edit().putString(PS_KEY_TAG, CryptHelper.getObscuredContent(username, "")).commit();
        }
        androidSettings.edit().putBoolean(LOGIN_PREF_REMEMBER_TAG, loginPrefRemembered).commit();
    }
    
    public void updateSettingsBeforeLogin(String hostname, String username, String password, int port, boolean useHTTPS, boolean loginPrefRemembered) {
    	this.hostname = hostname;
    	this.username = username;
    	this.password = password;
    	this.port = port;
    	this.useHTTPS = useHTTPS;
    	this.loginPrefRemembered = loginPrefRemembered;
    	storeSettings();
    }
    
    public void updateSettingsAfterLogin(String clientVersion, String serverVersion, AuthItem authItem) {
    	this.clientVersion = clientVersion;
    	this.serverVersion = serverVersion;
    	this.authItem = authItem;
    }
    
    public synchronized ModelConfiguration getModelConfiguration() {
        return new ModelConfiguration(hostname, port, useHTTPS, username, username, password, false, USE_REST_API, RETRIEVE_NOTIFICATIONS);
    }
    
    public synchronized RestApiConfiguration getRestApiConfiguration() {
        return new RestApiConfiguration(hostname, port, useHTTPS, getAuthToken());
    }
    
    public void setDialogInfoAreaDisplayed(boolean isDialogInfoAreaDisplayed) {
		this.isDialogInfoAreaDisplayed = isDialogInfoAreaDisplayed;
		SharedPreferences androidSettings = appContext.getSharedPreferences(DIME_SETTINGS_TAG, Context.MODE_PRIVATE);
		androidSettings.edit().putBoolean(IS_DIALOG_INFO_AREA_DISPLAYED, isDialogInfoAreaDisplayed).commit();
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
     * @param receiveNotifications the receiveNotifications to set
     */
    public synchronized void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
        storeSettings();
    }

    /**
     * @param crawlingContext the crawlingContext to set
     */
    public synchronized void setCrawlingContext(boolean crawlingContext) {
        this.crawlingContext = crawlingContext;
        storeSettings();
    }
    
    public String getAuthToken() {
    	return HttpHelper.getBase64UserNamePwdToken(username, password);
    }

	public boolean isDialogInfoAreaDisplayed() {
		return isDialogInfoAreaDisplayed;
	}

    public synchronized String getHostname() {
        return hostname;
    }
    
    public synchronized int getPort() {
        return port;
    }
    
    public synchronized boolean isUseHTTPS() {
        return useHTTPS;
    }
    
    public synchronized boolean isCrawlingContext() {
        return crawlingContext;
    }
    
    public synchronized Context getAppContext() {
        return appContext;
    }

    public synchronized String getMainSAID() {
        return username;
    }

    public synchronized String getPassword() {
        return password;
    }

    public synchronized boolean isReceiveNotifications() {
        return receiveNotifications;
    }

    public synchronized String getUsername() {
        return username;
    }

    public synchronized boolean isLoginPrefRemembered() {
        return loginPrefRemembered;
    }
	
	@Override
    public synchronized String toString() {
        return  "hostname: " + hostname
                + "\nmainSAID: " + username
                + "\nusername: " + username
                + "\npassword: " + password
                + "\nport: " + port
                + "\nuseHTTPS: " + useHTTPS
                + "\nreceiveNotifications: " + receiveNotifications
                + "\ncrawlingContext: " + crawlingContext
                + "\nloginPrefRemembered: " + loginPrefRemembered;
    }

	public AuthItem getAuthItem() {
		return authItem;
	}

	public void setAuthItem(AuthItem authItem) {
		this.authItem = authItem;
	}

	public boolean isSetPrefAccepted() {
		return (authItem != null) ? authItem.isEvaluationDataCapturingEnabled() : false;
	}

	public String getEvaluationId() {
		return (authItem != null) ? authItem.getEvaluationId() : "";
	}

	public void setLoginPrefRemembered(boolean loginPrefRemembered) {
		this.loginPrefRemembered = loginPrefRemembered;
		storeSettings();
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	public List<String> getOwnServers() {
		return ownServers.getOwnServers();
	}
	
	public void addOwnServer(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		ownServers.addOwnServer(new StringBuilder().append(hostname).append(":").append(port).toString());
		SharedPreferences androidSettings = appContext.getSharedPreferences(DIME_SETTINGS_TAG, Context.MODE_PRIVATE);
		androidSettings.edit().putString(OWN_SERVERS, ownServers.createJSONObject().toString()).commit();
	}

	public void removeOwnServer(String server) {
		ownServers.removeOwnServer(server);
		SharedPreferences androidSettings = appContext.getSharedPreferences(DIME_SETTINGS_TAG, Context.MODE_PRIVATE);
		androidSettings.edit().putString(OWN_SERVERS, ownServers.createJSONObject().toString()).commit();
	}
    
}
