package eu.dime.mobile.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.listener.CheckListener;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.Model;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.AccountItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.ServiceAdapterItem;

public class BaseAdapter_Accounts extends BaseAdapterDisplayableItem {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	AccountItem ai = (AccountItem) mItems.get(position);
    	// Keeps reference to avoid future findViewById()
    	DimeViewHolder viewHolder;
    	if (convertView == null) {
			viewHolder = new DimeViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_account_item, null);
			viewHolder.name = (TextView) convertView.findViewById(R.account.name);
			viewHolder.image = (ImageView) convertView.findViewById(R.account.image);
			viewHolder.isActive = (TextView) convertView.findViewById(R.account.isActive);
			viewHolder.selectedCB = (CheckBox) convertView.findViewById(R.account.checkBox);
			viewHolder.isConfigurable = (ImageView) convertView.findViewById(R.account.isConfigurable);
	    	convertView.setTag(viewHolder);
		} else {
			viewHolder = (DimeViewHolder) convertView.getTag();
			viewHolder.selectedCB.setOnCheckedChangeListener(null);
		}
        viewHolder.isConfigurable.setVisibility((ai.getSettings() != null && ai.getSettings().size() > 0) ? View.VISIBLE : View.GONE);
        viewHolder.isActive.setVisibility((ai.isActive()) ? View.VISIBLE : View.GONE);
        viewHolder.name.setText(ai.getName());
        //FIXME as soon as imageUrl is set correctly
        if(ai.getImageUrl() != null && ai.getImageUrl().length() > 0) {
        	ImageHelper.loadImageAsynchronously(viewHolder.image, ai, context);
        } else if (ai.getServiceAdapterGUID() != null && ai.getServiceAdapterGUID().length() > 0) {
        	ServiceAdapterItem service = (ServiceAdapterItem) Model.getInstance().getItem(mrContext, TYPES.SERVICEADAPTER, ai.getServiceAdapterGUID());
        	ImageHelper.loadImageAsynchronously(viewHolder.image, service, context);
        } else {
        	viewHolder.image.setImageDrawable(ImageHelper.getDefaultImageDrawable(ai, context));
        }
        viewHolder.selectedCB.setChecked((selection.contains(ai.getGuid())));
        viewHolder.selectedCB.setOnCheckedChangeListener(new CheckListener<DisplayableItem>(position, this));
        return convertView;
    }
    
    static class DimeViewHolder {
		
		TextView name;
		ImageView image;
		TextView isActive;
		CheckBox selectedCB;
		ImageView isConfigurable;
    	
	}
    
}