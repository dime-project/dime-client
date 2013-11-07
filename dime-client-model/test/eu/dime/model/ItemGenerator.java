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
 *  Description of ItemGenerator
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.05.2012
 */
package eu.dime.model;

import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.restapi.DimeHelper;
import eu.dime.simpleps.database.DatabaseAccess;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public class ItemGenerator {

    
    private static final String [] names = { "Gröpke Peter", "Amman Carmen", "Adelberg Klaus", "Aberle Hans", "Aldinger Inge", "Adelsberger Helmut", "Ackermann Petra", "Baumann Markus", "Baumgartner Sarah", "Bartels Henning", "Busse Constantin", "Boerner Bärbel", "Bernhardt Tanja", "Chilla Marc", "Cordes Adabi", "Cramer Melanie", "Dahlmann Irene", "Dorner Pabel", "Frick Tim", "Fiebig Barbara", "Flemming Tatjana", "Gensch Carla", "Gutjahr Katrin", "Gruening Günter", "Hock Klaus", "Heyer Martin", "Hampel Simon", "Hamm Birgit", "Hamm Karl", "Hamm Tobias", "Hoff Eckhard", "Morgenstern Susanne", "Messner Till", "Manz Adrian", "Tanner Alfred", "Thielen Fridolin", "Tessmann Jeannine", "Zimmermann Carsten", "Zimmer Florian", "Ziegler Birgit", "Zander Helmut", "Zeller Thomas"};
    
    private  Random rnd = new Random();
    private  DimeHelper dimeHelper = new DimeHelper();
    
    
    private Vector<String> getExistingGUIDs(ModelRequestContext mrContext, TYPES type, int maxNumberItems) {
        Vector<String> result = new Vector<String>(maxNumberItems);
        List<GenItem> allItems = Model.getInstance().getAllItems(mrContext, type);
        for (int i=0; i<maxNumberItems; i++){
            if (i>=allItems.size()){
                return result;
            }
            result.add(allItems.get(i).getGuid());
        }
        return result;

    }
    

    public  DisplayableItem generateRandomItem(ModelRequestContext mrContext, TYPES type) {
        
        DisplayableItem result = (DisplayableItem) ItemFactory.createNewItemByType(UUID.randomUUID().toString(),type);

        result.setName("TestName");
        result.setMType(type);
        result.setUserId(mrContext.owner);

        //in case child items exists for this type - add some random childs
        if (ModelHelper.getChildType(type) != null) {
            int maxNumberOfChilds = rnd.nextInt(5) + 5;
            Vector<String> childs = getExistingGUIDs(mrContext, ModelHelper.getChildType(type), maxNumberOfChilds);
            result.setItems(childs);
        }


        if (type == TYPES.PERSON) {
            result.setName(names[rnd.nextInt(names.length)]);
            //result.setImageUrl("/img/items/" + rnd.nextInt(10) + ".jpg");
            result.setImageUrl("http://lorempixel.com/200/200/people/"+ rnd.nextInt(10) + "/");
            
            //set trust-level for persons
            ((PersonItem)result).setTrustLevel(0.5);
        }else if (type == TYPES.PROFILE) {
            ((ProfileItem)result).setServiceAccountId(UUID.randomUUID().toString());
        }else if (type == TYPES.PROFILEATTRIBUTE) {
            ((ProfileAttributeItem)result).setCategory("Name");
            ((ProfileAttributeItem)result).getValue().put("nickname", "TN");
            ((ProfileAttributeItem)result).getValue().put("fullname", "Test Name");
            result.setImageUrl("http://lorempixel.com/200/200/abstract/"+ rnd.nextInt(10) + "/");
        } else {
            //result.setImageUrl("/img/items/" + (rnd.nextInt(8) + 11) + ".jpg");
            result.setImageUrl("http://lorempixel.com/200/200/abstract/"+ rnd.nextInt(10) + "/");
        }
        return result;
    }

    public  void createRandomItems(ModelRequestContext mrContext, TYPES type, int amount) {

        for (int i = 0; i < amount; i++) {

            JSONObject newItem = dimeHelper.packRequest(generateRandomItem(mrContext, type));
            try {
                DatabaseAccess.createItem(mrContext, type, newItem);
            } catch (CreateItemFailedException ex) {
                Logger.getLogger(ItemGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
