package eu.dime.mobile.view.settings;

import android.os.Bundle;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.model.specialitem.NotificationItem;

public class Activity_Settings_Rules extends ActivityDime {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = Activity_Settings_Rules.class.getSimpleName();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		startTask("");
	}

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void loadData() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void initializeData() {
		// TODO Auto-generated method stub
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Settings_Rules>createLVH(Activity_Settings_Rules.this);
	}

}