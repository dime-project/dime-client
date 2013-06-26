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
