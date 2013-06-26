package eu.dime.mobile.helper.objects;

import android.content.Intent;

public class NotificationProperties {
	
	private String notificationText;
	private Intent intent;
	private int drawableId;
	private String senderName;

	public NotificationProperties() {
	}

	public NotificationProperties(String notificationText, Intent intent, int drawableId, String senderName) {
		this.notificationText = notificationText;
		this.intent = intent;
		this.drawableId = drawableId;
		this.senderName = senderName;
	}

	public String getNotificationText() {
		return notificationText;
	}

	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public int getDrawableId() {
		return drawableId;
	}

	public void setDrawableId(int drawableId) {
		this.drawableId = drawableId;
	}

	public String getSenderName() {
		return senderName;
	}

}
