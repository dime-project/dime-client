package eu.dime.mobile.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.control.AndroidLoadingViewHandler;
import eu.dime.mobile.view.adapter.GroupViewAdapter;
import eu.dime.mobile.view.adapter.ItemViewAdapter;
import eu.dime.mobile.view.helpers.DimeIntentObject;
import eu.dime.mobile.view.helpers.DimeIntentObjectHelper;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;

public class GroupViewActivity extends Activity {

    private static final String TAG = "PeopleActivity";
    private DimeIntentObject dio = null;
    private ModelRequestContext mrContext=null;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_view);


    }



    @Override
    protected void onResume() {
        super.onResume();

        dio = new DimeIntentObjectHelper().readIntent(this.getIntent());
        mrContext = new ModelRequestContext(dio.getHoster(), dio.getOwner(), new AndroidLoadingViewHandler(this));  
        

        this.setTitle(ModelHelper.getNameOfType(dio.getGroupType()));
        getListView().setAdapter(new GroupViewAdapter(this, getListView(), dio.getGroupType(),mrContext));

        // Example to retrieve current user position
        /*ContextItem context = MainActivity.crawler.getCurrentContext(Scopes.SCOPE_POSITION);
        Position position = ContextHelper.getPosition(context);
        if (position != null) Log.d(TAG,"User is in lat:" + position.getLatitude() + ", lon:" + position.getLongitude());
        else Log.d(TAG,"User's position not available");*/
        
        // Example to post explicit user's login at an event (See ContextHelper JavaDocs)
        // If the event is associated to a known place the app should post also the current place (see lines below)
        /*ContextItem place = ContextHelper.createCurrentEventContextItem("388yDes2", "Modern History Class", new Integer(600));
        MainActivity.crawler.updateContext(Scopes.SCOPE_CURRENT_EVENT,place);*/
        
        // Example to post explicit user's login in a place (See ContextHelper JavaDocs)
        /*ContextItem event = ContextHelper.createCurrentPlaceContextItem("ametic:xyz276", "Room 236", new Integer(600));
        MainActivity.crawler.updateContext(Scopes.SCOPE_CURRENT_PLACE,event);*/
        
    }



    private ListView getListView() {
        return ((ListView) this.findViewById(R.id.group_view_list));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_list_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.group_view_group_button:
                getListView().setAdapter(new GroupViewAdapter(this, getListView(), dio.getGroupType(), mrContext));
                return true;
            case R.id.group_view_item_button:
                getListView().setAdapter(new ItemViewAdapter(this, getListView(), dio.getItemType(), mrContext));
                return true;
            case R.id.group_view_add_button:
                Intent myIntent = new Intent(GroupViewActivity.this, DetailViewActivity.class);
                DimeIntentObject dio_new = new DimeIntentObject(dio.getGroupType(), dio.getHoster(), dio.getOwner(), DimeIntentObject.ACTIONS.CREATE);
                this.startActivity(new DimeIntentObjectHelper().populateIntent(myIntent, dio_new));
                
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
