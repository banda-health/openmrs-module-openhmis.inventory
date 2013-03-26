package org.openmrs.module.openhmis.inventory.api.impl;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.IStockRoomDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockRoom;
import org.openmrs.module.openhmis.inventory.api.security.BasicMetadataAuthorizationPrivileges;

import java.util.ArrayList;
import java.util.Collection;

public class StockRoomDataServiceImpl
		extends BaseMetadataDataServiceImpl<StockRoom>
		implements IStockRoomDataService {
	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return new BasicMetadataAuthorizationPrivileges();
	}

	@Override
	protected void validate(StockRoom object) throws APIException {
	}

	@Override
	protected Collection<? extends OpenmrsObject> getRelatedObjects(StockRoom entity) {
		ArrayList<OpenmrsObject> results = new ArrayList<OpenmrsObject>();

		results.addAll(entity.getTransactions());
		results.addAll(entity.getItems());

		return results;
	}
}
