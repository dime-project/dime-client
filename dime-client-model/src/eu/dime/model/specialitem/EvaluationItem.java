/*
 *  Description of EvaluationItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 27.04.2012
 */
package eu.dime.model.specialitem;

import java.util.List;
import java.util.Vector;
import eu.dime.model.GenItem;
import eu.dime.model.InvalidJSONItemException;
import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public class EvaluationItem extends GenItem {
	
	public static final String CREATED_TAG = "created";
	public static final String TENANT_ID_TAG = "tenantId";
	public static final String CLIENT_ID_TAG = "clientId";
	public static final String ACTION_TAG = "action";
	public static final String CURRENT_PLACE_TAG = "currPlace";
	public static final String CURRENT_SITUATION_ID_TAG = "currSituationId";
	public static final String VIEW_STACK_TAG = "viewStack";
	public static final String INVOLVED_ITEMS_TAG = "involvedItems";
	
	private String tenantId;
	private String clientId;
	private String action;
	private String currentPlace;
	private String currentSituationId;
	private List<String> viewStack = new Vector<String>();
	private EvaluationInvolvedItem involvedItems = new EvaluationInvolvedItem();

    @Override
    protected void wipeItemForItem() {
    	this.tenantId = "";
		this.clientId = "";
		this.action = "";
		this.currentPlace = "";
		this.currentSituationId = "";
		this.viewStack.clear();
		this.involvedItems = new EvaluationInvolvedItem();
    }

    @Override
    protected GenItem getCloneForItem() {
        EvaluationItem result = new EvaluationItem();
        result.tenantId = this.tenantId;
		result.clientId = this.clientId;
		result.action = this.action;
		result.currentPlace = this.currentPlace;
		result.currentSituationId = this.currentSituationId;
		result.viewStack = this.viewStack;
		result.involvedItems = this.involvedItems;
		return result;
    }

    @Override
    public void readJSONObjectForItem(JSONObject jsonObject) {
    	// clean up first
        wipeItemForItem();
        // read the json
		this.tenantId = getStringValueOfJSONO(jsonObject, TENANT_ID_TAG);
		this.clientId = getStringValueOfJSONO(jsonObject, CLIENT_ID_TAG);
		this.action = getStringValueOfJSONO(jsonObject, ACTION_TAG);
		this.currentPlace = getStringValueOfJSONO(jsonObject, CURRENT_PLACE_TAG);
		this.currentSituationId = getStringValueOfJSONO(jsonObject, CURRENT_SITUATION_ID_TAG);
		this.viewStack = getStringListOfJSONObject(jsonObject, VIEW_STACK_TAG);
		try {
			EvaluationInvolvedItem ii = new EvaluationInvolvedItem();
			ii.readJSONObject(getItemOfJSONO(jsonObject, INVOLVED_ITEMS_TAG));
			this.involvedItems = ii.getClone();
		} catch (InvalidJSONItemException e) {
			e.printStackTrace();
		}
    }

    @Override
    protected JSONObject createJSONObjectForItem(JSONObject newJSONObject) {
    	newJSONObject.addChild(getJSONValue(System.currentTimeMillis(), CREATED_TAG));
    	newJSONObject.addChild(getJSONValue(this.tenantId, TENANT_ID_TAG));
    	newJSONObject.addChild(getJSONValue(this.clientId, CLIENT_ID_TAG));
    	newJSONObject.addChild(getJSONValue(this.action, ACTION_TAG));
    	newJSONObject.addChild(getJSONValue(this.currentPlace, CURRENT_PLACE_TAG));
    	newJSONObject.addChild(getJSONValue(this.currentSituationId, CURRENT_SITUATION_ID_TAG));
    	newJSONObject.addChild(getJSONCollection(this.viewStack, VIEW_STACK_TAG));
    	newJSONObject.addChild(this.involvedItems.createJSONObject());
		return newJSONObject;
    }

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCurrentPlace() {
		return currentPlace;
	}

	public void setCurrentPlace(String currentPlace) {
		this.currentPlace = currentPlace;
	}

	public String getCurrentSituationId() {
		return currentSituationId;
	}

	public void setCurrentSituationId(String currentSituationId) {
		this.currentSituationId = currentSituationId;
	}

	public List<String> getViewStack() {
		return viewStack;
	}

	public void setViewStack(List<String> viewStack) {
		this.viewStack = viewStack;
	}

	public EvaluationInvolvedItem getInvolvedItems() {
		return involvedItems;
	}

	public void setInvolvedItems(EvaluationInvolvedItem involvedItems) {
		this.involvedItems = involvedItems;
	}

  
}
