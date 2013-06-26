package eu.dime.mobile.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import eu.dime.mobile.R;
import eu.dime.mobile.control.AndroidLoadingViewHandler;
import eu.dime.mobile.view.adapter.ItemViewAdapter;
import eu.dime.mobile.view.helpers.DimeIntentObject;
import eu.dime.mobile.view.helpers.DimeIntentObjectHelper;
import eu.dime.model.ModelRequestContext;

public class CommunicationActivity extends Activity {

    private DimeIntentObject dio = null;
    private ModelRequestContext mrContext=null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.communication);
    }

    @Override
    protected void onResume() {
        super.onResume();

        dio = new DimeIntentObjectHelper().readIntent(this.getIntent());
        mrContext = new ModelRequestContext(dio.getHoster(), dio.getOwner(), new AndroidLoadingViewHandler(this));     
        
        
        getListView().setAdapter(new ItemViewAdapter(this, getListView(),
                dio.getItemType(), mrContext));
    }

    private ListView getListView() {
        return ((ListView) this.findViewById(R.id.communication_view_list));
    }
}
