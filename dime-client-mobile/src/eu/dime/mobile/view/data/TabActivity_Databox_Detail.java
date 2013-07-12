package eu.dime.mobile.view.data;

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
import eu.dime.mobile.helper.objects.ResultObject;
import eu.dime.mobile.helper.objects.ResultObjectDisplayable;
import eu.dime.mobile.helper.objects.IResultOfStandardDialog;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.mobile.view.adapter.BaseAdapter_Standard;
import eu.dime.model.GenItem;
import eu.dime.model.ModelHelper;
import eu.dime.model.displayable.DataboxItem;
import eu.dime.model.displayable.DisplayableItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabActivity_Databox_Detail extends TabActivityDime implements IResultOfStandardDialog {

	private DataboxItem selectedDB;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Databox_Detail.class.getSimpleName();
		selectedDB = (DataboxItem) AndroidModelHelper.getGenItemSynchronously(this, dio);
    	if(selectedDB != null) tabs.add(new DimeTabObject(getResources().getString(R.string.tab_databoxDetail) + selectedDB.getName(), ListActivity_Databox_Detail.class, dio));
    	TabActivity_Databox_Detail.this.init(true, true, true);
	}

	@Override
	protected void onClickActionButton() {
		Resources res = getResources();
		String[] actionsForDBDetail = { res.getString(R.string.action_addResourcesToDatabox), res.getString(R.string.action_removeResourcesFromDatabox), res.getString(R.string.action_shareSelectedItems) };
		if (currentActivity instanceof ListActivity_Databox_Detail) {
			selectedGUIDs = ((ListActivity_Databox_Detail) currentActivity).getSelectionGUIDS();
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForDBDetail), this, selectedGUIDs);
			actionDialog.show();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v instanceof Button) {
			Button button = (Button) v;
			Resources res = getResources();
			//add resources to databox
			if (button.getText().equals(res.getString(R.string.action_addResourcesToDatabox))) {
				actionDialog.dismiss();
				UIHelper.createStandardDialog(TabActivity_Databox_Detail.this, mrContext, new BaseAdapter_Standard(), (List<DisplayableItem>) (Object) AndroidModelHelper.removeListItemsByMeansOfItems((List<GenItem>) (Object) ModelHelper.getAllResources(mrContext), (List<GenItem>) (Object) ModelHelper.getResourcesOfDatabox(mrContext, selectedDB)), ResultObject.RESULT_OBJECT_TYPES.ADD_RESOURCES_TO_DATABOX);
			}
			//remove resources from databox
			else if (button.getText().equals(res.getString(R.string.action_removeResourcesFromDatabox))) {
				final String actionName = res.getResourceEntryName(R.string.action_removeResourcesFromDatabox);
				actionDialog.dismiss();
				selectedDB.removeItems(selectedGUIDs);
				AndroidModelHelper.updateGenItemAsyncronously(selectedDB, null, currentActivity, mrContext, actionName);
			}
			//share
			else if (button.getText().equals(res.getString(R.string.action_shareSelectedItems))) {
				actionDialog.dismiss();
				AndroidModelHelper.shareResources(this, new ArrayList<String>(selectedGUIDs), ModelHelper.getChildType(dio.getItemType()));
			}
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Databox_Detail> createLVH(TabActivity_Databox_Detail.this);
	}

	@Override
	public void handleResult(ResultObject result) {
		final String actionName = getResources().getResourceEntryName(R.string.action_addResourcesToDatabox);
		selectedDB.addItems(AndroidModelHelper.getListOfGuidsOfDisplayableList(((ResultObjectDisplayable)result).getDisplayables()));
		AndroidModelHelper.updateGenItemAsyncronously(selectedDB, null, currentActivity, mrContext, actionName);
	}
}