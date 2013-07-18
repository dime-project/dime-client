package eu.dime.mobile.view.abstr;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import eu.dime.control.LoadingViewHandler;
import eu.dime.control.NotificationListener;
import eu.dime.control.NotificationManager;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.model.GenItem;
import eu.dime.model.LoadingAbortedRuntimeException;
import eu.dime.model.ModelRequestContext;
import java.util.ArrayList;
import java.util.List;

public abstract class ListActivityDime<ITEM_TYPE extends GenItem> extends ListActivity implements OnItemClickListener, OnItemLongClickListener, NotificationListener {

	protected static String TAG = "not set";
    protected DimeIntentObject dio = null;
    protected List<ITEM_TYPE> listItems = new ArrayList<ITEM_TYPE>();
    protected ModelRequestContext mrContext = null;
    protected ListView listView;
	protected BaseAdapterDime<ITEM_TYPE> baseAdapter = null;
	protected LoadingViewHandler lvHandler = null;

    /**
     * Called when the activity is first created.
     */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lvHandler = createLoadingViewHandler();
        listView = getListView();
        // click on overall something item in ListView
        listView.setOnItemClickListener(this);
        // longClick on overall something item in ListView
        listView.setOnItemLongClickListener(this);
        TextView emptyText = UIHelper.createEmptyListWidget(this);
        ((ViewGroup) listView.getParent()).addView(emptyText);
        listView.setEmptyView(emptyText);
        init(this.getIntent());
    }
	
	@Override
    protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent != null) {
			init(intent);
		}
    }
	
	private void init(Intent intent) {
		dio = DimeIntentObjectHelper.readIntent(intent);
		mrContext = DimeClient.getMRC(dio.getOwnerId(), lvHandler);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		NotificationManager.registerSecondLevel(this);
	}

    @Override
    protected void onResume() {
        super.onResume();
        reloadList();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	NotificationManager.unregisterSecondLevel(this);
    }

    public void reloadList() {

        (new AsyncTask<Void, Void, List<ITEM_TYPE>>() {

            /**
             * The system calls this to perform work in a worker thread and
             * delivers it the parameters given to AsyncTask.execute()
             */
        	@Override
            protected List<ITEM_TYPE> doInBackground(Void... params) {
            	boolean isError = false;
            	List<ITEM_TYPE> items = new ArrayList<ITEM_TYPE>();
            	try {
    				items = loadListData();
				} catch (LoadingAbortedRuntimeException e){
					isError = true;
				} catch (RuntimeException e) {
					isError = true;
				}
            	if(isError) items = null;
            	return items;
            }

            /**
             * The system calls this to perform work in the UI thread and
             * delivers the result from doInBackground()
             */
            @Override
            protected void onPostExecute(List<ITEM_TYPE> result) {
            	if(!ListActivityDime.this.isFinishing()) {
	                if (result == null) {
						Toast.makeText(ListActivityDime.this, "You are working in offline mode now!", Toast.LENGTH_LONG).show();
	                } else {
	                	initializeHeader();
		                if(getListAdapter() == null) {
		                	baseAdapter.init(ListActivityDime.this, mrContext, result);
		                	setListAdapter(baseAdapter);
		                } else {
		                	baseAdapter.reinit(ListActivityDime.this, result);
		                }
	                }
            	}
            }
        }).execute();
    }

    protected void initializeHeader() {	}
    
    protected abstract List<ITEM_TYPE> loadListData();

    public void setBaseAdapter(BaseAdapterDime<ITEM_TYPE> baseAdapter) {
        this.baseAdapter = baseAdapter;
    }

    public List<String> getSelectionGUIDS() {
        return baseAdapter.getSelection();
    }
    
    public List<ITEM_TYPE> getSelectedListItems() {
    	List<ITEM_TYPE> items = new ArrayList<ITEM_TYPE>();
    	if (baseAdapter != null) {
    		items = baseAdapter.getSelectionItems();
    	}
    	return items;
    }

    public List<ITEM_TYPE> getListItems() {
        if (baseAdapter != null) {
            listItems = baseAdapter.getListItems();
        }
        return listItems;
    }
    
    @Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    	ITEM_TYPE item = getListItems().get(position);
    	UIHelper.showItemActionDialog(this, item);
		return false;
    }
    
    protected abstract LoadingViewHandler createLoadingViewHandler();
    
}