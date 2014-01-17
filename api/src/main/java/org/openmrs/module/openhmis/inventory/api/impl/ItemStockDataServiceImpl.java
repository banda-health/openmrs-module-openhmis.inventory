package org.openmrs.module.openhmis.inventory.api.impl;

import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseObjectDataServiceImpl;
import org.openmrs.module.openhmis.inventory.api.IItemStockDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.security.BasicObjectAuthorizationPrivileges;

public class ItemStockDataServiceImpl
		extends BaseObjectDataServiceImpl<ItemStock, BasicObjectAuthorizationPrivileges>
		implements IItemStockDataService {

	@Override
	protected BasicObjectAuthorizationPrivileges getPrivileges() {
		return new BasicObjectAuthorizationPrivileges();
	}

	@Override
	protected void validate(ItemStock object) throws APIException {

	}
}
