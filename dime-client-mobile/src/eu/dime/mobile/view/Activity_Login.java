package eu.dime.mobile.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.Settings;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.model.Model;
import eu.dime.model.specialitem.AuthItem;
import eu.dime.model.storage.InitStorageFailedException;
import eu.dime.restapi.DimeHelper;
import eu.dime.restapi.RestApiAccess;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.web.client.HttpHelper;

public class Activity_Login extends Activity implements OnClickListener, OnEditorActionListener, TextWatcher, OnCheckedChangeListener {

	private Settings settings;
    private CheckBox remember;
    private EditText user;
    private EditText pass;
    private Button login;
    private Spinner serverNameAndPort;
    private CheckBox isHttpsCheckBox;
    private ArrayAdapter<CharSequence> knownServersAdapter;
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
        remember = (CheckBox) findViewById(R.login.checkBox_remember);
        login = (Button) findViewById(R.login.button_login);
        serverNameAndPort = (Spinner) findViewById(R.login.select_server);
        isHttpsCheckBox = (CheckBox)findViewById(R.login.checkbox_is_https);
        ImageButton exit = (ImageButton) findViewById(R.id.exit);
        exit.setOnClickListener(this);
        TextView register = (TextView) findViewById(R.login.register);
        register.setOnClickListener(this);
        login.setOnClickListener(this);
        login.setEnabled(false);
        pass.setOnEditorActionListener(this);
        pass.addTextChangedListener(this);
        user.setOnEditorActionListener(this);
        user.addTextChangedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Restore preferences
        settings = DimeClient.getSettings();
        updateServers();
        remember.setChecked(settings.isLoginPrefRemembered());
        user.setText(settings.getUsername());
        isHttpsCheckBox.setChecked(settings.isUseHTTPS());
        if (settings.isLoginPrefRemembered()) {
        	pass.setText(settings.getPassword());
            try {
                login();
            } catch (DimeClientLoginException ex) {
                showDialog(ex.getMessage());
            }
         }
    }
    
    private void updateServers() {
    	String[] predefindesServers = getResources().getStringArray(R.array.known_dime_servers);
        List<String> ownServers = settings.getOwnServers();
        List<String> allservers = new ArrayList<String>();
        Collections.addAll(allservers, predefindesServers);
        allservers.addAll(ownServers);
        knownServersAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, allservers.toArray(new CharSequence[allservers.size()]));
        knownServersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serverNameAndPort.setAdapter(knownServersAdapter);
        serverNameAndPort.setSelection(knownServersAdapter.getPosition(settings.getHostname() + ":" + settings.getPort()));
    }

	@Override
    public void onClick(View v) {
		Builder builder;
        switch (v.getId()) {
        	case R.login.add_server:
        		LinearLayout container = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_add_own_server, null);
        		final EditText hostname = (EditText) container.findViewById(R.ownserver.hostname);
        		final EditText port = (EditText) container.findViewById(R.ownserver.port);
        		builder = UIHelper.createCustomAlertDialogBuilder(this, "Add own server", true, container);
        		builder.setPositiveButton("add", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int tmpPort = port.getText().toString().length() > 0 ? Integer.valueOf(port.getText().toString()) : DimeHelper.DEFAULT_PORT;
						settings.addOwnServer(hostname.getText().toString(), tmpPort);
						updateServers();
						Toast.makeText(Activity_Login.this, hostname.getText().toString() + ":" + tmpPort + " added to list!", Toast.LENGTH_LONG).show();
					}
				});
        		AlertDialog addDialog = UIHelper.displayAlertDialog(builder, true);
        		final Button addButton = addDialog.getButton(ProgressDialog.BUTTON_POSITIVE);
        		addButton.setEnabled(false);
        		hostname.addTextChangedListener(new TextWatcher() {
					@Override
					public void afterTextChanged(Editable hostname) {
						addButton.setEnabled((hostname.length() > 0));
					}
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
					
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) { }
				});
        		break;
        	case R.login.remove_server:
        		final CharSequence[] ownServers = settings.getOwnServers().toArray(new CharSequence[settings.getOwnServers().size()]);
        		if(ownServers.length > 0) {
	        		builder = UIHelper.createAlertDialogBuilder(this, "Remove own server", true);
	        		builder.setSingleChoiceItems(ownServers, 1, new DialogInterface.OnClickListener() {
	        			@Override
	        			public void onClick(DialogInterface dialog, int which) {
	        				settings.removeOwnServer((String) ownServers[which]);
							updateServers();
							dialog.dismiss();
	        			}
	        		});
	        		builder.show();
        		} else {
        			Toast.makeText(this, "No servers to remove...", Toast.LENGTH_SHORT).show();
        		}
        		break;
            case R.login.button_login:
            	try {
                    login();
                } catch (DimeClientLoginException ex) {
                    showDialog(ex.getMessage());
                }
            	break;
            
            case R.id.exit:
            	startActivity(new Intent(Activity_Login.this, Activity_Shutdown.class));
            	finish();
                break;
            
            case R.login.register:
    			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.project_landing_page)));
    			startActivity(browserIntent);
            	break;
        }
    }

    class ServerAndPort{
        String serverName;
        int port = DimeHelper.DEFAULT_PORT;

        public ServerAndPort(String serverAndPort) throws DimeClientLoginException {
            String[] input = serverAndPort.split(":");
            if (input.length == 0 || input.length > 2){
                throw new DimeClientLoginException("Invalid server entry: " + serverAndPort);
            }
            this.serverName = input[0];
            if (input.length > 1){
                try {
                    this.port = Integer.parseInt(input[1]);
                } catch (NumberFormatException ex){
                    throw new DimeClientLoginException("Invalid port in server entry: " + serverAndPort);
                }
            }
        }
    }

    private void validateUserNamePasswordGiven() throws DimeClientLoginException{
        if (!(user.getText().toString().length() > 0)) {
            throw new DimeClientLoginException("Please provide a username");
        } else if (!(pass.getText().toString().length() > 0)) {
            throw new DimeClientLoginException("Please provide a password");
        }
    }
    
    private void login() throws DimeClientLoginException {
        validateUserNamePasswordGiven();
        final ServerAndPort sap = new ServerAndPort(serverNameAndPort.getSelectedItem().toString());
    	final AsyncTask<Void, Void, String> loginTask = new MyAsyncTask(sap.serverName,
				user.getText().toString(), 
				pass.getText().toString(),
				sap.port,
				isHttpsCheckBox.isChecked() ,
				remember.isChecked());
    	dialog = ProgressDialog.show(this, null, "Trying to login...", true, true);
    	dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				loginTask.cancel(true);
				dialog.dismiss();
			}
		});
    	((TextView) dialog.findViewById(android.R.id.message)).setTextColor(Color.WHITE);
    	loginTask.execute();
    }
    
    private class MyAsyncTask extends AsyncTask<Void, Void, String> {
    	
        private String hostname;
        private String username; //== mainSAID
        private String password="";
        private int port;
        private boolean useHTTPS;
        private boolean loginPrefRemembered;
        
        public MyAsyncTask(String hostname, String username, String password, int port, boolean useHTTPS, boolean loginPrefRemembered) {
            this.hostname = hostname;
            this.username = username; //== mainSAID
            this.password = password;
            this.port = port;
            this.useHTTPS = useHTTPS;
            this.loginPrefRemembered = loginPrefRemembered;
        }
		
		@Override
        protected String doInBackground(Void... params) {
			String result = "";
			try {
				String myHostName =  hostname;
	        	settings.updateSettingsBeforeLogin(myHostName, username, password, port, useHTTPS, loginPrefRemembered);
		        if(!new DimeHelper().dimeServerIsAuthenticated(username, settings.getRestApiConfiguration())) {
		            result = "Could not login because the password was incorrect!";
		        } else {
		        	String clientVersion = DimeClient.getClientVersion();
		        	String serverVersion = new DimeHelper().getServerVersion(settings.getRestApiConfiguration());
		        	AuthItem auth = RestApiAccess.getAuthItem(username, settings.getRestApiConfiguration());
		        	if(auth != null) {
			        	settings.updateSettingsAfterLogin(clientVersion, serverVersion, auth);
			        	Logger.getLogger(DimeClient.class.getName()).log(Level.INFO, "updateClientConfiguration - start" + settings.getModelConfiguration().toString());
			        	Model.getInstance().updateSettings(settings.getModelConfiguration());
			        	Logger.getLogger(DimeClient.class.getName()).log(Level.INFO, "updateClientConfiguration - finished");
			        	DimeClient.toggleContextCrawler();
			        	AndroidModelHelper.sendEvaluationDataAsynchronously(null, DimeClient.getMRC(new DummyLoadingViewHandler()), "action_login");
		        	} else {
		        		result = "Could not load auth item!";
		        	}
		        }
		    } catch (UnknownHostException ex) {
		    	settings.updateSettingsBeforeLogin(hostname, username, password, port, useHTTPS, loginPrefRemembered);
                Logger.getLogger(DimeClient.class.getName()).log(Level.SEVERE, "Unable to resolve hostname for said:" + username);
                result = "Unable to resolve hostname for said:" + settings.getMainSAID();
            } catch (MalformedURLException ex) {
		        Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, "MalformedURLException:" + ex.getMessage());
		        result = "Private service not reachable! Url seems not to be valid!";
		    } catch (ProtocolException ex) {
		        Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, "ProtocolException:" + ex.getMessage());
		        result = "Private service not reachable! Problems with the protocol!";
		    } catch (IOException ex) {
		        Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, "IOException:" + ex.getMessage());
		        result = "Private service not reachable! Timeout reached!";
		    } catch (InitStorageFailedException ex) {
		    	Logger.getLogger(DimeClient.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		    	result = "Could not update model configuration. Login aborted!";
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
    		if(dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
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
            try {
                login();
            } catch (DimeClientLoginException ex) {
                showDialog(ex.getMessage());
            }            
        }
        return true;
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
		if ((!(user.getText().toString().length() > 0)) || (!(pass.getText().toString().length() > 0))) {
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
