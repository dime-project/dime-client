package eu.dime.mobile.view.abstr;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.model.GenItem;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.displayable.DisplayableItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class BaseAdapter_dime_gen extends BaseAdapter {

    protected static final String TAG = BaseAdapter_dime_gen.class.getSimpleName();
	protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<GenItem> mItems = new ArrayList<GenItem>();
    protected int expandedListItemId = -1;
    protected ListView mParent;
    protected List<String> selection = new ArrayList<String>();
    private Comparator<DisplayableItem> comparator;
    
    protected ModelRequestContext mrContext = null;
    
    public BaseAdapter_dime_gen(){ }

    public void init (Context context, ModelRequestContext mrContext, ListView parent, List<GenItem> items) {
    	
        this.mrContext = mrContext;
        this.mContext = context;
        this.mItems = items;
        this.mInflater = LayoutInflater.from(context);
        this.mParent = parent;
        this.comparator = new UIHelper.NameComparator();
    }

    protected void setComparator (Comparator<DisplayableItem> comp){
    	this.comparator = comp;
    }
    
    protected Comparator<DisplayableItem> getComparator (){
    	return this.comparator;
    }
    
    public List<String> getSelection(){
    	return this.selection;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    protected void expandedItemChanged(int position) {

        if (expandedListItemId != position) {
            expandedListItemId = position;
        } 
        else {
            expandedListItemId = -1;   
        }       
        notifyDataSetChanged();
    }

    protected void checkedItemChanged(int position, boolean isChecked) {

    	if (mItems.get(position) instanceof GenItem){
	        GenItem item = mItems.get(position);
	
	        if (isChecked) {
	            selection.add(item.getGuid());
	        } else {
	            selection.remove(item.getGuid());
	        }
    	}
    	else {
    		Log.e(TAG, "Checked Item changed on sth else than DisplayableItem: "+ mItems.get(position).getClass());
    	}
    }


    public class ExpandClickListener implements OnClickListener {

        protected int position;
        protected BaseAdapter_dime_gen pa;

        public ExpandClickListener(int pos, BaseAdapter_dime_gen pa) {
            this.position = pos;
            this.pa = pa;
        }

        @Override
        public void onClick(View v) {
            pa.expandedItemChanged(position);
        }
    }

    public class CheckListener implements OnCheckedChangeListener {

        protected int position;
        protected BaseAdapter_dime_gen pa;

        public CheckListener(int pos, BaseAdapter_dime_gen pa) {
            this.position = pos;
            this.pa = pa;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            pa.checkedItemChanged(position, isChecked);
        }
    }
}