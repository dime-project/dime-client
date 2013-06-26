package eu.dime.mobile.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.displayable.PersonItem;

public class BaseAdapter_SearchResult extends BaseAdapterDisplayableItem {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.adapter_searchresult_item, null);
        TextView name = (TextView) view.findViewById(R.searchresult.name);
        TextView nickName = (TextView) view.findViewById(R.searchresult.nickname);
        PersonItem pi = (PersonItem) mItems.get(position);
        name.setText(pi.getName());
        nickName.setText(pi.getGuid());
        return view;
    }
}