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
