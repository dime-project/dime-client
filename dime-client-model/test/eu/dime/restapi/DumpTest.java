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
 *  Description of DumpTest
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 08.06.2012
 */
package eu.dime.restapi;

import eu.dime.model.ClientModelTest;
import eu.dime.model.Model;
import eu.dime.model.ModelConfiguration;
import eu.dime.model.StaticTestData;
import eu.dime.model.storage.DimeHosterStorage;
import eu.dime.model.storage.InitStorageFailedException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DumpTest
 * 
 */
public class DumpTest {

    private static DimeHelper dimeHelper = new DimeHelper();
    public void startTest(String ip, int port, String hoster) {

        try {
            Model.getInstance().updateSettings(new ModelConfiguration(ip, port, false, hoster, StaticTestData.JUAN_USERNAME, StaticTestData.JUAN_PASSWORD, false, true, false));
			if (!dimeHelper.dimeServerIsAuthenticated(hoster, Model.getInstance().getRestApiConfiguration())) {
			    System.out.println("service is offline");
			    System.exit(-1);
			}
            DimeHosterStorage storage = RestApiAccess.getDump(hoster, Model.getInstance().getRestApiConfiguration());
        } catch (InitStorageFailedException ex) {
            Logger.getLogger(ClientModelTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new DumpTest().startTest(DimeHelper.DEFAULT_HOSTNAME, DimeHelper.DEFAULT_PORT, StaticTestData.DEFAULT_MAIN_SAID);
    }

 
}
