package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.module.openhmis.inventory.api.model.StockOperation;

public interface ITestableStockOperationService extends IStockOperationService {
	void calculateReservations(StockOperation operation);
}
