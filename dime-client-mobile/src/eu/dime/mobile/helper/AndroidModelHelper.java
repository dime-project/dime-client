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

package eu.dime.mobile.helper;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.control.SilentLoadingViewHandler;
import eu.dime.mobile.crawler.Constants;
import eu.dime.mobile.helper.listener.ClickListenerUserNotifications;
import eu.dime.mobile.helper.objects.AdvisoryProperties;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.objects.NotificationProperties;
import eu.dime.mobile.view.Activity_Main;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.mobile.view.abstr.ListActivityDime;
import eu.dime.mobile.view.abstr.TabActivityDisplayableItemDetail;
import eu.dime.mobile.view.dialog.Activity_Share_Dialog;
import eu.dime.mobile.view.dialog.Activity_Share_New_Livepost_Dialog;
import eu.dime.model.ComparatorHelper;
import eu.dime.model.CreateItemFailedException;
import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.LoadingAbortedRuntimeException;
import eu.dime.model.Model;
import eu.dime.model.ModelConfiguration;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.SharingNotSupportedForSAIDException;
import eu.dime.model.TYPES;
import eu.dime.model.acl.ACL;
import eu.dime.model.acl.ACLPackage;
import eu.dime.model.context.ContextItem;
import eu.dime.model.displayable.AgentItem;
import eu.dime.model.displayable.DataboxItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.displayable.ShareableItem;
import eu.dime.model.specialitem.EvaluationInvolvedItem;
import eu.dime.model.specialitem.EvaluationItem;
import eu.dime.model.specialitem.advisory.AdvisoryItem;
import eu.dime.model.specialitem.advisory.AdvisoryRequestItem;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;
import eu.dime.model.storage.InitStorageFailedException;
import eu.dime.restapi.MultiPartPostClient;
import eu.dime.restapi.RestApiAccess;

public class AndroidModelHelper {
	
	/**
     * be careful: this function returns the actual model configuration changes
     * might lead to reseting the model
     *
     * @return
     */
    public static ModelConfiguration getModelConfiguration() {
        return Model.getInstance().getSettings();
    }
    
    public static void resetModel() {
    	(new AsyncTask<Void, Void, Void>() {
    		
    		@Override
            protected Void doInBackground(Void... params) {
				try {
					Model.getInstance().restartModel();
                } catch (RuntimeException e) {
                	Log.d(AndroidModelHelper.class.getSimpleName(), e.getMessage());
				}
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
            	Log.d(AndroidModelHelper.class.getSimpleName(), "Model resettet!");
            }
        }).execute();
	}

	/** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** convenient functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */
	public static<ITEM_TYPE extends GenItem> List<ITEM_TYPE> getItemsForSelection(List<ITEM_TYPE> allItems, List<ITEM_TYPE> selectedItems) {
		List<ITEM_TYPE> itemsForSelection = new ArrayList<ITEM_TYPE>();
    	for (ITEM_TYPE livepostItem : allItems) {
			if(!selectedItems.contains(livepostItem)){
				itemsForSelection.add(livepostItem);
			}
		}
    	return itemsForSelection;
	}
	
	public static<ITEM_TYPE extends GenItem> List<ITEM_TYPE> removeListItemsByMeansOfItems(List<ITEM_TYPE> list, List<ITEM_TYPE> itemsToRemove) {
		List<ITEM_TYPE> result = new ArrayList<ITEM_TYPE>();
		for (ITEM_TYPE genItem : list) {
			if(!itemsToRemove.contains(genItem)){
				result.add(genItem);
			}
		}
    	return result;
    }
    
    public static<ITEM_TYPE extends GenItem> List<ITEM_TYPE> removeListItemsByMeansOfGuids(List<ITEM_TYPE> list, List<String> guidsOfItemsToRemove) {
    	List<ITEM_TYPE> result = new ArrayList<ITEM_TYPE>();
		for (ITEM_TYPE genItem : list) {
			if(!guidsOfItemsToRemove.contains(genItem.getGuid())){
				result.add(genItem);
			}
		}
    	return result;
    }
    
    public static<ITEM_TYPE extends GenItem> List<ITEM_TYPE> getListOfItemsWithGuids(List<ITEM_TYPE> list, List<String> guids) {
    	List<ITEM_TYPE> result = new Vector<ITEM_TYPE>();
    	if(guids != null) {
	    	for (ITEM_TYPE item : list) {
				if(guids.contains(item.getGuid())) {
					result.add(item);
				}
			}
    	}
    	return result;
    }
    
    public static List<String> getListOfGuidsOfDisplayableList(List<DisplayableItem> displayables) {
    	List<String> listOfGuids = new ArrayList<String>();
    	Collections.sort(displayables, new ComparatorHelper.NameComparator());
    	for(DisplayableItem item : displayables) {
    		listOfGuids.add(item.getGuid());
    	}
    	return listOfGuids;
    }
    
    public static List<String> getListOfGuidsOfGenItemList(List<GenItem> items) {
    	List<String> listOfGuids = new ArrayList<String>();
    	for(GenItem item : items) {
    		listOfGuids.add(item.getGuid());
    	}
    	return listOfGuids;
    }
    
    public static List<String> getListOfNamesOfDisplayableList(List<DisplayableItem> displayables) {
    	List<String> listOfNames = new ArrayList<String>();
    	Collections.sort(displayables, new ComparatorHelper.NameComparator());
		for (DisplayableItem item : displayables) {
			listOfNames.add(item.getName());
		}
		return listOfNames;
    }
    
	public static List<String> getListOfNamesOfGuidList(Context context, List<String> guids, boolean isShareable) {
    	List<String> listOfNames = new ArrayList<String>();
		for (String guid : guids) {
			try {
				listOfNames.add(getOwnItemFromStorage(guid, isShareable).getName());
			} catch (Exception e){
				listOfNames.add("could not find item name");
			}
		}
		return listOfNames;
    }
    
    public static DisplayableItem getDisplayableItemByName(Activity activity, ModelRequestContext mrContext, List<DisplayableItem> displayables, String name, TYPES type) {
    	String guid = "";
		for (DisplayableItem group : displayables) {
			if(group.getName().equalsIgnoreCase(name)){
				guid = group.getGuid();
			}
		}
		return (DisplayableItem) getGenItemSynchronously(activity, new DimeIntentObject(guid, type, mrContext.owner));
    }

	public static void clearCache(final String TAG) {
		(new AsyncTask<Void, Void, Void>() {
    		
    		@Override
            protected Void doInBackground(Void... params) {
				try {
                    Model.getInstance().resetStorage();
                } catch (InitStorageFailedException ex) {
                    Logger.getLogger(Activity_Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RuntimeException e) {
                	Log.d(TAG, e.getMessage());
				}
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
            	Log.d(TAG, "Cache cleared!");
            }
        }).execute();
	}
    
    /** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** model helper functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */
	public static GenItem getGenItemSynchronously(Context context, DimeIntentObject dio) {
		GenItem item = null;
		try {
			item = Model.getInstance().getItem(DimeClient.getMRC(dio.getOwnerId(), new SilentLoadingViewHandler()), dio.getItemType(), dio.getItemId());
		} catch (LoadingAbortedRuntimeException ex) {
			Log.d(AndroidModelHelper.class.getSimpleName(), ex.getMessage());
		}
		return item;
	}
	
	public static DisplayableItem getOwnItemFromStorage(String guid, boolean isShareable) {
		return ((DisplayableItem)Model.getInstance().getOwnItem(DimeClient.getUserMainSaid(), guid, isShareable));
	}
	
    public static void createGenItemAsynchronously(final GenItem item, final DialogInterface dialog, Activity activity, final ModelRequestContext mrContext, final String actionName) {
    	final WeakReference<Activity> mActivity = new WeakReference<Activity>(activity);
    	(new AsyncTask<Void, Void, String>() {
    		
			@Override
            protected String doInBackground(Void... params) {
				String result = "";
				try {
					Model.getInstance().createItem(mrContext, item);
					String name = (ModelHelper.isDisplayableItem(item.getMType())) ? " " + ((DisplayableItem)item).getName() : "";
					result = UIHelper.formatStringOnlyFirstCharUpperCase(item.getType()) + name + " created!";
				} catch (CreateItemFailedException e) {
					result = "Error: Creation of the " + UIHelper.formatStringOnlyFirstCharUpperCase(item.getType()) +" failed!";
				} catch (Exception e) {
					Log.e(mActivity.get().getClass().getSimpleName(), "Cast to ListActivityDime<GenItem> failed!");
				}
                return result;
            }

			@Override
            protected void onPostExecute(String result) {
				if(mActivity.get() != null && !mActivity.get().isFinishing()) {
					refreshViewAfterModelChanges(mActivity.get(), dialog);
				}
				Toast.makeText(DimeClient.getAppContext(), result, Toast.LENGTH_LONG).show();
				sendEvaluationDataAsynchronously(Arrays.asList(item), mrContext, actionName);
            }
            
        }).execute();
    }
    
    public static void updateGenItemAsynchronously(final GenItem item, final DialogInterface dialog, Activity activity, final ModelRequestContext mrContext, final String actionName) {
    	final WeakReference<Activity> mActivity = new WeakReference<Activity>(activity);
    	(new AsyncTask<Void, Void, String>() {
    		
			@Override
            protected String doInBackground(Void... params) {
				String result = "";
				try {
					Model.getInstance().updateItem(mrContext, item);
					String name = (item instanceof DisplayableItem) ? " " + ((DisplayableItem)item).getName() : "";
					result = UIHelper.formatStringOnlyFirstCharUpperCase(item.getType()) + name + " updated!";
				} catch (Exception e) {
					result= "Update of " + item.getType() + " failed!";
				}
                return result;
            }

			@Override
            protected void onPostExecute(String result) {
				if(mActivity.get() != null && !mActivity.get().isFinishing()) {
					refreshViewAfterModelChanges(mActivity.get(), dialog);
				}
				Toast.makeText(DimeClient.getAppContext(), result, Toast.LENGTH_LONG).show();
				sendEvaluationDataAsynchronously(Arrays.asList(item), mrContext, actionName);
            }
            
        }).execute();
    }
    
    public static void updateContextAsynchronously(final ContextItem item) {
    	(new AsyncTask<Void, Void, String>() {
    		
			@Override
            protected String doInBackground(Void... params) {
				String result = "posting context: " + item.getScope() + " successful!";
				GenItem response = null;
				try {
					response = RestApiAccess.postItemNew(DimeClient.getUserMainSaid(), Model.ME_OWNER,TYPES.CONTEXT, item, AndroidModelHelper.getModelConfiguration().restApiConfiguration);
				} catch (Exception ex) {
					result = "Exception " + ex.toString() + " posting context: " + item.getScope();
				}
				if(response == null) result = "Error occurred trying to send " + item.getScope() + " context data!";
                return result;
            }

			@Override
            protected void onPostExecute(String result) {
				Log.e(Constants.LOG_TAG, result);
            }
            
        }).execute();
    }
    
    @SuppressWarnings("unchecked")
	public static<ITEM_TYPE extends GenItem> void deleteGenItemsAsynchronously(Activity listActivity, final TYPES type, final ModelRequestContext mrContext, final String actionName) {
    	final WeakReference<ListActivityDime<ITEM_TYPE>> mActivity = new WeakReference<ListActivityDime<ITEM_TYPE>>((ListActivityDime<ITEM_TYPE>)listActivity);
    	(new AsyncTask<Void, Void, String>() {
    		
			@Override
            protected String doInBackground(Void... params) {
				String result = "";
				try {
					int count = 0;
					for (String itemGuid : mActivity.get().getSelectionGUIDS()) {
						Model.getInstance().removeItem(mrContext, itemGuid, type);
						count++;
					}
					result = count + " " + UIHelper.formatStringOnlyFirstCharUpperCase(type.toString()) + "(s) " + " deleted!";
				} catch (Exception e) {
					e.printStackTrace();
				}
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
            	if(mActivity.get() != null && !mActivity.get().isFinishing()) {
            		refreshViewAfterModelChanges(mActivity.get(), null);
            	}
            	Toast.makeText(DimeClient.getAppContext(), result, Toast.LENGTH_LONG).show();
            	sendEvaluationDataAsynchronously(getListOfItemsWithGuids((List<GenItem>)(Object)ModelHelper.getAllDisplayableItems(mrContext, type), mActivity.get().getSelectionGUIDS()), mrContext, actionName);
            }
            
        }).execute();
    }
    
    public static void createAndAssignProfileAttributesAsynchronously(final ProfileItem profile, final ProfileAttributeItem pai, final DialogInterface dialog, Activity activity, final ModelRequestContext mrContext, final String actionName) {
    	final WeakReference<Activity> mActivity = new WeakReference<Activity>(activity);
    	(new AsyncTask<Void, Void, String>() {
    		
			@Override
            protected String doInBackground(Void... params) {
				String result = "";
				try {
					ProfileAttributeItem tmp = (ProfileAttributeItem) Model.getInstance().createItem(mrContext, pai);
					String name = tmp.getName();
					result = UIHelper.formatStringOnlyFirstCharUpperCase(tmp.getType()) + name + " created!";
					profile.addItem(tmp.getGuid());
					updateGenItemAsynchronously(profile, null, mActivity.get(), mrContext, actionName);
				} catch (CreateItemFailedException e) {
					result = "Error: Creation of the " + UIHelper.formatStringOnlyFirstCharUpperCase(pai.getType()) +" failed!";
				}
                return result;
            }

			@Override
            protected void onPostExecute(String result) {
				if(mActivity.get() != null && !mActivity.get().isFinishing()) {
					refreshViewAfterModelChanges(mActivity.get(), dialog);
				}
				Toast.makeText(DimeClient.getAppContext(), result, Toast.LENGTH_LONG).show();
            	sendEvaluationDataAsynchronously(Arrays.asList((GenItem)pai), mrContext, "Create GenItem");
            }
            
        }).execute();
    }
    
    /** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** people view functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */
    public static void importAllContacts(Activity activity, final ModelRequestContext mrContext) {
//    	Cursor c = null;
//		try {
//			//FIXME Import of all contacts doesn`t work
//			Uri uri = ContactsContract.Contacts.CONTENT_URI;
//			String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
//			String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1'";
//			String[] selectionArgs = null;
//			String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
//			c = managedQuery(uri, projection, selection, selectionArgs, sortOrder);
//			if (c.moveToFirst()) {
//				do {
//					AndroidModelHelper.importContact(activity, mrContext, c);
//				} while (c.moveToNext());
//			}
//		} catch (Exception e) {	
//			
//		} finally {
//			if(c != null) c.close();
//		}
    }
    
	@SuppressWarnings("deprecation")
    public static void importSingleContact(Activity activity, final ModelRequestContext mrContext, Uri contactData) {
		Cursor c = activity.managedQuery(contactData, null, null, null, null);
		if (c.moveToFirst()) {
			final String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			final String photoId = c.getString(c.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
			final WeakReference<Activity> mActivity = new WeakReference<Activity>(activity);
			(new AsyncTask<Void, Void, String>() {
	    		
				@Override
	            protected String doInBackground(Void... params) {
					return importContact(mActivity.get(), mrContext, name, photoId) ? "Contact " + name + " imported!" : "Import of contact " + name + " failed!";
	            }
	
	            @SuppressWarnings("unchecked")
				@Override
	            protected void onPostExecute(String result) {
	            	if(mActivity.get() != null && !mActivity.get().isFinishing()) {
						refreshViewAfterModelChanges(mActivity.get(), null);
					}
	            	Toast.makeText(DimeClient.getAppContext(), result, Toast.LENGTH_LONG).show();
	            	sendEvaluationDataAsynchronously((List<GenItem>) (Object) Arrays.asList(new PersonItem()), mrContext, "Import contact");
	            }
	            
	        }).execute();
		}
    }
    
    @SuppressWarnings({ "unused"})
    //TODO upload picture, create profilecard with contact details (phone number...), create person with profilecard assigned
	public static boolean importContact(Activity activity, final ModelRequestContext mrContext, String name, String photoId) {
    	PersonItem pi = (PersonItem) ItemFactory.createNewDisplayableItemByType(TYPES.PERSON, name);
		String filePath = null;
		Cursor c2 = null;
		try {
			Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, Long.parseLong(photoId));
			byte[] photoBytes = null;
			c2 = activity.getContentResolver().query(photoUri, new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO }, null, null, null);
			filePath = FileHelper.parseUriToFilename(photoUri, activity);
			if (c2.moveToFirst()) {
				photoBytes = c2.getBlob(0);
			}
		} catch(NumberFormatException e) {
			
		} catch(Exception e) {
			
		} finally {
			if(c2 != null) c2.close();
		}
		if (filePath != null) {
			File file = new File(filePath);
			MultiPartPostClient myClient = new MultiPartPostClient(getModelConfiguration());
			try {
				myClient.uploadFile(file);
				pi.setImageUrl(filePath);
			} catch (IOException ex) { }
		}
		boolean success = true;
		try {
			Model.getInstance().createItem(mrContext, pi);
		} catch (CreateItemFailedException ex) {
			success = false;
		}
		return success;
	}

	public static void mergePersonsAsynchronously(Activity activity, final ModelRequestContext mrContext, final List<String> selectedGUIDs) {
		final WeakReference<Activity> mActivity = new WeakReference<Activity>(activity);
		(new AsyncTask<Void, Void, String>() {
    		
			@Override
            protected String doInBackground(Void... params) {
				String result = "";
				try {
					Model.getInstance().mergeItems(mrContext, TYPES.PERSON, selectedGUIDs);
					result = selectedGUIDs.size() + " persons merged!";
				} catch (Exception e) {
					result = "Persons could not be merged!";
				}
                return result;
            }

            @SuppressWarnings("unchecked")
			@Override
            protected void onPostExecute(String result) {
            	if(mActivity.get() != null && !mActivity.get().isFinishing()) {
					refreshViewAfterModelChanges(mActivity.get(), null);
				}
            	Toast.makeText(DimeClient.getAppContext(), result, Toast.LENGTH_SHORT).show();
            	sendEvaluationDataAsynchronously(getListOfItemsWithGuids((List<GenItem>)(Object)ModelHelper.getAllDisplayableItems(mrContext, TYPES.PERSON), selectedGUIDs), mrContext, "Merge persons");
            }
            
        }).execute();
	}
	
	@SuppressWarnings("unchecked")
	public static void refreshViewAfterModelChanges(Activity activity, DialogInterface dialog) {
		if(activity instanceof ListActivityDime) {
			((ListActivityDime<GenItem>) activity).resetSelection();
			((ListActivityDime<GenItem>) activity).reloadList();
		} else if(activity instanceof ActivityDime) {
			((ActivityDime) activity).startTask("Refreshing view...");
		}
		if(activity.getParent() != null) {
			UIHelper.hideSoftKeyboard(activity.getParent(), activity.getParent().getCurrentFocus());
		}
		if(activity.getParent() != null && activity.getParent() instanceof TabActivityDisplayableItemDetail) {
			((TabActivityDisplayableItemDetail)activity.getParent()).refreshView();
		}
    	if(dialog != null) dialog.dismiss();
	}
    
    /** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** share functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */
    public static void shareResources(Context context, List<String> guids, TYPES itemType) {
		Intent intent = new Intent(context, Activity_Share_Dialog.class);
		intent.putStringArrayListExtra(ModelHelper.getStringType(itemType), new ArrayList<String>(guids));
		context.startActivity(DimeIntentObjectHelper.populateIntent(intent, new DimeIntentObject()));
	}
    
    public static void shareResource(Context context, DisplayableItem item) {
		Intent intent = new Intent(context, Activity_Share_Dialog.class);
		intent.putStringArrayListExtra(ModelHelper.getStringType(item.getMType()), new ArrayList<String>(Arrays.asList(item.getGuid())));
		context.startActivity(DimeIntentObjectHelper.populateIntent(intent, new DimeIntentObject()));
	}
    
    public static void shareResources(Context context, List<GenItem> items) {
    	Intent intent = new Intent(context, Activity_Share_Dialog.class);
    	HashMap<TYPES, ArrayList<String>> hashMap = new HashMap<TYPES, ArrayList<String>>();
    	for (GenItem genItem : items) {
			if(hashMap.containsKey(genItem.getMType())) {
				hashMap.get(genItem.getMType()).add(genItem.getGuid());
			} else {
				ArrayList<String> guids = new ArrayList<String>();
				guids.add(genItem.getGuid());
				hashMap.put(genItem.getMType(), guids);
			}
		}
    	for (TYPES key : hashMap.keySet()) {
    		intent.putStringArrayListExtra(ModelHelper.getStringType(key), hashMap.get(key));
		}
		context.startActivity(DimeIntentObjectHelper.populateIntent(intent, new DimeIntentObject()));
    }
    
    public static void answerLivepost(Context context, List<PersonItem> items) {
		Intent intent = new Intent(context, Activity_Share_New_Livepost_Dialog.class);
		ArrayList<String> persons = new ArrayList<String>();
		for (PersonItem personItem : items) {
			persons.add(personItem.getGuid());
		}
		intent.putStringArrayListExtra(ModelHelper.getStringType(TYPES.PERSON), persons);
		context.startActivity(DimeIntentObjectHelper.populateIntent(intent, new DimeIntentObject()));
	}
    
    public static void loadAdvisoryPropertiesAsynchronously(final Context context, final AdvisoryRequestItem ari, final List<AdvisoryProperties> advisoryItemsNotValidAgentsForSharing, final TextView noWarnings, final TextView labelWarnings, final LinearLayout warningsContainer) {
		new AsyncTask<Void, Void, List<AdvisoryProperties>>() {
			@Override
			protected List<AdvisoryProperties> doInBackground(Void... params) {
				List<AdvisoryProperties> advisoryProperties = new ArrayList<AdvisoryProperties>();
				try {
                    List<AdvisoryItem> advisories = ModelHelper.getSharingAdvisories(DimeClient.getUserMainSaid(), ari);
                    Collections.sort(advisories, new ComparatorHelper.WarningLevelComparator());
                    for (AdvisoryItem ai : advisories) {
						advisoryProperties.add(UIHelper.getAdvisoryProperties(context, ai));
					}
                } catch (Exception ex) {
                    Logger.getLogger(Activity_Share_Dialog.class.getName()).log(Level.SEVERE, null, ex);
                } 
				advisoryProperties.addAll(advisoryItemsNotValidAgentsForSharing);
				return advisoryProperties;
			}

			@Override
			protected void onPostExecute(List<AdvisoryProperties> result) {
            	if (result.size() == 0) {
            		noWarnings.setText(context.getString(R.string.sharing_no_warnings));
            		noWarnings.setVisibility(View.VISIBLE);
            	} else {
            		noWarnings.setVisibility(View.GONE);
            	}
            	labelWarnings.setText(String.valueOf(result.size()));
            	for (AdvisoryProperties advisoryProperty : result) {
            		warningsContainer.addView(UIHelper.createWarningWidget(context, advisoryProperty));
				}
			}
		}.execute();
	}
    
    public static void shareItemsAsynchronously(Activity activity, final ModelRequestContext mrContext, final List<AgentItem> listOfSelectedAgentsTmp, final List<GenItem> listOfSelectedItemsTmp, final ProfileItem selectedProfile){
    	final WeakReference<Activity> mActivity = new WeakReference<Activity>(activity);
    	(new AsyncTask<Void, Void, String>() {

			@Override
            protected String doInBackground(Void... paramss) {
				String result = "";
				int successfullyShared = 0;
				int notSupportedCount = 0;
				if(listOfSelectedAgentsTmp != null && listOfSelectedItemsTmp != null) {
					for (AgentItem agent : listOfSelectedAgentsTmp) {
	                    for (GenItem item : listOfSelectedItemsTmp) {
	                    	try {
								ModelHelper.shareItemToAgent(mrContext, item, agent, selectedProfile);
								successfullyShared++;
							} catch (SharingNotSupportedForSAIDException e) {
								notSupportedCount++;
							}
	                    }
	                }
					if(successfullyShared > 0) result = successfullyShared + " items successfully shared to " + listOfSelectedAgentsTmp.size() + " receivers!";
					if(notSupportedCount > 0) result += "Not supported for " + notSupportedCount + " agents!";
				} else {
					result = "Error occurred during sharing request!";
				}
                return result;
            }

			@SuppressWarnings("unchecked")
            @Override
            protected void onPostExecute(String result) {
            	Toast.makeText(DimeClient.getAppContext(), result, Toast.LENGTH_LONG).show();
                if(!mActivity.get().isFinishing()) mActivity.get().finish();
                List<GenItem> affectedItems = new ArrayList<GenItem>();
                affectedItems.addAll(listOfSelectedItemsTmp);
                affectedItems.addAll((List<GenItem>) (Object) listOfSelectedAgentsTmp);
                sendEvaluationDataAsynchronously(affectedItems, mrContext, "Share");
            }
            
        }).execute();
    }
    
    public static void shareNewLivepostAsynchronously(Activity activity, final ModelRequestContext mrContext, final List<AgentItem> listOfSelectedAgentsTmp, final LivePostItem livepost, final ProfileItem selectedProfile){
    	final WeakReference<Activity> mActivity = new WeakReference<Activity>(activity);
    	(new AsyncTask<Void, Void, String>() {

			@Override
            protected String doInBackground(Void... params) {
				String result = "";
				try {
					for (AgentItem agent : listOfSelectedAgentsTmp) {
	                    livepost.addAccessingAgent(selectedProfile.getServiceAccountId(), agent.getGuid(), agent.getMType(), null);
	                }
					Model.getInstance().createItem(mrContext, livepost);
					result = "Livepost successfully shared to " + listOfSelectedAgentsTmp.size() + " receiver(s)!";
				} catch (Exception e) {
					result = "Could not create and share livepost!";
				}
				
                return result;
            }

			@SuppressWarnings("unchecked")
            @Override
            protected void onPostExecute(String result) {
            	Toast.makeText(DimeClient.getAppContext(), result, Toast.LENGTH_LONG).show();
                if(!mActivity.get().isFinishing()) mActivity.get().finish();
                List<GenItem> affectedItems = new ArrayList<GenItem>();
                affectedItems.addAll((List<GenItem>) (Object) Arrays.asList(livepost));
                affectedItems.addAll((List<GenItem>) (Object) listOfSelectedAgentsTmp);
                sendEvaluationDataAsynchronously(affectedItems, mrContext, "Share");
            }
            
        }).execute();
    }
    
    /** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** evaluation data functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */
    public static void sendEvaluationDataAsynchronously(final List<GenItem> items, final ModelRequestContext mrContext, final String action) {
    	if(action != null && DimeClient.getSettings().isSetPrefAccepted()) {
    		(new AsyncTask<Void, Void, Boolean>() {
        		
    			@Override
                protected Boolean doInBackground(Void... params) {
    				boolean result = true;
    				try {
    					EvaluationItem ei = createEvaluationItemWithClientData();
    					ei.setInvolvedItems(new EvaluationInvolvedItem(items));
    					ei.setAction(action);
    					RestApiAccess.postItemNew(mrContext.hoster, mrContext.owner, ei.getMType(), ei, DimeClient.getSettings().getRestApiConfiguration());
					} catch (Exception e) {
						result = false;
					}
					return result;
                }

    			@Override
                protected void onPostExecute(Boolean result) {
    				if(result) Log.d("DimeHelper", "Evaluation item sent to private server!");
                }
            
    		}).execute();
		}
	}
    
    public static EvaluationItem createEvaluationItemWithClientData() {
    	String version = DimeClient.getSettings().getClientVersion();
    	String hashedTenantId = DimeClient.getSettings().getEvaluationId();
    	String currentPlace = (ContextHelper.getCurrentPlace() != null) ? ContextHelper.getCurrentPlace().getPlaceId() : "not set";
    	String currentSituation = (ContextHelper.getCurrentSituationGuid() != null) ? ContextHelper.getCurrentSituationGuid() : "not set";
    	return ItemFactory.createNewEvaluationItem(version, hashedTenantId, currentPlace, currentSituation, DimeClient.getViewStack());
    }
	
	/** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** extended view functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */
	public static void loadGroupsOfChildAsynchronously(final Context context, final String owner, final DisplayableItem di, final TextView childrenText) {
		(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String children = "";
				if (di != null) {
					if(di.getMType().equals(TYPES.PERSON)) {
						List<GroupItem> groups = ModelHelper.getGroupsOfPerson(DimeClient.getMRC(owner, new SilentLoadingViewHandler()), di.getGuid());
						for (GroupItem gi : groups) {
							children += gi.getName() + ", ";
						}
					} else if(di.getMType().equals(TYPES.RESOURCE)) {
						List<DataboxItem> databoxes = ModelHelper.getDataboxesOfResource(DimeClient.getMRC(owner, new SilentLoadingViewHandler()), di.getGuid());
						for (DataboxItem dbi : databoxes) {
							children += dbi.getName() + ", ";
						}
					}
			        if(children.length() > 0) { children = children.substring(0, children.length()-2); }
				}
				return children;
			}

			@Override
			protected void onPostExecute(String children) {
				if(children.length()>0) {
					childrenText.setVisibility(View.VISIBLE);
					childrenText.setText(children);
				} else {
					childrenText.setVisibility(View.GONE);
				}
			}
		}).execute();
	}
	
	public static void loadNotificationPropertiesAsynchronously(final Context context, final UserNotificationItem uni, final View view, final TextView message, final TextView sender, final ImageView iv) {
		new AsyncTask<Void, Void, NotificationProperties>() {
			@Override
			protected NotificationProperties doInBackground(Void... params) {
				return UIHelper.getNotificationProperties(context, uni);
			}

			@Override
			protected void onPostExecute(NotificationProperties result) {
				if(result!=null) {
					sender.setText(result.getSenderName());
					message.setText(result.getNotificationText());
					view.setOnClickListener(new ClickListenerUserNotifications(context, uni, result));
					iv.setImageResource(result.getDrawableId());
				} else {
					message.setText("Could not load notification properties");
				}
			}
		}.execute();
	}
	
	public static void loadChildrenOfDisplayableItemAsynchronously(final Context context, final String owner, final LinearLayout previewContainer, final DisplayableItem item, final TextView emptyContainer) {
		new AsyncTask<Void, Void, List<DisplayableItem>>() {
			@Override
			protected List<DisplayableItem> doInBackground(Void... params) {
				List<DisplayableItem> items; 
				try {
					items = ModelHelper.getChildrenOfDisplayableItem(DimeClient.getMRC(owner, new SilentLoadingViewHandler()), item);
				} catch (Exception e) {
					items = new ArrayList<DisplayableItem>();
				}
				return items;
			}

			@Override
			protected void onPostExecute(List<DisplayableItem> result) {
				UIHelper.updatePreviewContainer(context, previewContainer, result, item, emptyContainer);
			}
		}.execute();
	}
	
	public static void loadProfileNamesAsynchronously(final Context context, final TextView tv, final PersonItem pi) {
		(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String profiles = "";
				if (pi != null) {
					List<ProfileItem> test = ModelHelper.getAllProfiles(DimeClient.getMRC(pi.getUserId(), new SilentLoadingViewHandler()));
			        for (ProfileItem profileItem : test) {
						profiles += profileItem.getName() + "; ";
					}
			        if(profiles.length() > 0){ profiles = profiles.substring(0, profiles.length()-2); }
				}
				return profiles;
			}

			@Override
			protected void onPostExecute(String profiles) {
				tv.setText(profiles);
			}
		}).execute();
	}
	
	public static void loadProfileAttributesOfProfileAsynchronously(final Context context, final TextView tv, final ProfileItem profile, final String specialType) {
		(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				String result = "";
				List<ProfileAttributeItem> attributes = ModelHelper.getProfileAttributesOfProfile(DimeClient.getMRC(profile.getUserId(), new SilentLoadingViewHandler()), profile);
				for (ProfileAttributeItem profileAttributeItem : attributes) {
					for (String key : profileAttributeItem.getValue().keySet()) {
						String value = profileAttributeItem.getValue().get(key);
						if(value.length() > 0) {
							if(specialType == null || specialType.length() == 0) {
								result = key + ":" + value;
							} else if(specialType.equals(key)) {
								result = value;
							}
						}
					}
				}
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				tv.setText(result);
			}
		}).execute();
	}

	public static void loadSharedResourcesAsynchronously(final Context context, final String owner, final AgentItem ai, final LinearLayout sharedContainer, final TextView sharedNoItems) {
		(new AsyncTask<Void, Void, List<DisplayableItem>>() {

			@SuppressWarnings("unchecked")
			@Override
			protected List<DisplayableItem> doInBackground(Void... params) {
				List<DisplayableItem> result;
				try {
					result = (List<DisplayableItem>) (Object) ModelHelper.getAllShareableItemsDirectlySharedToAgent(DimeClient.getMRC(owner, new SilentLoadingViewHandler()), ai.getGuid());
				} catch (Exception e) {
					result = null;
				}
				return result; 
			}

			@Override
			protected void onPostExecute(List<DisplayableItem> resources) {
				UIHelper.updateSharedContainerShareables(context, sharedContainer, resources, sharedNoItems);
			}
		}).execute();
	}
	
	public static void loadAccessingAgentsAsynchronously(final Context context, final ShareableItem gi, final LinearLayout sharedContainer, final TextView sharedNoItems) {
		(new AsyncTask<Void, Void, Iterable<ACLPackage>>() {

			@Override
			protected Iterable<ACLPackage> doInBackground(Void... params) {
				ACL access = ((GenItem)gi).getAccessingAgents();
				return access.getACLPackages();
			}

			@Override
			protected void onPostExecute(Iterable<ACLPackage> resources) {
				UIHelper.updateSharedContainerAgents(context, sharedContainer, resources, sharedNoItems);
			}
		}).execute();
	}
	
	/** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** other functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */
	public static Integer getTrustOrPrivacyLevelForDisplayableItem(DisplayableItem di) {
		Integer level = null;
		if (ModelHelper.isAgent(di.getMType())) {
			level = getTrustOrPrivacyLevelForDisplayableItem(((AgentItem) di).getTrustLevel());
		} else if (ModelHelper.isShareable(di.getMType())) {
			level = getTrustOrPrivacyLevelForDisplayableItem(((ShareableItem) di).getPrivacyLevel());
		}
		return level;
	}
	
	public static Integer getTrustOrPrivacyLevelForDisplayableItem(double value) {
		return (int) (value * 2.0d);
	}
	
	public static double normalizeTrustOrPrivacyLevelForDisplayableItem(int level) {
		return level/2.0d;
	}
    
}
