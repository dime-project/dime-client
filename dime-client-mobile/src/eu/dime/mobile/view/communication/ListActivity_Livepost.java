package eu.dime.mobile.view.communication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.view.abstr.ListActivityDisplayableItem;
import eu.dime.mobile.view.adapter.BaseAdapter_Livepost;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.specialitem.NotificationItem;

import java.util.List;

public class ListActivity_Livepost extends ListActivityDisplayableItem {

	/**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	TAG = ListActivity_Livepost.class.getSimpleName();
        setBaseAdapter(new BaseAdapter_Livepost());
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected List<DisplayableItem> loadListData() {
        return (List<DisplayableItem>) (Object) ModelHelper.getAllLivePosts(mrContext);
    }
    
    @Override
    protected void initializeHeader() {
    	
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 
    	Intent myIntent = new Intent(ListActivity_Livepost.this, TabActivity_Livepost_Detail.class);
        startActivity(DimeIntentObjectHelper.populateIntent(myIntent, new DimeIntentObject(getListItems().get(position))));
    }

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
    	if(item.getElement().getType().equalsIgnoreCase(TYPES.LIVEPOST.toString())){
    		reloadList();
    	}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<ListActivity_Livepost>createLVH(ListActivity_Livepost.this);
	}

}
