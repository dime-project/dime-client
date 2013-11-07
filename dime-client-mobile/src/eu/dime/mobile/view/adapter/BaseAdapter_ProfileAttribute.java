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

package eu.dime.mobile.view.adapter;

import java.util.Timer;
import java.util.TimerTask;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import eu.dime.mobile.R;
import eu.dime.mobile.helper.UIHelper;
import eu.dime.mobile.helper.listener.CheckListener;
import eu.dime.mobile.view.abstr.BaseAdapterDisplayableItem;
import eu.dime.model.Model;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;

public class BaseAdapter_ProfileAttribute extends BaseAdapterDisplayableItem {

    ProfileItem profile;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ProfileAttributeItem pai = (ProfileAttributeItem) mItems.get(position);
        convertView = mInflater.inflate(R.layout.adapter_profileattribute_item, null);
        LinearLayout tl = (LinearLayout) convertView.findViewById(R.id.attributes);
        TextView labelCat = (TextView) convertView.findViewById(R.id.attribute_category);
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.attribute_checkBox);
        convertView.setBackgroundColor(context.getResources().getColor((position % 2 == 0) ? R.color.background_grey_metabar : android.R.color.white));
        labelCat.setText(pai.getCaption());
        for (String key : pai.getValue().keySet()) {
        	String label = pai.getLabelForValueKey(key);
            String value = pai.getValue().get(key);
            LinearLayout line = new LinearLayout(context);
            line.setOrientation(LinearLayout.VERTICAL);
            line.setPadding(UIHelper.getDPvalue(10), UIHelper.getDPvalue(2), UIHelper.getDPvalue(10), UIHelper.getDPvalue(2));
            LinearLayout.LayoutParams lpms = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            lpms.setMargins(UIHelper.getDPvalue(10), 0, 0, 0);
            TextView labelTV = UIHelper.createTextView(context, R.style.dimeTheme, -1, 11, lpms, true);
            labelTV.setText(label);
            EditText valueET = new EditText(context);
            valueET.setText(value);
            valueET.setTextAppearance(context, R.style.dimeTheme);
            LinearLayout.LayoutParams lpms2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            lpms2.setMargins(0, 0, UIHelper.getDPvalue(10), 0);
            valueET.setLayoutParams(lpms2);
            if (profile.getUserId().equals(Model.ME_OWNER) && profile.isEditable()) {
                valueET.setInputType(UIHelper.switchInputType(key));
                valueET.setEnabled(true);
                valueET.addTextChangedListener(new DimeTextWatcher(pai, key));
            } else {
                valueET.setEnabled(false);
                valueET.setFocusable(false);
            }
            line.addView(labelTV);
            line.addView(valueET);
            tl.addView(line, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
        boolean isEditable = profile.getUserId().equals(Model.ME_OWNER) && profile.isEditable();
		cb.setEnabled(isEditable);
		if(isEditable) {
			cb.setChecked(selection.contains(profile.getGuid()));
			cb.setOnCheckedChangeListener(new CheckListener<DisplayableItem>(position, this));
		}
        return convertView;
    }

    class DimeTextWatcher implements TextWatcher {

        ProfileAttributeItem pai = null;
        String key = "";
        private Timer timer=new Timer();

        public DimeTextWatcher(ProfileAttributeItem pai, String key) {
            this.pai = pai;
            this.key = key;
        }

        @Override
        public void afterTextChanged(final Editable s) {
        	timer.cancel();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                	(new AsyncTask<Void, Void, String>() {
                		
                		@Override
                        protected String doInBackground(Void... params) {
                			String message = "";
                			try {
                                pai.getValue().put(key, s.toString());
                                Model.getInstance().updateItem(mrContext, pai);
                                message = "Profile attribute changed to " + s.toString();
            				} catch (RuntimeException e) {
            					message = "Error: " + e.getMessage();
            				} catch (Exception e) {
                                message = "Text not refreshed!";
                            }
                            return message;
                        }

                        @Override
                        protected void onPostExecute(String result) {
                        	Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                        }
                    }).execute();
                }

            }, 2000);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    public void setProfile(ProfileItem profile) {
        this.profile = profile;
    }
}