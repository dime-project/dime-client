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
