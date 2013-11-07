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

package eu.dime.mobile.view.communication;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.dime.control.SilentLoadingViewHandler;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.acl.ACLPackage;
import eu.dime.model.acl.ACLPerson;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.specialitem.NotificationItem;

public class Activity_Livepost_Detail extends ActivityDime {

    private LivePostItem livepost;
    private String senderName;
    private String receiverNames;
    private boolean isOwnItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = Activity_Livepost_Detail.class.getSimpleName();
        setContentView(R.layout.livepost_detail);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	startTask("");
    }

	@Override
	protected void loadData() {	
		livepost = (LivePostItem) Model.getInstance().getItem(mrContext, dio.getItemType(), dio.getItemId());
		isOwnItem = livepost.getUserId().equals(Model.ME_OWNER);
		if(isOwnItem) {
			senderName = "me";
			try {
				ArrayList<String> profiles = new ArrayList<String>();
				ArrayList<String> persons = new ArrayList<String>();
				ArrayList<String> groups = new ArrayList<String>();
				for (ACLPackage acl : livepost.getAccessingAgents()) {
					profiles.add(ModelHelper.getProfileWithSaid(DimeClient.getMRC(new SilentLoadingViewHandler()), acl.getSaidSender()).getName());
					for (ACLPerson aclp : acl.getPersons()) {
						try {
							persons.add(AndroidModelHelper.getOwnItemFromStorage(aclp.getPersonId(), false).getName());
						} catch (Exception e) {	}
					}
					for (String aclg : acl.getGroups()) {
						try {
							groups.add(AndroidModelHelper.getOwnItemFromStorage(aclg, false).getName());
						} catch (Exception e) {	}
					}
				}
				senderName += "@" + UIHelper.formatStringListCommaSeparated(profiles);
				receiverNames = ((persons != null && persons.size() > 0) ? UIHelper.formatStringListCommaSeparated(persons) : "") 
							  + ((groups != null && groups.size() > 0) ? ", "  + UIHelper.formatStringListCommaSeparated(groups) : "");
			} catch (Exception e) { }
		} else {
			try {
				senderName = AndroidModelHelper.getOwnItemFromStorage(livepost.getUserId(), false).getName();
			} catch (Exception e) {
				senderName = "could not retrieve sender!";
			}
		}
	}

	@Override
	protected void initializeData() {
		TextView sender = (TextView) findViewById(R.livepost.sender);
		TextView subject = (TextView) findViewById(R.livepost.subject);
		TextView message = (TextView) findViewById(R.livepost.message);
		TextView receiver = (TextView) findViewById(R.livepost.receiver);
		LinearLayout receiverContainer = (LinearLayout) findViewById(R.livepost.receiver_container);
		if(!isOwnItem) {
			receiverContainer.setVisibility(View.GONE);
		} else {
			receiver.setText(receiverNames);
		}
		sender.setText(senderName);
        subject.setText(livepost.getName());
        message.setText(livepost.getText());
	}

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		startTask("");
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Livepost_Detail>createLVH(Activity_Livepost_Detail.this);
	}
	
}
