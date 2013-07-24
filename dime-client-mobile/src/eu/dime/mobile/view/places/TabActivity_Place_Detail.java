package eu.dime.mobile.view.places;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.crawler.data.Place;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ContextHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.model.context.ContextItem;
import eu.dime.model.context.constants.Scopes;
import eu.dime.model.displayable.PlaceItem;

import java.util.ArrayList;
import java.util.Arrays;

public class TabActivity_Place_Detail extends TabActivityDime {
	
	private PlaceItem selectedPlace;
	private boolean isCurrentPlace = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Place_Detail.class.getSimpleName();
		(new AsyncTask<Void, Void, PlaceItem>() {
			@Override
			protected PlaceItem doInBackground(Void... params) {
				return (PlaceItem) AndroidModelHelper.getGenItemSynchronously(TabActivity_Place_Detail.this, dio);
			}
			
			@Override
			protected void onPostExecute(PlaceItem result) {
				if(result != null) {
					selectedPlace = result;
					tabs.add(new DimeTabObject(getResources().getString(R.string.tab_placeDetail) + selectedPlace.getName(), Activity_Place_Detail.class, dio));
					TabActivity_Place_Detail.this.init(true, false, false, true);
				} else {
					finish();
				}
			}
		}).execute();
	}

	@Override
	protected void onClickActionButton() {
		String favourite = (selectedPlace.getFavorite()) ? getResources().getString(R.string.action_removeFavouriteTag) : getResources().getString(R.string.action_setFavouriteTag);
		String current = (isCurrentPlace) ? getResources().getString(R.string.action_removeCurrentPositionTag) : getResources().getString(R.string.action_setCurrentPositionTag);
		String[] actionsForPlaceDetail = { favourite, current };
		if (currentActivity instanceof Activity_Place_Detail) {
			actionDialog = UIHelper.createActionDialog(this, Arrays.asList(actionsForPlaceDetail), this, new ArrayList<String>());
			actionDialog.show();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		(new AsyncTask<Void, Void, Boolean>() {
			@Override
            protected Boolean doInBackground(Void... params) {
				boolean result = false;
				Place place = ContextHelper.getCurrentPlace();
				if(place != null) {
					result = place.getPlaceId().equals(selectedPlace.getGuid());
				}
				return result;
			}
			@Override
			protected void onPostExecute(Boolean result) {
				isCurrentPlace = result;
			}
		}).execute();
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
					ContextItem contextItem = ContextHelper.createCurrentPlaceContextItem(selectedPlace.getGuid(), selectedPlace.getName(), (isCurrentPlace) ? Integer.valueOf(1) : Integer.valueOf(600));
					DimeClient.contextCrawler.updateContext(Scopes.SCOPE_CURRENT_PLACE, contextItem);
					((Activity_Place_Detail) currentActivity).startTask("Refreshing view...");
				}
				//set favorite flag
				else if (button.getText().equals(res.getString(R.string.action_removeFavouriteTag)) || button.getText().equals(res.getString(R.string.action_setFavouriteTag))) {
					actionDialog.dismiss();
					final String actionName = res.getResourceEntryName(R.string.action_removeFavouriteTag);
					selectedPlace.setFavorite((selectedPlace.getFavorite()) ? false : true);
					AndroidModelHelper.updateGenItemAsynchronously(selectedPlace, null, currentActivity, mrContext, actionName);
				}
			}
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Place_Detail>createLVH(TabActivity_Place_Detail.this);
	}
	
}
