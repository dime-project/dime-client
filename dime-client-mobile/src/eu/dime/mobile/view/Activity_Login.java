package eu.dime.mobile.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.settings.Settings;
import eu.dime.restapi.DimeHelper;
import eu.dime.restapi.RestApiConfiguration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.web.client.HttpHelper;

public class Activity_Login extends Activity implements OnClickListener, OnEditorActionListener, TextWatcher, OnCheckedChangeListener {

	private Settings settings;
	private CheckBox accept;
    private CheckBox remember;
    private EditText user;
    private EditText pass;
    private TextView acceptText;
    private Button login;
    private ImageView dimeLogo;
    private EditText serverIP;
    private EditText portEditText;
    private CheckBox isHttpsCheckBox;
    private TextView serverLabel;
    private TextView portLabel;
    private TextView isHttpsLabel;
    protected ProgressDialog dialog;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.login);
        user = (EditText) findViewById(R.login.editText_user);
        pass = (EditText) findViewById(R.login.editText_password);
        accept = (CheckBox) findViewById(R.login.checkBox_accept);
        acceptText = (TextView) findViewById(R.login.textView_acceptText);
        remember = (CheckBox) findViewById(R.login.checkBox_remember);
        login = (Button) findViewById(R.login.button_login);
        serverLabel = (TextView) findViewById(R.login.label_edittext_server);
        portLabel = (TextView) findViewById(R.login.label_view_port_txt);
        isHttpsLabel = (TextView) findViewById(R.login.label_is_https_txt);
        serverIP = (EditText) findViewById(R.login.editText_server);
        portEditText = (EditText) findViewById(R.login.editText_port);
        isHttpsCheckBox = (CheckBox)findViewById(R.login.checkbox_is_https);
        dimeLogo = (ImageView) findViewById(R.login.dime_logo);
        ImageButton exit = (ImageButton) findViewById(R.id.exit);
        exit.setOnClickListener(this);
        TextView register = (TextView) findViewById(R.login.register);
        register.setOnClickListener(this);
        acceptText.setText(Html.fromHtml(getString(R.string.acceptTermsAndConditions)));
        acceptText.setMovementMethod(LinkMovementMethod.getInstance());
        login.setOnClickListener(this);
        login.setEnabled(false);
        pass.setOnEditorActionListener(this);
        pass.addTextChangedListener(this);
        user.setOnEditorActionListener(this);
        user.addTextChangedListener(this);
        accept.setOnCheckedChangeListener(this);
        serverIP.setOnEditorActionListener(this); 
        portEditText.setOnEditorActionListener(this); 
        dimeLogo.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                settings = DimeClient.getSettings();
                settings.setOverrideDNS(!settings.getOverrideDNS());
                updateHiddenFields();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Restore preferences
        settings = DimeClient.getSettings();
        accept.setChecked(settings.isLoginPrefAccepted());
        remember.setChecked(settings.isLoginPrefRemembered());
        user.setText(settings.getUsername());
        pass.setText(settings.getPassword());
        serverIP.setText(settings.getHostname());
        updateHiddenFields();
        if (settings.isLoginPrefRemembered()) {
            login();
         }
    }

	@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.login.button_login:
            	login();
            	break;
            
            case R.id.exit:
            	startActivity(new Intent(Activity_Login.this, Activity_Shutdown.class));
            	finish();
                break;
            
            case R.login.register:
            	String myPath = settings.isUseHTTPS() ? "https://" : "http://";
                myPath += settings.getHostname() + ":" + settings.getPort();
                myPath += "/dime-communications/static/ui/dime/register.html";
            	String urlString = myPath;
    			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
    			startActivity(browserIntent);
            	break;
        }
    }
    
    private void login() {
    	dialog = ProgressDialog.show(this, null, "Trying to login...", true, true);
    	if(settings.getOverrideDNS()) {
            settings.setHostname(serverIP.getText().toString());
            try {
                settings.setPort(Integer.parseInt(portEditText.getText().toString()));
            } catch (NumberFormatException ex){
                Logger.getLogger(Activity_Login.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
            settings.setUseHTTPS(isHttpsCheckBox.isChecked());
        } else {
            settings.setPort(DimeHelper.DEFAULT_PORT);
            settings.setUseHTTPS(DimeHelper.DEFAULT_USE_HTTPS);
        }
        settings.setUserNamePassword(user.getText().toString(), pass.getText().toString(), accept.isChecked(), remember.isChecked());
    	final AsyncTask<Void, Void, String> loginTask = new MyAsyncTak().execute();
    	dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				loginTask.cancel(true);
				dialog.dismiss();
			}
		});
    	((TextView) dialog.findViewById(android.R.id.message)).setTextColor(Color.WHITE);
    }
    
    private class MyAsyncTak extends AsyncTask<Void, Void, String> {
		
		@Override
        protected String doInBackground(Void... params) {
			String result = "";
			try {
		        if(!new DimeHelper().dimeServerIsAuthenticated(settings.getMainSAID(), new RestApiConfiguration(settings.getHostname(), settings.getPort(), settings.isUseHTTPS(), settings.getAuthToken()))) {
		            result = "Could not login because the password was incorrect!";
		        } else {
		        	AndroidModelHelper.updateClientConfiguration(settings);
		        	DimeClient.toggleContextCrawler();
		        }
		    } catch (MalformedURLException ex) {
		        Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, "MalformedURLException:" + ex.getMessage());
		        result = "Private service not reachable! Url seems not to be valid!";
		    } catch (ProtocolException ex) {
		        Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, "ProtocolException:" + ex.getMessage());
		        result = "Private service not reachable! Problems with the protocol!";
		    } catch (IOException ex) {
		        Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, "IOException:" + ex.getMessage());
		        result = "Private service not reachable! Timeout reached!";
		    }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
    		if(result.length() == 0){
                Intent myIntent = new Intent(Activity_Login.this, Activity_Main.class);
                startActivity(DimeIntentObjectHelper.populateIntent(myIntent, new DimeIntentObject()));
        	} else {
        		showDialog(result);
        	}
    		if(dialog != null && dialog.isShowing()) dialog.dismiss();
        }
        
        @Override
        protected void onCancelled() {
        	super.onCancelled();
        	Log.d(Activity_Login.class.getSimpleName(), "Login attempt canceled!");
        }
        
    }
    
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
        	if (!accept.isChecked()) {
                showDialog("Please accept the terms and conditions");
            } else if (!(user.getText().toString().length() > 0)) {
                showDialog("Please provide a username");
            } else if (!(pass.getText().toString().length() > 0)) {
                showDialog("Please provide a password");
            } else {
            	login();
            }
        }
        return true;
    }

    private void updateHiddenFields() {
        int viewState = settings.getOverrideDNS() ? View.VISIBLE : View.GONE;
        serverIP.setVisibility(viewState);
        serverLabel.setVisibility(viewState);
        portLabel.setVisibility(viewState);
        portEditText.setVisibility(viewState);
        isHttpsLabel.setVisibility(viewState);
        isHttpsCheckBox.setVisibility(viewState);
        serverIP.setText(settings.getHostname());
        portEditText.setText(settings.getPort()+"");
        isHttpsCheckBox.setChecked(settings.isUseHTTPS());
    }
    
    @Override
    public void afterTextChanged(Editable s) {
    	login.setEnabled(loginAttemptPossible());
    }

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		login.setEnabled(loginAttemptPossible());
	}
	
	private boolean loginAttemptPossible(){
		boolean possible = true;
		if (!accept.isChecked() || (!(user.getText().toString().length() > 0)) || (!(pass.getText().toString().length() > 0))) {
            possible = false;
        }
		return possible;
	}
	
    private void showDialog(final String message) {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog alert = UIHelper.createInfoDialog(Activity_Login.this, message, "ok");
		        alert.show();
			}
		});
    }

}
