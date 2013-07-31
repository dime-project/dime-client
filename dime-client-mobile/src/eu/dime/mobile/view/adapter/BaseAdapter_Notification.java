package eu.dime.mobile.view.adapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
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
import eu.dime.model.specialitem.usernotification.UNEntryAdhocGroupRecommendation;
import eu.dime.model.specialitem.usernotification.UNEntryMergeRecommendation;
import eu.dime.model.specialitem.usernotification.UNEntryMessage;
import eu.dime.model.specialitem.usernotification.UNEntryRefToItem;
import eu.dime.model.specialitem.usernotification.UNEntrySituationRecommendation;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;

public class BaseAdapter_Notification extends BaseAdapterDime<UserNotificationItem> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	UserNotificationItem  uni = (UserNotificationItem) mItems.get(position);
    	// Keeps reference to avoid future findViewById()
    	DimeViewHolder viewHolder;
    	if (convertView == null) {
			viewHolder = new DimeViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_notification_item, null);
			viewHolder.date = (TextView) convertView.findViewById(R.id.notification_date);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.notification_image);
			viewHolder.message = (TextView) convertView.findViewById(R.id.notification_message);
			viewHolder.sender = (TextView) convertView.findViewById(R.id.notification_sender);
			viewHolder.selectedCB = (CheckBox) convertView.findViewById(R.id.notification_checkBox);
	    	convertView.setTag(viewHolder);
		} else {
			viewHolder = (DimeViewHolder) convertView.getTag();
			viewHolder.selectedCB.setOnCheckedChangeListener(null);
		}
		AndroidModelHelper.loadNotificationPropertiesAsynchronously(context, uni, convertView, viewHolder.message, viewHolder.sender, viewHolder.image);
		viewHolder.selectedCB.setChecked(selection.contains(uni.getGuid()));
        convertView.setBackgroundColor((!uni.isRead()) ? context.getResources().getColor(R.color.background_grey_bright) : context.getResources().getColor(android.R.color.white));
        viewHolder.date.setText(UIHelper.formatDateByMillis(uni.getCreated()));
        viewHolder.selectedCB.setOnCheckedChangeListener(new CheckListener<UserNotificationItem>(position, this));
        return convertView;
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    protected Comparator createComparator() {
        return new ComparatorHelper.UserNotificationComparator();
    }

	@SuppressLint("DefaultLocale")
	@Override
	protected List<UserNotificationItem> getFilteredResults(CharSequence constraint) {
		List<UserNotificationItem> tmp = new ArrayList<UserNotificationItem>(allItems);
		Iterator<UserNotificationItem> iter = tmp.iterator();
		while (iter.hasNext()){
			UserNotificationItem myItem = iter.next();
			boolean containsText = false;
			switch (myItem.getUnType()) {
			case ADHOC_GROUP_RECOMMENDATION:
				UNEntryAdhocGroupRecommendation entryAdhoc = (UNEntryAdhocGroupRecommendation) myItem.getUnEntry();
				containsText = (entryAdhoc.getName().toLowerCase().contains(String.valueOf(constraint).toLowerCase()));
				break;
			case MERGE_RECOMMENDATION:
				UNEntryMergeRecommendation entryMerge = (UNEntryMergeRecommendation) myItem.getUnEntry();
				containsText = (entryMerge.getSourceName().toLowerCase().contains(String.valueOf(constraint).toLowerCase())) ||
							   (entryMerge.getTargetName().toLowerCase().contains(String.valueOf(constraint).toLowerCase()));
				break;
			case MESSAGE:
				UNEntryMessage entryMessage = (UNEntryMessage) myItem.getUnEntry();
				containsText = (entryMessage.getMessage().toLowerCase().contains(String.valueOf(constraint).toLowerCase()));
				break;
			case REF_TO_ITEM:
				UNEntryRefToItem entryRef = (UNEntryRefToItem) myItem.getUnEntry();
				containsText =  (entryRef.getOperation().toLowerCase().contains(String.valueOf(constraint).toLowerCase())) ||
								(entryRef.getName().toLowerCase().contains(String.valueOf(constraint).toLowerCase())) ||
								(entryRef.getStringType().toLowerCase().contains(String.valueOf(constraint).toLowerCase()));
				break;
			case SITUATION_RECOMMENDATION:
				UNEntrySituationRecommendation entrySituation = (UNEntrySituationRecommendation) myItem.getUnEntry();
				containsText = (entrySituation.getUnType().toString().toLowerCase().contains(String.valueOf(constraint).toLowerCase()));
				break;
			case UNDEFINED:
				containsText = (!myItem.getUnType().toString().toLowerCase().contains(String.valueOf(constraint).toLowerCase()));
				break;
			}
            if (!containsText) {
                iter.remove();
            }
        }
        return tmp;
	}
	
	static class DimeViewHolder {
		
		TextView date;
		TextView sender;
		ImageView image;
		TextView message;
		CheckBox selectedCB;
		ImageView isConfigurable;
    	
	}

}