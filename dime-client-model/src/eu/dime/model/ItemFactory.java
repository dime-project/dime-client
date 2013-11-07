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
 *  Description of ItemFactory
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 27.04.2012
 */
package eu.dime.model;

import eu.dime.model.displayable.AccountItem;
import eu.dime.model.displayable.AgentItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileAttributeItem.VALUE_CATEGORIES;
import eu.dime.model.displayable.ShareableItem;
import eu.dime.model.specialitem.AccountSettingsItem;
import eu.dime.model.specialitem.AuthItem;
import eu.dime.model.specialitem.EvaluationItem;
import eu.dime.model.specialitem.SearchResultItem;
import eu.dime.model.specialitem.UserItem;
import eu.dime.model.specialitem.advisory.AdvisoryItem;
import eu.dime.model.specialitem.advisory.WarningAttributesObject;
import eu.dime.model.specialitem.usernotification.UNEntryMergeRecommendation;
import eu.dime.model.specialitem.usernotification.UNEntryRefToItem;
import eu.dime.model.specialitem.usernotification.UNTypeEntry;
import eu.dime.model.specialitem.usernotification.UN_TYPE;
import eu.dime.model.specialitem.usernotification.UnEntry;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public class ItemFactory {

    private ItemFactory() {
    }

    public static GenItem createNewItemByType(TYPES type) {
        try {
            @SuppressWarnings("unchecked")
            PSMapEntry<GenItem> typeEntry = Model.TypeMap.get(type);
            Class<GenItem> myClass = typeEntry.getTypeClass();
            GenItem result = (GenItem) myClass.newInstance(); //call empty constructor of type specific class
            result.setMType(type);
            result.setGuid(UUID.randomUUID().toString());
            ModelHelper.updateLastUpdatedIfDisplayable(result);
            return result;
        } catch (InstantiationException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static DisplayableItem createNewDisplayableItemByType(TYPES type, String name) {
        DisplayableItem result = (DisplayableItem) createNewItemByType(type);
        result.setUserId(Model.ME_OWNER);
        result.setName(name);
        result.setLastUpdated(System.currentTimeMillis());
        if(ModelHelper.isAgent(type)) {
        	((AgentItem) result).setTrustLevel(AgentItem.STANDARD_TRUST_LEVEL);
        } else if(ModelHelper.isShareable(type)) {
        	((ShareableItem) result).setPrivacyLevel(ShareableItem.STANDARD_PRIVACY_LEVEL);
        }
        return result;
    }

    public static GenItem createNewItemByType(String guid, TYPES type) {
        GenItem result = createNewItemByType(type);
        if (result != null) {
            result.setGuid(guid);
        }
        return result;
    }

    public static GenItem createNewItemByJSON(TYPES type, JSONObject jsonObject) throws InvalidJSONItemException {
        GenItem result = createNewItemByType(type);
        Model.TypeMap.get(type).castToClassType(result).readJSONObject(jsonObject);
        return result;
    }

    /**
     * creating a new item by the given JSON code. Overriding the guid by the
     * given one
     *
     * @param guid
     * @param type
     * @param jsonObject
     * @return
     * @throws InvalidJSONItemException
     * @throws ModelTypeNotFoundException
     */
    public static GenItem createNewItemByJSON(String guid, TYPES type, JSONObject jsonObject) throws InvalidJSONItemException {
        GenItem result = createNewItemByType(type);
        Model.TypeMap.get(type).castToClassType(result).readJSONObject(jsonObject, guid);
        result.setGuid(guid); //the guid has to be set after reading the JSONObject
        return result;
    }

    public static ProfileAttributeItem cretateNewProfileAttributeItem(VALUE_CATEGORIES category) {
        ProfileAttributeItem result = (ProfileAttributeItem) createNewItemByType(TYPES.PROFILEATTRIBUTE);
        result.updateCategoryRelatedFields(category);
        return result;
    }

    public static AccountItem createAccountItem(String name, String guid, List<AccountSettingsItem> settings, String imageUrl) {
        AccountItem result = createAccountItem(name, guid, imageUrl);
        result.setSettings(settings);
        return result;
    }

    public static AccountItem createAccountItem(String name, String guid, String imageUrl) {
        AccountItem result = (AccountItem) createNewDisplayableItemByType(TYPES.ACCOUNT, name);
        result.setActive(true);
        result.setServiceAdapterGUID(guid);
        result.setImageUrl(imageUrl);
        return result;
    }

    public static AuthItem createNewAuthItem(JSONObject jsonObject) {
        try {
            AuthItem result = new AuthItem();
            result.readJSONObject(jsonObject);
            return result;
        } catch (InvalidJSONItemException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static SearchResultItem createNewSearchResultItem(JSONObject jsonObject) {
        try {
            SearchResultItem result = new SearchResultItem();
            result.readJSONObject(jsonObject);
            return result;
        } catch (InvalidJSONItemException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static SearchResultItem createNewSearchResultItem(String nickname, String name, String surname, String said) {
	    SearchResultItem result = new SearchResultItem();
	    result.setNickname(nickname);
	    result.setName(name);
	    result.setSurname(surname);
	    result.setSaid(said);
	    return result;
    }

    public static EvaluationItem createNewEvaluationItem(String version, String hashedTenantId, String currentPlace, String currentSituation, List<String> viewStack) {
        EvaluationItem result = (EvaluationItem) createNewItemByType(TYPES.EVALUATION);
        result.setClientId(1 + "." + version);
        result.setTenantId(hashedTenantId);
        result.setCurrentPlace(currentPlace);
        result.setCurrentSituationId(currentSituation);
        result.setViewStack(viewStack);
        return result;
    }
    
    public static UserItem createNewUserItem(String username, String password, String nickname, String firstname, String lastname, boolean checkbox_agreed, String emailAddress) {
    	UserItem userItem = new UserItem();
    	userItem.setUsername(username);
    	userItem.setPassword(password);
    	userItem.setNickname(nickname);
    	userItem.setFirstname(firstname);
    	userItem.setLastname(lastname);
    	userItem.setCheckbox_agreed(checkbox_agreed);
    	userItem.setEmailAddress(emailAddress);
    	return userItem;
    }

    public static AdvisoryItem createNewAdvisoryItem(JSONObject jsonObject) {
        try {
            AdvisoryItem result = new AdvisoryItem();
            result.readJSONObject(jsonObject);
            return result;
        } catch (InvalidJSONItemException ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static AdvisoryItem createNewAdvisoryItem(double warningLevel, String warningType) {
        WarningAttributesObject warningObject = AdvisoryItem.getNewAttribObjByType(warningType);
        AdvisoryItem result = new AdvisoryItem(warningLevel, warningType, warningObject);
        return result;
    }

    @SuppressWarnings("unchecked")
	public static UserNotificationItem createUserNotification(UN_TYPE unType) {
        UserNotificationItem un = (UserNotificationItem) createNewItemByType(TYPES.USERNOTIFICATION);
        un.setName("Notification");
        un.setUserId("@me");
        try {
            UNTypeEntry<UnEntry> unTypeEntry = UserNotificationItem.UNTypesMap.get(unType);
            UnEntry unEntry = (UnEntry) unTypeEntry.unClass.newInstance();
            un.setUnEntry(unEntry);
        } catch (Exception ex) {
            Logger.getLogger(ItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return un;
    }
    
    public static UserNotificationItem createRefItemUserNotification(DisplayableItem item, String operation){
        UserNotificationItem result = createUserNotification(UN_TYPE.REF_TO_ITEM);
        UNEntryRefToItem entry = (UNEntryRefToItem) result.getUnEntry();
        entry.setGuid(item.getGuid());
        entry.setOperation(operation);
        entry.setType(item.getMType());
        entry.setUserId(item.getUserId());
        entry.setName(item.getName());
        return result;
    }
    
    public static UserNotificationItem createMergeRecommendationUserNotification(PersonItem source, PersonItem target) {
    	UserNotificationItem result = createUserNotification(UN_TYPE.MERGE_RECOMMENDATION);
    	UNEntryMergeRecommendation entry = (UNEntryMergeRecommendation) result.getUnEntry();
		entry.setSourceId(source.getGuid());
		entry.setSourceName(source.getName());
		entry.setTargetId(target.getGuid());
		entry.setTargetName(target.getName());
		entry.setSimilarity(0.2d);
		result.setUnEntry(entry);
    	return result;
    }
    
}
