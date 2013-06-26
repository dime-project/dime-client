/*
 *  Description of WrongPasswordException
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.07.2012
 */
package eu.dime.model.auth;

/**
 * WrongPasswordException
 * 
 */
public class WrongPasswordException extends AuthException {

    public WrongPasswordException(String string) {
        super(string);
    }

}
