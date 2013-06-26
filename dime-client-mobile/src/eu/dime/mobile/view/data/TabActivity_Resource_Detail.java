package eu.dime.mobile.view.data;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.mobile.view.dialog.Activity_Edit_Item_Dialog;
import eu.dime.mobile.view.dialog.Activity_Unshare_Dialog;
import eu.dime.model.displayable.ResourceItem;
import java.util.ArrayList;
import java.util.Arrays;

public class TabActivity_Resource_Detail extends TabActivityDime {
	
	private ResourceItem ri = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Resource_Detail.class.getSimpleName();
		ri = (ResourceItem) AndroidModelHelper.getGenItemSynchronously(this, dio);
    	if(ri != null) tabs.add(new DimeTabObject(getResources().getString(R.string.tab_dataDetail) + ri.getName(), Activity_Resource_Detail.class, dio));
    	TabActivity_Resource_Detail.this.init(true, false, true);
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Resource_Detail> createLVH(TabActivity_Resource_Detail.this);
	}

	@Override
	protected void onClickActionButton() {
		Resources res = getResources();
		String[] actionsForDBDetail = { res.getString(R.string.action_editItem), res.getString(R.string.action_share), res.getString(R.string.action_unshare) };
		if (currentActivity instanceof Activity_Resource_Detail) {
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForDBDetail), this, null);
			actionDialog.show();
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v instanceof Button) {
			Button button = (Button) v;
			Resources res = getResources();
			if (currentActivity instanceof Activity_Resource_Detail) {
				//edit item
				if (button.getText().equals(res.getString(R.string.action_editItem))) {
					actionDialog.dismiss();
					Intent intent = new Intent(this, Activity_Edit_Item_Dialog.class);
					startActivity(DimeIntentObjectHelper.populateIntent(intent, dio));
				}
				//share
				if (button.getText().equals(res.getString(R.string.action_share))) {
					actionDialog.dismiss();
					ArrayList<String> resource = new ArrayList<String>();
					resource.add(ri.getGuid());
					AndroidModelHelper.shareResources(this, resource, dio.getItemType());
				}
				//unshare
				if (button.getText().equals(res.getString(R.string.action_unshare))) {
					actionDialog.dismiss();
					Intent intent = new Intent(this, Activity_Unshare_Dialog.class);
					startActivity(DimeIntentObjectHelper.populateIntent(intent, dio));
				}
			}
		}
	}
	
}