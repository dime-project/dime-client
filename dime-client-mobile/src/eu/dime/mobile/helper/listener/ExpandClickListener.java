package eu.dime.mobile.helper.listener;

import android.view.View;
import android.view.View.OnClickListener;
import eu.dime.mobile.view.abstr.BaseAdapterDime;
import eu.dime.model.GenItem;

public class ExpandClickListener<ITEM_TYPE extends GenItem> implements OnClickListener {

    protected int position;
    protected BaseAdapterDime<ITEM_TYPE> pa;

    public ExpandClickListener(int pos, BaseAdapterDime<ITEM_TYPE> pa) {
        this.position = pos;
        this.pa = pa;
    }

    @Override
    public void onClick(View v) {
        pa.expandedItemChanged(position);
    }
}