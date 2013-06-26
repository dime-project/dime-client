/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.view.helper;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Toolkit;

/**
 *
 * @author simon
 */
public class UIHelper {
    public static void centerWindow(java.awt.Window window){
         //center to screen
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int left = (d.width - window.getWidth()) / 2;
        int top = (d.height - window.getHeight()) / 2;
        window.setLocation(left, top);
    }
    
    public static void moveWindowToMousePosition(java.awt.Window window){
        Point pos = getMousePosition().getLocation();
        window.setLocation(pos);
    }
    
    
    public static PointerInfo getMousePosition(){
        return MouseInfo.getPointerInfo();
    }
}
