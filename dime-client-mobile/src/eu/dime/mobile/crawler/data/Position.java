package eu.dime.mobile.crawler.data;

public class Position {
	
	private Double latitude;
	private Double longitude;
	private Float accuracy;
	private String locMode;
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Float getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(Float accuracy) {
		this.accuracy = accuracy;
	}
	public String getLocMode() {
		return locMode;
	}
	public void setLocMode(String locMode) {
		this.locMode = locMode;
	}

}
