package eu.dime.mobile.view.places;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.ContextHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.helper.objects.DimeIntentObject;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.DimeIntentObjectHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.view.abstr.ListActivityDisplayableItem;
import eu.dime.mobile.view.adapter.BaseAdapter_Place;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.PlaceItem;
import eu.dime.model.specialitem.NotificationItem;

import java.util.Arrays;
import java.util.List;

public class ListActivity_Place extends ListActivityDisplayableItem {

    private PlaceItem currentPlace;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	TAG = ListActivity_Place.class.getSimpleName();
    	getListView().setOnItemClickListener(this);
        setBaseAdapter(new BaseAdapter_Place());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<DisplayableItem> loadListData() {
    	try {
    		currentPlace = (PlaceItem) Model.getInstance().getItem(mrContext, TYPES.PLACE, ContextHelper.getCurrentPlace().getPlaceId());
		} catch (Exception e) {	}
        return (List<DisplayableItem>) (Object) AndroidModelHelper.removeListItemsByMeansOfItems(ModelHelper.getAllPlaces(mrContext), Arrays.asList(currentPlace));
    }

    @Override
    protected void initializeHeader() {
    	if (currentPlace == null) {
            Log.e(TAG, "Current place could not be retreived. Current context might not be available");
        } else {
	    	TextView name = (TextView) findViewById(R.place.name);
	        TextView dist = (TextView) findViewById(R.place.distance);
	        TextView favourite = (TextView) findViewById(R.place.favourite);
	        RatingBar ratingPublic = (RatingBar) findViewById(R.place.ratingPublic);
	        RatingBar ratingSocial = (RatingBar) findViewById(R.place.ratingSocial);
	        name.setText(currentPlace.getName());
	        dist.setText("Distance " + currentPlace.getDistance() * 1000 + " m");
	        ratingPublic.setRating((float) (currentPlace.getYmRating() * 5.0));
	        ratingSocial.setRating((float) (currentPlace.getSocRating() * 5.0));
	        // hide favourite label if not favourite - show if so 
	        if (!currentPlace.getFavorite()) {
	            UIHelper.hideView(favourite);
	        } else {
	            UIHelper.showView(favourite);
	        }
	        ImageView image = (ImageView) findViewById(R.place.placeImage);
	        ImageHelper.loadImageAsynchronously(image, currentPlace, this);
	        LinearLayout currentPlaceHeader = (LinearLayout) findViewById(R.place.currentPlaceHeader);
	        currentPlaceHeader.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                switch (v.getId()) {
	                    case R.place.currentPlaceHeader:
	                        if (currentPlace != null && currentPlace.getName().length() > 0) {
	                            Intent myIntent = new Intent(ListActivity_Place.this, TabActivity_Place_Detail.class);
	                            startActivity(DimeIntentObjectHelper.populateIntent(myIntent, new DimeIntentObject(currentPlace)));
	                        }
	                        break;
	                }
	            }
	        });
	    }
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent myIntent = new Intent(ListActivity_Place.this, TabActivity_Place_Detail.class);
        startActivity(DimeIntentObjectHelper.populateIntent(myIntent, new DimeIntentObject(getListItems().get(position))));
    }
    
    @Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		return false;
	}

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(item.getElement().getType().equalsIgnoreCase(TYPES.PLACE.toString()) || item.getElement().getType().equalsIgnoreCase(TYPES.CONTEXT.toString())){
    		reloadList();
    	}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<ListActivity_Place>createLVH(ListActivity_Place.this);
	}
}
