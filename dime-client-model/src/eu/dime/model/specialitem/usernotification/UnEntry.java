/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.specialitem.usernotification;

import eu.dime.model.JSONItem;

/**
 *
 * @author simon
 */
public abstract class UnEntry extends JSONItem<UnEntry> {
    public abstract UN_TYPE getUnType();
}

