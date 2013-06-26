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
public interface LoadingHandler {
    public void notifyLoadingDone();

    public void notifyItemCreated(GenItem item, String oldGuid);

    public void notifyLoadingTimeOut();
}
