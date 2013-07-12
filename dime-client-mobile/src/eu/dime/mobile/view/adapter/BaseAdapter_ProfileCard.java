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
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.ProfileItem;

public class BaseAdapter_ProfileCard extends BaseAdapterDisplayableItem {

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.adapter_profilecard_item, null);
		ImageButton expander = (ImageButton) view.findViewById(R.id.buttonExp);
    	LinearLayout layout = (LinearLayout) view.findViewById(R.id.expanded_adapter);
    	TextView previewNoItems = (TextView) view.findViewById(R.adapter.preview_noitems);
    	ImageView image = (ImageView) view.findViewById(R.id.imageViewProfile);
    	ImageView img = (ImageView) view.findViewById(R.id.locked);
    	CheckBox cb = (CheckBox) view.findViewById(R.id.checkBoxProfile);
    	LinearLayout previewContainer = (LinearLayout) view.findViewById(R.profile.previewContainer);
    	TextView name = (TextView) view.findViewById(R.id.textViewProfileName);
		ProfileItem profile = (ProfileItem) mItems.get(position);
		if(profile.isEditable()){
			img.setVisibility(View.GONE);
		} else {
			img.setVisibility(View.VISIBLE);
			view.setBackgroundColor(context.getResources().getColor(R.color.dm_col5));
		}
		name.setText(profile.getName());
		ImageHelper.loadImageAsynchronously(image, profile, context);
		if (expandedListItemId == position) {
			layout.setVisibility(View.VISIBLE);
    		expander.setBackgroundResource(R.drawable.button_collapse);
			AndroidModelHelper.loadChildrenOfDisplayableItemAsynchronously(context, mrContext.owner, previewContainer, profile, previewNoItems);
		} else {
			layout.setVisibility(View.GONE);
    		expander.setBackgroundResource(R.drawable.button_expand);
		}
        cb.setChecked(selection.contains(profile.getGuid()));
		cb.setOnCheckedChangeListener(new CheckListener<DisplayableItem>(position, this));
	    expander.setOnClickListener(new ExpandClickListener<DisplayableItem>(position, this));
		return view;
	}

}