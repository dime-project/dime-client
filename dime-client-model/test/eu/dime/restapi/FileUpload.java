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
