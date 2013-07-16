package eu.dime.mobile.view.places;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ContextHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.ListActivityDime;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.model.Model;
import eu.dime.model.context.ContextItem;
import eu.dime.model.context.constants.Scopes;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.PlaceItem;
import java.util.ArrayList;
import java.util.Arrays;

public class TabActivity_Place_Detail extends TabActivityDime {
	
	private PlaceItem selectedPlace;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Place_Detail.class.getSimpleName();
    	selectedPlace = (PlaceItem) AndroidModelHelper.getGenItemSynchronously(this, dio);
    	if(selectedPlace != null) tabs.add(new DimeTabObject(getResources().getString(R.string.tab_placeDetail) + selectedPlace.getName(), Activity_Place_Detail.class, dio));
    	TabActivity_Place_Detail.this.init(true, false, false, true);
	}

	@Override
	protected void onClickActionButton() {
		String favourite = (selectedPlace.getFavorite()) ? getResources().getString(R.string.action_removeFavouriteTag) : getResources().getString(R.string.action_setFavouriteTag);
		String current = ((ContextHelper.getCurrentPlace() != null) && (ContextHelper.getCurrentPlace().getPlaceName().equals(selectedPlace.getName()))) ? getResources().getString(R.string.action_removeCurrentPositionTag) : getResources().getString(R.string.action_setCurrentPositionTag);
		String[] actionsForPlaceDetail = { favourite, current };
		if (currentActivity instanceof Activity_Place_Detail) {
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForPlaceDetail), this, new ArrayList<String>());
			actionDialog.show();
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		if (view instanceof Button) {
			Button button = (Button) view;
			Resources res = getResources();
			if (currentActivity instanceof Activity_Place_Detail) {
				//set current position flag
				if (button.getText().equals(res.getString(R.string.action_removeCurrentPositionTag)) || button.getText().equals(res.getString(R.string.action_setCurrentPositionTag))) {
					actionDialog.dismiss();
					(new AsyncTask<Void, Void, Boolean>() {
			    		
						@Override
			            protected Boolean doInBackground(Void... params) {
							boolean isCurrentPlace = false;
							if (ContextHelper.getCurrentPlace() != null && ContextHelper.getCurrentPlace().getPlaceId().equals(selectedPlace.getGuid())) {
					    		ContextItem contextItem = ContextHelper.createCurrentPlaceContextItem(selectedPlace.getGuid(), selectedPlace.getName(), Integer.valueOf(1));
								Model.getInstance().updateItem(mrContext, contextItem); //FIXME use helper
								DimeClient.contextCrawler.updateContext(Scopes.SCOPE_CURRENT_PLACE, contextItem);
					    	} else {
					    		// create new context item
								ContextItem contextItem = ContextHelper.createCurrentPlaceContextItem(selectedPlace.getGuid(), selectedPlace.getName(), Integer.valueOf(600));
								Model.getInstance().updateItem(mrContext, contextItem); //FIXME use helper
								DimeClient.contextCrawler.updateContext(Scopes.SCOPE_CURRENT_PLACE, contextItem);
								isCurrentPlace = true;
					    	}
			                return isCurrentPlace;
			            }

			            @SuppressWarnings("unchecked")
						@Override
			            protected void onPostExecute(Boolean isCurrentPlace) {
			            	if(currentActivity instanceof ListActivityDime) ((ListActivityDime<DisplayableItem>)currentActivity).reloadList();
			            }
			            
			        }).execute();
				}
				//set favorite flag
				else if (button.getText().equals(res.getString(R.string.action_removeFavouriteTag)) || button.getText().equals(res.getString(R.string.action_setFavouriteTag))) {
					actionDialog.dismiss();
					final String actionName = res.getResourceEntryName(R.string.action_removeFavouriteTag);
					selectedPlace.setFavorite((selectedPlace.getFavorite()) ? false : true);
					AndroidModelHelper.updateGenItemAsyncronously(selectedPlace, null, currentActivity, mrContext, actionName);
				}
			}
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Place_Detail>createLVH(TabActivity_Place_Detail.this);
	}
	
}
