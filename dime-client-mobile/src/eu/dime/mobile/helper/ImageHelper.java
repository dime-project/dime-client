package eu.dime.mobile.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.model.ModelHelper;
import eu.dime.model.displayable.DataboxItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.PlaceItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.displayable.ResourceItem;
import sit.sstl.LRUCache;

public class ImageHelper {

	private static final int MAX_CACHE_ENTRIES = 50;	//TODO define good value

    private static sit.sstl.LRUCache<String, Bitmap> imageCache = null;
    private static final Object imageCacheLock = new Object();
    private static Set<String> bad = Collections.synchronizedSet(new HashSet<String>());
    
    public static void loadImageAsynchronously(ImageView iv, DisplayableItem di, Context context) {
		Bitmap bitmap = null;
		if (di != null && di.getImageUrl() != null && di.getImageUrl().length() > 0 && !bad.contains(di.getImageUrl())) {
			String url = ModelHelper.guessURLString(di.getImageUrl());
			try {
				bitmap = ImageHelper.getCachedImageBitmap(url, context);
			} catch (IOException e) {
				Log.d(context.getClass().getName(), "setImageBitmap failed");
			}
			if (bitmap == null) {
				iv.setImageDrawable(ImageHelper.getDefaultImageDrawable(di, context));
				final WeakReference<Context> mContext = new WeakReference<Context>(context);
				final WeakReference<ImageView> mImageView = new WeakReference<ImageView>(iv);
				final String diUrl = di.getImageUrl();
				AsyncTask<String, Void, Bitmap> task = new AsyncTask<String, Void, Bitmap>() {
					
					@Override
					protected Bitmap doInBackground(String... url) {
						Bitmap bitmap = null;
						try {
							bitmap = ImageHelper.getImageBitmap(url[0], mContext.get());
						} catch(OutOfMemoryError e){
							Log.d(mContext.getClass().getName(), "outOfMemoryException");
							clearCache();
						} catch (Exception e) {
							Log.d(mContext.getClass().getName(), "Exception when loading image for url: \"" + url + "\" (" + e.getMessage() + ")");
						}
						return bitmap;
					}
		
					@Override
					protected void onPostExecute(Bitmap bitmap) {
						if(mImageView.get() != null) {
							if (bitmap != null) {
								mImageView.get().setImageBitmap(bitmap);
							} else {
								bad.add(diUrl);
							}
						}
					}
				};
				task.execute(url);
			} else {
				iv.setImageBitmap(bitmap);
			}
		} else {
			iv.setImageDrawable(ImageHelper.getDefaultImageDrawable(di, context));
		}
	}

    public static Bitmap getImageBitmap(final String url, final Context activityContext) throws IOException, OutOfMemoryError {
        Bitmap bm = null;
        URL aURL = new URL(url);
        URLConnection conn = aURL.openConnection();
        if(DimeClient.getSettings().isUseHTTPS()){
        	conn.setRequestProperty("Authorization", "Basic " + DimeClient.getSettings().getAuthToken());
        }
        conn.connect();
        InputStream is = conn.getInputStream();
        try {
	        BufferedInputStream bis = new BufferedInputStream(is, 8192);
	        try {
	        	bm = BitmapFactory.decodeStream(bis);
	        } finally {
	        	bis.close();
	        }
        } finally {
        	is.close();
        }
        if (bm != null) {
        	synchronized (imageCacheLock) {
        		 imageCache.put(url, bm);
        	}
        }
        return bm;
    }

    public static Bitmap getCachedImageBitmap(String url, Context activityContext) throws IOException {
        synchronized (imageCacheLock) {
            if (imageCache == null) { //lazy creation
                imageCache = new LRUCache<String, Bitmap>(MAX_CACHE_ENTRIES);
            }
            Bitmap bm = imageCache.get(url);
            return bm;
        }
    }
    
    public static void clearCache() {
    	synchronized (imageCacheLock) {
			imageCache = null;
		}
    	bad.clear();
    }
    
    public static Bitmap getDefaultImageBitmap(DisplayableItem di, Context mContext) {
    	return BitmapFactory.decodeResource(mContext.getResources(), getDefaultimageDrawableId(di, mContext));
    }
    
    public static Drawable getDefaultImageDrawable(DisplayableItem di, Context mContext){
    	return mContext.getResources().getDrawable(getDefaultimageDrawableId(di, mContext));
    }
    
    @SuppressLint("DefaultLocale")
    public static int getDefaultimageDrawableId(DisplayableItem di, Context mContext) {
    	int icon = 0;
    	if(di instanceof ResourceItem) {
	        String lower = di.getName().toLowerCase();
	        if (lower.endsWith("xls") || lower.endsWith("xlsx")) {
	            icon = R.drawable.icon_black_data_xls;
	        } else if (lower.endsWith("doc") || lower.endsWith("docx")) {
	            icon = R.drawable.icon_black_data_doc;
	        } else if (lower.endsWith("pdf")) {
	            icon = R.drawable.icon_black_data_pdf;
	        } else if (lower.endsWith("jpg") || lower.endsWith("jpeg") || lower.endsWith("png") || lower.endsWith("bmp")) {
	            icon = R.drawable.icon_black_data_image;
	        } else {
	            icon = R.drawable.icon_black_data_resource;
	        }
    	} else if (di instanceof PersonItem || di instanceof ProfileItem){
    		icon = R.drawable.preview_person;
    	} else if (di instanceof DataboxItem){
    		icon = R.drawable.preview_databox;
    	} else if (di instanceof GroupItem){
    		icon = (((GroupItem)di).getGroupType().equals("urn:auto-generated")) ? R.drawable.preview_situation: R.drawable.preview_group;
    	} else if (di instanceof PlaceItem){
    		icon = R.drawable.preview_place;
    	} else if (di instanceof LivePostItem) {
    		icon = R.drawable.icon_black_communication;
    	} else {
    		icon = R.drawable.preview_unknown;
    	}
        return icon;
    }
    
    public static Bitmap getCachedBitmap(ImageView iv) {
		Bitmap bitmap = null;
		if(iv !=null && iv.getDrawingCache() != null){
			bitmap = Bitmap.createBitmap(iv.getDrawingCache());
		}
		return bitmap;
	}
    
}
