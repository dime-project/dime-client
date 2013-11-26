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
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import eu.dime.control.SilentLoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.Settings;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelConfiguration;
import eu.dime.model.specialitem.AuthItem;
import eu.dime.model.specialitem.UserItem;
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

public class Activity_Login extends Activity implements OnClickListener, OnEditorActionListener, TextWatcher {

	private static final int LOGIN_VIEW = 0;
	private static final int REGISTRATION_VIEW = 1;
	private Settings settings;
    private Spinner serverNameAndPort;
    private CheckBox isHttpsCheckBox;
    private ArrayAdapter<CharSequence> knownServersAdapter;
    protected ProgressDialog dialog;
    private boolean isLoginArea = true;
    //login area
    private LinearLayout loginArea;
    private EditText user;
    private EditText pass;
    private CheckBox remember;
    private Button buttonLogin;
    //register area
    private LinearLayout registerArea;
    private EditText registerUsername;
    private EditText registerPassword;
    private EditText registerPasswordRetype;
    private EditText registerEmail;
    private EditText registerNickname;
    private EditText registerFirstname;
    private EditText registerLastname;
    private CheckBox registerSET;
    private Button buttonRegister;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.login);
        serverNameAndPort = (Spinner) findViewById(R.login.select_server);
        isHttpsCheckBox = (CheckBox)findViewById(R.login.checkbox_is_https);
        loginArea = (LinearLayout) findViewById(R.login.login_area);
        user = (EditText) findViewById(R.login.editText_user);
        user.setOnEditorActionListener(this);
        user.addTextChangedListener(this);
        pass = (EditText) findViewById(R.login.editText_password);
        pass.setOnEditorActionListener(this);
        pass.addTextChangedListener(this);
        remember = (CheckBox) findViewById(R.login.checkBox_remember);
        buttonLogin = (Button) findViewById(R.login.button_login);
        registerArea = (LinearLayout) findViewById(R.login.register_area);
        registerUsername = (EditText) findViewById(R.login.editText_register_user);
        registerUsername.addTextChangedListener(this);
        registerPassword = (EditText) findViewById(R.login.editText_register_password);
        registerPassword.addTextChangedListener(this);
        registerPasswordRetype = (EditText) findViewById(R.login.editText_register_password_retype);
        registerPasswordRetype.addTextChangedListener(this);
        registerEmail = (EditText) findViewById(R.login.editText_register_email);
        registerEmail.addTextChangedListener(this);
        registerNickname = (EditText) findViewById(R.login.editText_register_public_nickname);
        registerNickname.addTextChangedListener(this);
        registerFirstname = (EditText) findViewById(R.login.editText_register_firstname);
        registerLastname = (EditText) findViewById(R.login.editText_register_lastname);
        registerSET = (CheckBox) findViewById(R.login.checkBox_agreeSET);
        buttonRegister = (Button) findViewById(R.login.button_register);
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
            	
            case R.login.button_register:
            	try {
					register();
				} catch (Exception ex) {
					showDialog(ex.getMessage());
				}
            	break;
            
            case R.login.dime_logo:
    			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.project_landing_page)));
    			startActivity(browserIntent);
            	break;
            	
            case R.login.login:
            	switchArea(LOGIN_VIEW);
            	break;
            	
            case R.login.register:
            	switchArea(REGISTRATION_VIEW);
            	break;
            	
            case R.settings.open_set_dialog:
            	builder = UIHelper.createAlertDialogBuilder(this, getString(R.string.settings_view_set_dialog_label), true);
            	builder.setMessage(R.string.settings_view_set_dialog_text);
            	UIHelper.displayAlertDialog(builder, false);
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
    
    private void register() throws DimeClientLoginException {
    	validateRegistrationValues();
    	final ServerAndPort sap = new ServerAndPort(serverNameAndPort.getSelectedItem().toString());
		UserItem userToRegister = ItemFactory.createNewUserItem(registerUsername.getText().toString(), 
						registerPassword.getText().toString(), 
						registerNickname.getText().toString(), 
						registerFirstname.getText().toString(), 
						registerLastname.getText().toString(), 
						registerSET.isChecked(), 
						registerEmail.getText().toString());
		final AsyncTask<Void, Void, String> registerTask = new MyRegisterAsyncTask(sap.serverName, sap.port, isHttpsCheckBox.isChecked(), userToRegister);
		dialog = ProgressDialog.show(this, null, "Trying to register...", true, true);
    	dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				registerTask.cancel(true);
				dialog.dismiss();
			}
		});
    	((TextView) dialog.findViewById(android.R.id.message)).setTextColor(Color.WHITE);
    	registerTask.execute();
	}
    
    private void validateRegistrationValues() throws DimeClientLoginException {
    	if(!UIHelper.containsOnlyUnicodeLettersOrDigits(registerUsername.getText().toString())) {
    		throw new DimeClientLoginException("Please provide a username, which contains only unicode letters or digits!");
    	}
    	if(!UIHelper.isValidEmail(registerEmail.getText().toString())) {
            throw new DimeClientLoginException("Please provide a valid email address!");
        }
	}
    
    private class MyRegisterAsyncTask extends AsyncTask<Void, Void, String> {
    	
        private String hostname;
        private int port;
        private UserItem userToRegister;
        private boolean https;
        
        public MyRegisterAsyncTask(String hostname, int port, boolean https, UserItem userToRegister){
        	this.hostname = hostname;
        	this.port = port;
        	this.userToRegister = userToRegister;
        	this.https = https;
        }
        
        @Override
        protected String doInBackground(Void... params) {
			String result = "";
			try {
				ModelConfiguration conf = RestApiAccess.registerNewUserCall(userToRegister, hostname, port, https);
				if (!new DimeHelper().dimeServerIsAuthenticated(conf.mainSAID, conf.restApiConfiguration)) {
					result = "Error occurred trying to register!";
				}
			} catch (Exception ex) {
				result = "Error occured trying to register: " + ex.getMessage() + "!";
			}
			return result;
        }

        @Override
        protected void onPostExecute(String result) {
    		if(result.length() == 0){
    			user.setText(userToRegister.getUsername());
    			pass.setText(userToRegister.getPassword());
    			switchArea(LOGIN_VIEW);
            	Toast.makeText(Activity_Login.this, "Registration successful for user " + userToRegister.getUsername(), Toast.LENGTH_LONG).show();
            	try {
                    login();
                } catch (DimeClientLoginException ex) {
                    showDialog(ex.getMessage());
                }
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
        	Log.d(Activity_Login.class.getSimpleName(), "Registration attempt canceled!");
        }
        
    }

	private void login() throws DimeClientLoginException {
        validateLoginValues();
        final ServerAndPort sap = new ServerAndPort(serverNameAndPort.getSelectedItem().toString());
    	final AsyncTask<Void, Void, String> loginTask = new MyLoginAsyncTask(sap.serverName,
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
    
    private void validateLoginValues() throws DimeClientLoginException {
        if (!(user.getText().toString().length() > 0)) {
            throw new DimeClientLoginException("Please provide a username!");
        }
        if (!(pass.getText().toString().length() > 0)) {
            throw new DimeClientLoginException("Please provide a password!");
        }
    }
    
    private class MyLoginAsyncTask extends AsyncTask<Void, Void, String> {
    	
        private String hostname;
        private String username; //== mainSAID
        private String password="";
        private int port;
        private boolean useHTTPS;
        private boolean loginPrefRemembered;
        
        public MyLoginAsyncTask(String hostname, String username, String password, int port, boolean useHTTPS, boolean loginPrefRemembered) {
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
			        	AndroidModelHelper.sendEvaluationDataAsynchronously(null, DimeClient.getMRC(new SilentLoadingViewHandler()), "action_login");
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
    
    public void switchArea(int areaCode) {
    	isLoginArea = (areaCode == LOGIN_VIEW);
    	registerArea.setVisibility(isLoginArea ? View.GONE : View.VISIBLE);
    	loginArea.setVisibility(isLoginArea ? View.VISIBLE : View.GONE);
    	int enabled = getResources().getColor(R.color.button_blue_top);
    	int disabled = getResources().getColor(android.R.color.white);
    	((TextView)findViewById(R.login.register)).setTextColor(isLoginArea ? disabled : enabled);
    	((TextView)findViewById(R.login.login)).setTextColor(isLoginArea ? enabled : disabled);
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
    
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(event != null && (event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_FORWARD)) {        	
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
    	if(isLoginArea) {
    		buttonLogin.setEnabled(!(user.getText().length() == 0 || pass.getText().length() == 0));
    	} else {
    		buttonRegister.setEnabled(!(((registerUsername.getText().length() == 0 || registerNickname.getText().length() == 0 || registerPassword.getText().length() == 0 || registerEmail.getText().length() == 0)) 
    				|| !registerPassword.getText().toString().equals(registerPasswordRetype.getText().toString())));	
    	}
    }

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

}
