/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.mobile.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.view.abstr.ListActivityDime;
import eu.dime.model.GenItem;
import eu.dime.model.ModelHelper;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.restapi.MultiPartPostClient;

/**
 * 
 * @author Simon Thiel
 */
public class FileHelper implements sit.io.FileHelperI {

	public void writeToFile(String fileName, String content) throws IOException {

		File root = Environment.getExternalStorageDirectory();
		if (root.canWrite()) {
			File gpxfile = new File(root, fileName);
			FileWriter gpxwriter = new FileWriter(gpxfile);
			BufferedWriter out = new BufferedWriter(gpxwriter);
			out.write(content);
			out.close();
		} else {
			throw new IOException("cannot write to SD-Card!");
		}
	}
	
	public static void saveFileAsynchronouslyOnSDAndOpen(final Activity activity, final ResourceItem resource, final Dialog dialog) {
		(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return saveFileOnSD(resource);
			}

			@Override
			protected void onPostExecute(String result) {
				if(dialog != null && dialog.isShowing()) dialog.dismiss();
				Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
				if (Environment.getExternalStorageDirectory().canRead()) {
					Intent newIntent = new Intent(android.content.Intent.ACTION_VIEW);
					File file = new File(Environment.getExternalStorageDirectory().toString() + "/DCIM", resource.getName());
					newIntent.setDataAndType(Uri.fromFile(file), resource.getMimeType());
					newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try {
					    activity.startActivity(newIntent);
					} catch (android.content.ActivityNotFoundException e) {
					    Toast.makeText(activity, "No handler for this type of file.", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}).execute();
	}
	
	public static void saveFileAsynchronouslyOnSD(final Activity activity, final ResourceItem resource, final Dialog dialog) {
		(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				return saveFileOnSD(resource);
			}

			@Override
			protected void onPostExecute(String result) {
				if(dialog != null && dialog.isShowing()) dialog.dismiss();
				Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
			}
		}).execute();
	}
	
	private static String saveFileOnSD(ResourceItem resource) {
		String result = "";
		if (Environment.getExternalStorageDirectory().canWrite()) {
			try {
				URL aURL = new URL(ModelHelper.guessURLString(resource.getDownloadUrl()));
				URLConnection conn = aURL.openConnection();
				if(DimeClient.getSettings().isUseHTTPS()){
		        	conn.setRequestProperty("Authorization", "Basic " + DimeClient.getSettings().getAuthToken());
		        }
				conn.connect();
				InputStream is = conn.getInputStream();
				try {
	        		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
	        	    File file = new File(extStorageDirectory + "/DCIM", resource.getName());
	        	    OutputStream output = new FileOutputStream (file);
	        	    try {
	        	        byte[] buffer = new byte[1024];
	        	        int bytesRead = 0;
	        	        while ((bytesRead = is.read(buffer, 0, buffer.length)) >= 0) {
	        	            output.write(buffer, 0, bytesRead);
	        	        }
	        	        result = "File saved on SD-card " + file;
	        	    } finally {
			            output.close();
			        }
				} finally {
				    is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				result = "Could not download the file because the download url is not valid!";
			} catch (IOException e) {
				e.printStackTrace();
				result = "Error occurred trying to download file!";
			} catch (OutOfMemoryError e) {
				Log.d("TEST", "Out of memory exception occurred!");
			}
		} else {
			result = "File could not be saved! Please make sure that a SD card is mounted correctly!";
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	public static String parseUriToFilename(Uri uri, Activity act) {
		String selectedImagePath = null;
		String filemanagerPath = uri.getPath();

		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = act.managedQuery(uri, projection, null, null, null);

		if (cursor != null) {
			// Here you will get a null pointer if cursor is null
			// This can be if you used OI file manager for picking the media
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			selectedImagePath = cursor.getString(column_index);
		}

		if (selectedImagePath != null) {
			return selectedImagePath;
		} else if (filemanagerPath != null) {
			return filemanagerPath;
		}
		return null;
	}

	public static String getRealPathFromURI(Uri contentURI, ContentResolver cr) {
		Cursor cursor = cr.query(contentURI, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}

	public static String savePhoto(FileInputStream myInputStream) {
		try {
			InputStream is = myInputStream;
			File root = Environment.getExternalStorageDirectory();
			String localFilePath = root.getPath() + "/myFile.jpg";
			FileOutputStream fos = new FileOutputStream(localFilePath, false);
			OutputStream os = new BufferedOutputStream(fos);
			byte[] buffer = new byte[1024];
			int byteRead = 0;
			while ((byteRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, byteRead);
			}
			fos.close();
			return localFilePath;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void uploadFile(final Activity activity, final Dialog dialog, final Intent intent) {
		(new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				Uri selectedImageUri = intent.getData();
				String filePath = null;
				String message = "";
				try {
					String filemanagerstring = selectedImageUri.getPath();
					String selectedImagePath = FileHelper.parseUriToFilename(selectedImageUri, activity);
					if (selectedImagePath != null) {
						filePath = selectedImagePath;
					} else if (filemanagerstring != null) {
						filePath = filemanagerstring;
					} else {
						message = "Error: Unknown path!";
					}

					if (filePath != null) {
						File file = new File(filePath);
						MultiPartPostClient myClient = new MultiPartPostClient(DimeClient.getSettings().getModelConfiguration());
						try {
							myClient.uploadFile(file);
						} catch (IOException ex) {
							Logger.getLogger(UploadPictureToPS.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
						}
						message = "File successfully uploaded to personal server!";
					}

				} catch (Exception e) {
					message = "Error! Cannot upload file..." + e;
				}
				return message;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void onPostExecute(String message) {
				Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
				dialog.dismiss();
				if(activity instanceof ListActivityDime) ((ListActivityDime<GenItem>) activity).reloadList();
			}
		}).execute();
	}
	
}
