package eu.dime.mobile.view.dialog;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import eu.dime.control.SilentLoadingViewHandler;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.AdvisoryProperties;
import eu.dime.mobile.helper.objects.ResultObject;
import eu.dime.mobile.helper.objects.ResultObjectDisplayable;
import eu.dime.mobile.helper.objects.ResultObjectProfileSharing;
import eu.dime.mobile.helper.objects.IResultOfStandardDialog;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.mobile.view.adapter.BaseAdapter_Dialog_Sharing_Profile;
import eu.dime.mobile.view.adapter.BaseAdapter_Standard;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.advisory.AdvisoryItem;
import eu.dime.model.specialitem.advisory.AdvisoryRequestItem;
import eu.dime.model.specialitem.advisory.WarningAgentNotValidForSharing;
import eu.dime.model.specialitem.advisory.WarningSharingNotPossible;
import eu.dime.model.GenItem;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.AgentItem;
import eu.dime.model.displayable.DataboxItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.displayable.ResourceItem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class Activity_Share_Dialog extends ActivityDime implements OnClickListener, IResultOfStandardDialog {

	private ImageView image;
	private TextView fullName;
	private TextView profileName;
	private TextView attribute;
	private TextView labelItems;
	private TextView labelWarnings;
	private TextView labelReceivers;
	private TextView noReceivers;
	private TextView noItems;
	private TextView noWarnings;
	private Button share;
    private LinearLayout dataContainer;
    private LinearLayout recieverContainer;
    private LinearLayout warningsContainer;
    private List<GroupItem> allGroups;
    private List<PersonItem> allPersonsValidForSharing;
    private List<DataboxItem> allDataboxes;
    private List<ResourceItem> allResources;
    private List<LivePostItem> allLiveposts;
    private List<ProfileItem> allProfilesValidForSharing;
    private List<AgentItem> listOfSelectedAgents = new ArrayList<AgentItem>();
    private List<GenItem> listOfSelectedItems = new ArrayList<GenItem>();
    private List<AdvisoryProperties> advisoryItemsNotValidAgentsForSharing = new Vector<AdvisoryProperties>();
    private List<GroupItem> selectedGroups = new ArrayList<GroupItem>();
    private List<PersonItem> selectedPersons = new ArrayList<PersonItem>();
    private List<DataboxItem> selectedDataboxes = new ArrayList<DataboxItem>();
    private List<ResourceItem> selectedResources = new ArrayList<ResourceItem>();
    private List<LivePostItem> selectedLiveposts = new ArrayList<LivePostItem>();
    private ProfileItem selectedProfile;
    private boolean firstLoad = true;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = Activity_Share_Dialog.class.getSimpleName();
        setContentView(R.layout.dialog_share);
        image = (ImageView) findViewById(R.share.image_profile);
    	fullName = (TextView) findViewById(R.share.text_full_name);
    	profileName = (TextView) findViewById(R.share.text_profile_name);
    	attribute = (TextView) findViewById(R.share.text_attribute);
        dataContainer = (LinearLayout) findViewById(R.share.container_data);
        warningsContainer = (LinearLayout) findViewById(R.share.container_warnings);
        recieverContainer = (LinearLayout) findViewById(R.share.container_reciever);
        labelWarnings = (TextView) findViewById(R.share.label_warnings);
        labelReceivers = (TextView) findViewById(R.share.label_receivers);
        labelItems = (TextView) findViewById(R.share.label_items);
        noReceivers = (TextView) findViewById(R.share.text_no_reciever);
        noItems = (TextView) findViewById(R.share.text_no_items);
        noWarnings = (TextView) findViewById(R.share.text_no_warnings);
        share = (Button) findViewById(R.share.button_share);
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
        allPersonsValidForSharing = ModelHelper.getAllPersonsValidForSharing(DimeClient.getMRC(dio.getOwnerId(), new SilentLoadingViewHandler()));
        allResources = ModelHelper.getAllResources(DimeClient.getMRC(dio.getOwnerId(), new SilentLoadingViewHandler()));
        allLiveposts = ModelHelper.getAllLivePosts(DimeClient.getMRC(dio.getOwnerId(), new SilentLoadingViewHandler()));
        allDataboxes = ModelHelper.getAllDataBoxes(DimeClient.getMRC(dio.getOwnerId(), new SilentLoadingViewHandler()));
        allGroups = ModelHelper.getAllGroups(DimeClient.getMRC(dio.getOwnerId(), new SilentLoadingViewHandler()));
        allProfilesValidForSharing = ModelHelper.getAllValidProfilesForSharing(DimeClient.getMRC(dio.getOwnerId(), new SilentLoadingViewHandler()));
        selectedPersons = AndroidModelHelper.getListOfItemsWithGuids(ModelHelper.getAllPersons(DimeClient.getMRC(dio.getOwnerId(), new SilentLoadingViewHandler())), getIntent().getStringArrayListExtra(ModelHelper.getStringType(TYPES.PERSON)));
        selectedResources = AndroidModelHelper.getListOfItemsWithGuids(allResources, getIntent().getStringArrayListExtra(ModelHelper.getStringType(TYPES.RESOURCE)));
		selectedLiveposts = AndroidModelHelper.getListOfItemsWithGuids(allLiveposts, getIntent().getStringArrayListExtra(ModelHelper.getStringType(TYPES.LIVEPOST)));
        selectedGroups = AndroidModelHelper.getListOfItemsWithGuids(allGroups, getIntent().getStringArrayListExtra(ModelHelper.getStringType(TYPES.GROUP)));
        selectedDataboxes = AndroidModelHelper.getListOfItemsWithGuids(allDataboxes, getIntent().getStringArrayListExtra(ModelHelper.getStringType(TYPES.DATABOX)));
        listOfSelectedAgents.addAll(selectedGroups);
        listOfSelectedAgents.addAll(selectedPersons);
        List<ProfileItem> selectedProfiles = AndroidModelHelper.getListOfItemsWithGuids(allProfilesValidForSharing, getIntent().getStringArrayListExtra(ModelHelper.getStringType(TYPES.PROFILE)));
        if(selectedProfiles.size() > 0) {
        	selectedProfile = selectedProfiles.get(0);
        	if(selectedProfiles.size() > 1) {
        		advisoryItemsNotValidAgentsForSharing.add(new AdvisoryProperties("You can only select one profile", "More than one profiles were selected. " + (selectedProfiles.size() -1) + " profiles are ignored!", R.drawable.icon_small_info));
        	}
        } else {
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
        dataContainer.removeAllViews();
        warningsContainer.removeAllViews();
        if(firstLoad) {
        	firstLoad = false;
        } else {
        	advisoryItemsNotValidAgentsForSharing.clear();
        }
        checkValidityOfChildrenOfGroups(selectedGroups);
        checkValidityOfPersons(selectedPersons, "");
        listOfSelectedAgents.clear();
        listOfSelectedAgents.addAll(selectedGroups);
        listOfSelectedAgents.addAll(selectedPersons);
        listOfSelectedItems.clear();
        listOfSelectedItems.addAll(selectedResources);
        listOfSelectedItems.addAll(selectedDataboxes);
        listOfSelectedItems.addAll(selectedLiveposts);
        share.setEnabled(isSharingPossible());
        updateSelectedProfile(selectedProfile);
        addWidgets(recieverContainer,(List<DisplayableItem>) (Object) listOfSelectedAgents, labelReceivers, noReceivers);
        addWidgets(dataContainer,(List<DisplayableItem>) (Object) listOfSelectedItems, labelItems, noItems);
        if(isSharingPossible()) {
	        //generate recommendation in the background
        	labelWarnings.setText(String.valueOf(0));
        	noWarnings.setVisibility(View.VISIBLE);
            noWarnings.setText("Loading...");
	        AndroidModelHelper.loadAdvisoryPropertiesAsynchronously(this, new AdvisoryRequestItem(selectedProfile.getGuid(), AndroidModelHelper.getListOfGuidsOfGenItemList((List<GenItem>) (Object) listOfSelectedAgents), AndroidModelHelper.getListOfGuidsOfGenItemList(listOfSelectedItems)), advisoryItemsNotValidAgentsForSharing, noWarnings, labelWarnings, warningsContainer);
        } else {
        	noWarnings.setVisibility(View.GONE);
        	warningsContainer.addView(UIHelper.createWarningWidget(this, UIHelper.getAdvisoryProperties(this, new AdvisoryItem(1.0d, AdvisoryItem.WARNING_TYPES[6], new WarningSharingNotPossible()))));
        	labelWarnings.setText(String.valueOf(advisoryItemsNotValidAgentsForSharing.size() + 1));
        	for (AdvisoryProperties advisory : advisoryItemsNotValidAgentsForSharing) {
				warningsContainer.addView(UIHelper.createWarningWidget(this, advisory));
			}
        }
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void onClick(View v) {
    	try {
    		switch (v.getId()) {
            case R.share.button_sel_person:
            	UIHelper.createStandardDialog(Activity_Share_Dialog.this, mrContext, new BaseAdapter_Standard(), (List<DisplayableItem>)(Object) AndroidModelHelper.getItemsForSelection(allPersonsValidForSharing, selectedPersons), ResultObject.RESULT_OBJECT_TYPES.SHARING_PERSONS);
                break;

            case R.share.button_sel_group:
            	UIHelper.createStandardDialog(this, mrContext, new BaseAdapter_Standard(), (List<DisplayableItem>)(Object) AndroidModelHelper.getItemsForSelection(allGroups, selectedGroups), ResultObject.RESULT_OBJECT_TYPES.SHARING_GROUPS);
                break;

            case R.share.button_sel_resource:
            	UIHelper.createStandardDialog(Activity_Share_Dialog.this, mrContext, new BaseAdapter_Standard(), (List<DisplayableItem>)(Object) AndroidModelHelper.getItemsForSelection(allResources, selectedResources), ResultObject.RESULT_OBJECT_TYPES.SHARING_RESOURCES);
                break;
                
            case R.share.button_sel_databox:
            	UIHelper.createStandardDialog(Activity_Share_Dialog.this, mrContext, new BaseAdapter_Standard(), (List<DisplayableItem>)(Object) AndroidModelHelper.getItemsForSelection(allDataboxes, selectedDataboxes), ResultObject.RESULT_OBJECT_TYPES.SHARING_DATABOXES);
                break;
                
            case R.share.button_sel_message:
            	UIHelper.createStandardDialog(this, mrContext, new BaseAdapter_Standard(), (List<DisplayableItem>)(Object)  AndroidModelHelper.getItemsForSelection(allLiveposts, selectedLiveposts), ResultObject.RESULT_OBJECT_TYPES.SHARING_LIVEPOSTS);
                break;

            case R.share.container_profile:
            	UIHelper.createStandardDialog(Activity_Share_Dialog.this, mrContext, new BaseAdapter_Dialog_Sharing_Profile(), (List<DisplayableItem>) (Object) allProfilesValidForSharing, ResultObject.RESULT_OBJECT_TYPES.SHARING_PROFILE);
                break;

            case R.share.button_share:
                shareSelectedItems();
                break;
                
            case R.share.button_cancel:
            	List<GenItem> items = new ArrayList<GenItem>();
            	items.addAll(listOfSelectedItems);
            	items.addAll((List<GenItem>) (Object) listOfSelectedAgents);
            	items.add(selectedProfile);
            	AndroidModelHelper.sendEvaluationDataAsynchronously(items, mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_canceled));
            	finish();
            	break;
            }
		} catch (Exception e) {
			Toast.makeText(this, "Could not initialize share dialog. Please reload manually.", Toast.LENGTH_LONG).show();
		}
    }
    
    private void checkValidityOfPersons(List<PersonItem> persons, String parentGuid) {
    	List<String> agentsNotValidForSharing = new Vector<String>();
    	WarningAgentNotValidForSharing warning = new WarningAgentNotValidForSharing();
    	Iterator<PersonItem> iter = persons.iterator();
        while (iter.hasNext()){
            PersonItem personItem = iter.next();
            if(!ModelHelper.isPersonValidForSharing(personItem)) {
            	agentsNotValidForSharing.add(personItem.getGuid());
				iter.remove();
			}
        }
    	if(agentsNotValidForSharing.size() > 0) {
	    	warning.setAgentsNotValidForSharing(agentsNotValidForSharing);
	    	if(parentGuid.length() > 0) warning.setParentGroup(parentGuid);
	    	advisoryItemsNotValidAgentsForSharing.add(UIHelper.getAdvisoryProperties(this, new AdvisoryItem(0.0d, AdvisoryItem.WARNING_TYPES[5], warning)));
    	}
    }
    
    @SuppressWarnings("unchecked")
	private void checkValidityOfChildrenOfGroups(List<GroupItem> groups) {
    	for (GroupItem groupItem : groups) {
			checkValidityOfPersons((List<PersonItem>)(Object)ModelHelper.getChildrenOfDisplayableItem(mrContext, groupItem), groupItem.getGuid());
		}
    }
    
    private boolean isSharingPossible() {
    	return (listOfSelectedAgents != null && listOfSelectedItems != null && selectedProfile != null && listOfSelectedAgents.size() > 0 && listOfSelectedItems.size() > 0);
    }
    
	private void shareSelectedItems() {
        if(!isSharingPossible()) {
            String message = "Please select ";
            if(listOfSelectedAgents.size() == 0) message += "an agent, ";
            if(listOfSelectedItems.size() == 0) message += "an item, ";
            if(selectedProfile == null) message +="a profile";
            message += "!";
        	AlertDialog infoAlert = UIHelper.createInfoDialog(this, message, "ok");
            infoAlert.show();
        } 
//        else if(warningsContainer.getChildCount() > 0) {
//        	Builder builder = UIHelper.createAlertDialogBuilder(this, "Confirmation needed", true);
//            builder.setMessage("Do you really want to ignore the warning message and share the selected resources?");
//            builder.setPositiveButton("share", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                	AndroidModelHelper.shareItemsAsynchronously(Activity_Share_Dialog.this, mrContext, listOfSelectedAgents, listOfSelectedItems, selectedProfile);
//                }
//            });
//            UIHelper.displayAlertDialog(builder, false);
//		} 
        else {
        	AndroidModelHelper.shareItemsAsynchronously(Activity_Share_Dialog.this, mrContext, listOfSelectedAgents, listOfSelectedItems, selectedProfile);
        }
	}
    
    @SuppressWarnings("unchecked")
	private void addWidgets(LinearLayout ll, List<DisplayableItem> items, TextView labelCount, TextView noItemsText){
    	labelCount.setText(String.valueOf(items.size()));
    	if(items.size() > 0){
    		noItemsText.setVisibility(View.GONE);
	    	for(DisplayableItem item : items){
	    		if(item.getMType().equals(TYPES.GROUP)) {
	    			ll.addView(UIHelper.createSharingWidget(this, item, (List<DisplayableItem>) (Object) selectedGroups));
		    	} else if(item.getMType().equals(TYPES.RESOURCE)){
		    		ll.addView(UIHelper.createSharingWidget(this, item, (List<DisplayableItem>) (Object) selectedResources));
		    	} else if(item.getMType().equals(TYPES.DATABOX)) {
		    		ll.addView(UIHelper.createSharingWidget(this, item,(List<DisplayableItem>) (Object) selectedDataboxes));
		    	} else if(item.getMType().equals(TYPES.PERSON)) {
		    		ll.addView(UIHelper.createSharingWidget(this, item, (List<DisplayableItem>) (Object) selectedPersons));
		    	} else if(item.getMType().equals(TYPES.LIVEPOST)) {
		    		ll.addView(UIHelper.createSharingWidget(this, item, (List<DisplayableItem>) (Object) selectedLiveposts));
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
	    	ImageHelper.loadImageAsynchronously(image, item, Activity_Share_Dialog.this);
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
			case SHARING_DATABOXES:
				selectedDataboxes.addAll((List<DataboxItem>)(Object)((ResultObjectDisplayable)result).getDisplayables());
				break;
			case SHARING_RESOURCES:
				selectedResources.addAll((List<ResourceItem>)(Object)((ResultObjectDisplayable)result).getDisplayables());
				break;
			case SHARING_LIVEPOSTS:
				selectedLiveposts.addAll((List<LivePostItem>)(Object)((ResultObjectDisplayable)result).getDisplayables());
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
		return LoadingViewHandlerFactory.<Activity_Share_Dialog>createLVH(Activity_Share_Dialog.this);
	}
	
}
