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

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.listener.CheckListener;
import eu.dime.mobile.helper.listener.ExpandClickListener;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;

import java.util.List;

public class BaseAdapter_Dialog_Sharing_Profile extends BaseAdapterDisplayableItem {
	
	@Override
	public void init(Context context, ModelRequestContext mrContext, List<DisplayableItem> items) {
		super.init(context, mrContext, items);
		if(items.size()>0 && items.get(0) != null) {
			selection.add(items.get(0).getGuid());
		}
	}

	@SuppressWarnings({ "deprecation" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.adapter_dialog_sharing_profilecard_item, null);
		ImageButton expander = (ImageButton) view.findViewById(R.id.buttonExp);
    	LinearLayout layout = (LinearLayout) view.findViewById(R.id.expanded_adapter);
		ProfileItem profile = (ProfileItem) mItems.get(position);
		TextView name = (TextView) view.findViewById(R.id.textViewProfileName);
		name.setText(profile.getName());
		ImageView image = (ImageView) view.findViewById(R.id.imageViewProfile);
		ImageHelper.loadImageAsynchronously(image, profile, context);
		if (expandedListItemId == position) {
			layout.setVisibility(View.VISIBLE);
    		expander.setBackgroundResource(R.drawable.button_collapse);
    		layout.removeAllViews();
			List<ProfileAttributeItem> pailist = ModelHelper.getProfileAttributesOfProfile(mrContext, profile);
			int counter = 0;
			for (ProfileAttributeItem pai : pailist) {       
	            if (pai!=null) {
					TextView labelCat = new TextView(context);
					labelCat.setText(pai.getCategory());
					labelCat.setTextAppearance(context, R.style.dimeTheme);
					labelCat.setTypeface(null, Typeface.BOLD);
					LinearLayout.LayoutParams lpms1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);
					lpms1.setMargins(0, 8, 0, 2);
					labelCat.setLayoutParams(lpms1);
					boolean hasChild = false;
					int index = 0;
					for (String key : pai.getValue().keySet()) {
						String value = pai.getValue().get(key);
						if(value.length()>0) {
							if(!hasChild) {
								hasChild = true;
								index = layout.getChildCount();
							}
							LinearLayout line = new LinearLayout(context);
							line.setOrientation(LinearLayout.HORIZONTAL);
							line.setPadding(0, 2, 0, 2);
							TextView labelTV = new TextView(context);
							labelTV.setText(key);
							labelTV.setSingleLine();
							labelTV.setTextAppearance(context, R.style.dimeTheme);
							LinearLayout.LayoutParams lpms = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
							lpms.setMargins(10, 0, 0, 0);
							labelTV.setLayoutParams(lpms);
							line.addView(labelTV);
							TextView valueTV = new TextView(context);
							valueTV.setText(value);
							valueTV.setTextAppearance(context, R.style.dimeTheme);
							LinearLayout.LayoutParams lpms2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
							lpms2.setMargins(0, 0, 6, 0);
							valueTV.setLayoutParams(lpms2);
							valueTV.setGravity(Gravity.RIGHT);
							line.addView(valueTV);
							if(counter % 2 == 0) {
								line.setBackgroundColor(context.getResources().getColor(R.color.background_grey_bright));
							} else {
								line.setBackgroundColor(context.getResources().getColor(android.R.color.white));
							}
							layout.addView(line, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
							counter++;
						}
					}
					if(hasChild) { layout.addView(labelCat,index); }
	            }
			}
		} else {
			layout.setVisibility(View.GONE);
    		expander.setBackgroundResource(R.drawable.button_expand);
		}
		RadioButton cb = (RadioButton) view.findViewById(R.id.selector);
		if (selection.contains(profile.getGuid())) {
            cb.setChecked(true);
        }
		cb.setOnCheckedChangeListener(new CheckListener<DisplayableItem>(position, this));
	    expander.setOnClickListener(new ExpandClickListener<DisplayableItem>(position, this));
		return view;
	}
	
	@Override
	public void checkedItemChanged(int position, boolean isChecked){
        if (mItems.get(position) instanceof DisplayableItem) {
        	selection.clear();
            DisplayableItem item = (DisplayableItem) mItems.get(position);
            selection.add(item.getGuid());
        } else {
            Log.e(TAG, "Checked Item changed on sth else than DisplayableItem: " + mItems.get(position).getClass());
        }
        notifyDataSetChanged();
    }

}