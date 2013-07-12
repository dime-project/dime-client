package eu.dime.mobile.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import eu.dime.mobile.DimeClient;
import eu.dime.restapi.MultiPartPostClient;
import java.io.File;
import java.io.IOException;
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

        // if this is from the share menu
        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_STREAM)) {

                // Get resource path from intent callee
                Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);

                // Obtain actual filename of shared picture
                String filename = FileHelper.parseUriToFilename(uri, this);

                if (filename != null) {
                    File file = new File(filename);
                    Toast.makeText(this, "Uploading picture to Personal Server", Toast.LENGTH_LONG).show();
                    
                    MultiPartPostClient myClient = new MultiPartPostClient(DimeClient.getSettings().getModelConfiguration());
                    try {
                        myClient.uploadFile(file);
                    } catch (IOException ex) {
                        Logger.getLogger(UploadPictureToPS.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            } else if (extras.containsKey(Intent.EXTRA_TEXT)) {
                Logger.getLogger(UploadPictureToPS.class.getName()).log(Level.INFO, "received Intent.EXTRA_TEXT");
            }
        }
        /*
         * Finish activity and go back to the picture
         */
        finish();
    }
}