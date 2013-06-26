package eu.dime.mobile.view.dialog;

import java.util.Arrays;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.model.GenItem;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.displayable.AgentItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.ShareableItem;
import eu.dime.model.specialitem.NotificationItem;

public class Activity_Edit_Item_Dialog extends ActivityDime implements OnClickListener, OnSeekBarChangeListener {

	private DisplayableItem item;
	private EditText name;
	private LinearLayout seekLayout;
	private LinearLayout specificContainer;
	private TextView seekLabel;
	private SeekBar seek;
	private TextView seekText;
	private ImageView headerImage;
	private TextView headerName;
	private TextView headerModified;
	private TextView headerChangesMade;
	private Button saveButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = Activity_Edit_Item_Dialog.class.getSimpleName();
		setContentView(R.layout.dialog_edit_item);
		name = (EditText) findViewById(R.edit.name);
		headerImage = (ImageView) findViewById(R.header.image);
		headerName = (TextView) findViewById(R.header.name);
		headerModified = (TextView) findViewById(R.header.lastModified);
		headerChangesMade = (TextView) findViewById(R.header.changesMade);
		seekLayout = (LinearLayout) findViewById(R.edit.bar_layout);
		specificContainer = (LinearLayout) findViewById(R.edit.specific_container);
		seekLabel = (TextView) findViewById(R.edit.bar_label);
		seek = (SeekBar) findViewById(R.edit.bar);
		seekText = (TextView) findViewById(R.edit.bar_text);
		Button uploadImage = (Button) findViewById(R.edit.button_upload_picture);
		Button cancelButton = (Button) findViewById(R.edit.button_cancel);
		saveButton = (Button) findViewById(R.edit.button_save);
		uploadImage.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		seek.setOnSeekBarChangeListener(this);
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

	@Override
	protected void initializeData() {
		ImageHelper.loadImageAsynchronously(headerImage, item, this);
		headerName.setText(item.getName());
		name.setText(item.getName());
		headerModified.setText(UIHelper.formatDateByMillis(item.getLastUpdated()));
		UIHelper.updateTrustOrPrivacyLevelTextView(this, seekText, item);
		Integer level = AndroidModelHelper.getTrustOrPrivacyLevelForDisplayableItem(item);
		if(level != null) { 
			seek.setProgress(level); 
			seekLabel.setText(UIHelper.getTrustOrPrivacyLabelOfProgressBar(item));
		}
		else { 
			seekLayout.setVisibility(View.GONE); 
		}
		switch (item.getMType()) {
		case LIVEPOST:
			LinearLayout ll = new LinearLayout(this);
			TextView label = new TextView(this);
			label.setText("Message:");
			label.setPadding(0, 0, 10, 0);
			label.setLayoutParams(new LayoutParams(seekLabel.getWidth(), LayoutParams.MATCH_PARENT));
			label.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
			EditText message = UIHelper.createEditText(this, InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, "message", 4, false);
			message.setText(((LivePostItem)item).getText());
			message.setId(R.livepost.message);
			message.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
			message.setGravity(Gravity.TOP);
			ll.addView(label);
			ll.addView(message);
			specificContainer.addView(ll);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.edit.button_save:
			try {
				if(name.getText().length() >0 && !item.getName().equals(name.getText().toString())) item.setName(name.getText().toString());
				switch (item.getMType()) {
				case LIVEPOST:
					EditText message = (EditText) findViewById(R.livepost.message);
					if(!((LivePostItem)item).getText().equals(message.getText().toString())){
						((LivePostItem)item).setText(message.getText().toString());
					}
					break;
				default: break;
				}
				AndroidModelHelper.updateGenItemAsyncronously(item, null, this, mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_edit_item_save));
				finish();
			} catch (Exception e) {
				Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
			}
			break;
		case R.edit.button_cancel:
			AndroidModelHelper.sendEvaluationDataAsynchronously(Arrays.asList((GenItem)item), mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_canceled));
			finish();
			break;
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		setChangesMade(true);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if(ModelHelper.isAgent(item.getMType())) {
			((AgentItem)item).setTrustLevel(AndroidModelHelper.normalizeTrustOrPrivacyLevelForDisplayableItem(seekBar.getProgress()));
		} else if(ModelHelper.isShareable(item.getMType())) {
			((ShareableItem)item).setPrivacyLevel(AndroidModelHelper.normalizeTrustOrPrivacyLevelForDisplayableItem(seekBar.getProgress()));
		}
		UIHelper.updateTrustOrPrivacyLevelTextView(this, seekText, item);
	}

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		startTask("");
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Edit_Item_Dialog> createLVH(Activity_Edit_Item_Dialog.this);
	}
	
	private void setChangesMade(boolean isChanged) {
		String string = "";
		int color = 0;
		if(isChanged) {
			string = "Changes not saved!";
			color = R.color.dm_col2;
		} else {
			string = "No changes made!";
			color = R.color.dm_col3;
		}
		headerChangesMade.setText(string);
		headerChangesMade.setTextColor(color);
	}

}