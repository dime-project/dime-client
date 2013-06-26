package eu.dime.mobile.helper.objects;

import eu.dime.model.displayable.ServiceAdapterItem;

public class ResultObjectServiceAdapter extends ResultObject {
	
	private ServiceAdapterItem service;

	public ResultObjectServiceAdapter(ServiceAdapterItem service) {
		this.type = RESULT_OBJECT_TYPES.SERVICE_CONNECTION;
		this.service = service;
	}
	
	public ServiceAdapterItem getServiceAdapter() {
		return service;
	}

}
