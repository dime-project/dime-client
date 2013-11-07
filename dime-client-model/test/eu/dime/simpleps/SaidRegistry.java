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

package eu.dime.simpleps;

import eu.dime.control.LoadingViewHandler;
import eu.dime.model.CreateItemFailedException;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.ProfileAttributeCategoriesEntry;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.storage.DimeMemory;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.io.FileHelper;

/**
 *
 * @author simon
 */
public class SaidRegistry {



    private Map<String, String> cachedMappings = new HashMap();
    private Set<String> storedHosters = Model.getInstance().getAllHosters();
    
    private static SaidRegistry instance = new SaidRegistry();
    
    
    private SaidRegistry() {
        //look for files in the dime path to find all exising hosters 
        initStoredHosters();
    }
    
    private void initStoredHosters(){
        FileHelper fh = new FileHelper();
        File folder = new File(DimeMemory.path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            File myFile = listOfFiles[i];
            if (myFile.isFile()) {
                if ("json".equals(fh.getExtention(myFile.getAbsolutePath()))){
                    String fileName = myFile.getName();
                    
                    int dotIndex = fileName.indexOf('.');                    
                    String hosterId = fileName.substring(0, dotIndex);
                    while (hosterId.startsWith("_")){ //FIXME !!!! important this won't work generally 
                                                      //- since many special char in hosterIds are replaced by "_" in the filename
                        hosterId = hosterId.substring(1);
                    }
                    
                    storedHosters.add(hosterId);
                }
            } else if (listOfFiles[i].isDirectory()) {
                //ignore
            }            
        }
        String logStr = "found "+storedHosters.size()+" stored hosters:\n";
        for (String hosterId: storedHosters){
            logStr+= hosterId+" ";
        }
        Logger.getLogger(SaidRegistry.class.getName()).log(Level.INFO, logStr);
    }
    
    

    private static void updateCachedMappings(LoadingViewHandler lvh){
        Set<String> allHosters = instance.storedHosters;
        allHosters.addAll(Model.getInstance().getAllHosters());
        
        for (String hoster : allHosters){
            ModelRequestContext mrcHost = new ModelRequestContext(hoster, Model.ME_OWNER, lvh);
            for (ProfileItem profile: ModelHelper.getAllProfiles(mrcHost)){
                instance.cachedMappings.put(profile.getServiceAccountId(), hoster);
                
            }            
        }
    }
    
    private static String lookupLocalNameByServiceAccountFromModel(LoadingViewHandler lvh, 
            String serviceAccountId, String localHoster) {
        
        if (serviceAccountId==null){
            return null;
        }
        
        List<ProfileItem> allProfiles = ModelHelper.getAllAllProfiles(new ModelRequestContext(localHoster, Model.ME_OWNER, lvh));
        for (ProfileItem profile: allProfiles){
            if (serviceAccountId.equals(profile.getServiceAccountId())){
                
                ModelRequestContext mrc = new ModelRequestContext(localHoster, profile.getUserId(), lvh);
                
                //we have the model - now lets find a "Name" profile-attribute
                for (String profAttrGuid : profile.getItems()){
                    ProfileAttributeItem profAttrItem = (ProfileAttributeItem) Model.getInstance().getItem(mrc, 
                            TYPES.PROFILEATTRIBUTE, profAttrGuid);
                    if (profAttrItem==null){
                        Logger.getLogger(SaidRegistry.class.getName()).log(Level.WARNING, 
                                "cannot find profile attribute item with guid:"+profAttrGuid
                                +"\n hoster: "+mrc.hoster+" owner: "+ mrc.owner
                                );
                        continue;
                    }
                    if (profAttrItem.getCategoryType()==ProfileAttributeItem.VALUE_CATEGORIES.PERSON_NAME){
                        ProfileAttributeCategoriesEntry categoryEntry = profAttrItem.getProfileCategoryEntry();
                        int nameGiven = 4;
                        int familyName = 1;
                        String result = profAttrItem.getValue().get(categoryEntry.keys[familyName])
                                + " "+  profAttrItem.getValue().get(categoryEntry.keys[nameGiven]);                        
                        return result;
                    }
                }
                return null;
            }
        }
        return null;
    }

    
    private static String createAccountForSaid(LoadingViewHandler lvh, String serviceAccountId, String localHosterId) {
        
        String otherPersonsName = lookupLocalNameByServiceAccountFromModel(lvh, serviceAccountId, localHosterId);
        
        if (otherPersonsName==null){
            return null;
        }
        
        String otherPersonUserId = SimplePSHelper.createAccountForPerson(otherPersonsName);
        
        ModelRequestContext mrc = new ModelRequestContext(otherPersonUserId, Model.ME_OWNER, lvh);
        try {
            //create public profile for remote user
            SimplePSHelper.createDefaultProfileForPerson(mrc, otherPersonsName, serviceAccountId);
            
            //store new mapping in cache
            instance.cachedMappings.put(serviceAccountId, otherPersonUserId);
            
        } catch (CreateItemFailedException ex) {
            Logger.getLogger(SaidRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return otherPersonUserId;
    }
    
    public static String findHosterIdByServiceAccount(LoadingViewHandler lvh, String serviceAccountId, String localHosterId) {
        if (instance.cachedMappings.containsKey(serviceAccountId)){
            return instance.cachedMappings.get(serviceAccountId);
        }
        //we didn't find it in the cache - so let's update the cache from the model
        updateCachedMappings(lvh);
        
        //and try again
        String result = null;
        if (instance.cachedMappings.containsKey(serviceAccountId)){
            result = instance.cachedMappings.get(serviceAccountId);
        }
        if (result==null){
            result = createAccountForSaid(lvh, serviceAccountId, localHosterId);
        }
        
        return result;        
    }
    
    public static Set<String> updateAndGetAllHosters(LoadingViewHandler lvh){
        updateCachedMappings(lvh);
        Set<String> result = new HashSet();
        for (String hoster : instance.cachedMappings.values()){
            result.add(hoster);
        }
        return result;
    }
}
