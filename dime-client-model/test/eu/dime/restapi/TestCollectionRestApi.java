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
 *  Description of TestCollection
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 04.05.2012
 */
package eu.dime.restapi;

import eu.dime.model.CALLTYPES;
import eu.dime.model.ModelHelper;
import eu.dime.model.TYPES;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public class TestCollectionRestApi {

	private Map<TestReference, TestResult> testCollection = new LinkedHashMap<TestReference, TestResult>();

    public static class TestReference {

        String hoster;
        String owner;
        RestApiTest.TL testCase;
        TYPES type;
        CALLTYPES callType;

        public TestReference(String hoster, String owner, RestApiTest.TL label, TYPES type, CALLTYPES callType) {
            this.hoster = hoster;
            this.owner = owner;
            this.testCase = label;
            this.type = type;
            this.callType = callType;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TestReference other = (TestReference) obj;
            if ((this.hoster == null) ? (other.hoster != null) : !this.hoster.equals(other.hoster)) {
                return false;
            }
            if ((this.owner == null) ? (other.owner != null) : !this.owner.equals(other.owner)) {
                return false;
            }
            if ((this.testCase == null) ? (other.testCase != null) : !this.testCase.equals(other.testCase)) {
                return false;
            }
            if (this.type != other.type) {
                return false;
            }
            if (this.callType != other.callType) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + (this.hoster != null ? this.hoster.hashCode() : 0);
            hash = 97 * hash + (this.owner != null ? this.owner.hashCode() : 0);
            hash = 97 * hash + (this.testCase != null ? this.testCase.hashCode() : 0);
            hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
            hash = 97 * hash + (this.callType != null ? this.callType.hashCode() : 0);
            return hash;
        }
    }
    
    public static class TestResult {
    	
        private JSONResponse response;
        private String remark;
        private long neededTime;
        private String notificationSent = "";
        private boolean isPlausible;

        public String getRemark() {
            return remark;
        }

        public JSONResponse getResponse() {
            return response;
        }
        
        public long getNeededTime() {
        	return neededTime;
        }

		public String getNotificationSent() {
			return notificationSent;
		}
		
		public void setNotificationSent(String notificationSent){
			this.notificationSent = notificationSent;
		}

		public boolean isPlausible() {
			return isPlausible;
		}

		public void setPlausible(boolean isPlausible) {
			this.isPlausible = isPlausible;
		}

		public void setResponse(JSONResponse response) {
			this.response = response;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public void setNeededTime(long neededTime) {
			this.neededTime = neededTime;
		}
		
    }

    private String getSemicolonString(List<String> fields) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);

            //escape field if necessary
            if (field.contains(";")) {
                String oldField = field;
                field = "\""; //start with a quote
                for (int j = 0; j < oldField.length(); j++) {
                    char myChar = oldField.charAt(j);
                    if (myChar == '"') {
                        field += "\""; //add double quotes
                    }
                    field += myChar;
                }
                field += "\""; //end with quotes
            }

            //add it to the string
            result.append(field);
            if (i < fields.size() - 1) {
                result.append(";");
            }
        }//for fields
        result.append("\n");
        return result.toString();
    }

    private String jsonFormatOfReply(JSONResponse response) {
        if ((response.replyObjects != null) && (!response.replyObjects.isEmpty())) {
            StringBuilder result = new StringBuilder();
            for (int i=0; i<response.replyObjects.size();i++){
                JSONObject replyObject = response.replyObjects.get(i);
                result.append(replyObject.toString());
                if (i<response.replyObjects.size()-1){
                    result.append(",\n");
                }
            }
            return result.toString();
        }
        return response.getReply();
    }

    public String toWikiDoc() {
        StringBuilder result = new StringBuilder();
        result.append("h1. Specification of REST-API\n* generated from the di.me client model\n\nh2. Contents\n{toc:maxLevel=3|minLevel=2|exclude=Contents}\n\n");

        //results
        for (Map.Entry<TestReference, TestResult> entry : testCollection.entrySet()) {
            if (entry.getKey().testCase.equals(RestApiTest.TL.T1_GET_ALL)) { //initial information about the call
                result.append("h2. ").append(ModelHelper.getNameOfType(entry.getKey().type)).append("\n\n");
                result.append("||hoster||owner||\n");
                result.append("|").append(entry.getKey().hoster).append("|").append(entry.getKey().hoster).append("|\n\n");
            }
            if (entry.getKey().testCase.equals(RestApiTest.TL.T2_GET_ITEM_FIRST_ITEM) && (entry.getValue() != null)) {
                result.append("h3. Get First Item ").append(entry.getKey().type).append("\n* ").append(entry.getValue().getResponse().getCall());
                result.append("\n\n{code}\n");
                result.append(jsonFormatOfReply(entry.getValue().getResponse())).append("\n");
                result.append("{code}\n\n");
            } else if (entry.getKey().testCase.equals(RestApiTest.TL.T4_CREATE_ITEM_NEW_ITEM) && (entry.getValue() != null)) {
                result.append("h3. Create New Item ").append(entry.getKey().type).append("\n* ").append(entry.getValue().getResponse().getCall());
                result.append("\n\n{code}\n");
                result.append(jsonFormatOfReply(entry.getValue().getResponse())).append("\n");
                result.append("{code}\n\n");
            }
            if (entry.getKey().testCase.equals(RestApiTest.TL.T0_GET_DUMP)) {
                result.append("h2. GET Dump\n\n");
                result.append("* Call: ").append(entry.getValue().getResponse().getCall());
                result.append("\n\n{code}\n");
                result.append(jsonFormatOfReply(entry.getValue().getResponse())).append("\n");
                result.append("{code}\n\n");
            }
        }
        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Vector<String> column = new Vector<String>();
        //add headerline
        column.add("hoster");
        column.add("owner");
        column.add("label");
        column.add("type");
        column.add("callType");
        column.add("call");
        column.add("code");
        column.add("hasError");
        column.add("payload");
        column.add("reply");
        column.add("replyObjects.size()");
        column.add("isErrorMessage");
        column.add("callFailed");
        column.add("noDataFound");
        column.add("noDataEntryFound");
        column.add("remarks");
        column.add("time for call (ms)");
        column.add("notification sent");
        column.add("plausible");
        result.append(getSemicolonString(column));
        //results
        for (Map.Entry<TestReference, TestResult> entry : testCollection.entrySet()) {
            column.clear();
            column.add(entry.getKey().hoster);
            column.add(entry.getKey().owner);
            column.add("" + entry.getKey().testCase);
            column.add(entry.getKey().type+"");
            column.add(entry.getKey().callType.toString());
            if (entry.getValue().getResponse() != null) {
                column.add(entry.getValue().getResponse().getCall());
                column.add(String.valueOf(entry.getValue().getResponse().getCode()));
                column.add(String.valueOf(entry.getValue().getResponse().hasError()));
                column.add(String.valueOf(entry.getValue().getResponse().getPayload()));
                String reply = String.valueOf(entry.getValue().getResponse().getReply());
                reply = reply.replaceAll("\\n", "");
                if(reply.length()>1000){
                	reply = reply.substring(0, 1000);
                }
                column.add(reply);
                if ((entry.getValue().getResponse().replyObjects != null)&&(!entry.getValue().getResponse().replyObjects.isEmpty())) {
                    column.add(entry.getValue().getResponse().replyObjects.size() + "");
                } else {
                    column.add("0");
                }
                column.add(String.valueOf(entry.getValue().getResponse().isErrorMessage));
                column.add(String.valueOf(entry.getValue().getResponse().callFailed));
                column.add(String.valueOf(entry.getValue().getResponse().noDataFound));
                column.add(String.valueOf(entry.getValue().getResponse().noDataEntryFound));                                
                column.add(entry.getValue().getRemark());
                column.add(entry.getValue().getNeededTime()+ "");
                column.add(entry.getValue().getNotificationSent());
                column.add(String.valueOf(entry.getValue().isPlausible()));
            }
            result.append(getSemicolonString(column));
        }
        return result.toString();
    }
    
    void addResponse(TestReference testRef, TestResult testRes) {
        testCollection.put(testRef, testRes);
    }
    
    public Map<TestReference, TestResult> getTestCollection(){
    	return testCollection;
    }
    
}
