/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

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
