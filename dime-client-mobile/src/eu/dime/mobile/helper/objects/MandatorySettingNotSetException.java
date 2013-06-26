package eu.dime.mobile.helper.objects;

@SuppressWarnings("serial")
public class MandatorySettingNotSetException extends Exception {
	
	public static final String string = "Mandatory setting not set!";
	
	public MandatorySettingNotSetException(String string) {
        super(string);
    }
	
	public MandatorySettingNotSetException() {
        super(string);
    }

}
