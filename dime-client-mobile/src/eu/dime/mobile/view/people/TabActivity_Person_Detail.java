package eu.dime.mobile.view.people;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.helper.objects.IResultOfStandardDialog;
import eu.dime.mobile.helper.objects.ResultObject;
import eu.dime.mobile.helper.objects.ResultObjectProfileSharing;
import eu.dime.mobile.view.abstr.TabActivityDisplayableItemDetail;
import eu.dime.mobile.view.adapter.BaseAdapter_Dialog_Sharing_Profile;
import eu.dime.model.ModelHelper;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ProfileItem;

public class TabActivity_Person_Detail extends TabActivityDisplayableItemDetail implements IResultOfStandardDialog {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Person_Detail.class.getSimpleName();
	}
	
	@Override
	protected void initializeTabs() {
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_personDetailProfile), ListActivity_Person_Profile.class, dio));
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_personDetailData), ListActivity_Person_Data.class, dio));
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_personDetailMessages), ListActivity_Person_Livepost.class, dio));
    	TabActivity_Person_Detail.this.init(true, true, true, true);
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Person_Detail>createLVH(TabActivity_Person_Detail.this);
	}

	@Override
	protected List<String> getActionsForDetailView() {
		ArrayList<String> actions = new ArrayList<String>();
		if (currentActivity instanceof ListActivity_Person_Profile) {
			actions.add(getString(R.string.action_setDefaultProfile));
		} else if (currentActivity instanceof ListActivity_Person_Livepost) {
			actions.add(getString(R.string.action_answerLivepost));
		}
		return actions;
	}

	@Override
	protected void initializeHeader() {
		ViewGroup headerContainer = (ViewGroup) findViewById(R.tabframe.header);
		LinearLayout header = null;
		if(headerContainer.getChildCount() > 0) {
			header = (LinearLayout) headerContainer.getChildAt(0);
		} else {
			header = (LinearLayout) getLayoutInflater().inflate(R.layout.header_standard, headerContainer);
		}
		UIHelper.inflateStandardHeader(this, di, mrContext, header);
	}
	
	@Override
	public void onClick(View view) {
		super.onClick(view);
		if (view instanceof Button) {
            Button button = (Button) view;
            Resources res = getResources();
            /**
             * ---------------------------------------------------------------------------------------------------------------------------
             * Actions for ListActivity_Person_Profile
             * ---------------------------------------------------------------------------------------------------------------------------
             */
			if (currentActivity instanceof ListActivity_Person_Profile) {
				//Set default profile
				if(button.getText().equals(res.getString(R.string.action_setDefaultProfile))) {
					actionDialog.dismiss();
					(new AsyncTask<Void, Void, List<ProfileItem>>() {	
						@Override
						protected List<ProfileItem> doInBackground(Void... params) {
							return ModelHelper.getAllValidProfilesForSharing(DimeClient.getMRC(di.getGuid(), new DummyLoadingViewHandler()));
						}
						@SuppressWarnings("unchecked")
						@Override
						protected void onPostExecute(List<ProfileItem> result) {
							if(result == null || result.size() == 0) {
								Toast.makeText(TabActivity_Person_Detail.this, "Cannot change default profile! No valid profiles for sharing!", Toast.LENGTH_LONG).show();
							} else if (result.size() == 1){
								Toast.makeText(TabActivity_Person_Detail.this, "Cannot change default profile! Only " + result.get(0).getName() + " valid for sharing!", Toast.LENGTH_LONG).show();
							} else {
								UIHelper.createStandardDialog(TabActivity_Person_Detail.this, DimeClient.getMRC(di.getGuid(), new DummyLoadingViewHandler()), new BaseAdapter_Dialog_Sharing_Profile(), (List<DisplayableItem>) (Object) result, ResultObject.RESULT_OBJECT_TYPES.SHARING_PROFILE);
							}
						}

					}).execute();
				}
			}
			/**
             * ---------------------------------------------------------------------------------------------------------------------------
             * Actions for ListActivity_Person_Livepost
             * ---------------------------------------------------------------------------------------------------------------------------
             */
			else if (currentActivity instanceof ListActivity_Person_Livepost) {
				//Search public resolver
				if (button.getText().equals(res.getString(R.string.action_answerLivepost))) {
					actionDialog.dismiss();
					AndroidModelHelper.answerLivepost(currentActivity, Arrays.asList((PersonItem) di));
				}
			}
		}
	}
	
	@Override
	public void handleResult(ResultObject result) {
		if(result instanceof ResultObjectProfileSharing) {
			ProfileItem profile = null;
			String profileName = "null";
			try {
				profile = ((ResultObjectProfileSharing) result).getProfile();
				profileName = profile.getName();
				((PersonItem) di).setDefaultProfileGuid(profile.getGuid());
				AndroidModelHelper.updateGenItemAsynchronously(di, null, this, mrContext, null);
			} catch (Exception e) {
				Toast.makeText(this, "Could not update default profile " + profileName, Toast.LENGTH_LONG).show();
			}
		}
	}
	
}