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

package eu.dime.mobile.view.abstr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.model.GenItem;
import eu.dime.model.ModelRequestContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author simon
 * @param <ITEM_TYPE>
 */
public abstract class BaseAdapterDime<ITEM_TYPE extends GenItem> extends BaseAdapter implements Filterable {

    protected static final String TAG = BaseAdapterDime.class.getSimpleName();
    protected LayoutInflater mInflater;
    protected List<ITEM_TYPE> mItems = new ArrayList<ITEM_TYPE>();
    protected List<ITEM_TYPE> allItems = new ArrayList<ITEM_TYPE>();
    protected int expandedListItemId = -1;
    protected List<String> selection = new ArrayList<String>();
    private Comparator<ITEM_TYPE> comparator;
    protected ModelRequestContext mrContext = null;
    protected Context context = null;

    public void init(Context context, ModelRequestContext mrContext, List<ITEM_TYPE> items) {
    	this.context = context;
        this.mrContext = mrContext;
        this.mItems = items;
        this.allItems = items;
        this.mInflater = LayoutInflater.from(context);
        this.comparator = createComparator();
        sortItems();
    }
    
    public void reinit(Context context, List<ITEM_TYPE> items) {
    	this.context = context;
    	this.mItems = items;
        this.allItems = items;
        sortItems();
    }

    protected void sortItems() {
    	if (mItems != null) { 
    		Collections.sort(mItems, comparator); 
    		notifyDataSetChanged();
    	}
    }

    public List<String> getSelection() {
        return this.selection;
    }
    
    public List<ITEM_TYPE> getSelectionItems() {
    	List<ITEM_TYPE> items = new ArrayList<ITEM_TYPE>();
    	for (ITEM_TYPE item : allItems) {
			if(selection.contains(item.getGuid())) {
				items.add(item);
			}
		}
    	return items;
    }
    
    public void resetSelection(){
    	selection.clear();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    public List<ITEM_TYPE> getListItems() {
        return mItems;
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);
    
    public void resetExpandedItem() {
    	expandedListItemId = -1;
    }
    
    public void expandedItemChanged(int position) {
        if (expandedListItemId != position) {
            expandedListItemId = position;
            DimeClient.addStringToViewStack(context.getResources().getString(R.string.self_evaluation_tool_expandedview));
        } else {
            expandedListItemId = -1;
        }
        notifyDataSetChanged();
    }
    
    public void checkedItemChanged(int position, boolean isChecked) {
        GenItem item = (GenItem) mItems.get(position);
        if (isChecked) {
            selection.add(item.getGuid());
        } else {
            selection.remove(item.getGuid());
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mItems = (List<ITEM_TYPE>) results.values;
                BaseAdapterDime.this.notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ITEM_TYPE> filteredResults = getFilteredResults(constraint);
                FilterResults results = new FilterResults();
                results.values = filteredResults;
                return results;
            }
        };
    }

    protected abstract Comparator<ITEM_TYPE> createComparator();
    protected abstract List<ITEM_TYPE> getFilteredResults(CharSequence constraint);
}