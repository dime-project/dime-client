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
