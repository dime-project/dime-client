package eu.dime.mobile.view.people;

import java.util.ArrayList;
import java.util.Arrays;
import android.annotation.SuppressLint;
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
import eu.dime.model.displayable.PersonItem;

public class TabActivity_Person_Detail extends TabActivityDime {
	
	private PersonItem pi; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Person_Detail.class.getSimpleName();
        pi = (PersonItem) AndroidModelHelper.getGenItemSynchronously(this, dio);
    	if(pi != null) {
			tabs.add(new DimeTabObject(getResources().getString(R.string.tab_personDetailProfile), ListActivity_Person_Profile.class, dio));
			tabs.add(new DimeTabObject(getResources().getString(R.string.tab_personDetailData), ListActivity_Person_Data.class, dio));
			tabs.add(new DimeTabObject(getResources().getString(R.string.tab_personDetailMessages), ListActivity_Person_Livepost.class, dio));
		}
    	TabActivity_Person_Detail.this.init(true, true, true);
	}
	
	@Override
	protected void onClickActionButton() {
		if (currentActivity instanceof ListActivity_Person_Profile) {
			selectedGUIDs = ((ListActivity_Person_Profile) currentActivity).getSelectionGUIDS();
			String[] actionsForPersonProfile = {"Set as standard profile", getResources().getString(R.string.action_share)}; //FIXME
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForPersonProfile), this, selectedGUIDs);
			actionDialog.show();
		} else if (currentActivity instanceof ListActivity_Person_Data) {
			selectedGUIDs = ((ListActivity_Person_Data) currentActivity).getSelectionGUIDS();
			String[] actionsForPersonData = { getResources().getString(R.string.action_shareSelectedItems)};
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForPersonData), this, selectedGUIDs);
			actionDialog.show();
		} else if (currentActivity instanceof ListActivity_Person_Livepost) {
			selectedGUIDs = ((ListActivity_Person_Livepost) currentActivity).getSelectionGUIDS();
			String[] actionsForPersonLivestream = { getResources().getString(R.string.action_shareSelectedItems)};
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForPersonLivestream), this, selectedGUIDs);
			actionDialog.show();
		}	
	}

	@Override
	@SuppressLint("ShowToast")
	public void onClick(View view) {
		super.onClick(view);
		if (view instanceof Button) {
            Button button = (Button) view;
			if (button.getText().equals(getResources().getString(R.string.action_share)) || button.getText().equals(getResources().getString(R.string.action_shareSelectedItems))) {
				actionDialog.dismiss();
				//share to person
				if (currentActivity instanceof ListActivity_Person_Profile) {
					ArrayList<String> person = new ArrayList<String>();
					person.add(pi.getGuid());
					AndroidModelHelper.shareResources(TabActivity_Person_Detail.this, person, TYPES.PERSON);
				}
				//share selected liveposts
				else if (currentActivity instanceof ListActivity_Person_Livepost) {
					AndroidModelHelper.shareResources(TabActivity_Person_Detail.this, new ArrayList<String>(selectedGUIDs), TYPES.LIVEPOST);
				}
				//share selected resources
				else if (currentActivity instanceof ListActivity_Person_Data) {
					AndroidModelHelper.shareResources(TabActivity_Person_Detail.this, new ArrayList<String>(selectedGUIDs), TYPES.RESOURCE);
				}
			}
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Person_Detail>createLVH(TabActivity_Person_Detail.this);
	}
}

