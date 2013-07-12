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
        View vi = convertView;
        vi = mInflater.inflate(R.layout.adapter_profileattribute_item, null);
        ProfileAttributeItem pai = (ProfileAttributeItem) mItems.get(position);
        LinearLayout tl = (LinearLayout) vi.findViewById(R.id.attributes);
        tl.removeAllViews();
        TextView labelCat = (TextView) vi.findViewById(R.id.attribute_category);
        CheckBox cb = (CheckBox) vi.findViewById(R.id.attribute_checkBox);
        if (profile.isEditable()) {
            cb.setVisibility(View.VISIBLE);
        } else {
            cb.setVisibility(View.GONE);
            labelCat.setPadding(0, 20, 0, 20);
        }
        labelCat.setText(pai.getCategory());
        int counter = 0;
        for (String key : pai.getValue().keySet()) {
        	String label = pai.getLabelForValueKey(key);
            String value = pai.getValue().get(key);
            LinearLayout line = new LinearLayout(context);
            line.setOrientation(LinearLayout.HORIZONTAL);
            line.setPadding(0, 2, 0, 2);
            LinearLayout.LayoutParams lpms = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
            lpms.setMargins(10, 0, 0, 0);
            TextView labelTV = UIHelper.createTextView(context, R.style.dimeTheme, -1, -1, lpms, true);
            labelTV.setText(label);
            EditText valueET = new EditText(context);
            valueET.setText(value);
            valueET.setTextAppearance(context, R.style.dimeTheme);
            LinearLayout.LayoutParams lpms2 = new LinearLayout.LayoutParams(150, LayoutParams.WRAP_CONTENT, 1f);
            lpms2.setMargins(0, 0, 10, 0);
            valueET.setLayoutParams(lpms2);
            valueET.setTextSize(14);
            if (profile.isEditable()) {
                valueET.setInputType(UIHelper.switchInputType(key));
                valueET.setEnabled(true);
                valueET.addTextChangedListener(new DimeTextWatcher(pai, key));
            } else {
                valueET.setEnabled(false);
            }
            line.addView(labelTV);
            line.addView(valueET);
            if (counter % 2 == 0) {
                line.setBackgroundColor(context.getResources().getColor(R.color.dm_row_alternate));
            } else {
                line.setBackgroundColor(context.getResources().getColor(R.color.dm_row_alternate_light));
            }
            tl.addView(line, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            counter++;
        }
        cb.setOnCheckedChangeListener(new CheckListener<DisplayableItem>(position, this));
        return vi;
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