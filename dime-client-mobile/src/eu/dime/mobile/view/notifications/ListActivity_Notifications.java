/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

package eu.dime.mobile.view.notifications;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ListActivityDime;
import eu.dime.mobile.view.adapter.BaseAdapter_Notification;
import eu.dime.model.Model;
import eu.dime.model.TYPES;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;
import java.util.List;

public class ListActivity_Notifications extends ListActivityDime<UserNotificationItem> {

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
    protected List<UserNotificationItem> loadListData() {
        return (List<UserNotificationItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.USERNOTIFICATION);
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