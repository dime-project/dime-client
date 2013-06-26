package eu.dime.mobile.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.listener.CheckListener;
import eu.dime.mobile.helper.listener.ExpandClickListener;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.displayable.AccountItem;
import eu.dime.model.displayable.DisplayableItem;

public class BaseAdapter_Accounts extends BaseAdapterDisplayableItem {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.adapter_account_item, null);
        TextView name = (TextView) convertView.findViewById(R.account.name);
        ImageView image = (ImageView) convertView.findViewById(R.account.image);
        CheckBox selectedCB = (CheckBox) convertView.findViewById(R.account.checkBox);
        ImageButton expander = (ImageButton) convertView.findViewById(R.id.buttonExp);
    	LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.expanded_adapter);
        AccountItem ai = (AccountItem) mItems.get(position);
        name.setText(ai.getName());
        image.setImageDrawable(ImageHelper.getDefaultImageDrawable(ai, context));
        if (selection.contains(ai.getGuid())) {
            selectedCB.setChecked(true);
        }
        if (expandedListItemId == position) {
        	layout.setVisibility(View.VISIBLE);
    		expander.setBackgroundResource(R.drawable.button_collapse);
        } else {
        	layout.setVisibility(View.GONE);
    		expander.setBackgroundResource(R.drawable.button_expand);
        }
        selectedCB.setOnCheckedChangeListener(new CheckListener<DisplayableItem>(position, this));
        expander.setOnClickListener(new ExpandClickListener<DisplayableItem>(position, this));
        return convertView;
    }
    
}