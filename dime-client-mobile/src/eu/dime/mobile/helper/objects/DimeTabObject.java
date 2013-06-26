package eu.dime.mobile.helper.objects;

import android.app.Activity;

public class DimeTabObject {
	
	public String label;
	public Class<Activity> classObject;
	public DimeIntentObject dio;
	public int tabResourceId;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DimeTabObject(String label, Class classObject, DimeIntentObject dio) {
		this.label = label;
		this.classObject = classObject;
		this.dio = dio;
	}

}
