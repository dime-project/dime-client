/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.restapi;

import eu.dime.model.Model;
import eu.dime.model.ModelConfiguration;
import eu.dime.model.StaticTestData;
import eu.dime.model.storage.InitStorageFailedException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simon
 */
public class FileUpload {

    public void startTest(String filename) {
        try {
            Model.getInstance().updateSettings(new ModelConfiguration(DimeHelper.DEFAULT_HOSTNAME, 8080, false,
                    StaticTestData.DEFAULT_MAIN_SAID, StaticTestData.JUAN_USERNAME, StaticTestData.JUAN_PASSWORD, false, true, false));

            File file = new File(filename);
            if (!file.exists()){
                Logger.getLogger(FileUpload.class.getName()).log(Level.SEVERE, "file not found:"+filename);
                return;
            }

            MultiPartPostClient myClient = new MultiPartPostClient(Model.getInstance().getSettings());
            
            myClient.uploadFile(file);
        } catch (IOException ex) {
            Logger.getLogger(FileUpload.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InitStorageFailedException ex) {
            Logger.getLogger(FileUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        new FileUpload().startTest("resources/www/dime-communications/static/ui/dime/img/logo.png");




    }
}
