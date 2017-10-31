package org.openmrs.module.openhmis.inventory.api.impl;

import org.openmrs.module.openhmis.inventory.api.IItemStockDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.ITestableStockOperationService;
import org.springframework.beans.factory.annotation.Autowired;

public class TestableStockOperationServiceImpl extends StockOperationServiceImpl implements ITestableStockOperationService {
	@Autowired
	public TestableStockOperationServiceImpl(IStockOperationDataService operationService,
	    IStockroomDataService stockroomService, IItemStockDataService itemStockService) {
		super(operationService, stockroomService, itemStockService);
	}
}
