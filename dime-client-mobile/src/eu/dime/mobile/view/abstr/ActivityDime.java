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

package eu.dime.mobile.view.abstr;

import eu.dime.control.LoadingViewHandler;
import eu.dime.control.NotificationListener;
import eu.dime.control.NotificationManager;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.model.ModelRequestContext;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public abstract class ActivityDime extends Activity implements NotificationListener {

    protected String TAG = "not set";
    protected DimeIntentObject dio = null;
    protected ProgressDialog dialog;
    protected LoadingViewHandler lvHandler = null;
    protected ModelRequestContext mrContext = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	lvHandler = createLoadingViewHandler();
    	init(this.getIntent());
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	if(intent != null) {
    		init(intent);
    	}
    }
    
    private void init(Intent intent) {
    	if(DimeClient.getAppContext() != null && DimeClient.getSettings() != null) {
	    	dio = DimeIntentObjectHelper.readIntent(intent);
	    	mrContext = DimeClient.getMRC(dio.getOwnerId(), lvHandler);
    	} else {               	
        	finish();
    	}
    }
    
    @Override
	protected void onStart() {
    	super.onStart();
    	NotificationManager.registerSecondLevel(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(DimeClient.getSettings() == null || DimeClient.getSettings().getAuthItem() == null) {
        	Toast.makeText(getApplicationContext(), "Error occurred! Please login again...", Toast.LENGTH_SHORT).show();           	
        	finish();
        }
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	      if(dialog != null) {
	          dialog.dismiss();
	      }
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		NotificationManager.unregisterSecondLevel(this);
	}

	public void startTask(String dialogText) {
    	if(dialogText.length() > 0){
	    	dialog = UIHelper.createCustonProgressDialog(ActivityDime.this, dialogText);
    	}
    	(new AsyncTask<Void, Void, Boolean>() {
    		
    		 /**
             * The system calls this to perform work in a worker thread and
             * delivers it the parameters given to AsyncTask.execute()
             */
    		@Override
            protected Boolean doInBackground(Void... params) {
    			boolean isError = false;
    			try {
    				loadData();
				} catch (RuntimeException e) {
					Log.d(TAG, "Error: " + e.getMessage());
					isError = true;
				}
                return isError;
            }
    		
    		/**
             * The system calls this to perform work in the UI thread and
             * delivers the result from doInBackground()
             */
            @Override
            protected void onPostExecute(Boolean result) {
            	if(!ActivityDime.this.isFinishing()) {
	            	if(!result) {
	            		try {
	            			initializeData();
						} catch (Exception e) {
							Toast.makeText(ActivityDime.this, "Couldn´t load activity!", Toast.LENGTH_LONG).show();
							ActivityDime.this.finish();
						}
	            	} else {
						if(ActivityDime.this.isTaskRoot()) Toast.makeText(ActivityDime.this, "You are working in offline mode now!", Toast.LENGTH_LONG).show();
	            	}
	            	if(dialog != null && dialog.isShowing()) dialog.dismiss();
            	}
            }
        }).execute();
    }

    protected abstract void loadData();

    protected abstract void initializeData();
    
    protected abstract LoadingViewHandler createLoadingViewHandler();
    
}
