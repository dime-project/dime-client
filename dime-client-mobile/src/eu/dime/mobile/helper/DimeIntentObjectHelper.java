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
 *  Description of DimeIntentObjectHelper
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 29.03.2012
 */
package eu.dime.mobile.helper;

import android.content.Intent;
import android.os.Bundle;
import java.io.Serializable;

import eu.dime.mobile.helper.objects.DimeIntentObject;

/**
 *
 * @author Simon Thiel
 */
public class DimeIntentObjectHelper {
	
    public static DimeIntentObject readIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
        	return ((DimeIntentObject) extras.get(DimeIntentObject.TAG));
        } else {
            throw new UnsupportedOperationException("Error. Extra not set!");
        }
    }

    public static Intent populateIntent(Intent intent, DimeIntentObject dimeIntentObject) {
        intent.putExtra(DimeIntentObject.TAG, (Serializable)dimeIntentObject);
        return intent;
    }
}
