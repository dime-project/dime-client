package eu.dime.mobile.view.people;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ListActivityDisplayableItem;
import eu.dime.mobile.view.adapter.BaseAdapter_ProfileCard;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.specialitem.NotificationItem;

import java.util.List;

public class ListActivity_Person_Profile extends ListActivityDisplayableItem {
	
	private PersonItem pi = new PersonItem();
	private ModelRequestContext mrContextMe = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = ListActivity_Person_Profile.class.getSimpleName();
		setContentView(R.layout.list_standard_detail);
        setBaseAdapter(new BaseAdapter_ProfileCard());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<DisplayableItem> loadListData() {
    	mrContextMe = DimeClient.getMRC(createLoadingViewHandler());
		mrContext = DimeClient.getMRC(dio.getItemId(), createLoadingViewHandler());
    	pi = (PersonItem) Model.getInstance().getItem(mrContextMe, dio.getItemType(), dio.getItemId());
        return (List<DisplayableItem>) (Object) ModelHelper.getProfilesSharedByPerson(mrContext);
    }
    
    @Override
    protected void initializeHeader() {
    	UIHelper.inflateStandardHeader(this, pi, mrContextMe);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TODO Implement Detail View
    }

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(item.getElement().getType().equalsIgnoreCase(TYPES.PERSON.toString())){
    		reloadList();
    	}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<ListActivity_Person_Profile>createLVH(ListActivity_Person_Profile.this);
	}
}
