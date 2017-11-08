package org.openmrs.module.openhmis.inventory.api.model;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

public class AdjustmentOperationTypeTest extends BaseOperationTypeTest {
	@Test
	public void onPending_shouldNegateQuantityAndSetStockroomAndPatient() throws Exception {
		AdjustmentOperationType adjustmentOperationType = (AdjustmentOperationType)stockOperationTypeDataService.getById(0);
		StockOperation stockOperation = stockOperationDataService.getById(7);

		adjustmentOperationType.onPending(stockOperation);
		Set<StockOperationTransaction> transactions = stockOperation.getTransactions();
		assertTrue(transactions.size() == 1);
		for (StockOperationTransaction transaction : transactions) {
			assertTrue(transaction.getStockroom().getId() == 3);
		}
	}

	@Test
	public void onCancelled_shouldSetStockroomAndNegateQuantity() throws Exception {
		AdjustmentOperationType adjustmentOperationType = (AdjustmentOperationType)stockOperationTypeDataService.getById(0);
		StockOperation stockOperation = stockOperationDataService.getById(7);

		adjustmentOperationType.onCancelled(stockOperation);
		Set<StockOperationTransaction> transactions = stockOperation.getTransactions();
		assertTrue(transactions.size() == 1);
		for (StockOperationTransaction transaction : transactions) {
			assertTrue(transaction.getStockroom().getId() == 3);
			assertTrue(transaction.getQuantity() == -5);
		}
	}

	@Test
	public void onCompleted_shouldClearReservedTransactions() throws Exception {
		AdjustmentOperationType adjustmentOperationType = (AdjustmentOperationType)stockOperationTypeDataService.getById(0);
		StockOperation stockOperation = stockOperationDataService.getById(7);

		assertTrue(stockOperation.getReserved().size() == 1);
		adjustmentOperationType.onCompleted(stockOperation);
		assertTrue(stockOperation.getReserved().size() == 0);
	}
}
