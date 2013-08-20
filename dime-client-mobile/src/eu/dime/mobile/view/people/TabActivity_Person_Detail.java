package eu.dime.mobile.view.people;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.TabActivityDisplayableItemDetail;
import eu.dime.model.displayable.PersonItem;

public class TabActivity_Person_Detail extends TabActivityDisplayableItemDetail {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Person_Detail.class.getSimpleName();
	}
	
	@Override
	protected void initializeTabs() {
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_personDetailProfile), ListActivity_Person_Profile.class, dio));
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_personDetailData), ListActivity_Person_Data.class, dio));
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_personDetailMessages), ListActivity_Person_Livepost.class, dio));
    	TabActivity_Person_Detail.this.init(true, true, true, true);
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Person_Detail>createLVH(TabActivity_Person_Detail.this);
	}

	@Override
	protected List<String> getActionsForDetailView() {
		ArrayList<String> actions = new ArrayList<String>();
		if (currentActivity instanceof ListActivity_Person_Profile) {
			//FIXME add standard profile for instance ListActivity_Person_Profile
		} else if (currentActivity instanceof ListActivity_Person_Livepost) {
			actions.add(getString(R.string.action_answerLivepost));
		}
		return actions;
	}

	@Override
	protected void initializeHeader() {
		ViewGroup headerContainer = (ViewGroup) findViewById(R.tabframe.header);
		LinearLayout header = null;
		if(headerContainer.getChildCount() > 0) {
			header = (LinearLayout) headerContainer.getChildAt(0);
		} else {
			header = (LinearLayout) getLayoutInflater().inflate(R.layout.header_standard, headerContainer);
		}
		UIHelper.inflateStandardHeader(this, di, mrContext, header);
	}
	
	@Override
	public void onClick(View view) {
		super.onClick(view);
		if (view instanceof Button) {
            Button button = (Button) view;
            Resources res = getResources();
            /**
             * ---------------------------------------------------------------------------------------------------------------------------
             * Actions for ListActivity_Person_Profile
             * ---------------------------------------------------------------------------------------------------------------------------
             */
			if (currentActivity instanceof ListActivity_Person_Profile) {
				
			}
			/**
             * ---------------------------------------------------------------------------------------------------------------------------
             * Actions for ListActivity_Person_Livepost
             * ---------------------------------------------------------------------------------------------------------------------------
             */
			else if (currentActivity instanceof ListActivity_Person_Livepost) {
				//Search public resolver
				if (button.getText().equals(res.getString(R.string.action_answerLivepost))) {
					actionDialog.dismiss();
					AndroidModelHelper.answerLivepost(currentActivity, Arrays.asList((PersonItem) di));
				}
			}
		}
	}
	
}