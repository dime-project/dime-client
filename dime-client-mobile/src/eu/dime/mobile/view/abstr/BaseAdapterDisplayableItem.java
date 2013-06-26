/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.mobile.view.abstr;

import android.annotation.SuppressLint;
import eu.dime.model.ComparatorHelper;
import eu.dime.model.displayable.DisplayableItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author simon
 */
public abstract class BaseAdapterDisplayableItem extends BaseAdapterDime<DisplayableItem> {
    
	@Override
    protected Comparator<DisplayableItem> createComparator() {
    	return new ComparatorHelper.NameComparator();
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected List<DisplayableItem> getFilteredResults(CharSequence constraint) {
        List<DisplayableItem> tmp = new ArrayList<DisplayableItem>(allItems);
        List<DisplayableItem> tmp2 = new ArrayList<DisplayableItem>(allItems);
        for (DisplayableItem item : tmp) {
            if (!item.getName().toLowerCase().contains(String.valueOf(constraint).toLowerCase())) {
                tmp2.remove(item);
            }
        }
        return tmp2;
    }

}
