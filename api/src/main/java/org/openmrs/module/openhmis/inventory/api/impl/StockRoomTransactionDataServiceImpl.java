package org.openmrs.module.openhmis.inventory.api.impl;

import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseCustomizableMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.IStockRoomTransactionDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction;
import org.openmrs.module.openhmis.inventory.api.security.TransactionAuthorizationPrivileges;

public class StockRoomTransactionDataServiceImpl
		extends BaseCustomizableMetadataDataServiceImpl<StockRoomTransaction>
		implements IStockRoomTransactionDataService {
	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return new TransactionAuthorizationPrivileges();
	}

	@Override
	protected void validate(StockRoomTransaction object) throws APIException {
	}
}
