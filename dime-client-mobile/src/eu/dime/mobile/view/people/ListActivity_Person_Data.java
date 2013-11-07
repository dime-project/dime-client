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

package eu.dime.mobile.view.people;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.view.abstr.ListActivityDisplayableItem;
import eu.dime.mobile.view.adapter.BaseAdapter_Standard;
import eu.dime.mobile.view.data.TabActivity_Databox_Detail;
import eu.dime.mobile.view.data.TabActivity_Resource_Detail;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.specialitem.NotificationItem;
import java.util.ArrayList;
import java.util.List;

public class ListActivity_Person_Data extends ListActivityDisplayableItem {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = ListActivity_Person_Data.class.getSimpleName();
		setBaseAdapter(new BaseAdapter_Standard());
	}

	@Override
	protected List<DisplayableItem> loadListData() {
		List<DisplayableItem> data = new ArrayList<DisplayableItem>();
		mrContext = DimeClient.getMRC(dio.getItemId(), createLoadingViewHandler());
		data.addAll(ModelHelper.getDataboxesSharedByPerson(mrContext));
		data.addAll(ModelHelper.getResourcesSharedByPerson(mrContext));
		return data;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		DisplayableItem di = getListItems().get(position);
		Intent myIntent = new Intent(ListActivity_Person_Data.this, (di.getMType().equals(TYPES.RESOURCE)) ? TabActivity_Resource_Detail.class : TabActivity_Databox_Detail.class);
        startActivity(DimeIntentObjectHelper.populateIntent(myIntent, new DimeIntentObject(di)));
	}

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(item.getElement().getType().equalsIgnoreCase(TYPES.PERSON.toString())){
    		reloadList();
    	}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<ListActivity_Person_Data>createLVH(ListActivity_Person_Data.this);
	}
	
}