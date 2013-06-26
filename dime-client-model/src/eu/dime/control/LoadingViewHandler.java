/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.control;

import eu.dime.model.GenItem;

/**
 *
 * @author simon
 */
public interface LoadingViewHandler extends LoadingHandler {
    
    
    public void showLoadingView();

    public GenItem showCreateLoadingView(String oldGUID);

    public boolean doContinueAfterTimeOut();
    
    
}
