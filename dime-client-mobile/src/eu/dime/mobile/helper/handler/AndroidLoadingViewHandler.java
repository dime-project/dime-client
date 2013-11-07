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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.mobile.helper.handler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import eu.dime.control.AbstractLoadingViewHandler;
import eu.dime.control.TimeOutWhileLoadingException;
import eu.dime.model.GenItem;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @param <T> Actual calling extending Activity
 * @author simon
 */
public class AndroidLoadingViewHandler<T extends Activity> extends AbstractLoadingViewHandler {

    private final static int DIALOG_TYPE_UNDEFINED = 0;
    private final static int DIALOG_TYPE_LOADING = 1;
    private final static int DIALOG_TYPE_CONFIRM = 2;
    private final static Object currDialogLock = new Object();
    private static Dialog currDialog = null;
    private static int currDialogType = DIALOG_TYPE_UNDEFINED;
    private boolean continueAfterTimeOut = true;
    private final T activity;
    private final Object waitForConfirmationLock = new Object();
    private boolean confirmationHandled = false;
    

    public AndroidLoadingViewHandler(T activity) { //TODO add string which is displayed as message of the alert dialog
        this.activity = activity;
    }
    
    public void showLoadingView() {
    	setContinueAfterTimeOut(true); //this also indicates, whether loading was successful without running into a timeout 
        setAndShowCurrDialog(DIALOG_TYPE_LOADING);
        try {
            waitForLoading();
        } catch (TimeOutWhileLoadingException ex) {
            //silently catch exception
        }
        AndroidLoadingViewHandler.this.dismissCurrDialog(DIALOG_TYPE_LOADING);
    }

    public GenItem showCreateLoadingView(final String oldGUID) {
        setAndShowCurrDialog(DIALOG_TYPE_LOADING);
        GenItem result = waitForCreateItem(oldGUID);
        AndroidLoadingViewHandler.this.dismissCurrDialog(DIALOG_TYPE_LOADING);
        return result;
    }
    
    @Override
    protected void handleTimeOutNotified() {
        synchronized(waitForConfirmationLock){
            confirmationHandled=false;
            setAndShowCurrDialog(DIALOG_TYPE_CONFIRM);
            //wait for user entry
            while(!isConfirmationHandled()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AndroidLoadingViewHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //reset the currDialog - since the confirm dialog dismissed itself
            synchronized (AndroidLoadingViewHandler.currDialogLock) {
                AndroidLoadingViewHandler.currDialog = null;
                AndroidLoadingViewHandler.currDialogType=DIALOG_TYPE_UNDEFINED;
            }
        }
    }

    private void setAndShowCurrDialog(final int dialogType) {
        if ((currDialogType == dialogType) || (dialogType == DIALOG_TYPE_UNDEFINED)) {
            return;
        }        
        activity.runOnUiThread(new Runnable() {
            public void run() {
                synchronized (AndroidLoadingViewHandler.currDialogLock) {
                    //check again (for performance reasons)
                    if (currDialogType == dialogType) {
                        return;
                    }
                    //do not replace a confirmation dialog
                    if (currDialogType == DIALOG_TYPE_CONFIRM) {                        
                        if (dialogType == DIALOG_TYPE_CONFIRM){
                            throw new RuntimeException("Received two confirmation dialog requests at the same time");
                        }//else
                        return;                        
                    }
                    if (AndroidLoadingViewHandler.currDialog != null) {
                        AndroidLoadingViewHandler.currDialog.dismiss();
                    }
                    Dialog dialog = null;
                    if (dialogType == DIALOG_TYPE_LOADING) {
                        dialog = createProgressDialog(AndroidLoadingViewHandler.this);
                        dialog.setOnCancelListener(new OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								
							}
						});
                    } else if (dialogType == DIALOG_TYPE_CONFIRM) {
                        dialog = createTimeoutConfirmationDialog(AndroidLoadingViewHandler.this);
                    }
                    AndroidLoadingViewHandler.currDialog = dialog;
                    AndroidLoadingViewHandler.currDialogType = dialogType;
                    if(!activity.isFinishing()){
                    	AndroidLoadingViewHandler.currDialog.show();
                    }
                }
            }
        });
    }

    private void dismissCurrDialog(final int originDialogType) {
        synchronized (AndroidLoadingViewHandler.currDialogLock) {
            if (AndroidLoadingViewHandler.currDialog != null) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        //since this is handled async, we need to lock and check for null again
                        //first check (3 lines above) is only for performance reasons
                        synchronized (AndroidLoadingViewHandler.currDialogLock) {
                            //do not close a confirmation dialog from a loading returner
                            if ((currDialogType == DIALOG_TYPE_CONFIRM) && (originDialogType== DIALOG_TYPE_LOADING)){
                                return;
                            }
                            if (AndroidLoadingViewHandler.currDialog != null) {
                                AndroidLoadingViewHandler.currDialog.dismiss();
                                AndroidLoadingViewHandler.currDialog = null;
                                AndroidLoadingViewHandler.currDialogType = DIALOG_TYPE_UNDEFINED;
                            }
                        }
                    }
                });
            }
        }
    }
    
    @SuppressWarnings("rawtypes")
	private static <T extends Activity> AlertDialog createProgressDialog(final AndroidLoadingViewHandler parentInstance) {   
        AlertDialog dialog = new AlertDialog.Builder(parentInstance.activity).setTitle("Loading Data").setMessage("...").create();
        return dialog;
    }

    @SuppressWarnings("rawtypes")
	private static <T extends Activity> AlertDialog createTimeoutConfirmationDialog(final AndroidLoadingViewHandler parentInstance) {
        AlertDialog result = new AlertDialog.Builder(parentInstance.activity).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Network Connection Timeout").setMessage("Retry?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                parentInstance.setContinueAfterTimeOut(true);
                parentInstance.setConfirmationHandled(true);                
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                parentInstance.setContinueAfterTimeOut(false);
                parentInstance.setConfirmationHandled(true);
            }
        }).create();
        return result;
    }

    public boolean doContinueAfterTimeOut() {
        return isContinueAfterTimeOut();
    }

    /**
     * @return the continueAfterTimeOut
     */
    public boolean isContinueAfterTimeOut() {
        return continueAfterTimeOut;
    }

    /**
     * @param continueAfterTimeOut the continueAfterTimeOut to set
     */
    public void setContinueAfterTimeOut(boolean continueAfterTimeOut) {
        this.continueAfterTimeOut = continueAfterTimeOut;
    }

    /**
     * @return the confirmationHandled
     */
    public boolean isConfirmationHandled() {
        return confirmationHandled;
    }

    /**
     * @param confirmationHandled the confirmationHandled to set
     */
    public void setConfirmationHandled(boolean confirmationHandled) {
        this.confirmationHandled = confirmationHandled;
    }
    
}
