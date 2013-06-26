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
