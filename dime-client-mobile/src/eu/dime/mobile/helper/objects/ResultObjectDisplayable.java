package eu.dime.mobile.helper.objects;

import java.util.List;
import eu.dime.model.displayable.DisplayableItem;

public class ResultObjectDisplayable extends ResultObject {
	
	private List<DisplayableItem> displayables;

	public ResultObjectDisplayable(List<DisplayableItem> groups, RESULT_OBJECT_TYPES type) {
		this.type = type;
		this.displayables = groups;
	}

	public List<DisplayableItem> getDisplayables() {
		return displayables;
	}

}
