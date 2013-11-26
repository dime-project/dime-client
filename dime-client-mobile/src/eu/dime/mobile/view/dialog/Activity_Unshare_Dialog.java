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

package eu.dime.mobile.view.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.model.GenItem;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.acl.ACL;
import eu.dime.model.displayable.AgentItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.ShareableItem;
import eu.dime.model.specialitem.NotificationItem;

public class Activity_Unshare_Dialog extends ActivityDime implements OnClickListener {
	
	private DisplayableItem item;
	private LinearLayout sharedContainer;
	private Button saveButton;
	private List<DisplayableItem> items;
	private List<DisplayableItem> itemsMarkedForUnsharing = new ArrayList<DisplayableItem>();
	private List<DisplayableItem> agentsMarkedForUnsharing = new ArrayList<DisplayableItem>();
	private TextView shareCount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = Activity_Unshare_Dialog.class.getSimpleName();
		setContentView(R.layout.dialog_unshare);
		sharedContainer = (LinearLayout) findViewById(R.unshare.container);
		Button cancelButton = (Button) findViewById(R.unshare.button_cancel);
		saveButton = (Button) findViewById(R.unshare.button_save);
		saveButton.setOnClickListener(this);
		shareCount = (TextView) findViewById(R.unshare.share_count);
		cancelButton.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		DimeClient.addStringToViewStack(TAG.substring(9)); //remove Activity_
		startTask("");
	}

	@Override
	protected void loadData() {
		item = (DisplayableItem) Model.getInstance().getItem(mrContext, dio.getItemType(), dio.getItemId());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initializeData() {
		LinearLayout header = (LinearLayout) findViewById(R.unshare.header);
		UIHelper.inflateStandardHeader(this, item, mrContext, header);
		sharedContainer.removeAllViews();
		if(item instanceof AgentItem) {
			items = (List<DisplayableItem>) (Object) ModelHelper.getAllShareableItemsDirectlySharedToAgent(mrContext, item.getGuid());
			addShareableWidgets(sharedContainer, items, itemsMarkedForUnsharing);
			shareCount.setText(String.valueOf(items.size()));
		} else if(item instanceof ShareableItem) {
			List<ACL> acls = ModelHelper.getAclsOfItemAndContainers(mrContext, item);
			addAgentWidgets(sharedContainer, acls, agentsMarkedForUnsharing);
			shareCount.setText(String.valueOf(acls.size()));
		}
	}
	
	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		startTask("");
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Unshare_Dialog>createLVH(Activity_Unshare_Dialog.this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.unshare.button_save:
			int errors = 0;
			if(ModelHelper.isAgent(item.getMType())) {
				for(DisplayableItem di : itemsMarkedForUnsharing) {
					try {
						di.removeAccessingAgent(item.getGuid(), item.getMType());
					} catch (Exception e) {
						errors++;
					}
				}
			} else if(ModelHelper.isShareable(item.getMType())) {
				for(DisplayableItem di : agentsMarkedForUnsharing) {
					try {
						item.removeAccessingAgent(di.getGuid(), di.getMType());
					} catch (Exception e) {	
						errors++;
					}
				}
			}
			if(errors > 0) Toast.makeText(this, "Could not unshare " + errors + " items!", Toast.LENGTH_SHORT).show();
			AndroidModelHelper.updateGenItemAsynchronously(item, null, this, mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_unshare_save));
			finish();
			break;
		case R.unshare.button_cancel:
			AndroidModelHelper.sendEvaluationDataAsynchronously(Arrays.asList((GenItem)item), mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_canceled));
			finish();
			break;
		}
	}
	
	private void addShareableWidgets(LinearLayout ll, List<DisplayableItem> items, List<DisplayableItem> markableItems){
    	if(items.size() > 0){
	    	for(DisplayableItem item : items){
		    	ll.addView(UIHelper.createSharingWidget(this,item, markableItems));
	        }
    	} else {
    		TextView tv = new TextView(this);
    		String text = "No items shared to agent!";
    		tv.setText(text);
    		tv.setTextColor(getResources().getColor(R.color.dm_text_restrained));
    		ll.addView(tv);
    	}
    }
	
	private void addAgentWidgets(LinearLayout ll, List<ACL> items, List<DisplayableItem> markableItems){
    	if(items.size() > 0 && items.get(0).getACLPackages().iterator().hasNext()){
	    	for(ACL item : items){
	    		for (String guid : item.getAgentGuids()) {
					DisplayableItem agent = (DisplayableItem) ModelHelper.getAgent(mrContext, guid);
					ll.addView(UIHelper.createSharingWidget(this,agent, markableItems));
				}
		    	
	        }
    	} else {
    		TextView tv = new TextView(this);
    		String text = "Item not shared to anybody!";
    		tv.setText(text);
    		tv.setTextColor(getResources().getColor(R.color.dm_text_restrained));
    		ll.addView(tv);
    	}
    }

}