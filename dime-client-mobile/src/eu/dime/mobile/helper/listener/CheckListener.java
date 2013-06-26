package eu.dime.mobile.helper.listener;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import eu.dime.mobile.view.abstr.BaseAdapterDime;
import eu.dime.model.GenItem;

public class CheckListener<ITEM_TYPE extends GenItem> implements OnCheckedChangeListener {

    protected int position;
    protected BaseAdapterDime<ITEM_TYPE> pa;

    public CheckListener(int pos, BaseAdapterDime<ITEM_TYPE> pa) {
        this.position = pos;
        this.pa = pa;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        pa.checkedItemChanged(position, isChecked);
    }
}
