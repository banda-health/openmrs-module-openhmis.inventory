package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.module.openhmis.inventory.api.model.StockOperation;

public interface ITestableStockOperationDataService extends IStockOperationDataService {
	void calculateReservations(StockOperation operation);
}

