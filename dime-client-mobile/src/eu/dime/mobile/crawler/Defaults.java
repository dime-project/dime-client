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

package eu.dime.mobile.crawler;

import java.util.HashMap;
import java.util.Map;

import eu.dime.model.context.constants.Scopes;

public class Defaults {
	
	public static final int DEFAULT_BT_SCAN		= 60;  // seconds 
	public static final int DEFAULT_WF_SCAN		= 60;  // seconds
	//public static final int DEFAULT_POS_SCAN	= Constants.POS_ON_REQUEST;  // seconds
	public static final int DEFAULT_POS_SCAN	= 1800;  // seconds
	public static final int DEFAULT_STATUS_SCAN	= 180;  // seconds
	
	public static final int DEFAULT_BT_UPDATE		= DEFAULT_BT_SCAN;  // seconds 
	public static final int DEFAULT_WF_UPDATE		= 150;  // seconds 
	public static final int DEFAULT_POS_UPDATE		= 180;  // seconds 
	public static final int DEFAULT_STATUS_UPDATE	= DEFAULT_STATUS_SCAN;  // seconds 
	
	
	public static final int DEFAULT_VALIDITY	= 600;  // seconds 
	
	public static Map<String,Integer> VALIDITY = new HashMap<String,Integer>();
	static {
		VALIDITY.put(Scopes.SCOPE_BT,Integer.valueOf((int)1.25*DEFAULT_BT_UPDATE));
		VALIDITY.put(Scopes.SCOPE_WF,Integer.valueOf((int)1.25*DEFAULT_WF_UPDATE));
		VALIDITY.put(Scopes.SCOPE_POSITION,Integer.valueOf((int)1.25*DEFAULT_POS_UPDATE));
	}

}
