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

public abstract class ResultObject {
	
	public enum RESULT_OBJECT_TYPES {
		SHARING_PROFILE, SHARING_PERSONS, SHARING_GROUPS, SHARING_DATABOXES, SHARING_RESOURCES, SHARING_LIVEPOSTS, 
		SERVICE_CONNECTION, ADD_RESOURCES_TO_DATABOX, ASSIGN_RESOURCES_TO_DATABOX, ADD_PEOPLE_TO_GROUP, ASSIGN_PEOPLE_TO_GROUP;
	};
	
	protected RESULT_OBJECT_TYPES type;
	
	public RESULT_OBJECT_TYPES getType() {
		return type;
	}
	
}
