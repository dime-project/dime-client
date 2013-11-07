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

package eu.dime.mobile.helper.listener;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.control.SilentLoadingViewHandler;
import eu.dime.mobile.helper.objects.NotificationProperties;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;

public class ClickListenerUserNotifications implements OnClickListener {
	
	private Context context;
	private UserNotificationItem uni;
	private NotificationProperties np;
	
	public ClickListenerUserNotifications(Context context, UserNotificationItem uni, NotificationProperties np) {
		this.context = context;
		this.uni = uni;
		this.np = np;
	}
	
	@Override
	public void onClick(View v) {
	    try {
	    	context.startActivity(np.getIntent());
	    	if(!uni.isRead()) {
	    		uni.setRead(true);
	    		AndroidModelHelper.updateGenItemAsynchronously(uni, null, (Activity) context, DimeClient.getMRC(new SilentLoadingViewHandler()), null);
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
