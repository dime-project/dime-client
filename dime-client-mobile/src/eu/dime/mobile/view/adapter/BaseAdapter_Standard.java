package eu.dime.mobile.view.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.listener.CheckListener;
import eu.dime.mobile.helper.listener.ExpandClickListener;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.AgentItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.model.displayable.ShareableItem;

public class BaseAdapter_Standard extends BaseAdapterDisplayableItem {

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DisplayableItem di = mItems.get(position);
		// Keeps reference to avoid future findViewById()
		DimeViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new DimeViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_standard_item, null);
			viewHolder.name = (TextView) convertView.findViewById(R.adapter.name);
			viewHolder.image = (ImageView) convertView.findViewById(R.adapter.image);
			viewHolder.attribute1 = (TextView) convertView.findViewById(R.adapter.attribute1);
			viewHolder.attribute2 = (TextView) convertView.findViewById(R.adapter.attribute2);
			viewHolder.selectedCB = (CheckBox) convertView.findViewById(R.adapter.checkBox);
			viewHolder.expander = (ImageButton) convertView.findViewById(R.id.buttonExp);
			viewHolder.validForSharingIcon = (ImageView) convertView.findViewById(R.id.is_valid_for_sharing);
			viewHolder.expandedArea = (LinearLayout) convertView.findViewById(R.id.expandedArea);
			viewHolder.sharedArea = (LinearLayout) convertView.findViewById(R.adapter.sharedArea);
			viewHolder.previewArea = (LinearLayout) convertView.findViewById(R.adapter.previewArea);
			viewHolder.previewContainer = (LinearLayout) convertView.findViewById(R.adapter.previewContainer);
			viewHolder.previewNoItems = (TextView) convertView.findViewById(R.adapter.preview_noitems);
			viewHolder.infoContainer = (LinearLayout) convertView.findViewById(R.adapter.info_container);
			viewHolder.sharedContainer = (LinearLayout) convertView.findViewById(R.adapter.shared_container);
			viewHolder.sharedNoItems = (TextView) convertView.findViewById(R.adapter.shared_noitems);
	    	convertView.setTag(viewHolder);
		} else {
			viewHolder = (DimeViewHolder) convertView.getTag();
			viewHolder.resetAllViews();
		}
    	//initialize values of all DisplayableItems
		viewHolder.name.setText((ModelHelper.isParentable(di)) ? di.getName() + " ("+di.getItems().size()+")": di.getName());
		ImageHelper.loadImageAsynchronously(viewHolder.image, di, context);
		boolean isValidForSharing = false;
		//initialize values of GroupItem
		switch (di.getMType()) {
		case GROUP:
			isValidForSharing = true;
			GroupItem group = (GroupItem) di;
			viewHolder.attribute1.setText(di.getType());
			if (group.getGroupType().equals(GroupItem.VALID_GROUP_TYPE_VALUES[0])){
				convertView.setBackgroundColor(context.getResources().getColor(R.color.background_yellow));
			} else if (group.getGroupType().equals(GroupItem.VALID_GROUP_TYPE_VALUES[1])) {
				convertView.setBackgroundColor(context.getResources().getColor(R.color.background_green));
			} else {
				convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
			}
			break;
		case PERSON:
			PersonItem person = (PersonItem) di;
			String defaultProfile = person.getDefaultProfileGuid();
			isValidForSharing = (defaultProfile != null && defaultProfile.length() > 0) ? true : false;
//			if(isValidForSharing) {
				AndroidModelHelper.loadProfilesOfPersonAsynchronously(context, person, viewHolder.attribute1);
//			} else {
//				viewHolder.attribute1.setVisibility(View.GONE);
//			}
			AndroidModelHelper.loadGroupsOfChildAsynchronously(context, mrContext.owner, di, viewHolder.attribute2);
			break;
		case RESOURCE:
			ResourceItem resource = (ResourceItem) di;
			isValidForSharing = di.getUserId().equals(Model.ME_OWNER) ? true : false;
			if (resource.getMimeType() != null && resource.getMimeType().length() > 0) {
				viewHolder.attribute1.setText(resource.getMimeType());
	        }
	        if (resource.getFileSize() != null) {
	        	viewHolder.attribute2.setText(resource.getFileSize() + " Byte");
	        	viewHolder.attribute2.setVisibility(View.VISIBLE);
	        }
			break;
		case DATABOX:
			isValidForSharing = di.getUserId().equals(Model.ME_OWNER) ? true : false;
			break;
		case LIVEPOST:
			LivePostItem livepost = (LivePostItem) di;
			viewHolder.attribute1.setText(UIHelper.formatDateByMillis(livepost.getCreated()));
        	viewHolder.attribute2.setText(livepost.getText());
        	viewHolder.attribute2.setVisibility(View.VISIBLE);
			isValidForSharing = di.getUserId().equals(Model.ME_OWNER) ? true : false;
			if ((!livepost.getUserId().equals(Model.ME_OWNER))) {
	        	PersonItem pi = (PersonItem) AndroidModelHelper.getGenItemSynchronously(context, new DimeIntentObject(livepost.getUserId(), TYPES.PERSON));
	        	if(pi != null){
	        		viewHolder.name.setText(pi.getName() + ": " + livepost.getName());
	        	}
	        }
			break;
		default:
			viewHolder.attribute1.setText(di.getType());
			break;
		}
		viewHolder.validForSharingIcon.setVisibility((!isValidForSharing) ? View.VISIBLE : View.GONE);
		if (expandedListItemId == position) {
			viewHolder.expandedArea.setVisibility(View.VISIBLE);
			viewHolder.expander.setBackgroundResource(R.drawable.button_collapse);
    		UIHelper.updateInfoContainer((Activity)context, viewHolder.infoContainer, di);
    		if(ModelHelper.isParentable(di)){
    			viewHolder.previewArea.setVisibility(View.VISIBLE);
    			AndroidModelHelper.loadChildrenOfDisplayableItemAsynchronously(context, mrContext.owner, viewHolder.previewContainer, di, viewHolder.previewNoItems);
    		} else if(di.getMType().equals(TYPES.LIVEPOST)) {
    			viewHolder.previewArea.setVisibility(View.VISIBLE);
    			UIHelper.updatePreviewContainer(context, viewHolder.previewContainer, null, di, viewHolder.previewNoItems);
    		} else {
    			viewHolder.previewArea.setVisibility(View.GONE);
    		}
    		if(di.getUserId().equals(Model.ME_OWNER)) {
    			viewHolder.sharedArea.setVisibility(View.VISIBLE);
	    		if(ModelHelper.isAgent(di.getMType())) {
	    			AndroidModelHelper.loadSharedResourcesAsynchronously(context, mrContext.owner, (AgentItem) di, viewHolder.sharedContainer, viewHolder.sharedNoItems);
	    		} else if(ModelHelper.isShareable(di.getMType())) {
	    			AndroidModelHelper.loadAccessingAgentsAsynchronously(context, (ShareableItem) di, viewHolder.sharedContainer, viewHolder.sharedNoItems);
	    		}
    		} else {
    			viewHolder.sharedArea.setVisibility(View.GONE);
    		}
		} else {
			viewHolder.expandedArea.setVisibility(View.GONE);
			viewHolder.expander.setBackgroundResource(R.drawable.button_expand);
		}
		boolean isEditable = di.getUserId().equals(Model.ME_OWNER);
		viewHolder.selectedCB.setEnabled(isEditable);
		if(isEditable) {
			viewHolder.selectedCB.setChecked(selection.contains(di.getGuid()));
			viewHolder.selectedCB.setOnCheckedChangeListener(new CheckListener<DisplayableItem>(position, this));
		}
		viewHolder.expander.setOnClickListener(new ExpandClickListener<DisplayableItem>(position, this));
		return convertView;
	}
	
	static class DimeViewHolder {
		
		TextView name;
		ImageView image;
		TextView attribute1;
		TextView attribute2;
		CheckBox selectedCB;
		ImageButton expander;
		ImageView validForSharingIcon;
    	LinearLayout expandedArea;
    	LinearLayout previewArea;
    	LinearLayout sharedArea;
    	LinearLayout previewContainer;
    	TextView previewNoItems;
    	LinearLayout infoContainer;
    	LinearLayout sharedContainer;
    	TextView sharedNoItems;
    	
    	public void resetAllViews() {
    		infoContainer.removeAllViews();
    		sharedContainer.removeAllViews();
    		previewContainer.removeAllViews();
    		attribute2.setVisibility(View.GONE);
    		selectedCB.setOnCheckedChangeListener(null);
			expander.setOnClickListener(null);
    	}
    	
	}

}