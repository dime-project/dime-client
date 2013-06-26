package eu.dime.model.specialitem.advisory;

import java.util.List;
import java.util.Vector;
import sit.json.JSONObject;
import eu.dime.model.InvalidJSONItemException;

public class WarningAttributesDisjunctGroups extends WarningAttributesObject {
	
	public static final String PREVIOUS_SHARED_GROUPS = "previousSharedGroups";
    public static final String CONCERNED_PERSONS = "concernedPersons";
    public static final String CONCERNED_RESOURCES = "concernedResources";

	private List<String> previousSharedGroups;
    private List<String> concernedPersons;
    private List<String> concernedResources;

    public WarningAttributesDisjunctGroups() {
        wipeItem();
    }
    
    public WarningAttributesDisjunctGroups(List<String> previousSharedGroups, List<String> concernedPersons, List<String> conecernedResources) {
        this.previousSharedGroups = previousSharedGroups;
        this.concernedPersons = concernedPersons;
        this.concernedResources = conecernedResources;
    }

    @Override
    public WarningAttributesDisjunctGroups getClone() {
        return new WarningAttributesDisjunctGroups(previousSharedGroups, concernedPersons, concernedResources);
    }

    @Override
    protected final void wipeItem() {
        this.previousSharedGroups = new Vector<String>();
        this.concernedPersons = new Vector<String>();
        this.concernedResources = new Vector<String>();
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONCollection(previousSharedGroups, PREVIOUS_SHARED_GROUPS));
        result.addChild(getJSONCollection(concernedPersons, CONCERNED_PERSONS));
        result.addChild(getJSONCollection(concernedResources, CONCERNED_RESOURCES));
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        // clean up first
        wipeItem();

        // read the json
        this.previousSharedGroups = getStringListOfJSONObject(jsonObject, PREVIOUS_SHARED_GROUPS);
        this.concernedPersons = getStringListOfJSONObject(jsonObject, CONCERNED_PERSONS);
        this.concernedResources = getStringListOfJSONObject(jsonObject, CONCERNED_RESOURCES);
    }

    public List<String> getPreviousSharedGroups() {
        return previousSharedGroups;
    }

    public List<String> getConcernedPersons() {
        return concernedPersons;
    }

    public List<String> getConcernedResources() {
        return concernedResources;
    }

}
