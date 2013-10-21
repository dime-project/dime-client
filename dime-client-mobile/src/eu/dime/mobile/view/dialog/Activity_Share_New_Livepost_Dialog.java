package eu.dime.mobile.view.dialog;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.ResultObject;
import eu.dime.mobile.helper.objects.ResultObjectDisplayable;
import eu.dime.mobile.helper.objects.ResultObjectProfileSharing;
import eu.dime.mobile.helper.objects.IResultOfStandardDialog;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.mobile.view.adapter.BaseAdapter_Dialog_Sharing_Profile;
import eu.dime.mobile.view.adapter.BaseAdapter_Standard;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.AgentItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ProfileItem;
import java.util.ArrayList;
import java.util.List;

public class Activity_Share_New_Livepost_Dialog extends ActivityDime implements OnClickListener, IResultOfStandardDialog, OnSeekBarChangeListener {

	private ImageView image;
	private TextView fullName;
	private TextView profileName;
	private TextView attribute;
//	private TextView labelWarnings;
	private TextView labelReceivers;
	private TextView noReceivers;
//	private TextView noWarnings;
	private Button share;
    private LinearLayout recieverContainer;
//    private LinearLayout warningsContainer;
    private List<GroupItem> allGroups;
    private List<PersonItem> allPersonsValidForSharing;
    private List<ProfileItem> allProfilesValidForSharing;
    private List<AgentItem> listOfSelectedAgents = new ArrayList<AgentItem>();
    private List<GroupItem> selectedGroups = new ArrayList<GroupItem>();
    private List<PersonItem> selectedPersons = new ArrayList<PersonItem>();
    private ProfileItem selectedProfile;
    private SeekBar privacy;
    private TextView privacyText;
    private EditText livepostSubject;
    private EditText livepostMessage;
    private View coloredBar;
    LivePostItem livepost = (LivePostItem) ItemFactory.createNewDisplayableItemByType(TYPES.LIVEPOST, "");

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = Activity_Share_New_Livepost_Dialog.class.getSimpleName();
        setContentView(R.layout.dialog_share_new_livepost);
        image = (ImageView) findViewById(R.share.image_profile);
    	fullName = (TextView) findViewById(R.share.text_full_name);
    	profileName = (TextView) findViewById(R.share.text_profile_name);
    	attribute = (TextView) findViewById(R.share.text_attribute);
        recieverContainer = (LinearLayout) findViewById(R.share.container_reciever);
//        labelWarnings = (TextView) findViewById(R.share.label_warnings);
        labelReceivers = (TextView) findViewById(R.share.label_receivers);
        noReceivers = (TextView) findViewById(R.share.text_no_reciever);
        coloredBar = findViewById(R.share.coloredBar);
//        noWarnings = (TextView) findViewById(R.share.text_no_warnings);
        share = (Button) findViewById(R.share.button_share);
        privacy = (SeekBar) findViewById(R.share.bar);
        privacyText = (TextView) findViewById(R.share.bar_text);
        livepostSubject = (EditText) findViewById(R.share.livepost_subject);
        livepostMessage = (EditText) findViewById(R.share.livepost_message);
        privacy.setOnSeekBarChangeListener(this);
        Integer level = AndroidModelHelper.getTrustOrPrivacyLevelForDisplayableItem(livepost.getPrivacyLevel());
        if(level != null) {
        	coloredBar.setBackgroundColor(UIHelper.getWarningColor(this, livepost));
			privacy.setProgress(level); 
		}
    }

    @Override
    protected void onResume() {
        super.onResume();
        share.setEnabled(false);
        DimeClient.addStringToViewStack(TAG.substring(9)); //remove Activity_
        startTask("Initializing share dialog...");
    }
    
    @Override
    protected void loadData() {
        allPersonsValidForSharing = ModelHelper.getAllPersonsValidForSharing(DimeClient.getMRC(dio.getOwnerId(), new DummyLoadingViewHandler()));
        allGroups = ModelHelper.getAllGroups(DimeClient.getMRC(dio.getOwnerId(), new DummyLoadingViewHandler()));
        allProfilesValidForSharing = ModelHelper.getAllValidProfilesForSharing(DimeClient.getMRC(dio.getOwnerId(), new DummyLoadingViewHandler()));
        selectedPersons = AndroidModelHelper.getListOfItemsWithGuids(ModelHelper.getAllPersons(DimeClient.getMRC(dio.getOwnerId(), new DummyLoadingViewHandler())), getIntent().getStringArrayListExtra(ModelHelper.getStringType(TYPES.PERSON)));
//        selectedGroups = AndroidModelHelper.getListOfItemsWithGuids(allGroups, getIntent().getStringArrayListExtra(TYPES.GROUP.toString()));
//        listOfSelectedAgents.addAll(selectedGroups);
//        listOfSelectedAgents.addAll(selectedPersons);
        String profileGuid = getIntent().getStringExtra(ModelHelper.getStringType(TYPES.PROFILE));
        if(profileGuid != null) {
        	selectedProfile = (ProfileItem) Model.getInstance().getItem(DimeClient.getMRC(dio.getOwnerId(), new DummyLoadingViewHandler()), TYPES.PROFILE, profileGuid);
        }
        if(selectedProfile == null || !selectedProfile.supportsSharing()) {
        	selectedProfile = ModelHelper.getDefaultProfileForSharing(mrContext, listOfSelectedAgents);
        }
    }
    
    @Override
    protected void initializeData() {
        updateViewOnSelectionChanged();
    }

    @SuppressWarnings("unchecked")
	public void updateViewOnSelectionChanged() {
        //update text fields
        recieverContainer.removeAllViews();
        listOfSelectedAgents.clear();
        listOfSelectedAgents.addAll(selectedGroups);
        listOfSelectedAgents.addAll(selectedPersons);
        share.setEnabled(isSharingPossible());
        updateSelectedProfile(selectedProfile);
        addWidgets(recieverContainer,(List<DisplayableItem>) (Object) listOfSelectedAgents, labelReceivers, noReceivers);
//        if(isSharingPossible()) {
//	        //generate recommendation in the background
//        	labelWarnings.setText(String.valueOf(0));
//        	noWarnings.setVisibility(View.VISIBLE);
//            noWarnings.setText("Loading...");
//	        AndroidModelHelper.loadAdvisoryPropertiesAsyncronously(this, new AdvisoryRequestItem(selectedProfile.getGuid(), AndroidModelHelper.getListOfGuidsOfGenItemList((List<GenItem>) (Object) listOfSelectedAgents), AndroidModelHelper.getListOfGuidsOfGenItemList(listOfSelectedItems)), advisoryItemsNotValidAgentsForSharing, noWarnings, labelWarnings, warningsContainer);
//        } else {
//        	noWarnings.setVisibility(View.GONE);
//        	warningsContainer.addView(UIHelper.createWarningWidget(this, UIHelper.getAdvisoryProperties(this, new AdvisoryItem(1.0d, AdvisoryItem.WARNING_TYPES[6], new WarningSharingNotPossible()))));
//        	labelWarnings.setText(String.valueOf(advisoryItemsNotValidAgentsForSharing.size() + 1));
//        	for (AdvisoryProperties advisory : advisoryItemsNotValidAgentsForSharing) {
//				warningsContainer.addView(UIHelper.createWarningWidget(this, advisory));
//			}
//        }
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.share.button_sel_person:
        	UIHelper.createStandardDialog(Activity_Share_New_Livepost_Dialog.this, mrContext, new BaseAdapter_Standard(), (List<DisplayableItem>)(Object) AndroidModelHelper.getItemsForSelection(allPersonsValidForSharing, selectedPersons), ResultObject.RESULT_OBJECT_TYPES.SHARING_PERSONS);
            break;

        case R.share.button_sel_group:
        	UIHelper.createStandardDialog(this, mrContext, new BaseAdapter_Standard(), (List<DisplayableItem>)(Object) AndroidModelHelper.getItemsForSelection(allGroups, selectedGroups), ResultObject.RESULT_OBJECT_TYPES.SHARING_GROUPS);
            break;
        case R.share.container_profile:
        	UIHelper.createStandardDialog(Activity_Share_New_Livepost_Dialog.this, mrContext, new BaseAdapter_Dialog_Sharing_Profile(), (List<DisplayableItem>) (Object) allProfilesValidForSharing, ResultObject.RESULT_OBJECT_TYPES.SHARING_PROFILE);
            break;

        case R.share.button_share:
        	if(!isSharingPossible()) {
                String message = "Please select ";
                if(listOfSelectedAgents.size() == 0) message += "an agent, ";
                if(selectedProfile == null) message +="a profile";
                message += "!";
            	AlertDialog infoAlert = UIHelper.createInfoDialog(this, message, "ok");
                infoAlert.show();
            }  
            else {
            	livepost = (LivePostItem) ItemFactory.createNewDisplayableItemByType(TYPES.LIVEPOST, livepostSubject.getText().toString());
            	livepost.setPrivacyLevel(AndroidModelHelper.normalizeTrustOrPrivacyLevelForDisplayableItem(privacy.getProgress()));
            	livepost.setText(livepostMessage.getText().toString());
            	livepost.setTimeStamp(System.currentTimeMillis());
            	AndroidModelHelper.shareNewLivepostAsynchronously(Activity_Share_New_Livepost_Dialog.this, mrContext, listOfSelectedAgents, livepost, selectedProfile);
            }
            break;
            
        case R.share.button_cancel:
        	List<GenItem> items = new ArrayList<GenItem>();
        	items.addAll((List<GenItem>) (Object) listOfSelectedAgents);
        	items.add(selectedProfile);
        	AndroidModelHelper.sendEvaluationDataAsynchronously(items, mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_canceled));
        	finish();
        	break;
        }
    }

    
    private boolean isSharingPossible() {
    	return (listOfSelectedAgents != null && selectedProfile != null && listOfSelectedAgents.size() > 0);
    }
    
    @SuppressWarnings("unchecked")
	private void addWidgets(LinearLayout ll, List<DisplayableItem> items, TextView labelCount, TextView noItemsText){
    	labelCount.setText(String.valueOf(items.size()));
    	if(items.size() > 0){
    		noItemsText.setVisibility(View.GONE);
	    	for(DisplayableItem item : items){
	    		if(item.getMType().equals(TYPES.GROUP)) {
	    			ll.addView(UIHelper.createSharingWidget(this, item, (List<DisplayableItem>) (Object) selectedGroups));
		    	} else if(item.getMType().equals(TYPES.PERSON)) {
		    		ll.addView(UIHelper.createSharingWidget(this, item, (List<DisplayableItem>) (Object) selectedPersons));
		    	}
	        }
    	} else {
    		noItemsText.setVisibility(View.VISIBLE);
    	}
    }
    
    private void updateSelectedProfile(ProfileItem item) {
    	fullName.setText("");
    	profileName.setText("");
    	attribute.setText("");
    	if(item != null) {
	    	ImageHelper.loadImageAsynchronously(image, item, Activity_Share_New_Livepost_Dialog.this);
	    	AndroidModelHelper.loadProfileAttributesOfProfileAsynchronously(this, fullName, item, "fullname");
	    	AndroidModelHelper.loadProfileAttributesOfProfileAsynchronously(this, attribute, item, null);
	    	profileName.setText(item.getName());
    	} else {
    		profileName.setText("<no profile selected>");
    		fullName.setText("...");
    	}
    }

	@SuppressWarnings("unchecked")
	@Override
	public void handleResult(ResultObject result) {
		if(result instanceof ResultObjectProfileSharing) {
			selectedProfile = ((ResultObjectProfileSharing) result).getProfile();
		} else if (result instanceof ResultObjectDisplayable) {
			switch (result.getType()) {
			case SHARING_PERSONS:
				selectedPersons.addAll((List<PersonItem>)(Object)((ResultObjectDisplayable) result).getDisplayables());
				break;
			case SHARING_GROUPS:
				selectedGroups.addAll((List<GroupItem>)(Object)((ResultObjectDisplayable)result).getDisplayables());
				break;
			default:
				break;
			}
		}
		updateViewOnSelectionChanged();
	}
	
	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) { }

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Share_New_Livepost_Dialog>createLVH(Activity_Share_New_Livepost_Dialog.this);
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		livepost.setPrivacyLevel(AndroidModelHelper.normalizeTrustOrPrivacyLevelForDisplayableItem(seekBar.getProgress()));
		privacyText.setText(UIHelper.getWarningText(livepost));
		coloredBar.setBackgroundColor(UIHelper.getWarningColor(this, livepost));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}
	
}
