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

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import eu.dime.mobile.view.adapter.BaseAdapter_Image;
import eu.dime.model.GenItem;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.AgentItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.ResourceItem;
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
	private TextView seekHint;
	private Button saveButton;
	private View coloredBar;
	private List<ResourceItem> resources;

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
		seekHint = (TextView) findViewById(R.edit.bar_hint);
		coloredBar = findViewById(R.edit.coloredBar);
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

	@SuppressWarnings("unchecked")
	@Override
	protected void loadData() {
		item = (DisplayableItem) Model.getInstance().getItem(mrContext, dio.getItemType(), dio.getItemId());
		resources = (List<ResourceItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.RESOURCE);
	}

	@Override
	protected void initializeData() {
		ImageHelper.loadImageAsynchronously(headerImage, item, this);
		headerName.setText(item.getName());
		name.setText(item.getName());
		headerModified.setText(UIHelper.formatDateByMillis(item.getLastUpdated()));
		seekText.setText(UIHelper.getWarningText(item));
		Integer level = AndroidModelHelper.getTrustOrPrivacyLevelForDisplayableItem(item);
		if(!item.getMType().equals(TYPES.GROUP) && level != null) { 
			seek.setProgress(level);
			coloredBar.setBackgroundColor(UIHelper.getWarningColor(this, item));
			seekLabel.setText(UIHelper.getTrustOrPrivacyLabelOfProgressBar(item));
			seekHint.setText(UIHelper.updateEditTrustOrPrivacyLevelHint(this, item));
		} else { 
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
				AndroidModelHelper.updateGenItemAsynchronously(item, null, this, mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_edit_item_save));
				finish();
			} catch (Exception e) {
				Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
			}
			break;
		case R.edit.button_cancel:
			AndroidModelHelper.sendEvaluationDataAsynchronously(Arrays.asList((GenItem)item), mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_canceled));
			finish();
			break;
		case R.edit.button_upload_picture:
			// custom dialog
			final Dialog dialog = new Dialog(this);
			ListView list = new ListView(this);
			list.setBackgroundColor(getResources().getColor(android.R.color.white));
			BaseAdapter_Image adapter = new BaseAdapter_Image();
			List<String> exampleImages = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.example_images)));
			if(resources != null && resources.size() > 0) {
				for (ResourceItem ri : resources) {
					if(ri.getImageUrl().length() > 0) {
						exampleImages.add(ri.getImageUrl());
					}
				}
			}
			list.setAdapter(adapter);
			list.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String name = ((TextView)view.findViewById(R.id.name)).getText().toString();
					String url = ((TextView)view.findViewById(R.id.name)).getTag().toString();
					dialog.cancel();
					Toast.makeText(Activity_Edit_Item_Dialog.this, "using image " + name + "!", Toast.LENGTH_LONG).show();
					item.setImageUrl(url);
					ImageHelper.loadImageAsynchronously(headerImage, item, Activity_Edit_Item_Dialog.this);
				}
			});
			dialog.setContentView(list);
			dialog.setTitle("Select Image...");
			dialog.show();
			((TextView) dialog.findViewById(android.R.id.title)).setTextColor(Color.WHITE);
			adapter.init(this, exampleImages);
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
		seekText.setText(UIHelper.getWarningText(item));
		coloredBar.setBackgroundColor(UIHelper.getWarningColor(this, item));
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
			color = R.color.dm_text_highlighted_gold;
		} else {
			string = "No changes made!";
			color = R.color.dm_text_restrained;
		}
		headerChangesMade.setText(string);
		headerChangesMade.setTextColor(color);
	}

}