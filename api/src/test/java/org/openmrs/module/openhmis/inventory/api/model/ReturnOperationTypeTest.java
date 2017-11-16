package org.openmrs.module.openhmis.inventory.api.model;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

public class ReturnOperationTypeTest extends BaseOperationTypeTest {
	@Test
	public void onCancelled_shouldClearReservedTransactions() throws Exception {
		ReturnOperationType returnOperationType = (ReturnOperationType)stockOperationTypeDataService.getById(4);
		StockOperation stockOperation = stockOperationDataService.getById(3);

		assertTrue(stockOperation.getReserved().size() == 1);
		returnOperationType.onCancelled(stockOperation);
		assertTrue(stockOperation.getReserved().size() == 0);
	}

	@Test
	public void onCompleted_shouldSetDestinationStockroom() throws Exception {
		ReturnOperationType returnOperationType = (ReturnOperationType)stockOperationTypeDataService.getById(4);
		StockOperation stockOperation = stockOperationDataService.getById(3);

		returnOperationType.onCompleted(stockOperation);
		Set<StockOperationTransaction> transactions = stockOperation.getTransactions();
		assertTrue(transactions.size() == 1);
		for (StockOperationTransaction transaction : transactions) {
			assertTrue(transaction.getStockroom().getId() == 4);
		}
	}
}
