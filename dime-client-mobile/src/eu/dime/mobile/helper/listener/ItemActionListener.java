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

package eu.dime.mobile.helper.listener;

import java.util.Arrays;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.view.dialog.Activity_Edit_Item_Dialog;
import eu.dime.mobile.view.dialog.Activity_Unshare_Dialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ItemActionListener implements OnClickListener {
	
	private Dialog dialog;
	private Context context;
	private DimeIntentObject dio;
	
	public ItemActionListener(Context context, DimeIntentObject dio){
		this.context = context;
		this.dio = dio;
	}

	@Override
	public void onClick(View view) {
		if (view instanceof Button) {
			Button button = (Button) view;
			Resources res = context.getResources();
			if(dialog != null && dialog.isShowing()) dialog.dismiss();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//edit item
			if (button.getText().equals(res.getString(R.string.action_editItem))) {
				Intent intent = new Intent(context, Activity_Edit_Item_Dialog.class);
				context.startActivity(DimeIntentObjectHelper.populateIntent(intent, dio));
			}
			//share
			if (button.getText().equals(res.getString(R.string.action_share))) {
				AndroidModelHelper.shareResources(context, Arrays.asList(dio.getItemId()), dio.getItemType());
			}
			//unshare
			if (button.getText().equals(res.getString(R.string.action_unshare))) {
				Intent intent = new Intent(context, Activity_Unshare_Dialog.class);
				context.startActivity(DimeIntentObjectHelper.populateIntent(intent, dio));
			}
		}
	}

	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

}
