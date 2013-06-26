package eu.dime.mobile.view.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.listener.CheckListener;
import eu.dime.mobile.helper.listener.ExpandClickListener;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.ComparatorHelper;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BaseAdapter_Livepost extends BaseAdapterDisplayableItem {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view =  mInflater.inflate(R.layout.adapter_livepost_item, null);
        LivePostItem livepost = (LivePostItem) mItems.get(position);
        CheckBox selectedCB = (CheckBox) view.findViewById(R.livepost.checkBox);
        TextView text = (TextView) view.findViewById(R.livepost.text);
        TextView time = (TextView) view.findViewById(R.livepost.time);
        TextView subject = (TextView) view.findViewById(R.livepost.subject);
        ImageView imageLeft = (ImageView) view.findViewById(R.livepost.image_left);
        ImageView imageRight = (ImageView) view.findViewById(R.livepost.image_right);
        ImageButton expander = (ImageButton) view.findViewById(R.id.buttonExp);
        time.setText(UIHelper.formatDateByMillis(livepost.getTimeStamp()));
        selectedCB.setChecked((selection.contains(livepost.getGuid())));
        subject.setText(livepost.getName());
        if ((!livepost.getUserId().equals(Model.ME_OWNER))) {
        	PersonItem pi = (PersonItem) AndroidModelHelper.getGenItemSynchronously(context, new DimeIntentObject(livepost.getUserId(), TYPES.PERSON));
        	imageLeft.setVisibility(View.GONE);
            imageRight.setVisibility(View.VISIBLE);
        	if(pi != null){
	            ImageHelper.loadImageAsynchronously(imageRight, pi, context);
        	}
        } else {
        	imageLeft.setVisibility(View.VISIBLE);
            imageRight.setVisibility(View.GONE);
            ImageHelper.loadImageAsynchronously(imageLeft, (DisplayableItem) ModelHelper.getDefaultProfileForSharing(mrContext), context);
        }
        text.setText(livepost.getText());
        if (expandedListItemId == position) {
        	expander.setBackgroundResource(R.drawable.button_collapse);
        	text.setSingleLine(false);
        	text.setEllipsize(null);
        } else {
        	expander.setBackgroundResource(R.drawable.button_expand);
        	text.setSingleLine(true);
        	text.setEllipsize(TruncateAt.END);
        }
        expander.setOnClickListener(new ExpandClickListener<DisplayableItem>(position, this));
        selectedCB.setOnCheckedChangeListener(new CheckListener<DisplayableItem>(position, this));
        return view;
    }
    
    @SuppressLint("DefaultLocale")
    @Override
    protected List<DisplayableItem> getFilteredResults(CharSequence constraint) {
        List<DisplayableItem> tmp = new ArrayList<DisplayableItem>(allItems);
        List<DisplayableItem> tmp2 = new ArrayList<DisplayableItem>(allItems);
        for (DisplayableItem item : tmp) {
        	LivePostItem lpi = (LivePostItem) item;
            if (!lpi.getText().toLowerCase().contains(String.valueOf(constraint).toLowerCase()) && !lpi.getName().toLowerCase().contains(String.valueOf(constraint).toLowerCase())) {
                tmp2.remove(item);
            }
        }
        return tmp2;
    }
    
    @Override
    protected Comparator<DisplayableItem> createComparator() {
        return new ComparatorHelper.LivePostComparator();
    }
    
}