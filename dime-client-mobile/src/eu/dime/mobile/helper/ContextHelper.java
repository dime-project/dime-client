package eu.dime.mobile.helper;

import android.util.Log;
import eu.dime.mobile.DimeClient;
import eu.dime.mobile.crawler.Constants;
import eu.dime.mobile.crawler.Defaults;
import eu.dime.mobile.crawler.data.Place;
import eu.dime.mobile.crawler.data.Position;
import eu.dime.model.Model;
import eu.dime.model.TYPES;
import eu.dime.model.context.ContextDataBoolean;
import eu.dime.model.context.ContextDataDouble;
import eu.dime.model.context.ContextDataFactory;
import eu.dime.model.context.ContextDataFloat;
import eu.dime.model.context.ContextDataIntegerList;
import eu.dime.model.context.ContextDataString;
import eu.dime.model.context.ContextDataStringList;
import eu.dime.model.context.ContextItem;
import eu.dime.model.context.constants.Scopes;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

public class ContextHelper {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ContextItem createContextData(String scope, Map<String,Object> data, int validityInSeconds) {
		ContextItem result = new ContextItem(UUID.randomUUID().toString());//TODO use factory method instead
		result.setMType(TYPES.CONTEXT);
		// To Be Revised
		result.getContextSource().id = Constants.SOURCE_NAME;
		result.getContextSource().version = "0.9";
		result.getEntity().id = Model.getInstance().getSettings().mainSAID;
		result.getEntity().type = eu.dime.model.context.constants.Constants.USER;
		result.setTimestamp(System.currentTimeMillis());
		result.setExpires(System.currentTimeMillis() + (validityInSeconds * 1000));
		result.setScope(scope);
		Set keys = data.keySet();
		Vector<String> fields = new Vector();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			fields.add(it.next());
		}
		for (int i=0; i<fields.size(); i++) {
			Object obj = data.get(fields.elementAt(i));
			if (obj instanceof String) {
				String value = (String)obj;
				ContextDataString cData = (ContextDataString)ContextDataFactory.createContextData(fields.elementAt(i));
				cData.setValue(value);
				result.addDataPart(fields.elementAt(i),cData);
			} else if (obj instanceof Double) {
				Double value = (Double)obj;
				ContextDataDouble cData = (ContextDataDouble)ContextDataFactory.createContextData(fields.elementAt(i));
				cData.setValue(value);
				result.addDataPart(fields.elementAt(i),cData);
			} else if (obj instanceof Float) {
				Float value = (Float)obj;
				ContextDataFloat cData = (ContextDataFloat)ContextDataFactory.createContextData(fields.elementAt(i));
				cData.setValue(value);
				result.addDataPart(fields.elementAt(i),cData);
			} else if (obj instanceof Boolean) {
				Boolean value = (Boolean)obj;
				ContextDataBoolean cData = (ContextDataBoolean)ContextDataFactory.createContextData(fields.elementAt(i));
				cData.setValue(value);
				result.addDataPart(fields.elementAt(i),cData);
			} else if (obj instanceof List<?>) {
				try {
					List<String> value = (List<String>)obj;
					ContextDataStringList cData = (ContextDataStringList)ContextDataFactory.createContextData(fields.elementAt(i));
					Iterator<String> iter = value.iterator();
					while (iter.hasNext()) {
						cData.addString(iter.next());
					}
					result.addDataPart(fields.elementAt(i),cData);
				} catch (ClassCastException ex) {
					//Log.w(Constants.LOG_TAG,ex.toString());
				}
				try {
					List<Integer> value = (List<Integer>)obj;
					ContextDataIntegerList cData = (ContextDataIntegerList)ContextDataFactory.createContextData(fields.elementAt(i));
					Iterator<Integer> iter = value.iterator();
					while (iter.hasNext()) {
						cData.addInt(iter.next());
					}
					result.addDataPart(fields.elementAt(i),cData);
				} catch (ClassCastException ex) {
					//Log.w(Constants.LOG_TAG,ex.toString());
				}
			}
		}
		return result;
	}
	
	public static Position getPosition(ContextItem context) {
		Position position = new Position();
		if (!context.getScope().equalsIgnoreCase(Scopes.SCOPE_POSITION))
			return null;
		try {
			ContextDataDouble latitude = (ContextDataDouble)context.getDataPart(Scopes.SCOPE_POSITION_LAT);
			ContextDataDouble longitude = (ContextDataDouble)context.getDataPart(Scopes.SCOPE_POSITION_LON);
			ContextDataFloat accuracy = (ContextDataFloat)context.getDataPart(Scopes.SCOPE_POSITION_ACC);
			ContextDataString locMode = (ContextDataString)context.getDataPart(Scopes.SCOPE_POSITION_LOCMODE);
			position.setLatitude(latitude.getValue());
			position.setLongitude(longitude.getValue());
			position.setAccuracy(accuracy.getValue());
			position.setLocMode(locMode.getValue());
			return position;
		} catch (Exception ex) {
			Log.e(Constants.LOG_TAG,ex.toString());
		}
		return null;
	}
	
	public static Place getCurrentPlace() {
		ContextItem item = (ContextItem)DimeClient.contextCrawler.getCurrentContext(Scopes.SCOPE_CURRENT_PLACE);
		if (item != null) {
			Place place = new Place();
			ContextDataString placeId = (ContextDataString)item.getDataPart(Scopes.SCOPE_CURRENT_PLACE_ID);
			if (placeId != null) place.setPlaceId(placeId.getValue());
			else return null;
			ContextDataString placeName = (ContextDataString)item.getDataPart(Scopes.SCOPE_CURRENT_PLACE_NAME);
			if (placeName != null) place.setPlaceName(placeName.getValue());
			return place;
		}
		return null;
	}
	
	public static String getCurrentSituationGuid() {
		String guid = null;
		ContextItem item = (ContextItem)DimeClient.contextCrawler.getCurrentContext(Scopes.SCOPE_SITUATION);
		if (item != null) {
			ContextDataString contextData = (ContextDataString) item.getDataPart(Scopes.SCOPE_CURRENT_SITUATION);
			if(contextData != null) guid = contextData.getValue();
		}
		return guid;
	}
	
	/**
	 * 
	 * @param placeId id of the current place. Ex. "ametic:35627" or "r355shY" or ..
	 * @param placeName name of the current place. Ex. "Restaurant New Mexico" or "Room 342"   
	 * @param duration expected duration of user's stay in the place (in seconds), null if unknown
	 * @return the ContextItem representing the current place
	 */
	public static ContextItem createCurrentPlaceContextItem(String placeId, String placeName, Integer duration) {
		if (duration == null) duration = Integer.valueOf(Defaults.DEFAULT_VALIDITY);
		Map<String,Object> context = new HashMap<String, Object>();
		context.put(Scopes.SCOPE_CURRENT_PLACE_ID, placeId);
		context.put(Scopes.SCOPE_CURRENT_PLACE_NAME, placeName);
		return createContextData(Scopes.SCOPE_CURRENT_PLACE, context, duration.intValue());
	}
	
	/**
	 * 
	 * @param eventId id of the current event. Ex. "ametic:35627" or "r355shY" or ..
	 * @param eventName name of the current event. Ex. "Science class" or "Initial keynote" or ..
	 * @param duration expected event duration (in seconds), null if unknown
	 * @return the ContextItem representing the current event
	 */
	public static ContextItem createCurrentEventContextItem(String eventId, String eventName, Integer duration) {
		if (duration == null) duration = Integer.valueOf(Defaults.DEFAULT_VALIDITY);
		Map<String,Object> context = new HashMap<String, Object>();
		context.put(Scopes.SCOPE_CURRENT_EVENT_ID, eventId);
		context.put(Scopes.SCOPE_CURRENT_EVENT_NAME, eventName);
		return createContextData(Scopes.SCOPE_CURRENT_EVENT, context, duration.intValue());
	}
	
	/**
	 * 
	 * @param situation current situation. Ex. "@Demo Room" or..
	 * @param duration
	 * @return the ContextItem representing the current situation
	 */
	public static ContextItem createCurrentSituationContextItem(String situation, Integer duration) {
		if (duration == null) duration = Integer.valueOf(Defaults.DEFAULT_VALIDITY);
		Map<String,Object> context = new HashMap<String, Object>();
		context.put(Scopes.SCOPE_CURRENT_SITUATION, situation);
		return createContextData(Scopes.SCOPE_SITUATION, context, duration.intValue());
	}

}
