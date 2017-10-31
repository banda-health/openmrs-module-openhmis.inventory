package org.openmrs.module.openhmis.inventory.api.model;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.BaseModuleContextTest;
import org.openmrs.module.openhmis.inventory.api.IItemDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationTypeDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.TestConstants;

public abstract class BaseOperationTypeTest extends BaseModuleContextTest {
	public static final String DATASET = TestConstants.BASE_DATASET_DIR + "StockOperationTest.xml";

	IStockOperationTypeDataService stockOperationTypeDataService;
	IStockOperationDataService stockOperationDataService;
	IStockroomDataService stockroomDataService;

	@Before
	public void before() throws Exception {
		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(IStockroomDataServiceTest.DATASET);
		executeDataSet(DATASET);

		stockOperationTypeDataService = Context.getService(IStockOperationTypeDataService.class);
		stockOperationDataService = Context.getService(IStockOperationDataService.class);
		stockroomDataService = Context.getService(IStockroomDataService.class);
	}
}
