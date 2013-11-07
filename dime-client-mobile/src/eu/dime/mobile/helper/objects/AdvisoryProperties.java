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