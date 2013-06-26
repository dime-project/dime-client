package eu.dime.mobile.view.datamining;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import eu.dime.mobile.R;
import eu.dime.mobile.service.DataMiningService;

public class DataMiningActivity extends Activity implements OnClickListener {

    private static final String TAG = "ServicesDemo";
    Button buttonStart, buttonStop, buttonReset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datamining_service);

        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonReset = (Button) findViewById(R.id.buttonReset);

        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonReset.setOnClickListener(this);
    }

    public void onClick(View src) {
        switch (src.getId()) {
            case R.id.buttonStart:
                Log.d(TAG, "onClick: starting srvice");
                startService(new Intent(this, DataMiningService.class));
                //Intent intent = new Intent("eu.dime.mobile.datamining.crawler.DataMiningService");
                //this.startService(intent);
                break;

            case R.id.buttonStop:
                Log.d(TAG, "onClick: stopping srvice");
                stopService(new Intent(this, DataMiningService.class));
                break;

            case R.id.buttonReset:
                SharedPreferences preferences = PreferenceManager.
                        getDefaultSharedPreferences(this);
                Editor preferencesEditor = preferences.edit();
                preferencesEditor.putLong(Constants.SETTINGS_LAST_CRAWLING_TIMESTAMP_KEY, 0L);
                preferencesEditor.putBoolean(Constants.SETTINGS_CRAWL_IMAGES, true);
                preferencesEditor.commit();
                Toast.makeText(this, "Preferences Reset Successfully", Toast.LENGTH_SHORT).
                        show();
                break;
        }
    }
}