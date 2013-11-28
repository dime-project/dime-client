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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.listener.ExpandClickListener;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.PersonItem;

public class BaseAdapter_Match extends BaseAdapterDisplayableItem {

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.adapter_match_item, null);
		ImageView image = (ImageView) view.findViewById(R.match.image);
		ImageButton expander = (ImageButton) view.findViewById(R.id.buttonExp);
		TextView name = (TextView) view.findViewById(R.match.person_name);
		TextView profileNames = (TextView) view.findViewById(R.match.profile_names);
		LinearLayout layout = (LinearLayout) view.findViewById(R.match.layout);
		PersonItem person = (PersonItem) mItems.get(position);
		name.setText(person.getName());
		ImageHelper.loadImageAsynchronously(image, person, context);
		if (expandedListItemId == position) {
			layout.setVisibility(View.VISIBLE);
			expander.setBackgroundResource(R.drawable.button_collapse);
			AndroidModelHelper.loadProfileNamesAsynchronously(context, profileNames, person);
		} else {
			layout.setVisibility(View.GONE);
    		expander.setBackgroundResource(R.drawable.button_expand);
		}
		expander.setOnClickListener(new ExpandClickListener<DisplayableItem>(position, this));
		return view;
	}

}