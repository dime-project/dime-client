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

/*
 *  Description of ShareableItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 30.10.2012
 */
package eu.dime.model.displayable;

import eu.dime.model.GenItemI;

/**
 * interface for shareable items
 * @author Simon Thiel
 */
public interface ShareableItem extends GenItemI {
	
	public static final double STANDARD_PRIVACY_LEVEL = 0.5;
	
	public Double getPrivacyLevel();
	public void setPrivacyLevel(Double privacyLevel);
	
}
