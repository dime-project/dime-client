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
			viewHolder.image = (ImageView) convertView.findViewById(R.id.imageViewProfile);
			viewHolder.previewNoItems = (TextView) convertView.findViewById(R.profile.preview_noitems);
			viewHolder.selectedCB = (CheckBox) convertView.findViewById(R.id.checkBoxProfile);
			viewHolder.expander = (ImageButton) convertView.findViewById(R.id.buttonExp);
			viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.expanded_adapter);
	    	viewHolder.previewContainer = (LinearLayout) convertView.findViewById(R.profile.previewContainer);
	    	viewHolder.sharingNotSupported = (LinearLayout) convertView.findViewById(R.id.sharing_not_supported);
	    	convertView.setTag(viewHolder);
		} else {
			viewHolder = (DimeViewHolder) convertView.getTag();
			viewHolder.selectedCB.setOnCheckedChangeListener(null);
		}
		convertView.setBackgroundColor(context.getResources().getColor(profile.isEditable() ? android.R.color.transparent : R.color.background_green));
		viewHolder.sharingNotSupported.setVisibility(profile.supportsSharing() ? View.GONE : View.VISIBLE);
		viewHolder.name.setText(profile.getName());
		ImageHelper.loadImageAsynchronously(viewHolder.image, profile, context);
		if (expandedListItemId == position) {
			viewHolder.layout.setVisibility(View.VISIBLE);
    		viewHolder.expander.setBackgroundResource(R.drawable.button_collapse);
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
		ImageView image;
		TextView previewNoItems;
		CheckBox selectedCB;
		ImageButton expander;
		LinearLayout layout;
		LinearLayout previewContainer;
		LinearLayout sharingNotSupported;
    	
	}

}