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

package eu.dime.mobile.view;

import eu.dime.control.SilentLoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class Activity_Shutdown extends Activity {
	
	protected ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shutdown);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dialog = ProgressDialog.show(this, null, "Shutting down di.me...", true, false, null);
    	((TextView) dialog.findViewById(android.R.id.message)).setTextColor(Color.WHITE);
    	try {
    		AndroidModelHelper.sendEvaluationDataAsynchronously(null, DimeClient.getMRC(new SilentLoadingViewHandler()), "action_logout");
		} catch (Exception e) {	}
    	DimeClient.shutdown();
    	Handler handler = new Handler();
    	handler.postDelayed(new Runnable() {
    	    public void run() {
    	    	if(dialog != null && dialog.isShowing()) dialog.dismiss();
    	        finish();
    	    }}, 2000); 
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    System.runFinalizersOnExit(true);
	    System.exit(0);
	}

}
