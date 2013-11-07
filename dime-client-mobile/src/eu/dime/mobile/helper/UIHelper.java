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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.control.SilentLoadingViewHandler;
import eu.dime.mobile.helper.listener.ItemActionListener;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.objects.ResultObject;
import eu.dime.mobile.helper.objects.ResultObject.RESULT_OBJECT_TYPES;
import eu.dime.mobile.helper.objects.AdvisoryProperties;
import eu.dime.mobile.helper.objects.NotificationProperties;
import eu.dime.mobile.helper.objects.ResultObjectDisplayable;
import eu.dime.mobile.helper.objects.ResultObjectProfileSharing;
import eu.dime.mobile.helper.objects.ResultObjectServiceAdapter;
import eu.dime.mobile.helper.objects.IResultOfStandardDialog;
import eu.dime.mobile.helper.objects.StandardDialogProperties;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.mobile.view.adapter.BaseAdapter_Dialog_Sharing_Profile;
import eu.dime.mobile.view.adapter.BaseAdapter_Livepost;
import eu.dime.mobile.view.adapter.BaseAdapter_ServiceAdapter;
import eu.dime.mobile.view.adapter.BaseAdapter_Standard;
import eu.dime.mobile.view.communication.TabActivity_Livepost_Detail;
import eu.dime.mobile.view.data.TabActivity_Resource_Detail;
import eu.dime.mobile.view.dialog.Activity_Edit_Item_Dialog;
import eu.dime.mobile.view.dialog.Activity_Share_Dialog;
import eu.dime.mobile.view.dialog.Activity_Share_New_Livepost_Dialog;
import eu.dime.mobile.view.dialog.Activity_Unshare_Dialog;
import eu.dime.mobile.view.dialog.ListActivity_Merge_Dialog;
import eu.dime.mobile.view.people.TabActivity_Group_Detail;
import eu.dime.mobile.view.people.TabActivity_Person_Detail;
import eu.dime.mobile.view.situations.TabActivity_Situations;
import eu.dime.model.GenItem;
import eu.dime.model.LoadingAbortedRuntimeException;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.ModelTypeNotFoundException;
import eu.dime.model.TYPES;
import eu.dime.model.specialitem.advisory.AdvisoryItem;
import eu.dime.model.specialitem.advisory.WarningAgentNotValidForSharing;
import eu.dime.model.specialitem.advisory.WarningAttributesDisjunctGroups;
import eu.dime.model.specialitem.advisory.WarningAttributesProfileNotShared;
import eu.dime.model.specialitem.advisory.WarningAttributesUntrusted;
import eu.dime.model.specialitem.advisory.WarningTooManyReceivers;
import eu.dime.model.specialitem.advisory.WarningTooManyResources;
import eu.dime.model.specialitem.usernotification.UNEntryAdhocGroupRecommendation;
import eu.dime.model.specialitem.usernotification.UNEntryMergeRecommendation;
import eu.dime.model.specialitem.usernotification.UNEntryMessage;
import eu.dime.model.specialitem.usernotification.UNEntryRefToItem;
import eu.dime.model.specialitem.usernotification.UNEntrySituationRecommendation;
import eu.dime.model.specialitem.usernotification.UN_TYPE;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;
import eu.dime.model.acl.ACLPackage;
import eu.dime.model.acl.ACLPerson;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.model.displayable.ServiceAdapterItem;
import eu.dime.model.displayable.SituationItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class UIHelper {

	/** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** standard ui functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */

	public static void hideView(View v) {
		if(v != null) {
			v.setVisibility(View.GONE);
		}
	}

	public static void showView(View v) {
		if(v != null) {
			v.setVisibility(View.VISIBLE);
		}
	}
	
	public static int getDPvalue(int pixelValue) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixelValue, DimeClient.getAppContext().getResources().getDisplayMetrics());
	}

	public static TextView createTextView(Context context, int style, int typeface, int textSize, LayoutParams params, boolean isSingleLine) {
		TextView textView = new TextView(context);
		if(isSingleLine) {
			textView.setSingleLine(true);
			textView.setEllipsize(TruncateAt.END);
		}
		if(typeface != -1) textView.setTypeface(null, typeface);
		if(params != null) textView.setLayoutParams(params);
		if(textSize != -1) textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
		if(style != -1) textView.setTextAppearance(context, style);
		return textView;
	}

	public static EditText createEditText(Context context, int input, String hint, int lines, boolean focused) {
		EditText editText = new EditText(context);
		editText.setHint(hint);
		editText.setInputType(input);
		editText.setLines(lines);
		editText.setSingleLine(lines == 1);
		editText.setFocusableInTouchMode(true);
		if (focused)
			editText.requestFocus();
		return editText;
	}

	public static LinearLayout createLinearLayout(Context context, int orientation) {
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(orientation);
		return linearLayout;
	}

	public static void hideSoftKeyboard(Context context, View view) {
		((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void showSoftKeyboard(Context context, View view) {
		((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	}

	public final static boolean isValidEmail(CharSequence target) {
		return (target == null) ? false : (android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches());
	}

	@SuppressLint("DefaultLocale")
	public static int switchInputType(String key) {
		int inputType = InputType.TYPE_CLASS_TEXT;
		if (key.toLowerCase().contains("date")) {
			inputType = InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE;
		} else if (key.toLowerCase().contains("email")) {
			inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
		} else if (key.toLowerCase().contains("postal") || key.toLowerCase().contains("pobox") || key.toLowerCase().contains("phone")) {
			inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED;
		} else if (key.toLowerCase().contains("phone")) {
			inputType = InputType.TYPE_CLASS_PHONE;
		}
		return inputType;
	}

	public static boolean isUIThread() {
		return (Looper.getMainLooper().getThread() == Thread.currentThread());
	}

	/** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** dialog functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */

	public static void createStandardDialog(final Activity activity, ModelRequestContext mrContext, final BaseAdapterDisplayableItem adapter, List<DisplayableItem> items, final ResultObject.RESULT_OBJECT_TYPES type) {
		try {
			Builder builder = new AlertDialog.Builder(activity);
			LinearLayout ll = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_standard, null);
			ListView list = (ListView) ll.findViewById(R.dialog.list);
			TextView emptyText = createEmptyListWidget(activity);
			ll.addView(emptyText);
			list.setEmptyView(emptyText);
			list.setAdapter(adapter);
			list.setTextFilterEnabled(true);
			builder.setView(ll);
			final LinearLayout header = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_standard_header, null);
			final LinearLayout searchArea = (LinearLayout) header.findViewById(R.dialog.search_area);
			final LinearLayout infoArea = (LinearLayout) header.findViewById(R.dialog.info_area);
			final EditText searchField = (EditText) header.findViewById(R.dialog.searchfield);
			final TextView searchResults = (TextView) header.findViewById(R.dialog.searchresults);
			searchResults.setText(adapter.getCount() + " result(s)");
			searchField.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View view, boolean hasFocus) {
					if (!hasFocus) hideSoftKeyboard(activity, view);
				}
			});
			searchField.addTextChangedListener(new TextWatcher() {
				@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
				@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				@Override
				public void afterTextChanged(Editable searchText) {
					adapter.getFilter().filter(searchText);
					final Handler handler = new Handler(new Handler.Callback() {
						@Override
						public boolean handleMessage(Message msg) {
							searchResults.setText(adapter.getCount() + " result(s)");
							return true;
						}
					});
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							handler.sendEmptyMessage(0);
						}
					}, 200);
				}
			});
			StandardDialogProperties properties = getStandardDialogProperties(activity.getResources(), type);
			TextView titleTextView = (TextView) header.findViewById(R.dialog.title);
			titleTextView.setText(properties.getlabel());
			TextView infoText = (TextView) header.findViewById(R.dialog.info_text);
			infoText.setText(properties.getInfoText());
			Button search = (Button) header.findViewById(R.dialog.button_search);
			search.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (searchArea.getVisibility() == View.GONE) {
						searchArea.setVisibility(View.VISIBLE);
						searchField.requestFocus();
						showSoftKeyboard(activity, searchField);
						v.setBackgroundColor(activity.getResources().getColor(android.R.color.black));
					} else {
						searchField.setText("");
						searchField.clearFocus();
						searchArea.setVisibility(View.GONE);
						v.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));
					}
				}
			});
			infoArea.setVisibility((DimeClient.getSettings().isDialogInfoAreaDisplayed()) ? View.VISIBLE : View.GONE);
			Button infoButton = (Button) header.findViewById(R.dialog.button_info);
			infoButton.setBackgroundColor((DimeClient.getSettings().isDialogInfoAreaDisplayed()) ? activity.getResources().getColor(android.R.color.black) : activity.getResources().getColor(android.R.color.transparent));
			infoButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (infoArea.getVisibility() == View.GONE) {
						infoArea.setVisibility(View.VISIBLE);
						DimeClient.getSettings().setDialogInfoAreaDisplayed(true);
						v.setBackgroundColor(activity.getResources().getColor(android.R.color.black));
					} else {
						infoArea.setVisibility(View.GONE);
						DimeClient.getSettings().setDialogInfoAreaDisplayed(false);
						v.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));
					}
				}
			});
			builder.setCustomTitle(header);
			builder.setCancelable(true);
			builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (activity instanceof IResultOfStandardDialog) {
						IResultOfStandardDialog rosdi = (IResultOfStandardDialog) activity;
						if (adapter instanceof BaseAdapter_Dialog_Sharing_Profile) {
							List<DisplayableItem> items = ((BaseAdapter_Dialog_Sharing_Profile) adapter).getSelectionItems();
 							if(items.size() == 1) {
								ProfileItem item = (ProfileItem) items.get(0);
								rosdi.handleResult(new ResultObjectProfileSharing(item));
							}
						} else if (adapter instanceof BaseAdapter_Standard || adapter instanceof BaseAdapter_Livepost) {
							List<DisplayableItem> displayables = adapter.getSelectionItems();
							rosdi.handleResult(new ResultObjectDisplayable(displayables, type));
						} else if (adapter instanceof BaseAdapter_ServiceAdapter) {
							List<DisplayableItem> items = ((BaseAdapter_ServiceAdapter) adapter).getSelectionItems();
 							if(items.size() == 1) {
								ServiceAdapterItem item = (ServiceAdapterItem) items.get(0);
								rosdi.handleResult(new ResultObjectServiceAdapter(item));
 							}
						}
					}
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			adapter.init(activity, mrContext, items);
		} catch (Exception e) {
			Toast.makeText(activity, "Error loading items!", Toast.LENGTH_LONG).show();
		}
	}

	public static Builder createAlertDialogBuilder(Context context, String title, boolean isCancelable) {
		Builder builder = new Builder(context);
		builder.setTitle(title);
		builder.setCancelable(isCancelable);
		if (isCancelable) {
			builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		return builder;
	}

	public static Builder createCustomAlertDialogBuilder(Context context, String title, boolean isCancelable, View view) {
		Builder builder = createAlertDialogBuilder(context, title, isCancelable);
		builder.setView(view);
		return builder;
	}

	public static AlertDialog createInfoDialog(Context context, String infotext, String buttonlabel) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(infotext).setCancelable(false).setPositiveButton(buttonlabel, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		return alert;
	}
	
	public static ProgressDialog createCustonProgressDialog(Context context, String dialogText) {
		ProgressDialog dialog = ProgressDialog.show(context, null, dialogText, true, true, new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		});
		dialog.setCancelable(true);
    	((TextView) dialog.findViewById(android.R.id.message)).setTextColor(Color.WHITE);
    	return dialog;
	}

	public static AlertDialog displayAlertDialog(Builder builder, boolean showSoftInput) {
		AlertDialog alert = builder.create();
		if(showSoftInput) alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		alert.show();
		if (alert.getButton(AlertDialog.BUTTON_NEGATIVE) == null) {
			alert.getButton(AlertDialog.BUTTON_POSITIVE).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		}
		return alert;
	}
	
	public static Dialog createActionDialog(Context context, List<String> names, OnClickListener listener, List<String> selectedGUIDs) {
		Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		dialog.setTitle("Select Action");
		((TextView) dialog.findViewById(android.R.id.title)).setTextColor(Color.WHITE);
		((TextView) dialog.findViewById(android.R.id.title)).setPadding(0, 0, 0, 0);
		dialog.setContentView(R.layout.dialog_action);
		dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon_bar_action);
		TextView selection = (TextView) dialog.findViewById(R.action_dialog.selection_text);
		if (selectedGUIDs != null) {
			String text = String.valueOf(selectedGUIDs.size());
			selection.setText(text);
		} else {
			selection.setVisibility(View.GONE);
			dialog.findViewById(R.action_dialog.selection_bar).setVisibility(View.GONE);
		}
		LinearLayout generalContainer = (LinearLayout) dialog.findViewById(R.action_dialog.actions_general);
		LinearLayout selectionContainer = (LinearLayout) dialog.findViewById(R.action_dialog.actions_selection);
		LinearLayout selectionBar = (LinearLayout) dialog.findViewById(R.action_dialog.selection_bar);
		int i = 0;
		for (String name : names) {
			Button tmp = new Button(context);
			tmp.setBackgroundResource(R.drawable.button_col4_grey);
			tmp.setText(name);
			tmp.setGravity(Gravity.CENTER_VERTICAL);
			int id = UIHelper.getResourceIdByName(context.getResources(), name);
			StateListDrawable sld = (StateListDrawable) context.getResources().getDrawable(id);
			tmp.setCompoundDrawablesWithIntrinsicBounds(sld, null, null, null);
			tmp.setCompoundDrawablePadding(getDPvalue(10));
			tmp.setId(id);
			LinearLayout.LayoutParams lpms = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lpms.setMargins(0, getDPvalue(2), 0, 0);
			tmp.setLayoutParams(lpms);
			tmp.setOnClickListener(listener);
			if (selectedGUIDs != null && ((selectedGUIDs.isEmpty() && name.contains("select")) || (selectedGUIDs.size() < 2 && name.contains("Merge")))) {
				tmp.setEnabled(false);
			}
			if(name.contains("select")) {
				selectionContainer.addView(tmp);
				i++;
			} else {
				generalContainer.addView(tmp);
			}
		}
		if(i == 0) {
			selectionContainer.setVisibility(View.GONE);
			selectionBar.setVisibility(View.GONE);
		}
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		Window window = dialog.getWindow();
		lp.copyFrom(window.getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);
		return dialog;
	}

	public static void showItemActionDialog(Context context, GenItem item) {
		Resources res = context.getResources();
		String[] actions = { res.getString(R.string.action_editItem), res.getString(R.string.action_share), res.getString(R.string.action_unshare) };
		ItemActionListener listener = new ItemActionListener(context, new DimeIntentObject(item));
		Dialog dialog = createActionDialog(context, Arrays.asList(actions), listener, null);
		listener.setDialog(dialog);
		dialog.show();
		if(ModelHelper.isDisplayableItem(item.getMType())) {
			DisplayableItem di = (DisplayableItem) item;
			if(!di.getUserId().equals(Model.ME_OWNER)) {
				Button buttonEdit = (Button) dialog.findViewById(R.string.action_editItem);
				if(buttonEdit != null) buttonEdit.setEnabled(false);
				Button buttonShare = (Button) dialog.findViewById(R.string.action_share);
				if(buttonShare != null) buttonShare.setEnabled(false);
				Button buttonUnshare = (Button) dialog.findViewById(R.string.action_unshare);
				if(buttonUnshare != null) buttonUnshare.setEnabled(false);
			}
			if(item.getMType().equals(TYPES.PERSON)) {
				PersonItem person = (PersonItem) di;
				if(person.getDefaultProfileGuid().length() == 0) {
					Button button = (Button) dialog.findViewById(R.string.action_share);
					if(button != null) button.setEnabled(false);
				}
			}
		}
	}

	/** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** header functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */

	public static void inflateStandardHeader(final Activity activity, final DisplayableItem di, ModelRequestContext mrContext, LinearLayout header) {
		if (di != null && header != null) {
            if(di.getUserId().equals(Model.ME_OWNER)) header.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity, Activity_Edit_Item_Dialog.class);
					activity.startActivity(DimeIntentObjectHelper.populateIntent(intent, new DimeIntentObject(di)));
				}
			});
			ImageView image = (ImageView) header.findViewById(R.header.image);
			TextView nameText = (TextView) header.findViewById(R.header.name_text);
			TextView changedText = (TextView) header.findViewById(R.header.changed_text);
			TextView childrenText = (TextView) header.findViewById(R.header.children_text);
			TextView barText = (TextView) header.findViewById(R.header.bar_text);
			TextView levelText = (TextView) header.findViewById(R.header.level_text);
			LinearLayout barArea = (LinearLayout) header.findViewById(R.header.bar_area);
			View bar = (View) header.findViewById(R.header.bar);
			ImageView lock = (ImageView) header.findViewById(R.id.locked);
			String name = (ModelHelper.isParentable(di)) ? di.getName() + " (" + di.getItems().size() + ")" : di.getName();
			nameText.setText(name);
			changedText.setText(formatDateByMillis(di.getLastUpdated()));
			ImageHelper.loadImageAsynchronously(image, di, activity);
			lock.setVisibility(di.getUserId().equals(Model.ME_OWNER) ? View.GONE : View.VISIBLE);
			AndroidModelHelper.loadGroupsOfChildAsynchronously(activity, mrContext.owner, di, childrenText);
			if(di.getMType().equals(TYPES.GROUP)) {
				barArea.setVisibility(View.GONE);
			} else {
				barArea.setVisibility(View.VISIBLE);
				barText.setText(getTrustOrPrivacyLabelOfProgressBar(di));
				levelText.setText(getWarningText(di));
				bar.setBackgroundColor(getWarningColor(activity, di));
			}
		} else {
			Toast.makeText(activity, "Error loading item!", Toast.LENGTH_LONG).show();
			activity.getParent().finish();
		}
	}
	
	private static LinearLayout createNewExpandedViewRow(Context context) {
		LinearLayout.LayoutParams lpmsLayout = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout ll = new LinearLayout(context);
		lpmsLayout.setMargins(0, 0, 0, getDPvalue(5));
		ll.setLayoutParams(lpmsLayout);
		ll.setPadding(getDPvalue(5), 0, getDPvalue(5), 0);
		ll.setBackgroundColor(context.getResources().getColor(R.color.background_grey_metabar));
		return ll;
	}

	/** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** adapter functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */
	
	public static void updateInfoContainer(Context context, LinearLayout infoContainer, DisplayableItem di) {
		LinearLayout llchanged = createNewExpandedViewRow(context);
		TextView labelTV = createTextView(context, R.style.dimeTheme, Typeface.NORMAL, 11, null, true);
		labelTV.setText("changed:");
		TextView valueTV = createTextView(context, R.style.dimeTheme, Typeface.NORMAL, 11, null, true);
		valueTV.setText(UIHelper.formatDateByMillis(di.getLastUpdated()));
		valueTV.setPadding(getDPvalue(15), 0, 0, 0);
		llchanged.addView(labelTV);
		llchanged.addView(valueTV);
		infoContainer.addView(llchanged);
		if(!di.getMType().equals(TYPES.GROUP)) {
			LinearLayout llContainer = new LinearLayout(context);
			llContainer.setOrientation(LinearLayout.VERTICAL);
			LinearLayout llbar = createNewExpandedViewRow(context);
			TextView labelTVbar = createTextView(context, R.style.dimeTheme, Typeface.NORMAL, 11, null, true);
			TextView barText = new TextView(context);
			labelTVbar.setText(getTrustOrPrivacyLabelOfProgressBar(di));
			barText.setTextSize(11);
			barText.setPadding(getDPvalue(15), 0, 0, 0);
			barText.setText(getWarningText(di));
			View bar = new View(context);
			bar.setBackgroundColor(getWarningColor(context, di));
			LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getDPvalue(5));
			barParams.setMargins(0, -getDPvalue(5), 0, getDPvalue(5));
			bar.setLayoutParams(barParams);
			llbar.addView(labelTVbar);
			llbar.addView(barText);
			llContainer.addView(llbar);
			llContainer.addView(bar);
			infoContainer.addView(llContainer);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void updatePreviewContainer(Context context, LinearLayout previewContainer, List<DisplayableItem> displayables, DisplayableItem item, TextView emptyContainer) {
		String empty = "";
		int counter = 0;
		switch (item.getMType()) {
		case DATABOX:
			for (DisplayableItem di : displayables) {
				LinearLayout ll = createNewExpandedViewRow(context);
				TextView resourceName = createTextView(context, R.style.dimeTheme, Typeface.NORMAL, 11, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f), true);
				if (counter > 4) {
					resourceName.setText("...");
					ll.addView(resourceName);
					previewContainer.addView(ll);
					break;
				} else {
					resourceName.setText(di.getName());
					ll.addView(resourceName);
					TextView resourceFileSize = new TextView(context);
					resourceFileSize.setTextSize(11);
					resourceFileSize.setText(String.valueOf(((ResourceItem) di).getFileSize()) + " bytes");
					resourceFileSize.setGravity(Gravity.RIGHT);
					ll.addView(resourceFileSize);
					previewContainer.addView(ll);
				}
				counter++;
			}
			empty = "no resources assigned to databox";
			break;
		case GROUP:
			LinearLayout llGroup = new LinearLayout(context);
			for (DisplayableItem pi : displayables) {
				ImageView persimage = new ImageView(context);
				ImageHelper.loadImageAsynchronously(persimage, pi, context);
				LinearLayout.LayoutParams lpms = new LinearLayout.LayoutParams(getDPvalue(40), getDPvalue(40));
				lpms.setMargins(getDPvalue(5), 0, 0, 0);
				persimage.setLayoutParams(lpms);
				if (counter == 0 || counter % 6 == 0) {
					llGroup = new LinearLayout(context);
					llGroup.setPadding(0, 0, 0, getDPvalue(10));
					llGroup.setGravity(Gravity.LEFT);
					previewContainer.addView(llGroup);
				}
				llGroup.addView(persimage);
				counter++;
			}
			empty = "no persons assigned to group";
			break;
		case PROFILE:
			for (ProfileAttributeItem pai : (List<ProfileAttributeItem>) (Object) displayables) {
				if (pai != null) {
					TextView labelCat = createTextView(context, -1, Typeface.BOLD, 11, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT), true);
					labelCat.setText(pai.getCategory() + ((pai.getName() != null && pai.getName().length() > 0 ) ? " (" + pai.getName() + ")" : ""));
					labelCat.setPadding(getDPvalue(5), getDPvalue(5), 0, 0);
					boolean hasChild = false;
					int index = 0;
					for (String key : pai.getValue().keySet()) {
						String label = pai.getLabelForValueKey(key);
						String value = pai.getValue().get(key);
						if (value.length() > 0) {
							if (!hasChild) {
								hasChild = true;
								index = previewContainer.getChildCount();
							}
							LinearLayout ll = createNewExpandedViewRow(context);
							TextView labelTV = createTextView(context, R.style.dimeTheme, Typeface.NORMAL, 11, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f), true);
							labelTV.setText(label);
							TextView valueTV = createTextView(context, R.style.dimeTheme, Typeface.NORMAL, 11, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f), true);
							valueTV.setText(value);
							ll.addView(labelTV);
							ll.addView(valueTV);
							previewContainer.addView(ll);
						}
					}
					if (hasChild) {
						previewContainer.addView(labelCat, index);
					}
				}
			}
			empty = "no attributes assigned to profile";
			break;
		case LIVEPOST:
			TextView text = createTextView(context, R.style.dimeTheme, Typeface.NORMAL, 11, null, false);
			text.setPadding(getDPvalue(10), 0, getDPvalue(10), getDPvalue(5));
			text.setText(((LivePostItem) item).getText());
			previewContainer.addView(text);
			empty = "<empty>";
			break;
		default:
			break;
		}
		if((displayables != null && displayables.size() == 0) || (item.getMType().equals(TYPES.LIVEPOST) && ((LivePostItem) item).getText().length() == 0)) {
			emptyContainer.setVisibility(View.VISIBLE);
			emptyContainer.setText(empty);
		} else {
			emptyContainer.setVisibility(View.GONE);
		}
	}

	public static void updateSharedContainerShareables(Context context, LinearLayout previewContainer, List<DisplayableItem> displayables, TextView emptyContainer) {
		int counter = 0;
		if(displayables == null) {
			emptyContainer.setVisibility(View.VISIBLE);
			emptyContainer.setText("error occurred loading shared items");
		} else if(displayables.size() > 0) {
			emptyContainer.setVisibility(View.GONE);
			for (DisplayableItem di : displayables) {
				LinearLayout ll = createNewExpandedViewRow(context);
				TextView resourceName = createTextView(context, R.style.dimeTheme, Typeface.NORMAL, 11, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f), true);
				if (counter > 4) {
					resourceName.setText("...");
					ll.addView(resourceName);
					previewContainer.addView(ll);
					break;
				} else {
					ImageView image = new ImageView(context);
					int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(value, value);
					params.setMargins(0, 1, getDPvalue(10), 0);
					image.setLayoutParams(params);
					ImageHelper.loadImageAsynchronously(image, di, context);
					resourceName.setText((ModelHelper.isParentable(di)) ? di.getName() + " ("+di.getItems().size()+")": di.getName());
					ll.addView(image);
					ll.addView(resourceName);
					previewContainer.addView(ll);
				}
				counter++;
			}
		} else {
			emptyContainer.setVisibility(View.VISIBLE);
			emptyContainer.setText("nothing shared");
		}
	}

	public static void updateSharedContainerAgents(Context context, LinearLayout previewContainer, Iterable<ACLPackage> aclPackage, TextView emptyContainer) {
		try {
			if(aclPackage.iterator().hasNext()) {
				emptyContainer.setVisibility(View.GONE);
				for (ACLPackage acl : aclPackage) {
					ProfileItem profile = ModelHelper.getProfileWithSaid(DimeClient.getMRC(new SilentLoadingViewHandler()), acl.getSaidSender()); //FIXME Load async??
					TextView profileHeadline = createTextView(context, 0, Typeface.BOLD, 11, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT), false);
					profileHeadline.setText("@" + profile.getName());
					profileHeadline.setPadding(getDPvalue(10), 0, 0, 0);
					previewContainer.addView(profileHeadline);
					int counter = 0;
					for (ACLPerson persons : acl.getPersons()) {
						LinearLayout ll = createNewExpandedViewRow(context);
						TextView personName = createTextView(context, R.style.dimeTheme, Typeface.NORMAL, 11, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f), true);
						if (counter > 4) {
							personName.setText("...");
							ll.addView(personName);
							previewContainer.addView(ll);
							break;
						} else {
							PersonItem pi = (PersonItem) AndroidModelHelper.getGenItemSynchronously(context, new DimeIntentObject(persons.getPersonId(), TYPES.PERSON));
							personName.setText(pi.getName());
							ll.addView(personName);
							previewContainer.addView(ll);
						}
						counter++;
					}
					counter = 0;
					for (String group : acl.getGroups()) {
						LinearLayout ll = createNewExpandedViewRow(context);
						TextView groupName = createTextView(context, R.style.dimeTheme, Typeface.NORMAL, 11, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f), true);
						if (counter > 4) {
							groupName.setText("...");
							ll.addView(groupName);
							previewContainer.addView(ll);
							break;
						} else {
							GroupItem gi = (GroupItem) AndroidModelHelper.getGenItemSynchronously(context, new DimeIntentObject(group, TYPES.GROUP));
							groupName.setText(gi.getName() + "(group)");
							ll.addView(groupName);
							previewContainer.addView(ll);
						}
						counter++;
					}
				}
			} else {
				emptyContainer.setVisibility(View.VISIBLE);
				emptyContainer.setText("nobody has access");
			}
		} catch (Exception e) { }
	}
	
	/** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** widget functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */
	
	public static TextView createEmptyListWidget(Context context) {
		TextView emptyText = new TextView(context);
        emptyText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        emptyText.setText("No items in list");
        emptyText.setVisibility(View.GONE);
        emptyText.setId(android.R.id.empty);
        emptyText.setGravity(Gravity.CENTER_HORIZONTAL);
        emptyText.setPadding(0, getDPvalue(20), 0, getDPvalue(20));
        return emptyText;
	}

	public static LinearLayout createSharingWidget(final Context context, final DisplayableItem item, final List<DisplayableItem> items) {
		final LinearLayout ll = new LinearLayout(context);
		ll.setPadding(0, 0, 0, getDPvalue(10));
		ImageView image = new ImageView(context);
		int id = 0;
		switch (item.getMType()) {
		case GROUP:
			id = R.drawable.icon_small_group;
			break;
		case RESOURCE:
			id = R.drawable.icon_small_resource;
			break;
		case DATABOX:
			id = R.drawable.icon_small_databox;
			break;
		case PERSON:
			id = R.drawable.icon_small_person;
			break;
		case LIVEPOST:
			id = R.drawable.icon_small_communication;
			break;
		default:
			id = R.drawable.icon_small_info;
			break;
		}
		Drawable drawable = context.getResources().getDrawable(id).mutate();
		drawable.setColorFilter(getWarningColor(context, item), Mode.SRC_ATOP);
		image.setImageDrawable(drawable);
		image.setPadding(0, 0, getDPvalue(8), 0);
		ImageButton recycleButton = new ImageButton(context);
		recycleButton.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
		recycleButton.setImageResource(R.drawable.icon_small_recycle);
		recycleButton.setPadding(0, 0, 0, 0);
		recycleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(context instanceof Activity_Share_Dialog) {
					items.remove(item);
					((Activity_Share_Dialog) context).updateViewOnSelectionChanged();
				} else if(context instanceof Activity_Share_New_Livepost_Dialog){
					items.remove(item);
					((Activity_Share_New_Livepost_Dialog) context).updateViewOnSelectionChanged();
				} else if(context instanceof Activity_Unshare_Dialog){
					items.add(item);
					((LinearLayout) ll.getParent()).removeView(ll);
				}
			}
		});
		TextView text = createTextView(context, -1, -1, -1, new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f), true);
		text.setText(item.getName());
		text.setGravity(Gravity.CENTER_VERTICAL);
		ll.addView(image);
		ll.addView(text);
		ll.addView(recycleButton);
		return ll;
	}

	@SuppressWarnings("deprecation")
	public static View createWarningWidget(final Context context, AdvisoryProperties advisoryProperty) {
		LinearLayout llparent = createLinearLayout(context, LinearLayout.VERTICAL);
		llparent.setPadding(0, 0, 0, getDPvalue(10));
		LinearLayout llchild = new LinearLayout(context);
		final ImageView image = new ImageView(context);
		image.setPadding(0, 0, getDPvalue(8), 0);
		image.setImageResource(advisoryProperty.getDrawableId());
		final ImageView expand = new ImageView(context);
		expand.setBackgroundResource(R.drawable.button_expand_bar);
		TextView headline = createTextView(context, -1, -1, 14, new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f), true);
		headline.setText(advisoryProperty.getHeadline());
		headline.setGravity(Gravity.CENTER_VERTICAL);
		final TextView text = createTextView(context, R.style.dimeTheme, Typeface.NORMAL, 12, null, false);
		text.setText(advisoryProperty.getAdvisoryText());
		text.setVisibility(View.GONE);
		llchild.setTag(true);
		llchild.setPadding(0, getDPvalue(5), 0, 0);
		llchild.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v.getTag().equals(true)){
					v.setTag(false);
					expand.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_collapse_bar));
					text.setVisibility(View.VISIBLE);
					text.setPadding(image.getWidth(), 0, expand.getWidth(), 5);
				} else {
					v.setTag(true);
					expand.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_expand_bar));
					text.setVisibility(View.GONE);
					text.setPadding(image.getWidth(), 0, expand.getWidth(), 5);
				}
			}
		});
		llchild.addView(image);
		llchild.addView(headline);
		llchild.addView(expand);
		llparent.addView(llchild);
		llparent.addView(text);
		return llparent;
	}
	
	public static String getTrustOrPrivacyLabelOfProgressBar(DisplayableItem di) {
		String s = "";
		if(ModelHelper.isAgent(di.getMType())) {
			s = "Trust:";
		} else if(ModelHelper.isShareable(di.getMType())) {
			s = "Privacy:";
		}
		return s;
	}
	
	public static String updateEditTrustOrPrivacyLevelHint(Context context, DisplayableItem di) {
		String hint = "";
		if(ModelHelper.isAgent(di.getMType())) {
			hint = context.getResources().getString(R.string.edit_trust_value_hint);
		} else if(ModelHelper.isShareable(di.getMType())) {
			hint = context.getResources().getString(R.string.edit_privacy_value_hint);
		}
		return hint;
	}

	/** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** format functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */

	@SuppressLint("SimpleDateFormat")
	public static String formatDateByMillis(long timeStamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeStamp);
		DateFormat df = new SimpleDateFormat("E', 'dd MMM yyyy"); // DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT)
		return df.format(cal.getTime());
	}
	
	public static String formatStringListCommaSeparated(List<String> strings) {
		StringBuilder sb = new StringBuilder();
		String delim = "";
		if(strings.size() == 0) {
			//should not occur
			sb.append("\'" + "empty" + "\'");
		} else {
			for (int i = 0; i < strings.size(); i++) {
				sb.append(delim).append(strings.get(i));
				delim = ", ";
			}
		}
		return sb.toString();
	}
	
	public static String formatStringListListSeparated(List<String> strings) {
		StringBuilder sb = new StringBuilder();
		String delim = " -";
		if(strings.size() == 0) {
			//should not occur
			sb.append(delim).append("\'" + "empty" + "\'");
		} else {
			for (int i = 0; i < strings.size(); i++) {
				sb.append(delim).append("\'" + strings.get(i) + "\'");
				delim = System.getProperty("line.separator") + " -";
			}
		}
		return sb.toString();
	}
	
	public static String concatNamesLists(String s1, String s2) {
		String result = "";
		if (s1.length() > 0 && s2.length() > 0) {
			result = s1 + ", " + s2;
		} else if (s1.length() > 0) {
			result = s1;
		} else if (s2.length() > 0) {
			result = s2;
		}
		return result;
	}
	
	@SuppressLint("DefaultLocale") 
    public static String formatStringOnlyFirstCharUpperCase(String string) {
    	string = string.toLowerCase();
    	String firstChar = "" + string.charAt(0);
    	return firstChar.toUpperCase() + string.substring(1);
    }
	
	public static String formatDoubleToPercentage(double score) {
		return ((int) ((score/1.0d) * 100)) + "%";
	}

	/** ------------------------------------------------------------------------------------------------------------------------------------------
	 ** resources functions
	 ** ------------------------------------------------------------------------------------------------------------------------------------------ */
	
	public static NotificationProperties getNotificationProperties(Context context, UserNotificationItem userNotification) {
		DimeIntentObject dio = new DimeIntentObject();
        String notificationText = "Error occurred trying to get notification text!";
        String sender = "Personal Server";
        Intent intent = new Intent();           
        int drawableId = R.drawable.icon_black_info;
        if(userNotification.getUnType().equals(UN_TYPE.REF_TO_ITEM)) {
        	try {
	            UNEntryRefToItem entry = (UNEntryRefToItem) userNotification.getUnEntry();
	        	dio = new DimeIntentObject(entry.getGuid(), entry.getType(), entry.getUserId());
				if(entry.getOperation().equals(UNEntryRefToItem.OPERATION_SHARED) || entry.getOperation().equals(UNEntryRefToItem.OPERATION_UNSHARED)) {
					if (entry.getType().equals(TYPES.GROUP)) {
					    intent = new Intent(context, TabActivity_Group_Detail.class);
					    drawableId = R.drawable.icon_black_group;
					} else if (entry.getType().equals(TYPES.PERSON)) {
					    intent = new Intent(context, TabActivity_Person_Detail.class);
					    drawableId = R.drawable.icon_black_person;
					} else if (entry.getType().equals(TYPES.DATABOX)) {
					    intent = new Intent(context, TabActivity_Resource_Detail.class);
					    drawableId = R.drawable.icon_black_databox;
					} else if (entry.getType().equals(TYPES.RESOURCE)) {
					    intent = new Intent(context, TabActivity_Resource_Detail.class);
					    drawableId = R.drawable.icon_black_data_doc;
					} else if(entry.getType().equals(TYPES.LIVEPOST)){
					    intent = new Intent(context, TabActivity_Livepost_Detail.class);
					    drawableId = R.drawable.icon_black_communication;
					} else {
						throw new Exception("type not supported yet");
					}
					try {
		        		PersonItem pi = (PersonItem) AndroidModelHelper.getGenItemSynchronously(context, new DimeIntentObject(entry.getUserId(), TYPES.PERSON));
		        		if(pi != null) sender = pi.getName();
			        	DisplayableItem di = (DisplayableItem) AndroidModelHelper.getGenItemSynchronously(context, new DimeIntentObject(entry.getGuid(), entry.getType(), entry.getUserId()));
				        if(di != null) notificationText = UIHelper.formatStringOnlyFirstCharUpperCase(entry.getType().toString()) + " \'" + di.getName() + "\' was " + (entry.getOperation().equals(UNEntryRefToItem.OPERATION_SHARED) ? "shared" : "unshared") + "!";
		        	} catch (LoadingAbortedRuntimeException e) {
	    				notificationText = "\'unknown item\' was " + (entry.getOperation().equals(UNEntryRefToItem.OPERATION_SHARED) ? "shared" : "unshared") + "!";
	    				intent = new Intent();
	    			}
				} else {
					if(entry.getOperation().equals(UNEntryRefToItem.OPERATION_INC_PRIV)) {
						drawableId = R.drawable.icon_color_privacyup;
					} else if(entry.getOperation().equals(UNEntryRefToItem.OPERATION_DEC_PRIV)) {
						drawableId = R.drawable.icon_color_privacydown;
					} else if(entry.getOperation().equals(UNEntryRefToItem.OPERATION_INC_TRUST)) {
						drawableId = R.drawable.icon_color_trustup;
					} else if(entry.getOperation().equals(UNEntryRefToItem.OPERATION_DEC_TRUST)) {
						drawableId = R.drawable.icon_color_trustdown;
					}
					String value = (ModelHelper.isShareable(entry.getType())) ? " privay " : " trust ";
					String operation = (entry.getOperation().equals(UNEntryRefToItem.OPERATION_DEC_PRIV) || entry.getOperation().equals(UNEntryRefToItem.OPERATION_DEC_TRUST)) ? "decrease " : "increase ";
					notificationText = "The system suggests to " + operation + value +  "value of the " + ModelHelper.getStringType(entry.getType()) + "!";
					intent = new Intent(context, Activity_Edit_Item_Dialog.class);
				}
			} catch (ModelTypeNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    } else if(userNotification.getUnType().equals(UN_TYPE.ADHOC_GROUP_RECOMMENDATION)){
	    	UNEntryAdhocGroupRecommendation entry = (UNEntryAdhocGroupRecommendation) userNotification.getUnEntry();
	    	notificationText = "The system recognized the new adhoc group "+ entry.getName() + "!";
	    	drawableId = R.drawable.icon_black_group;
	    	dio = new DimeIntentObject(entry.getGuid(), TYPES.GROUP);
	    	intent = new Intent(context, TabActivity_Group_Detail.class);
	    } else if(userNotification.getUnType().equals(UN_TYPE.MERGE_RECOMMENDATION)){
	    	UNEntryMergeRecommendation entry = (UNEntryMergeRecommendation) userNotification.getUnEntry();
	    	notificationText = "The system suggests to merge " + entry.getSourceName() + " with " + entry.getTargetName() + "!";
	    	dio = new DimeIntentObject(userNotification.getGuid(), TYPES.USERNOTIFICATION);
	    	intent = new Intent(context, ListActivity_Merge_Dialog.class);
	    	drawableId = R.drawable.icon_black_people;
	    } else if(userNotification.getUnType().equals(UN_TYPE.MESSAGE)){
	    	UNEntryMessage entry = (UNEntryMessage) userNotification.getUnEntry();
	    	notificationText = entry.getMessage() + " Link: " + entry.getLink();
	    	drawableId = R.drawable.icon_small_info;
	    } else if(userNotification.getUnType().equals(UN_TYPE.SITUATION_RECOMMENDATION)){
	    	UNEntrySituationRecommendation entry = (UNEntrySituationRecommendation) userNotification.getUnEntry();
	    	SituationItem si = (SituationItem) AndroidModelHelper.getGenItemSynchronously(context, new DimeIntentObject(entry.getGuid(), TYPES.SITUATION));
	    	notificationText = "The system recognized " + si.getName() + " with a prohability of " + UIHelper.formatDoubleToPercentage(entry.getScore()) + " as current situation! Do you confirm to activate it?";
	    	drawableId = R.drawable.icon_black_situation;
	    	dio = new DimeIntentObject(TYPES.SITUATION);
	    	intent = new Intent(context, TabActivity_Situations.class);
	    } else {
	    	return null;
	    }
	    return new NotificationProperties(notificationText, DimeIntentObjectHelper.populateIntent(intent, dio), drawableId, sender);
	}
	
	public static AdvisoryProperties getAdvisoryProperties(Context context, AdvisoryItem ai) {
		String headline = "";
		String warningText = "";
		int imageId = 0;
		if (ai.getWarningLevel() <= 0.5) {
			imageId = R.drawable.icon_small_share_severe;
		} else {
			imageId = R.drawable.icon_small_share_critical;
		}
		String warningType = ai.getWarningType();
		// untrusted
		if(warningType.equals(AdvisoryItem.WARNING_TYPES[0])) {
			headline = context.getString(R.string.sharing_warning_untrusted);
			WarningAttributesUntrusted attributes = (WarningAttributesUntrusted) ai.getAttributes();
			warningText += getWarningText(AndroidModelHelper.getTrustOrPrivacyLevelForDisplayableItem(attributes.getPrivacyValue())) + " privacy: " 
					+ System.getProperty("line.separator")
					+ formatStringListListSeparated(AndroidModelHelper.getListOfNamesOfGuidList(context, attributes.getPrivateResources(), true))
					+ System.getProperty("line.separator")
					+ getWarningText(AndroidModelHelper.getTrustOrPrivacyLevelForDisplayableItem(attributes.getTrustValue())) + " trust:"
					+ System.getProperty("line.separator")
					+ formatStringListListSeparated(AndroidModelHelper.getListOfNamesOfGuidList(context, attributes.getUntrustedAgents(), false));
		}
		// disjunct_groups
		else if(warningType.equals(AdvisoryItem.WARNING_TYPES[1])) {
			headline = context.getString(R.string.sharing_warning_disjunct_groups);
			WarningAttributesDisjunctGroups attributes = (WarningAttributesDisjunctGroups) ai.getAttributes();
			warningText += formatStringListListSeparated(AndroidModelHelper.getListOfNamesOfGuidList(context, attributes.getConcernedPersons(), false))
					+ System.getProperty("line.separator")
					+ "previous recepients:"
					+ System.getProperty("line.separator")
					+ formatStringListListSeparated(AndroidModelHelper.getListOfNamesOfGuidList(context, attributes.getPreviousSharedGroups(), false));
		}
		// profile_not_shared
		else if(warningType.equals(AdvisoryItem.WARNING_TYPES[2])) {
			headline = context.getString(R.string.sharing_warning_unshared_profile);
			WarningAttributesProfileNotShared attributes = (WarningAttributesProfileNotShared) ai.getAttributes();
			warningText += "the selected profile was never shared with:"
					+ System.getProperty("line.separator")
					+ formatStringListListSeparated(AndroidModelHelper.getListOfNamesOfGuidList(context, attributes.getPersonGuids(), false));
		}
		// too_many_resources
		else if(warningType.equals(AdvisoryItem.WARNING_TYPES[3])) {
			headline = context.getString(R.string.sharing_warning_too_many_resources);
			WarningTooManyResources attributes = (WarningTooManyResources) ai.getAttributes();
			warningText += attributes.getNumberOfResources() 
					+ " items selected!";
		}
		// too_many_receivers
		else if(warningType.equals(AdvisoryItem.WARNING_TYPES[4])) {
			headline = context.getString(R.string.sharing_warning_too_many_receivers);
			WarningTooManyReceivers attributes = (WarningTooManyReceivers) ai.getAttributes();
			warningText += attributes.getNumberOfReceivers() +
					" recipients selected!";
		}
		// agent_not_valid_for_sharing
		else if(warningType.equals(AdvisoryItem.WARNING_TYPES[5])) {
			headline = context.getString(R.string.sharing_warning_agent_not_valid_for_sharing);
			WarningAgentNotValidForSharing attributes = (WarningAgentNotValidForSharing) ai.getAttributes();
			warningText += formatStringListListSeparated(AndroidModelHelper.getListOfNamesOfGuidList(context, attributes.getAgentsNotValidForSharing(), false))
					+ System.getProperty("line.separator")
					+ ((attributes.getParentGroup().length() > 0) ? "of group " + AndroidModelHelper.getOwnItemFromStorage(attributes.getParentGroup(), false).getName() + "." + System.getProperty("line.separator") : "")
					+ "You can only share to persons with a di.me account!";
		}
		// sharing_not_possible
		else if(warningType.equals(AdvisoryItem.WARNING_TYPES[6])) {
			headline = context.getString(R.string.sharing_warning_not_possible);
			warningText = context.getString(R.string.sharing_warning_not_possible_detailed);
			imageId = R.drawable.icon_small_info;
		}
		return new AdvisoryProperties(headline, warningText, imageId);
	}
	
	private static StandardDialogProperties getStandardDialogProperties(Resources res, RESULT_OBJECT_TYPES type) {
		String label = "";
		String infoText = "";
		switch (type) {
		case SERVICE_CONNECTION:
			label = res.getString(R.string.service_connection_dialog_label);
			infoText = res.getString(R.string.service_connection_dialog_info);
			break;
		case SHARING_DATABOXES:
			label = "Select databoxes";
			infoText = res.getString(R.string.sharing_dialog_select_databoxes_info);
			break;
		case SHARING_GROUPS:
			label = "Select groups";
			infoText = res.getString(R.string.sharing_dialog_select_group_info);
			break;
		case SHARING_LIVEPOSTS:
			label = "Select liveposts";
			infoText = res.getString(R.string.sharing_dialog_select_liveposts_info);
			break;
		case SHARING_PERSONS:
			label = "Select persons";
			infoText = res.getString(R.string.sharing_dialog_select_persons_info);
			break;
		case SHARING_PROFILE:
			label = "Select profile";
			infoText = res.getString(R.string.sharing_dialog_select_profile_info);
			break;
		case SHARING_RESOURCES:
			label = "Select resources";
			infoText = res.getString(R.string.sharing_dialog_select_resources_info);
			break;
		case ADD_RESOURCES_TO_DATABOX:
			label = "Select resource(s)";
			infoText = res.getString(R.string.dialog_add_resources_to_databox_info);
			break;
		case ASSIGN_RESOURCES_TO_DATABOX:
			label = "Select databoxes";
			infoText = res.getString(R.string.dialog_add_resources_to_databox_info);
			break;
		case ADD_PEOPLE_TO_GROUP:
			label = "Select people";
			infoText = res.getString(R.string.dialog_add_people_to_group_info);
			break;
		case ASSIGN_PEOPLE_TO_GROUP:
			label = "Select groups";
			infoText = res.getString(R.string.dialog_add_people_to_group_info);
			break;
		default:
			break;
		}
		return new StandardDialogProperties(label, infoText, 0);
	}
	
	public static int getWarningColor(Context context, DisplayableItem di) {
		int resId = 0;
		int level = AndroidModelHelper.getTrustOrPrivacyLevelForDisplayableItem(di);
		if (level <= 0) {
			if(ModelHelper.isAgent(di.getMType())) {
				resId = context.getResources().getColor(R.color.trust_level_low);
			}
			else {
				resId = context.getResources().getColor(R.color.privacy_level_low);
			}
		} else if (level <= 1) {
			if(ModelHelper.isAgent(di.getMType())) {
				resId = context.getResources().getColor(R.color.trust_level_medium);
			} else {
				resId = context.getResources().getColor(R.color.privacy_level_medium);
			}
		} else if (level <= 2) {
			if(ModelHelper.isAgent(di.getMType())) {
				resId = context.getResources().getColor(R.color.trust_level_high);
			}
			else {
				resId = context.getResources().getColor(R.color.privacy_level_high);
			}
		}
		return resId;
	}
	
	public static String getWarningText(DisplayableItem di) {
		return getWarningText(AndroidModelHelper.getTrustOrPrivacyLevelForDisplayableItem(di));
	}
	
	private static String getWarningText(int level) {
		String s = "";
		if (level <= 0) {
			s = "low";
		} else if (level <= 1) {
			s = "medium";
		} else if (level <= 2) {
			s ="high";
		}
		return s;
	}

	public static int getResourceIdByName(Resources res, String name) {
		int resId = R.drawable.action_place;
		// Action icons
		if (name.equals(res.getString(R.string.action_addPeopleToGroup)) || name.equals(res.getString(R.string.action_addPeopleToGroupDetail))) {
			resId = R.drawable.action_assign_to_group;
		} else if (name.equals(res.getString(R.string.action_removeSelectedPeopleFromGroup))) {
			resId = R.drawable.action_remove_person;
		} else if (name.equals(res.getString(R.string.action_renameGroup))) {
			resId = R.drawable.action_rename_group;
		} else if (name.equals(res.getString(R.string.action_mergeSelection))) {
			resId = R.drawable.action_merge;
		} else if (name.equals(res.getString(R.string.action_share)) || name.equals(res.getString(R.string.action_shareSelectedItems))) {
			resId = R.drawable.action_share;
		} else if(name.equals(res.getString(R.string.action_unshare))) { 
			resId = R.drawable.action_unshare;
		} else if (name.equals(res.getString(R.string.action_saveAsNewGroup))) {
			resId = R.drawable.action_save_as_new_group;
		} else if (name.equals(res.getString(R.string.action_addNewGroup))) {
			resId = R.drawable.action_add_group;
		} else if (name.equals(res.getString(R.string.action_deleteSelectedGroups))) {
			resId = R.drawable.action_delete_group;
		} else if (name.equals(res.getString(R.string.action_searchPublicResolverService))) {
			resId = R.drawable.action_search_public;
		} else if (name.equals(res.getString(R.string.action_importSpecificContactFromPhoneBook))) {
			resId = R.drawable.action_import_person;
		} else if (name.equals(res.getString(R.string.action_importAllContactsFromPhoneBook))) {
			resId = R.drawable.action_import_all;
		} else if (name.equals(res.getString(R.string.action_addToNewGroup))) {
			resId = R.drawable.action_add_group;
		} else if (name.equals(res.getString(R.string.action_uploadImage))) {
			resId = R.drawable.action_upload_picture;
		} else if (name.equals(res.getString(R.string.action_assignToNewDatabox))) {
			resId = R.drawable.action_add_new_databox;
		} else if (name.equals(res.getString(R.string.action_assignToDatabox))) {
			resId = R.drawable.action_assign_to_databox;
		} else if (name.equals(res.getString(R.string.action_addNewDatabox))) {
			resId = R.drawable.action_add_new_databox;
		} else if (name.equals(res.getString(R.string.action_deleteSelectedDataboxes))) {
			resId = R.drawable.action_delete_databox;
		} else if (name.equals(res.getString(R.string.action_addResourcesToDatabox))) {
			resId = R.drawable.action_assign_to_databox;
		} else if (name.equals(res.getString(R.string.action_removeResourcesFromDatabox))) {
			resId = R.drawable.action_delete_resource;
		} else if (name.equals(res.getString(R.string.action_newMessage))) {
			resId = R.drawable.action_add_new_message;
		} else if (name.equals(res.getString(R.string.action_newProfile))) {
			resId = R.drawable.action_add_profilecard;
		} else if (name.equals(res.getString(R.string.action_deleteSelectedProfiles))) {
			resId = R.drawable.action_delete_profilecard;
		} else if (name.equals(res.getString(R.string.action_addExistingAttribute))) {
			resId = R.drawable.action_add_existing_attribute;
		} else if (name.equals(res.getString(R.string.action_addNewAttribute))) {
			resId = R.drawable.action_add_new_attribute;
		} else if (name.equals(res.getString(R.string.action_deleteSelectedAttributeCategories))) {
			resId = R.drawable.action_delete_attribute;
		} else if (name.equals(res.getString(R.string.action_exit))) {
			resId = R.drawable.action_exit;
		} else if (name.equals(res.getString(R.string.action_logout))) {
			resId = R.drawable.action_logout;
		} else if (name.equals(res.getString(R.string.action_deleteSituation))) {
			resId = R.drawable.action_delete_situation;
		} else if (name.equals(res.getString(R.string.action_newSituation))) {
			resId = R.drawable.action_add_situation;
		} else if (name.equals(res.getString(R.string.action_setCurrentPositionTag))) {
			resId = R.drawable.action_set_current_position;
		} else if (name.equals(res.getString(R.string.action_removeCurrentPositionTag))) {
			resId = R.drawable.action_remove_current_position;
		} else if (name.equals(res.getString(R.string.action_setFavouriteTag))) {
			resId = R.drawable.action_set_favorite;
		} else if (name.equals(res.getString(R.string.action_removeFavouriteTag))) {
			resId = R.drawable.action_remove_favorite;
		} else if (name.equals(res.getString(R.string.action_removeAllNotifications))) {
			resId = R.drawable.action_delete_all_notifications;
		} else if (name.equals(res.getString(R.string.action_removeSelectedNotifications))) {
			resId = R.drawable.action_delete_notification;
		} else if (name.equals(res.getString(R.string.action_connectServiceAdapter))) {
			resId = R.drawable.action_add_situation;
		} else if (name.equals(res.getString(R.string.action_disconnectServiceAdapter))) {
			resId = R.drawable.action_delete_situation;
		} else if (name.equals(res.getString(R.string.action_editItem))) {
			resId = R.drawable.action_edit_item;
		} else if (name.equals(res.getString(R.string.action_removeSelectedResources))) {
			resId = R.drawable.action_delete_resource;
		} else if (name.equals(res.getString(R.string.action_uploadFile))) {
			resId = R.drawable.action_add_resources;
		} else if (name.equals(res.getString(R.string.action_removePerson))) {
			resId = R.drawable.action_remove_person;
		} else if (name.equals(res.getString(R.string.action_answerLivepost))) {
			resId = R.drawable.action_add_new_message;
		} else if (name.equals(res.getString(R.string.action_setDefaultProfile))) {
			resId = R.drawable.action_add_profilecard;
		}
		// Tab icons
		else if (name.equals("All people")) {
			resId = R.drawable.icon_black_person;
		} else if (name.equals("Groups")) {
			resId = R.drawable.icon_black_group;
		}
		return resId;
	}

}
