package eu.dime.mobile.view.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.FileHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.helper.objects.ResultObject;
import eu.dime.mobile.helper.objects.ResultObjectDisplayable;
import eu.dime.mobile.helper.objects.IResultOfStandardDialog;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.mobile.view.adapter.BaseAdapter_Standard;
import eu.dime.model.ItemFactory;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DataboxItem;
import eu.dime.model.displayable.DisplayableItem;

public class TabActivity_Data extends TabActivityDime implements IResultOfStandardDialog {
	
	private static final int PICK_FILE = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Data.class.getSimpleName();
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_myResourcesData), ListActivity_Resource.class, new DimeIntentObject(TYPES.RESOURCE)));
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_myResourcesDataboxes), ListActivity_Databox.class, new DimeIntentObject(TYPES.DATABOX)));
		super.init(true, true, true, true);
	}

	@Override
	protected void onClickActionButton() {
		Resources res = getResources();
		//actions for ListActivity_Data
		if (currentActivity instanceof ListActivity_Resource) {
			selectedGUIDs = new ArrayList<String>(((ListActivity_Resource) currentActivity).getSelectionGUIDS());
			String[] actionsForResources = { res.getString(R.string.action_uploadFile), res.getString(R.string.action_removeSelectedResources), res.getString(R.string.action_assignToDatabox), res.getString(R.string.action_assignToNewDatabox) };
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForResources), this, selectedGUIDs);
			actionDialog.show();

		}
		//actions for ListActivity_Databox
		else if (currentActivity instanceof ListActivity_Databox) {
			selectedGUIDs = ((ListActivity_Databox) currentActivity).getSelectionGUIDS();
			String[] actionsForDatabox = { res.getString(R.string.action_addNewDatabox), res.getString(R.string.action_deleteSelectedDataboxes) };
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForDatabox), this, selectedGUIDs);
			actionDialog.show();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View view) {
		super.onClick(view);
		if (view instanceof Button) {
			final Activity reftoparent = TabActivity_Data.this;
			Button button = (Button) view;
			Resources res = getResources();
			/**
             * ---------------------------------------------------------------------------------------------------------------------------
             * Actions for ListActivity_Data
             * ---------------------------------------------------------------------------------------------------------------------------
             */
			if (currentActivity instanceof ListActivity_Resource) {
				//upload file
				if (button.getText().equals(res.getString(R.string.action_uploadFile))) {
					actionDialog.dismiss();
					try {
						Intent intent = new Intent();
						intent.setType("*/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE);
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(), "Error! Cannot load file library...", Toast.LENGTH_LONG).show();
						Log.e(e.getClass().getName(), e.getMessage(), e);
					}
				}
				//delete selected resopurces
				else if (button.getText().equals(res.getString(R.string.action_removeSelectedResources))) {
					final String actionName = res.getResourceEntryName(R.string.action_removeSelectedResources);
					actionDialog.dismiss();
					Builder builder = UIHelper.createAlertDialogBuilder(reftoparent, "Really delete selected resources?", true);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							 AndroidModelHelper.deleteGenItemsAsynchronously(currentActivity, TYPES.RESOURCE, mrContext, actionName);
						}
					});
					UIHelper.displayAlertDialog(builder, false);
				}
				//assign to databox
				else if (button.getText().equals(res.getString(R.string.action_assignToDatabox))) {
					actionDialog.dismiss();
					UIHelper.createStandardDialog(reftoparent, mrContext, new BaseAdapter_Standard(), (List<DisplayableItem>) (Object) ModelHelper.getAllDataBoxes(mrContext), ResultObject.RESULT_OBJECT_TYPES.ASSIGN_RESOURCES_TO_DATABOX);
				}
				//assign to new databox
				else if (button.getText().equals(res.getString(R.string.action_assignToNewDatabox))) {
					final String actionName = res.getResourceEntryName(R.string.action_assignToNewDatabox);
					actionDialog.dismiss();
					final EditText text = UIHelper.createEditText(TabActivity_Data.this, InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "type name...", 1, true);
					AlertDialog.Builder builder = UIHelper.createCustomAlertDialogBuilder(TabActivity_Data.this, "Add new Databox", true, text);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, int which) {
							if (text != null && text.getText() != null && text.getText().length() > 0) {
								DataboxItem newBox = (DataboxItem) ItemFactory.createNewDisplayableItemByType(TYPES.DATABOX, text.getText().toString());
								newBox.setItems(selectedGUIDs);
								AndroidModelHelper.createGenItemAsyncronously(newBox, dialog, currentActivity, mrContext, actionName);
							} else {
								UIHelper.createInfoDialog(TabActivity_Data.this, "Please provide a databox name", "ok");
							}
						}
					});
					AlertDialog alert = builder.create();
					alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
					alert.show();
					alert.getButton(AlertDialog.BUTTON_POSITIVE).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				}
			}
			/**
             * ---------------------------------------------------------------------------------------------------------------------------
             * Actions for ListActivity_Databox
             * ---------------------------------------------------------------------------------------------------------------------------
             */
			else if (currentActivity instanceof ListActivity_Databox) {
				//add new databox
				if (button.getText().equals(res.getString(R.string.action_addNewDatabox))) {
					final String actionName = res.getResourceEntryName(R.string.action_addNewDatabox);
					actionDialog.dismiss();
					final EditText text = UIHelper.createEditText(reftoparent, InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "name...", 1, true);
					AlertDialog.Builder builder = UIHelper.createCustomAlertDialogBuilder(reftoparent, "Add new Databox", true, text);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, int which) {
							if (text != null && text.getText() != null && text.getText().length() > 0) {
								DataboxItem newBox = (DataboxItem) ItemFactory.createNewDisplayableItemByType(TYPES.DATABOX, text.getText().toString());
								AndroidModelHelper.createGenItemAsyncronously(newBox, dialog, currentActivity, mrContext, actionName);
							} else {
								UIHelper.createInfoDialog(reftoparent, "Please provide a databox name", "ok");
							}
						}
					});
					UIHelper.displayAlertDialog(builder, true);
				}
				//delete selected databoxes
				else if (button.getText().equals(res.getString(R.string.action_deleteSelectedDataboxes))) {
					final String actionName = res.getResourceEntryName(R.string.action_deleteSelectedDataboxes);
					actionDialog.dismiss();
					Builder builder = UIHelper.createAlertDialogBuilder(reftoparent, "Really delete selected databoxes?", true);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							 AndroidModelHelper.deleteGenItemsAsynchronously(currentActivity, TYPES.DATABOX, mrContext, actionName);
						}
					});
					UIHelper.displayAlertDialog(builder, false);
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent intent) {
		switch (requestCode) {
		case PICK_FILE:
			if (resultCode == Activity.RESULT_OK) {
				ProgressDialog dialog = UIHelper.createCustonProgressDialog(this, "Uploading file to personal server...");
				FileHelper.uploadFile(currentActivity, dialog, intent.getData());
			} else {
				Toast.makeText(TabActivity_Data.this, "No file selected!", Toast.LENGTH_SHORT).show();
			}
			break;
		default:

		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Data>createLVH(TabActivity_Data.this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleResult(ResultObject result) {
		if(result instanceof ResultObjectDisplayable) {
			final String actionName = getResources().getResourceEntryName(R.string.action_assignToDatabox);
			for(DataboxItem databoxItem : (List<DataboxItem>) (Object) ((ResultObjectDisplayable)result).getDisplayables()) {
				databoxItem.addItems(selectedGUIDs);
				AndroidModelHelper.updateGenItemAsyncronously(databoxItem, null, currentActivity, mrContext, actionName);
			}
		}
	}

}
