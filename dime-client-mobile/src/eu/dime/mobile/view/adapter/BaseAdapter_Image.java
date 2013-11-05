package eu.dime.mobile.view.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.model.ModelHelper;

public class BaseAdapter_Image extends BaseAdapter {
	
	protected LayoutInflater mInflater;
    protected List<String> mItems = new ArrayList<String>();
    protected Context context = null;
    
    public void init(Context context, List<String> items) {
    	this.context = context;
        this.mItems = items;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	String url = mItems.get(position);
    	String fileName = url.substring( url.lastIndexOf('/') + 1, url.length() );
    	DimeViewHolder viewHolder;
    	if (convertView == null) {
			viewHolder = new DimeViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_image_item, null);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
	    	convertView.setTag(viewHolder);
		} else {
			viewHolder = (DimeViewHolder) convertView.getTag();
		}
        viewHolder.name.setText(fileName);
        viewHolder.name.setTag(url);
        ImageHelper.loadImageAsynchronously(viewHolder.image, ModelHelper.guessURLString(url));
        return convertView;
    }

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	static class DimeViewHolder {
		
		TextView name;
		ImageView image;
    	
	}
    
}