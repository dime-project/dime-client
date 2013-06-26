package eu.dime.mobile.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.ContextHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.ComparatorHelper;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.PlaceItem;
import java.util.Comparator;

public class BaseAdapter_Place extends BaseAdapterDisplayableItem {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        PlaceItem placeItem = (PlaceItem) mItems.get(position);
        vi = mInflater.inflate(R.layout.adapter_place_item, null);
        TextView name = (TextView) vi.findViewById(R.placeitem.name);
        TextView dist = (TextView) vi.findViewById(R.placeitem.distance);
        TextView favourite = (TextView) vi.findViewById(R.placeitem.favourite);
        TextView current = (TextView) vi.findViewById(R.placeitem.current);
        RatingBar ratingPublic = (RatingBar) vi.findViewById(R.placeitem.ratingPublic);
        RatingBar ratingSocial = (RatingBar) vi.findViewById(R.placeitem.ratingSocial);
        name.setText(placeItem.getName());
        dist.setText("Distance " + placeItem.getDistance() * 1000 + "m");
        ratingPublic.setRating((float) (placeItem.getYmRating() * 5.0));
        ratingSocial.setRating((float) (placeItem.getSocRating() * 5.0));
        // hide favourite label if not favourite
        if (!placeItem.getFavorite()) {
            UIHelper.hideView(favourite);
        }
        // hide current label if not equal to currentPlace
        if (ContextHelper.getCurrentPlace() != null && placeItem != null) {
            if (!placeItem.getGuid().equals(ContextHelper.getCurrentPlace().getPlaceId())) {
                UIHelper.hideView(current);
            }
        } else {
            UIHelper.hideView(current);
        }
        ImageView image = (ImageView) vi.findViewById(R.placeitem.image);
        ImageHelper.loadImageAsynchronously(image, placeItem, context);
        return vi;
    }
    
	@Override
    protected Comparator<DisplayableItem> createComparator() {
        return new ComparatorHelper.RatingComparator();
    }
}