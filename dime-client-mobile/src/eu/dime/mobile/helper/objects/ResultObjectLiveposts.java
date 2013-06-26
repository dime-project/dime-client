package eu.dime.mobile.helper.objects;

import java.util.List;
import eu.dime.model.displayable.LivePostItem;

public class ResultObjectLiveposts extends ResultObject {
	
	private List<LivePostItem> liveposts;

	public ResultObjectLiveposts(List<LivePostItem> liveposts) {
		this.type = RESULT_OBJECT_TYPES.SHARING_LIVEPOSTS;
		this.liveposts = liveposts;
	}

	public List<LivePostItem> getLiveposts() {
		return liveposts;
	}
	
}