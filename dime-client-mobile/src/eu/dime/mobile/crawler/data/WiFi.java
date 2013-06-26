package eu.dime.mobile.crawler.data;

public class WiFi {

	private String macAddress = "";
	private String ssid = "";
	private int signalStrength;
	private int occourrences = 0;
	
	public WiFi(String mac, String ssid, int sstr, int i) {
		this.macAddress = mac;
		this.ssid = ssid;
		this.signalStrength = sstr;
		this.occourrences = i;
	}
	
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public int getSignalStrength() {
		return signalStrength;
	}
	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}

	public int getOccourrences() {
		return occourrences;
	}

	public void setOccourrences(int occourrences) {
		this.occourrences = occourrences;
	}
	
	
	
}
