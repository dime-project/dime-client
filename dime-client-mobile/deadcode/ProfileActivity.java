package eu.dime.mobile.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import eu.dime.mobile.R;
import eu.dime.mobile.view.adapter.ItemViewAdapter;
import eu.dime.mobile.view.helpers.DimeIntentObject;
import eu.dime.mobile.view.helpers.DimeIntentObjectHelper;

public class ProfileActivity extends Activity {

    private DimeIntentObject dio = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dio = new DimeIntentObjectHelper().readIntent(this.getIntent());
        
        
        getListView().setAdapter(new ItemViewAdapter(this, getListView(),
                dio.getGroupType(), dio.getMRC(this)));


    }

    private ListView getListView() {
        return ((ListView) this.findViewById(R.id.profile_view_list));
    }
}
