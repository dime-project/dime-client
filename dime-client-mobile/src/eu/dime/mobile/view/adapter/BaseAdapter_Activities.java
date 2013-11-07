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

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.listener.ExpandClickListener;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.displayable.ActivityItem;
import eu.dime.model.displayable.DisplayableItem;

public class BaseAdapter_Activities extends BaseAdapterDisplayableItem {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.adapter_activity_item, null);
        TextView name = (TextView) convertView.findViewById(R.activity.textView_name);
        TextView description = (TextView) convertView.findViewById(R.activity.textView_description);
        ImageButton expander = (ImageButton) convertView.findViewById(R.id.buttonExp);
    	LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.expanded_adapter);
        ActivityItem ai = (ActivityItem) mItems.get(position);
        name.setText(ai.getName());
        description.setText(ai.getDescription());
        if (expandedListItemId == position) {
        	TextView calories = (TextView) convertView.findViewById(R.activity.textView_calories);
        	TextView distance = (TextView) convertView.findViewById(R.activity.textView_distance);
        	TextView duration = (TextView) convertView.findViewById(R.activity.textView_duration);
        	layout.setVisibility(View.VISIBLE);
    		expander.setBackgroundResource(R.drawable.button_collapse);
            calories.setText(String.valueOf(ai.getCaloriesExpended()));
            distance.setText(ai.getDistanceCovered() + " m");
            duration.setText(ai.getDuration() + " seconds");
        } else {
        	layout.setVisibility(View.GONE);
    		expander.setBackgroundResource(R.drawable.button_expand);
        }
        expander.setOnClickListener(new ExpandClickListener<DisplayableItem>(position, this));
        return convertView;
    }
    
}