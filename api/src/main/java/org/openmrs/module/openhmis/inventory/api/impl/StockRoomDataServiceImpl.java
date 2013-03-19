package org.openmrs.module.openhmis.inventory.api.impl;

import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.model.StockRoom;
import org.openmrs.module.openhmis.inventory.api.security.BasicMetadataAuthorizationPrivileges;

public class StockRoomDataServiceImpl
		extends BaseMetadataDataServiceImpl<StockRoom> {
	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return new BasicMetadataAuthorizationPrivileges();
	}

	@Override
	protected void validate(StockRoom object) throws APIException {
	}
}
