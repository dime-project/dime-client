package eu.dime.mobile.view.settings;

import java.util.List;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.view.abstr.ListActivityDisplayableItem;
import eu.dime.mobile.view.adapter.BaseAdapter_Accounts;
import eu.dime.mobile.view.dialog.Activity_Account_Configuration_Dialog;
import eu.dime.model.Model;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.AccountItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.ServiceAdapterItem;
import eu.dime.model.specialitem.NotificationItem;

public class ListActivity_Settings_Services extends ListActivityDisplayableItem {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = ListActivity_Settings_Services.class.getSimpleName();
		setBaseAdapter(new BaseAdapter_Accounts());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<DisplayableItem> loadListData() {
		return (List<DisplayableItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.ACCOUNT);
	}
	
	@Override
	protected void initializeHeader() {
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final AccountItem account = (AccountItem) getListItems().get(position);
		if(account.getSettings().size()>0) {
			(new AsyncTask<Void, Void, ServiceAdapterItem>() {	
				@Override
				protected ServiceAdapterItem doInBackground(Void... params) {
					ServiceAdapterItem sai;
					try {
						sai = (ServiceAdapterItem) Model.getInstance().getItem(mrContext, TYPES.SERVICEADAPTER, account.getServiceAdapterGUID());
					} catch (RuntimeException e) {
						sai = null;
					}
					return sai;
				}
				@Override
				protected void onPostExecute(ServiceAdapterItem result) {
					if(result != null) {
						Intent myIntent = new Intent(ListActivity_Settings_Services.this, Activity_Account_Configuration_Dialog.class);
						myIntent.putExtra(Activity_Account_Configuration_Dialog.ACCOUNT_GUID_TAG, account.getGuid());
				        startActivity(DimeIntentObjectHelper.populateIntent(myIntent, new DimeIntentObject(result)));
					}
				}
			}).execute();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		return false;
	}
	
	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(item.getElement().getType().equalsIgnoreCase(TYPES.ACCOUNT.toString())){
    		reloadList();
    	}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<ListActivity_Settings_Services>createLVH(ListActivity_Settings_Services.this);
	}

}
