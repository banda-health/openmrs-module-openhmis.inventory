package org.openmrs.module.openhmis.inventory.api.impl;

import org.junit.Assert;
import org.junit.Test;

public class StockOperationDataServiceImplTest {
	/**
	 * @verifies throw an APIException if the type requires a source and the source is null
	 * @see StockOperationDataServiceImpl#validate(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void validate_shouldThrowAnAPIExceptionIfTheTypeRequiresASourceAndTheSourceIsNull() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @verifies throw an APIException if the type requires a destination and the destination is null
	 * @see StockOperationDataServiceImpl#validate(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void validate_shouldThrowAnAPIExceptionIfTheTypeRequiresADestinationAndTheDestinationIsNull() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @verifies throw an APIException if the type requires a patient and the patient is null
	 * @see StockOperationDataServiceImpl#validate(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void validate_shouldThrowAnAPIExceptionIfTheTypeRequiresAPatientAndThePatientIsNull() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @verifies use closest expiration from the source stock room
	 * @see StockOperationDataServiceImpl#calculateQualifiers(org.openmrs.module.openhmis.inventory.api.model.StockOperation, org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction)
	 */
	@Test
	public void calculateQualifiers_shouldUseClosestExpirationFromTheSourceStockRoom() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @verifies use oldest batch operation with the calculated expiration
	 * @see StockOperationDataServiceImpl#calculateQualifiers(org.openmrs.module.openhmis.inventory.api.model.StockOperation, org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction)
	 */
	@Test
	public void calculateQualifiers_shouldUseOldestBatchOperationWithTheCalculatedExpiration() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @verifies set the expiration to null if no valid item stock can be found
	 * @see StockOperationDataServiceImpl#calculateQualifiers(org.openmrs.module.openhmis.inventory.api.model.StockOperation, org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction)
	 */
	@Test
	public void calculateQualifiers_shouldSetTheExpirationToNullIfNoValidItemStockCanBeFound() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @verifies set the batch to null if no valid item stock can be found
	 * @see StockOperationDataServiceImpl#calculateQualifiers(org.openmrs.module.openhmis.inventory.api.model.StockOperation, org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction)
	 */
	@Test
	public void calculateQualifiers_shouldSetTheBatchToNullIfNoValidItemStockCanBeFound() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @verifies throw IllegalArgumentException if operation is null
	 * @see StockOperationDataServiceImpl#calculateQualifiers(org.openmrs.module.openhmis.inventory.api.model.StockOperation, org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction)
	 */
	@Test
	public void calculateQualifiers_shouldThrowIllegalArgumentExceptionIfOperationIsNull() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}

	/**
	 * @verifies throw IllegalArgumentException if transaction is null
	 * @see StockOperationDataServiceImpl#calculateQualifiers(org.openmrs.module.openhmis.inventory.api.model.StockOperation, org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction)
	 */
	@Test
	public void calculateQualifiers_shouldThrowIllegalArgumentExceptionIfTransactionIsNull() throws Exception {
		//TODO auto-generated
		Assert.fail("Not yet implemented");
	}
}
