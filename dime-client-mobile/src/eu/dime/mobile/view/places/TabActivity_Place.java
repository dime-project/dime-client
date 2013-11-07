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

package eu.dime.mobile.view.places;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.control.NotificationListener;
import eu.dime.control.NotificationManager;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ContextHelper;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.objects.DimeTabObject;
import eu.dime.mobile.view.abstr.TabActivityDime;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.PlaceItem;
import eu.dime.model.specialitem.NotificationItem;

public class TabActivity_Place extends TabActivityDime implements NotificationListener {

	protected PlaceItem currentPlace;
	protected boolean ownItem = false;
	
	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = TabActivity_Place.class.getSimpleName();
		tabs.add(new DimeTabObject(getResources().getString(R.string.tab_places), ListActivity_Place.class, new DimeIntentObject(TYPES.PLACE)));
		super.init(true, true, false, false);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		NotificationManager.registerSecondLevel(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refreshView();
	}
    
    private void refreshView() {
    	(new AsyncTask<Void, Void, PlaceItem>() {
			@Override
			protected PlaceItem doInBackground(Void... params) {
				PlaceItem place = null;
				try {
					place = (PlaceItem) AndroidModelHelper.getGenItemSynchronously(TabActivity_Place.this, new DimeIntentObject(ContextHelper.getCurrentPlace().getPlaceId(), TYPES.PLACE));
				} catch (Exception e) {
				}
				return place;
			}
			
			@Override
			protected void onPostExecute(PlaceItem result) {
				currentPlace = result;
				initializeHeader();
			}
		}).execute();
	}

	@SuppressWarnings("deprecation")
	@Override
    protected void onStop() {
    	super.onStop();
    	NotificationManager.unregisterSecondLevel(this);
    }

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<TabActivity_Place>createLVH(TabActivity_Place.this);
	}

	@Override
	protected void onClickActionButton() {
		
	}
	
    protected void initializeHeader() {
    	ViewGroup headerContainer = (ViewGroup) findViewById(R.tabframe.header);
		LinearLayout header = null;
		if(headerContainer.getChildCount() > 0) {
			header = (LinearLayout) headerContainer.getChildAt(0);
		} else {
			header = (LinearLayout) getLayoutInflater().inflate(R.layout.header_place, headerContainer);
		}
		TextView name = (TextView) header.findViewById(R.place.name);
		ImageView image = (ImageView) findViewById(R.place.placeImage);
        TextView dist = (TextView) header.findViewById(R.place.distance);
        TextView favourite = (TextView) header.findViewById(R.place.favourite);
        RatingBar ratingSocial = (RatingBar) header.findViewById(R.place.ratingSocial);
        RatingBar ratingOwn = (RatingBar) header.findViewById(R.place.ratingOwn);
		if (currentPlace != null && currentPlace.getName().length() > 0) {
			header.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
	                Intent myIntent = new Intent(TabActivity_Place.this, TabActivity_Place_Detail.class);
	                startActivity(DimeIntentObjectHelper.populateIntent(myIntent, new DimeIntentObject(currentPlace)));
				}
			});
	        name.setText(currentPlace.getName());
	        dist.setText("Distance " + currentPlace.getDistance() * 1000 + " m");
	        ratingSocial.setRating((float) (currentPlace.getSocRating() * 5.0));
	        ratingOwn.setRating((float) (currentPlace.getUserRating() * 5.0));
	        favourite.setVisibility(currentPlace.getFavorite() ? View.VISIBLE : View.GONE);
	        ImageHelper.loadImageAsynchronously(image, currentPlace, this);
		} else {
			header.setOnClickListener(null);
			name.setText("Current place not set!");
			image.setImageDrawable(ImageHelper.getDefaultImageDrawable(currentPlace, this));
			dist.setText("-");
			UIHelper.hideView(favourite);
			ratingOwn.setRating(0);
			ratingSocial.setRating(0);
		}
	}
    
    @Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(item.getElement().getType().equals(TYPES.CONTEXT.toString())) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					refreshView();
				}
			});
		}
	}
	
}
