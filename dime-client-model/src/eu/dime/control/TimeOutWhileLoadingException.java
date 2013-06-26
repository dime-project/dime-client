/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.control;

/**
 *
 * @author simon
 */
public class TimeOutWhileLoadingException extends Exception{

    public TimeOutWhileLoadingException() {
        super("Timeout occured while wait for loading");
    }
    
}
