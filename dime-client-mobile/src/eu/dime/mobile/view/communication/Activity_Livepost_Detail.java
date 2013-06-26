package eu.dime.mobile.view.communication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.mobile.view.dialog.Activity_Edit_Item_Dialog;
import eu.dime.model.Model;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.specialitem.NotificationItem;

public class Activity_Livepost_Detail extends ActivityDime implements OnClickListener {

    private LivePostItem livepost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = Activity_Livepost_Detail.class.getSimpleName();
        setContentView(R.layout.livepost_detail);
        ((LinearLayout)findViewById(R.id.header)).setOnClickListener(this);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	startTask("");
    }

	@Override
	protected void loadData() {	
		livepost = (LivePostItem) Model.getInstance().getItem(mrContext, dio.getItemType(), dio.getItemId());
	}

	@Override
	protected void initializeData() { 
		UIHelper.inflateStandardHeader(this, livepost, mrContext);
		TextView subject = (TextView) findViewById(R.livepost.subject);
		TextView message = (TextView) findViewById(R.livepost.message);
        subject.setText(livepost.getName());
        message.setText(livepost.getText());
	}
	
	@Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.header:
        	Intent intent = new Intent(this, Activity_Edit_Item_Dialog.class);
			startActivity(DimeIntentObjectHelper.populateIntent(intent, dio));
			break;
        }
	}

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		startTask("");
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Livepost_Detail>createLVH(Activity_Livepost_Detail.this);
	}
}
