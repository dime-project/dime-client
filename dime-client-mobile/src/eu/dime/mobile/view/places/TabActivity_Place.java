package eu.dime.mobile.view.places;

import java.util.Arrays;
import android.os.Bundle;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.model.TYPES;

public class TabActivity_Place extends TabActivityDime {

	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Place.class.getSimpleName();
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_places), ListActivity_Place.class, new DimeIntentObject(TYPES.PLACE)));
		super.init(true, true, false, false);
	}
	
    @Override
    protected void onClickActionButton() {
        String[] actionsForPlacesList = {};
        if (currentActivity instanceof ListActivity_Place) {
            selectedGUIDs = ((ListActivity_Place) currentActivity).getSelectionGUIDS();
            actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForPlacesList), this, selectedGUIDs);
        }
    }

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Place>createLVH(TabActivity_Place.this);
	}
}
