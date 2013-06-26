/*
 *  Description of ClientModelTest
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 06.06.2012
 */
package eu.dime.model;

import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.storage.InitStorageFailedException;
import eu.dime.restapi.DimeHelper;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ClientModelTest
 *
 */
public class ClientModelTest {

    public void startTest(String ip, int port, boolean useHttps, String hoster, String password) {

        Random rnd = new Random();
        ModelRequestContext mrContext = new ModelRequestContext(hoster, Model.ME_OWNER, new DummyLoadingViewHandler());

        try {
            Model.getInstance().updateSettings(new ModelConfiguration( ip, port, useHttps, hoster, hoster, password, false, true, false));

//            if (!(new DimeHelper()).dimeServerIsAlive(hoster)) {
//                System.out.println("service is offline");
//                System.exit(-1);
//            }
            
            ModelHelper.getAllLivePosts(mrContext); //HACK call twice to work arround async request issue
            try {
                //give the model thread some time to instatiate etc...
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.
                        getMessage(), ex);
            }

            List<LivePostItem> livePosts = ModelHelper.getAllLivePosts(mrContext);

            StringBuilder outStr = new StringBuilder();
            for (LivePostItem item : livePosts) {
                outStr.append("------------------------------------\n");
                outStr.append(item.toString()).append("\n");

            }
            Logger.getLogger(ClientModelTest.class.getName()).log(Level.INFO, outStr.
                    toString());

            if (livePosts.isEmpty()) {
                return;
            }
            LivePostItem randomLivePost = livePosts.get(rnd.nextInt(livePosts.
                    size()));

            List<PersonItem> persons =
                    ModelHelper.getAllPersons(mrContext);
            PersonItem randomPerson = persons.get(rnd.nextInt(persons.size()));
            ProfileItem profileOfPerson = ModelHelper.getAllProfiles(mrContext).get(0);
            try {
                ModelHelper.shareItemToAgent(mrContext, randomLivePost, randomPerson, profileOfPerson);
            } catch (SharingNotSupportedForSAIDException ex) {
                Logger.getLogger(ClientModelTest.class.getName()).log(Level.SEVERE, null, ex);
            }

            Logger.getLogger(ClientModelTest.class.getName()).log(Level.INFO, "shared "
                    + ModelHelper.getItemsDirectlySharedToAgent(mrContext, randomPerson.
                    getGuid(), TYPES.LIVEPOST).size() + " items");
            
            Logger.getLogger(ClientModelTest.class.getName()).log(Level.INFO, "shared to ...");
            for (String agentGuid: Model.getInstance().getItem(mrContext, TYPES.LIVEPOST, randomLivePost.getGuid()).getAccessingAgents().getAgentGuids()){
                Logger.getLogger(ClientModelTest.class.getName()).log(Level.INFO, " --> "+agentGuid);
            }
                    

            Logger.getLogger(ClientModelTest.class.getName()).log(Level.INFO, "shareItemTo done.");

        } catch (InitStorageFailedException ex) {
            Logger.getLogger(ClientModelTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //give the model thread some time to instatiate etc...
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.
                    getMessage(), ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //new ClientModelTest().startTest("10.36.40.7", StaticTestData.JUAN_MAIN_SAID);
        //new ClientModelTest().startTest("137.251.22.69", 8443, true, "123");
        new ClientModelTest().startTest(DimeHelper.DEFAULT_HOSTNAME,  DimeHelper.DEFAULT_PORT, false, StaticTestData.DEFAULT_MAIN_SAID, StaticTestData.JUAN_PASSWORD);
        Model.getInstance().shutdownStorage();
        System.exit(0);
    }
}
