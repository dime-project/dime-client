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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.model.specialitem.usernotification;

import eu.dime.model.InvalidJSONItemException;
import sit.json.JSONObject;

/**
 *
 * @author simon
 */
public class UNEntryMessage extends UnEntry {

    public static final String MESSAGE = "message";
    public static final String LINK = "link";
    private String message;
    private String link;

    public UNEntryMessage() {
        wipeItem();
    }

    @Override
    public UnEntry getClone() {
        UNEntryMessage entry = new UNEntryMessage();
        entry.message = this.message;
        entry.link = this.link;
        return entry;
    }

    @Override
    protected final void wipeItem() {
        this.message = "";
        this.link = "";
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(message, MESSAGE));
        result.addChild(getJSONValue(link, LINK));
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        wipeItem();
        this.message = getStringValueOfJSONO(jsonObject, MESSAGE);
        this.link = getStringValueOfJSONO(jsonObject, LINK);
    }

    public String getMessage() {
        return message;
    }

    public String getLink() {
        return link;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }
    
    @Override
    public UN_TYPE getUnType() {
        return UN_TYPE.MESSAGE;
    }
    
    
}
