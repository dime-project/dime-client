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
import android.widget.ImageView;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.displayable.PersonItem;

public class BaseAdapter_Match extends BaseAdapterDisplayableItem {

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.adapter_match_item, null);
    	ImageView image = (ImageView) view.findViewById(R.match.image);
//    	CheckBox cb = (CheckBox) view.findViewById(R.match.checkBox);
    	TextView name = (TextView) view.findViewById(R.match.person_name);
    	TextView status = (TextView) view.findViewById(R.match.person_status);
		PersonItem person = (PersonItem) mItems.get(position);
		name.setText(person.getName());
		status.setText("not merged");
		ImageHelper.loadImageAsynchronously(image, person, context);
//        cb.setChecked(selection.contains(person.getGuid()));
//		cb.setOnCheckedChangeListener(new CheckListener<DisplayableItem>(position, this));
		return view;
	}

}