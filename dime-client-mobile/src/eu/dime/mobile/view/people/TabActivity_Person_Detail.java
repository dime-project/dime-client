package eu.dime.mobile.view.people;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.TabActivityDisplayableItemDetail;

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
		//FIXME add standard profile for instance ListActivity_Person_Profile
		return new ArrayList<String>();
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
	
}