package eu.dime.mobile.view.abstr;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.Activity_Main;
import eu.dime.mobile.view.Activity_Shutdown;
import eu.dime.model.GenItem;
import eu.dime.model.ModelRequestContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@SuppressWarnings("deprecation")
public abstract class TabActivityDime extends TabActivity implements OnClickListener, TabHost.OnTabChangeListener, OnFocusChangeListener {

	protected String TAG = "not set";
    protected Activity currentActivity = getCurrentActivity();
    protected List<String> selectedGUIDs = null;
    protected DimeIntentObject dio = null;
    protected LoadingViewHandler lvHandler = null;
    protected ModelRequestContext mrContext = null;
    protected Dialog actionDialog = null;
    protected List<DimeTabObject> tabs = new Vector<DimeTabObject>();
	private BaseAdapterDime<GenItem> baseAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.tabframe);
    	lvHandler = createLoadingViewHandler();
        getTabHost().setOnTabChangedListener(this);
        init(this.getIntent());
    }
    
    private void init(Intent intent) {
    	dio = DimeIntentObjectHelper.readIntent(intent);
    	mrContext = DimeClient.getMRC(dio.getOwnerId(), lvHandler);
    }
    
	protected void init(boolean showHomeButton, boolean showSearchButton, boolean showShareButton, boolean showActionButton) {
		getTabHost().getTabContentView().removeAllViews();
    	if(!showHomeButton) { UIHelper.hideView(findViewById(R.tabframe.view_home)); }
        if(!showSearchButton) { UIHelper.hideView(findViewById(R.tabframe.view_search)); }
        if(!showShareButton) { UIHelper.hideView(findViewById(R.tabframe.view_share)); }
        if(!showActionButton) { UIHelper.hideView(findViewById(R.tabframe.view_action)); }
        //populate each intent
        for (DimeTabObject tab : tabs) {
            Intent intent = new Intent().setClass(this, tab.classObject);
            addTab(tab.label, DimeIntentObjectHelper.populateIntent(intent, tab.dio));
        }
        getTabHost().setCurrentTab(0);
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	if(intent != null) { init(intent); }
    }
    
    @Override
	protected void onStart() {
    	super.onStart();
	}
    
    @Override
    public void onPause() {
        super.onPause();
        if(actionDialog != null) {
            actionDialog.dismiss();
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	if(DimeClient.getSettings().getAuthItem() != null) {
	    	currentActivity = getCurrentActivity();
	    	try {
	    		DimeClient.addStringToViewStack(TAG.substring(12)); //remove TabActivity_
			} catch (Exception e) { }
    	} else {
        	Toast.makeText(getApplicationContext(), "Error occurred! Please login again...", Toast.LENGTH_SHORT).show();
        	startActivity(new Intent(TabActivityDime.this, Activity_Shutdown.class));	                	
        	finish();
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        currentActivity = getCurrentActivity();
        UIHelper.hideView(findViewById(R.tabframe.search_area));
        findViewById(R.tabframe.button_open_search).setSelected(false);
        UIHelper.showView(findViewById(R.tabframe.header));
    }

    protected void onClickHomeButton() {
    	AndroidModelHelper.sendEvaluationDataAsynchronously(new ArrayList<GenItem>(), mrContext, getString(R.string.action_homeButtonPressed));
        Intent myIntent = new Intent(TabActivityDime.this, Activity_Main.class);
        this.startActivity(DimeIntentObjectHelper.populateIntent(myIntent, dio));
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected void onClickOpenSearchButton(View v) {
        LinearLayout searchArea = (LinearLayout) findViewById(R.tabframe.search_area);
        ListView listView = ((ListActivity) currentActivity).getListView();
        EditText searchField = (EditText) findViewById(R.tabframe.autocompleteTextView_searchfield);
        final TextView searchResults = (TextView) findViewById(R.tabframe.searchresults);
        View header = (View) findViewById(R.tabframe.header);
        if (searchArea.getVisibility() == View.GONE) {
        	v.setSelected(true);
        	DimeClient.addStringToViewStack(getResources().getString(R.string.self_evaluation_tool_search));
            UIHelper.showView(searchArea);
            ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInputFromWindow(searchField.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            searchField.requestFocus();
            if(header!=null) {
            	TextView text = (TextView) header.findViewWithTag("name");
            	if(text != null && text.getText().length() > 0) searchField.setHint("search items of " + text.getText() + "...");
            	header.setVisibility(View.GONE);
            }
        } else {
        	v.setSelected(false);
            UIHelper.hideView(searchArea);
            searchField.setText("");
            if(header!=null) {
            	header.setVisibility(View.VISIBLE);
            }
        }
        if(listView.getAdapter() instanceof HeaderViewListAdapter) {
        	HeaderViewListAdapter hvla = (HeaderViewListAdapter) listView.getAdapter();
        	baseAdapter = (BaseAdapterDime) hvla.getWrappedAdapter();
        } else {
        	baseAdapter = (BaseAdapterDime) listView.getAdapter();
        }
        searchResults.setText(baseAdapter.getCount() + " result(s)");
        listView.setTextFilterEnabled(true);
        searchField.setOnFocusChangeListener(this);
        searchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable searchText) {
                baseAdapter.getFilter().filter(searchText);
                final Handler handler = new Handler(new Handler.Callback() {
					@Override
					public boolean handleMessage(Message msg) {
						searchResults.setText(baseAdapter.getCount() + " result(s)");
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
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected void onClickShareButton() {
    	AndroidModelHelper.shareResources(this, ((ListActivityDime) currentActivity).getSelectedListItems());
	}
    
    protected abstract void onClickActionButton();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.tabframe.button_home:
            onClickHomeButton();
            break;
        case R.tabframe.button_open_search:
            onClickOpenSearchButton(v);
            break;
        case R.tabframe.button_share:
            onClickShareButton();
            break;
        case R.tabframe.button_action:
            onClickActionButton();
            DimeClient.addStringToViewStack(getResources().getString(R.string.self_evaluation_tool_actiondialog));
            break;
        }
    }

	// Can also be overridden by containded listactivities
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	onClickActionButton();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
	public void onFocusChange(View view, boolean hasFocus) {
    	if(!hasFocus) {
    		((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
    	}
	}
    
    protected abstract LoadingViewHandler createLoadingViewHandler();

	protected void addTab(String tabname, Intent intent) {
		// Initialize a TabSpec for each tab and add it to the TabHost
		TabHost.TabSpec tabSpec = getTabHost().newTabSpec(tabname).setIndicator(tabname, getResources().getDrawable(R.drawable.tab)).setContent(intent);
		LinearLayout ll = new LinearLayout(this);
		ll.setBackgroundResource(R.drawable.tab);
		ll.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		ImageView image = new ImageView(this);
		image.setImageResource(UIHelper.getResourceIdByName(getResources(), tabname));
		image.setLayoutParams(new LayoutParams(40, 40));
		image.setPadding(0, 0, 10, 0);
		TextView txtTabInfo = new TextView(this);
		txtTabInfo.setText(tabname);
		txtTabInfo.setTextColor(Color.WHITE);
//		ll.addView(image); //TODO add different image style and then reactivate this
		ll.addView(txtTabInfo);
		tabSpec.setIndicator(ll);
		getTabHost().addTab(tabSpec);
	}
    
}
