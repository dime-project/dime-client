package eu.dime.mobile.view.people;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.helper.objects.ResultObject;
import eu.dime.mobile.helper.objects.ResultObjectDisplayable;
import eu.dime.mobile.helper.objects.IResultOfStandardDialog;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.mobile.view.adapter.BaseAdapter_Standard;
import eu.dime.mobile.view.dialog.ListActivity_Public_Search_Dialog;
import eu.dime.model.ItemFactory;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import java.util.Arrays;
import java.util.List;

public class TabActivity_People extends TabActivityDime implements IResultOfStandardDialog {

	private static final int IMPORT_CONTACTS = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_People.class.getSimpleName();
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_persons), ListActivity_Person.class, new DimeIntentObject(TYPES.PERSON)));
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_groups), ListActivity_Group.class, new DimeIntentObject(TYPES.GROUP)));
		super.init(true, true, true, true);
	}

	@Override
	protected void onClickActionButton() {
		Resources res = getResources();
		//actions for ListActivity_People_All
		if (currentActivity instanceof ListActivity_Person) {
			selectedGUIDs = ((ListActivity_Person) currentActivity).getSelectionGUIDS();
			String[] actionsForAll = { res.getString(R.string.action_searchPublicResolverService), res.getString(R.string.action_importSpecificContactFromPhoneBook), res.getString(R.string.action_removePerson), res.getString(R.string.action_mergeSelection), res.getString(R.string.action_addToNewGroup), res.getString(R.string.action_addPeopleToGroup) };
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForAll), this, selectedGUIDs);
			actionDialog.show();
		}
		//actions for ListActivity_People_Group
		else if (currentActivity instanceof ListActivity_Group) {
			selectedGUIDs = ((ListActivity_Group) currentActivity).getSelectionGUIDS();
			String[] actionsForGroup = { res.getString(R.string.action_addNewGroup), res.getString(R.string.action_deleteSelectedGroups) };
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForGroup), this, selectedGUIDs);
			actionDialog.show();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@SuppressLint("ShowToast")
	public void onClick(View view) {
		super.onClick(view);
		if (view instanceof Button) {
            Button button = (Button) view;
            Resources res = getResources();
            /**
             * ---------------------------------------------------------------------------------------------------------------------------
             * Actions for ListActivity_People_All
             * ---------------------------------------------------------------------------------------------------------------------------
             */
			if (currentActivity instanceof ListActivity_Person) {
				//Search public resolver
				if (button.getText().equals(res.getString(R.string.action_searchPublicResolverService))) {
					actionDialog.dismiss();
					Intent intent = new Intent(TabActivity_People.this, ListActivity_Public_Search_Dialog.class);
					startActivity(DimeIntentObjectHelper.populateIntent(intent, new DimeIntentObject()));
				}
				//merge persons
				else if (button.getText().equals(res.getString(R.string.action_mergeSelection))) {
					actionDialog.dismiss();
					Builder builder = UIHelper.createAlertDialogBuilder(currentActivity, "Merge all selected to one person?", true);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							AndroidModelHelper.mergePersonsAsynchronously(currentActivity, mrContext, selectedGUIDs);
						}
					});
					UIHelper.displayAlertDialog(builder, false);
				}
				//delete selected persons
				else if (button.getText().equals(res.getString(R.string.action_removePerson))) {
					final String actionName = res.getResourceEntryName(R.string.action_removePerson);
					actionDialog.dismiss();
					Builder builder = UIHelper.createAlertDialogBuilder(TabActivity_People.this, "Really delete selected person(s)?", true);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							AndroidModelHelper.deleteGenItemsAsynchronously(currentActivity, TYPES.PERSON, mrContext, actionName);
						}
					});
					UIHelper.displayAlertDialog(builder, false);
				}
				//add people to group
				else if (button.getText().equals(res.getString(R.string.action_addPeopleToGroup))) {
					actionDialog.dismiss();
					UIHelper.createStandardDialog(this, mrContext, new BaseAdapter_Standard(), (List<DisplayableItem>) (Object) ModelHelper.getAllGroups(mrContext), ResultObject.RESULT_OBJECT_TYPES.ASSIGN_PEOPLE_TO_GROUP);
				}
				//ad people to new group
				else if(button.getText().equals(res.getString(R.string.action_addToNewGroup))) {
					final String actionName = res.getResourceEntryName(R.string.action_addToNewGroup);
					actionDialog.dismiss();
					final EditText text = UIHelper.createEditText(TabActivity_People.this, InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "name...", 1, true);
            		AlertDialog.Builder builder = UIHelper.createCustomAlertDialogBuilder(TabActivity_People.this, "Group name", true, text);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, int which) {
							if(text != null && text.getText() != null && text.getText().length() > 0) {
								GroupItem newGroup = (GroupItem) ItemFactory.createNewDisplayableItemByType(TYPES.GROUP, text.getText().toString());
								newGroup.setItems(selectedGUIDs);
								AndroidModelHelper.createGenItemAsynchronously(newGroup, dialog, currentActivity, mrContext, actionName);
							} else {
								UIHelper.createInfoDialog(TabActivity_People.this, "Please provide a group name", "ok");
							}
						}
					});
					UIHelper.displayAlertDialog(builder, true);
				}
				//import specific contact from phone book
				else if (button.getText().equals(res.getString(R.string.action_importSpecificContactFromPhoneBook))) {
					actionDialog.dismiss();
					try {
						Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
						startActivityForResult(intent, IMPORT_CONTACTS);
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(), "Error! Cannot load phone book...", Toast.LENGTH_LONG).show();
						Log.e(e.getClass().getName(), e.getMessage(), e);
					}
				}
				//import all contacts from phone book
				else if (button.getText().equals(res.getString(R.string.action_importAllContactsFromPhoneBook))) {
					actionDialog.dismiss();
					Builder builder = UIHelper.createAlertDialogBuilder(this, "Really import all contacts from phone book?", true);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							AndroidModelHelper.importAllContacts(TabActivity_People.this, mrContext);
						}
					});
					UIHelper.displayAlertDialog(builder, false);
				}
			}
			/**
	         * ---------------------------------------------------------------------------------------------------------------------------
	         * Actions for ListActivity_People_Group
	         * ---------------------------------------------------------------------------------------------------------------------------
	         */
			else if (currentActivity instanceof ListActivity_Group) {
				//delete selected groups
				if (button.getText().equals(res.getString(R.string.action_deleteSelectedGroups))) {
					final String actionName = res.getResourceEntryName(R.string.action_deleteSelectedGroups);
					actionDialog.dismiss();
					Builder builder = UIHelper.createAlertDialogBuilder(TabActivity_People.this, "Really delete selected group(s)?", true);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							AndroidModelHelper.deleteGenItemsAsynchronously(currentActivity, TYPES.GROUP, mrContext, actionName);
						}
					});
					UIHelper.displayAlertDialog(builder, false);
				}
				//new group
				else if (button.getText().equals(res.getString(R.string.action_addNewGroup))) {
					final String actionName = res.getResourceEntryName(R.string.action_addNewGroup);
					actionDialog.dismiss();
					final Activity reftoparent = TabActivity_People.this;
					final EditText text = UIHelper.createEditText(reftoparent, InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "name...", 1, true);
					Builder builder = UIHelper.createCustomAlertDialogBuilder(reftoparent, "Add new group", true, text);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, int which) {
							if (text != null && text.getText() != null && text.getText().length() > 0) {
								GroupItem newGroup = (GroupItem) ItemFactory.createNewDisplayableItemByType(TYPES.GROUP, text.getText().toString());
								AndroidModelHelper.createGenItemAsynchronously(newGroup, dialog, currentActivity, mrContext, actionName);
							} else {
								UIHelper.createInfoDialog(reftoparent, "Please provide a group name", "ok");
							}
						}
					});
					UIHelper.displayAlertDialog(builder, true);
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case IMPORT_CONTACTS:
			if (resultCode == Activity.RESULT_OK) {
				AndroidModelHelper.importSingleContact(this, mrContext, data.getData());
			}
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_People>createLVH(TabActivity_People.this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleResult(ResultObject result) {
		if(result instanceof ResultObjectDisplayable) {
			String actionName = getResources().getResourceEntryName(R.string.action_addPeopleToGroup);
			for(GroupItem groupItem : (List<GroupItem>)(Object)((ResultObjectDisplayable)result).getDisplayables()){
				groupItem.addItems(selectedGUIDs);
				AndroidModelHelper.updateGenItemAsynchronously(groupItem, null, currentActivity, mrContext, actionName);
			}
		}
	}
	
}
