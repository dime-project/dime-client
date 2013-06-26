package eu.dime.mobile.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;

public class SituationActivity extends Activity {

    private static final String TAG = "SituationActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.situations);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    

    private EditText getEditText() {
        return ((EditText) this.findViewById(R.id.situationTextField));
    }

    

    public void clickHandler(View v) {

        Log.d(TAG, "Button pressed: " + v.getId());


        switch (v.getId()) {
            case R.id.situation_view_start_test_button:
                if (getEditText() != null) {
                    getEditText().setText("test deactivated");
                }
                break;
            case R.id.situation_view_collect_REST_calls_button:
                Log.d(TAG, "situation_view_collect_REST_calls_button pressed!");
                 if (getEditText() != null) {
                    getEditText().setText("method deactivated");
                }

                break;
        }
    }
}
