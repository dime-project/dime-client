package eu.dime.mobile.view.places;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.R;
import eu.dime.mobile.crawler.data.Place;
import eu.dime.mobile.helper.ContextHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ActivityDime;
import eu.dime.model.Model;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.PlaceItem;
import eu.dime.model.specialitem.NotificationItem;

public class Activity_Place_Detail extends ActivityDime implements OnClickListener, OnRatingBarChangeListener {
	
	private PlaceItem place;
	private PlaceItem currentPlace;
	private RatingBar ratingSetPersonal;
	private RatingBar ratingPersonal;
	private TextView favouriteTextView;
	private TextView currentTextView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = Activity_Place_Detail.class.getSimpleName();
		setContentView(R.layout.place_detail);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		startTask("Initialize place details...");
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.placedetail.button_openmaps:
			String formattedPos = place.getPosition().substring(0, place.getPosition().length() - 2);
			formattedPos = formattedPos.replace(" ", ",");
			formattedPos = formattedPos.replace("+", "");
			String mapQuery = "geo:" + formattedPos + "?q=" + formattedPos + "(" + place.getName() + ")";
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapQuery));
			startActivity(intent);
			break;

		case R.placedetail.button_openurl:
			Intent webIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(place.getUrl()));
			startActivity(webIntent);
			break;
		}
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
		place.setUserRating(ratingSetPersonal.getRating() / 5.0);
		Model.getInstance().updateItem(mrContext, place);
		ratingPersonal.setRating(ratingSetPersonal.getRating());
	}

	@Override
	protected void loadData() {
		place = (PlaceItem) Model.getInstance().getItem(mrContext, TYPES.PLACE, dio.getItemId());
		Place placeTmp = ContextHelper.getCurrentPlace(); // get current place from context
		if (placeTmp != null) { currentPlace = (PlaceItem) Model.getInstance().getItem(mrContext, TYPES.PLACE, placeTmp.getPlaceId()); }
	}

	@Override
	protected void initializeData() {
		TextView dist = (TextView) findViewById(R.placedetail.distance);
		TextView info = (TextView) findViewById(R.placedetail.info);
		favouriteTextView = (TextView) findViewById(R.placedetail.favourite);
		currentTextView = (TextView) findViewById(R.placedetail.current);
		RatingBar ratingPublic = (RatingBar) findViewById(R.placedetail.ratingPublic);
		RatingBar ratingSocial = (RatingBar) findViewById(R.placedetail.ratingSocial);
		ratingPersonal = (RatingBar) findViewById(R.placedetail.ratingPersonal);
		ratingSetPersonal = (RatingBar) findViewById(R.placedetail.ratingSetPersonal);
		dist.setText("Distance " + place.getDistance() * 1000 + " m");
		info.setText(place.getInformation());
		ratingPublic.setRating((float) (place.getYmRating() * 5.0));
		ratingSocial.setRating((float) (place.getSocRating() * 5.0));
		ratingPersonal.setRating((float) (place.getUserRating() * 5.0));
		ratingSetPersonal.setRating((float) (place.getUserRating() * 5.0));
		ratingSetPersonal.setOnRatingBarChangeListener(this);
		
		// hide favourite label if not favourite
		if (!place.getFavorite()) { UIHelper.hideView(favouriteTextView); }
		
		// hide current label if not equal to currentPlace
		if (currentPlace != null && place != null) {
			if (!place.getGuid().equals(currentPlace.getGuid())) { UIHelper.hideView(currentTextView); }
		} else { UIHelper.hideView(currentTextView); }
		
		ImageView image = (ImageView) findViewById(R.placedetail.image);
		ImageHelper.loadImageAsynchronously(image, place, this);
		Button openMaps = (Button) findViewById(R.placedetail.button_openmaps);
		Button openWeb = (Button) findViewById(R.placedetail.button_openurl);
		openMaps.setOnClickListener(this);
		openWeb.setOnClickListener(this);
		if(place.getPosition() == null || place.getPosition().length() == 0) {
			openMaps.setEnabled(false);
			openMaps.setClickable(false);
		}
		if (place.getUrl() == null || place.getUrl().length()==0){
			openWeb.setEnabled(false);
			openWeb.setClickable(false);
		}
	}

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
		if(item.getElement().getType().equalsIgnoreCase(TYPES.PLACE.toString()) || item.getElement().getType().equalsIgnoreCase(TYPES.CONTEXT.toString())){
			startTask("");
		}
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<Activity_Place_Detail>createLVH(Activity_Place_Detail.this);
	}

}
