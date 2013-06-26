/*
 *  Description of ModelConfiguration
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 11.04.2012
 */
package eu.dime.model;

import eu.dime.restapi.RestApiConfiguration;
import sit.web.client.HttpHelper;

/**
 *
 * @author Simon Thiel
 */
public class ModelConfiguration {

    public final String hostname;
    public final int port;
    public final boolean useHTTPS;
    public final String mainSAID;
    public final String username;
    public final String password;
    public final boolean persistence;
    public final boolean accessRemoteRestAPI;    
    public final boolean fetchNotifications;
    public final String token;
    public final RestApiConfiguration restApiConfiguration;

    public ModelConfiguration(String hostname, int port, boolean useHTTPS, 
            String mainSAID, String username, String password, boolean persistence, 
            boolean accessRemoteRestAPI, boolean fetchNotifications) {
        
        this.hostname = hostname;
        this.port = port;
        this.useHTTPS = useHTTPS;
        this.mainSAID = mainSAID;
        this.username = username;
        this.password = password;
        this.persistence = persistence;
        this.accessRemoteRestAPI = accessRemoteRestAPI;
        this.fetchNotifications = fetchNotifications;
        this.token = HttpHelper.getBase64UserNamePwdToken(username, password);
        this.restApiConfiguration = new RestApiConfiguration(hostname, port, useHTTPS, token);
    }

    @Override
    public String toString() {
        return "persistence: " + persistence
                + "\naccessRemoteRestAPI: " + accessRemoteRestAPI 
                + "\nip: " + hostname 
                + "\nport: " + port 
                + "\nhttps: " + useHTTPS
                + "\nfetchNotifications: "+fetchNotifications;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModelConfiguration other = (ModelConfiguration) obj;
        if ((this.hostname == null) ? (other.hostname != null) : !this.hostname.equals(other.hostname)) {
            return false;
        }
        if (this.port != other.port) {
            return false;
        }
        if (this.useHTTPS != other.useHTTPS) {
            return false;
        }
        if ((this.mainSAID == null) ? (other.mainSAID != null) : !this.mainSAID.equals(other.mainSAID)) {
            return false;
        }
        if ((this.username == null) ? (other.username != null) : !this.username.equals(other.username)) {
            return false;
        }
        if ((this.password == null) ? (other.password != null) : !this.password.equals(other.password)) {
            return false;
        }
        if (this.persistence != other.persistence) {
            return false;
        }
        if (this.accessRemoteRestAPI != other.accessRemoteRestAPI) {
            return false;
        }
        if (this.fetchNotifications != other.fetchNotifications) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.hostname != null ? this.hostname.hashCode() : 0);
        hash = 83 * hash + this.port;
        hash = 83 * hash + (this.useHTTPS ? 1 : 0);
        hash = 83 * hash + (this.mainSAID != null ? this.mainSAID.hashCode() : 0);
        hash = 83 * hash + (this.username != null ? this.username.hashCode() : 0);
        hash = 83 * hash + (this.password != null ? this.password.hashCode() : 0);
        hash = 83 * hash + (this.persistence ? 1 : 0);
        hash = 83 * hash + (this.accessRemoteRestAPI ? 1 : 0);
        hash = 83 * hash + (this.fetchNotifications ? 1 : 0);
        return hash;
    }

    RestApiConfiguration getRestApiConfiguration() {
        return restApiConfiguration;
    }

}
