/*
 *  Description of TestCollection
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 04.05.2012
 */
package eu.dime.restapi;

import eu.dime.model.CALLTYPES;
import eu.dime.model.TYPES;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * 
 * @author Simon Thiel
 */
public class TestCollectionShareItem {

	private Map<TestReference, TestResult> testCollection = new LinkedHashMap<TestReference, TestResult>();

	public static class TestReference {

		String hoster;
		String owner;
		ShareItemTest.TL testCase;
		TYPES type;
		CALLTYPES callType;

		public TestReference(String hoster, String owner, ShareItemTest.TL label, TYPES type, CALLTYPES callType) {
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

		private String remark;
		private long neededTime;
		private boolean isPlausible;

		public TestResult(boolean isPlausible, String remark, long neededTime) {
			this.remark = remark;
			this.neededTime = neededTime;
			this.isPlausible = isPlausible;
		}

		public String getRemark() {
			return remark;
		}

		public long getNeededTime() {
			return neededTime;
		}

		public boolean isPlausible() {
			return isPlausible;
		}

	}

	private String getSemicolonString(List<String> fields) {
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < fields.size(); i++) {
			String field = fields.get(i);

			// escape field if necessary
			if (field.contains(";")) {
				String oldField = field;
				field = "\""; // start with a quote
				for (int j = 0; j < oldField.length(); j++) {
					char myChar = oldField.charAt(j);
					if (myChar == '"') {
						field += "\""; // add double quotes
					}
					field += myChar;
				}
				field += "\""; // end with quotes
			}

			// add it to the string
			result.append(field);
			if (i < fields.size() - 1) {
				result.append(";");
			}
		}// for fields
		result.append("\n");
		return result.toString();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		Vector<String> column = new Vector<String>();
		// add headerline
		column.add("hoster");
		column.add("owner");
		column.add("label");
		column.add("type");
		column.add("callType");
		column.add("plausible");
		column.add("remarks");
		column.add("time for call (ms)");
		result.append(getSemicolonString(column));
		// results
		for (Map.Entry<TestReference, TestResult> entry : testCollection.entrySet()) {
			column.clear();
			column.add(entry.getKey().hoster);
			column.add(entry.getKey().owner);
			column.add("" + entry.getKey().testCase);
			column.add(entry.getKey().type + "");
			column.add(entry.getKey().callType.toString());
			column.add(String.valueOf(entry.getValue().isPlausible()));
			column.add(entry.getValue().getRemark());
			column.add(entry.getValue().getNeededTime() + "");
			result.append(getSemicolonString(column));
		}
		return result.toString();
	}

	void addResponse(TestReference testRef, TestResult testRes) {
		testCollection.put(testRef, testRes);
	}

	public Map<TestReference, TestResult> getTestCollection() {
		return testCollection;
	}

}
