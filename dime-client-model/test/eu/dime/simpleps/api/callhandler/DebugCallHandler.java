/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.simpleps.api.callhandler;

import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.model.CreateItemFailedException;
import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.ItemGenerator;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.StaticTestData;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.model.specialitem.usernotification.UNEntryRefToItem;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;
import eu.dime.simpleps.database.DatabaseAccess;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.web.WebRequest;

/**
 *
 * @author simon
 */
public class DebugCallHandler extends CallHandler {

    private Random rnd = new Random();

    @Override
    public DIME_HANDLER_PARAMS[] getSignature() {
        // dime-communications/api/dime/rest/9702325/@dump
        return new DIME_HANDLER_PARAMS[]{
                    DIME_HANDLER_PARAMS.HOSTER,
                    DIME_HANDLER_PARAMS.DEBUG
                };
    }

    @Override
    public String handleCall(WebRequest wr, ParamsMap params) {
        Logger.getLogger(DebugCallHandler.class.getName()).log(Level.INFO, "debug called");
        trigerUserNotification();
        return "ok";
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    private void trigerUserNotification() {
        Logger.getLogger(DebugCallHandler.class.getName()).log(Level.INFO, "trigger creation of user notifications");
        try {
            ModelRequestContext mrc = new ModelRequestContext(StaticTestData.DEFAULT_MAIN_SAID, Model.ME_OWNER, new DummyLoadingViewHandler());
            int result = rnd.nextInt(4);
            switch (result) {
                case 0:
                    ItemGenerator ig = new ItemGenerator();
                    LivePostItem lpItem = (LivePostItem) ig.generateRandomItem(mrc, TYPES.LIVEPOST);
                    lpItem.setText("Hello World! \nNew message:\n" + System.currentTimeMillis());
                    lpItem = (LivePostItem) Model.getInstance().createItem(mrc, lpItem);
                    DatabaseAccess.sendNotification(mrc, NotificationItem.OPERATION_CREATE, lpItem);
                    UserNotificationItem uNot1 = ItemFactory.createRefItemUserNotification(lpItem, UNEntryRefToItem.OPERATION_SHARED);
                    uNot1 = (UserNotificationItem) Model.getInstance().createItem(mrc, uNot1);
                    DatabaseAccess.sendNotification(mrc, NotificationItem.OPERATION_CREATE, uNot1);
                    break;

                case 1:
                    List<GenItem> persons = ModelHelper.pickNRandom(Model.getInstance().getAllItems(mrc, TYPES.PERSON), 2);
                    UserNotificationItem uNot2 = ItemFactory.createMergeRecommendationUserNotification((PersonItem) persons.get(0), (PersonItem) persons.get(1));
                    uNot2 = (UserNotificationItem) Model.getInstance().createItem(mrc, uNot2);
                    DatabaseAccess.sendNotification(mrc, NotificationItem.OPERATION_CREATE, uNot2);
                    break;
                case 2:
                    PersonItem trustPerson = (PersonItem) ModelHelper.pickNRandom(Model.getInstance().getAllItems(mrc, TYPES.PERSON), 1).get(0);
                    String trustOp = rnd.nextBoolean()?UNEntryRefToItem.OPERATION_INC_TRUST:UNEntryRefToItem.OPERATION_DEC_TRUST;
                    UserNotificationItem uNot3 = ItemFactory.createRefItemUserNotification(trustPerson, trustOp);
                    uNot3 = (UserNotificationItem) Model.getInstance().createItem(mrc, uNot3);
                    DatabaseAccess.sendNotification(mrc, NotificationItem.OPERATION_CREATE, uNot3);
                    break;
                case 3:
                    ResourceItem privRes = (ResourceItem) ModelHelper.pickNRandom(Model.getInstance().getAllItems(mrc, TYPES.RESOURCE), 1).get(0);
                    String privOp = rnd.nextBoolean()?UNEntryRefToItem.OPERATION_INC_PRIV:UNEntryRefToItem.OPERATION_DEC_PRIV;
                    UserNotificationItem uNot4 = ItemFactory.createRefItemUserNotification(privRes, privOp);
                    uNot4 = (UserNotificationItem) Model.getInstance().createItem(mrc, uNot4);
                    DatabaseAccess.sendNotification(mrc, NotificationItem.OPERATION_CREATE, uNot4);
                    break;
            }
        } catch (CreateItemFailedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);

        }

    }
}
