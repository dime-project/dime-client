package eu.dime.mobile.view.adapter;

import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.listener.CheckListener;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.ServiceAdapterItem;

public class BaseAdapter_ServiceAdapter extends BaseAdapterDisplayableItem {
	
	@Override
	public void init(Context context, ModelRequestContext mrContext, List<DisplayableItem> items) {
		super.init(context, mrContext, items);
		if(items.size()>0 && items.get(0) != null) {
			selection.add(items.get(0).getGuid());
		}
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ServiceAdapterItem ai = (ServiceAdapterItem) mItems.get(position);
    	// Keeps reference to avoid future findViewById()
    	DimeViewHolder viewHolder;
    	if (convertView == null) {
			viewHolder = new DimeViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_serviceadapter_item, null);
			viewHolder.name = (TextView) convertView.findViewById(R.service.name);
			viewHolder.image = (ImageView) convertView.findViewById(R.service.image);
			viewHolder.description = (TextView) convertView.findViewById(R.service.description);
			viewHolder.selectedCB = (RadioButton) convertView.findViewById(R.service.radioButton);
			viewHolder.isConfigurable = (ImageView) convertView.findViewById(R.service.isConfigurable);
	    	convertView.setTag(viewHolder);
		} else {
			viewHolder = (DimeViewHolder) convertView.getTag();
			viewHolder.selectedCB.setOnCheckedChangeListener(null);
		}
    	viewHolder.isConfigurable.setVisibility((ai.isConfigurable()) ? View.VISIBLE : View.GONE);
    	viewHolder.name.setText(ai.getName());
    	viewHolder.description.setText(ai.getDescription());
        ImageHelper.loadImageAsynchronously(viewHolder.image, ai, context);
        viewHolder.selectedCB.setChecked((selection.contains(ai.getGuid())));
        viewHolder.selectedCB.setOnCheckedChangeListener(new CheckListener<DisplayableItem>(position, this));
        return convertView;
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
    
    static class DimeViewHolder {
		
		TextView name;
		ImageView image;
		TextView description;
		RadioButton selectedCB;
		ImageView isConfigurable;
    	
	}
    
}