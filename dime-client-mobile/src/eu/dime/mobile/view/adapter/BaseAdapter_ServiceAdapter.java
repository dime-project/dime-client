package eu.dime.mobile.view.adapter;

import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
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
	
	private ServiceAdapterItem selectedServiceAdapter;

	public ServiceAdapterItem getSelectedServiceAdapter() {
		return selectedServiceAdapter;
	}
	
	@Override
	public void init(Context context, ModelRequestContext mrContext, ListView parent, List<DisplayableItem> items) {
		super.init(context, mrContext, parent, items);
		if(items.size()>0 && items.get(0) !=null) {
			selection.add(items.get(0).getGuid());
			selectedServiceAdapter = (ServiceAdapterItem) items.get(0);
		}
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	convertView = mInflater.inflate(R.layout.adapter_serviceadapter_item, null);
        TextView name = (TextView) convertView.findViewById(R.service.name);
        TextView description = (TextView) convertView.findViewById(R.service.description);
        ImageView image = (ImageView) convertView.findViewById(R.service.image);
        RadioButton selectedCB = (RadioButton) convertView.findViewById(R.service.radioButton);
        ServiceAdapterItem ai = (ServiceAdapterItem) mItems.get(position);
        name.setText(ai.getName());
        description.setText(ai.getDescription());
        ImageHelper.loadImageAsynchronously(image, ai, context);
        if (selection.contains(ai.getGuid())) {
            selectedCB.setChecked(true);
        }
        selectedCB.setOnCheckedChangeListener(new CheckListener<DisplayableItem>(position, this));
        return convertView;
    }
    
    @Override
	public void checkedItemChanged(int position, boolean isChecked){
        if (mItems.get(position) instanceof DisplayableItem) {
        	selection.clear();
            DisplayableItem item = (DisplayableItem) mItems.get(position);
            selection.add(item.getGuid());
            selectedServiceAdapter = (ServiceAdapterItem) item;
        } else {
            Log.e(TAG, "Checked Item changed on sth else than DisplayableItem: " + mItems.get(position).getClass());
        }
        notifyDataSetChanged();
    }
    
}