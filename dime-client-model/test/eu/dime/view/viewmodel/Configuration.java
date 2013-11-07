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

/**
 *
 * @author simon
 */

public class Configuration {
    
    
    private String presetName = "undefined";
    private String hostname;
    private int port;
    private boolean useHTTPS;
    private String mainSAID;
    private String username;
    private String password;
    private boolean persistence;
    private boolean accessRemoteRestAPI;
    private boolean fetchNotifications;

    public Configuration() {
    }

    public Configuration(String presetName, ModelConfiguration configuration) {
        updateModelConfiguration(presetName, configuration);
    }

    public final void updateModelConfiguration(String presetName, ModelConfiguration configuration) {
        this.presetName = presetName;
        this.hostname = configuration.hostname;
        this.port = configuration.port;
        this.useHTTPS = configuration.useHTTPS;
        this.mainSAID = configuration.mainSAID;
        this.username = configuration.username;
        this.password = configuration.password;
        this.persistence = configuration.persistence;
        this.accessRemoteRestAPI = configuration.accessRemoteRestAPI;
        this.fetchNotifications = configuration.fetchNotifications;
    }

    public ModelConfiguration produceModelConfiguration() {
        return new ModelConfiguration(hostname, port, useHTTPS, mainSAID, username, password, persistence, accessRemoteRestAPI, fetchNotifications);
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the useHTTPS
     */
    public boolean isUseHTTPS() {
        return useHTTPS;
    }

    /**
     * @return the mainSAID
     */
    public String getMainSAID() {
        return mainSAID;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the persistence
     */
    public boolean isPersistence() {
        return persistence;
    }

    /**
     * @return the accessRemoteRestAPI
     */
    public boolean isAccessRemoteRestAPI() {
        return accessRemoteRestAPI;
    }

    /**
     * @return the fetchNotifications
     */
    public boolean isFetchNotifications() {
        return fetchNotifications;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @param useHTTPS the useHTTPS to set
     */
    public void setUseHTTPS(boolean useHTTPS) {
        this.useHTTPS = useHTTPS;
    }

    /**
     * @param mainSAID the mainSAID to set
     */
    public void setMainSAID(String mainSAID) {
        this.mainSAID = mainSAID;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param persistence the persistence to set
     */
    public void setPersistence(boolean persistence) {
        this.persistence = persistence;
    }

    /**
     * @param accessRemoteRestAPI the accessRemoteRestAPI to set
     */
    public void setAccessRemoteRestAPI(boolean accessRemoteRestAPI) {
        this.accessRemoteRestAPI = accessRemoteRestAPI;
    }

    /**
     * @param fetchNotifications the fetchNotifications to set
     */
    public void setFetchNotifications(boolean fetchNotifications) {
        this.fetchNotifications = fetchNotifications;
    }

    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param hostname the hostname to set
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @return the presetName
     */
    public String getPresetName() {
        return presetName;
    }

    /**
     * @param presetName the presetName to set
     */
    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    @Override
    public String toString() {
        return presetName;
    }
    
    
    
}
