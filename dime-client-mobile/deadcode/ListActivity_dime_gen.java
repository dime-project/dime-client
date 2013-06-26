package eu.dime.mobile.view.abstr;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import eu.dime.mobile.helper.DimeIntentObject;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.LoadingViewHandlerFactory;
import eu.dime.model.GenItem;
import eu.dime.model.ModelRequestContext;
import java.util.ArrayList;
import java.util.List;

public abstract class ListActivity_dime_gen extends ListActivity implements OnItemClickListener, OnItemLongClickListener {

    protected final String TAG = ListActivity_dime_gen.class.getSimpleName();
    protected DimeIntentObject dio = null;
    protected List<GenItem> listItems = new ArrayList<GenItem>();
    protected ModelRequestContext mrContext = null;
    protected ListView listView;
    protected BaseAdapter_dime_gen baseAdapter = null;
    protected View headerView = null;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState
     * @param listItems
     * @param baseAdapter_dime  
     */
    public void onCreate(Bundle savedInstanceState, List<GenItem> listItems, BaseAdapter_dime_gen baseAdapter_dime) {
        super.onCreate(savedInstanceState);
        listView = getListView();
        dio = new DimeIntentObjectHelper().readIntent(this.getIntent());
        mrContext = new ModelRequestContext(dio.getHoster(), dio.getOwner(),
	      LoadingViewHandlerFactory.<ListActivity_dime_gen>createLVH(ListActivity_dime_gen.this));
        setListItems(listItems);
        View header = initializeHeader();
        if (header != null) {
            this.headerView = header;
            listView.addHeaderView(header, null, false);
        }
        setBaseAdapter(baseAdapter_dime);
        baseAdapter.init(this.getBaseContext(), mrContext, listView, listItems);
        setListAdapter(baseAdapter);
        // click on overall something item in ListView
        listView.setOnItemClickListener(this);
        // longClick on overall something item in ListView
        listView.setOnItemLongClickListener(this);
    }

    protected abstract View initializeHeader();

    @Override
    protected void onResume() {
        super.onResume();
        reloadList();
    }

    public void reloadList() {
        List<GenItem> result = loadListData();
        if (result == null) {
            result = new ArrayList<GenItem>();
            Toast.makeText(getApplicationContext(), "ERROR, no data available!", Toast.LENGTH_SHORT).show();
        }
        showListData(result);
        setListItems(result);
        baseAdapter.init(ListActivity_dime_gen.this.getBaseContext(), mrContext, listView, result);
        refreshList();
    }

    public void refreshList() {
        baseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//    	Toast.makeText(getApplicationContext(), "Clicked item " + getListItems().get(position).getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (getListItems().size() <= position) {
            return false;
        }
//    	Toast.makeText(getApplicationContext(), "LongClicked item " + getListItems().get(position).getName(), Toast.LENGTH_SHORT).show();
        return false;
    }

    protected abstract List<GenItem> loadListData();

    protected abstract void showListData(List<GenItem> result);

    public void setListItems(List<GenItem> listItems) {
        this.listItems = listItems;
    }

    public void setBaseAdapter(BaseAdapter_dime_gen baseAdapter) {
        this.baseAdapter = baseAdapter;
    }

    public List<String> getSelectionGUIDS() {
        return baseAdapter.getSelection();
    }

    public List<GenItem> getListItems() {
        return listItems;
    }
}
