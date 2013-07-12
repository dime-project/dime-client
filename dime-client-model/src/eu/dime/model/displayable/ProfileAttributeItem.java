/*
 *  Description of PersonItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.05.2012
 */
package eu.dime.model.displayable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;
import sit.json.JSONPathAccessException;
import sit.sstl.StrictSITEnumMap;

/**
 * 
 * @author Simon Thiel
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class ProfileAttributeItem extends DisplayableItem {

	public static final String VALUE_TAG = "value";
	public static final String CATEGORY_TAG = "category";

	public static enum VALUE_CATEGORIES {
		PERSON_NAME, BIRTH_DATE, EMAIL_ADDRESS, PHONE_NUMBER, POSTAL_CODE, AFFILIATION, HOBBY
	};

	public static final StrictSITEnumMap<VALUE_CATEGORIES, ProfileAttributeCategoriesEntry> ValueCategoriesMap = new StrictSITEnumMap(VALUE_CATEGORIES.class, new ProfileAttributeCategoriesEntry[] {
			new ProfileAttributeCategoriesEntry(VALUE_CATEGORIES.PERSON_NAME, "PersonName", "Name", new String[] { "nameHonorificSuffix", "nameFamily", "nameHonorificPrefix", "nameAdditional","nameGiven", "nickname","fullname" }, new String[] { "suffix", "family name", "title", "middle name","first name", "nickname","full name" }),
			new ProfileAttributeCategoriesEntry(VALUE_CATEGORIES.BIRTH_DATE, "BirthDate", "Birthday", new String[] { "birthDate" }, new String[] { "date" }),
			new ProfileAttributeCategoriesEntry(VALUE_CATEGORIES.EMAIL_ADDRESS, "EmailAddress", "Email", new String[] { "emailAddress" }, new String[] { "email address" }),
			new ProfileAttributeCategoriesEntry(VALUE_CATEGORIES.PHONE_NUMBER, "PhoneNumber", "Phone", new String[] { "phoneNumber" }, new String[] { "number" }),
			new ProfileAttributeCategoriesEntry(VALUE_CATEGORIES.POSTAL_CODE, "PostalAddress", "Address", new String[] { "region", "country", "extendedAddress", "addressLocation", "streetAddress", "postalcode", "locality", "pobox" }, new String[] { "region", "country", "addition to address", "location", "street", "postal code", "locality", "PO box" }),
			new ProfileAttributeCategoriesEntry(VALUE_CATEGORIES.AFFILIATION, "Affiliation", "Affiliation", new String[] { "department", "org", "title", "role" }, new String[] { "department", "organistaion", "job description", "role" }),
			new ProfileAttributeCategoriesEntry(VALUE_CATEGORIES.HOBBY, "Hobby", "Hobby", new String[] { "hobby" }, new String[] { "hobby" }) });

	private String category = "";
	private Map<String, String> value = new HashMap();

	public ProfileAttributeItem() {
	}

	public ProfileAttributeItem(String guid) {
		super(guid);
	}

	@Override
	protected void wipeItemForDisplayItem() {
		value.clear();
		category = "";
	}

	@Override
	protected DisplayableItem getCloneForDisplayItem() {
		ProfileAttributeItem result = new ProfileAttributeItem();
		// deep copy of hash map:
		result.value = new HashMap();
		for (Entry<String, String> entry : value.entrySet()) {
			result.value.put(entry.getKey(), entry.getValue());
		}
		result.category = this.category;
		return result;
	}

	@Override
	public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
		this.category = getStringValueOfJSONO(jsonObject, CATEGORY_TAG);
		try {
			value.clear(); // TODO check whether necessary
			JSONObject valuesJSON = jsonObject.getChild(VALUE_TAG);
			for (Map.Entry<String, JSONObject> valueJSON : valuesJSON) {
				value.put(valueJSON.getKey(), valueJSON.getValue().getValue());
			}
		} catch (JSONPathAccessException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "unable to access key:" + VALUE_TAG + " in json");
			Logger.getLogger(this.getClass().getName()).log(Level.FINE, "JSON:\n" + jsonObject.toString());
		}
	}

	@Override
	protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
		newJSONObject.addChild(getJSONValue(category, CATEGORY_TAG));
		JSONObject valuesJSON = new JSONObject(VALUE_TAG);
		for (Entry<String, String> entry : value.entrySet()) {
			valuesJSON.addChild(new JSONObject(entry.getKey(), entry.getValue(), true));
		}
		newJSONObject.addChild(valuesJSON);
		return newJSONObject;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		changed = true;
		this.category = category;
	}

	public Map<String, String> getValue() {
		this.changed = true;
		return value;
	}

	public void setValue(Map<String, String> value) {
		this.changed = true;
		this.value = value;
	}

	/**
	 * value entries will be put to the map in the profile attribute item it is
	 * due to the caller to make sure the keys fit to the category set
	 * 
	 * @param key
	 * @param value
	 */
	public void setValueEntry(String key, String value) {
		this.value.put(key, value);
	}

	/**
	 * updates fields category, name and values according to
	 * ProfileAttributeItem.ValueCategoriesMap !ATTENTION previous values for
	 * category, name and values will be overwritten!
	 * 
	 * @param category
	 */
	public void updateCategoryRelatedFields(VALUE_CATEGORIES category) {
		ProfileAttributeCategoriesEntry categoryEntry = ProfileAttributeItem.ValueCategoriesMap.get(category);
		this.setCategory(categoryEntry.name);
		this.setName(categoryEntry.caption);
		HashMap<String, String> values = new HashMap();
		for (String key : categoryEntry.keys) {
			values.put(key, "");
		}
		this.setValue(values);
	}

	public VALUE_CATEGORIES getCategoryType() {
		for (VALUE_CATEGORIES categoryType : VALUE_CATEGORIES.values()) {
			ProfileAttributeCategoriesEntry categoryEntry = ProfileAttributeItem.ValueCategoriesMap.get(categoryType);
			if (categoryEntry.name.equals(this.category)) {
				return categoryType;
			}
		}
		return null;
	}

	public String getCaption() {
		ProfileAttributeCategoriesEntry entry = getProfileCategoryEntry();
		if (entry != null) {
			return entry.caption;
		}
		return "category type not set";
	}

	public ProfileAttributeCategoriesEntry getProfileCategoryEntry() {
		VALUE_CATEGORIES categoryType = getCategoryType();
		if (categoryType == null) {
			return null;
		}
		return ProfileAttributeItem.ValueCategoriesMap.get(categoryType);
	}

	public static String getCategoryNameOfType(VALUE_CATEGORIES categoryType) {
		return ProfileAttributeItem.ValueCategoriesMap.get(categoryType).name;
	}
	
	public String getLabelForValueKey(String key) {
		String label = "";
		ProfileAttributeCategoriesEntry entry = getProfileCategoryEntry();
		for (int i = 0; i < entry.keys.length; i++) {
			if(entry.keys[i].equals(key)) {
				label = entry.labels[i];
			}
		}
		return label;
	}

}
