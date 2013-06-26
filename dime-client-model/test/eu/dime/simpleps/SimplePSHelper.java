/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.simpleps;

import eu.dime.control.LoadingViewHandler;
import eu.dime.model.CreateItemFailedException;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.ModelTypeNotFoundException;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ProfileAttributeCategoriesEntry;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.view.csvimport.CSVParser;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simon
 */
public class SimplePSHelper {
    public static final String MY__PUBLIC__PROFILE = "My Public Profile";

    private static Random rnd = new Random();

    public static TYPES guessType(String typeString) {
        if (typeString == null) {
            return null;
        }
        try {
            //first check for canonical type string

            return ModelHelper.getMTypeFromString(typeString);
        } catch (ModelTypeNotFoundException ex) {
            //didn't work
        }
        //check TYPES.toString()
        try {
            TYPES type = TYPES.valueOf(typeString);
        } catch (IllegalArgumentException ex) {
            // didn't work either
        }
        //last try
        for (TYPES type : TYPES.values()) {
            if (typeString.equalsIgnoreCase(type.toString())) {
                return type;
            }
        }
        //give up
        return null;

    }

    private static ProfileAttributeItem createNameProfileAttribute(String owner, String name) {

        ProfileAttributeItem result = ItemFactory.cretateNewProfileAttributeItem(ProfileAttributeItem.VALUE_CATEGORIES.PERSON_NAME);

        result.setUserId(owner);

        ProfileAttributeCategoriesEntry categoryEntry = result.getProfileCategoryEntry();
        int nameGiven = 4;
        int familyName = 1;

        //we expect name consisting of "familyName nameGiven" //FIXME
        String[] nameSplit = name.split(" ");

        result.getValue().put(categoryEntry.keys[nameGiven], nameSplit[0]); //at least we should have a given name

        if (nameSplit.length > 1) {
            result.getValue().put(categoryEntry.keys[familyName], nameSplit[nameSplit.length - 1]); //take the last part as family name
        }

        return result;
    }

    private static ProfileItem createPublicProfile(String owner, String said) {
        ProfileItem result = (ProfileItem) ItemFactory.createNewItemByType(TYPES.PROFILE);

        result.setName(MY__PUBLIC__PROFILE);
        result.setUserId(owner);
        result.setServiceAccountId(said);
        return result;
    }

    public static ProfileItem createDefaultProfileForPerson(ModelRequestContext mrc, String personName, String said) throws CreateItemFailedException {
        ProfileAttributeItem nameAttrItem = createNameProfileAttribute(mrc.owner, personName);
        nameAttrItem = (ProfileAttributeItem) Model.getInstance().createItem(mrc, nameAttrItem);

        ProfileItem publicProfile = createPublicProfile(mrc.owner, said);
        publicProfile.addItem(nameAttrItem.getGuid());

        publicProfile = (ProfileItem) Model.getInstance().createItem(mrc, publicProfile);

        return publicProfile;
    }

    private static String generateUserId(String personName) {
        if (personName==null){
            personName="John Doe";
        }
        if (personName.length() < 3) {
            Logger.getLogger(CSVParser.DimeDataLineProcessor.class.getName()).log(Level.WARNING,
                    "personname is very short: " + personName
                    + " return UUID as userID");
            return UUID.randomUUID().getMostSignificantBits() + "";
        }

        String result = personName;
        result = result.replaceAll(" ", "").trim();
        result = result.substring(result.length() - 3, result.length() - 1)
                + result.substring(0, ((result.length() > 6) ? 7 : result.length()));

        result += rnd.nextInt(1000);
        return result;
    }

    public static String createAccountForPerson(String personName) {

        String remoteUserId = generateUserId(personName);
        //TODO -generating simply a new userID only works with the simple PS - for 
        // dime-PS it would be necessary to register a new account and create an entry
        // at the DNS !!!! 



        return remoteUserId;
    }

    public static ProfileItem createContactEntryAndPublicProfileForRemotePerson(LoadingViewHandler lvh, String localName, String localSaid, String otherPersonsUserId) throws CreateItemFailedException {

        ModelRequestContext remoteMrc = new ModelRequestContext(otherPersonsUserId,
                Model.ME_OWNER, lvh);

    
        //create entry for local person at the remote account
        PersonItem localPersonAtRemote = (PersonItem) ItemFactory.createNewItemByType(TYPES.PERSON);
        localPersonAtRemote.setUserId(Model.ME_OWNER);
        localPersonAtRemote.setName(localName);
        localPersonAtRemote = (PersonItem) Model.getInstance().createItem(remoteMrc, localPersonAtRemote);

        //create profile and profile attribute for local person at the remote account
        ModelRequestContext remoteLocalMrc =
                new ModelRequestContext(otherPersonsUserId,
                localPersonAtRemote.getGuid(), lvh);

        return createDefaultProfileForPerson(remoteLocalMrc, localName, localSaid);      
    }
    
    public static ProfileItem getAndCreateDefaultProfile(ModelRequestContext mrc) throws CreateItemFailedException{
        DisplayableItem defaultProfile
                = ModelHelper.getDisplayableByName(mrc, SimplePSHelper.MY__PUBLIC__PROFILE, TYPES.PROFILE);
        
        if (defaultProfile==null){
            defaultProfile = createDefaultProfileForPerson(mrc, mrc.hoster, UUID.randomUUID().toString());
        }
        return (ProfileItem) defaultProfile;
    }
}
