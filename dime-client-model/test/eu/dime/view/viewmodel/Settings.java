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
