package org.openmrs.module.openhmis.inventory.api;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.model.IStockOperationType;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTypeBase;

public class IStockOperationTypeDataServiceTest
        extends IMetadataDataServiceTest<IStockOperationTypeDataService, IStockOperationType> {

	@Override
	public void before() throws Exception {
		super.before();

		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IStockroomDataServiceTest.DATASET);
	}

	@Override
	public IStockOperationType createEntity(boolean valid) {
		IStockOperationType type = new TestOperationType();

		if (valid) {
			type.setName("Test Type");
		}

		type.setDescription("test description");
		type.setAvailableWhenReserved(true);
		type.setHasDestination(true);
		type.setHasSource(true);
		type.setHasRecipient(true);

		// TODO: Add attributes

		return type;
	}

	@Override
	protected int getTestEntityCount() {
		return 9;
	}

	@Override
	protected void updateEntityFields(IStockOperationType entity) {
		entity.setName(entity.getName() + " updated");
		entity.setDescription(entity.getDescription() + " updated");
		entity.setAvailableWhenReserved(!entity.getAvailableWhenReserved());
		entity.setHasDestination(!entity.getHasDestination());
		entity.setHasSource(!entity.getHasSource());
		entity.setHasRecipient(!entity.getHasRecipient());

		// TODO: Update attributes
	}

	@Override
	protected void assertEntity(IStockOperationType expected, IStockOperationType actual) {
		super.assertEntity(expected, actual);

		Assert.assertEquals(expected.getAvailableWhenReserved(), actual.getAvailableWhenReserved());
		Assert.assertEquals(expected.getHasDestination(), actual.getHasDestination());
		Assert.assertEquals(expected.getHasSource(), actual.getHasSource());
		Assert.assertEquals(expected.getHasRecipient(), actual.getHasRecipient());

		//TODO: assert attributes
	}

	@Test(expected = UnsupportedOperationException.class)
	@Override
	public void save_shouldReturnSavedObject() throws Exception {
		super.save_shouldReturnSavedObject();
	}

	@Test(expected = UnsupportedOperationException.class)
	@Override
	public void save_shouldCreateTheObjectSuccessfully() throws Exception {
		super.save_shouldCreateTheObjectSuccessfully();
	}

	@Test(expected = UnsupportedOperationException.class)
	@Override
	public void save_shouldValidateTheObjectBeforeSaving() throws Exception {
		super.save_shouldValidateTheObjectBeforeSaving();
	}

	@Override
	public void purge_shouldDeleteTheSpecifiedObject() throws Exception {
		IStockOperationType result = service.getById(0);
		Assert.assertNotNull(result);

		service.purge(result);
		Context.flushSession();

		result = service.getById(result.getId());
		Assert.assertNull(result);
	}

	protected class TestOperationType extends StockOperationTypeBase {
		Integer pendingCount = 0;
		Integer cancelledCount = 0;
		Integer completedCount = 0;

		@Override
		public void onPending(StockOperation operation) {
			pendingCount++;
		}

		@Override
		public void onCancelled(StockOperation operation) {
			cancelledCount++;
		}

		@Override
		public void onCompleted(StockOperation operation) {
			completedCount++;
		}

		@Override
		public boolean isNegativeItemQuantityAllowed() {
			return false;
		}
	}
}
