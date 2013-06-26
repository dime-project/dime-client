package eu.dime.mobile.view.dialog;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.MandatorySettingNotSetException;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.AccountItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.displayable.ServiceAdapterItem;
import eu.dime.model.specialitem.AccountSettingsItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.AccountSettingsItem.ACCOUNT_SETTINGS_TYPES;

public class Activity_Account_Configuration_Dialog extends ActivityDime implements OnClickListener {
	
	public static final String ACCOUNT_GUID_TAG = "accountGuid";
	
	private ServiceAdapterItem serviceAdapter;
	private Button saveButton;
	private LinearLayout settingsContainer;
	private TextView serviceName;
	private TextView serviceDescription;
	private ImageView serviceImage;
	private String accountGuid;
	private AccountItem account;
	private List<AccountSettingsItem> oldSettings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = Activity_Account_Configuration_Dialog.class.getSimpleName();
		setContentView(R.layout.dialog_account_configuration);
		accountGuid = getIntent().getStringExtra(ACCOUNT_GUID_TAG);
		serviceImage = (ImageView) findViewById(R.account.service_icon);
		serviceName = (TextView) findViewById(R.account.service_name);
		serviceDescription = (TextView) findViewById(R.account.service_description);
		settingsContainer = (LinearLayout) findViewById(R.account.container_settings);
		saveButton = (Button) findViewById(R.account.button_save);
		saveButton.setOnClickListener(this);
		Button cancelButton = (Button) findViewById(R.account.button_cancel);
		cancelButton.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		startTask("Initializing configuration...");
	}

	@Override
	protected void loadData() {
		serviceAdapter = (ServiceAdapterItem) Model.getInstance().getItem(mrContext, TYPES.SERVICEADAPTER, dio.getItemId());
		account = (accountGuid == null) ? ItemFactory.createAccountItem(serviceAdapter.getName(), serviceAdapter.getGuid(), serviceAdapter.getImageUrl()) : (AccountItem) Model.getInstance().getItem(mrContext, TYPES.ACCOUNT, accountGuid);
		oldSettings = (accountGuid == null) ? serviceAdapter.getSettings() : account.getSettings();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initializeData() {
		serviceName.setText(serviceAdapter.getName());
		serviceDescription.setText(serviceAdapter.getDescription());
		ImageHelper.loadImageAsynchronously(serviceImage, serviceAdapter, this);
		settingsContainer.removeAllViews();
		for(AccountSettingsItem setting : oldSettings) {
			View view = null;
			if(setting.getType().equals(ACCOUNT_SETTINGS_TYPES.STRING.toString())) {
				view = UIHelper.createEditText(this, InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, setting.getName(), 1, false);
				EditText edit = (EditText) view;
				edit.setText(setting.getValue());
			} else if(setting.getType().equals(ACCOUNT_SETTINGS_TYPES.BOOLEAN.toString())) {
				view = new CheckBox(this);
				CheckBox checkBox = (CheckBox) view;
				checkBox.setText(setting.getName());
				checkBox.setChecked(setting.getValue().equals("true"));
			} else if(setting.getType().equals(ACCOUNT_SETTINGS_TYPES.PASSWORD.toString())) {
				view = UIHelper.createEditText(this, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, setting.getName(), 1, false);
				EditText edit = (EditText) view;
				edit.setText(setting.getValue());
			} else if(setting.getType().equals(ACCOUNT_SETTINGS_TYPES.LINK.toString())) {
				view = UIHelper.createTextView(this, R.style.default_theme, -1, -1, null, false);
				TextView text = (TextView) view;
				text.setText(Html.fromHtml("<a href='"+setting.getValue()+"'><u>Usage Terms</u></a>"));
				text.setLinksClickable(true);
		        text.setMovementMethod(LinkMovementMethod.getInstance());
			} else if(setting.getType().equals(ACCOUNT_SETTINGS_TYPES.ACCOUNT.toString())) {
				view = new Spinner(this);
				Spinner spinner = (Spinner) view;
				spinner.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f));
				List<String> stringProfiles = AndroidModelHelper.getListOfNamesOfDisplayableList((List<DisplayableItem>)(Object) ModelHelper.getAllValidProfilesForSharing(mrContext));
				CharSequence[] cs = stringProfiles.toArray(new CharSequence[stringProfiles.size()]);
				ArrayAdapter<CharSequence> profiles = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, cs);
				spinner.setAdapter(profiles);
			}
			if(view != null ) {
				view.setTag(setting);
				if(setting.isMandatory()) {
					LinearLayout ll = new LinearLayout(this);
					ll.setOrientation(LinearLayout.HORIZONTAL);
					TextView text = new TextView(this);
					text.setText("*");
					text.setPadding(10, 0, 0, 0);
					text.setTypeface(null, Typeface.BOLD);
					ll.addView(view);
					ll.addView(text);
					settingsContainer.addView(ll);
				} else {
					settingsContainer.addView(view);
				}
			} else {
				Log.d(TAG, "error creating settings fields");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        case R.account.button_save:
        	List<AccountSettingsItem> newSettings = new Vector<AccountSettingsItem>();
        	try {
	        	for(AccountSettingsItem setting : oldSettings) {
	        		String value = "";
        			if(setting.getType().equals(ACCOUNT_SETTINGS_TYPES.STRING.toString())) {
        				EditText edit = (EditText) settingsContainer.findViewWithTag(setting);
        				if(setting.isMandatory() && edit.getText().length() <= 0) {
        					throw new MandatorySettingNotSetException();
        				} else {
        					value = edit.getText().toString();
        				}
        			} else if(setting.getType().equals(ACCOUNT_SETTINGS_TYPES.BOOLEAN.toString())) {
        				CheckBox checkBox = (CheckBox) settingsContainer.findViewWithTag(setting);
        				if(setting.isMandatory() && !checkBox.isChecked()) {
        					throw new MandatorySettingNotSetException();
        				} else {
        					value = String.valueOf(checkBox.isChecked());
        				}
        			} else if(setting.getType().equals(ACCOUNT_SETTINGS_TYPES.PASSWORD.toString())) {
        				EditText edit = (EditText) settingsContainer.findViewWithTag(setting);
        				if(setting.isMandatory() && edit.getText().length() <= 0) {
        					throw new MandatorySettingNotSetException();
        				} else {
        					value = edit.getText().toString();
        				}
        			} else if(setting.getType().equals(ACCOUNT_SETTINGS_TYPES.ACCOUNT.toString())) {
        				Spinner spinner = (Spinner) settingsContainer.findViewWithTag(setting);
        				if(setting.isMandatory() && spinner.getSelectedItem().toString().length() <= 0) {
        					throw new MandatorySettingNotSetException();
        				} else {
        					value = ((ProfileItem) AndroidModelHelper.getDisplayableItemByName(this, mrContext, (List<DisplayableItem>) (Object) ModelHelper.getAllValidProfilesForSharing(mrContext), spinner.getSelectedItem().toString(), TYPES.PROFILE)).getServiceAccountId();
        				}
        			}
	    			setting.setValue(value);
	    			newSettings.add(setting);
	    		}
	        	account.setSettings(newSettings);
	        	if(accountGuid == null) {
	        		AndroidModelHelper.createGenItemAsyncronously(account, null, this, mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_account_configuration_save));
	        	} else {
	        		AndroidModelHelper.updateGenItemAsyncronously(account, null, this, mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_account_configuration_update));
	        	}
	        	finish();
        	} catch (MandatorySettingNotSetException ex) {
        		Toast.makeText(this, "Not all mandatory fields set! Please check again!", Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
			}
        	break;
        case R.account.button_cancel:
        	AndroidModelHelper.sendEvaluationDataAsynchronously(Arrays.asList((GenItem)account), mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_canceled));
        	finish();
        	break;
		}
	}
	
	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(item.getElement().getType().equalsIgnoreCase(TYPES.GROUP.toString())){
    		startTask("");
    	}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Account_Configuration_Dialog>createLVH(Activity_Account_Configuration_Dialog.this);
	}

}
