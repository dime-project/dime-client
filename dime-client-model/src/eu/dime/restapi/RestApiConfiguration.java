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
 *  Description of RestApiConfiguration
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 02.07.2012
 */
package eu.dime.restapi;

/**
 * RestApiConfiguration
 * 
 */
public class RestApiConfiguration {
	
    public final String hostName;
    public final int port;
    public final boolean isHttps;
    public final String authToken;

    public RestApiConfiguration(String hostName, int port, boolean isHttps, String authToken) {
        this.hostName = hostName;
        this.port = port;
        this.isHttps = isHttps;
        this.authToken = authToken;
    }
  
}
