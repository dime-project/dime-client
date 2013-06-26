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