package eu.dime.mobile.view.communication;

import java.util.ArrayList;
import java.util.Arrays;
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
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.mobile.view.dialog.Activity_Share_New_Livepost_Dialog;
import eu.dime.model.TYPES;

public class TabActivity_Communication extends TabActivityDime {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Communication.class.getSimpleName();
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_communication), ListActivity_Livepost.class, new DimeIntentObject(TYPES.LIVEPOST)));
		init(true, true, true, true);
	}

	@Override
	protected void onClickActionButton() {
		Resources res = getResources();
		String[] actionsForResources = { res.getString(R.string.action_newMessage), res.getString(R.string.action_removeLivepost) };
		if (currentActivity instanceof ListActivity_Livepost) {
			selectedGUIDs = new ArrayList<String>(((ListActivity_Livepost) currentActivity).getSelectionGUIDS());
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForResources), this, selectedGUIDs);
			actionDialog.show();
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		if (view instanceof Button) {
			Button button = (Button) view;
			Resources res = getResources();
			if (currentActivity instanceof ListActivity_Livepost) {
				//new livepost
				if (button.getText().equals(res.getString(R.string.action_newMessage))) {
					Intent myIntent = new Intent(TabActivity_Communication.this, Activity_Share_New_Livepost_Dialog.class);
	                this.startActivity(DimeIntentObjectHelper.populateIntent(myIntent, new DimeIntentObject(TYPES.LIVEPOST)));
//					final String actionName = res.getResourceEntryName(R.string.action_newMessage);
//					actionDialog.dismiss();
//					final LinearLayout ll = UIHelper.createLinearLayout(TabActivity_Communication.this, LinearLayout.VERTICAL);
//					final EditText subjectArea = UIHelper.createEditText(TabActivity_Communication.this, InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "enter subject...", 1, true);
//					final EditText textarea = UIHelper.createEditText(TabActivity_Communication.this, InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "enter message...", 4, false);
//					ll.addView(subjectArea);
//					ll.addView(textarea);
//					Builder builder = UIHelper.createCustomAlertDialogBuilder(TabActivity_Communication.this, "New message", true, ll);
//					builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(final DialogInterface dialog, int which) {
//							if (subjectArea.getText().length() > 0 ) {
//								LivePostItem lpi = (LivePostItem) ItemFactory.createNewDisplayableItemByType(TYPES.LIVEPOST, "" + subjectArea.getText());
//								lpi.setText("" + textarea.getText());
//								lpi.setTimeStamp(System.currentTimeMillis());
//								AndroidModelHelper.createGenItemAsyncronously(lpi, dialog, currentActivity, mrContext, actionName);
//							} else {
//								dialog.dismiss();
//								UIHelper.createInfoDialog(TabActivity_Communication.this, "You need to specify a subject for the livepost!", "ok");
//							}
//						}
//					});
//					UIHelper.displayAlertDialog(builder, true);
				}
				//remove livepost
				else if (button.getText().equals(res.getString(R.string.action_removeLivepost))) {
					final String actionName = res.getResourceEntryName(R.string.action_removeLivepost);
					actionDialog.dismiss();
					AndroidModelHelper.deleteGenItemsAsynchronously(currentActivity, TYPES.LIVEPOST, mrContext, actionName);
				}
			}
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Communication> createLVH(TabActivity_Communication.this);
	}
}
