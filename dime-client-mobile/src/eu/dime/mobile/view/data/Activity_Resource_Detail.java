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

package eu.dime.mobile.view.data;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.dime.control.SilentLoadingViewHandler;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.FileHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.acl.ACLPackage;
import eu.dime.model.acl.ACLPerson;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.model.specialitem.NotificationItem;

public class Activity_Resource_Detail extends ActivityDime implements OnClickListener {

	private ResourceItem resource;
	private String ownerName;
    private String receiverNames;
	private boolean isOwnItem;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = Activity_Resource_Detail.class.getSimpleName();
		setContentView(R.layout.resource_detail);
	}

	@Override
	protected void onResume() {
		super.onResume();
		startTask("");
	}

	@Override
	protected void loadData() {
		resource = (ResourceItem) Model.getInstance().getItem(mrContext, dio.getItemType(), dio.getItemId());
		isOwnItem = resource.getUserId().equals(Model.ME_OWNER);
		if(isOwnItem) {
			ownerName = "me";
			try {
				ArrayList<String> profiles = new ArrayList<String>();
				ArrayList<String> persons = new ArrayList<String>();
				ArrayList<String> groups = new ArrayList<String>();
				for (ACLPackage acl : resource.getAccessingAgents()) {
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
				if(profiles.size() > 0) {
					ownerName += "@" + UIHelper.formatStringListCommaSeparated(profiles);
				}
				receiverNames = ((persons != null && persons.size() > 0) ? UIHelper.formatStringListCommaSeparated(persons) : "") 
							  + ((groups != null && groups.size() > 0) ? ", "  + UIHelper.formatStringListCommaSeparated(groups) : "");
			} catch (Exception e) { }
		} else {
			try {
				ownerName = AndroidModelHelper.getOwnItemFromStorage(resource.getUserId(), false).getName();
			} catch (Exception e) {
				ownerName = "could not retrieve sender!";
			}
		}
	}

	@Override
	protected void initializeData() {
		TextView owner = (TextView) findViewById(R.resource.owner);
		TextView mimeType = (TextView) findViewById(R.resource.mime);
		TextView filesize = (TextView) findViewById(R.resource.filesize);
		TextView url = (TextView) findViewById(R.resource.url);
		TextView sharedTo = (TextView) findViewById(R.resource.sharedTo);
		LinearLayout sharedToContainer = (LinearLayout) findViewById(R.resource.sharedTo_container);
		owner.setText(ownerName);
		if(!isOwnItem || receiverNames.length() == 0) {
			sharedToContainer.setVisibility(View.GONE);
		} else {
			sharedTo.setText(receiverNames);
		}
		mimeType.setText(resource.getMimeType());
		filesize.setText(resource.getFileSize() + " Bytes");
		url.setText(resource.getDownloadUrl());
		Button openURL = (Button) findViewById(R.resource.button_openfile);
		Button download = (Button) findViewById(R.resource.button_downloadfile);
		openURL.setOnClickListener(this);
		download.setOnClickListener(this);
	}

	@Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.resource.button_openfile:
        	FileHelper.saveResourceItemAsynchronouslyOnSDAndOpen(this, resource, UIHelper.createCustonProgressDialog(this, "Trying to open the file!"));
        	break;
        case R.resource.button_downloadfile:
        	FileHelper.saveResourceItemAsynchronouslyOnSD(this, resource, UIHelper.createCustonProgressDialog(this, "Trying to download the file!"));
        	break;
        }
    }

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if (item.getElement().getType().equalsIgnoreCase(TYPES.RESOURCE.toString())) {
			startTask("");
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Resource_Detail> createLVH(Activity_Resource_Detail.this);
	}

}
