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
 *  Description of StaticTestData
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 02.05.2012
 */
package eu.dime.model;

/**
 *
 * @author Simon Thiel
 */
public class StaticTestData {
  /**
     * fix set of saids and user ids for setting up a dummy service
     * @see https://dev.deri.ie/confluence/display/digitalme/Event+Validation+-+Testing+Framework
     */
    public static final String DEFAULT_MAIN_SAID = "ana02"; //HOSTER
    public static final String DEFAULT_PASSWORD = "dimePassForUser";
    public static final String[] SIMPLE_PS_SAIDS = {"max01", "ana02", "tom03"};
    
    public static final String JUAN_USERNAME = DEFAULT_MAIN_SAID;  //in segovia it was defined to have username == main_said
    public static final String JUAN_PASSWORD = "dimepass4owner"; 
    
}
