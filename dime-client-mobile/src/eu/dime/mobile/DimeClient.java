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
 *  Description of DimeClient
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 01.06.2012
 */
package eu.dime.mobile;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import eu.dime.control.LoadingViewHandler;
import eu.dime.control.NotificationListener;
import eu.dime.control.NotificationManager;
import eu.dime.mobile.crawler.Factory;
import eu.dime.mobile.crawler.IContextCrawler;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.objects.NotificationProperties;
import eu.dime.mobile.view.Activity_Main;
import eu.dime.model.Model;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.TYPES;
import eu.dime.model.context.constants.Scopes;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;
import eu.dime.restapi.RestApiAccess;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DimeClient
 *
 */
public class DimeClient extends Application implements NotificationListener {

	public static final String TAG = "DimeClient";
    private static Context appContext = null;
    public final static int NOTIFICATION_GENERAL_ID = 1;
    private static Settings settings;
    private android.app.NotificationManager androidNotificationManager = null;
    public static IContextCrawler contextCrawler = null;
    private static List<String> viewStack = new Vector<String>();

    @Override
    public void onCreate() {
    	super.onCreate();
		Logger.getLogger(DimeClient.class.getName()).log(Level.INFO, "create application");
		triggerAndroidNetWorkarrounds();
		appContext = getApplicationContext();
		settings = new Settings(appContext);
		Logger.getLogger(DimeClient.class.getName()).log(Level.INFO, "settings loaded");
		androidNotificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        eu.dime.control.NotificationManager.registerSecondLevel(this);
        Logger.getLogger(DimeClient.class.getName()).log(Level.INFO, "notification manager registered");
        initContextCrawler();
        Logger.getLogger(DimeClient.class.getName()).log(Level.INFO, "context crawler initialized");
    }

    private void triggerAndroidNetWorkarrounds() {
        // to allow for establishing many http calls
        System.setProperty("http.keepAlive", "false");
        // because of http://code.google.com/p/android/issues/detail?id=9431
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.net.preferIPv6Addresses", "false");
     // Trick to enable bluetooth management from a worker thread 
        BluetoothAdapter.getDefaultAdapter();
    }

    private void initContextCrawler() {
        contextCrawler = Factory.getCrawlerInstance();
        contextCrawler.setApplicationContext(getApplicationContext());
        contextCrawler.enableSensor(Scopes.SCOPE_BT, true);
        contextCrawler.enableSensor(Scopes.SCOPE_WF, true);
        contextCrawler.enableSensor(Scopes.SCOPE_POSITION, true);
        contextCrawler.enableSensor(Scopes.SCOPE_STATUS, true);
    }
    
    public static void toggleContextCrawler() {
        if (settings.isCrawlingContext()) {
            Logger.getLogger(DimeClient.class.getName()).log(Level.INFO, "updateClientConfiguration - starting context crawler");
            contextCrawler.start();
        } else {
        	Logger.getLogger(DimeClient.class.getName()).log(Level.INFO, "updateClientConfiguration - stopping context crawler");
            contextCrawler.stop();
        }
    }
    
    public static void shutdown() {
    	contextCrawler.stop();
    	NotificationManager.stop();
    	settings = null;
    	viewStack = null;
    	appContext = null;
    }

    @SuppressWarnings("deprecation")
    private void showNotification(Context context, String notificationText, PendingIntent intent) {
        if (intent == null) {
            intent = PendingIntent.getActivity(context, 0, new Intent(context, Activity_Main.class), 0);
        }
        if (notificationText == null || notificationText.length() == 0) {
            notificationText = "open in #di.me";
        }
        Notification n = new Notification(R.drawable.icon_android_notification, notificationText, System.currentTimeMillis());
        n.vibrate = new long[]{100, 200, 100, 500};
        n.setLatestEventInfo(context, notificationText, "open in #di.me", intent);
        n.flags|= Notification.FLAG_AUTO_CANCEL;
        androidNotificationManager.notify(NOTIFICATION_GENERAL_ID, n);
    }

    public void notificationReceived(final String fromHoster, final NotificationItem item) {
        Logger.getLogger(DimeClient.class.getName()).log(Level.INFO, "received notification: " + item.getOperation() + " " + item.getType() + "!");
        if (item.getOperation().equals(NotificationItem.OPERATION_CREATE) && item.getElement().getType().equals("usernotification")) {
            (new AsyncTask<Void, Void, NotificationProperties>() {

                @Override
                protected NotificationProperties doInBackground(Void... params) {
                    NotificationProperties np = null;
                    try {
                        UserNotificationItem notification = (UserNotificationItem) AndroidModelHelper.getGenItemSynchronously(getAppContext(), new DimeIntentObject(item.getElement().getGuid(), TYPES.USERNOTIFICATION));
                        np = UIHelper.getNotificationProperties(getAppContext(), notification);
                    } catch (Exception e) {
                        Log.d(DimeClient.appContext.getClass().getName(), e.toString());
                    }
                    return np;
                }

                @Override
                protected void onPostExecute(NotificationProperties np) {
                    if (np != null) {
                        showNotification(appContext, np.getNotificationText(), PendingIntent.getActivity(getAppContext(), 0, np.getIntent(), 0));
                    }
                }
            }).execute();

        } else if (item.getOperation().equals(NotificationItem.OPERATION_CREATE) && item.getElement().getType().equals("auth")) {
        	settings.setAuthItem(RestApiAccess.getAuthItem(getUserMainSaid(), settings.getRestApiConfiguration()));
        }
    }
    
    public static ModelRequestContext getMRC(LoadingViewHandler lvh) {
        return getMRC(Model.ME_OWNER, lvh);
    }

    public static ModelRequestContext getMRC(String owner, LoadingViewHandler lvh) {
        return new ModelRequestContext(getUserMainSaid(), owner, lvh);
    }

    public static Context getAppContext() {
        return DimeClient.appContext;
    }

    /**
     * be careful this function returns the actual settings - all changes will
     * be stored in the android configuration automatically
     *
     * @return
     */
    public static Settings getSettings() {
        return settings;
    }
    
    public static String getUserMainSaid() {
        return settings.getMainSAID();
    }
	
	public static List<String> getViewStack() {
		List<String> tmp = new Vector<String>(viewStack);
		viewStack.clear();
		return tmp;
	}

	public static void addStringToViewStack(String view) {
		if(settings.isSetPrefAccepted()) {
			viewStack.add(view);
			Log.i(TAG, viewStack.toString());
		}
	}

	public static String getClientVersion() {
		String clientVersion = "unknown";
		try {
			PackageInfo info = appContext.getPackageManager().getPackageInfo(appContext.getPackageName(), 0);
			clientVersion = info.versionName;
		} catch (NameNotFoundException e) {	
			//silently catch exception
		}
		return clientVersion;
	}
	
}
