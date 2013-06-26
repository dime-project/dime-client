/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.restapi;

import eu.dime.model.StaticTestData;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simon
 */
public class DNSLookupTest {
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Logger.getLogger(DNSLookupTest.class.getName()).log(Level.INFO, DimeHelper.resolveIPOfPS("ps43"));
            Logger.getLogger(DNSLookupTest.class.getName()).log(Level.INFO, DimeHelper.resolveIPOfPS(StaticTestData.DEFAULT_MAIN_SAID));
        } catch (UnknownHostException ex) {
            Logger.getLogger(DNSLookupTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
