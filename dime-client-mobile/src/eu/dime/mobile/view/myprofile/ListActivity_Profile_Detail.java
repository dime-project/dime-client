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

package eu.dime.mobile.view.myprofile;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ListActivityDisplayableItem;
import eu.dime.mobile.view.adapter.BaseAdapter_ProfileAttribute;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.specialitem.NotificationItem;
import java.util.List;

public class ListActivity_Profile_Detail extends ListActivityDisplayableItem {

    private ProfileItem profile;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = ListActivity_Profile_Detail.class.getSimpleName();
        setBaseAdapter(new BaseAdapter_ProfileAttribute());
    }

    @Override
    protected List<DisplayableItem> loadListData() {
        profile = (ProfileItem) Model.getInstance().getItem(mrContext, dio.getItemType(), dio.getItemId());
        ((BaseAdapter_ProfileAttribute) baseAdapter).setProfile(profile);
        return ModelHelper.getChildrenOfDisplayableItem(mrContext, profile);
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
    	if(item.getElement().getType().equalsIgnoreCase(TYPES.PROFILEATTRIBUTE.toString()) || item.getElement().getType().equalsIgnoreCase(TYPES.PROFILE.toString())){
    		reloadList();
    	}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<ListActivity_Profile_Detail>createLVH(ListActivity_Profile_Detail.this);
	}
	
}