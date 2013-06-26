/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.control;

import eu.dime.model.storage.LoadListenerKey;

/**
 *
 * @author simon
 */
public interface CacheLoadedHandler {

    public void registerOnCacheLoad(LoadingHandler listener, LoadListenerKey listenerKey);
}
