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
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.displayable.PersonItem;

public class BaseAdapter_SearchResult extends BaseAdapterDisplayableItem {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.adapter_searchresult_item, null);
        TextView name = (TextView) view.findViewById(R.searchresult.name);
        TextView nickName = (TextView) view.findViewById(R.searchresult.nickname);
        PersonItem pi = (PersonItem) mItems.get(position);
        name.setText(pi.getName());
        nickName.setText(pi.getGuid());
        return view;
    }
}