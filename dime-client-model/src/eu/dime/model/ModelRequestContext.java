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
