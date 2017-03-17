package org.openmrs.module.openhmis.inventory.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.BaseModuleContextTest;
import org.openmrs.module.openhmis.inventory.ModuleSettings;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction;
import org.openmrs.module.openhmis.inventory.api.model.Settings;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTransaction;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

public class IStockOperationServiceTest extends BaseModuleContextTest {
	IStockOperationTypeDataService typeService;
	IStockroomDataService stockroomService;
	IItemDataService itemService;
	IStockOperationDataService operationService;
	IStockOperationService service;

	IItemDataServiceTest itemTest;
	IStockOperationDataServiceTest operationTest;

	@Before
	public void before() throws Exception {
		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(IStockroomDataServiceTest.DATASET);

		typeService = Context.getService(IStockOperationTypeDataService.class);
		stockroomService = Context.getService(IStockroomDataService.class);
		itemService = Context.getService(IItemDataService.class);
		operationService = Context.getService(IStockOperationDataService.class);
		service = Context.getService(IStockOperationService.class);

		itemTest = new IItemDataServiceTest();
		operationTest = new IStockOperationDataServiceTest();
	}

	/**
	 * @verifies not throw exception if transactions is null
	 * @see IStockOperationService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldNotThrowExceptionIfTransactionsIsNull() throws Exception {
		service.applyTransactions((StockOperationTransaction)null);
	}

	/**
	 * @verifies not throw exception if transactions is empty
	 * @see IStockOperationService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldNotThrowExceptionIfTransactionsIsEmpty() throws Exception {
		// Empty params list
		service.applyTransactions();

		// Empty list
		service.applyTransactions(new ArrayList<StockOperationTransaction>());
	}

	/**
	 * @verifies add source stockroom item stock and detail if no item stock found
	 * @see IStockOperationService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldAddSourceStockroomItemStockAndDetailIfNoItemStockFound() throws Exception {
		// Create a new item
		Item item = itemTest.createEntity(true);
		itemService.save(item);
		Item item2 = itemTest.createEntity(true);
		itemService.save(item2);
		Context.flushSession();

		// Get a stockroom
		Stockroom stockroom = stockroomService.getById(0);

		// Ensure that the stockroom does not have any item stock for the created item2
		Assert.assertNull(stockroomService.getItem(stockroom, item));
		Assert.assertNull(stockroomService.getItem(stockroom, item2));

		// Create a new empty operation
		StockOperation operation = operationTest.createEntity(true);
		if (operation.getTransactions() != null)
			operation.getTransactions().clear();
		if (operation.getReserved() != null)
			operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setDestination(stockroom);

		// Create the transactions
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockroom(stockroom);
		tx.setQuantity(10);
		tx.setBatchOperation(operation);
		tx.setOperation(operation);
		StockOperationTransaction tx2 = new StockOperationTransaction();
		tx2.setItem(item2);
		tx2.setStockroom(stockroom);
		tx2.setQuantity(20);
		tx2.setBatchOperation(operation);
		tx2.setOperation(operation);

		operation.addTransaction(tx);
		operation.addTransaction(tx2);

		operationService.save(operation);
		service.applyTransactions(tx, tx2);
		Context.flushSession();

		// Check that the stockroom now has this item stock and details
		stockroom = stockroomService.getById(0);
		ItemStock stock = stockroomService.getItem(stockroom, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item, stock.getItem());
		Assert.assertEquals(10, stock.getQuantity());

		Assert.assertNotNull(stock.getDetails());
		Assert.assertEquals(1, stock.getDetails().size());
		ItemStockDetail detail = stock.getDetails().iterator().next();
		Assert.assertEquals(item, detail.getItem());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(10, (int)detail.getQuantity());
		Assert.assertEquals(operation, detail.getBatchOperation());
		Assert.assertEquals(stock, detail.getItemStock());

		stock = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item2, stock.getItem());
		Assert.assertEquals(20, stock.getQuantity());

		Assert.assertNotNull(stock.getDetails());
		Assert.assertEquals(1, stock.getDetails().size());
		detail = stock.getDetails().iterator().next();
		Assert.assertEquals(item2, detail.getItem());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(20, (int)detail.getQuantity());
		Assert.assertEquals(operation, detail.getBatchOperation());
		Assert.assertEquals(stock, detail.getItemStock());
	}

	/**
	 * @verifies update source stockroom item stock and detail if item exists
	 * @see IStockOperationService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldUpdateSourceStockroomItemStockAndDetailIfItemExists() throws Exception {
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		StockOperation batchOperation = operationService.getById(0);

		// Get a stockroom
		Stockroom stockroom = stockroomService.getById(0);

		// Ensure that the stockroom has stock for the created items
		ItemStock stock = stockroomService.getItem(stockroom, item);
		Assert.assertNotNull(stock);
		int qty = stock.getQuantity();
		Assert.assertTrue(qty > 0);
		ItemStock stock2 = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock2);
		int qty2 = stock2.getQuantity();
		Assert.assertTrue(qty2 > 0);

		// Create a new empty operation
		StockOperation operation = operationTest.createEntity(true);
		if (operation.getTransactions() != null)
			operation.getTransactions().clear();
		if (operation.getReserved() != null)
			operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setDestination(stockroom);

		// Create the transactions
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockroom(stockroom);
		tx.setQuantity(10);
		tx.setBatchOperation(batchOperation);
		tx.setOperation(operation);
		StockOperationTransaction tx2 = new StockOperationTransaction();
		tx2.setItem(item2);
		tx2.setStockroom(stockroom);
		tx2.setQuantity(20);
		tx2.setBatchOperation(batchOperation);
		tx2.setOperation(operation);

		operation.addTransaction(tx);
		operation.addTransaction(tx2);

		operationService.save(operation);
		service.applyTransactions(tx, tx2);
		Context.flushSession();

		// Check that the stockroom has the item stock
		stockroom = stockroomService.getById(0);
		stock = stockroomService.getItem(stockroom, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item, stock.getItem());
		Assert.assertEquals(10 + qty, stock.getQuantity());

		// Check the stock detail
		Assert.assertNotNull(stock.getDetails());
		Assert.assertEquals(1, stock.getDetails().size());

		ItemStockDetail detail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertEquals(item, detail.getItem());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(qty + 10, (int)detail.getQuantity());
		Assert.assertEquals(batchOperation, detail.getBatchOperation());
		Assert.assertEquals(stock, detail.getItemStock());

		// Check that the stockroom has the item stock
		stock = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item2, stock.getItem());
		Assert.assertEquals(20 + qty2, stock.getQuantity());

		// Check the stock detail
		Assert.assertNotNull(stock.getDetails());
		Assert.assertEquals(1, stock.getDetails().size());

		detail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertEquals(item2, detail.getItem());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(qty2 + 20, (int)detail.getQuantity());
		Assert.assertEquals(batchOperation, detail.getBatchOperation());
		Assert.assertEquals(stock, detail.getItemStock());
	}

	/**
	 * @verifies add source stockroom item stock with negative quantity when transaction quantity is negative and stock not
	 *           found
	 * @see IStockOperationService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldAddSourceStockroomItemStockWithNegativeQuantityWhenTransactionQuantityIsNegativeAndStockNotFound()
	        throws Exception {
		// Create a new item
		Item item = itemTest.createEntity(true);
		itemService.save(item);
		Item item2 = itemTest.createEntity(true);
		itemService.save(item2);
		Context.flushSession();

		// Get a stockroom
		Stockroom stockroom = stockroomService.getById(0);

		// Ensure that the stockroom does not have any item stock for the created item2
		Assert.assertNull(stockroomService.getItem(stockroom, item));
		Assert.assertNull(stockroomService.getItem(stockroom, item2));

		// Create a new empty operation
		StockOperation operation = operationTest.createEntity(true);
		if (operation.getTransactions() != null)
			operation.getTransactions().clear();
		if (operation.getReserved() != null)
			operation.getReserved().clear();
		operation.setStatus(StockOperationStatus.COMPLETED);

		// Create the transactions
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockroom(stockroom);
		tx.setQuantity(-10);
		tx.setOperation(operation);
		StockOperationTransaction tx2 = new StockOperationTransaction();
		tx2.setItem(item2);
		tx2.setStockroom(stockroom);
		tx2.setQuantity(-20);
		tx2.setOperation(operation);

		operation.addTransaction(tx);
		operation.addTransaction(tx2);

		operationService.save(operation);
		service.applyTransactions(tx, tx2);
		Context.flushSession();

		// Check that the stockroom now has the item stock with negative qty
		stockroom = stockroomService.getById(0);
		ItemStock stock = stockroomService.getItem(stockroom, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item, stock.getItem());
		Assert.assertEquals(-10, stock.getQuantity());

		ItemStockDetail detail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertEquals(item, detail.getItem());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(-10, (int)detail.getQuantity());
		Assert.assertNull(detail.getBatchOperation());
		Assert.assertEquals(stock, detail.getItemStock());

		stock = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item2, stock.getItem());
		Assert.assertEquals(-20, stock.getQuantity());

		detail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertEquals(item2, detail.getItem());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(-20, (int)detail.getQuantity());
		Assert.assertNull(detail.getBatchOperation());
		Assert.assertEquals(stock, detail.getItemStock());
	}

	/**
	 * @verifies update source stockroom item stock and create detail if needed
	 * @see IStockOperationService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldUpdateSourceStockroomItemStockAndCreateDetailIfNeeded() throws Exception {
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Get a stockroom
		Stockroom stockroom = stockroomService.getById(0);

		// Ensure that the stockroom has stock for the created items
		ItemStock stock = stockroomService.getItem(stockroom, item);
		Assert.assertNotNull(stock);
		int qty = stock.getQuantity();
		Assert.assertTrue(qty > 0);
		ItemStock stock2 = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock2);
		int qty2 = stock2.getQuantity();
		Assert.assertTrue(qty2 > 0);

		// Create a new empty operation
		StockOperation operation = operationTest.createEntity(true);
		if (operation.getTransactions() != null)
			operation.getTransactions().clear();
		if (operation.getReserved() != null)
			operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setDestination(stockroom);

		// Create the transactions
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockroom(stockroom);
		tx.setQuantity(10);
		tx.setBatchOperation(operation);
		tx.setOperation(operation);
		StockOperationTransaction tx2 = new StockOperationTransaction();
		tx2.setItem(item2);
		tx2.setStockroom(stockroom);
		tx2.setQuantity(20);
		tx2.setBatchOperation(operation);
		tx2.setOperation(operation);

		operation.addTransaction(tx);
		operation.addTransaction(tx2);

		operationService.save(operation);
		service.applyTransactions(tx, tx2);
		Context.flushSession();

		// Check that the stockroom now has this item stock
		stockroom = stockroomService.getById(0);
		stock = stockroomService.getItem(stockroom, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item, stock.getItem());
		Assert.assertEquals(10 + qty, stock.getQuantity());

		Assert.assertNotNull(stock.getDetails());
		Assert.assertEquals(2, stock.getDetails().size());

		ItemStockDetail detail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertEquals(item, detail.getItem());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(qty, (int)detail.getQuantity());
		Assert.assertEquals(0, (int)detail.getBatchOperation().getId());
		Assert.assertEquals(stock, detail.getItemStock());

		detail = Iterators.get(stock.getDetails().iterator(), 1);
		Assert.assertEquals(item, detail.getItem());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(10, (int)detail.getQuantity());
		Assert.assertEquals(operation, detail.getBatchOperation());
		Assert.assertEquals(stock, detail.getItemStock());

		stock = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item2, stock.getItem());
		Assert.assertEquals(20 + qty2, stock.getQuantity());

		detail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertEquals(item2, detail.getItem());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(qty2, (int)detail.getQuantity());
		Assert.assertEquals(0, (int)detail.getBatchOperation().getId());
		Assert.assertEquals(stock, detail.getItemStock());

		detail = Iterators.get(stock.getDetails().iterator(), 1);
		Assert.assertEquals(item2, detail.getItem());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(20, (int)detail.getQuantity());
		Assert.assertEquals(operation, detail.getBatchOperation());
		Assert.assertEquals(stock, detail.getItemStock());
	}

	/**
	 * @verifies add item stock detail with no expiration or batch when item stock quantity is negative
	 * @see IStockOperationService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldAddItemStockDetailWithNoExpirationOrBatchWhenItemStockQuantityIsNegative()
	        throws Exception {
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(2);

		// Get a stockroom
		Stockroom stockroom = stockroomService.getById(0);

		// Ensure that the stockroom has stock for the item stock
		ItemStock stock = stockroomService.getItem(stockroom, item);
		Assert.assertNotNull(stock);
		Assert.assertNotNull(stock.getDetails());
		ItemStockDetail stockDetail = Iterators.get(stock.getDetails().iterator(), 0);
		int qty = stock.getQuantity();
		Assert.assertTrue(qty > 0);
		ItemStock stock2 = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock2.getDetails());
		ItemStockDetail stockDetail2 = Iterators.get(stock2.getDetails().iterator(), 0);
		Assert.assertNotNull(stock2);
		int qty2 = stock2.getQuantity();
		Assert.assertTrue(qty2 > 0);

		// Create a new empty operation
		StockOperation operation = operationTest.createEntity(true);
		if (operation.getTransactions() != null)
			operation.getTransactions().clear();
		if (operation.getReserved() != null)
			operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getDistribution());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setSource(stockroom);

		// Create the transactions to remove more than all stock for each item
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockroom(stockroom);
		tx.setQuantity((qty + 10) * -1);
		tx.setOperation(operation);
		tx.setBatchOperation(stockDetail.getBatchOperation());
		StockOperationTransaction tx2 = new StockOperationTransaction();
		tx2.setItem(item2);
		tx2.setStockroom(stockroom);
		tx2.setQuantity((qty2 + 20) * -1);
		tx2.setOperation(operation);
		tx2.setBatchOperation(stockDetail2.getBatchOperation());
		tx2.setExpiration(stockDetail2.getExpiration());

		operation.addTransaction(tx);
		operation.addTransaction(tx2);

		operationService.save(operation);
		service.applyTransactions(tx, tx2);
		Context.flushSession();

		// Check that the stockroom still has these item stock
		stockroom = stockroomService.getById(0);
		stock = stockroomService.getItem(stockroom, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-10, stock.getQuantity());

		// Check that there is a single detail with no qualifiers
		Assert.assertNotNull(stock.getDetails());
		Assert.assertEquals(1, stock.getDetails().size());
		ItemStockDetail detail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertNotNull(detail);
		Assert.assertNull(detail.getExpiration());
		Assert.assertNull(detail.getBatchOperation());
		Assert.assertEquals(-10, (int)detail.getQuantity());

		stock = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-20, stock.getQuantity());

		Assert.assertNotNull(stock.getDetails());
		Assert.assertEquals(1, stock.getDetails().size());
		detail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertNotNull(detail);
		Assert.assertNull(detail.getExpiration());
		Assert.assertNull(detail.getBatchOperation());
		Assert.assertEquals(-20, (int)detail.getQuantity());
	}

	/**
	 * @verifies remove item stock if quantity is zero
	 * @see IStockOperationService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldNotRemoveItemStockIfQuantityIsZeroButStockContainsDetails() throws Exception {
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Get a stockroom
		Stockroom stockroom = stockroomService.getById(0);

		// Ensure that the stockroom has stock for the created items
		ItemStock stock = stockroomService.getItem(stockroom, item);
		Assert.assertNotNull(stock);
		Assert.assertNotNull(stock.getDetails());
		int qty = stock.getQuantity();
		Assert.assertTrue(qty > 0);
		ItemStock stock2 = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock2);
		int qty2 = stock2.getQuantity();
		Assert.assertTrue(qty2 > 0);

		// Create a new empty operation
		StockOperation operation = operationTest.createEntity(true);
		if (operation.getTransactions() != null)
			operation.getTransactions().clear();
		if (operation.getReserved() != null)
			operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getDistribution());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setSource(stockroom);

		// Create the transactions to remove all stock for each item
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockroom(stockroom);
		tx.setQuantity(qty * -1);
		tx.setOperation(operation);
		StockOperationTransaction tx2 = new StockOperationTransaction();
		tx2.setItem(item2);
		tx2.setStockroom(stockroom);
		tx2.setQuantity(qty2 * -1);
		tx2.setOperation(operation);

		operation.addTransaction(tx);
		operation.addTransaction(tx2);

		operationService.save(operation);
		service.applyTransactions(tx, tx2);
		Context.flushSession();

		// Check that the stockroom no longer has these items
		stockroom = stockroomService.getById(0);
		stock = stockroomService.getItem(stockroom, item);
		Assert.assertNotNull(stock);
		Assert.assertNotNull(stock.getDetails());

		stock = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock);
		Assert.assertNotNull(stock.getDetails());
	}

	/**
	 * @verifies remove item stock detail if quantity is zero
	 * @see IStockOperationService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldRemoveItemStockDetailIfQuantityIsZero() throws Exception {
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Get a stockroom
		Stockroom stockroom = stockroomService.getById(0);

		// Ensure that the stockroom has stock for the created items
		ItemStock stock = stockroomService.getItem(stockroom, item);
		Assert.assertNotNull(stock);
		int qty = stock.getQuantity();
		Assert.assertTrue(qty > 0);
		Assert.assertEquals(1, stock.getDetails().size());
		ItemStockDetail stockDetail = Iterators.get(stock.getDetails().iterator(), 0);
		ItemStock stock2 = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock2);
		int qty2 = stock2.getQuantity();
		Assert.assertTrue(qty2 > 0);
		Assert.assertEquals(1, stock2.getDetails().size());
		ItemStockDetail stockDetail2 = Iterators.get(stock2.getDetails().iterator(), 0);

		// Create a new empty operation
		StockOperation operation = operationTest.createEntity(true);
		if (operation.getTransactions() != null)
			operation.getTransactions().clear();
		if (operation.getReserved() != null)
			operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getDistribution());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setSource(stockroom);

		// Create the transactions to remove all stock for each item
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockroom(stockroom);
		tx.setQuantity(qty * -1);
		tx.setOperation(operation);
		tx.setBatchOperation(stockDetail.getBatchOperation());
		StockOperationTransaction tx2 = new StockOperationTransaction();
		tx2.setItem(item2);
		tx2.setStockroom(stockroom);
		tx2.setQuantity(qty2 * -1);
		tx2.setOperation(operation);
		tx2.setBatchOperation(stockDetail2.getBatchOperation());

		operation.addTransaction(tx);
		operation.addTransaction(tx2);

		operationService.save(operation);
		service.applyTransactions(tx, tx2);
		Context.flushSession();

		// Check that the stockroom no longer has these items
		stockroom = stockroomService.getById(0);
		stock = stockroomService.getItem(stockroom, item);
		Assert.assertNull(stock);

		stock = stockroomService.getItem(stockroom, item2);
		Assert.assertNull(stock);

		// Check that the stockroom no longer has these details
		ItemStockDetail detail =
		        stockroomService.getStockroomItemDetail(stockroom, item, stockDetail.getExpiration(),
		            stockDetail.getBatchOperation());
		Assert.assertNull(detail);

		detail =
		        stockroomService.getStockroomItemDetail(stockroom, item2, stockDetail2.getExpiration(),
		            stockDetail2.getBatchOperation());
		Assert.assertNull(detail);
	}

	@Test
	public void applyTransactions_testMixedExpirableItemTransactions() throws Exception {
		Item item0 = itemService.getById(0);
		Item item1 = itemService.getById(1);
		Item item2 = itemService.getById(2);

		StockOperation batchOperation = operationService.getById(0);

		// Get a stockroom
		Stockroom stockroom = stockroomService.getById(0);

		// Ensure that the stockroom has stock for the created items
		ItemStock stock = stockroomService.getItem(stockroom, item0);
		Assert.assertNotNull(stock);
		int qty0 = stock.getQuantity();
		Assert.assertTrue(qty0 > 0);
		ItemStock stock1 = stockroomService.getItem(stockroom, item1);
		Assert.assertNotNull(stock1);
		int qty1 = stock1.getQuantity();
		Assert.assertTrue(qty1 > 0);
		ItemStock stock2 = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock2);
		int qty2 = stock2.getQuantity();
		Date date2 = Iterators.getOnlyElement(stock2.getDetails().iterator()).getExpiration();
		Assert.assertTrue(qty2 > 0);
		Assert.assertNotNull(date2);

		// Create a new empty operation
		StockOperation operation = operationTest.createEntity(true);
		if (operation.getItems() != null)
			operation.getItems().clear();
		if (operation.getTransactions() != null)
			operation.getTransactions().clear();
		if (operation.getReserved() != null)
			operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setDestination(stockroom);

		// Create the transactions
		StockOperationTransaction tx0 = new StockOperationTransaction();
		tx0.setItem(item0);
		tx0.setStockroom(stockroom);
		tx0.setQuantity(10);
		tx0.setBatchOperation(batchOperation);
		tx0.setOperation(operation);

		StockOperationTransaction tx1 = new StockOperationTransaction();
		tx1.setItem(item1);
		tx1.setStockroom(stockroom);
		tx1.setQuantity(20);
		tx1.setBatchOperation(batchOperation);
		tx1.setOperation(operation);

		StockOperationTransaction tx2 = new StockOperationTransaction();
		tx2.setItem(item2);
		tx2.setStockroom(stockroom);
		tx2.setQuantity(30);
		tx2.setBatchOperation(batchOperation);
		tx2.setExpiration(date2);
		tx2.setOperation(operation);

		operation.addTransaction(tx0);
		operation.addTransaction(tx1);
		operation.addTransaction(tx2);

		operationService.save(operation);
		service.applyTransactions(tx0, tx2, tx1);
		Context.flushSession();

		// Check that the stockroom has the item stock
		stockroom = stockroomService.getById(0);
		stock = stockroomService.getItem(stockroom, item0);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item0, stock.getItem());
		Assert.assertEquals(10 + qty0, stock.getQuantity());

		// Check the stock detail
		Assert.assertNotNull(stock.getDetails());
		Assert.assertEquals(1, stock.getDetails().size());

		ItemStockDetail detail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertEquals(item0, detail.getItem());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(qty0 + 10, (int)detail.getQuantity());
		Assert.assertEquals(batchOperation, detail.getBatchOperation());
		Assert.assertEquals(stock, detail.getItemStock());

		// Check that the stockroom has the item stock
		stock = stockroomService.getItem(stockroom, item1);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item1, stock.getItem());
		Assert.assertEquals(20 + qty1, stock.getQuantity());

		// Check the stock detail
		Assert.assertNotNull(stock.getDetails());
		Assert.assertEquals(1, stock.getDetails().size());

		detail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertEquals(item1, detail.getItem());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(qty1 + 20, (int)detail.getQuantity());
		Assert.assertEquals(batchOperation, detail.getBatchOperation());
		Assert.assertEquals(stock, detail.getItemStock());

		// Check that the stockroom has the item stock
		stock = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item2, stock.getItem());
		Assert.assertEquals(30 + qty2, stock.getQuantity());

		// Check the stock detail
		Assert.assertNotNull(stock.getDetails());
		Assert.assertEquals(1, stock.getDetails().size());

		detail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertEquals(item2, detail.getItem());
		Assert.assertEquals(date2, detail.getExpiration());
		Assert.assertEquals(qty2 + 30, (int)detail.getQuantity());
		Assert.assertEquals(batchOperation, detail.getBatchOperation());
		Assert.assertEquals(stock, detail.getItemStock());
	}

	/**
	 * @verifies update the source stockroom item stock quantities
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldUpdateTheSourceStockroomItemStockQuantities() throws Exception {
		// Get the patient this operation will be distributing to
		Patient patient = Context.getPatientService().getPatient(1);

		// Get the source stockroom
		Stockroom source = stockroomService.getById(0);
		source.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Create the operation
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getDistribution());
		operation.setSource(source);
		operation.setPatient(patient);

		// Create the operation reservations
		StockOperationItem operationItem = operation.addItem(item, 1);
		StockOperationItem operationItem2 = operation.addItem(item2, 3);

		// Get the current stockroom item quantities
		int itemQty = stockroomService.getItem(source, item).getQuantity();
		int item2Qty = stockroomService.getItem(source, item2).getQuantity();

		// Submit the operation (this will apply the item reservations)
		service.submitOperation(operation);
		Context.flushSession();

		// Check the reservation quantities
		Assert.assertEquals(1, (int)operationItem.getQuantity());
		Assert.assertEquals(3, (int)operationItem2.getQuantity());

		// Check that the reservation quantities were removed from the source stockroom
		Assert.assertEquals(itemQty - operationItem.getQuantity(), stockroomService.getItem(source, item).getQuantity());
		Assert.assertEquals(item2Qty - operationItem2.getQuantity(), stockroomService.getItem(source, item2).getQuantity());

		// Update the status to completed and submit again
		operation.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(operation);
		Context.flushSession();

		// Check that the reservation transaction were deleted
		Assert.assertNotNull(operation.getReserved());
		Assert.assertEquals(0, operation.getReserved().size());

		// Check that the operation transactions were created
		Assert.assertNotNull(operation.getTransactions());
		Assert.assertEquals(2, operation.getTransactions().size());

		// Check that the operation transaction have the correct data
		for (StockOperationTransaction operationTx : operation.getTransactions()) {
			Assert.assertEquals(patient, operationTx.getPatient());
			Assert.assertEquals(operation, operationTx.getOperation());
			Assert.assertNotNull(operationTx.getStockroom());

			if (operationTx.getItem() == item) {
				Assert.assertEquals(-1, (int)operationTx.getQuantity());
			} else if (operationTx.getItem() == item2) {
				Assert.assertEquals(-3, (int)operationTx.getQuantity());
			} else {
				Assert.fail("Unexpected transaction item.");
			}
		}
	}

	/**
	 * @verifies remove empty item stock from the source stockroom
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldRemoveEmptyItemStockFromTheSourceStockroom() throws Exception {
		// Get the patient this operation will be distributing to
		Patient patient = Context.getPatientService().getPatient(1);

		// Get the source stockroom
		Stockroom source = stockroomService.getById(0);
		source.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Create the operation
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getDistribution());
		operation.setSource(source);
		operation.setPatient(patient);

		// Get the current stockroom item quantities
		int itemQty = stockroomService.getItem(source, item).getQuantity();
		int item2Qty = stockroomService.getItem(source, item2).getQuantity();

		// Create the operation reservations for the total stockroom item quantities
		StockOperationItem operationItem = operation.addItem(item, itemQty);
		StockOperationItem operationItem2 = operation.addItem(item2, item2Qty);

		// Submit the operation (this will apply the item reservations)
		service.submitOperation(operation);
		Context.flushSession();

		// Check the reservation quantities
		Assert.assertEquals(itemQty, (int)operationItem.getQuantity());
		Assert.assertEquals(item2Qty, (int)operationItem2.getQuantity());

		// Check that the reservation quantities caused the item stock to be removed from the stockroom
		Assert.assertNull(stockroomService.getItem(source, item));
		Assert.assertNull(stockroomService.getItem(source, item2));
	}

	/**
	 * @verifies set the correct availability for the reserved stock quantity
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldSetTheCorrectAvailabilityForTheReservedStockQuantity() throws Exception {
		// Get the patient this operation will be distributing to
		Patient patient = Context.getPatientService().getPatient(1);

		// Get the source stockroom
		Stockroom source = stockroomService.getById(0);
		source.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Create the operation
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getDistribution());
		operation.setSource(source);
		operation.setPatient(patient);

		// Create the operation reservations
		ReservedTransaction tx = operation.addReserved(item, 1);
		ReservedTransaction tx2 = operation.addReserved(item2, 3);

		Assert.assertEquals(WellKnownOperationTypes.getDistribution().getAvailableWhenReserved(), tx.getAvailable());
		Assert.assertEquals(WellKnownOperationTypes.getDistribution().getAvailableWhenReserved(), tx2.getAvailable());
	}

	/**
	 * @verifies update the destination stockroom item stock quantities
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldUpdateTheDestinationStockroomItemStockQuantities() throws Exception {
		// Get the destination stockroom
		Stockroom stockroom = stockroomService.getById(0);
		stockroom.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Get the current stockroom item quantities
		int itemQty = stockroomService.getItem(stockroom, item).getQuantity();
		int item2Qty = stockroomService.getItem(stockroom, item2).getQuantity();

		// Create the operation
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(stockroom);

		// Create the operation reservations
		operation.addItem(item, 1);
		operation.addItem(item2, 3);

		// Submit the operation (this will apply the item reservations)
		service.submitOperation(operation);
		Context.flushSession();

		// Check that the destination quantities have not yet been updated
		Assert.assertEquals(itemQty, stockroomService.getItem(stockroom, item).getQuantity());
		Assert.assertEquals(item2Qty, stockroomService.getItem(stockroom, item2).getQuantity());

		operation.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(operation);
		Context.flushSession();

		// Check the destination stockroom quantities have now been updated
		Assert.assertEquals(itemQty + 1, stockroomService.getItem(stockroom, item).getQuantity());
		Assert.assertEquals(item2Qty + 3, stockroomService.getItem(stockroom, item2).getQuantity());
	}

	/**
	 * @verifies add the destination stockroom item stock if not found
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldAddTheDestinationStockroomItemStockIfNotFound() throws Exception {
		// Get the destination stockroom
		Stockroom stockroom = stockroomService.getById(2);
		stockroom.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Ensure that there is not stock for these items
		Assert.assertNull(stockroomService.getItem(stockroom, item));
		Assert.assertNull(stockroomService.getItem(stockroom, item2));

		// Create the operation
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(stockroom);

		// Create the operation reservations
		StockOperationItem operationItem = operation.addItem(item, 1);
		StockOperationItem operationItem2 = operation.addItem(item2, 3);

		// Submit the operation (this will apply the item reservations)
		service.submitOperation(operation);
		Context.flushSession();

		// We didn't get an exception so it didn't try to update the source stockroom

		// Check that the destination stock has not yet been created
		Assert.assertNull(stockroomService.getItem(stockroom, item));
		Assert.assertNull(stockroomService.getItem(stockroom, item2));

		operation.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(operation);
		Context.flushSession();

		// Check that the destination stockroom stock and quantity has now been created
		Assert.assertEquals(1, stockroomService.getItem(stockroom, item).getQuantity());
		Assert.assertEquals(3, stockroomService.getItem(stockroom, item2).getQuantity());
	}

	/**
	 * @verifies throw APIException if the operation type is receipt and expiration is not defined for expirable items
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = APIException.class)
	public void submitOperation_shouldThrowAPIExceptionIfTheOperationTypeIsReceiptAndExpirationIsNotDefinedForExpirableItems()
	        throws Exception {
		// Get the destination stockroom
		Stockroom stockroom = stockroomService.getById(2);
		stockroom.getItems();

		// Get the items for the test operation
		Item item2 = itemService.getById(2);
		Assert.assertTrue(item2.getHasExpiration());

		// Create the operation
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(stockroom);
		operation.addItem(item2, 1);

		// Submit the operation (this should throw because the item was added without an expiration)
		service.submitOperation(operation);
	}

	/**
	 * @verifies update operation status to pending if status is new
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldUpdateOperationStatusToPendingIfStatusIsNew() throws Exception {
		// Get the destination stockroom
		Stockroom stockroom = stockroomService.getById(2);
		stockroom.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);

		// Create the operation
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(stockroom);

		// Create the operation item
		operation.addItem(item, 1);

		Assert.assertEquals(StockOperationStatus.NEW, operation.getStatus());

		// Submit the operation
		service.submitOperation(operation);

		Assert.assertEquals(StockOperationStatus.PENDING, operation.getStatus());
	}

	/**
	 * @verifies create new reservations from the operation items
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldCreateNewReservationsFromTheOperationItems() throws Exception {
		// Get the destination stockroom
		Stockroom stockroom = stockroomService.getById(2);
		stockroom.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);

		// Create the operation
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(stockroom);

		// Create the operation item
		operation.addItem(item, 1);

		Assert.assertEquals(0, operation.getReserved().size());

		// Submit the operation
		service.submitOperation(operation);

		Assert.assertEquals(1, operation.getReserved().size());
		ReservedTransaction tx = Iterators.get(operation.getReserved().iterator(), 0);
		Assert.assertEquals(item, tx.getItem());
		Assert.assertEquals(1, (int)tx.getQuantity());
		Assert.assertEquals(operation, tx.getBatchOperation());
		Assert.assertFalse(tx.isCalculatedBatch());
		Assert.assertNull(tx.getExpiration());
		Assert.assertFalse(tx.isCalculatedExpiration());
	}

	/**
	 * @verifies not recreate existing reservations if submitted multiple times
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldNotRecreateExistingReservationsIfSubmittedMultipleTimes() throws Exception {
		// Get the destination stockroom
		Stockroom stockroom = stockroomService.getById(2);
		stockroom.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);

		// Create the operation
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(stockroom);

		// Create the operation item
		operation.addItem(item, 1);

		Assert.assertEquals(0, operation.getReserved().size());

		// Submit the operation
		service.submitOperation(operation);
		Assert.assertEquals(1, operation.getReserved().size());
		ReservedTransaction tx = Iterators.get(operation.getReserved().iterator(), 0);

		service.submitOperation(operation);
		Assert.assertEquals(1, operation.getReserved().size());
		Assert.assertEquals(tx, Iterators.get(operation.getReserved().iterator(), 0));
	}

	/**
	 * @verifies properly process operation as submitted for each state change
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldProperlyProcessOperationAsSubmittedForEachStateChange() throws Exception {
		// Get the destination stockroom
		Stockroom stockroom = stockroomService.getById(2);
		stockroom.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);

		// Create the operation
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(stockroom);

		// Create the operation item
		operation.addItem(item, 1);

		Assert.assertEquals(0, operation.getReserved().size());

		// Submit the operation
		service.submitOperation(operation);
		Assert.assertEquals(1, operation.getReserved().size());
		Assert.assertEquals(0, operation.getTransactions().size());

		operation.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(operation);
		Assert.assertEquals(0, operation.getReserved().size());
		Assert.assertEquals(1, operation.getTransactions().size());
	}

	/**
	 * @verifies calculate expiration if not defined for expirable item
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldCalculateExpirationIfNotDefinedForExpirableItem() throws Exception {
		// Get a stockroom with expirable item stock
		Stockroom source = stockroomService.getById(0);
		Stockroom dest = stockroomService.getById(2);

		// Get an expirable item
		Item item = itemService.getById(2);

		ItemStock sourceItemStock = stockroomService.getItem(source, item);

		Assert.assertNotNull(sourceItemStock);
		ItemStockDetail sourceItemDetail = Iterators.getOnlyElement(sourceItemStock.getDetails().iterator());
		Date exp = sourceItemDetail.getExpiration();
		Assert.assertFalse(sourceItemDetail.isCalculatedExpiration());

		// Create the operation
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setSource(source);
		operation.setDestination(dest);

		// Create the operation item
		operation.addItem(item, 2);

		Assert.assertEquals(0, operation.getReserved().size());

		// Submit the operation
		service.submitOperation(operation);
		// One reserved transaction for the item stock being transferred
		Assert.assertEquals(1, operation.getReserved().size());
		// One operation transaction for the item stock moved out of the source stockroom
		Assert.assertEquals(1, operation.getTransactions().size());

		ReservedTransaction reservedTransaction = Iterators.getOnlyElement(operation.getReserved().iterator());
		Assert.assertEquals(exp, reservedTransaction.getExpiration());
		Assert.assertTrue(reservedTransaction.isCalculatedExpiration());

		operation.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(operation);
		Assert.assertEquals(0, operation.getReserved().size());
		Assert.assertEquals(2, operation.getTransactions().size());

		StockOperationTransaction tx = Iterators.get(operation.getTransactions().iterator(), 1);
		Assert.assertEquals(exp, tx.getExpiration());
		Assert.assertTrue(tx.isCalculatedExpiration());

		ItemStock destItemStock = stockroomService.getItem(dest, item);
		Assert.assertNotNull(destItemStock);

		ItemStockDetail destItemDetail = Iterators.getOnlyElement(destItemStock.getDetails().iterator());
		Assert.assertNotNull(destItemDetail);
		Assert.assertEquals(exp, destItemDetail.getExpiration());
		Assert.assertTrue(destItemDetail.isCalculatedExpiration());
	}

	/**
	 * @verifies calculate batch operation if not defined
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldCalculateBatchOperationIfNotDefined() throws Exception {
		// Get a stockroom with expirable item stock
		Stockroom source = stockroomService.getById(0);
		Stockroom dest = stockroomService.getById(2);

		// Get an expirable item
		Item item = itemService.getById(0);

		ItemStock sourceItemStock = stockroomService.getItem(source, item);

		Assert.assertNotNull(sourceItemStock);
		ItemStockDetail sourceItemDetail = Iterators.getOnlyElement(sourceItemStock.getDetails().iterator());
		StockOperation batchOperation = sourceItemDetail.getBatchOperation();
		Assert.assertFalse(sourceItemDetail.isCalculatedBatch());

		// Create the operation
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setSource(source);
		operation.setDestination(dest);

		// Create the operation item
		operation.addItem(item, 2);

		Assert.assertEquals(0, operation.getReserved().size());

		// Submit the operation
		service.submitOperation(operation);
		Context.flushSession();

		operation = operationService.getById(operation.getId());
		// One reserved transaction for the item stock being transferred
		Assert.assertEquals(1, operation.getReserved().size());
		// One operation transaction for the item stock moved out of the source stockroom
		Assert.assertEquals(1, operation.getTransactions().size());

		ReservedTransaction reservedTransaction = Iterators.getOnlyElement(operation.getReserved().iterator());
		// Make sure that transaction was actually persisted
		Assert.assertNotNull(reservedTransaction.getId());
		Assert.assertEquals(batchOperation, reservedTransaction.getBatchOperation());
		Assert.assertTrue(reservedTransaction.isCalculatedBatch());

		operation.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(operation);
		Context.flushSession();

		operation = operationService.getById(operation.getId());
		Assert.assertEquals(0, operation.getReserved().size());
		Assert.assertEquals(2, operation.getTransactions().size());

		StockOperationTransaction tx = Iterators.get(operation.getTransactions().iterator(), 1);
		Assert.assertNotNull(tx.getId());
		Assert.assertEquals(batchOperation, tx.getBatchOperation());
		Assert.assertTrue(tx.isCalculatedBatch());

		ItemStock destItemStock = stockroomService.getItem(dest, item);
		Assert.assertNotNull(destItemStock);

		ItemStockDetail destItemDetail = Iterators.getOnlyElement(destItemStock.getDetails().iterator());
		Assert.assertNotNull(destItemDetail);
		Assert.assertEquals(batchOperation, destItemDetail.getBatchOperation());
		Assert.assertTrue(destItemDetail.isCalculatedBatch());

		List<StockOperationTransaction> transactions =
		        Context.getService(IStockOperationTransactionDataService.class).getAll();
		Assert.assertNotNull(transactions);
		Assert.assertTrue(transactions.size() > 0);
	}

	/**
	 * @verifies throw an IllegalArgumentException if the operation is null
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void submitOperation_shouldThrowAnIllegalArgumentExceptionIfTheOperationIsNull() throws Exception {
		service.submitOperation(null);
	}

	/**
	 * @verifies throw an APIException if the operation type is null
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = APIException.class)
	public void submitOperation_shouldThrowAnAPIExceptionIfTheOperationTypeIsNull() throws Exception {
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(null);

		service.submitOperation(operation);
	}

	/**
	 * @verifies throw an APIException if the operation has no operation items
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = APIException.class)
	public void submitOperation_shouldThrowAnAPIExceptionIfTheOperationHasNoOperationItems() throws Exception {
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();

		Assert.assertEquals(0, operation.getReserved().size());

		service.submitOperation(operation);
	}

	/**
	 * @verifies throw an APIException if the operation type requires a source and the source is null
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = APIException.class)
	public void submitOperation_shouldThrowAnAPIExceptionIfTheOperationTypeRequiresASourceAndTheSourceIsNull()
	        throws Exception {
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getDistribution());
		operation.setSource(null);

		Item item = itemService.getById(0);
		operation.addReserved(item, 10);

		service.submitOperation(operation);
	}

	/**
	 * @verifies throw an APIException if the operation type requires a destination and the destination is null
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = APIException.class)
	public void submitOperation_shouldThrowAnAPIExceptionIfTheOperationTypeRequiresADestinationAndTheDestinationIsNull()
	        throws Exception {
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(null);

		Item item = itemService.getById(0);
		operation.addReserved(item, 10);

		service.submitOperation(operation);
	}

	/**
	 * @verifies throw an APIException if the operation type requires a patient and the patient is null
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = APIException.class)
	public void submitOperation_shouldThrowAnAPIExceptionIfTheOperationTypeRequiresAPatientAndThePatientIsNull()
	        throws Exception {
		WellKnownOperationTypes.getDistribution().setRecipientRequired(true);

		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getDistribution());
		operation.setSource(stockroomService.getById(0));
		operation.setPatient(null);
		operation.setInstitution(null);

		Item item = itemService.getById(0);
		operation.addReserved(item, 10);

		service.submitOperation(operation);
	}

	@Test
	public void submitOperation_shouldStoreBatchAndExpEvenWhenCalculated() throws Exception {
		Item item = itemTest.createEntity(true);
		item.setHasExpiration(true);
		itemService.save(item);
		Context.flushSession();

		Stockroom stockroom0 = stockroomService.getById(0);
		Stockroom stockroom1 = stockroomService.getById(1);

		StockOperation receiptOperation = operationTest.createEntity(true);
		receiptOperation.setInstanceType(WellKnownOperationTypes.getReceipt());
		receiptOperation.setOperationNumber("Test1");
		receiptOperation.setStatus(StockOperationStatus.NEW);
		receiptOperation.setDestination(stockroom0);
		if (receiptOperation.getReserved() != null) {
			receiptOperation.getReserved().clear();
		}
		if (receiptOperation.getTransactions() != null) {
			receiptOperation.getTransactions().clear();
		}

		Date exp = new Date();
		receiptOperation.addItem(item, 10, exp, receiptOperation);

		// Submit a new op
		service.submitOperation(receiptOperation);
		Context.flushSession();

		// Complete the op
		receiptOperation.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(receiptOperation);
		Context.flushSession();

		// Check stockroom
		ItemStock stock = stockroomService.getItem(stockroom0, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(10, stock.getQuantity());

		Assert.assertNotNull(stock.getDetails());
		Assert.assertEquals(1, stock.getDetails().size());
		ItemStockDetail detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(10, (int)detail.getQuantity());
		Assert.assertEquals(false, detail.getCalculatedBatch());
		Assert.assertEquals(false, detail.getCalculatedExpiration());
		Assert.assertEquals(receiptOperation, detail.getBatchOperation());
		Assert.assertTrue(DateUtils.isSameDay(exp, detail.getExpiration()));

		// Now create a transfer operation
		StockOperation transferOperation = operationTest.createEntity(true);
		transferOperation.setInstanceType(WellKnownOperationTypes.getTransfer());
		transferOperation.setOperationNumber("Test2");
		transferOperation.setStatus(StockOperationStatus.NEW);
		transferOperation.setSource(stockroom0);
		transferOperation.setDestination(stockroom1);
		if (transferOperation.getReserved() != null) {
			transferOperation.getReserved().clear();
		}
		if (transferOperation.getTransactions() != null) {
			transferOperation.getTransactions().clear();
		}

		transferOperation.addItem(item, 10);

		// Submit the new transfer op
		service.submitOperation(transferOperation);
		Context.flushSession();

		// Complete the transfer op
		transferOperation.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(transferOperation);
		Context.flushSession();

		// Check the stockrooms
		stock = stockroomService.getItem(stockroom0, item);
		Assert.assertNull(stock);

		stock = stockroomService.getItem(stockroom1, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(10, stock.getQuantity());

		Assert.assertNotNull(stock.getDetails());
		Assert.assertEquals(1, stock.getDetails().size());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(10, (int)detail.getQuantity());
		Assert.assertEquals(true, detail.getCalculatedBatch());
		Assert.assertEquals(true, detail.getCalculatedExpiration());
		Assert.assertEquals(receiptOperation, detail.getBatchOperation());
		Assert.assertTrue(DateUtils.isSameDay(exp, detail.getExpiration()));
	}

	@Test
	public void submitOperation_shouldRollBackSubsequentPendingOperationWhenClosingOperation() throws Exception {
		Item item = itemTest.createEntity(true);
		item.setHasExpiration(true);

		itemService.save(item);
		Context.flushSession();

		Calendar now = Calendar.getInstance();

		// Create new receipt with exp and save (PENDING)
		StockOperation receipt = operationTest.createEntity(true);
		receipt.getReserved().clear();
		receipt.setInstanceType(WellKnownOperationTypes.getReceipt());
		receipt.setDestination(stockroomService.getById(0));

		now.add(Calendar.MINUTE, 1);
		receipt.setOperationDate(now.getTime());

		Calendar expCal = Calendar.getInstance();
		expCal.add(Calendar.MONTH, 3);
		receipt.addItem(item, 15, expCal.getTime());

		service.submitOperation(receipt);
		Context.flushSession();

		// Create new transfer for same item (auto exp) and save (PENDING)
		StockOperation transfer = operationTest.createEntity(true);
		transfer.getReserved().clear();
		transfer.setInstanceType(WellKnownOperationTypes.getTransfer());
		transfer.setSource(stockroomService.getById(0));
		transfer.setDestination(stockroomService.getById(1));

		now.add(Calendar.MINUTE, 1);
		transfer.setOperationDate(now.getTime());

		transfer.addItem(item, 5);

		service.submitOperation(transfer);
		Context.flushSession();

		// Refresh the operations we created
		receipt = operationService.getById(receipt.getId());
		transfer = operationService.getById(transfer.getId());

		// Transfer tx will not have exp or batch because the receipt has not been completed
		StockOperationTransaction tx = Iterators.getOnlyElement(transfer.getTransactions().iterator());
		Assert.assertEquals(item, tx.getItem());
		Assert.assertNull(tx.getExpiration());
		Assert.assertNull(tx.getBatchOperation());

		// Source SR will have a negative quantity with no exp or batch in details
		ItemStock stock = stockroomService.getItem(transfer.getSource(), item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-5, stock.getQuantity());
		ItemStockDetail detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(-5, (int)detail.getQuantity());
		Assert.assertNull(detail.getExpiration());
		Assert.assertNull(detail.getBatchOperation());

		// Complete the receipt
		receipt.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(receipt);
		Context.flushSession();

		// Refresh operations
		receipt = operationService.getById(receipt.getId());
		transfer = operationService.getById(transfer.getId());

		// Source SR should have not have neg qty detail, pending item details should be minus transfer qty
		stock = stockroomService.getItem(receipt.getDestination(), item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(10, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(10, (int)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), detail.getExpiration()));
		Assert.assertEquals(receipt, detail.getBatchOperation());

		// Transfer pending tx and tx should now have exp and batch from completed receipt
		ReservedTransaction resTx = Iterators.getOnlyElement(transfer.getReserved().iterator());
		Assert.assertEquals(item, resTx.getItem());
		Assert.assertEquals(5, (int)resTx.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), resTx.getExpiration()));
		Assert.assertEquals(receipt, resTx.getBatchOperation());

		tx = Iterators.getOnlyElement(transfer.getTransactions().iterator());
		Assert.assertEquals(item, tx.getItem());
		Assert.assertEquals(-5, (int)tx.getQuantity());
		Assert.assertEquals(transfer.getSource(), tx.getStockroom());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), tx.getExpiration()));
		Assert.assertEquals(receipt, tx.getBatchOperation());

		// Complete the transfer
		transfer.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(transfer);
		Context.flushSession();

		// Dest SR should have receipt exp and batch and be set to auto
		stock = stockroomService.getItem(transfer.getDestination(), item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(5, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(5, (int)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), detail.getExpiration()));
		Assert.assertEquals(receipt, detail.getBatchOperation());
	}

	@Test
	public void submitOperation_shouldRollBackSubsequentCompletedOperationWhenClosingOperation() throws Exception {
		Item item = itemTest.createEntity(true);
		item.setHasExpiration(true);

		itemService.save(item);
		Context.flushSession();

		Calendar now = Calendar.getInstance();

		// Create new receipt with exp and save (PENDING)
		StockOperation receipt = operationTest.createEntity(true);
		receipt.getReserved().clear();
		receipt.setInstanceType(WellKnownOperationTypes.getReceipt());
		receipt.setDestination(stockroomService.getById(0));

		now.add(Calendar.MINUTE, 1);
		receipt.setOperationDate(now.getTime());

		Calendar expCal = Calendar.getInstance();
		expCal.add(Calendar.MONTH, 3);
		receipt.addItem(item, 15, expCal.getTime());

		service.submitOperation(receipt);
		Context.flushSession();

		// Create new transfer for same item (auto exp) and save (PENDING)
		StockOperation transfer = operationTest.createEntity(true);
		transfer.getReserved().clear();
		transfer.setInstanceType(WellKnownOperationTypes.getTransfer());
		transfer.setSource(stockroomService.getById(0));
		transfer.setDestination(stockroomService.getById(1));

		now.add(Calendar.MINUTE, 1);
		transfer.setOperationDate(now.getTime());

		transfer.addItem(item, 5);

		service.submitOperation(transfer);
		Context.flushSession();

		// Refresh the operations we created
		receipt = operationService.getById(receipt.getId());
		transfer = operationService.getById(transfer.getId());

		// Transfer tx will not have exp or batch because the receipt has not been completed
		StockOperationTransaction tx = Iterators.getOnlyElement(transfer.getTransactions().iterator());
		Assert.assertEquals(item, tx.getItem());
		Assert.assertNull(tx.getExpiration());
		Assert.assertNull(tx.getBatchOperation());

		// Source SR will have a negative quantity with no exp or batch in details
		ItemStock stock = stockroomService.getItem(transfer.getSource(), item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-5, stock.getQuantity());
		ItemStockDetail detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(-5, (int)detail.getQuantity());
		Assert.assertNull(detail.getExpiration());
		Assert.assertNull(detail.getBatchOperation());

		// Complete the transfer
		transfer.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(transfer);
		Context.flushSession();

		// Refresh the transfer op
		transfer = operationService.getById(transfer.getId());

		// Dest SR should have null exp and batch and be set to auto
		stock = stockroomService.getItem(transfer.getDestination(), item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(5, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(5, (int)detail.getQuantity());
		Assert.assertNull(detail.getExpiration());
		Assert.assertNull(detail.getBatchOperation());
		Assert.assertTrue(detail.getCalculatedExpiration());
		Assert.assertTrue(detail.getCalculatedBatch());

		// Complete the receipt
		receipt.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(receipt);
		Context.flushSession();

		// Refresh operations
		receipt = operationService.getById(receipt.getId());
		transfer = operationService.getById(transfer.getId());

		// Source SR should have not have neg qty detail, pending item details should be minus transfer qty
		stock = stockroomService.getItem(receipt.getDestination(), item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(10, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(10, (int)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), detail.getExpiration()));
		Assert.assertEquals(receipt, detail.getBatchOperation());

		// Transfer tx's should now have exp and batch from completed receipt
		Assert.assertEquals(2, transfer.getTransactions().size());
		tx = Iterators.get(transfer.getTransactions().iterator(), 0);
		Assert.assertEquals(item, tx.getItem());

		// The order of the tx's is not knoen so figure it out here
		int factor = -1;
		Stockroom stockroom = transfer.getSource();
		if (tx.getQuantity() < 0) {
			Assert.assertEquals(-5, (int)tx.getQuantity());
			Assert.assertEquals(transfer.getSource(), tx.getStockroom());

			factor = 1;
			stockroom = transfer.getDestination();
		} else {
			Assert.assertEquals(5, (int)tx.getQuantity());
			Assert.assertEquals(transfer.getDestination(), tx.getStockroom());
		}

		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), tx.getExpiration()));
		Assert.assertEquals(receipt, tx.getBatchOperation());

		tx = Iterators.get(transfer.getTransactions().iterator(), 1);
		Assert.assertEquals(item, tx.getItem());
		Assert.assertEquals(5 * factor, (int)tx.getQuantity());
		Assert.assertEquals(stockroom, tx.getStockroom());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), tx.getExpiration()));
		Assert.assertEquals(receipt, tx.getBatchOperation());

		// Dest SR should have receipt exp and batch and be set to auto
		stock = stockroomService.getItem(transfer.getDestination(), item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(5, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(5, (int)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), detail.getExpiration()));
		Assert.assertEquals(receipt, detail.getBatchOperation());
		Assert.assertTrue(detail.getCalculatedExpiration());
		Assert.assertTrue(detail.getCalculatedBatch());

		// Transfer op should still be COMPLETED
		transfer = operationService.getById(transfer.getId());
		Assert.assertEquals(StockOperationStatus.COMPLETED, transfer.getStatus());
	}

	@Test
	public void submitOperation_shouldRollBackSubsequentCancelledOperationWhenClosingOperation() throws Exception {
		Item item = itemTest.createEntity(true);
		item.setHasExpiration(true);

		itemService.save(item);
		Context.flushSession();

		Calendar now = Calendar.getInstance();

		// Create new receipt with exp and save (PENDING)
		StockOperation receipt = operationTest.createEntity(true);
		receipt.getReserved().clear();
		receipt.setInstanceType(WellKnownOperationTypes.getReceipt());
		receipt.setDestination(stockroomService.getById(0));

		now.add(Calendar.MINUTE, 1);
		receipt.setOperationDate(now.getTime());

		Calendar expCal = Calendar.getInstance();
		expCal.add(Calendar.MONTH, 3);

		receipt.addItem(item, 15, expCal.getTime());

		service.submitOperation(receipt);
		Context.flushSession();

		// Create new transfer for same item (auto exp) and save (PENDING)
		StockOperation transfer = operationTest.createEntity(true);
		transfer.getReserved().clear();
		transfer.setInstanceType(WellKnownOperationTypes.getTransfer());
		transfer.setSource(stockroomService.getById(0));
		transfer.setDestination(stockroomService.getById(1));

		now.add(Calendar.MINUTE, 1);
		transfer.setOperationDate(now.getTime());

		transfer.addItem(item, 5);

		service.submitOperation(transfer);
		Context.flushSession();

		// Refresh the operations we created
		receipt = operationService.getById(receipt.getId());
		transfer = operationService.getById(transfer.getId());

		// Transfer tx will not have exp or batch because the receipt has not been completed
		StockOperationTransaction tx = Iterators.getOnlyElement(transfer.getTransactions().iterator());
		Assert.assertEquals(item, tx.getItem());
		Assert.assertNull(tx.getExpiration());
		Assert.assertNull(tx.getBatchOperation());

		// Source SR will have a negative quantity with no exp or batch in details
		ItemStock stock = stockroomService.getItem(transfer.getSource(), item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-5, stock.getQuantity());
		ItemStockDetail detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(-5, (int)detail.getQuantity());
		Assert.assertNull(detail.getExpiration());
		Assert.assertNull(detail.getBatchOperation());

		// Complete the transfer
		transfer.setStatus(StockOperationStatus.CANCELLED);
		service.submitOperation(transfer);
		Context.flushSession();

		// Refresh the transfer op
		transfer = operationService.getById(transfer.getId());

		// Dest SR should not have the item
		stock = stockroomService.getItem(transfer.getDestination(), item);
		Assert.assertNull(stock);

		// Complete the receipt
		receipt.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(receipt);
		Context.flushSession();

		// Refresh operations
		receipt = operationService.getById(receipt.getId());
		transfer = operationService.getById(transfer.getId());

		// Source SR should have not have neg qty detail, pending item details should be minus transfer qty
		stock = stockroomService.getItem(receipt.getDestination(), item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(15, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(15, (int)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), detail.getExpiration()));
		Assert.assertEquals(receipt, detail.getBatchOperation());

		// Transfer tx's should now have exp and batch from cancelled receipt
		Assert.assertEquals(2, transfer.getTransactions().size());
		tx = Iterators.get(transfer.getTransactions().iterator(), 0);
		Assert.assertEquals(item, tx.getItem());

		// The order of the tx's is not knoen so figure it out here
		int factor = -1;
		if (tx.getQuantity() < 0) {
			Assert.assertEquals(-5, (int)tx.getQuantity());

			factor = 1;
		} else {
			Assert.assertEquals(5, (int)tx.getQuantity());
		}

		Assert.assertEquals(transfer.getSource(), tx.getStockroom());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), tx.getExpiration()));
		Assert.assertEquals(receipt, tx.getBatchOperation());

		tx = Iterators.get(transfer.getTransactions().iterator(), 1);
		Assert.assertEquals(item, tx.getItem());
		Assert.assertEquals(5 * factor, (int)tx.getQuantity());
		Assert.assertEquals(transfer.getSource(), tx.getStockroom());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), tx.getExpiration()));
		Assert.assertEquals(receipt, tx.getBatchOperation());

		// Dest SR should have receipt exp and batch and be set to auto
		stock = stockroomService.getItem(transfer.getDestination(), item);
		Assert.assertNull(stock);

		// Transfer op should still be CANCELLED
		transfer = operationService.getById(transfer.getId());
		Assert.assertEquals(StockOperationStatus.CANCELLED, transfer.getStatus());
	}

	@Test
	public void submitOperation_shouldRollBackSubsequentOperationsInProperOrderWhenClosingOperation() throws Exception {
		Item item = itemTest.createEntity(true);
		item.setName("New Item 1");
		item.setHasExpiration(true);
		Item item2 = itemTest.createEntity(true);
		item2.setName("New Item 2");
		item2.setHasExpiration(true);

		itemService.save(item);
		itemService.save(item2);
		Context.flushSession();

		Calendar now = Calendar.getInstance();

		// Create new receipt with exp and save (PENDING) - R1
		StockOperation r1 = operationTest.createEntity(true);
		r1.getReserved().clear();
		r1.setInstanceType(WellKnownOperationTypes.getReceipt());
		r1.setDestination(stockroomService.getById(0));

		now.add(Calendar.MINUTE, 1);
		r1.setOperationDate(now.getTime());

		Calendar expCal = Calendar.getInstance();
		expCal.add(Calendar.MONTH, 3);
		r1.addItem(item, 15, expCal.getTime());
		Calendar expCal2 = Calendar.getInstance();
		expCal2.add(Calendar.MONTH, 6);
		r1.addItem(item2, 25, expCal2.getTime());

		service.submitOperation(r1);
		Context.flushSession();

		// Create new transfer (SR1 -> SR2) for same item (auto exp) and save (PENDING) - T1
		StockOperation t1 = operationTest.createEntity(true);
		t1.getReserved().clear();
		t1.setInstanceType(WellKnownOperationTypes.getTransfer());
		t1.setSource(stockroomService.getById(0));
		t1.setDestination(stockroomService.getById(1));

		now.add(Calendar.MINUTE, 1);
		t1.setOperationDate(now.getTime());

		t1.addItem(item, 5);
		t1.addItem(item2, 10);

		service.submitOperation(t1);
		Context.flushSession();

		// Create new transfer (SR2 -> SR3) for same item (auto exp) and save (PENDING) - T2
		StockOperation t2 = operationTest.createEntity(true);
		t2.getReserved().clear();
		t2.setInstanceType(WellKnownOperationTypes.getTransfer());
		t2.setSource(stockroomService.getById(1));
		t2.setDestination(stockroomService.getById(2));

		now.add(Calendar.MINUTE, 1);
		t2.setOperationDate(now.getTime());

		t2.addItem(item, 5);
		t2.addItem(item2, 7);

		service.submitOperation(t2);
		Context.flushSession();

		// Create new distribution (SR3 - Dept) for same item (auto exp) and save (PENDING) - D1
		StockOperation d1 = operationTest.createEntity(true);
		d1.getReserved().clear();
		d1.setInstanceType(WellKnownOperationTypes.getDistribution());
		d1.setSource(stockroomService.getById(2));
		d1.setDepartment(item.getDepartment());

		now.add(Calendar.MINUTE, 1);
		d1.setOperationDate(now.getTime());

		d1.addItem(item, 5);
		d1.addItem(item2, 3);

		service.submitOperation(d1);
		Context.flushSession();

		// Refresh the operations we created
		r1 = operationService.getById(r1.getId());
		t1 = operationService.getById(t1.getId());
		t2 = operationService.getById(t2.getId());
		d1 = operationService.getById(d1.getId());

		// T1 tx will not have exp or batch because the receipt has not been completed
		Assert.assertEquals(2, t1.getTransactions().size());
		StockOperationTransaction tx = getTransactionForItem(t1.getTransactions(), item);
		Assert.assertEquals(item, tx.getItem());
		Assert.assertEquals(-5, (int)tx.getQuantity());
		Assert.assertNull(tx.getExpiration());
		Assert.assertNull(tx.getBatchOperation());

		tx = getTransactionForItem(t1.getTransactions(), item2);
		Assert.assertEquals(item2, tx.getItem());
		Assert.assertEquals(-10, (int)tx.getQuantity());
		Assert.assertNull(tx.getExpiration());
		Assert.assertNull(tx.getBatchOperation());

		// SR1 will have a negative quantity with no exp or batch in details
		ItemStock stock = stockroomService.getItem(t1.getSource(), item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-5, stock.getQuantity());
		ItemStockDetail detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(-5, (int)detail.getQuantity());
		Assert.assertNull(detail.getExpiration());
		Assert.assertNull(detail.getBatchOperation());

		stock = stockroomService.getItem(t1.getSource(), item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-10, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item2, detail.getItem());
		Assert.assertEquals(-10, (int)detail.getQuantity());
		Assert.assertNull(detail.getExpiration());
		Assert.assertNull(detail.getBatchOperation());

		// T2 tx will not have exp or batch because T1 has not been completed
		Assert.assertEquals(2, t2.getTransactions().size());
		tx = getTransactionForItem(t2.getTransactions(), item);
		Assert.assertEquals(item, tx.getItem());
		Assert.assertEquals(-5, (int)tx.getQuantity());
		Assert.assertNull(tx.getExpiration());
		Assert.assertNull(tx.getBatchOperation());

		tx = getTransactionForItem(t2.getTransactions(), item2);
		Assert.assertEquals(item2, tx.getItem());
		Assert.assertEquals(-7, (int)tx.getQuantity());
		Assert.assertNull(tx.getExpiration());
		Assert.assertNull(tx.getBatchOperation());

		// SR2 will have a negative quantity with no exp or batch in details
		stock = stockroomService.getItem(t2.getSource(), item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-5, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(-5, (int)detail.getQuantity());
		Assert.assertNull(detail.getExpiration());
		Assert.assertNull(detail.getBatchOperation());

		stock = stockroomService.getItem(t2.getSource(), item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-7, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item2, detail.getItem());
		Assert.assertEquals(-7, (int)detail.getQuantity());
		Assert.assertNull(detail.getExpiration());
		Assert.assertNull(detail.getBatchOperation());

		// D1 tx will not have exp or batch because T2 has not been completed
		Assert.assertEquals(2, d1.getTransactions().size());
		tx = getTransactionForItem(d1.getTransactions(), item);
		Assert.assertEquals(item, tx.getItem());
		Assert.assertEquals(-5, (int)tx.getQuantity());
		Assert.assertNull(tx.getExpiration());
		Assert.assertNull(tx.getBatchOperation());

		tx = getTransactionForItem(d1.getTransactions(), item2);
		Assert.assertEquals(item2, tx.getItem());
		Assert.assertEquals(-3, (int)tx.getQuantity());
		Assert.assertNull(tx.getExpiration());
		Assert.assertNull(tx.getBatchOperation());

		// SR3 will have a negative quantity with no exp or batch in details
		stock = stockroomService.getItem(d1.getSource(), item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-5, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(-5, (int)detail.getQuantity());
		Assert.assertNull(detail.getExpiration());
		Assert.assertNull(detail.getBatchOperation());

		stock = stockroomService.getItem(d1.getSource(), item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-3, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item2, detail.getItem());
		Assert.assertEquals(-3, (int)detail.getQuantity());
		Assert.assertNull(detail.getExpiration());
		Assert.assertNull(detail.getBatchOperation());

		// Complete the receipt
		r1.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(r1);
		Context.flushSession();

		// Refresh operations
		r1 = operationService.getById(r1.getId());
		t1 = operationService.getById(t1.getId());
		t2 = operationService.getById(t2.getId());
		d1 = operationService.getById(d1.getId());

		// SR1 should have not have neg qty detail
		stock = stockroomService.getItem(r1.getDestination(), item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(10, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(10, (int)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), detail.getExpiration()));
		Assert.assertEquals(r1, detail.getBatchOperation());

		stock = stockroomService.getItem(r1.getDestination(), item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(15, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item2, detail.getItem());
		Assert.assertEquals(15, (int)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal2.getTime(), detail.getExpiration()));
		Assert.assertEquals(r1, detail.getBatchOperation());

		// T1 tx's should now have exp and batch from completed receipt
		Assert.assertEquals(2, t1.getTransactions().size());
		tx = getTransactionForItem(t1.getTransactions(), item);
		Assert.assertEquals(item, tx.getItem());
		Assert.assertEquals(-5, (int)tx.getQuantity());
		Assert.assertEquals(t1.getSource(), tx.getStockroom());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), tx.getExpiration()));
		Assert.assertEquals(r1, tx.getBatchOperation());

		tx = getTransactionForItem(t1.getTransactions(), item2);
		Assert.assertEquals(item2, tx.getItem());
		Assert.assertEquals(-10, (int)tx.getQuantity());
		Assert.assertEquals(t1.getSource(), tx.getStockroom());
		Assert.assertTrue(DateUtils.isSameDay(expCal2.getTime(), tx.getExpiration()));
		Assert.assertEquals(r1, tx.getBatchOperation());

		// Operations should still be PENDING
		Assert.assertEquals(StockOperationStatus.PENDING, t1.getStatus());
		Assert.assertEquals(StockOperationStatus.PENDING, t2.getStatus());
		Assert.assertEquals(StockOperationStatus.PENDING, d1.getStatus());

		// Complete T1
		t1.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(t1);
		Context.flushSession();

		// SR2 should not have stock for item-1 and should have item-2 stock with exp and batch
		stock = stockroomService.getItem(t1.getDestination(), item);
		Assert.assertNull(stock);

		stock = stockroomService.getItem(t1.getDestination(), item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(3, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item2, detail.getItem());
		Assert.assertEquals(3, (int)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal2.getTime(), detail.getExpiration()));
		Assert.assertEquals(r1, detail.getBatchOperation());

		// T2 tx's should now have exp and batch from completed receipt
		Assert.assertEquals(2, t2.getTransactions().size());
		tx = getTransactionForItem(t2.getTransactions(), item);
		Assert.assertEquals(item, tx.getItem());
		Assert.assertEquals(-5, (int)tx.getQuantity());
		Assert.assertEquals(t2.getSource(), tx.getStockroom());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), tx.getExpiration()));
		Assert.assertEquals(r1, tx.getBatchOperation());

		tx = getTransactionForItem(t2.getTransactions(), item2);
		Assert.assertEquals(item2, tx.getItem());
		Assert.assertEquals(-7, (int)tx.getQuantity());
		Assert.assertEquals(t2.getSource(), tx.getStockroom());
		Assert.assertTrue(DateUtils.isSameDay(expCal2.getTime(), tx.getExpiration()));
		Assert.assertEquals(r1, tx.getBatchOperation());

		// Complete T2
		t2.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(t2);
		Context.flushSession();

		// SR3 should not have stock for item-1 and should have item-2 stock with exp and batch
		stock = stockroomService.getItem(t2.getDestination(), item);
		Assert.assertNull(stock);

		stock = stockroomService.getItem(t2.getDestination(), item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(4, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(item2, detail.getItem());
		Assert.assertEquals(4, (int)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal2.getTime(), detail.getExpiration()));
		Assert.assertEquals(r1, detail.getBatchOperation());

		// D1 tx's should now have exp and batch from completed receipt
		Assert.assertEquals(2, d1.getTransactions().size());
		tx = getTransactionForItem(d1.getTransactions(), item);
		Assert.assertEquals(item, tx.getItem());
		Assert.assertEquals(-5, (int)tx.getQuantity());
		Assert.assertEquals(d1.getSource(), tx.getStockroom());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), tx.getExpiration()));
		Assert.assertEquals(r1, tx.getBatchOperation());

		tx = getTransactionForItem(d1.getTransactions(), item2);
		Assert.assertEquals(item2, tx.getItem());
		Assert.assertEquals(-3, (int)tx.getQuantity());
		Assert.assertEquals(d1.getSource(), tx.getStockroom());
		Assert.assertTrue(DateUtils.isSameDay(expCal2.getTime(), tx.getExpiration()));
		Assert.assertEquals(r1, tx.getBatchOperation());

		// Complete D1
		d1.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(d1);
		Context.flushSession();

		// Check each stockroom
		Stockroom sr0 = stockroomService.getById(0);
		Stockroom sr1 = stockroomService.getById(1);
		Stockroom sr2 = stockroomService.getById(2);

		stock = stockroomService.getItem(sr0, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(10, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(10, (int)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal.getTime(), detail.getExpiration()));
		Assert.assertEquals(r1, detail.getBatchOperation());
		Assert.assertFalse(detail.getCalculatedExpiration());
		Assert.assertFalse(detail.getCalculatedBatch());

		stock = stockroomService.getItem(sr0, item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(15, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(15, (int)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal2.getTime(), detail.getExpiration()));
		Assert.assertEquals(r1, detail.getBatchOperation());
		Assert.assertFalse(detail.getCalculatedExpiration());
		Assert.assertFalse(detail.getCalculatedBatch());

		stock = stockroomService.getItem(sr1, item);
		Assert.assertNull(stock);

		stock = stockroomService.getItem(sr1, item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(3, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(3, (int)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal2.getTime(), detail.getExpiration()));
		Assert.assertEquals(r1, detail.getBatchOperation());
		Assert.assertTrue(detail.getCalculatedExpiration());
		Assert.assertTrue(detail.getCalculatedBatch());

		stock = stockroomService.getItem(sr2, item);
		Assert.assertNull(stock);

		stock = stockroomService.getItem(sr2, item2);
		Assert.assertNotNull(stock);
		Assert.assertEquals(4, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(4, (int)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(expCal2.getTime(), detail.getExpiration()));
		Assert.assertEquals(r1, detail.getBatchOperation());
		Assert.assertTrue(detail.getCalculatedExpiration());
		Assert.assertTrue(detail.getCalculatedBatch());
	}

	@Test
	public void submitOperation_shouldNotAutoCompleteOperationIfPropertyNotTrue() throws Exception {
		Item item = itemTest.createEntity(true);
		itemService.save(item);
		Context.flushSession();

		Stockroom sr = stockroomService.getById(0);
		StockOperation operation = operationTest.createEntity(true);

		operation.setStatus(StockOperationStatus.NEW);
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.getReserved().clear();
		operation.getTransactions().clear();
		operation.setDestination(sr);

		operation.addItem(item, 100);

		// Make sure that autocomplete setting is not true
		Assert.assertFalse(ModuleSettings.loadSettings().getAutoCompleteOperations());

		operation = service.submitOperation(operation);
		Context.flushSession();

		// Check that the operation is pending and not completed
		Assert.assertEquals(StockOperationStatus.PENDING, operation.getStatus());
		Assert.assertEquals(1, operation.getReserved().size());
		Assert.assertEquals(0, operation.getTransactions().size());

		// Check that the proper reservation has been created
		ReservedTransaction tx = Iterators.getOnlyElement(operation.getReserved().iterator());
		Assert.assertEquals(item, tx.getItem());
		Assert.assertEquals(100, (int)tx.getQuantity());

		// Check that the destination stockroom has not been updated
		ItemStock stock = stockroomService.getItem(sr, item);
		Assert.assertNull(stock);
	}

	@Test
	public void submitOperation_shouldAutoCompleteOperationIfPropertyTrue() throws Exception {
		Item item = itemTest.createEntity(true);
		itemService.save(item);
		Context.flushSession();

		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		Stockroom sr = stockroomService.getById(0);
		StockOperation operation = operationTest.createEntity(true);

		operation.setStatus(StockOperationStatus.NEW);
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.getReserved().clear();
		operation.getTransactions().clear();
		operation.setDestination(sr);

		operation.addItem(item, 100);

		// Make sure that autocomplete setting is true
		Assert.assertTrue(ModuleSettings.loadSettings().getAutoCompleteOperations());

		operation = service.submitOperation(operation);
		Context.flushSession();

		// Check that the operation is pending and not completed
		Assert.assertEquals(StockOperationStatus.COMPLETED, operation.getStatus());
		Assert.assertEquals(0, operation.getReserved().size());
		Assert.assertEquals(1, operation.getTransactions().size());

		// Check that the proper reservation has been created
		StockOperationTransaction tx = Iterators.getOnlyElement(operation.getTransactions().iterator());
		Assert.assertEquals(item, tx.getItem());
		Assert.assertEquals(100, (int)tx.getQuantity());
		Assert.assertEquals(sr, tx.getStockroom());

		// Check that the destination stockroom has not been updated
		ItemStock stock = stockroomService.getItem(sr, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(100, stock.getQuantity());
	}

	/**
	 * @verifies add the destination stockroom item stock if existing is negative
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldAddTheDestinationStockroomItemStockIfExistingIsNegative() throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		// Create a new expirable item
		Item item = itemTest.createEntity(true);
		item.setHasExpiration(true);
		itemService.save(item);
		Context.flushSession();

		// Create a negative item stock (and detail) for the item in a stockroom
		Stockroom sr = stockroomService.getById(0);

		ItemStock stock = new ItemStock();
		stock.setItem(item);
		stock.setQuantity(-10);

		ItemStockDetail detail = new ItemStockDetail();
		detail.setStockroom(sr);
		detail.setItem(item);
		detail.setBatchOperation(null);
		detail.setCalculatedBatch(true);
		detail.setCalculatedExpiration(true);
		detail.setExpiration(null);
		detail.setQuantity(-10);

		stock.addDetail(detail);
		sr.addItem(stock);

		stockroomService.save(sr);
		Context.flushSession();

		// Create a new operation to add stock to the stockroom
		StockOperation op = operationTest.createEntity(true);
		op.getReserved().clear();
		op.setStatus(StockOperationStatus.NEW);
		op.setInstanceType(WellKnownOperationTypes.getReceipt());
		op.setDestination(sr);
		op.setOperationDate(new Date());
		op.setDepartment(item.getDepartment());

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		op.addItem(item, 15, cal.getTime());

		service.submitOperation(op);
		Context.flushSession();

		stock = stockroomService.getItem(sr, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(5, stock.getQuantity());

		Assert.assertEquals(1, stock.getDetails().size());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(5, (long)detail.getQuantity());
		Assert.assertFalse(detail.getCalculatedBatch());
		Assert.assertEquals(op, detail.getBatchOperation());
		Assert.assertFalse(detail.getCalculatedExpiration());
		Assert.assertTrue(DateUtils.isSameDay(cal.getTime(), detail.getExpiration()));
	}

	/**
	 * @verifies remove item stock from destination stockroom if quantity becomes zero
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldRemoveItemStockFromDestinationStockroomIfQuantityBecomesZero() throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		// Create a new expirable item
		Item item = itemTest.createEntity(true);
		item.setHasExpiration(true);
		itemService.save(item);
		Context.flushSession();

		// Create a negative item stock (and detail) for the item in a stockroom
		Stockroom sr = stockroomService.getById(0);

		ItemStock stock = new ItemStock();
		stock.setItem(item);
		stock.setQuantity(-10);

		ItemStockDetail detail = new ItemStockDetail();
		detail.setStockroom(sr);
		detail.setItem(item);
		detail.setBatchOperation(null);
		detail.setCalculatedBatch(true);
		detail.setCalculatedExpiration(true);
		detail.setExpiration(null);
		detail.setQuantity(-10);

		stock.addDetail(detail);
		sr.addItem(stock);

		stockroomService.save(sr);
		Context.flushSession();

		// Create a new operation to add stock to the stockroom
		StockOperation op = operationTest.createEntity(true);
		op.getReserved().clear();
		op.setStatus(StockOperationStatus.NEW);
		op.setInstanceType(WellKnownOperationTypes.getReceipt());
		op.setDestination(sr);
		op.setOperationDate(new Date());
		op.setDepartment(item.getDepartment());

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		op.addItem(item, 10, cal.getTime());

		service.submitOperation(op);
		Context.flushSession();

		stock = stockroomService.getItem(sr, item);
		Assert.assertNull(stock);
	}

	@Test
	public void submitOperation_shouldAddTheDestinationStockroomItemStockIfExistingRemainsNegative() throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		// Create a new expirable item
		Item item = itemTest.createEntity(true);
		item.setHasExpiration(true);
		itemService.save(item);
		Context.flushSession();

		// Create a negative item stock (and detail) for the item in a stockroom
		Stockroom sr = stockroomService.getById(0);

		ItemStock stock = new ItemStock();
		stock.setItem(item);
		stock.setQuantity(-10);

		ItemStockDetail detail = new ItemStockDetail();
		detail.setStockroom(sr);
		detail.setItem(item);
		detail.setBatchOperation(null);
		detail.setCalculatedBatch(true);
		detail.setCalculatedExpiration(true);
		detail.setExpiration(null);
		detail.setQuantity(-10);

		stock.addDetail(detail);
		sr.addItem(stock);

		stockroomService.save(sr);
		Context.flushSession();

		// Create a new operation to add stock to the stockroom
		StockOperation op = operationTest.createEntity(true);
		op.getReserved().clear();
		op.setStatus(StockOperationStatus.NEW);
		op.setInstanceType(WellKnownOperationTypes.getReceipt());
		op.setDestination(sr);
		op.setOperationDate(new Date());
		op.setDepartment(item.getDepartment());

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		op.addItem(item, 7, cal.getTime());

		service.submitOperation(op);
		Context.flushSession();

		stock = stockroomService.getItem(sr, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-3, stock.getQuantity());

		Assert.assertEquals(1, stock.getDetails().size());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(-3, (long)detail.getQuantity());
		Assert.assertTrue(detail.getCalculatedBatch());
		Assert.assertNull(detail.getBatchOperation());
		Assert.assertTrue(detail.getCalculatedExpiration());
		Assert.assertNull(detail.getExpiration());
	}

	/**
	 * @verifies throw APIException if source stockroom is null and the expiration is not specified for an expirable item
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = APIException.class)
	public void submitOperation_shouldThrowAPIExceptionIfSourceStockroomIsNullAndTheExpirationIsNotSpecifiedForAnExpirableItem()
	        throws Exception {
		Stockroom destRoom = stockroomService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(true);

		itemService.save(newItem);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.NEW);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		operation.addItem(newItem, 25);

		service.submitOperation(operation);
	}

	/**
	 * @verifies add stock if calculate expiration is false and expiration is null for an expirable item
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldAddStockIfCalculateExpirationIsFalseAndExpirationIsNullForAnExpirableItem()
	        throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		Stockroom sourceRoom = stockroomService.getById(0);
		Stockroom destRoom = stockroomService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(true);

		itemService.save(newItem);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.NEW);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		StockOperationItem item = operation.addItem(newItem, 25);
		item.setCalculatedExpiration(false);

		service.submitOperation(operation);

		ItemStock stock = stockroomService.getItem(destRoom, newItem);
		Assert.assertNotNull(stock);
		Assert.assertEquals(25, stock.getQuantity());
		Assert.assertEquals(1, stock.getDetails().size());

		ItemStockDetail detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertNull(detail.getExpiration());
	}

	/**
	 * @verifies not include rollback operations when rolling back and reapplying subsequent operations
	 * @see IStockOperationService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldNotIncludeRollbackOperationsWhenRollingBackAndReapplyingSubsequentOperations()
	        throws Exception {
		Stockroom source = stockroomService.getById(0);
		Stockroom dest = stockroomService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(false);
		itemService.save(newItem);
		Context.flushSession();

		StockOperation op1 = new StockOperation();
		op1.setInstanceType(WellKnownOperationTypes.getReceipt());
		op1.setStatus(StockOperationStatus.NEW);
		op1.setDestination(source);
		op1.setOperationNumber("A123-1");
		op1.setOperationDate(new Date());
		op1.addItem(newItem, 25);

		op1 = service.submitOperation(op1);
		Context.flushSession();

		StockOperation op2 = new StockOperation();
		op2.setInstanceType(WellKnownOperationTypes.getTransfer());
		op2.setStatus(StockOperationStatus.NEW);
		op2.setSource(source);
		op2.setDestination(dest);
		op2.setOperationNumber("A123-2");
		op2.setOperationDate(new Date());
		op2.addItem(newItem, 25);

		op2 = service.submitOperation(op2);
		Context.flushSession();
		op2.setStatus(StockOperationStatus.COMPLETED);
		op2 = service.submitOperation(op2);
		Context.flushSession();

		StockOperation op3 = new StockOperation();
		op3.setInstanceType(WellKnownOperationTypes.getDistribution());
		op3.setStatus(StockOperationStatus.NEW);
		op3.setSource(source);
		op3.setOperationNumber("A123-3");
		op3.setOperationDate(new Date());
		op3.setPatient(Context.getPatientService().getPatient(0));
		op3.addItem(newItem, 10);

		op3 = service.submitOperation(op3);
		Context.flushSession();
		op3.setStatus(StockOperationStatus.COMPLETED);
		op3 = service.submitOperation(op3);
		Context.flushSession();

		ItemStock stock = stockroomService.getItem(source, newItem);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-35, stock.getQuantity());
		ItemStockDetail detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(-35, (long)detail.getQuantity());
		Assert.assertNull(detail.getBatchOperation());

		stock = stockroomService.getItem(dest, newItem);
		Assert.assertNotNull(stock);
		Assert.assertEquals(25, stock.getQuantity());

		// State is what we expect so now rollback transfer operation
		op2 = service.rollbackOperation(op2);
		Context.flushSession();

		stock = stockroomService.getItem(source, newItem);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-10, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(-10, (long)detail.getQuantity());
		Assert.assertNull(detail.getBatchOperation());

		stock = stockroomService.getItem(dest, newItem);
		Assert.assertNull(stock);

		// Now apply the first receipt
		op1.setStatus(StockOperationStatus.COMPLETED);
		op1 = service.submitOperation(op1);
		Context.flushSession();

		// Check that transfer (the rolled back operation was not reapplied
		stock = stockroomService.getItem(source, newItem);
		Assert.assertNotNull(stock);
		Assert.assertEquals(15, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(15, (long)detail.getQuantity());
		Assert.assertEquals(op1, detail.getBatchOperation());

		stock = stockroomService.getItem(dest, newItem);
		Assert.assertNull(stock);
	}

	/**
	 * @verifies rollback the specified operation
	 * @see IStockOperationService#rollbackOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void rollbackOperation_shouldRollbackTheSpecifiedOperation() throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		Stockroom source = stockroomService.getById(0);
		Stockroom dest = stockroomService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(false);
		itemService.save(newItem);
		Context.flushSession();

		StockOperation op1 = new StockOperation();
		op1.setInstanceType(WellKnownOperationTypes.getReceipt());
		op1.setStatus(StockOperationStatus.NEW);
		op1.setDestination(source);
		op1.setOperationNumber("A123-1");
		op1.setOperationDate(new Date());
		op1.addItem(newItem, 25);

		op1 = service.submitOperation(op1);
		Context.flushSession();

		ItemStock stock = stockroomService.getItem(source, newItem);
		Assert.assertNotNull(stock);
		Assert.assertEquals(25, stock.getQuantity());
		ItemStockDetail detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(25, (long)detail.getQuantity());
		Assert.assertEquals(op1, detail.getBatchOperation());

		// State is what we expect so now rollback transfer operation
		op1 = service.rollbackOperation(op1);
		Context.flushSession();

		stock = stockroomService.getItem(source, newItem);
		Assert.assertNull(stock);
	}

	/**
	 * @verifies rollback and reapply any following operations
	 * @see IStockOperationService#rollbackOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void rollbackOperation_shouldRollbackAndReapplyAnyFollowingOperations() throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		Stockroom source = stockroomService.getById(0);
		Stockroom dest = stockroomService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(false);
		itemService.save(newItem);
		Context.flushSession();

		StockOperation op1 = new StockOperation();
		op1.setInstanceType(WellKnownOperationTypes.getReceipt());
		op1.setStatus(StockOperationStatus.NEW);
		op1.setDestination(source);
		op1.setOperationNumber("A123-1");
		op1.setOperationDate(new Date());
		op1.addItem(newItem, 25);

		op1 = service.submitOperation(op1);
		Context.flushSession();

		StockOperation op2 = new StockOperation();
		op2.setInstanceType(WellKnownOperationTypes.getTransfer());
		op2.setStatus(StockOperationStatus.NEW);
		op2.setSource(source);
		op2.setDestination(dest);
		op2.setOperationNumber("A123-2");
		op2.setOperationDate(new Date());
		op2.addItem(newItem, 25);

		op2 = service.submitOperation(op2);
		Context.flushSession();

		StockOperation op3 = new StockOperation();
		op3.setInstanceType(WellKnownOperationTypes.getDistribution());
		op3.setStatus(StockOperationStatus.NEW);
		op3.setSource(source);
		op3.setOperationNumber("A123-3");
		op3.setOperationDate(new Date());
		op3.setPatient(Context.getPatientService().getPatient(0));
		op3.addItem(newItem, 10);

		op3 = service.submitOperation(op3);
		Context.flushSession();

		ItemStock stock = stockroomService.getItem(source, newItem);
		Assert.assertNotNull(stock);
		Assert.assertEquals(-10, stock.getQuantity());
		ItemStockDetail detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(-10, (long)detail.getQuantity());
		Assert.assertNull(detail.getBatchOperation());

		stock = stockroomService.getItem(dest, newItem);
		Assert.assertNotNull(stock);
		Assert.assertEquals(25, stock.getQuantity());

		// State is what we expect so now rollback transfer operation
		op2 = service.rollbackOperation(op2);
		Context.flushSession();

		stock = stockroomService.getItem(source, newItem);
		Assert.assertNotNull(stock);
		Assert.assertEquals(15, stock.getQuantity());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertEquals(15, (long)detail.getQuantity());
		Assert.assertEquals(op1, detail.getBatchOperation());

		stock = stockroomService.getItem(dest, newItem);
		Assert.assertNull(stock);
	}

	/**
	 * @verifies set the operation status to Rollback
	 * @see IStockOperationService#rollbackOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void rollbackOperation_shouldSetTheOperationStatusToRollback() throws Exception {
		StockOperation op = operationService.getById(1);

		Assert.assertEquals(StockOperationStatus.COMPLETED, op.getStatus());

		op = service.rollbackOperation(op);

		Assert.assertEquals(StockOperationStatus.ROLLBACK, op.getStatus());
	}

	/**
	 * @verifies throw APIException if operation status is not Completed
	 * @see IStockOperationService#rollbackOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = APIException.class)
	public void rollbackOperation_shouldThrowAPIExceptionIfOperationStatusIsNotCompleted() throws Exception {
		StockOperation op = operationTest.createEntity(true);
		Assert.assertNotEquals(StockOperationStatus.COMPLETED, op.getStatus());

		service.rollbackOperation(op);
	}

	/**
	 * @verifies throw IllegalArgumentException if operation is null
	 * @see IStockOperationService#rollbackOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void rollbackOperation_shouldThrowIllegalArgumentExceptionIfOperationIsNull() throws Exception {
		service.rollbackOperation(null);
	}

	@Test
	public void submitOperation_shouldUseItemStockWithSpecifiedExpiration() throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		Stockroom source = stockroomService.getById(0);
		Stockroom dest = stockroomService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(true);
		itemService.save(newItem);
		Context.flushSession();

		StockOperation op1 = new StockOperation();
		op1.setInstanceType(WellKnownOperationTypes.getReceipt());
		op1.setStatus(StockOperationStatus.NEW);
		op1.setDestination(source);
		op1.setOperationNumber("A123-1");
		op1.setOperationDate(new Date());

		Calendar exp = Calendar.getInstance();
		exp.add(Calendar.YEAR, 1);
		op1.addItem(newItem, 25, exp.getTime());

		op1 = service.submitOperation(op1);
		Context.flushSession();

		ItemStock stock = stockroomService.getItem(source, newItem);
		Assert.assertNotNull(stock);
		Assert.assertEquals(25, stock.getQuantity());
		Assert.assertEquals(1, stock.getDetails().size());
		ItemStockDetail detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertNotNull(detail);
		Assert.assertTrue(DateUtils.isSameDay(exp.getTime(), detail.getExpiration()));
		Assert.assertEquals(25, (long)detail.getQuantity());

		StockOperation op2 = new StockOperation();
		op2.setInstanceType(WellKnownOperationTypes.getTransfer());
		op2.setStatus(StockOperationStatus.NEW);
		op2.setSource(source);
		op2.setDestination(dest);
		op2.setOperationNumber("A123-2");
		op2.setOperationDate(new Date());

		op2.addItem(newItem, 10, exp.getTime());

		op2 = service.submitOperation(op2);
		Context.flushSession();

		stock = stockroomService.getItem(source, newItem);
		Assert.assertNotNull(stock);
		Assert.assertEquals(15, stock.getQuantity());
		Assert.assertEquals(1, stock.getDetails().size());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertNotNull(detail);
		Assert.assertTrue(DateUtils.isSameDay(exp.getTime(), detail.getExpiration()));
		Assert.assertEquals(15, (long)detail.getQuantity());

		stock = stockroomService.getItem(dest, newItem);
		Assert.assertNotNull(stock);
		Assert.assertEquals(10, stock.getQuantity());
		Assert.assertEquals(1, stock.getDetails().size());
		detail = Iterators.getOnlyElement(stock.getDetails().iterator());
		Assert.assertNotNull(detail);
		Assert.assertTrue(DateUtils.isSameDay(exp.getTime(), detail.getExpiration()));
		Assert.assertEquals(10, (long)detail.getQuantity());
	}

	private StockOperationTransaction getTransactionForItem(Collection<StockOperationTransaction> transactions,
	        final Item item) {
		return Iterators.find(transactions.iterator(), new Predicate<StockOperationTransaction>() {
			@Override
			public boolean apply(StockOperationTransaction input) {
				return input.getItem() == item;
			}
		});
	}
}
