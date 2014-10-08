package org.openmrs.module.openhmis.inventory.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTransaction;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import com.google.common.collect.Iterators;

public class IStockOperationServiceTest extends BaseModuleContextSensitiveTest {
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
	public
	        void
	        applyTransactions_shouldAddSourceStockroomItemStockWithNegativeQuantityWhenTransactionQuantityIsNegativeAndStockNotFound()
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
		
		// Create the transactions to remove more than all stock for each item
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockroom(stockroom);
		tx.setQuantity((qty + 10) * -1);
		tx.setOperation(operation);
		StockOperationTransaction tx2 = new StockOperationTransaction();
		tx2.setItem(item2);
		tx2.setStockroom(stockroom);
		tx2.setQuantity((qty2 + 20) * -1);
		tx2.setOperation(operation);
		
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
	public void applyTransactions_shouldRemoveItemStockIfQuantityIsZero() throws Exception {
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
		Assert.assertNull(stock);
		
		stock = stockroomService.getItem(stockroom, item2);
		Assert.assertNull(stock);
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
		ItemStockDetail stockDetail = Iterators.get(stock.getDetails().iterator(), 0);
		ItemStock stock2 = stockroomService.getItem(stockroom, item2);
		Assert.assertNotNull(stock2);
		int qty2 = stock2.getQuantity();
		Assert.assertTrue(qty2 > 0);
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
	public void
	        submitOperation_shouldThrowAPIExceptionIfTheOperationTypeIsReceiptAndExpirationIsNotDefinedForExpirableItems()
	                throws Exception {
		// Get the destination stockroom
		Stockroom stockroom = stockroomService.getById(2);
		stockroom.getItems();
		
		// Get the items for the test operation
		Item item2 = itemService.getById(2);
		
		Assert.assertEquals(true, item2.hasExpiration());
		
		// Create the operation
		StockOperation operation = operationTest.createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(stockroom);
		
		// Create the operation item
		StockOperationItem operationItem = operation.addItem(item2, 1);
		
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
		Assert.assertNull(operation.getTransactions());
		
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
		Assert.assertEquals(exp, detail.getExpiration());
		
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
		Assert.assertEquals(exp, detail.getExpiration());
	}
}
