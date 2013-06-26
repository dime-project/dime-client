/*
 *  Description of Class
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 06.06.2012
 */
package eu.dime.mobile.control;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import eu.dime.mobile.DimeClient;
import eu.dime.model.CALLTYPES;
import eu.dime.model.GenItem;
import eu.dime.restapi.RestApiAccess;
import java.util.List;
import java.util.Vector;

/**
 * Class
 * 
 */
class RestApiAccessor extends AsyncTask<RestCallParams, Object, List<GenItem>> {
    private ProgressDialog dialog;    

    RestApiAccessor() {
        
    }

    @Override
    protected void onPreExecute() {
        Context context = DimeClient.getActivityContext();
        if (context != null) {
            dialog = new ProgressDialog(context);
            dialog.setMessage("please_wait_while_loading");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    @Override
    protected List<GenItem> doInBackground(RestCallParams... arg0) {
        if (arg0[0].operation == CALLTYPES.AT_ALL_GET) {
            return RestApiAccess.getAllItems(arg0[0].hoster, arg0[0].owner, arg0[0].type);
        } //else
        List<GenItem> result = new Vector();
        GenItem resItem = null;
        if (arg0[0].operation == CALLTYPES.AT_ITEM_GET) {
            resItem = RestApiAccess.getItem(arg0[0].hoster, arg0[0].owner, arg0[0].type, arg0[0].guid);
        } else if (arg0[0].operation == CALLTYPES.AT_ITEM_POST_UPDATE) {
            resItem = RestApiAccess.postItemUpdate(arg0[0].hoster, arg0[0].owner, arg0[0].item.getMType(), arg0[0].item);
        } else if (arg0[0].operation == CALLTYPES.AT_ITEM_DELETE) {
            resItem = RestApiAccess.removeItem(arg0[0].hoster, arg0[0].owner, arg0[0].type, arg0[0].guid);
        }
        if (resItem != null) {
            result.add(resItem);
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<GenItem> result) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
