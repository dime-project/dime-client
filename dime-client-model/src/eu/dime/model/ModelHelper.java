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
 *  Description of ModelHelper
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 30.05.2012
 */
package eu.dime.model;

import eu.dime.control.SilentLoadingViewHandler;
import eu.dime.model.acl.ACL;
import eu.dime.model.displayable.AccountItem;
import eu.dime.model.displayable.AgentItem;
import eu.dime.model.displayable.DataboxItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.EventItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.LiveStreamItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.PlaceItem;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.model.displayable.ServiceAdapterItem;
import eu.dime.model.specialitem.SearchResultItem;
import eu.dime.model.specialitem.advisory.AdvisoryItem;
import eu.dime.model.specialitem.advisory.AdvisoryRequestItem;
import eu.dime.restapi.DimeHelper;
import eu.dime.restapi.RestApiAccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import sit.web.client.HttpHelper;

/**
 *
 * @author Simon Thiel
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ModelHelper {

    public static TYPES getMTypeFromString(String type) throws ModelTypeNotFoundException {
        for (TYPES mType : TYPES.values()) {
            PSMapEntry entry = Model.TypeMap.get(mType);
            if (entry.type.equals(type)) {
                return mType;
            }
        }
        throw new ModelTypeNotFoundException(type);
    }

    public static String getStringType(TYPES type) {
        return Model.TypeMap.get(type).type;
    }

    public static String getRestFunctionName(CALLTYPES callType){
        return Model.CallTypeMap.get(callType).functionName;
    }
    
    public static String getPathNameOfType(TYPES type) {
        return Model.TypeMap.get(type).path;
    }

    public static boolean isShareable(TYPES type) {
        return Model.TypeMap.get(type).isShareable;
    }
    
    public static boolean isAgent(TYPES type) {
    	boolean isAgent = false;
		if(type.equals(TYPES.PERSON) || type.equals(TYPES.GROUP)) {
			isAgent = true;
		}
    	return isAgent;
    }

    public static boolean isDisplayableItem(TYPES type) {
        try {
            Class typeClass = Model.TypeMap.get(type).getTypeClass();
            return DisplayableItem.class.isAssignableFrom(typeClass);
        } catch (NullPointerException ex) {
            throw new RuntimeException("unable to find type: " + type, ex);
        }
    }

    public static void sortItemsByName(List<DisplayableItem> items) {
        Collections.sort(items, new Comparator<DisplayableItem>() {

            public int compare(DisplayableItem item1, DisplayableItem item2) {
                return item1.getName().compareTo(item2.getName());
            }
        });
    }

    public static void sortItemsByType(List<GenItem> items) {
        Collections.sort(items, new Comparator<GenItem>() {

            public int compare(GenItem item1, GenItem item2) {
                return item1.getType().compareTo(item2.getType());
            }
        });
    }

    public static TYPES getChildType(TYPES type) {
        return Model.TypeMap.get(type).childType;
    }

    public static TYPES getParentType(TYPES type) {
        return Model.TypeMap.get(type).parentType;
    }
    
    public static boolean hasParent(TYPES type){
        return (Model.TypeMap.get(type).parentType!=null);
    }

    public static String getNameOfType(TYPES type) {
        try {
            return Model.TypeMap.get(type).type;
        } catch (NullPointerException ex) {
            throw new RuntimeException("unable to find type: " + type, ex);
        }
    }

    public static String getCallName(CALLTYPES callType) {
        return Model.CallTypeMap.get(callType).callName;
    }

    /**
     * returns the complete subpath for accessing the specified item based on
     * the call-type given.
     *
     * @param callType
     * @param hoster
     * @param owner
     * @param type
     * @param guid
     * @return
     */
    public static String getPath(CALLTYPES callType, String hoster, String owner, TYPES type, String guid) {
        CallTypeEntry ctEntry = Model.CallTypeMap.get(callType);
        StringBuilder result = new StringBuilder();
        if (callType.equals(CALLTYPES.COMET)) {
            result.append(DimeHelper.DIME_NOTIFICATION_PATH)
                    .append(HttpHelper.encodeString(hoster))
                    .append("/").append(HttpHelper.encodeString(guid))
                    .append("/").append(ctEntry.functionName)                    
                    .append("?"+DimeHelper.DIME_NOTIFICATION_PUSH_PARM_STARTING_FROM+"=")
                    .append(DimeHelper.systemStartTime);
            return result.toString();
        }//else
        if (callType.equals(CALLTYPES.DUMP)) {
            result.append(DimeHelper.DIME_BASIC_PATH).append(HttpHelper.encodeString(hoster)).append("/").append(ctEntry.functionName);
            return result.toString();
        }//else
        if (callType.equals(CALLTYPES.MERGE)) {
            result.append(DimeHelper.DIME_BASIC_PATH).append(HttpHelper.encodeString(hoster))
                    .append("/").append(getPathNameOfType(type))
                    .append("/").append(ctEntry.functionName);
            return result.toString();
        
        }//else
        if (callType.equals(CALLTYPES.AUTH_GET)) {
            result.append(DimeHelper.DIME_BASIC_PATH).append(HttpHelper.encodeString(hoster))
                    .append("/").append(ctEntry.functionName);
            return result.toString();
        }//else
        if (callType.equals(CALLTYPES.AUTH_POST)) {
            result.append(DimeHelper.DIME_BASIC_PATH).append(HttpHelper.encodeString(hoster))
                    .append("/").append(ctEntry.functionName)
                    .append("/@me");
            return result.toString();
        }//else
        if (callType.equals(CALLTYPES.AT_GLOBAL_ALL_GET)) {
        	result.append(DimeHelper.DIME_BASIC_PATH).append(HttpHelper.encodeString(hoster))
        		.append("/")
        		.append(getPathNameOfType(type))
        		.append("/")
        		.append(ctEntry.functionName);
        	return result.toString();
        }//else
        if (owner == null || owner.length() == 0) {
            throw new RuntimeException("Owner must be specified!"
                    + "\n(getPath for: CallType" + callType
                    + "\nhoster:" + hoster
                    + "\nowner:" + owner
                    + "\ntype:" + type
                    + "\nguid:" + guid);
        }
        result.append(DimeHelper.DIME_BASIC_PATH).append(HttpHelper.encodeString(hoster)).append("/").append(getPathNameOfType(type)).append("/").append(owner.equals("@me") ? "@me" : HttpHelper.encodeString(owner)).append("/");
        if (callType.equals(CALLTYPES.AT_ALL_GET)) {
            result.append(ctEntry.functionName);
        } else if (callType.equals(CALLTYPES.AT_ITEM_POST_NEW)) {
            // add nothing
       } else {
            result.append(HttpHelper.encodeString(guid));
        }
        return result.toString();
    }
    
    public static String getPathForFileUpload(String hoster) {
        CallTypeEntry ctEntry = Model.CallTypeMap.get(CALLTYPES.CRAWLER);
        
        StringBuilder result = new StringBuilder();
        result.append(DimeHelper.DIME_BASIC_PATH).append(HttpHelper.encodeString(hoster))
                .append("/").append(Model.TypeMap.get(TYPES.RESOURCE).path)
                .append("/").append(ctEntry.functionName);
        return result.toString();
    }

    public static TYPES lookupType(String param) {
        for (TYPES type : TYPES.values()) {
            if (type.name().equals(param)) {
                return type;
            }
        }
        return null;
    }

    public static List<DisplayableItem> getAllDisplayableItems(ModelRequestContext mrContext, TYPES type) {
        try {
            return (List<DisplayableItem>) (Object) Model.getInstance().getAllItems(mrContext, type); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<DisplayableItem>();
    }

    public static List<PersonItem> getAllPersons(ModelRequestContext mrContext) {
        try {
            return (List<PersonItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.PERSON); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<PersonItem>();
    }
    
    public static boolean isPersonValidForSharing(PersonItem personItem) {
    	return personItem.getDefaultProfileGuid() != null && personItem.getDefaultProfileGuid().length() > 0;
    }
    
    public static List<PersonItem> getAllPersonsValidForSharing(ModelRequestContext mrContext) {
        List<PersonItem> persons = getAllPersons(mrContext);
        Iterator<PersonItem> iter = persons.iterator();
        while (iter.hasNext()){
            PersonItem personItem = iter.next();
            if(!isPersonValidForSharing(personItem)) {
				iter.remove();
			}
        }
        return persons;
    }

    public static List<GroupItem> getAllGroups(ModelRequestContext mrContext) {
        try {
            return (List<GroupItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.GROUP); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<GroupItem>();
    }

    public static List<ResourceItem> getAllResources(ModelRequestContext mrContext) {
        try {
            return (List<ResourceItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.RESOURCE); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<ResourceItem>();
    }

    public static List<ResourceItem> getAllAllResources(ModelRequestContext mrContext) {
        try {
            return (List<ResourceItem>) (Object) Model.getInstance().getAllAllItems(mrContext, TYPES.RESOURCE); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<ResourceItem>();
    }

    public static List<DataboxItem> getAllDataBoxes(ModelRequestContext mrContext) {
        try {
            return (List<DataboxItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.DATABOX); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<DataboxItem>();
    }

    public static List<DataboxItem> getAllAllDataBoxes(ModelRequestContext mrContext) {
        try {
            return (List<DataboxItem>) (Object) Model.getInstance().getAllAllItems(mrContext, TYPES.DATABOX); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<DataboxItem>();
    }

    public static List<ProfileItem> getAllProfiles(ModelRequestContext mrContext) {
        try {
        	List<ProfileItem> profiles = (List<ProfileItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.PROFILE);
        	Iterator<ProfileItem> iter = profiles.iterator();
            while (iter.hasNext()){
                ProfileItem profile = iter.next();
	        	if(profile.getServiceAccountId() == null || profile.getServiceAccountId().length() == 0) {        		
	        		String profileName = profile.getName();
                    iter.remove();
	        		Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, "profile " + profileName + " igonored because of missing said!");
	        	}
            }
            return profiles;
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<ProfileItem>();
    }

    public static List<ProfileItem> getAllAllProfiles(ModelRequestContext mrContext) {
        try {
            return (List<ProfileItem>) (Object) Model.getInstance().getAllAllItems(mrContext, TYPES.PROFILE); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<ProfileItem>();

    }

    public static List<LivePostItem> getAllLivePosts(ModelRequestContext mrContext) {
        try {
            return (List<LivePostItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.LIVEPOST); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<LivePostItem>();
    }

    public static List<LivePostItem> getAllAllLivePosts(ModelRequestContext mrContext) {
        try {
            return (List<LivePostItem>) (Object) Model.getInstance().getAllAllItems(mrContext, TYPES.LIVEPOST); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<LivePostItem>();
    }

    public static List<EventItem> getAllEvents(ModelRequestContext mrContext) {
        try {
            return (List<EventItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.EVENT); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<EventItem>();
    }

    public static List<EventItem> getAllAllEvents(ModelRequestContext mrContext) {
        try {
            return (List<EventItem>) (Object) Model.getInstance().getAllAllItems(mrContext, TYPES.EVENT); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<EventItem>();
    }

    public static List<PlaceItem> getAllPlaces(ModelRequestContext mrContext) {
        try {
            return (List<PlaceItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.PLACE); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<PlaceItem>();
    }
    
    public static List<ServiceAdapterItem> getAllServiceAdapters(ModelRequestContext mrContext) {
        try {
            return (List<ServiceAdapterItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.SERVICEADAPTER); //HACK free cast
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex.getCause());
        }
        return new Vector<ServiceAdapterItem>();
    }

    public static List<GroupItem> getGroupsOfPerson(ModelRequestContext mrContext, String personGUID) {
		List<GroupItem> result = new Vector();
        List<GroupItem> allGroups = getAllGroups(mrContext);
        for (GroupItem group : allGroups) {
            if (group.containsItem(personGUID)) {
                result.add(group);
            }
        }
        return result;
    }
    
    public static List<DataboxItem> getDataboxesOfResource(ModelRequestContext mrContext, String guid) {
    	List<DataboxItem> result = new Vector();
        List<DataboxItem> allDataboxes = getAllDataBoxes(mrContext);
        for (DataboxItem databoxItem : allDataboxes) {
            if (databoxItem.containsItem(guid)) {
                result.add(databoxItem);
            }
        }
        return result;
	}

    public static List<ProfileItem> getProfilesSharedByPerson(ModelRequestContext mrContext) {
        return getAllProfiles(mrContext);
    }

    public static List<DataboxItem> getDataboxesSharedByPerson(ModelRequestContext mrContext) {
        return getAllDataBoxes(mrContext);
    }

    public static List<ResourceItem> getResourcesSharedByPerson(ModelRequestContext mrContext) {
        return getAllResources(mrContext);
    }

    public static List<LivePostItem> getLivePostsSharedByPerson(ModelRequestContext mrContext) {
        return getAllLivePosts(mrContext);
    }
    
    public static List<PersonItem> getPersonsOfGroup(ModelRequestContext mrContext, GroupItem group) {
        return (List<PersonItem>)(Object) getChildrenOfDisplayableItem(mrContext, group);
    }
    
    public static List<ResourceItem> getResourcesOfDatabox(ModelRequestContext mrContext, DataboxItem databox) {
        return (List<ResourceItem>)(Object) getChildrenOfDisplayableItem(mrContext, databox);
    }

    public static List<ProfileAttributeItem> getProfileAttributesOfProfile(ModelRequestContext mrContext, ProfileItem profile) {
        return (List<ProfileAttributeItem>)(Object) getChildrenOfDisplayableItem(mrContext, profile);
    }

    public static List<LivePostItem> getLivePostsOfLivestream(ModelRequestContext mrContext, LiveStreamItem liveStream) {
        return (List<LivePostItem>)(Object) getChildrenOfDisplayableItem(mrContext, liveStream);
    }
    
    public static List<DisplayableItem> getChildrenOfDisplayableItem(ModelRequestContext mrContext, DisplayableItem item) {
        PSMapEntry typeEntry = Model.TypeMap.get(item.getMType());
        TYPES childType = typeEntry.childType;
        if (childType == null) {
            return new Vector(); //TODO throw exception instead?
        }
        Vector<DisplayableItem> result = new Vector();
        try {
            for (String guid : item.getItems()) {
                DisplayableItem myItem = (DisplayableItem) Model.getInstance().getItem(mrContext, childType, guid);
                if (myItem != null) {
                    result.add(myItem);
                } else {
                    Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE,
                            "Corrupt collection!\nUnable to find child from collection!\nCollection:"
                            + item.getGuid() + "(" + item.getType()
                            + ")\nItem:" + guid + "(" + childType + ")");
                }
            }
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return result;
    }

    public static List<DisplayableItem> getParentsOfDisplayableItem(ModelRequestContext mrContext, String guid, TYPES type) {
        TYPES parentType = Model.TypeMap.get(type).parentType;
        if (parentType == null) {
            return new Vector(); //TODO throw exception instead?
        }
        //we can safely use the list returned from that function, since it's only a copy of the actual objects
        List<DisplayableItem> parents = (List<DisplayableItem>) (Object) Model.getInstance().getAllItems(mrContext, parentType);
        Iterator<DisplayableItem> iter = parents.iterator();
        while (iter.hasNext()){
            DisplayableItem myItem = iter.next();
            if (!myItem.containsItem(guid)){
                iter.remove(); //remove items that are not the parent of this item
            }
        }
        return parents;        
    }

    public static void shareItemToAgent(ModelRequestContext mrContext, GenItem item, AgentItem agent, ProfileItem senderProfile) throws SharingNotSupportedForSAIDException {
        shareItemToAgent(mrContext, item, agent, senderProfile.getServiceAccountId());
    }

    public static void shareItemToAgent(ModelRequestContext mrContext, GenItem item, AgentItem agent, String senderSAID) throws SharingNotSupportedForSAIDException {
        if (senderSAID==null || senderSAID.length()==0 || senderSAID.equals("@me")){
            throw new SharingNotSupportedForSAIDException("Cannot share with said:"+senderSAID); 
        }
        item.addAccessingAgent(senderSAID, agent.getGuid(), agent.getMType(), null);
        Model.getInstance().updateItem(mrContext, item);
    }

    public static AgentItem getAgent(ModelRequestContext mrContext, String agentGuid) {
        try {
            AgentItem result = (AgentItem) Model.getInstance().getItem(mrContext, TYPES.PERSON, agentGuid);
            if (result == null) { // person was not found - maybe its a group
                result = (AgentItem) Model.getInstance().getItem(mrContext, TYPES.GROUP, agentGuid);
            }
            return result;
        } catch (ClassCastException ex) {
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    
    public static AgentItem getAgentByName(ModelRequestContext mrContext, String agentName) {
        List <PersonItem> persons = getAllPersons(mrContext);
        for (PersonItem person : persons){
            if (agentName.equals(person.getName())){
                return person;
            }
        }
        List <GroupItem> groups = getAllGroups(mrContext);
        for (GroupItem group : groups){
            if (agentName.equals(group.getName())){
                return group;
            }
        }
        return null;
    }
    
    public static boolean agentIsPerson(ModelRequestContext mrContext, String agentId){
        for (PersonItem person: getAllPersons(mrContext)){
            if (person.getGuid().equals(agentId)){
                return true;
            }
        }
        return false;
    }
    
    public static ArrayList<String> getAgentAndGroupGuids(ModelRequestContext mrContext, AgentItem agent) {
        ArrayList<String> agentGuids = new ArrayList();
        agentGuids.add(agent.getGuid());
        if (agent.getMType()==TYPES.PERSON){
            //add groups for persons
            for (GroupItem group : getGroupsOfPerson(mrContext, agent.getGuid())){
                agentGuids.add(group.getGuid());
            }
        }
        return agentGuids;
    }
    
    public static ArrayList<ACL> getAclsOfItemAndContainers(ModelRequestContext mrContext, GenItem item) {
        ArrayList<ACL> result = new ArrayList();
        result.add(item.getAccessingAgents());
        //add containers for resources and liveposts        
        if (getParentType(item.getMType()) != null) { 
            //parent is only working for DisplayableItems !!!
            List<DisplayableItem> parents = getAllDisplayableItems(
                    new ModelRequestContext(mrContext.hoster, Model.ME_OWNER, mrContext.lvHandler), 
                    getParentType(item.getMType()));
            for (DisplayableItem pItem : parents) {
                if (pItem.containsItem(item.getGuid())) { //e.g. databox contains resource (item) as child
                    result.add(pItem.getAccessingAgents());
                }
            }
        }
        return result;
    }
    
    /**
     * checks whether the given agent has somehow access to a specific item
     * @param mrContext
     * @param agent 
     * @param item 
     * @return 
     */
    public static boolean agentHasAccessToItem(ModelRequestContext mrContext, AgentItem agent, GenItem item) {
        ArrayList<String> agentGuids = getAgentAndGroupGuids(mrContext, agent);
        ArrayList<ACL> acls = getAclsOfItemAndContainers(mrContext, item);
        for (String agentGuid: agentGuids){
            for (ACL acl:acls){
                if (acl.agentHasDirectAccess(agentGuid)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public static List<GenItem> getItemsDirectlySharedToAgent(ModelRequestContext mrContext, String agentGuid, TYPES type) {
        List<GenItem> result = Model.getInstance().getAllItems(new ModelRequestContext(mrContext.hoster, Model.ME_OWNER, new SilentLoadingViewHandler()), type);
        Iterator<GenItem> iter = result.iterator();
        while (iter.hasNext()) {
            GenItem item = iter.next();
            if (!item.hasDirectAccess(agentGuid)) {
                iter.remove();
            }
        }
        return result;
    }
    
    public static List<GenItem> getAllShareableItemsDirectlySharedToAgent(ModelRequestContext mrContext, String agentGuid) {
    	List<GenItem> allItems = new ArrayList<GenItem>();
    	for(TYPES type : TYPES.values()){
    		if(isShareable(type)) {
    			allItems.addAll(getItemsDirectlySharedToAgent(mrContext, agentGuid, type));
    		}
    	}
    	return allItems;
    }

    public static List<DataboxItem> getDataboxesDirectlySharedToAgent(ModelRequestContext mrContext, String agentGuid) {
        return (List<DataboxItem>) (Object) getItemsDirectlySharedToAgent(mrContext, agentGuid, TYPES.DATABOX);
    }

    public static List<ResourceItem> getResourcesDirectlySharedToAgent(ModelRequestContext mrContext, String agentGuid) {
        return (List<ResourceItem>) (Object) getItemsDirectlySharedToAgent(mrContext, agentGuid, TYPES.RESOURCE);
    }

    public static List<LivePostItem> getLivePostsDirectlySharedToAgent(ModelRequestContext mrContext, String agentGuid) {
        return (List<LivePostItem>) (Object) getItemsDirectlySharedToAgent(mrContext, agentGuid, TYPES.LIVEPOST);
    }

    public static List<ProfileItem> getProfilesDirectlySharedToAgent(ModelRequestContext mrContext, String agentGuid) {
        return (List<ProfileItem>) (Object) getItemsDirectlySharedToAgent(mrContext, agentGuid, TYPES.PROFILE);
    }

    public static List<ProfileAttributeItem> getProfileAttributesDirectlySharedToAgent(ModelRequestContext mrContext, String agentGuid) {
        return (List<ProfileAttributeItem>) (Object) getItemsDirectlySharedToAgent(mrContext, agentGuid, TYPES.PROFILEATTRIBUTE);
    }

    public static List<EventItem> getEventsDirectlySharedToAgent(ModelRequestContext mrContext, String agentGuid) {
        return (List<EventItem>) (Object) getItemsDirectlySharedToAgent(mrContext, agentGuid, TYPES.EVENT);
    }
    
    /**
     * tries to find out whether a url is absolute or relative and returns an adapted version
     * @param urlString
     * @return 
     */
    public static String guessURLString(String urlString){
        //check for absolute url
        if ((urlString == null) || (urlString.toLowerCase().startsWith("http"))) {
            return urlString;
        } //we probably have a relative URL
        return DimeHelper.getAbsoluteUrlString(urlString, Model.getInstance().getRestApiConfiguration());
    }

    public static void addPersonToGroup(ModelRequestContext mrContext, PersonItem personItem, GroupItem groupItem) {
        groupItem.addItem(personItem.getGuid());
        Model.getInstance().updateItem(mrContext, groupItem);
    }
    
    public static void addResourceToDatabox(ModelRequestContext mrContext, ResourceItem resourceItem, DataboxItem dbItem) {
        dbItem.addItem(resourceItem.getGuid());
        Model.getInstance().updateItem(mrContext, dbItem);
    }

    public static void addProfileAttributeToProfile(ModelRequestContext mrContext, ProfileAttributeItem profileAttributeItem, ProfileItem profileItem) {
        profileItem.addItem(profileAttributeItem.getGuid());
        Model.getInstance().updateItem(mrContext, profileItem);
    }
    
    public static DisplayableItem getDisplayableByName(ModelRequestContext mrc, String name, TYPES type) {
        if (name==null){
            return null;
        }
        List<DisplayableItem> dItems = getAllDisplayableItems(mrc, type);
        for (DisplayableItem dItem : dItems){
            if (name.equals(dItem.getName())){
                return dItem;
            }
        }
        return null;
    }
    
    public static boolean isParentable(GenItem item){
        PSMapEntry entry = Model.TypeMap.get(item.getMType());
        return entry.childType!=null;
    }
    
    public static List<DisplayableItem> getAllAgentsAsDisplayable(ModelRequestContext mrC) {
        return (List<DisplayableItem>)(Object)getAllAgents(mrC);
    }

    public static List<AgentItem> getAllAgents(ModelRequestContext mrC) {
        List<AgentItem> result = new Vector<AgentItem>();
        for (PersonItem person : getAllPersons(mrC)){
            result.add(person);
        }
        for (GroupItem group: getAllGroups(mrC)){
            result.add(group);
        }
        return result;
    }

    public static List<DisplayableItem> getDisplayableItemsFromAndForPerson(ModelRequestContext mrC, String personGuid, TYPES itemType) {
        ModelRequestContext mrC_person = new ModelRequestContext(mrC.hoster, personGuid, mrC.lvHandler);
        // items coming from that person        
        List<DisplayableItem> itemsFromPerson = getAllDisplayableItems(mrC_person, itemType);
        try {
            //items the user is sharing to the person
            List<DisplayableItem> itemsToPerson = (List<DisplayableItem>) (Object) getItemsDirectlySharedToAgent(mrC, personGuid, itemType);
            itemsFromPerson.addAll(itemsToPerson);
            return itemsFromPerson;
        } catch(ClassCastException ex){
            Logger.getLogger(ModelHelper.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        return new Vector<DisplayableItem>();
    }
    
    public static ProfileItem getProfileWithSaid(ModelRequestContext mrc, String said){
        for (ProfileItem profile: getAllProfiles(mrc)){
            if (profile.getServiceAccountId().equals(said)){
                return profile;
            }
        }
        return null;
    }
    
    public static List<ProfileItem> getAllValidProfilesForSharing(ModelRequestContext mrContext){        
        List<ProfileItem> result = getAllProfiles(mrContext);
        Iterator<ProfileItem> iter = result.iterator();
        while (iter.hasNext()){
            ProfileItem profile = iter.next();
            if (profile.getUserId().equals(Model.ME_OWNER) && !profile.supportsSharing()){
                iter.remove();
            }
        }
        return result;
    }
    
    public static ProfileItem getDefaultProfileForSharing(ModelRequestContext mrContext, List<AgentItem> receivers){
        for (AgentItem agent : receivers){
            List<String> agentGuids = getAgentAndGroupGuids(mrContext, agent);
            for (String agentGuid:agentGuids){
                List<ProfileItem> result = getProfilesDirectlySharedToAgent(mrContext, agentGuid);
                if (result.size()>0){
                    return result.get(0);
                }
            }
        }       
        //return default otherwise
        return getDefaultProfileForSharing(mrContext);
    }
    
    public static ProfileItem getDefaultProfileForSharing(ModelRequestContext mrContext){
        List<ProfileItem> allProfiles = getAllValidProfilesForSharing(mrContext);
        for (ProfileItem profile: allProfiles){  //TODO FIXME to be removed? HACK we search for a public profile 
            if (profile.getName().toLowerCase().contains("public")){
                return profile;
            }
        }
        //if no "public" profile return the first one that is not empty
        if (allProfiles.size()>0){
            return allProfiles.get(0);            
        }
        return null;
    }
    
    public static String getDefaultSenderSaid(ModelRequestContext mrContext){
       ProfileItem profile = getDefaultProfileForSharing(mrContext);
       if (profile != null){
           return profile.getServiceAccountId();
       }
       return null;
    }
    
    public static String getDefaultReceiverSaidForPerson(ModelRequestContext mrc, PersonItem person) {
        if (person.getDefaultProfileGuid()==null){
            return null;
        }
        ProfileItem profile = (ProfileItem) Model.getInstance().getItem(new ModelRequestContext(mrc.hoster, person.getGuid(), mrc.lvHandler), TYPES.PROFILE, person.getDefaultProfileGuid());
        if (profile!=null){
            return profile.getServiceAccountId();
        }
        return null;
    }
  
    public static String getDefaultReceiverSaidForPerson(ModelRequestContext mrc, String personGuid) {
        PersonItem person = (PersonItem) Model.getInstance().getItem(new ModelRequestContext(mrc.hoster, Model.ME_OWNER, mrc.lvHandler), TYPES.PERSON, personGuid);
        return getDefaultReceiverSaidForPerson(mrc, person);
    }

    public static String findPersonGuidByServiceAccount(ModelRequestContext personContext, String serviceAccountId) {
        List<ProfileItem> allProfiles = getAllAllProfiles(personContext);
        for (ProfileItem profile: allProfiles){
            if (profile.getServiceAccountId().equals(serviceAccountId)){
                return profile.getUserId();
            }
        }
        return null;
    }
    
    @SuppressWarnings("unused")
	public static boolean ownerHasProfileWithServiceAccountId(ModelRequestContext ownerContext, String serviceAccountId) {
        //retrieve all profiles for each hoster
        List<ProfileItem> profiles = ModelHelper.getAllProfiles(ownerContext);
        for (ProfileItem profile: profiles){
            if (profile.getServiceAccountId().equals(serviceAccountId)){
                String result = profile.getServiceAccountId();
                return true;
            }
        }
        return false;
    }
    
    public static List<SearchResultItem> searchGlobaly(ModelRequestContext mrc, String query){
        return RestApiAccess.searchGlobal(mrc.hoster, query, Model.getInstance().getRestApiConfiguration());
    }
    
    public static boolean addPublicContact(ModelRequestContext mrc, SearchResultItem publicContact){
        return RestApiAccess.addPublicPerson(mrc.hoster, publicContact,  Model.getInstance().getRestApiConfiguration());
    }

    public static List<AdvisoryItem> getSharingAdvisories(String hoster, AdvisoryRequestItem ari) {
            return RestApiAccess.requestSharingAdvisories(hoster, ari, Model.getInstance().getRestApiConfiguration());
    }
    
    public static void updateLastUpdatedIfDisplayable(GenItem item) {
        if (ModelHelper.isDisplayableItem(item.getMType())) {
            ((DisplayableItem) item).setLastUpdated(System.currentTimeMillis());
        }
    }
    
    public static boolean isFitbitAdapterConnected(ModelRequestContext mrContext) {
		boolean isConnected = false;
		List<AccountItem> accounts = (List<AccountItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.ACCOUNT);
		for (AccountItem accountItem : accounts) {
			if(accountItem.getName().toLowerCase().contains("fitbit")) {
				isConnected = true;
			}
		}
		return isConnected;
	}

	public static boolean isPlaceAdapterConnected(ModelRequestContext mrContext) {
		boolean isConnected = false;
		List<AccountItem> accounts = (List<AccountItem>) (Object) Model.getInstance().getAllItems(mrContext, TYPES.ACCOUNT);
		for (AccountItem accountItem : accounts) {
			if(accountItem.getName().contains("YellowMap")) {
				isConnected = true;
			}
		}
		return isConnected;
	}
	
	public static List<GenItem> pickNRandom(List<GenItem> items, int n) {
		if(items.size() < n) n = items.size();
    	List<GenItem> listOfGuids = new ArrayList<GenItem>(items);
        Collections.shuffle(listOfGuids);
        return listOfGuids.subList(0, n);
    }
	
}
