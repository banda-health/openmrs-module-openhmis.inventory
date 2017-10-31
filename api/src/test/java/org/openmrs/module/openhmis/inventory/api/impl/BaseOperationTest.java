/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.api.impl;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.BaseModuleContextTest;
import org.openmrs.module.openhmis.inventory.api.IDepartmentDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.IItemDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.IItemStockDataService;
import org.openmrs.module.openhmis.inventory.api.IItemStockDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.ITestableStockOperationService;
import org.openmrs.module.openhmis.inventory.api.TestConstants;

public abstract class BaseOperationTest extends BaseModuleContextTest {
	IItemDataService itemDataService;
	IStockroomDataService stockroomDataService;
	IItemStockDataService itemStockDataService;
	IStockOperationDataService operationDataService;
	ITestableStockOperationService service;

	IItemDataServiceTest itemTest;
	IStockOperationDataServiceTest operationTest;
	IStockroomDataServiceTest stockroomTest;
	IItemStockDataServiceTest itemStockTest;

	StockOperationServiceImplTest stockOperationServiceImplTest;

	@Before
	public void before() throws Exception {
		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IDepartmentDataServiceTest.DEPARTMENT_DATASET);
		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(IStockroomDataServiceTest.DATASET);

		stockroomDataService = Context.getService(IStockroomDataService.class);
		itemDataService = Context.getService(IItemDataService.class);
		itemStockDataService = Context.getService(IItemStockDataService.class);
		operationDataService = Context.getService(IStockOperationDataService.class);
		service = Context.getService(ITestableStockOperationService.class);

		itemTest = new IItemDataServiceTest();
		operationTest = new IStockOperationDataServiceTest();
		stockroomTest = new IStockroomDataServiceTest();
		itemStockTest = new IItemStockDataServiceTest();
		stockOperationServiceImplTest = new StockOperationServiceImplTest();
	}
}
