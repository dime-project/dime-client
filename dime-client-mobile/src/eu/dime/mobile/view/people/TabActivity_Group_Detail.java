package eu.dime.mobile.view.people;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.interfaces.ResultsOfStandardDialogInterface;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.helper.objects.ResultObject;
import eu.dime.mobile.helper.objects.ResultObjectDisplayable;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.mobile.view.adapter.BaseAdapter_Standard;
import eu.dime.model.GenItem;
import eu.dime.model.ModelHelper;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabActivity_Group_Detail extends TabActivityDime implements ResultsOfStandardDialogInterface {

	private GroupItem selectedGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Group_Detail.class.getSimpleName();
		selectedGroup = (GroupItem) AndroidModelHelper.getGenItemSynchronously(this, dio);
    	if(selectedGroup != null) tabs.add(new DimeTabObject(getResources().getString(R.string.tab_groupDetail) + selectedGroup.getName(), ListActivity_Group_Detail.class, dio));
    	TabActivity_Group_Detail.this.init(true, true, true);
	}

	@Override
	protected void onClickActionButton() {
		Resources res = getResources();
		if (currentActivity instanceof ListActivity_Group_Detail) {
			selectedGUIDs = ((ListActivity_Group_Detail) currentActivity).getSelectionGUIDS();
			//actions when group is editable
			if (selectedGroup.isEditable()) {
				String[] actionsForGroupDetail = { res.getString(R.string.action_addPeopleToGroupDetail), res.getString(R.string.action_removeSelectedPeopleFromGroup), res.getString(R.string.action_mergeSelection), res.getString(R.string.action_shareSelectedItems) };
				actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForGroupDetail), this, selectedGUIDs);
			} 
			//action when group is not editable, e.g. facebook
			else {
				String[] actionsForGroupDetailLocked = { res.getString(R.string.action_saveAsNewGroup) };
				actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForGroupDetailLocked), this, selectedGUIDs);
			}
			actionDialog.show();
		}
	}

	@SuppressLint("ShowToast")
	@Override
	@SuppressWarnings("unchecked")
	public void onClick(View v) {
		super.onClick(v);
		if (v instanceof Button) {
			Button button = (Button) v;
			Resources res = getResources();
			if (currentActivity instanceof ListActivity_Group_Detail) {
				//add people to group
				if (button.getText().equals(res.getString(R.string.action_addPeopleToGroupDetail))) {
					actionDialog.dismiss();
					UIHelper.createStandardDialog(this, mrContext, new BaseAdapter_Standard(), (List<DisplayableItem>) (Object) AndroidModelHelper.removeListItemsByMeansOfItems((List<GenItem>) (Object) ModelHelper.getAllPersons(mrContext), (List<GenItem>) (Object) ModelHelper.getPersonsOfGroup(mrContext, selectedGroup)), ResultObject.RESULT_OBJECT_TYPES.ADD_PEOPLE_TO_GROUP); 
				}
				//remove selected people from group
				else if (button.getText().equals(res.getString(R.string.action_removeSelectedPeopleFromGroup))) {
					final String actionName = res.getResourceEntryName(R.string.action_removeSelectedPeopleFromGroup);
					actionDialog.dismiss();
					selectedGroup.removeItems(selectedGUIDs);
					AndroidModelHelper.updateGenItemAsyncronously(selectedGroup, null, currentActivity, mrContext, actionName);
				}
				//merge selection
				else if (button.getText().equals(res.getString(R.string.action_mergeSelection))) {
					actionDialog.dismiss();
					AlertDialog.Builder builder = UIHelper.createAlertDialogBuilder(this, "Merge all selected to one person?", true);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							AndroidModelHelper.mergePersonsAsynchronously(mrContext, selectedGUIDs);
						}
					});
					UIHelper.displayAlertDialog(builder, false);
				}
				//share
				else if (button.getText().equals(res.getString(R.string.action_shareSelectedItems))) {
					actionDialog.dismiss();
					AndroidModelHelper.shareResources(currentActivity, new ArrayList<String>(selectedGUIDs), dio.getItemType());
				}
				//save as new group -> only for adhocgroups
				else if (button.getText().equals(res.getString(R.string.action_saveAsNewGroup))) {
					final String actionName = res.getResourceEntryName(R.string.action_saveAsNewGroup);
					actionDialog.dismiss();
					final EditText text = UIHelper.createEditText(this, InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "name...", 1, true);
					Builder builder = UIHelper.createCustomAlertDialogBuilder(this, "Group name", true, text);
					builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {  
							if (text != null && text.getText() != null && text.getText().length() > 0) {
								selectedGroup.setGroupType(GroupItem.VALID_GROUP_TYPE_VALUES[2]);
								selectedGroup.setName(text.getText().toString());
								AndroidModelHelper.updateGenItemAsyncronously(selectedGroup, dialog, currentActivity, mrContext, actionName);
							} else {
								UIHelper.createInfoDialog(TabActivity_Group_Detail.this, "Please provide a group name!", "ok");
							}
						}
					});
					UIHelper.displayAlertDialog(builder, true);
				}
			}
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Group_Detail>createLVH(TabActivity_Group_Detail.this);
	}

	@Override
	public void handleResult(ResultObject result) {
		final String actionName = getResources().getResourceEntryName(R.string.action_addPeopleToGroupDetail);
		selectedGroup.addItems((AndroidModelHelper.getListOfGuidsOfDisplayableList(((ResultObjectDisplayable)result).getDisplayables())));
		AndroidModelHelper.updateGenItemAsyncronously(selectedGroup, null, currentActivity, mrContext, actionName);
	}
}
