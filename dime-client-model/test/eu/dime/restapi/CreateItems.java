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
 *  Description of createItems
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 08.07.2012
 */
package eu.dime.restapi;

import eu.dime.model.*;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import sit.web.client.HttpHelper;

/**
 * createItems
 * 
 */
public class CreateItems {

    static Random rnd = new Random();
    
    static GenItem createItem(String hoster, TYPES type, RestApiConfiguration conf){
        GenItem newItem;
        if (type==TYPES.PROFILEATTRIBUTE){
            int category = rnd.nextInt(ProfileAttributeItem.VALUE_CATEGORIES.values().length);
            ProfileAttributeItem pnewItem = ItemFactory.cretateNewProfileAttributeItem(ProfileAttributeItem.VALUE_CATEGORIES.values()[category]);
            for (Entry<String, String> value : pnewItem.getValue().entrySet()){
                value.setValue(UUID.randomUUID().toString());
            }
            
            newItem = pnewItem;
        }else{
        
            newItem = ItemFactory.createNewItemByType(type);
        }
        return RestApiAccess.postItemNew(hoster, Model.ME_OWNER, type, newItem, conf);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String hoster = "9702325";
        RestApiConfiguration conf = new RestApiConfiguration("192.168.2.106", 8443, true,
                HttpHelper.getBase64UserNamePwdToken(hoster, "dimepass4owner"));
        
        
        
        ProfileAttributeItem profileAttributeItem = (ProfileAttributeItem) 
                createItem(hoster, TYPES.PROFILEATTRIBUTE, conf);
        
        
        ProfileItem profile = (ProfileItem) RestApiAccess.getAllItems(hoster, Model.ME_OWNER, TYPES.PROFILE, conf).get(0);
        profile.addItem(profileAttributeItem.getGuid());
        
        RestApiAccess.postItemUpdate(hoster, Model.ME_OWNER, TYPES.PROFILE, profile, conf);
        
//        ShareItem si = new ShareItem("a",
//                "b",
//                TYPES.PERSON, 
//                "c",
//                TYPES.LIVEPOST, "d");
//        
//        System.out.println("Object:");
//        System.out.println(si.createJSONObject().toString());
//        System.out.println("Path:");
//        System.out.println(ModelHelper.getPathForShare("9702325", Model.ME_OWNER, si));
        
        
        
        
    }
}
