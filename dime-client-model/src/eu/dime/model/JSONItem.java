/*
 *  Description of JSONItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 03.05.2012
 */
package eu.dime.model;

import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.json.JSONPathAccessException;

/**
 *
 * @author Simon Thiel
 */
public abstract class JSONItem<T> {

    public abstract T getClone();

    protected abstract void wipeItem();

    public abstract JSONObject createJSONObject();

    public abstract void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException;

    protected String getStringValueOfJSONO(JSONObject jsonO, String key) {
        try {
            JSONObject valueO = jsonO.getChild(key);
            if (valueO != null) {
                String result = valueO.getValue();
                if (result != null) {
                    return result;
                }
            }
        } catch (JSONPathAccessException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                    "unable to access key:" + key + " in json");
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "JSON:\n" + jsonO.toString());
        }
        return "";
    }

    protected int getIntegerValueOfJSONO(JSONObject jsonO, String key) {
        try {
            String result = getStringValueOfJSONO(jsonO, key);
            if (result != null) {
                return Integer.parseInt(result);
            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                    "value of " + key + " is not of type Integer");
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "JSON:\n" + jsonO.toString());
        }
        return -1;
    }

    protected long getLongValueOfJSONO(JSONObject jsonO, String key) {
        try {
            String result = getStringValueOfJSONO(jsonO, key);
            if (result != null) {
                return Long.parseLong(result);
            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                    "value of " + key + " is not of type Long");
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "JSON:\n" + jsonO.toString());
        }
        return -1;
    }

    protected double getDoubleValueOfJSONO(JSONObject jsonO, String key) {
        try {
            String result = getStringValueOfJSONO(jsonO, key);
            if (result != null) {
                return Double.parseDouble(result);
            } else {
            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                    "value of " + key + " is not of type Double");
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "JSON:\n" + jsonO.toString());
        }
        return -1.0;
    }

    protected float getFloatValueOfJSONO(JSONObject jsonO, String key) {
        try {
            String result = getStringValueOfJSONO(jsonO, key);
            if (result != null) {
                return Float.parseFloat(result);
            } else {
            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                    "value of " + key + " is not of type Float");
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "JSON:\n" + jsonO.toString());
        }
        return -1;
    }

    protected boolean getBooleanValueOfJSONO(JSONObject jsonO, String key) {

        String result = getStringValueOfJSONO(jsonO, key);
        if (result != null) {
            return Boolean.parseBoolean(result);
        }

        return false;
    }

    protected JSONObject getItemOfJSONO(JSONObject jsonO, String key) {
        try {
            return jsonO.getChild(key);
        } catch (JSONPathAccessException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                    "unable to access key:" + key + " in json");
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "JSON:\n" + jsonO.toString());
        }
        return null;
    }

    protected Vector<JSONObject> getItemsOfJSONO(JSONObject jsonO, String key) {

        try {
            JSONObject childs = jsonO.getChild(key);
            if (childs != null) {
                return childs.getItems();
            }
        } catch (JSONPathAccessException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                    "unable to access key:" + key + " in json");
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "JSON:\n" + jsonO.toString());
        }
        return new Vector();
    }
    
    protected void readStringListOfJSONObjectIntoCollection(JSONObject jsonO, String key, 
            Collection<String> result){
        
        for (String stringEntry: getStringListOfJSONObject(jsonO, key)){
            result.add(stringEntry);
        }        
    }

    protected List<String> getStringListOfJSONObject(JSONObject jsonO, String key) {

        try {
            return getStringListOfJSONCollection(jsonO.getChild(key));

        } catch (JSONPathAccessException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                    "unable to access key:" + key + " in json");
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "JSON:\n" + jsonO.toString());
        } catch(NullPointerException e){
        	
        }
        return new Vector();
    }

    protected List<String> getStringListOfJSONCollection(JSONObject jsonO) {
        Vector<String> result = new Vector();

        if (jsonO != null) {
            for (JSONObject child : jsonO.getItems()) {

                result.add(child.getValue());
            }
        }

        return result;
    }

    protected List<Integer> getIntegerListOfJSONCollection(JSONObject jsonO) {
        Vector<Integer> result = new Vector();

        if (jsonO != null) {
            for (JSONObject child : jsonO.getItems()) {
                try {
                    String strVal = child.getValue();
                    if (strVal != null) {
                        result.add(Integer.parseInt(strVal));
                    }
                } catch (NumberFormatException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
                            "value in " + jsonO.getKey() + " is not of type Integer");
                    Logger.getLogger(this.getClass().getName()).log(Level.FINE, "JSON:\n" + jsonO.toString());

                }
            }
        }
        return result;
    }

    protected JSONObject getJSONValue(Double value, String key) {
        JSONObject result = new JSONObject(key);
        result.setValue("" + value, false);
        return result;
    }

    protected JSONObject getJSONValue(Float value, String key) {
        JSONObject result = new JSONObject(key);
        result.setValue("" + value, false);
        return result;
    }

    protected JSONObject getJSONValue(Integer value, String key) {
        JSONObject result = new JSONObject(key);
        result.setValue("" + value, false);
        return result;
    }

    protected JSONObject getJSONValue(Long value, String key) {
        JSONObject result = new JSONObject(key);
        result.setValue("" + value, false);
        return result;
    }

    protected JSONObject getJSONValue(Boolean value, String key) {
        JSONObject result = new JSONObject(key);
        result.setValue("" + value, false);
        return result;
    }

    protected JSONObject getJSONValue(String value, String key) {
        JSONObject result = new JSONObject(key);
        result.setValue(value, true);
        return result;
    }

    protected JSONObject getJSONObject(JSONItem item, String key) {
        JSONObject result = item.createJSONObject();
        result.setKey(key);
        return result;
    }

    protected JSONObject getJSONCollection(Iterable<String> values, String key) {

        JSONObject result = new JSONObject(key);

        result.setType(JSONObject.JSON_TYPE_COLLECTION); // set the type manually for cases values==null and values.isEmpty()
        //otherwise an empty set is handled as empty object
        if (values == null) {
            return result;
        }
        
        for (String value : values) {
            result.addItem(getJSONValue(value, key));
        }
        return result;
    }

    protected JSONObject getJSONCollectionFromIntegers(List<Integer> values, String key) {

        JSONObject result = new JSONObject(key);

        result.setType(JSONObject.JSON_TYPE_COLLECTION); // set the type manually for cases values==null and values.isEmpty()
        //otherwise an empty set is handled as empty object
        if (values == null) {
            return result;
        }

        for (int i = 0; i < values.size(); i++) {
            result.addItem(getJSONValue(values.get(i), key));
        }
        return result;
    }
}
