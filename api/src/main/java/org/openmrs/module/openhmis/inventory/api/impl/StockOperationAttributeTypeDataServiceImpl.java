package org.openmrs.module.openhmis.inventory.api.impl;

import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.IStockOperationAttributeTypeDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationAttributeType;

public class StockOperationAttributeTypeDataServiceImpl
	extends BaseMetadataDataServiceImpl<StockOperationAttributeType>
	implements IStockOperationAttributeTypeDataService {

	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return null;
	}

	@Override
	protected void validate(StockOperationAttributeType object) throws APIException {
	}
}
