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