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

package eu.dime.mobile.view.dialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import eu.dime.control.LoadingViewHandler;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.AndroidModelHelper;
import eu.dime.mobile.helper.ImageHelper;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.handler.LoadingViewHandlerFactory;
import eu.dime.mobile.view.abstr.ListActivityDisplayableItem;
import eu.dime.mobile.view.adapter.BaseAdapter_Match;
import eu.dime.model.GenItem;
import eu.dime.model.Model;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.usernotification.UNEntryMergeRecommendation;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListActivity_Merge_Dialog extends ListActivityDisplayableItem implements OnClickListener{
	
	private UserNotificationItem uni;
	private PersonItem pi;
	private TextView similarity;
    private double similarityValue = 0.0;

    /**
     * Called when the activity is first created.
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = ListActivity_Merge_Dialog.class.getSimpleName();
		setContentView(R.layout.dialog_merge);
		setBaseAdapter(new BaseAdapter_Match());
		similarity = (TextView) findViewById(R.merge.similarity);
	}
  
	@Override
	protected List<DisplayableItem> loadListData() {
		uni = (UserNotificationItem) Model.getInstance().getItem(mrContext, dio.getItemType(), dio.getItemId());
		UNEntryMergeRecommendation entry = (UNEntryMergeRecommendation)uni.getUnEntry();
		similarityValue = entry.getSimilarity();
		pi = (PersonItem) Model.getInstance().getItem(mrContext, TYPES.PERSON, entry.getSourceId());
		List<DisplayableItem> matchSuggestions = new ArrayList<DisplayableItem>();
		PersonItem person = (PersonItem) Model.getInstance().getItem(mrContext, TYPES.PERSON, entry.getTargetId());
		if(person != null) matchSuggestions.add(person);
		return matchSuggestions;
	}

	@Override
	protected void initializeHeader() {
		if(pi != null){
			TextView name = (TextView) findViewById(R.merge.person_name);
			ImageView image = (ImageView) findViewById(R.merge.person_image);
			ImageHelper.loadImageAsynchronously(image, pi, this);
			name.setText(pi.getName());
		}
		similarity.setText(UIHelper.formatDoubleToPercentage(similarityValue));
	}
	
	@Override
	public void onClick(View v) {
		 UNEntryMergeRecommendation entry = (UNEntryMergeRecommendation)uni.getUnEntry();
		 switch (v.getId()) {
         case R.merge.button_merge:
        	 entry.setStatus(UNEntryMergeRecommendation.STATUS_TYPES[1]);
        	 uni.setUnEntry(entry);
        	 (new AsyncTask<Void, Void, String>() {
         		
     			@Override
                 protected String doInBackground(Void... params) {
     				String result = "";
     				try {
     					UNEntryMergeRecommendation entry = (UNEntryMergeRecommendation)uni.getUnEntry();
     					Model.getInstance().mergeItems(mrContext, TYPES.PERSON, Arrays.asList(entry.getSourceId(), entry.getTargetId()));
     					Model.getInstance().removeItem(mrContext, uni.getGuid(), uni.getMType());
     					result = "Successfully merged the persons!";
     				} catch (Exception e) {
     					result= "Could not merge persons!";
     				}
                    return result;
                 }

     			@Override
                 protected void onPostExecute(String result) {
     				Toast.makeText(DimeClient.getAppContext(), result, Toast.LENGTH_LONG).show();
     				AndroidModelHelper.sendEvaluationDataAsynchronously(null, mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_merge_save));
     				ListActivity_Merge_Dialog.this.finish();
                 }
                 
             }).execute();
             break;
             
         case R.merge.button_decline:
        	 entry.setStatus(UNEntryMergeRecommendation.STATUS_TYPES[2]);
        	 uni.setUnEntry(entry);
        	 (new AsyncTask<Void, Void, String>() {
         		
     			@Override
                 protected String doInBackground(Void... params) {
     				String result = "";
     				try {
     					Model.getInstance().updateItem(mrContext, uni);
     					Model.getInstance().removeItem(mrContext, uni.getGuid(), uni.getMType());
     					result = "Merge declined successfully!";
     				} catch (Exception e) {
     					result= "Could not decline merge!";
     				}
                     return result;
                 }

     			@Override
                protected void onPostExecute(String result) {
     				Toast.makeText(DimeClient.getAppContext(), result, Toast.LENGTH_LONG).show();
     				AndroidModelHelper.sendEvaluationDataAsynchronously(null, mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_merge_save));
     				ListActivity_Merge_Dialog.this.finish();
                }
                 
             }).execute();
        	 break;

         case R.merge.button_cancel: 
        	 AndroidModelHelper.sendEvaluationDataAsynchronously(new ArrayList<GenItem>(), mrContext, getResources().getString(R.string.self_evaluation_tool_dialog_canceled));
        	 finish();
        	 break;
	    }
	
    }

	@Override
	public void notificationReceived(String fromHoster, NotificationItem item) {
    	
	}

	@Override
	protected LoadingViewHandler createLoadingViewHandler() {
		return LoadingViewHandlerFactory.<ListActivity_Merge_Dialog>createLVH(ListActivity_Merge_Dialog.this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}
	
}
