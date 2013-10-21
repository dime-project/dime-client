package eu.dime.mobile.view.adapter;

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
import eu.dime.mobile.helper.listener.CheckListener;
import eu.dime.mobile.helper.listener.ExpandClickListener;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.Model;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.ProfileItem;

public class BaseAdapter_ProfileCard extends BaseAdapterDisplayableItem {

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ProfileItem profile = (ProfileItem) mItems.get(position);
		// Keeps reference to avoid future findViewById()
		DimeViewHolder viewHolder;
    	if (convertView == null) {
			viewHolder = new DimeViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_profilecard_item, null);
			viewHolder.name = (TextView) convertView.findViewById(R.id.textViewProfileName);
			viewHolder.attribute1 = (TextView) convertView.findViewById(R.id.attribute1);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.imageViewProfile);
			viewHolder.previewNoItems = (TextView) convertView.findViewById(R.profile.preview_noitems);
			viewHolder.selectedCB = (CheckBox) convertView.findViewById(R.id.checkBoxProfile);
			viewHolder.validForSharingIcon = (ImageView) convertView.findViewById(R.id.is_valid_for_sharing);
			viewHolder.expander = (ImageButton) convertView.findViewById(R.id.buttonExp);
			viewHolder.borderContainer = (LinearLayout) convertView.findViewById(R.profile.borderContainer);
			viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.expanded_adapter);
			viewHolder.sharedArea = (LinearLayout) convertView.findViewById(R.adapter.sharedArea);
	    	viewHolder.previewContainer = (LinearLayout) convertView.findViewById(R.profile.previewContainer);
	    	viewHolder.sharedContainer = (LinearLayout) convertView.findViewById(R.adapter.shared_container);
			viewHolder.sharedNoItems = (TextView) convertView.findViewById(R.adapter.shared_noitems);
	    	convertView.setTag(viewHolder);
		} else {
			viewHolder = (DimeViewHolder) convertView.getTag();
			viewHolder.selectedCB.setOnCheckedChangeListener(null);
			viewHolder.previewContainer.removeAllViews();
			viewHolder.sharedContainer.removeAllViews();
		}
//		viewHolder.borderContainer.setBackgroundColor(context.getResources().getColor(profile.isEditable() ? android.R.color.transparent : R.color.background_green));
    	boolean isValidForSharing = profile.getUserId().equals(Model.ME_OWNER) && profile.supportsSharing();
		viewHolder.name.setText(profile.getName());
		AndroidModelHelper.loadProfileAttributesOfProfileAsynchronously(context, viewHolder.attribute1, profile, null);
		ImageHelper.loadImageAsynchronously(viewHolder.image, profile, context);
		viewHolder.validForSharingIcon.setVisibility((!isValidForSharing) ? View.VISIBLE : View.GONE);
		if (expandedListItemId == position) {
			viewHolder.layout.setVisibility(View.VISIBLE);
    		viewHolder.expander.setBackgroundResource(R.drawable.button_collapse);
    		if(isValidForSharing) {
    			viewHolder.sharedArea.setVisibility(View.VISIBLE);
	    		AndroidModelHelper.loadAccessingAgentsAsynchronously(context, profile, viewHolder.sharedContainer, viewHolder.sharedNoItems);
    		} else {
    			viewHolder.sharedArea.setVisibility(View.GONE);
    		}
			AndroidModelHelper.loadChildrenOfDisplayableItemAsynchronously(context, mrContext.owner, viewHolder.previewContainer, profile, viewHolder.previewNoItems);
		} else {
			viewHolder.layout.setVisibility(View.GONE);
    		viewHolder.expander.setBackgroundResource(R.drawable.button_expand);
		}
		boolean isEditable = profile.getUserId().equals(Model.ME_OWNER);
		viewHolder.selectedCB.setEnabled(isEditable);
		if(isEditable) {
			viewHolder.selectedCB.setChecked(selection.contains(profile.getGuid()));
			viewHolder.selectedCB.setOnCheckedChangeListener(new CheckListener<DisplayableItem>(position, this));
		}
	    viewHolder.expander.setOnClickListener(new ExpandClickListener<DisplayableItem>(position, this));
		return convertView;
	}
	
	static class DimeViewHolder {
		
		TextView name;
		TextView attribute1;
		ImageView image;
		TextView previewNoItems;
		CheckBox selectedCB;
		ImageButton expander;
		ImageView validForSharingIcon;
		LinearLayout layout;
		LinearLayout borderContainer;
		LinearLayout sharedArea;
		LinearLayout previewContainer;
		LinearLayout sharedContainer;
    	TextView sharedNoItems;
    	
	}

}