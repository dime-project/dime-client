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

package eu.dime.mobile.helper.listener;

import android.view.View;
import android.view.View.OnClickListener;
import eu.dime.mobile.view.abstr.BaseAdapterDime;
import eu.dime.model.GenItem;

public class ExpandClickListener<ITEM_TYPE extends GenItem> implements OnClickListener {

    protected int position;
    protected BaseAdapterDime<ITEM_TYPE> pa;

    public ExpandClickListener(int pos, BaseAdapterDime<ITEM_TYPE> pa) {
        this.position = pos;
        this.pa = pa;
    }

    @Override
    public void onClick(View v) {
        pa.expandedItemChanged(position);
    }
}