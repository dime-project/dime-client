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

package eu.dime.mobile.view.settings;

import android.os.Bundle;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.model.specialitem.NotificationItem;

public class Activity_Settings_Rules extends ActivityDime {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = Activity_Settings_Rules.class.getSimpleName();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		startTask("");
	}

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void loadData() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void initializeData() {
		// TODO Auto-generated method stub
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Settings_Rules>createLVH(Activity_Settings_Rules.this);
	}

}