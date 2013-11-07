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
import java.util.Arrays;
import java.util.List;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.TabActivityDisplayableItemDetail;
import eu.dime.model.displayable.PersonItem;

public class TabActivity_Livepost_Detail extends TabActivityDisplayableItemDetail {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Livepost_Detail.class.getSimpleName();
	}
	
	@Override
	protected void initializeTabs() {
    	tabs.add(new DimeTabObject(getResources().getString(R.string.tab_communicationDetail) + " " + di.getName(), Activity_Livepost_Detail.class, dio));
    	TabActivity_Livepost_Detail.this.init(true, false, isOwnItem(), true);
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Livepost_Detail> createLVH(TabActivity_Livepost_Detail.this);
	}

	@Override
	protected List<String> getActionsForDetailView() {
		ArrayList<String> actions = new ArrayList<String>();
		if(!isOwnItem()) {
			actions.add(getString(R.string.action_answerLivepost));
		}
		return actions;
	}

	@Override
	protected void initializeHeader() {
		ViewGroup headerContainer = (ViewGroup) findViewById(R.tabframe.header);
		LinearLayout header = null;
		if(headerContainer.getChildCount() > 0) {
			header = (LinearLayout) headerContainer.getChildAt(0);
		} else {
			header = (LinearLayout) getLayoutInflater().inflate(R.layout.header_standard, headerContainer);
		}
		UIHelper.inflateStandardHeader(this, di, mrContext, header);
	}
	
	@Override
	public void onClick(View view) {
		super.onClick(view);
		if (view instanceof Button) {
            Button button = (Button) view;
			/**
             * ---------------------------------------------------------------------------------------------------------------------------
             * Actions for Activity_Livepost_Detail
             * ---------------------------------------------------------------------------------------------------------------------------
             */
			if (currentActivity instanceof Activity_Livepost_Detail) {
				//Search public resolver
				if (button.getText().equals(getString(R.string.action_answerLivepost))) {
					actionDialog.dismiss();
					AndroidModelHelper.answerLivepost(currentActivity, Arrays.asList((PersonItem) AndroidModelHelper.getOwnItemFromStorage(di.getUserId(), false)));
				}
			}
		}
	}
	
}
