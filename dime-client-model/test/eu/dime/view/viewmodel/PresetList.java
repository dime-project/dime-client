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
package eu.dime.view.viewmodel;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.swing.DefaultListModel;

/**
 *
 * @author simon
 */
public class PresetList extends DefaultListModel<Configuration> implements Iterable<Configuration> {

    public void addOrReplaceElement(Configuration configuration) {

        for (Configuration myConfiguration : this) {

            if (myConfiguration.getPresetName().equals(configuration.getPresetName())) {
                this.removeElement(myConfiguration);
                break;
            }
        }
        this.addElement(configuration);
    }

    public Iterator<Configuration> iterator() {

        return new Iterator<Configuration>() {

            private int iteratorIndex = 0;

            public boolean hasNext() {
                return this.iteratorIndex < PresetList.this.getSize();
            }

            public Configuration next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return PresetList.this.get(iteratorIndex++);
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
        
    }
}
