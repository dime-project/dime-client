package eu.dime.mobile.view.abstr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import eu.dime.control.NotificationListener;
import eu.dime.control.NotificationManager;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.view.dialog.Activity_Edit_Item_Dialog;
import eu.dime.mobile.view.dialog.Activity_Unshare_Dialog;
import eu.dime.model.Model;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.specialitem.NotificationItem;

public abstract class TabActivityDisplayableItemDetail extends TabActivityDime implements OnClickListener, NotificationListener {

	protected DisplayableItem di;
	private boolean isLoading = false;
	
	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isLoading = true;
		(new AsyncTask<Void, Void, DisplayableItem>() {
			
			@Override
			protected DisplayableItem doInBackground(Void... params) {
				return (DisplayableItem) AndroidModelHelper.getGenItemSynchronously(TabActivityDisplayableItemDetail.this, dio);
			}
			
			@Override
			protected void onPostExecute(DisplayableItem result) {
				if(result != null) {
					di = result;
					initializeTabs();
					isLoading = false;
				} else {
					Toast.makeText(getApplicationContext(), "Couldn´t load detail view of item!", Toast.LENGTH_LONG).show();
					TabActivityDisplayableItemDetail.this.finish();
				}
			}
		}).execute();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		NotificationManager.registerSecondLevel(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refreshView();
	}
    
    public void refreshView() {
    	if(!isLoading) {
    		isLoading = true;
	    	(new AsyncTask<Void, Void, DisplayableItem>() {
				@Override
				protected DisplayableItem doInBackground(Void... params) {
					return (DisplayableItem) AndroidModelHelper.getGenItemSynchronously(TabActivityDisplayableItemDetail.this, dio);
				}
				
				@Override
				protected void onPostExecute(DisplayableItem result) {
					if(result != null) {
						di = result;
						initializeHeader();
					}
					isLoading = false;
				}
			}).execute();
    	}
	}

	@SuppressWarnings("deprecation")
	@Override
    protected void onStop() {
    	super.onStop();
    	NotificationManager.unregisterSecondLevel(this);
    }

	@Override
	protected void init(boolean showHomeButton, boolean showSearchButton, boolean showShareButton, boolean showActionButton) {
		initializeHeader();
		super.init(showHomeButton, showSearchButton, showShareButton, showActionButton);
	}
	
	protected abstract void initializeTabs();
    protected abstract void initializeHeader();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void onClickActionButton() {
		List<String> actions = new ArrayList<String>();
		actions.addAll(getActionsForDetailView());
		if(currentActivity instanceof ListActivityDime) {
			selectedGUIDs = ((ListActivityDime) currentActivity).getSelectionGUIDS();
		} else if(currentActivity instanceof ActivityDime) {
			selectedGUIDs = Arrays.asList(di.getGuid());
		}
		actions.add(getString(R.string.action_editItem));
		actions.add(getString(R.string.action_unshare));
		actionDialog = UIHelper.createActionDialog(this, actions, this, selectedGUIDs);
		actionDialog.show();
	}
	
	protected abstract List<String> getActionsForDetailView();
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v instanceof Button) {
			Button button = (Button) v;
			Resources res = getResources();
			//edit item
			if (button.getText().equals(res.getString(R.string.action_editItem))) {
				actionDialog.dismiss();
				Intent intent = new Intent(this, Activity_Edit_Item_Dialog.class);
				startActivity(DimeIntentObjectHelper.populateIntent(intent, dio));
			}
			//share
			if (button.getText().equals(res.getString(R.string.action_share))) {
				actionDialog.dismiss();
				ArrayList<String> resource = new ArrayList<String>();
				resource.add(di.getGuid());
				AndroidModelHelper.shareResources(this, resource, dio.getItemType());
			}
			//unshare
			if (button.getText().equals(res.getString(R.string.action_unshare))) {
				actionDialog.dismiss();
				Intent intent = new Intent(this, Activity_Unshare_Dialog.class);
				startActivity(DimeIntentObjectHelper.populateIntent(intent, dio));
			}
		}
	}
	
	@Override
	protected void onClickShareButton() {
    	AndroidModelHelper.shareResource(this, di);
	}
	
	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(di.getType().equals(item.getElement().getType())) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					refreshView();
				}
			});
		}
	}
	
	protected boolean isOwnItem() {
		return di.getUserId().equals(Model.ME_OWNER);
	}

}
