/*
 *  Description of StaticTestData
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 02.05.2012
 */
package eu.dime.model;

/**
 *
 * @author Simon Thiel
 */
public class StaticTestData {
  /**
     * fix set of saids and user ids for setting up a dummy service
     * @see https://dev.deri.ie/confluence/display/digitalme/Event+Validation+-+Testing+Framework
     */
    public static final String DEFAULT_MAIN_SAID = "ana02"; //HOSTER
    public static final String DEFAULT_PASSWORD = "dimePassForUser";
    public static final String[] SIMPLE_PS_SAIDS = {"max01", "ana02", "tom03"};
    public static final String[] SHARETEST_NICKNAMES = {"sharetesta", "sharetestb"};
    public static final String[] SHARETEST_FIRSTNAMES = {"Andreas", "Bastian"};
    public static final String[] SHARETEST_LASTNAMES = {"Sharetest", "Sharetest"};
    
    public static final String JUAN_USERNAME = DEFAULT_MAIN_SAID;  //in segovia it was defined to have username == main_said
    public static final String JUAN_PASSWORD = "dimepass4owner"; 
}
