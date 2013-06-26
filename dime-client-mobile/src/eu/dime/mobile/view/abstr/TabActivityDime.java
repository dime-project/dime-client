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
import android.widget.Button;
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
        Button openSearchButton = (Button) findViewById(R.tabframe.button_open_search);
        Button homeButton = (Button) findViewById(R.tabframe.button_home);
        Button actionButton = (Button) findViewById(R.tabframe.button_action);
        openSearchButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);
        actionButton.setOnClickListener(this);
        getTabHost().setOnTabChangedListener(this);
        init(this.getIntent());
    }
    
    private void init(Intent intent) {
    	dio = DimeIntentObjectHelper.readIntent(intent);
    	mrContext = DimeClient.getMRC(dio.getOwnerId(), lvHandler);
    }
    
	protected void init(boolean showHomeButton, boolean showSearchButton, boolean showActionButton) {
    	if(!showHomeButton) { UIHelper.hideView(findViewById(R.tabframe.view_home)); }
        if(!showSearchButton) { UIHelper.hideView(findViewById(R.tabframe.view_search)); }
        if(!showActionButton) { UIHelper.hideView(findViewById(R.tabframe.view_action)); }
        
        if(tabs.size() >0) {
	        //populate each intent
	        for (DimeTabObject tab : tabs) {
	            Intent intent = new Intent().setClass(this, tab.classObject);
	            addTab(tab.label, DimeIntentObjectHelper.populateIntent(intent, tab.dio));
	        }
	        getTabHost().setCurrentTab(0);
        } else {
        	Toast.makeText(this, "Couldn´t load detail view of item!", Toast.LENGTH_LONG).show();
        	finish();
        }
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
    	currentActivity = getCurrentActivity();
    	DimeClient.addStringToViewStack(TAG.substring(12)); //remove TabActivity_
    	UIHelper.hideView(findViewById(R.tabframe.view_search));
    }

    @Override
    public void onTabChanged(String tabId) {
        currentActivity = getCurrentActivity();
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void onClickOpenSearchButton() {
        LinearLayout searchArea = (LinearLayout) findViewById(R.tabframe.search_area);
        ListView listView = ((ListActivity) currentActivity).getListView();
        EditText searchField = (EditText) findViewById(R.tabframe.autocompleteTextView_searchfield);
        final TextView searchResults = (TextView) findViewById(R.tabframe.searchresults);
        View header = ((View)listView.getParent()).findViewById(R.id.header);
        if (searchArea.getVisibility() == View.GONE) {
        	DimeClient.addStringToViewStack(getResources().getString(R.string.self_evaluation_tool_search));
            UIHelper.showView(searchArea);
            ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInputFromWindow(searchField.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            searchField.requestFocus();
            if(header!=null) header.setVisibility(View.GONE);
        } else {
            UIHelper.hideView(searchArea);
            searchField.setText("");
            if(header!=null) header.setVisibility(View.VISIBLE);
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

    protected abstract void onClickActionButton();

    protected void onClickHomeButton() {
    	AndroidModelHelper.sendEvaluationDataAsynchronously(new ArrayList<GenItem>(), mrContext, getString(R.string.action_homeButtonPressed));
        Intent myIntent = new Intent(TabActivityDime.this, Activity_Main.class);
        this.startActivity(DimeIntentObjectHelper.populateIntent(myIntent, dio));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.tabframe.button_open_search:
                onClickOpenSearchButton();
                break;

            case R.tabframe.button_action:
                onClickActionButton();
                DimeClient.addStringToViewStack(getResources().getString(R.string.self_evaluation_tool_actiondialog));
                break;

            case R.tabframe.button_home:
                onClickHomeButton();
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
		image.setImageResource(UIHelper.getResourceId(getResources(), tabname));
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
