package org.openmrs.module.openhmis.inventory.api.model;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.openhmis.inventory.api.WellKnownOperationTypes;

import com.google.common.collect.Iterators;

public class DisposedOperationTypeTest extends BaseOperationTypeTest {
	@Test
	public void onPending_shouldNegateQuantityAndSetStockroomAndPatient() throws Exception {
		IStockOperationType operationType = WellKnownOperationTypes.getDisposed();
		StockOperation stockOperation = stockOperationDataService.getById(6);

		Assert.assertEquals(StockOperationStatus.NEW, stockOperation.getStatus());
		Assert.assertNotNull(stockOperation.getItems());
		Assert.assertEquals(1, stockOperation.getItems().size());

		StockOperationItem item = Iterators.getOnlyElement(stockOperation.getItems().iterator());
		Assert.assertNotNull(item);
		Assert.assertEquals(5, (int)item.getQuantity());

		ItemStock itemStock = stockroomDataService.getItem(stockOperation.getSource(), item.getItem());
		Assert.assertNotNull(itemStock);
		int itemStockQty = itemStock.getQuantity();

		// Apply the operation
		operationType.onPending(stockOperation);

		// The item stock quantity should be the original quantity minus the disposed quantity
		itemStock = stockroomDataService.getItem(stockOperation.getSource(), item.getItem());
		Assert.assertNotNull(itemStock);
		Assert.assertEquals(itemStockQty - item.getQuantity(), itemStock.getQuantity());

		// A single operation transaction should have been created
		Assert.assertEquals(1, stockOperation.getTransactions().size());
		StockOperationTransaction transaction = Iterators.getOnlyElement(stockOperation.getTransactions().iterator());
		Assert.assertNotNull(transaction);
		Assert.assertEquals(-5, (int)transaction.getQuantity());

		// A single reservation transactions should exist
		Assert.assertEquals(1, stockOperation.getReserved().size());
		ReservedTransaction reservedTransaction = Iterators.getOnlyElement(stockOperation.getReserved().iterator());
		Assert.assertNotNull(reservedTransaction);
		Assert.assertEquals(5, (int)reservedTransaction.getQuantity());
	}

	@Test
	public void onCancelled_shouldSetStockroomAndNegateQuantity() throws Exception {
		IStockOperationType operationType = WellKnownOperationTypes.getDisposed();
		StockOperation stockOperation = stockOperationDataService.getById(6);

		Assert.assertEquals(StockOperationStatus.NEW, stockOperation.getStatus());

		StockOperationItem item = Iterators.getOnlyElement(stockOperation.getItems().iterator());
		ItemStock itemStock = stockroomDataService.getItem(stockOperation.getSource(), item.getItem());
		Assert.assertNotNull(itemStock);
		int itemStockQty = itemStock.getQuantity();

		// First, apply the operation
		operationType.onPending(stockOperation);

		// The item stock quantity should be the original quantity minus the disposed quantity
		itemStock = stockroomDataService.getItem(stockOperation.getSource(), item.getItem());
		Assert.assertNotNull(itemStock);
		Assert.assertEquals(itemStockQty - item.getQuantity(), itemStock.getQuantity());

		// Now cancel the operation
		operationType.onCancelled(stockOperation);

		// The item stock quantity should be back to the original value
		itemStock = stockroomDataService.getItem(stockOperation.getSource(), item.getItem());
		Assert.assertNotNull(itemStock);
		Assert.assertEquals(itemStockQty, itemStock.getQuantity());

		// Two operation transactions should have been created, the first removing the quantity, the next adding it back
		Assert.assertEquals(2, stockOperation.getTransactions().size());
		StockOperationTransaction transaction = Iterators.get(stockOperation.getTransactions().iterator(), 0);
		Assert.assertEquals(-5, (int)transaction.getQuantity());
		transaction = Iterators.get(stockOperation.getTransactions().iterator(), 1);
		Assert.assertEquals(5, (int)transaction.getQuantity());

		// The reservation transactions should be empty
		Assert.assertEquals(0, stockOperation.getReserved().size());
	}

	@Test
	public void onCompleted_shouldClearReservedTransactions() throws Exception {
		IStockOperationType operationType = WellKnownOperationTypes.getDisposed();
		StockOperation stockOperation = stockOperationDataService.getById(6);

		Assert.assertEquals(StockOperationStatus.NEW, stockOperation.getStatus());

		StockOperationItem item = Iterators.getOnlyElement(stockOperation.getItems().iterator());
		ItemStock itemStock = stockroomDataService.getItem(stockOperation.getSource(), item.getItem());
		Assert.assertNotNull(itemStock);
		int itemStockQty = itemStock.getQuantity();

		// First, apply the operation
		operationType.onPending(stockOperation);

		// The item stock quantity should be the original quantity minus the disposed quantity
		itemStock = stockroomDataService.getItem(stockOperation.getSource(), item.getItem());
		Assert.assertEquals(itemStockQty - item.getQuantity(), itemStock.getQuantity());

		Assert.assertEquals(1, stockOperation.getTransactions().size());
		Assert.assertEquals(1, stockOperation.getReserved().size());

		// Now complete the operation
		operationType.onCompleted(stockOperation);

		// The item stock quantity remains the same
		itemStock = stockroomDataService.getItem(stockOperation.getSource(), item.getItem());
		Assert.assertEquals(itemStockQty - item.getQuantity(), itemStock.getQuantity());

		// The operation transaction remains but the reservation is deleted
		Assert.assertEquals(1, stockOperation.getTransactions().size());
		Assert.assertEquals(0, stockOperation.getReserved().size());
	}
}
