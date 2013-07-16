package eu.dime.mobile.view.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ListActivityDisplayableItem;
import eu.dime.mobile.view.adapter.BaseAdapter_SearchResult;
import eu.dime.model.GenItem;
import eu.dime.model.ModelHelper;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.SearchResultItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.PersonItem;
import java.util.ArrayList;
import java.util.List;
import android.os.AsyncTask;

public class ListActivity_Public_Search_Dialog extends ListActivityDisplayableItem implements OnClickListener {
	
	private List<SearchResultItem> sri = new ArrayList<SearchResultItem>();

	/**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	TAG = ListActivity_Public_Search_Dialog.class.getSimpleName();
    	setContentView(R.layout.dialog_user_directory);
    	getListView().setOnItemClickListener(this);
        setBaseAdapter(new BaseAdapter_SearchResult());
    }

    @Override
    protected List<DisplayableItem> loadListData() {
    	EditText nameView = (EditText) findViewById(R.publicresolver.searchfield);
    	List<DisplayableItem> ldi = new ArrayList<DisplayableItem>();
    	if(nameView.getText().toString().length() > 0){
    		sri = ModelHelper.searchGlobaly(mrContext, nameView.getText().toString());
        	for (SearchResultItem searchResultItem : sri) {
    			DisplayableItem di = new PersonItem();
        		di.setName(searchResultItem.getName() + " " + searchResultItem.getSurname());
        		di.setUserId(searchResultItem.getNickname());
        		ldi.add(di);
    		}
    	}
        return ldi;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        case R.publicresolver.search_button:
        	reloadList();
        	break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final SearchResultItem searchResultItem = sri.get(position);
		AlertDialog.Builder builder = UIHelper.createAlertDialogBuilder(this, "Add " + searchResultItem.getNickname() + " to contact list?", true);
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				(new AsyncTask<Void, Void, Boolean>() {
		    		
					@Override
		            protected Boolean doInBackground(Void... params) {
						boolean result;
						try {
							result = ModelHelper.addPublicContact(mrContext, searchResultItem);
						} catch (Exception e) {
							Log.e(TAG, "Merge Error: " + e.toString());
							result = false;
						}
		                return result;
		            }

		            @Override
		            protected void onPostExecute(Boolean result) {
		            	if(result){
		            		AndroidModelHelper.sendEvaluationDataAsynchronously(new ArrayList<GenItem>(), mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_public_search_save));
		            		ListActivity_Public_Search_Dialog.this.finish();
		            	} else {
		            		Toast.makeText(getBaseContext(), "Error: Person could not be added!", Toast.LENGTH_LONG).show();
		            	}
		            }
		            
		        }).execute();
			}
		});
		UIHelper.displayAlertDialog(builder, false);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		return false;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		AndroidModelHelper.sendEvaluationDataAsynchronously(new ArrayList<GenItem>(), mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_canceled));
	}

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) { }

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<ListActivity_Public_Search_Dialog>createLVH(ListActivity_Public_Search_Dialog.this);
	}
	
}
