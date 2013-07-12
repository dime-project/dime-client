package eu.dime.mobile.view.data;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.view.abstr.ListActivityDisplayableItem;
import eu.dime.mobile.view.adapter.BaseAdapter_Standard;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DataboxItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.specialitem.NotificationItem;

import java.util.List;

public class ListActivity_Databox_Detail extends ListActivityDisplayableItem {

    private DataboxItem selectedDatabox;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	TAG = ListActivity_Databox_Detail.class.getSimpleName();
    	setContentView(R.layout.list_standard_detail);
        setBaseAdapter(new BaseAdapter_Standard());
    }

    @Override
    protected List<DisplayableItem> loadListData() {
        selectedDatabox = (DataboxItem) Model.getInstance().getItem(mrContext, dio.getItemType(), dio.getItemId());
        return ModelHelper.getChildrenOfDisplayableItem(mrContext, selectedDatabox);
    }

    @Override
    protected void initializeHeader() {
    	UIHelper.inflateStandardHeader(this, selectedDatabox, mrContext);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	 Intent myIntent = new Intent(ListActivity_Databox_Detail.this, TabActivity_Resource_Detail.class);
         startActivity(DimeIntentObjectHelper.populateIntent(myIntent, new DimeIntentObject(getListItems().get(position))));
    }

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(item.getElement().getType().equalsIgnoreCase(TYPES.DATABOX.toString())){
    		reloadList();
    	}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<ListActivity_Databox_Detail>createLVH(ListActivity_Databox_Detail.this);
	}
	
}
