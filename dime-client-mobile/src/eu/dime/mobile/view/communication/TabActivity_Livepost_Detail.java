package eu.dime.mobile.view.communication;

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

public class TabActivity_Livepost_Detail extends TabActivityDisplayableItemDetail {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Livepost_Detail.class.getSimpleName();
	}
	
	@Override
	protected void initializeTabs() {
    	tabs.add(new DimeTabObject(getResources().getString(R.string.tab_communicationDetail) + " " + di.getName(), Activity_Livepost_Detail.class, dio));
    	TabActivity_Livepost_Detail.this.init(true, false, isOwnItem(), isOwnItem());
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Livepost_Detail> createLVH(TabActivity_Livepost_Detail.this);
	}

	@Override
	protected List<String> getActionsForDetailView() {
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
