package eu.dime.mobile.view.people;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.view.abstr.ListActivityDisplayableItem;
import eu.dime.mobile.view.adapter.BaseAdapter_Standard;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.specialitem.NotificationItem;
import java.util.List;

public class ListActivity_Group_Detail extends ListActivityDisplayableItem {

    private GroupItem selectedGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	TAG = ListActivity_Group_Detail.class.getSimpleName();
		getListView().setOnItemClickListener(this);
        setBaseAdapter(new BaseAdapter_Standard());
    }

    @Override
    protected List<DisplayableItem> loadListData() {
    	selectedGroup = (GroupItem) Model.getInstance().getItem(mrContext, dio.getItemType(), dio.getItemId());
        return ModelHelper.getChildrenOfDisplayableItem(mrContext, selectedGroup);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent myIntent = new Intent(ListActivity_Group_Detail.this, TabActivity_Person_Detail.class);
        startActivity(DimeIntentObjectHelper.populateIntent(myIntent, new DimeIntentObject(getListItems().get(position))));
    }
    
	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(item.getElement().getType().equalsIgnoreCase(TYPES.GROUP.toString())){
    		reloadList();
    	}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<ListActivity_Group_Detail>createLVH(ListActivity_Group_Detail.this);
	}
	
}