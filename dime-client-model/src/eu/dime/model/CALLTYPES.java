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
 *  Description of CALLTYPES
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 30.05.2012
 */
package eu.dime.model;

/**
 * CALLTYPES
 * 
 */
public enum CALLTYPES {
    AT_ALL_GET, AT_ITEM_GET, AT_ITEM_POST_NEW, AT_ITEM_POST_UPDATE, AT_ITEM_DELETE, 
    COMET, DUMP, MERGE, AUTH_GET, AUTH_POST, CRAWLER, USER_ALL_GET, REGISTER_USER, AT_GLOBAL_ALL_GET
}
