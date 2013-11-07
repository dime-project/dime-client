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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import eu.dime.control.SilentLoadingViewHandler;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.Settings;
import eu.dime.mobile.crawler.Factory;
import eu.dime.mobile.crawler.data.Place;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ContextHelper;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.mobile.view.communication.TabActivity_Communication;
import eu.dime.mobile.view.data.TabActivity_Data;
import eu.dime.mobile.view.myprofile.TabActivity_Myprofile;
import eu.dime.mobile.view.notifications.TabActivity_Notifications;
import eu.dime.mobile.view.people.TabActivity_People;
import eu.dime.mobile.view.places.TabActivity_Place;
import eu.dime.mobile.view.settings.TabActivity_Settings;
import eu.dime.mobile.view.situations.TabActivity_Situations;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.PlaceItem;
import eu.dime.model.displayable.SituationItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;
import eu.dime.restapi.DimeHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Activity_Main extends ActivityDime implements OnClickListener, OnLongClickListener {

    private Button placeButton;
    private Button situationButton;
    private Button notificationButton;
    private List<SituationItem> situations;
    private List<UserNotificationItem> notifications = null;
    private PlaceItem currentPlace;
    private ImageView dimeLogo;
    protected Dialog actionDialog = null;
    private TextView dimeUser;
    private boolean isPlaceAdapterConnected = false;
    private boolean isDimeServerAlive = false;
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = Activity_Main.class.getSimpleName();
        setContentView(R.layout.main);
        dimeUser = (TextView) findViewById(R.main.dime_user);
        placeButton = (Button) findViewById(R.main.button_place);
        situationButton = (Button) findViewById(R.main.button_situation);
        notificationButton = (Button) findViewById(R.main.button_notification);
        dimeLogo = (ImageView) findViewById(R.main.dime_logo);
        dimeLogo.setOnClickListener(this);
        placeButton.setOnClickListener(this);
        situationButton.setOnClickListener(this);
        situationButton.setOnLongClickListener(this);
        notificationButton.setOnClickListener(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        try {
	        DimeClient.addStringToViewStack(TAG.substring(9)); //remove Activity_
	        dimeUser.setText(DimeClient.getUserMainSaid());
        	startTask("");
        } catch (Exception e) {	
			finish();
		}
    }
    
	@Override
    @SuppressWarnings("unchecked")
    protected void loadData() {
		isDimeServerAlive = new DimeHelper().dimeServerIsAlive(DimeClient.getSettings().getRestApiConfiguration());
		if(isDimeServerAlive) {
	        situations = (List<SituationItem>) (Object) Model.getInstance().getAllItems(DimeClient.getMRC(dio.getOwnerId(), new SilentLoadingViewHandler()), TYPES.SITUATION);
	        notifications = (List<UserNotificationItem>) (Object) Model.getInstance().getAllItems(DimeClient.getMRC(dio.getOwnerId(), new SilentLoadingViewHandler()), TYPES.USERNOTIFICATION);
	        isPlaceAdapterConnected = ModelHelper.isPlaceAdapterConnected(DimeClient.getMRC(dio.getOwnerId(), new SilentLoadingViewHandler()));
	        if(isPlaceAdapterConnected) {
		        Place placeTmp = ContextHelper.getCurrentPlace();
		        if (placeTmp != null) {
		            currentPlace = (PlaceItem) Model.getInstance().getItem(DimeClient.getMRC(dio.getOwnerId(), new SilentLoadingViewHandler()), TYPES.PLACE, placeTmp.getPlaceId());
		        } else {
		        	currentPlace = null;
		        }
	        }
		}
    }

    @Override
    protected void initializeData() {
    	if(isDimeServerAlive) {
	    	initializePlaces();
	        initializeSituations();
	        initializeNotifications();
    	} else {
    		Toast.makeText(getApplicationContext(), "Could not reach server! Working in offline mode...", Toast.LENGTH_SHORT).show();
    	}
    }
    
    private void initializeSituations() {
    	int sitCount = 0;
        String currentSit = null;
        if(situations != null){
	        for (SituationItem sit : situations) {
	            if (sit.isActive()) {
	                sitCount++;
	                if (sitCount == 1) {
	                    currentSit = sit.getName();
	                }
	            }
	        }
        }
        if (sitCount == 0) {
            situationButton.setText("situation unknown");
        } else {
            currentSit = currentSit.concat(" [");
            String sitCountString = Integer.toString(sitCount);
            currentSit = currentSit.concat(sitCountString);
            currentSit = currentSit.concat("]");
            situationButton.setText(currentSit);
        }
    }
    
    private void initializeNotifications() {
    	notificationButton.setText("notifications");
        String currentNoti = "notifications";
        List<UserNotificationItem> unread = new ArrayList<UserNotificationItem>();
        if(notifications != null) {
	        for (UserNotificationItem userNotification : notifications) {
				if(!userNotification.isRead()){
					unread.add(userNotification);
				}
			}
	        if (unread.size() > 0) {
	            currentNoti = currentNoti.concat(" [");
	            String notiCountString = Integer.toString(notifications.size());
	            currentNoti = currentNoti.concat(notiCountString);
	            currentNoti = currentNoti.concat("]");
	            notificationButton.setText(currentNoti);
	        }
        }
    }

    private void initializePlaces() {
    	if(isPlaceAdapterConnected) {
    		placeButton.setVisibility(View.VISIBLE);
    		if (currentPlace != null && currentPlace.getName().length() > 0) {
    			String originalPlaceName;
                String writtenPlaceName;
                originalPlaceName = currentPlace.getName();
                if (originalPlaceName.length() > 19) {

                    writtenPlaceName = originalPlaceName.substring(0, 18);
                    writtenPlaceName = writtenPlaceName.concat("...");
                } else {
                    writtenPlaceName = originalPlaceName;
                }
                placeButton.setText(writtenPlaceName);
            } else {
                placeButton.setText("place unknown");
            }
    	} else {
    		placeButton.setVisibility(View.GONE);
    	}
	}

	@Override
    public boolean onLongClick(View v) {
        String sitListMessage = "";
        int sitCount = 0;
        AlertDialog.Builder sitList = UIHelper.createAlertDialogBuilder(this, "Situations list", false);
        for (SituationItem sit : situations) {
            if (sit.isActive()) {
                sitCount++;
                sitListMessage = sitListMessage.concat(sit.getName());
                sitListMessage = sitListMessage.concat("\n");
            }
        }
        sitList.setMessage((sitCount == 0) ? "Situation unknown" : sitListMessage);
        sitList.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            }
        });
        UIHelper.displayAlertDialog(sitList, false);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent myIntent = null;
        switch (v.getId()) {
            case R.main.button_people:
                myIntent = new Intent(Activity_Main.this, TabActivity_People.class);
                this.startActivity(DimeIntentObjectHelper.populateIntent(myIntent, dio));
                break;

            case R.main.button_myprofile:
                myIntent = new Intent(Activity_Main.this, TabActivity_Myprofile.class);
                this.startActivity(DimeIntentObjectHelper.populateIntent(myIntent, dio));
                break;

            case R.main.button_communication:
                myIntent = new Intent(Activity_Main.this, TabActivity_Communication.class);
                this.startActivity(DimeIntentObjectHelper.populateIntent(myIntent, dio));
                break;

            case R.main.button_mydata:
                myIntent = new Intent(Activity_Main.this, TabActivity_Data.class);
                this.startActivity(DimeIntentObjectHelper.populateIntent(myIntent, dio));
                break;

            case R.main.button_settings:
                myIntent = new Intent(Activity_Main.this, TabActivity_Settings.class);
                this.startActivity(DimeIntentObjectHelper.populateIntent(myIntent, dio));
                break;

            case R.main.button_place:
                myIntent = new Intent(Activity_Main.this, TabActivity_Place.class);
                this.startActivity(DimeIntentObjectHelper.populateIntent(myIntent, dio));
                break;

            case R.main.button_situation:
                myIntent = new Intent(Activity_Main.this, TabActivity_Situations.class);
                this.startActivity(DimeIntentObjectHelper.populateIntent(myIntent, dio));
                break;

            case R.main.button_notification:
                myIntent = new Intent(Activity_Main.this, TabActivity_Notifications.class);
                this.startActivity(DimeIntentObjectHelper.populateIntent(myIntent, dio));
                break;
            
            case R.main.logout:
            	createActionMenu();
            	break;
            	
//            case R.main.button_calendar:
//            	Log.d(TAG, "calendarBtn pressed!");
//            	//TODO add calendar activity
//            	break;
                
            case R.main.dime_logo:
            	eu.dime.mobile.helper.AndroidModelHelper.clearCache(TAG);
            	break;
            	
            case R.main.info:
            	Settings settings = DimeClient.getSettings();
            	View infoDialog = getLayoutInflater().inflate(R.layout.dialog_client_info, null);
            	TextView hostName = (TextView) infoDialog.findViewById(R.info.ip_address);
            	hostName.setText(ModelHelper.guessURLString("/"));
            	TextView clientVersion = (TextView) infoDialog.findViewById(R.info.client_version);
                clientVersion.setText(settings.getClientVersion());
                TextView apiVersion = (TextView) infoDialog.findViewById(R.info.api_version);
                apiVersion.setText(settings.getServerVersion());
                OnClickListener dialogListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						String subPath = "";
						switch (v.getId()) {
						case R.info.feedback:
							subPath = DimeHelper.DIME_QUESTIONAIRE_PATH;
							break;
						case R.info.tutorial:
							subPath = DimeHelper.DIME_HOWTO_PATH;
							break;
						case R.info.usage_terms:
							subPath = DimeHelper.DIME_USAGE_TERMS_PATH;
							break;
						case R.info.privacy_declaration:
							subPath = DimeHelper.DIME_PRIVACY_POLICY_PATH;
							break;
						}
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ModelHelper.guessURLString(subPath)));
						startActivity(browserIntent);
					}
				};
                TextView feedback = (TextView) infoDialog.findViewById(R.info.feedback);
                feedback.setPaintFlags(feedback.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                feedback.setOnClickListener(dialogListener);
                TextView tutorial = (TextView) infoDialog.findViewById(R.info.tutorial);
                tutorial.setPaintFlags(tutorial.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                tutorial.setOnClickListener(dialogListener);
                TextView terms = (TextView) infoDialog.findViewById(R.info.usage_terms);
                terms.setPaintFlags(terms.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                terms.setOnClickListener(dialogListener);
                TextView declaration = (TextView) infoDialog.findViewById(R.info.privacy_declaration);
                declaration.setPaintFlags(declaration.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                declaration.setOnClickListener(dialogListener);
                TextView bugs = (TextView) infoDialog.findViewById(R.info.bugs);
                bugs.setText(Html.fromHtml(getString(R.string.info_dialog_bugs_text)));
                bugs.setMovementMethod(LinkMovementMethod.getInstance());
                TextView trial = (TextView) infoDialog.findViewById(R.info.test_trial);
                trial.setText(Html.fromHtml(getString(R.string.info_dialog_about_trial_homepage)));
                trial.setMovementMethod(LinkMovementMethod.getInstance());
                TextView os = (TextView) infoDialog.findViewById(R.info.open_source);
                os.setText(Html.fromHtml(getString(R.string.info_dialog_about_os_homepage)));
                os.setMovementMethod(LinkMovementMethod.getInstance());
                TextView rp = (TextView) infoDialog.findViewById(R.info.research_project);
                rp.setText(Html.fromHtml(getString(R.string.info_dialog_about_project_homepage)));
                rp.setMovementMethod(LinkMovementMethod.getInstance());
                AlertDialog.Builder builderInfo = UIHelper.createCustomAlertDialogBuilder(this, "Di.me info", false, infoDialog);                
                builderInfo.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					dialog.dismiss();
    				}
                });
                UIHelper.displayAlertDialog(builderInfo, false);
            	break;
        }
    }
    
    @Override
    public void onBackPressed() {
    	createActionMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	createActionMenu();
        return true;
    }

    private void createActionMenu() {
    	String[] names = {"Logout", "Exit"};
        actionDialog = UIHelper.createActionDialog(this, Arrays.asList(names), new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				actionDialog.dismiss();
	            if (v instanceof Button) {
	                Button button = (Button) v;
	                if (button.getText().equals("Exit")) {
	                	startActivity(new Intent(Activity_Main.this, Activity_Shutdown.class));	                	
	                	finish();
	                } else if (button.getText().equals("Logout")) {
	                	AndroidModelHelper.sendEvaluationDataAsynchronously(null, DimeClient.getMRC(new SilentLoadingViewHandler()), "action_logout");
	                	AndroidModelHelper.resetModel();
	                	Factory.getCrawlerInstance().stop();
	                    DimeClient.getSettings().setLoginPrefRemembered(false);
	                    Intent intent = new Intent(Activity_Main.this, Activity_Login.class);
	                    startActivity(intent);
	                }
	            }
			}
			
		}, null);
        actionDialog.show();
	}

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		loadData();
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Main>createLVH(Activity_Main.this);
	}

}
