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

package eu.dime.model.specialitem.advisory;

import java.util.List;
import java.util.Vector;
import sit.json.JSONObject;
import eu.dime.model.InvalidJSONItemException;

public class WarningAttributesUntrusted extends WarningAttributesObject {
	
	public static final String UNTRUSTED_AGENTS = "untrustedAgents";
    public static final String PRIVATE_RESOURCES = "privateResources";
    public static final String TRUST_VALUE = "trustValue";
    public static final String PRIVACY_VALUE = "privacyValue";

	private List<String> untrustedAgents;
    private List<String> privateResources;
    private double privacyValue = 0.0d;
    private double trustValue = 0.0d;

    public WarningAttributesUntrusted() {
        wipeItem();
    }
    
    public WarningAttributesUntrusted(List<String> untrustedAgents, List<String> privateResources) {
        this.untrustedAgents = untrustedAgents;
        this.privateResources = privateResources;
    }
   
    
    @Override
    public WarningAttributesUntrusted getClone() {
        return new WarningAttributesUntrusted(untrustedAgents, privateResources);
    }

    @Override
    protected final void wipeItem() {
        this.untrustedAgents = new Vector<String>();
        this.privateResources = new Vector<String>();
        this.privacyValue = 0.0d;
        this.trustValue = 0.0d;
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONCollection(this.untrustedAgents, UNTRUSTED_AGENTS));
        result.addChild(getJSONCollection(this.privateResources, PRIVATE_RESOURCES));
        result.addChild(getJSONValue(this.privacyValue, PRIVACY_VALUE));
        result.addChild(getJSONValue(this.trustValue, TRUST_VALUE));
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        // clean up first
        wipeItem();

        // read the json
        this.untrustedAgents = getStringListOfJSONObject(jsonObject, UNTRUSTED_AGENTS);
        this.privateResources = getStringListOfJSONObject(jsonObject, PRIVATE_RESOURCES);
        this.privacyValue = getDoubleValueOfJSONO(jsonObject, PRIVACY_VALUE);
        this.trustValue = getDoubleValueOfJSONO(jsonObject, TRUST_VALUE);
    }

    public List<String> getUntrustedAgents() {
        return untrustedAgents;
    }

    public List<String> getPrivateResources() {
        return privateResources;
    }

	public double getPrivacyValue() {
		return privacyValue;
	}

	public double getTrustValue() {
		return trustValue;
	}

}
