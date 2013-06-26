package eu.dime.mobile.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.view.helpers.DimeIntentObject;
import eu.dime.mobile.view.helpers.DimeIntentObjectHelper;
import eu.dime.model.Model;
import eu.dime.model.TYPES;
import eu.dime.model.context.constants.Scopes;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private final String hoster = DimeClient.getUserMainSaid();
    private final String owner = Model.ME_OWNER;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        Log.d(TAG, "System online");

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void clickHandler(View v) {


        Log.d(TAG, "Button pressed: " + v.getId());
        Intent myIntent = null;

        switch (v.getId()) {
            case R.id.peopleBtn:
                Log.d(TAG, "peopleBtn pressed!");
                myIntent = new Intent(MainActivity.this, GroupViewActivity.class);
                this.startActivity(new DimeIntentObjectHelper().populateIntent(myIntent, new DimeIntentObject(TYPES.GROUP, hoster, owner)));

                break;
            case R.id.myProfileBtn:
                Log.d(TAG, "myProfileBtn pressed!");

                myIntent = new Intent(MainActivity.this, GroupViewActivity.class);
                this.startActivity(new DimeIntentObjectHelper().populateIntent(myIntent, new DimeIntentObject(TYPES.PROFILE, hoster, owner)));

                break;
            case R.id.communicateBtn:
                Log.d(TAG, "communicateBtn pressed!");
                myIntent = new Intent(MainActivity.this, CommunicationActivity.class);
                this.startActivity(new DimeIntentObjectHelper().populateIntent(myIntent, new DimeIntentObject(TYPES.LIVESTREAM, hoster, owner)));
                break;
            case R.id.dataBtn:
                Log.d(TAG, "peopleBtn pressed!");
                myIntent = new Intent(MainActivity.this, GroupViewActivity.class);
                this.startActivity(new DimeIntentObjectHelper().populateIntent(myIntent, new DimeIntentObject(TYPES.DATABOX, hoster, owner)));
                break;
            case R.id.shareBtn:
                Log.d(TAG, "shareBtn pressed!");
                myIntent = new Intent(MainActivity.this, ShareActivity.class);
                this.startActivity(myIntent);

                break;

            case R.id.situationsBtn:
                Log.d(TAG, "situationsBtn pressed!");
                myIntent = new Intent(MainActivity.this, SituationActivity.class);
                this.startActivity(myIntent);

                break;

            
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_view_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.main_view_option_menu_settings_button:
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                this.startActivity(myIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
