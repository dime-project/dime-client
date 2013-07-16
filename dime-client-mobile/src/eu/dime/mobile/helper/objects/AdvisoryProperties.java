package eu.dime.mobile.helper.objects;

public class AdvisoryProperties {
	
	private String headline;
	private String advisoryText;
	private int drawableId;

	public AdvisoryProperties() { }

	public AdvisoryProperties(String headline, String advisoryText, int drawableId) {
		this.headline = headline;
		this.advisoryText = advisoryText;
		this.drawableId = drawableId;
	}
	
	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getAdvisoryText() {
		return advisoryText;
	}

	public void setAdvisoryText(String notificationText) {
		this.advisoryText = notificationText;
	}

	public int getDrawableId() {
		return drawableId;
	}

	public void setDrawableId(int drawableId) {
		this.drawableId = drawableId;
	}

}