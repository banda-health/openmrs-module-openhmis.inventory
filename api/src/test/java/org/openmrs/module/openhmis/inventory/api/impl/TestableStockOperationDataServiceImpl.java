package org.openmrs.module.openhmis.inventory.api.impl;

import org.openmrs.module.openhmis.inventory.api.IItemStockDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.ITestableStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.springframework.beans.factory.annotation.Autowired;

public class TestableStockOperationDataServiceImpl
		extends StockOperationDataServiceImpl
		implements ITestableStockOperationDataService {

	@Autowired
	public TestableStockOperationDataServiceImpl(IStockroomDataService stockroomService, IItemStockDataService itemStockService) {
		super(stockroomService, itemStockService);
	}

	@Override
	protected Class<StockOperation> getEntityClass() {
		return StockOperation.class;
	}
}
