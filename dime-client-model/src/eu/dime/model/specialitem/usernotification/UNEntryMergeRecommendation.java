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
public class UNEntryMergeRecommendation extends UnEntry {

    public static final String SOURCE_ID_TAG = "sourceId";
    public static final String SOURCE_NAME_TAG = "sourceName";
    public static final String TARGET_ID_TAG = "targetId";
    public static final String TARGET_NAME_TAG = "targetName";
    public static final String SIMILYRITY = "similarity";
    public static final String STATUS = "status";
    
    public static final String[] STATUS_TYPES = new String[]{"pending", "accepted", "dismissed"};
    
    private String sourceId;
    private String sourceName;
    private String targetId;
    private String targetName;
    private double similarity;
    private String status;

    public UNEntryMergeRecommendation() {
        wipeItem();
    }

    @Override
    public UnEntry getClone() {
        UNEntryMergeRecommendation entry = new UNEntryMergeRecommendation();
        entry.sourceId = this.sourceId;
        entry.sourceName = this.sourceName;
        entry.targetId = this.targetId;
        entry.targetName = this.targetName;
        entry.similarity = this.similarity;
        entry.status = this.status;
        return entry;
    }

    @Override
    protected final void wipeItem() {
        this.sourceId = "";
        this.sourceName = "";
        this.targetId = "";
        this.targetName = "";
        this.similarity = 0.0d;
        this.status = STATUS_TYPES[0];
    }

    @Override
    public JSONObject createJSONObject() {
        JSONObject result = new JSONObject("0");
        result.addChild(getJSONValue(sourceId, SOURCE_ID_TAG));
        result.addChild(getJSONValue(sourceName, SOURCE_NAME_TAG));
        result.addChild(getJSONValue(targetId, TARGET_ID_TAG));
        result.addChild(getJSONValue(targetName, TARGET_NAME_TAG));
        result.addChild(getJSONValue(similarity, SIMILYRITY));
        result.addChild(getJSONValue(status, STATUS));
        return result;
    }

    @Override
    public void readJSONObject(JSONObject jsonObject) throws InvalidJSONItemException {
        // clean up first
        wipeItem();
        // read the json
        this.sourceId = getStringValueOfJSONO(jsonObject, SOURCE_ID_TAG);
        this.sourceName = getStringValueOfJSONO(jsonObject, SOURCE_NAME_TAG);
        this.targetId = getStringValueOfJSONO(jsonObject, TARGET_ID_TAG);
        this.targetName = getStringValueOfJSONO(jsonObject, TARGET_NAME_TAG);
        this.similarity = getDoubleValueOfJSONO(jsonObject, SIMILYRITY);
        this.status = getStringValueOfJSONO(jsonObject, STATUS);
    }

    public String getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTargetId() {
        return targetId;
    }

	public void setTargetId(String target) {
		this.targetId = target;
	}

    public double getSimilarity() {
        return similarity;
    }

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	
	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	
	@Override
    public UN_TYPE getUnType() {
        return UN_TYPE.MERGE_RECOMMENDATION;
    }
    
}
