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
