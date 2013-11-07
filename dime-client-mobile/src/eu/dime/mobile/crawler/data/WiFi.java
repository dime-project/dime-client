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
