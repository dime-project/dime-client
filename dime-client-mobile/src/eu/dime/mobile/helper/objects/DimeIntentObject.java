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
 *  Description of DimeIntentObject
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 28.03.2012
 */
package eu.dime.mobile.helper.objects;

import eu.dime.mobile.helper.UIHelper;
import eu.dime.model.GenItem;
import eu.dime.model.Model;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;

import java.io.Serializable;

import android.util.Log;

/**
 *
 * @author Simon Thiel
 */
@SuppressWarnings("serial")
public class DimeIntentObject implements Serializable {

    public static final String TAG = "DimeIntentObject";
    
    private TYPES itemType = null;
    private String itemId = null;
    private String ownerId = Model.ME_OWNER;
    
    public DimeIntentObject() {	}

    public DimeIntentObject(TYPES itemType) {
        this.itemId = Model.ME_OWNER;
        this.itemType = itemType;
    }
    
    public DimeIntentObject(String itemId, TYPES itemType) {
        this.itemId = itemId;
        this.itemType = itemType;
    }
    
    public DimeIntentObject(String itemId, TYPES itemType, String ownerId) {
        this.itemId = itemId;
        this.itemType = itemType;
        this.ownerId = ownerId;
    }

    public DimeIntentObject(GenItem item) {
    	this.itemId = item.getGuid();
        this.itemType = item.getMType();
        if(item instanceof DisplayableItem) {
        	DisplayableItem di = ((DisplayableItem)item);
        	if (di.getUserId()!=null && di.getUserId().length() > 0) {
        		this.ownerId = di.getUserId();
        	} else {
        		Log.d(TAG, "UserId null or empty for " + UIHelper.formatStringOnlyFirstCharUpperCase(di.getType().toString()) + " " + di.getName() + "!");
        	}
        }
    }

	public TYPES getItemType() {
        return itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
	
	public DimeIntentObject getClone() {
		return new DimeIntentObject(this.itemId, this.itemType);
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
   
}
