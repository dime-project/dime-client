package eu.dime.mobile.view.situations;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ListActivityDisplayableItem;
import eu.dime.mobile.view.adapter.BaseAdapter_Situation;
import eu.dime.model.Model;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.specialitem.NotificationItem;

import java.util.List;

public class ListActivity_Situations extends ListActivityDisplayableItem {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	TAG = ListActivity_Situations.class.getSimpleName();
        setBaseAdapter(new BaseAdapter_Situation());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<DisplayableItem> loadListData() {
        return (List<DisplayableItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.SITUATION);
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		return false;
	}
	
	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(item.getElement().getType().equalsIgnoreCase(TYPES.SITUATION.toString())){
    		reloadList();
    	}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<ListActivity_Situations>createLVH(ListActivity_Situations.this);
	}
	
}