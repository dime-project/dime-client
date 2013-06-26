/*
 *  Description of DetailViewActivity
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 28.03.2012
 */
package eu.dime.mobile.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.R;
import eu.dime.mobile.control.AndroidLoadingViewHandler;
import eu.dime.mobile.view.helpers.DimeIntentObject;
import eu.dime.mobile.view.helpers.DimeIntentObjectHelper;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import sit.android.view.ViewHelper;

/**
 *
 * @author Simon Thiel
 */
public class DetailViewActivity extends Activity {

    static final int DIALOG_CLOSE_ID = 0;
    private ViewHelper vh = new ViewHelper();
    private TYPES itemType;
    private DisplayableItem item;
    private TYPES groupType;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.detail_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUI();

    }

    private void initUI() {
        DimeIntentObject dio = new DimeIntentObjectHelper().readIntent(this.getIntent());
        this.groupType = dio.getGroupType();
        this.itemType = dio.getItemType();

        String myTitle = (dio.getAction() == DimeIntentObject.ACTIONS.SHOW) ? "" : "New ";

        myTitle += ModelHelper.getNameOfType(this.itemType);
        this.setTitle(myTitle);

        TextView typeView = ((TextView) this.findViewById(R.id.detail_view_type));
        typeView.setText(ModelHelper.getStringType(itemType));

        if (dio.getAction() == DimeIntentObject.ACTIONS.CREATE) {
            this.item = (DisplayableItem) ItemFactory.createNewItemByType(itemType);
            this.item.setMType(itemType);
        } else {
            this.item = (DisplayableItem) Model.getInstance().getItem(new ModelRequestContext(DimeClient.getUserMainSaid(),
                    Model.ME_OWNER, new AndroidLoadingViewHandler(this)), itemType, dio.getItemId());

            getNameView().setText(this.item.getName());


        }

        vh.populateImageView(this.findViewById(android.R.id.content), R.id.detail_view_image, this.item.getImageUrl(), DefaultImages.getDefaultImageId(itemType), this);


    }

    private EditText getNameView() {
        return ((EditText) this.findViewById(R.id.detail_view_name));
    }

    private ImageView getImageView() {
        return ((ImageView) this.findViewById(R.id.detail_view_image));
    }

    private void updateItemFromView() {
        this.item.setName(getNameView().getText().toString());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch (id) {
            case DIALOG_CLOSE_ID:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Item was changed. Save it?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        DetailViewActivity.this.updateItemFromView();
                        Model.getInstance().updateItem(new ModelRequestContext(DimeClient.getUserMainSaid(),
                                Model.ME_OWNER, new AndroidLoadingViewHandler(DetailViewActivity.this)), item);
                        DetailViewActivity.super.onBackPressed();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        DetailViewActivity.super.onBackPressed();
                    }
                });
                AlertDialog alert = builder.create();
                dialog = alert;
                break;

            default:
                dialog = null;
        }
        return dialog;

    }

    @Override
    public void onBackPressed() {
        boolean wasChanged = false;

        if (!getNameView().getText().toString().equals(item.getName())) { //name was changed
            wasChanged = true;
        }

        if (wasChanged) {
            showDialog(DIALOG_CLOSE_ID);
            return;
        }
        super.onBackPressed();

    }
}
