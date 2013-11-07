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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.view.viewmodel;

import eu.dime.model.ModelConfiguration;
import eu.dime.model.StaticTestData;
import eu.dime.restapi.DimeHelper;
import sit.tools.SettingsFactory;

/**
 *
 * @author simon
 */
public class ModelConfigurationHelper {
    
    public static Settings getStoredSettings() {
        Settings result;
        
        result = (Settings) (new SettingsFactory<Settings>()).loadSettings(Settings.class);
        
        if ((result != null) && (result.getCurrentConfiguration()!=null)) {
            return result;
        }//else
        //unable to restore settings
        result = new Settings();
        result.setCurrentConfiguration(
                new Configuration("default", new ModelConfiguration(
                DimeHelper.DEFAULT_HOSTNAME,
                DimeHelper.DEFAULT_PORT,
                DimeHelper.DEFAULT_USE_HTTPS,
                StaticTestData.DEFAULT_MAIN_SAID,
                StaticTestData.JUAN_USERNAME,
                StaticTestData.JUAN_PASSWORD,
                false,
                true,
                true)));
        
        return result;
    }
    
    public static void saveConfiguration(Settings settings) {        
        new SettingsFactory<Settings>().saveSettings(settings);
    }
}
