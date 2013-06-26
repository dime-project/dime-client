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
