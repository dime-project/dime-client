package eu.dime.mobile.helper.objects;

import eu.dime.model.displayable.ProfileItem;

public class ResultObjectProfileSharing extends ResultObject {
	
	private ProfileItem profile;

	public ResultObjectProfileSharing(ProfileItem profile) {
		this.type = RESULT_OBJECT_TYPES.SHARING_PROFILE;
		this.profile = profile;
	}
	
	public ProfileItem getProfile() {
		return profile;
	}
	
}