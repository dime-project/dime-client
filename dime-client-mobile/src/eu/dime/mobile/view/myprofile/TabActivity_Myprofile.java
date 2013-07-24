package eu.dime.mobile.view.myprofile;

import java.util.Arrays;
import android.app.AlertDialog;
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
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.model.ItemFactory;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.ProfileItem;

public class TabActivity_Myprofile extends TabActivityDime {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Myprofile.class.getSimpleName();
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_profileCards), ListActivity_Profile.class, new DimeIntentObject(TYPES.PROFILE)));
		super.init(true, false, true, true);
	}

	@Override
	protected void onClickActionButton() {
		Resources res = getResources();
		String[] actionsForProfileList = { res.getString(R.string.action_newProfile), res.getString(R.string.action_deleteSelectedProfiles) };
		if (currentActivity instanceof ListActivity_Profile) {
			selectedGUIDs = ((ListActivity_Profile) currentActivity).getSelectionGUIDS();
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForProfileList), this, selectedGUIDs);
			actionDialog.show();
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		if (view instanceof Button) {
			Button button = (Button) view;
			Resources res = getResources();
			if (currentActivity instanceof ListActivity_Profile) {
				//delete selected profiles
				if (button.getText().equals(res.getString(R.string.action_deleteSelectedProfiles))) {
					final String actionName = res.getResourceEntryName(R.string.action_deleteSelectedProfiles);
					actionDialog.dismiss();
					AlertDialog.Builder builder = UIHelper.createAlertDialogBuilder(this, "Confirm request", true);
					builder.setMessage("Really delete selected profiles?");
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
	
						@Override
						public void onClick(final DialogInterface dialog, int which) {
							AndroidModelHelper.deleteGenItemsAsynchronously(currentActivity, TYPES.PROFILE, mrContext, actionName);
						}
					});
					UIHelper.displayAlertDialog(builder, false);
				}
				//new profile
				else if (button.getText().equals(res.getString(R.string.action_newProfile))) {
					final String actionName = res.getResourceEntryName(R.string.action_newProfile);
					actionDialog.dismiss();
					final EditText inputName = UIHelper.createEditText(this, InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "name...", 1, true);
					AlertDialog.Builder builder = UIHelper.createCustomAlertDialogBuilder(this, "Add new Profile Card", true, inputName);
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface dialog, int which) {
							if (inputName != null && inputName.getText() != null && inputName.getText().length() > 0) {
								ProfileItem newProfile = (ProfileItem) ItemFactory.createNewDisplayableItemByType(TYPES.PROFILE, inputName.getText().toString());
								newProfile.setEditable(true);
								AndroidModelHelper.createGenItemAsynchronously(newProfile, dialog, currentActivity, mrContext, actionName);
							} else {
								UIHelper.createInfoDialog(TabActivity_Myprofile.this, "Please provide a profile name", "ok");
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
		return LoadingViewHandlerFactory.<TabActivity_Myprofile>createLVH(TabActivity_Myprofile.this);
	}
}
