package org.openmrs.module.openhmis.inventory.api.model;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

public class ReceiptOperationTypeTest extends BaseOperationTypeTest {
	@Test
	public void onCancelled_shouldClearReservedTransactions() throws Exception {
		ReceiptOperationType receiptOperationType = (ReceiptOperationType)stockOperationTypeDataService.getById(7);
		StockOperation stockOperation = stockOperationDataService.getById(4);

		assertTrue(stockOperation.getReserved().size() == 1);
		receiptOperationType.onCancelled(stockOperation);
		assertTrue(stockOperation.getReserved().size() == 0);
	}

	@Test
	public void onCompleted_shouldSetDestinationStockroom() throws Exception {
		ReceiptOperationType receiptOperationType = (ReceiptOperationType)stockOperationTypeDataService.getById(7);
		StockOperation stockOperation = stockOperationDataService.getById(4);

		receiptOperationType.onCompleted(stockOperation);
		Set<StockOperationTransaction> transactions = stockOperation.getTransactions();
		assertTrue(transactions.size() == 1);
		for (StockOperationTransaction transaction : transactions) {
			assertTrue(transaction.getStockroom().getId() == 4);
		}
	}
}
