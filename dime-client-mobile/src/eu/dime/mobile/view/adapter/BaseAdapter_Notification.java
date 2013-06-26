package eu.dime.mobile.view.adapter;

import java.util.Comparator;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.listener.CheckListener;
import eu.dime.mobile.view.abstr.BaseAdapterDime;
import eu.dime.model.ComparatorHelper;
import eu.dime.model.GenItem;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;

public class BaseAdapter_Notification extends BaseAdapterDime<GenItem> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.adapter_notification_item, null);
        ImageView iv = (ImageView) view.findViewById(R.id.notification_image);
        CheckBox selectedCB = (CheckBox) view.findViewById(R.id.notification_checkBox);
        TextView message = (TextView) view.findViewById(R.id.notification_message);
        TextView sender = (TextView) view.findViewById(R.id.notification_sender);
        TextView date = (TextView) view.findViewById(R.id.notification_date);
        UserNotificationItem  uni = (UserNotificationItem) mItems.get(position);
		AndroidModelHelper.loadNotificationPropertiesAsyncronously(context, uni, view, message, sender, iv);
        selectedCB.setChecked(selection.contains(uni.getGuid()));
        if(!uni.isRead()){
        	view.setBackgroundColor(context.getResources().getColor(R.color.dm_row_alternate));
        }
        date.setText(UIHelper.formatDateByMillis(uni.getCreated()));
        selectedCB.setOnCheckedChangeListener(new CheckListener<GenItem>(position, this));
        return view;
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    protected Comparator createComparator() {
        return new ComparatorHelper.UserNotificationComparator();
    }

	@Override
	protected List<GenItem> getFilteredResults(CharSequence constraint) {
		return null;
	}

}