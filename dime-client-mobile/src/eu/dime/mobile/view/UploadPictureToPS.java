package eu.dime.mobile.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.helper.FileHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.model.specialitem.AuthItem;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadPictureToPS extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();
        AuthItem auth = DimeClient.getSettings().getAuthItem();
        if(auth != null || DimeClient.getSettings().isLoginPrefRemembered()) {
	        // if this is from the share menu
	        if (Intent.ACTION_SEND.equals(action)) {
	            if (extras.containsKey(Intent.EXTRA_STREAM)) {
	                // Get resource path from intent callee
	                Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
	                ProgressDialog dialog = UIHelper.createCustonProgressDialog(this, "Uploading file to personal server...");
	                FileHelper.uploadFile(this, dialog, uri);
	            } else if (extras.containsKey(Intent.EXTRA_TEXT)) {
	                Logger.getLogger(UploadPictureToPS.class.getName()).log(Level.INFO, "received Intent.EXTRA_TEXT");
	            }
	        }
        } else {
        	Toast.makeText(this, "Cannot upload file to your personal server. Please login first!", Toast.LENGTH_LONG).show();
        	finish();
        }

    }
}