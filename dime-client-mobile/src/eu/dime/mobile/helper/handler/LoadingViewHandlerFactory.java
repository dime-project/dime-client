/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.mobile.helper.handler;

import android.app.Activity;
import eu.dime.control.LoadingViewHandler;

/**
 *
 * @param <T> subclass of activity
 * @author simon
 */
public class LoadingViewHandlerFactory {
    /**
     * 
     * @param <T>
     * @param activity
     * @return
     */
    public static <T extends Activity> LoadingViewHandler createLVH(T activity){
        //ATTENTION - make sure this handler is not called by the UIThread!!
        AndroidLoadingViewHandler<T> result =  new AndroidLoadingViewHandler<T>(activity);
        return result;
    }
    
}
