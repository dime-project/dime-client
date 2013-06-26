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
