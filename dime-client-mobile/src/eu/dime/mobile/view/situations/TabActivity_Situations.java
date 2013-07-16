package eu.dime.mobile.view.situations;

import java.util.Arrays;

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
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.model.ItemFactory;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.SituationItem;

public class TabActivity_Situations extends TabActivityDime {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Situations.class.getSimpleName();
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_situations), ListActivity_Situations.class, new DimeIntentObject(TYPES.SITUATION)));
		if(ModelHelper.isFitbitAdapterConnected(mrContext)) tabs.add(new DimeTabObject(getResources().getString(R.string.tab_activities), ListActivity_Activities.class, new DimeIntentObject(TYPES.ACTIVITY)));
		super.init(true, false, false, true);
	}

	@Override
	protected void onClickActionButton() {
		String[] actionsForSituationsList = { getResources().getString(R.string.action_newSituation), getResources().getString(R.string.action_deleteSituation) };
		if (currentActivity instanceof ListActivity_Situations) {
			selectedGUIDs = ((ListActivity_Situations) currentActivity).getSelectionGUIDS();
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForSituationsList), this, selectedGUIDs);
			actionDialog.show();
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		if (view instanceof Button) {
			Button button = (Button) view;
			Resources res = getResources();
			if (currentActivity instanceof ListActivity_Situations) {
				//new Situation
				if (button.getText().equals(res.getString(R.string.action_newSituation))) {
					final String actionName = res.getResourceEntryName(R.string.action_newSituation);
					actionDialog.dismiss();
					final EditText text = UIHelper.createEditText(this, InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "situation name...", 1, true);
					Builder builder = UIHelper.createCustomAlertDialogBuilder(this, "New Situation", true, text);
					builder.setPositiveButton("new", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog, int which) {  
							if(text != null && text.getText() != null && text.getText().length() > 0) {
								SituationItem si = (SituationItem) ItemFactory.createNewDisplayableItemByType(TYPES.SITUATION, text.getText().toString());
								AndroidModelHelper.createGenItemAsyncronously(si, dialog, currentActivity, mrContext, actionName);
							} else {
								UIHelper.createInfoDialog(TabActivity_Situations.this, "Please provide a situation name", "ok");
							}
						}
					});
					UIHelper.displayAlertDialog(builder, true);
				}
				//delete situation
				else if (button.getText().equals(res.getString(R.string.action_deleteSituation))) {
					final String actionName = res.getResourceEntryName(R.string.action_deleteSituation);
					actionDialog.dismiss();
					AndroidModelHelper.deleteGenItemsAsynchronously(currentActivity, TYPES.SITUATION, mrContext, actionName);
				}
			}
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Situations>createLVH(TabActivity_Situations.this);
	}
}
