package org.openmrs.module.openhmis.inventory.api.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.api.IItemDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationTypeDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.TestConstants;

public class DistributionOperationTypeTest extends BaseOperationTypeTest {
	IStockOperationTypeDataService stockOperationTypeDataService;
	IStockOperationDataService stockOperationDataService;
	Patient patient;

	@Before
	public void before() throws Exception {
		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(IStockroomDataServiceTest.DATASET);
		executeDataSet(DATASET);

		stockOperationTypeDataService = Context.getService(IStockOperationTypeDataService.class);
		stockOperationDataService = Context.getService(IStockOperationDataService.class);

		patient = Context.getPatientService().getPatient(1);
	}

	@Test
	public void onPending_shouldNegateQuantityAndSetStockroomAndPatient() throws Exception {
		DistributionOperationType distributionOperationType =
		        (DistributionOperationType)stockOperationTypeDataService.getById(2);
		StockOperation stockOperation = stockOperationDataService.getById(5);
		stockOperation.setPatient(patient);

		distributionOperationType.onPending(stockOperation);
		Set<StockOperationTransaction> transactions = stockOperation.getTransactions();
		assertTrue(transactions.size() == 1);
		for (StockOperationTransaction transaction : transactions) {
			assertEquals(3, (int)transaction.getStockroom().getId());
			assertEquals(-5, (int)transaction.getQuantity());
			assertEquals(patient.getId(), transaction.getPatient().getPatientId());
		}
	}

	@Test
	public void onCancelled_shouldSetStockroom() throws Exception {
		DistributionOperationType distributionOperationType =
		        (DistributionOperationType)stockOperationTypeDataService.getById(2);
		StockOperation stockOperation = stockOperationDataService.getById(5);

		distributionOperationType.onCancelled(stockOperation);
		Set<StockOperationTransaction> transactions = stockOperation.getTransactions();
		assertNotNull(transactions);
		assertEquals(1, transactions.size());
		for (StockOperationTransaction transaction : transactions) {
			assertEquals(3, (int)transaction.getStockroom().getId());
		}
	}

	@Test
	public void onCompleted_shouldClearReservedTransactions() throws Exception {
		DistributionOperationType distributionOperationType =
		        (DistributionOperationType)stockOperationTypeDataService.getById(2);
		StockOperation stockOperation = stockOperationDataService.getById(5);

		assertEquals(1, stockOperation.getReserved().size());
		distributionOperationType.onCompleted(stockOperation);
		assertEquals(0, stockOperation.getReserved().size());
	}

}
