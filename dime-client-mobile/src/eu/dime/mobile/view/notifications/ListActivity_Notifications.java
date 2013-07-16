package eu.dime.mobile.view.notifications;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ListActivityDime;
import eu.dime.mobile.view.adapter.BaseAdapter_Notification;
import eu.dime.model.GenItem;
import eu.dime.model.Model;
import eu.dime.model.TYPES;
import eu.dime.model.specialitem.NotificationItem;

import java.util.List;

public class ListActivity_Notifications extends ListActivityDime<GenItem> {

	/**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = ListActivity_Notifications.class.getSimpleName();
        setBaseAdapter(new BaseAdapter_Notification());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<GenItem> loadListData() {
        return (List<GenItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.USERNOTIFICATION);
    }

	@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //implemented in BaseAdapter_Notification because notificationProperties are loaded there to display the message correctly
    }

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(item.getElement().getType().equalsIgnoreCase(TYPES.USERNOTIFICATION.toString())){
    		reloadList();
    	}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<ListActivity_Notifications>createLVH(ListActivity_Notifications.this);
	}
	
}