package org.openmrs.module.openhmis.inventory.api.model;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.api.IItemDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationTransactionDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationTypeDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataServiceTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class TransferOperationTypeTest extends BaseModuleContextSensitiveTest {

    IStockOperationTypeDataService stockOperationTypeDataService;
    IStockOperationDataService stockOperationDataService;
    IStockOperationTransactionDataService stockOperationTransactionDataService;

    @Before
    public void before() throws Exception {
        executeDataSet(IItemDataServiceTest.ITEM_DATASET);
        executeDataSet(IStockroomDataServiceTest.DATASET);

        stockOperationTypeDataService = Context.getService(IStockOperationTypeDataService.class);
        stockOperationDataService = Context.getService(IStockOperationDataService.class);
    }

    @Test
    public void onPending_shouldNegateQuantityAndSetStockroom() throws Exception {
        TransferOperationType transferOperationType = (TransferOperationType) stockOperationTypeDataService.getById(5);
        StockOperation stockOperation = stockOperationDataService.getById(8);

        transferOperationType.onPending(stockOperation);
        Set<StockOperationTransaction> transactions = stockOperation.getTransactions();
        assertTrue(transactions.size() == 1);
        for (StockOperationTransaction transaction : transactions) {
            assertTrue(transaction.getStockroom().getId() == 3);
            assertTrue(transaction.getQuantity() == -5);
        }
    }

    @Test
    public void onCancelled_shouldSetStockroom() throws Exception {
        TransferOperationType transferOperationType = (TransferOperationType) stockOperationTypeDataService.getById(5);
        StockOperation stockOperation = stockOperationDataService.getById(8);

        transferOperationType.onCancelled(stockOperation);
        Set<StockOperationTransaction> transactions = stockOperation.getTransactions();
        assertTrue(transactions.size() == 1);
        for (StockOperationTransaction transaction : transactions) {
            assertTrue(transaction.getStockroom().getId() == 3);
            assertTrue(transaction.getQuantity() == 5);
        }
    }

    @Test
    public void onCompleted_shouldSetDestinationStockroom() throws Exception {
        TransferOperationType transferOperationType = (TransferOperationType) stockOperationTypeDataService.getById(5);
        StockOperation stockOperation = stockOperationDataService.getById(8);

        transferOperationType.onCompleted(stockOperation);
        Set<StockOperationTransaction> transactions = stockOperation.getTransactions();
        assertTrue(transactions.size() == 1);
        for (StockOperationTransaction transaction : transactions) {
            assertTrue(transaction.getStockroom().getId() == 4);
            assertTrue(transaction.getQuantity() == 5);
        }
    }

}
