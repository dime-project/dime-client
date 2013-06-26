package eu.dime.mobile.view.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.interfaces.ResultsOfStandardDialogInterface;
import eu.dime.mobile.helper.objects.ResultObject;
import eu.dime.mobile.helper.objects.ResultObjectDisplayable;
import eu.dime.mobile.helper.objects.ResultObjectLiveposts;
import eu.dime.mobile.helper.objects.ResultObjectProfileSharing;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.mobile.view.adapter.BaseAdapter_Dialog_Sharing_Profile;
import eu.dime.mobile.view.adapter.BaseAdapter_Livepost;
import eu.dime.mobile.view.adapter.BaseAdapter_Standard;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.advisory.AdvisoryItem;
import eu.dime.model.specialitem.advisory.AdvisoryRequestItem;
import eu.dime.model.ComparatorHelper;
import eu.dime.model.GenItem;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.AgentItem;
import eu.dime.model.displayable.DataboxItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.displayable.ResourceItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Activity_Share_Dialog extends ActivityDime implements OnClickListener, ResultsOfStandardDialogInterface {

	private ImageView image;
	private TextView fullName;
	private TextView profileName;
	private TextView attributeKey;
	private TextView attributeValue;
	private TextView labelItems;
	private TextView labelWarnings;
	private TextView labelReceivers;
	private Button share;
	private ImageView warningsExpander;
    private LinearLayout dataContainer;
    private LinearLayout recieverContainer;
    private LinearLayout warningsContainer;
    private LinearLayout profileContainer;
    private List<GroupItem> allGroups;
    private List<PersonItem> allPersons;
    private List<DataboxItem> allDataboxes;
    private List<ResourceItem> allResources;
    private List<LivePostItem> allLiveposts;
    private List<ProfileItem> allProfiles;
    private List<AgentItem> listOfSelectedAgents = new ArrayList<AgentItem>();
    private List<GenItem> listOfSelectedItems = new ArrayList<GenItem>();
    private List<GroupItem> selectedGroups = new ArrayList<GroupItem>();
    private List<PersonItem> selectedPersons = new ArrayList<PersonItem>();
    private List<DataboxItem> selectedDataboxes = new ArrayList<DataboxItem>();
    private List<ResourceItem> selectedResources = new ArrayList<ResourceItem>();
    private List<LivePostItem> selectedLiveposts = new ArrayList<LivePostItem>();
    private ProfileItem selectedProfile;
    private boolean isShareWarning = false;

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
    	attributeKey = (TextView) findViewById(R.share.text_attribute_key);
    	attributeValue = (TextView) findViewById(R.share.text_attribute_value);
    	profileContainer = (LinearLayout) findViewById(R.share.container_profile);
        dataContainer = (LinearLayout) findViewById(R.share.container_data);
        warningsContainer = (LinearLayout) findViewById(R.share.container_warnings);
        recieverContainer = (LinearLayout) findViewById(R.share.container_reciever);
        labelWarnings = (TextView) findViewById(R.share.label_warnings);
        labelReceivers = (TextView) findViewById(R.share.label_receivers);
        labelItems = (TextView) findViewById(R.share.label_items);
        Button selectPerson = (Button) findViewById(R.share.button_sel_person);
        Button selectGroup = (Button) findViewById(R.share.button_sel_group);
        Button selectResource = (Button) findViewById(R.share.button_sel_resource);
        Button selectDatabox = (Button) findViewById(R.share.button_sel_databox);
        Button selectLivepost = (Button) findViewById(R.share.button_sel_message);
        share = (Button) findViewById(R.share.button_share);
        Button cancel = (Button) findViewById(R.share.button_cancel);
        warningsExpander = (ImageView) findViewById(R.share.button_expander_warnings);
        warningsExpander.setOnClickListener(this);
        selectPerson.setOnClickListener(this);
        selectGroup.setOnClickListener(this);
        selectResource.setOnClickListener(this);
        selectDatabox.setOnClickListener(this);
        selectLivepost.setOnClickListener(this);
        profileContainer.setOnClickListener(this);
        share.setOnClickListener(this);
        cancel.setOnClickListener(this);
        // close touchkeyboard since it's not needed
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        share.setEnabled(false);
        DimeClient.addStringToViewStack(TAG.substring(9)); //remove Activity_
        startTask("");
    }
    
    @Override
    protected void loadData() {
        allPersons = ModelHelper.getAllPersons(mrContext);
        allResources = ModelHelper.getAllResources(mrContext);
        allLiveposts = ModelHelper.getAllLivePosts(mrContext);
        allDataboxes = ModelHelper.getAllDataBoxes(mrContext);
        allGroups = ModelHelper.getAllGroups(mrContext);
        allProfiles = ModelHelper.getAllValidProfilesForSharing(mrContext);
        selectedPersons = AndroidModelHelper.getListOfItemsWithGuids(allPersons, getIntent().getStringArrayListExtra(TYPES.PERSON.toString()));
        selectedResources = AndroidModelHelper.getListOfItemsWithGuids(allResources, getIntent().getStringArrayListExtra(TYPES.RESOURCE.toString()));
		selectedLiveposts = AndroidModelHelper.getListOfItemsWithGuids(allLiveposts, getIntent().getStringArrayListExtra(TYPES.LIVEPOST.toString()));
        selectedGroups = AndroidModelHelper.getListOfItemsWithGuids(allGroups, getIntent().getStringArrayListExtra(TYPES.GROUP.toString()));
        selectedDataboxes = AndroidModelHelper.getListOfItemsWithGuids(allDataboxes, getIntent().getStringArrayListExtra(TYPES.DATABOX.toString()));
        listOfSelectedAgents.addAll(selectedGroups);
        listOfSelectedAgents.addAll(selectedPersons);
        selectedProfile = ModelHelper.getDefaultProfileForSharing(mrContext, listOfSelectedAgents);
    }

    @Override
    protected void initializeData() {
        updateViewOnSelectionChanged();
    }

    @SuppressWarnings("unchecked")
	public void updateViewOnSelectionChanged() {
    	isShareWarning = false;
        //update text fields
        recieverContainer.removeAllViews();
        dataContainer.removeAllViews();
        warningsContainer.removeAllViews();
        listOfSelectedAgents.clear();
        listOfSelectedAgents.addAll(selectedGroups);
        listOfSelectedAgents.addAll(selectedPersons);
        listOfSelectedItems.clear();
        listOfSelectedItems.addAll(selectedResources);
        listOfSelectedItems.addAll(selectedDataboxes);
        listOfSelectedItems.addAll(selectedLiveposts);
        share.setEnabled(isSharingPossible());
        updateSelectedProfile(selectedProfile);
        addWidgets(recieverContainer,(List<DisplayableItem>) (Object) listOfSelectedAgents);
        addWidgets(dataContainer,(List<DisplayableItem>) (Object) listOfSelectedItems);

        if(isSharingPossible()) {
	        //generate recommendation in the background
	        (new AsyncTask<Object, Object, List<AdvisoryItem>>() {
	
	            @SuppressWarnings({ "rawtypes" })
				@Override
	            protected List<AdvisoryItem> doInBackground(Object... paramss) {   
	                try {
	                	List<String> agents = new ArrayList();
	                	List<String> shareables = new ArrayList();
	                	for (GenItem item : listOfSelectedItems) {
	                		shareables.add(item.getGuid());
						}
	                    
	                    for (AgentItem item : listOfSelectedAgents) {
	               		 	agents.add(item.getGuid());
						}
	                    AdvisoryRequestItem ari = new AdvisoryRequestItem(selectedProfile.getGuid(), agents, shareables);
	                    List<AdvisoryItem> advisories = ModelHelper.getSharingAdvisories(mrContext, ari);
	                    Collections.sort(advisories, new ComparatorHelper.WarningLevelComparator());
	                    return advisories;
	                } catch (Exception ex) {
	                    Logger.getLogger(Activity_Share_Dialog.class.getName()).log(Level.SEVERE, null, ex);
	                } 
	                return null;
	            }
	
	            @Override
	            protected void onPostExecute(List<AdvisoryItem> result) {
	                super.onPostExecute(result);
	                if (result != null && result.size() > 0 ) {
	                	isShareWarning = true;
	                	labelWarnings.setText(getString(R.string.sharing_label_warnings) + " " + result.size());
	                	for (AdvisoryItem advisoryItem : result) {
	                		warningsContainer.addView(UIHelper.createWarningWidget(Activity_Share_Dialog.this, advisoryItem));
						}
	                } else {
	                	labelWarnings.setText(getString(R.string.sharing_label_warnings));
	                	LinearLayout ll = new LinearLayout(Activity_Share_Dialog.this);
	                	ImageView image = new ImageView(Activity_Share_Dialog.this);
	                	image.setBackgroundResource(UIHelper.getImageIdForWarning(0.0));
	                	TextView text = new TextView(Activity_Share_Dialog.this);
	                	text.setText(getResources().getString(R.string.sharing_no_warnings));
	                	text.setTextColor(getResources().getColor(android.R.color.darker_gray));
	                	warningsContainer.addView(ll);
	                }
	            }
	            
	        }).execute(new Object());
        } else {
        	TextView text = new TextView(this);
        	text.setText(getResources().getString(R.string.sharing_warning_not_possible));
        	text.setTextColor(getResources().getColor(R.color.dm_col3));
        	warningsContainer.addView(text);
        	labelWarnings.setText(getString(R.string.sharing_label_warnings));
        }

    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.share.button_sel_person:
            	UIHelper.createStandardDialog(Activity_Share_Dialog.this, mrContext, new BaseAdapter_Standard(), (List<DisplayableItem>)(Object) AndroidModelHelper.getItemsForSelection(allPersons, selectedPersons), ResultObject.RESULT_OBJECT_TYPES.SHARING_PERSONS);
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
            	UIHelper.createStandardDialog(this, mrContext, new BaseAdapter_Livepost(), (List<DisplayableItem>)(Object)  AndroidModelHelper.getItemsForSelection(allLiveposts, selectedLiveposts), ResultObject.RESULT_OBJECT_TYPES.SHARING_LIVEPOSTS);
                break;

            case R.share.container_profile:
            	UIHelper.createStandardDialog(Activity_Share_Dialog.this, mrContext, new BaseAdapter_Dialog_Sharing_Profile(), (List<DisplayableItem>) (Object) allProfiles, ResultObject.RESULT_OBJECT_TYPES.SHARING_PROFILE);
                break;
                
            case R.share.button_expander_warnings:
            	if(warningsContainer.getVisibility() == View.VISIBLE) {
            		warningsContainer.setVisibility(View.GONE);
            		warningsExpander.setImageResource(R.drawable.button_expand_bar);
            	} else {
            		warningsContainer.setVisibility(View.VISIBLE);
            		warningsExpander.setImageResource(R.drawable.button_collapse_bar);
            	}
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
    }
    private boolean isSharingPossible() {
    	return (listOfSelectedAgents != null && listOfSelectedItems != null && selectedProfile != null && listOfSelectedAgents.size() > 0 && listOfSelectedItems.size() > 0);
    }
    
	private void shareSelectedItems() {
        if (!isSharingPossible()) {
            String message = "Please select ";
            if(listOfSelectedAgents.size() == 0) message += "an agent, ";
            if(listOfSelectedItems.size() == 0) message += "an item, ";
            if(selectedProfile == null) message +="a profile";
            message += "!";
        	AlertDialog infoAlert = UIHelper.createInfoDialog(this, message, "ok");
            infoAlert.show();
        } 
        else if (isShareWarning) {
        	Builder builder = UIHelper.createAlertDialogBuilder(this, "Confirmation needed", true);
            builder.setMessage("Do you really want to ignore the warning message and share the selected resources?");
            builder.setPositiveButton("share", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                	AndroidModelHelper.shareItemsAsynchronously(Activity_Share_Dialog.this, mrContext, listOfSelectedAgents, listOfSelectedItems, selectedProfile);
                }
            });
            UIHelper.displayAlertDialog(builder, false);
		} 
        else {
        	AndroidModelHelper.shareItemsAsynchronously(Activity_Share_Dialog.this, mrContext, listOfSelectedAgents, listOfSelectedItems, selectedProfile);
        }
	}
    
    
    
    @SuppressWarnings("unchecked")
	private void addWidgets(LinearLayout ll, List<DisplayableItem> items){
    	if(items.size()>0){
    		if(ll.getId() == R.share.container_data){
    			labelItems.setText(getString(R.string.sharing_label_items) + " " + items.size());
    		} else {
    			labelReceivers.setText(getString(R.string.sharing_label_receivers) + " " + items.size());
    		}
	    	for(DisplayableItem item : items){
	    		if(item instanceof GroupItem) {
	    			ll.addView(UIHelper.createSharingWidget(this,item, (List<DisplayableItem>) (Object) selectedGroups));
		    	} else if(item instanceof ResourceItem){
		    		ll.addView(UIHelper.createSharingWidget(this,item, (List<DisplayableItem>) (Object) selectedResources));
		    	} else if(item instanceof DataboxItem) {
		    		ll.addView(UIHelper.createSharingWidget(this,item,(List<DisplayableItem>) (Object) selectedDataboxes));
		    	} else if(item instanceof PersonItem) {
		    		ll.addView(UIHelper.createSharingWidget(this,item, (List<DisplayableItem>) (Object) selectedPersons));
		    	} else if(item instanceof LivePostItem) {
		    		ll.addView(UIHelper.createSharingWidget(this,item, (List<DisplayableItem>) (Object) selectedLiveposts));
		    	}
	        }
    	} else {
    		TextView tv = new TextView(this);
    		String text = "";
    		if(ll.getId() == R.share.container_data){
    			text = getString(R.string.sharing_no_items);
    			labelItems.setText(getString(R.string.sharing_label_items));
    		} else {
    			text = getString(R.string.sharing_no_receiver);
    			labelReceivers.setText(getString(R.string.sharing_label_receivers));
    		}
    		tv.setText(text);
    		tv.setTextColor(getResources().getColor(R.color.dm_col3));
    		ll.addView(tv);
    	}
    }
    
    private void updateSelectedProfile(ProfileItem item) {
    	fullName.setText("");
    	profileName.setText("");
    	attributeKey.setText("");
		attributeValue.setText("");
    	if(item != null) {
	    	ImageHelper.loadImageAsynchronously(image, item, Activity_Share_Dialog.this);
	    	List<ProfileAttributeItem> attributes = ModelHelper.getProfileAttributesOfProfile(mrContext, item);
	    	for (ProfileAttributeItem profileAttributeItem : attributes) {
	    		for (String key : profileAttributeItem.getValue().keySet()) {
					String value = profileAttributeItem.getValue().get(key);
					if(value.length() > 0) {
						if(key.equals("fullname")){
							fullName.setText(value);
						} else {
			    			attributeKey.setText(key);
			    			attributeValue.setText(value);
						}
					}
	    		}
			}
	    	profileName.setText(item.getName());
    	} else {
    		fullName.setText("&lt;no profile selected&gt;");
    		profileName.setText("...");
    	}
    }

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		//TODO save activity state
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Share_Dialog>createLVH(Activity_Share_Dialog.this);
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
			default:
				break;
			}
		} else if (result instanceof ResultObjectLiveposts) {
			selectedLiveposts.addAll(((ResultObjectLiveposts)result).getLiveposts());
		}
		updateViewOnSelectionChanged();
	}
	
}
