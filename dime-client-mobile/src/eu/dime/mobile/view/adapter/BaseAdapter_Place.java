package eu.dime.mobile.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.ComparatorHelper;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.PlaceItem;
import java.util.Comparator;

public class BaseAdapter_Place extends BaseAdapterDisplayableItem {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	PlaceItem placeItem = (PlaceItem) mItems.get(position);
    	// Keeps reference to avoid future findViewById()
    	DimeViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new DimeViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_place_item, null);
			viewHolder.name = (TextView) convertView.findViewById(R.placeitem.name);
			viewHolder.image = (ImageView) convertView.findViewById(R.placeitem.image);
			viewHolder.distance = (TextView) convertView.findViewById(R.placeitem.distance);
			viewHolder.favourite = (TextView) convertView.findViewById(R.placeitem.favourite);
			viewHolder.ratingPublic = (RatingBar) convertView.findViewById(R.placeitem.ratingPublic);
			viewHolder.ratingSocial = (RatingBar) convertView.findViewById(R.placeitem.ratingSocial);
	    	convertView.setTag(viewHolder);
		} else {
			viewHolder = (DimeViewHolder) convertView.getTag();
		}
		viewHolder.name.setText(placeItem.getName());
		viewHolder.distance.setText("Distance " + placeItem.getDistance() * 1000 + "m");
		viewHolder.ratingPublic.setRating((float) (placeItem.getYmRating() * 5.0));
		viewHolder.ratingSocial.setRating((float) (placeItem.getSocRating() * 5.0));
        // hide favourite label if not favourite
        viewHolder.favourite.setVisibility((placeItem.getFavorite())? View.VISIBLE : View.GONE);
        ImageHelper.loadImageAsynchronously(viewHolder.image, placeItem, context);
        return convertView;
    }
    
	@Override
    protected Comparator<DisplayableItem> createComparator() {
        return new ComparatorHelper.RatingComparator();
    }
	
	static class DimeViewHolder {
		
		TextView name;
		ImageView image;
		TextView distance;
		TextView favourite;
    	RatingBar ratingPublic;
    	RatingBar ratingSocial;
    	
	}
	
}