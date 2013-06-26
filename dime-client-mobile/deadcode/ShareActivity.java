package eu.dime.mobile.view;

import android.app.Activity;
import android.os.Bundle;
import eu.dime.mobile.R;

public class ShareActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
