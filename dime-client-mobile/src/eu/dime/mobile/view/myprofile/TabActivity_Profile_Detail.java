package eu.dime.mobile.view.myprofile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.TabActivityDisplayableItemDetail;
import eu.dime.mobile.view.dialog.Activity_Edit_Item_Dialog;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.ProfileAttributeCategoriesEntry;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileAttributeItem.VALUE_CATEGORIES;
import eu.dime.model.displayable.ProfileItem;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TabActivity_Profile_Detail extends TabActivityDisplayableItemDetail {
	
	private boolean isEditable = false;
	boolean isShareable = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Profile_Detail.class.getSimpleName();
	}
	
	@Override
	protected void initializeTabs() {
		if(di.getMType().equals(TYPES.PROFILE)) {
    		isEditable = ((ProfileItem) di).isEditable() && di.getUserId().equals(Model.ME_OWNER);
    		isShareable = ((ProfileItem) di).supportsSharing() && di.getUserId().equals(Model.ME_OWNER);
    	}
    	tabs.add(new DimeTabObject(getResources().getString(R.string.tab_profileDetail) + di.getName(), ListActivity_Profile_Detail.class, dio));
    	TabActivity_Profile_Detail.this.init(true, false, isShareable, isEditable);
	}
	
	@Override
	protected void initializeHeader() {
		ViewGroup headerContainer = (ViewGroup) findViewById(R.tabframe.header);
		LinearLayout header;
		if(headerContainer.getChildCount() > 0) {
			header = (LinearLayout) headerContainer.getChildAt(0);
		} else {
			header = (LinearLayout) getLayoutInflater().inflate(R.layout.header_profile_detail, headerContainer);
		}
		if(di.getUserId().equals(Model.ME_OWNER)) header.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TabActivity_Profile_Detail.this, Activity_Edit_Item_Dialog.class);
				startActivity(DimeIntentObjectHelper.populateIntent(intent, new DimeIntentObject(di)));
			}
		});
		ImageView image = (ImageView) header.findViewById(R.id.imageView);
		LinearLayout readOnly = (LinearLayout) header.findViewById(R.profile.read_only);
		if(isEditable) readOnly.setVisibility(View.GONE);
		TextView name = (TextView) header.findViewById(R.id.textView_name);
		name.setText(di.getName());
		TextView standard = (TextView) header.findViewById(R.profile.standard);
//		if(!profile.isStandard()) { TODO implement standard profile
			standard.setVisibility(View.GONE);
//		}
		ImageHelper.loadImageAsynchronously(image, di, this);
	}

	@SuppressWarnings("unchecked")
    @SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
	@Override
    public void onClick(View view) {
        super.onClick(view);
        if (view instanceof Button) {
            Button button = (Button) view;
            Resources res = getResources();
            if (currentActivity instanceof ListActivity_Profile_Detail) {
            	//delete selected attribute category
                if (button.getText().equals(res.getString(R.string.action_deleteSelectedAttributeCategories))) {
                	final String actionName = res.getResourceEntryName(R.string.action_deleteSelectedAttributeCategories);
                	actionDialog.dismiss();
    				AlertDialog.Builder builder = UIHelper.createAlertDialogBuilder(this, "Confirm request", true);
    				builder.setMessage("Really delete selected attribute categories?");
    				builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
    					@Override
    					public void onClick(DialogInterface dialog, int which) {
    						AndroidModelHelper.deleteGenItemsAsynchronously(currentActivity, TYPES.PROFILEATTRIBUTE, mrContext, actionName);
    					}
    				});
    				UIHelper.displayAlertDialog(builder, false);
                }
                //add new Attribute
                else if (button.getText().equals(res.getString(R.string.action_addNewAttribute))) {
                	final String actionName = res.getResourceEntryName(R.string.action_addNewAttribute);
                	actionDialog.dismiss();
                	ArrayList<String> tmpItemsAL = new ArrayList<String>();
            		for (ProfileAttributeCategoriesEntry entry : ProfileAttributeItem.ValueCategoriesMap.values() ){
            			tmpItemsAL.add(entry.caption);
            		}
            		String[] tmpItems = new String[tmpItemsAL.size()];
            		tmpItems = tmpItemsAL.toArray(tmpItems);
            		final String[] items = tmpItems;
            		Builder builder = UIHelper.createAlertDialogBuilder(this, "Select category", true);
            		builder.setItems(items, new DialogInterface.OnClickListener() {
            		    public void onClick(DialogInterface dialog, int item) {
            		    	VALUE_CATEGORIES category = null;
            				for (ProfileAttributeCategoriesEntry entry : ProfileAttributeItem.ValueCategoriesMap.values() ){
            					if (entry.caption.equals(items[item])){
            						category=entry.category;
            					}
            				}
            				final ProfileAttributeItem pai = ItemFactory.cretateNewProfileAttributeItem(category);
            				ScrollView scrollView = new ScrollView(TabActivity_Profile_Detail.this);
            				LinearLayout ll = new LinearLayout(TabActivity_Profile_Detail.this);
            				LinearLayout.LayoutParams lpms = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
            				ll.setLayoutParams(lpms);
            				ll.setOrientation(LinearLayout.VERTICAL);
            				ll.setPadding(10, 0, 10, 0);
            				final LinkedHashMap<String, EditText> entriesStore = new LinkedHashMap<String, EditText>();
            				for (Map.Entry<String, String> entry : pai.getValue().entrySet()){
            					if(!pai.getCategory().equals(entry.getKey())){
            						TextView label = UIHelper.createTextView(TabActivity_Profile_Detail.this, R.style.dime_labelTV, -1, 11, null, false);
            						label.setText(pai.getLabelForValueKey(entry.getKey()));
            						ll.addView(label);
            					}
            					LinearLayout.LayoutParams lpms2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            					EditText value = new EditText(TabActivity_Profile_Detail.this);
            					value.setLayoutParams(lpms2);
            					value.setInputType(UIHelper.switchInputType(entry.getKey()));
            					entriesStore.put(entry.getKey(), value);
            					ll.addView(value);
            				}
            				scrollView.addView(ll);
            				AlertDialog.Builder builder = UIHelper.createCustomAlertDialogBuilder(TabActivity_Profile_Detail.this, pai.getCategory(), true, scrollView);
            				builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            					@Override
            					public void onClick(DialogInterface dialog, int which) {
            						for (Map.Entry<String, EditText> entry : entriesStore.entrySet()){
            							if (entry.getKey().toLowerCase().contains("date")) {
            								List<String> formats = Arrays.asList("dd.MM.yyyy", "yyyy/MM/dd");
            								String text = "";
            								for (String string : formats) {
            									SimpleDateFormat sdfToDate = new SimpleDateFormat(string);
                								try {
                									sdfToDate.parse(entry.getValue().getText().toString());
                									text = entry.getValue().getText().toString();
                									break;
                								} catch (ParseException e) { 
                									Log.d(TAG, "Could´t parse date!");
                								}
											}
            								pai.getValue().put(entry.getKey(), String.valueOf(text));
            							} else {
            								pai.getValue().put(entry.getKey(), entry.getValue().getText().toString());
            							}
            						}
            						AndroidModelHelper.createAndAssignProfileAttributesAsynchronously((ProfileItem) di, pai, dialog, currentActivity, mrContext, actionName);
            					}
            								
            				});
            				UIHelper.displayAlertDialog(builder, true);
            		    }
            		});
            		UIHelper.displayAlertDialog(builder, false);
                }
                //add existing attribute
                else if (button.getText().equals(res.getString(R.string.action_addExistingAttribute))) {
                	final String actionName = res.getResourceEntryName(R.string.action_addExistingAttribute);
                	actionDialog.dismiss();
                	ArrayList<String> tmpItemsAL = new ArrayList<String>();
                	final List<ProfileAttributeItem> attributes = (List<ProfileAttributeItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.PROFILEATTRIBUTE);
                	for (ProfileAttributeItem profileAttributeItem : attributes) {
                		String caption = profileAttributeItem.getValue().values().toString();
                		caption = caption.replace("[", "(");
                		caption = caption.replace("]", ")");
                		caption = caption.replace(" , ", "");
                		if(caption.length()>20){
                			caption = caption.substring(0, 17);
                			caption = caption.concat("...");
                		}
						tmpItemsAL.add(profileAttributeItem.getName() + caption);
					};
					String[] tmpItems = new String[tmpItemsAL.size()];
					tmpItems = tmpItemsAL.toArray(tmpItems);
					final String[] items = tmpItems;
                	Builder builder = UIHelper.createAlertDialogBuilder(this, "Select attribute category", true);
            		builder.setItems(items, new DialogInterface.OnClickListener() {
            		    public void onClick(DialogInterface dialog, final int item) {  
            				di.addItem(attributes.get(item).getGuid());
            				AndroidModelHelper.updateGenItemAsynchronously(di, dialog, currentActivity, mrContext, actionName);
            		    }
            		});
            		UIHelper.displayAlertDialog(builder, false);
                }
            }
        }
    }

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Profile_Detail>createLVH(TabActivity_Profile_Detail.this);
	}

	@Override
	protected List<String> getActionsForDetailView() {
		List<String> actions = new ArrayList<String>();
		actions.add(getString(R.string.action_addExistingAttribute));
		actions.add(getString(R.string.action_addNewAttribute));
		actions.add(getString(R.string.action_deleteSelectedAttributeCategories));
		return actions;
	}
	
}