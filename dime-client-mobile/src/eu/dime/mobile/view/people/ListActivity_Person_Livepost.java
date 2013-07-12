package eu.dime.mobile.view.people;

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
import eu.dime.mobile.view.communication.TabActivity_Livepost_Detail;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.specialitem.NotificationItem;

import java.util.List;

public class ListActivity_Person_Livepost extends ListActivityDisplayableItem {
	
	private PersonItem pi = new PersonItem();

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	TAG = ListActivity_Person_Livepost.class.getSimpleName();
    	setContentView(R.layout.list_standard_detail);
        setBaseAdapter(new BaseAdapter_Standard());
    }

    @SuppressWarnings("unchecked")
	@Override
    protected List<DisplayableItem> loadListData() {
    	pi = (PersonItem) Model.getInstance().getItem(mrContext, dio.getItemType(), dio.getItemId());
        return (List<DisplayableItem>) (Object) ModelHelper.getDisplayableItemsFromAndForPerson(mrContext, pi.getGuid(), TYPES.LIVEPOST);
    }
    
    @Override
    protected void initializeHeader() {
    	UIHelper.inflateStandardHeader(this, pi, mrContext);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	Intent myIntent = new Intent(ListActivity_Person_Livepost.this, TabActivity_Livepost_Detail.class);
        startActivity(DimeIntentObjectHelper.populateIntent(myIntent, new DimeIntentObject(getListItems().get(position))));
    }

    public PersonItem getCurrentPerson() {
        return pi;
    }

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(item.getElement().getType().equalsIgnoreCase(TYPES.PERSON.toString())){
    		reloadList();
    	}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<ListActivity_Person_Livepost>createLVH(ListActivity_Person_Livepost.this);
	}
}
