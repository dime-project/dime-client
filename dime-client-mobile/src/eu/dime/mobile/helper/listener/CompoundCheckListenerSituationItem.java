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
