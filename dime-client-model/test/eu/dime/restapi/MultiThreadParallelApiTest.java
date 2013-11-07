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
 *  Description of MultiThreadParallelApiTest
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 09.04.2012
 */
package eu.dime.restapi;

import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.model.Model;
import eu.dime.model.ModelConfiguration;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.StaticTestData;
import eu.dime.model.TYPES;
import eu.dime.model.storage.InitStorageFailedException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Simon Thiel
 */
public class MultiThreadParallelApiTest {
    private final int START_THREADS_COUNT = 1000;
    
    private int threadCount = 0;
    
    public synchronized int incThreads(){
        return threadCount++;
    }
    
    public synchronized void decThreads(int threadId){
        threadCount--;
        Logger.getLogger(MultiThreadParallelApiTest.class.getName()).log(Level.INFO, "thread "+threadId+" done - "+threadCount + " left");
    }
    

    public void startTest() {
        try {
            Model.getInstance().updateSettings(new ModelConfiguration(DimeHelper.DEFAULT_HOSTNAME, DimeHelper.DEFAULT_PORT, false, 
                   StaticTestData.DEFAULT_MAIN_SAID, StaticTestData.JUAN_USERNAME, StaticTestData.JUAN_PASSWORD, false, true, false));
            final Random rnd = new Random();
            for (int i = 0; i < START_THREADS_COUNT; i++) {
                
                Thread myThread = new Thread(new Runnable() {
                    
                    final ModelRequestContext mrContext = new ModelRequestContext(
                            StaticTestData.DEFAULT_MAIN_SAID, Model.ME_OWNER, new DummyLoadingViewHandler());

                    int myId;
                    
                    public void run() {
                        myId = MultiThreadParallelApiTest.this.incThreads();
                        try {
                            int typeId;
                            synchronized (rnd) {
                                typeId = rnd.nextInt(TYPES.values().length);
                            }
                            TYPES type = TYPES.values()[typeId];
                            //Logger.getLogger(getClass().getName()).log(Level.INFO, "getAllItems for " + type);
                            Model.getInstance().getAllItems(mrContext, type);
                            //Logger.getLogger(getClass().getName()).log(Level.INFO, "getAllItems for " + type + "done");
                        } catch (Exception ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "", ex);
                        }
                        MultiThreadParallelApiTest.this.decThreads(myId);
                    }
                });
                myThread.start();
            }
        } catch (InitStorageFailedException ex) {
            Logger.getLogger(MultiThreadParallelApiTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(true){
            synchronized(this){
                if (threadCount<=0){
                    Logger.getLogger(MultiThreadParallelApiTest.class.getName()).log(Level.INFO, "test finished");
                    System.exit(0);
                }                
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(MultiThreadParallelApiTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        new MultiThreadParallelApiTest().startTest();




    }
}
