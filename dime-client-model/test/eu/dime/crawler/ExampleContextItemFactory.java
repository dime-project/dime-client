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
 *  Description of ExampleContextItemFactory
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 03.05.2012
 */
package eu.dime.crawler;

import eu.dime.model.InvalidJSONItemException;
import eu.dime.model.ItemFactory;
import eu.dime.model.TYPES;
import eu.dime.model.context.ContextData;
import eu.dime.model.context.ContextDataFactory;
import eu.dime.model.context.ContextDataStringList;
import eu.dime.model.context.ContextItem;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public class ExampleContextItemFactory {

    public static ContextItem createItem(List<String> dataParts) {
        ContextItem result = new ContextItem(UUID.randomUUID().toString());
        result.setMType(TYPES.CONTEXT);

        for (String dataPartStr : dataParts) {
            ContextData cData = ContextDataFactory.createContextData(dataPartStr);
            result.addDataPart(dataPartStr, cData);
        }
        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Vector<String> listOfMyDataParts = new Vector();
        listOfMyDataParts.add("wfNames");
        listOfMyDataParts.add("wfList");


        ContextItem myContextItem = createItem(listOfMyDataParts);

        //add some content - Here some helper-class could be valuable
        for (int i = 0; i < 10; i++) {
            ((ContextDataStringList) myContextItem.getDataPart(listOfMyDataParts.get(0))).addString("" + i);
            ((ContextDataStringList) myContextItem.getDataPart(listOfMyDataParts.get(1))).addString("" + i);
        }

        myContextItem.setTimestamp(System.currentTimeMillis());
        myContextItem.setExpires(System.currentTimeMillis() + (300 * 1000));

        myContextItem.getContextSource().id = "39025u9032";
        myContextItem.getContextSource().version = "0.9";
        myContextItem.getEntity().id = "000000000001";
        myContextItem.getEntity().type = "wifi";


        //create a json object
        JSONObject contextJSONO = myContextItem.createJSONObject();
        System.out.println(contextJSONO.toString());

        //reparse it
        try {
            ContextItem myContextItem_new = (ContextItem) (ItemFactory.createNewItemByJSON(TYPES.CONTEXT, contextJSONO));
            JSONObject contextJSONO_new = myContextItem_new.createJSONObject();
            System.out.println(contextJSONO_new.toString());
        } catch (InvalidJSONItemException ex) {
            Logger.getLogger(ExampleContextItemFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
