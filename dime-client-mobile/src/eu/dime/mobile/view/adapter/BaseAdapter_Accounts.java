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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.listener.CheckListener;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.displayable.AccountItem;
import eu.dime.model.displayable.DisplayableItem;

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
        ImageHelper.loadImageAsynchronously(viewHolder.image, ai, context);
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