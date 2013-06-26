/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model;

import eu.dime.control.LoadingViewHandler;

/**
 *
 * @author simon
 */
public class ModelRequestContext {
	
    public final String hoster;
    public final String owner;
    public final LoadingViewHandler lvHandler;

    public ModelRequestContext(String hoster, String owner, LoadingViewHandler lvHandler) {
        this.hoster = hoster;
        this.owner = owner;
        this.lvHandler = lvHandler;
    }

    @Override
    public String toString() {
        return "hoster: "+hoster+"\nowner: "+owner+"\nlvHandler: "+(lvHandler!=null)+"\n";
    }

}
