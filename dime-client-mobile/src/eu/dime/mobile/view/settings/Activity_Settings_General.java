package eu.dime.mobile.view.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.settings.Settings;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.model.ModelHelper;
import eu.dime.model.auth.AuthException;
import eu.dime.model.auth.WrongPasswordException;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.restapi.DimeHelper;

public class Activity_Settings_General extends ActivityDime {
	
	private Settings settings = null;
	private ToggleButton contextCrawler;
	private ToggleButton setButton;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = Activity_Settings_General.class.getSimpleName();
        setContentView(R.layout.settings);
        contextCrawler = (ToggleButton) findViewById(R.id.settings_view_toggleButton_location_services);
        setButton = (ToggleButton) findViewById(R.settings.toggleButton_set);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTask("");
    }
    
    @Override
    protected void loadData() { 
    	settings = DimeClient.getSettings();
    }

    @Override
    protected void initializeData() {
        contextCrawler.setChecked(settings.isCrawlingContext());
        setButton.setChecked(settings.isSetPrefAccepted());
    }

    @Override
    public void onPause() {
    	super.onPause();
    	if(contextCrawler.isChecked() != settings.isCrawlingContext()) {
	        settings.setCrawlingContext(contextCrawler.isChecked());
	        DimeClient.toggleContextCrawler();
        }
        settings.setSetPrefAccepted(setButton.isChecked());
    }

    public void clickHandler(View v) {
        switch (v.getId()) {
        case R.id.settings_view_info:
        	View infoDialog = getLayoutInflater().inflate(R.layout.dialog_client_info, null);
        	EditText hostName = (EditText) infoDialog.findViewById(R.id.settings_view_ip_address);
        	EditText port = (EditText) infoDialog.findViewById(R.id.settings_view_port);
        	TextView version = (TextView) infoDialog.findViewById(R.id.settings_view_client_version);
        	CheckBox isHttps = (CheckBox) infoDialog.findViewById(R.id.settings_view_is_https);
        	hostName.setText(settings.getHostname());
            port.setText(settings.getPort() + "");
            isHttps.setChecked(settings.isUseHTTPS());
            version.setText(AndroidModelHelper.getVersion(new DimeHelper().dimeServerIsAlive(DimeClient.getUserMainSaid(), DimeClient.getModelConfiguration().restApiConfiguration)));
            AlertDialog.Builder builderInfo = UIHelper.createCustomAlertDialogBuilder(this, "Di.me info", false, infoDialog);
            builderInfo.setPositiveButton("Ok", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
            });
            UIHelper.displayAlertDialog(builderInfo, false);
        	break;
        case R.id.settings_view_terms:
        	//TODO open terms and conditions
        	break;
        case R.id.settings_view_howto:
        	//TODO open how to page
        	break;
        case R.id.clearCredentialsBtn:
            settings.setPassword("");
            break;
        case R.id.settings_view_change_pw_btn:
            View layout = getLayoutInflater().inflate(R.layout.dialog_change_pass, null);
            final EditText old_pw = (EditText) layout.findViewById(R.changePass.editText_oldpw);
            final EditText new_pw = (EditText) layout.findViewById(R.changePass.editText_newpw);
            final EditText new_pw_re = (EditText) layout.findViewById(R.changePass.editText_newpw_retype);
            AlertDialog.Builder builderPassword = UIHelper.createCustomAlertDialogBuilder(this, "Change password for " + settings.getUsername(), true, layout);
            builderPassword.setPositiveButton("Ok", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (!new_pw.getText().toString().equals(new_pw_re.getText().toString())) {
                        Toast.makeText(Activity_Settings_General.this, "new passwords are different: please try again!", Toast.LENGTH_LONG).show();
                    } else {
	                    try {
	                        if (ModelHelper.changePassword(old_pw.getText() + "", new_pw.getText() + "", mrContext)) {
	                            Toast.makeText(Activity_Settings_General.this, "success", Toast.LENGTH_LONG).show();
	                            DimeClient.getSettings().setPassword(new_pw.getText() + "");
	                            dialog.dismiss();
	                        }
	                    } catch (WrongPasswordException ex) {
	                        Toast.makeText(Activity_Settings_General.this, "wrong old password: please try again", Toast.LENGTH_LONG).show();

	                    } catch (AuthException ex) {
	                        Toast.makeText(Activity_Settings_General.this, "cannot connect to server - abort", Toast.LENGTH_LONG).show();
	                        dialog.dismiss();
	                    }
                    }
				}
			});
            UIHelper.displayAlertDialog(builderPassword, true);
            break;
        }
    }

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) { }

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Settings_General>createLVH(Activity_Settings_General.this);
	}
	
}
