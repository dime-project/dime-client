package eu.dime.mobile.helper.objects;

public class StandardDialogProperties {
	
	private String label;
	private String infoText;
	private int drawableId;

	public StandardDialogProperties() { }

	public StandardDialogProperties(String label, String infoText, int drawableId) {
		this.label = label;
		this.infoText = infoText;
		this.drawableId = drawableId;
	}
	
	public String getlabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getInfoText() {
		return infoText;
	}

	public void setInfoText(String infoText) {
		this.infoText = infoText;
	}

	public int getDrawableId() {
		return drawableId;
	}

	public void setDrawableId(int drawableId) {
		this.drawableId = drawableId;
	}

}