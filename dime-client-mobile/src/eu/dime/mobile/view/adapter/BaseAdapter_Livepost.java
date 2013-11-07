/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

package eu.dime.mobile.view.adapter;

import java.util.Comparator;

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
import eu.dime.model.ComparatorHelper;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ShareableItem;

public class BaseAdapter_Livepost extends BaseAdapterDisplayableItem {

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LivePostItem di = (LivePostItem) mItems.get(position);
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
		ImageHelper.loadImageAsynchronously(viewHolder.image, di, context);
		boolean isValidForSharing = di.getUserId().equals(Model.ME_OWNER) ? true : false;
		viewHolder.attribute1.setText(UIHelper.formatDateByMillis(di.getCreated()));
    	viewHolder.attribute2.setText(di.getText());
    	viewHolder.attribute2.setVisibility(View.VISIBLE);
		if ((!di.getUserId().equals(Model.ME_OWNER))) {
        	PersonItem pi = (PersonItem) AndroidModelHelper.getGenItemSynchronously(context, new DimeIntentObject(di.getUserId(), TYPES.PERSON));
        	if(pi != null){
        		viewHolder.name.setText(pi.getName() + ": " + di.getName());
        	}
        } else {
        	viewHolder.name.setText(di.getName());
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
	    		AndroidModelHelper.loadAccessingAgentsAsynchronously(context, (ShareableItem) di, viewHolder.sharedContainer, viewHolder.sharedNoItems);
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
	
	@Override
    protected Comparator<DisplayableItem> createComparator() {
    	return new ComparatorHelper.LivePostComparator();
    }

}