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

import java.util.Enumeration;
import java.util.Vector;
import javax.xml.bind.annotation.XmlRootElement;
import sit.tools.SettingsRoot;

/**
 *
 * @author simon
 */

@XmlRootElement( namespace = "http://morganasoft.de/" )
public class Settings extends SettingsRoot{
    private Vector<Configuration> configurations = new Vector();
    private Configuration currentConfiguration = null;

    /**
     * @return the configurations
     */
    public Vector<Configuration> getConfigurations() {
        return configurations;
    }


    /**
     * @param configurations the configurations to set
     */
    public void setConfigurations(Vector<Configuration> configurations) {
        this.configurations = configurations;
    }

    
    public void addConfiguration(Configuration c){
        this.configurations.add(c);
        
    }

    /**
     * @return the currentConfiguration
     */
    public Configuration getCurrentConfiguration() {
        return currentConfiguration;
    }

    /**
     * @param currentConfiguration the currentConfiguration to set
     */
    public void setCurrentConfiguration(Configuration currentConfiguration) {
        this.currentConfiguration = currentConfiguration;
    }

    public void setConfigurations(Enumeration<Configuration> elements) {
        configurations.clear();
        while (elements.hasMoreElements()){
            configurations.add(elements.nextElement());
        }
    }
}
