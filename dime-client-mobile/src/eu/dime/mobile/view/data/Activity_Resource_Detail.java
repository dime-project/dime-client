package eu.dime.mobile.view.data;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.FileHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.mobile.view.dialog.Activity_Edit_Item_Dialog;
import eu.dime.model.Model;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.model.specialitem.NotificationItem;

public class Activity_Resource_Detail extends ActivityDime implements OnClickListener {

	private ResourceItem resource;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = Activity_Resource_Detail.class.getSimpleName();
		setContentView(R.layout.resource_detail);
		((LinearLayout) findViewById(R.id.header)).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		startTask("");
	}

	@Override
	protected void loadData() {
		resource = (ResourceItem) Model.getInstance().getItem(mrContext, dio.getItemType(), dio.getItemId());
	}

	@Override
	protected void initializeData() {
		UIHelper.inflateStandardHeader(this, resource, mrContext);
		TextView owner = (TextView) findViewById(R.resource.owner);
		TextView mimeType = (TextView) findViewById(R.resource.mime);
		TextView filesize = (TextView) findViewById(R.resource.filesize);
		TextView url = (TextView) findViewById(R.resource.url);
		mimeType.setText(resource.getMimeType());
		filesize.setText(resource.getFileSize() + " Bytes");
		url.setText(resource.getDownloadUrl());
		owner.setText(resource.getFileOwner());
		Button openURL = (Button) findViewById(R.resource.button_openfile);
		Button download = (Button) findViewById(R.resource.button_downloadfile);
		openURL.setOnClickListener(this);
		download.setOnClickListener(this);
	}

	@Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.header:
			Intent intent = new Intent(this, Activity_Edit_Item_Dialog.class);
			startActivity(DimeIntentObjectHelper.populateIntent(intent, dio));
			break;
			
        case R.resource.button_openfile:
        	FileHelper.saveFileAsynchronouslyOnSDAndOpen(this, resource, UIHelper.createCustonProgressDialog(this, "Trying to open the file!"));
        break;
        
        case R.resource.button_downloadfile:
        	FileHelper.saveFileAsynchronouslyOnSD(this, resource, UIHelper.createCustonProgressDialog(this, "Trying to download the file!"));
        break;
        }
    }

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if (item.getElement().getType().equalsIgnoreCase(TYPES.RESOURCE.toString())) {
			startTask("");
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Resource_Detail> createLVH(Activity_Resource_Detail.this);
	}

}
