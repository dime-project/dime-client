/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.view.viewmodel;

import eu.dime.control.AbstractLoadingViewHandler;
import eu.dime.control.TimeOutWhileLoadingException;
import eu.dime.model.GenItem;
import eu.dime.view.ModelViewer;
import eu.dime.view.WaitMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author simon
 */
public class JLoadingViewHandler extends AbstractLoadingViewHandler {

    private boolean continueAfterTimeOut = true;

    abstract class WaitDelegate {

        abstract GenItem process();
    }

    class WaitThread implements Runnable {

        private final WaitMessage wm;
        private final WaitDelegate wd;
        private GenItem result = null;

        public WaitThread(WaitMessage wm, WaitDelegate wd) {
            this.wm = wm;
            this.wd = wd;
        }

        public void run() {
            result = wd.process();
            //make sure waitMessage was set Visible
            while (!wm.isVisible()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ModelViewer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {//HACK handle some concurrency issues?!?!?
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(ModelViewer.class.getName()).log(Level.SEVERE, null, ex);
            }
            wm.setVisible(false);
        }

        GenItem getResult() {
            return result;
        }
    }
    private final WaitMessage wm;

    public JLoadingViewHandler(WaitMessage wm) {
        this.wm = wm;
    }

    @Override
    protected void handleTimeOutNotified() {
        Logger.getLogger(JLoadingViewHandler.class.getName()).log(Level.SEVERE, "experienced timeout when accessing");
        int response = JOptionPane.showConfirmDialog(this.wm, "Timeout when tying to access the model!\n\nTry again?",
                "Network Connection Timeout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        this.continueAfterTimeOut = (response == JOptionPane.YES_OPTION);

    }

    public void showLoadingView() {
        this.continueAfterTimeOut = true;

        (new Thread(new WaitThread(wm, new WaitDelegate() {

            @Override
            GenItem process() {
                
                try {
                    waitForLoading();

                } catch (TimeOutWhileLoadingException ex) {
                    //catch silently and just return
                    //Logger.getLogger(JLoadingViewHandler.class.getName()).log(Level.SEVERE, null, ex);
                }

                return null;

            }
        }))).start();
        wm.setVisible(true);

    }

    public GenItem showCreateLoadingView(final String oldGUID) {
        this.continueAfterTimeOut = true;
        
        WaitThread wt = new WaitThread(wm, new WaitDelegate() {

            @Override
            GenItem process() {
                return waitForCreateItem(oldGUID);
            }
        });

        (new Thread(wt)).start();
        wm.setVisible(true);

        return wt.getResult();
    }

    public boolean doContinueAfterTimeOut() {
        return continueAfterTimeOut;
    }
}