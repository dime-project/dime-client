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

import android.app.Activity;
import android.content.Context;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.helper.ContextHelper;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.view.adapter.BaseAdapter_Situation;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.context.ContextItem;
import eu.dime.model.context.constants.Scopes;
import eu.dime.model.displayable.SituationItem;

public class CompoundCheckListenerSituationItem<DisplayableItem> implements OnCheckedChangeListener {

    protected int position;
    protected BaseAdapter_Situation pa;
    protected Context context;
    protected ModelRequestContext mrContext;

    public CompoundCheckListenerSituationItem(int pos, BaseAdapter_Situation pa, Context context, ModelRequestContext mrContext) {
        this.position = pos;
        this.pa = pa;
        this.context = context;
        this.mrContext = mrContext;
    }
    
    @Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	try {
    		SituationItem si = (SituationItem) pa.getItem(position);
    		si.setActive(isChecked);
    		AndroidModelHelper.updateGenItemAsynchronously(si, null, (Activity)context, mrContext, null);
    		ContextItem contextItem = ContextHelper.createCurrentSituationContextItem(si.getName(), Integer.valueOf(600));
    		DimeClient.contextCrawler.updateContext(Scopes.SCOPE_SITUATION, contextItem);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
