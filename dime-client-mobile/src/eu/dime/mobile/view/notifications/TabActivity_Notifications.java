package eu.dime.mobile.view.notifications;

import java.util.Arrays;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.model.TYPES;

public class TabActivity_Notifications extends TabActivityDime {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Notifications.class.getSimpleName();
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_notifications), ListActivity_Notifications.class, new DimeIntentObject(TYPES.USERNOTIFICATION)));
		super.init(true, false, false); //FIXME action menu not displayed until it is supported by the private server model
	}

	@Override
	protected void onClickActionButton() {
		String[] actionsForNotifications = { getResources().getString(R.string.action_removeSelectedNotifications) };
		if (currentActivity instanceof ListActivity_Notifications) {
			selectedGUIDs = ((ListActivity_Notifications) currentActivity).getSelectionGUIDS();
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForNotifications), this, selectedGUIDs);
			actionDialog.show();
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
        if (view instanceof Button) {
            Button button = (Button) view;
            Resources res = getResources();
            if (currentActivity instanceof ListActivity_Notifications) {
            	//remove selected notifications
				if (button.getText().equals(res.getString(R.string.action_removeSelectedNotifications))) {
					actionDialog.dismiss();
					AndroidModelHelper.deleteGenItemsAsynchronously(currentActivity, TYPES.NOTIFICATION, mrContext, res.getResourceEntryName(R.string.action_removeSelectedNotifications)); 
				}
				//remove all notifications
				else if (button.getText().equals(res.getString(R.string.action_removeAllNotifications))) {
					actionDialog.dismiss();
//					((ListActivity_Notifications)currentActivity).getListItems();
//					AndroidModelHelper.deleteGenItemsAsynchronously(currentActivity, TYPES.NOTIFICATION, mrContext, null);
				}
            }
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Notifications>createLVH(TabActivity_Notifications.this);
	}

}
