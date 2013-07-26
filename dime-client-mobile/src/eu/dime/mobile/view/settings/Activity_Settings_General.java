package eu.dime.mobile.view.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.Settings;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.model.Model;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.restapi.RestApiAccess;

public class Activity_Settings_General extends ActivityDime implements OnClickListener {
	
	private Settings settings = null;
	private ToggleButton contextCrawler;
	private ToggleButton setButton;
	boolean configurationChanged = false;
	boolean passwordChanged = false;

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
    	if(setButton.isChecked() != settings.isSetPrefAccepted()) {
    		settings.getAuthItem().setEvaluationDataCapturingEnabled(setButton.isChecked());
    		configurationChanged = true;
    	}
    	if(configurationChanged) {
    		(new AsyncTask<Void, Void, String>() {	
    			@Override
    			protected String doInBackground(Void... params) {
    				boolean result = RestApiAccess.postAuthItem(mrContext.hoster, settings.getAuthItem(), settings.getRestApiConfiguration());
    				String message = "Settings successfully updated!";
    				if(result) {
        				configurationChanged = false;
        				if(passwordChanged) {
	    					settings.setPassword(settings.getAuthItem().getPassword());
							Model.getInstance().updatePassword(settings.getPassword());
							passwordChanged = false;
							message += " Password set!";
        				}
    				} else {
    					message = "Error occurred updating auth item! Please try again...";
    				}

					return message;
    			}
    			@Override
    			protected void onPostExecute(String result) {
    				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
    			}
    		}).execute();
    	}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.clearCredentialsBtn:
            settings.setLoginPrefRemembered(false);
            break;
        case R.id.settings_view_change_pw_btn:
            View layout = getLayoutInflater().inflate(R.layout.dialog_change_pass, null);
            final EditText old_pw = (EditText) layout.findViewById(R.changePass.editText_oldpw);
            final EditText new_pw = (EditText) layout.findViewById(R.changePass.editText_newpw);
            final EditText new_pw_re = (EditText) layout.findViewById(R.changePass.editText_newpw_retype);
            AlertDialog.Builder builderPassword = UIHelper.createCustomAlertDialogBuilder(this, "Change password for " + settings.getUsername(), true, layout);
            builderPassword.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (!settings.getPassword().equals(old_pw.getText().toString())) {
						Toast.makeText(Activity_Settings_General.this, "old password is wrong, please try again", Toast.LENGTH_LONG).show();
                    } else if(!new_pw.getText().toString().equals(new_pw_re.getText().toString())) {
                    	Toast.makeText(Activity_Settings_General.this, "new passwords are different: please try again!", Toast.LENGTH_LONG).show();
                    } else {
                        settings.getAuthItem().setPassword(new_pw.getText().toString());
                        passwordChanged = true;
                    }
				}
			});
            UIHelper.displayAlertDialog(builderPassword, true);
            break;
        case R.settings.open_set_dialog:
        	AlertDialog.Builder builder = UIHelper.createAlertDialogBuilder(this, getString(R.string.settings_view_set_dialog_label), true);
        	builder.setMessage(R.string.settings_view_set_dialog_text);
        	UIHelper.displayAlertDialog(builder, false);
        	break;
        }
    }

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) { 
		if (item.getOperation().equals(NotificationItem.OPERATION_CREATE) && item.getElement().getType().equals("auth")) {
			startTask("");
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Settings_General>createLVH(Activity_Settings_General.this);
	}
	
}
