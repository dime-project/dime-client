package eu.dime.mobile.view.communication;

import java.util.ArrayList;
import java.util.Arrays;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.LivePostItem;

public class TabActivity_Livepost_Detail extends TabActivityDime {
	
	private LivePostItem lpi = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Livepost_Detail.class.getSimpleName();
    	lpi = (LivePostItem) AndroidModelHelper.getGenItemSynchronously(this, dio);
    	if(lpi != null) tabs.add(new DimeTabObject(getResources().getString(R.string.tab_communicationDetail) + " " + lpi.getName(), Activity_Livepost_Detail.class, dio));
    	TabActivity_Livepost_Detail.this.init( true, false, true);
	}

	@Override
	protected void onClickActionButton() {
		Resources res = getResources();
		String[] actionsForResources = { res.getString(R.string.action_share) };
		if (currentActivity instanceof Activity_Livepost_Detail) {
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForResources), this, null);
			actionDialog.show();
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		if (view instanceof Button) {
			Button button = (Button) view;
			Resources res = getResources();
			if (currentActivity instanceof Activity_Livepost_Detail) {
				//share
				if (button.getText().equals(res.getString(R.string.action_share))) {
					actionDialog.dismiss();
					ArrayList<String> liveposts = new ArrayList<String>();
					if(lpi != null) { liveposts.add(lpi.getGuid()); }
					AndroidModelHelper.shareResources(this, liveposts, TYPES.LIVEPOST);
				}
			}
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Livepost_Detail> createLVH(TabActivity_Livepost_Detail.this);
	}
}
