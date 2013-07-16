package eu.dime.mobile.view.abstr;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.view.dialog.Activity_Edit_Item_Dialog;
import eu.dime.mobile.view.dialog.Activity_Unshare_Dialog;
import eu.dime.model.Model;
import eu.dime.model.displayable.DisplayableItem;

public abstract class TabActivityDisplayableItemDetail extends TabActivityDime implements OnClickListener {
	
	protected DisplayableItem di;
	protected boolean ownItem = false;
	
	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		di = (DisplayableItem) AndroidModelHelper.getGenItemSynchronously(this, dio);
		if(di != null) {
			ownItem = di.getUserId().equals(Model.ME_OWNER);
		} else {
			Toast.makeText(this, "Couldn´t load detail view of item!", Toast.LENGTH_LONG).show();
        	finish();
		}
	}
	
	@Override
	protected void init(boolean showHomeButton, boolean showSearchButton, boolean showShareButton, boolean showActionButton) {
		initializeHeader();
		super.init(showHomeButton, showSearchButton, showShareButton, showActionButton);
	}
	
    protected abstract void initializeHeader();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void onClickActionButton() {
		selectedGUIDs = ((ListActivityDime) currentActivity).getSelectionGUIDS();
		List<String> actions = new ArrayList<String>();
		actions.addAll(getActionsForDetailView());
		actions.add(getString(R.string.action_editItem));
		actions.add(getString(R.string.action_share));
		actions.add(getString(R.string.action_unshare));
		actionDialog = UIHelper.createActionDialog(this, actions, this, selectedGUIDs);
		actionDialog.show();
	}
	
	protected abstract List<String> getActionsForDetailView();
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v instanceof Button) {
			Button button = (Button) v;
			Resources res = getResources();
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
				resource.add(di.getGuid());
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
	
	@Override
	protected void onClickShareButton() {
    	AndroidModelHelper.shareResource(this, di);
	}

}
