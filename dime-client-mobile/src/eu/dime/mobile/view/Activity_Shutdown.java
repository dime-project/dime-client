package eu.dime.mobile.view;

import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
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
    	DimeClient.shutdown();
    	Handler handler = new Handler();
    	handler.postDelayed(new Runnable() {
    	    public void run() {
    	    	if(dialog != null && dialog.isShowing()) dialog.dismiss();
    	        finish();
    	    }}, 3000); 
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    System.runFinalizersOnExit(true);
	    System.exit(0);
	}

}
