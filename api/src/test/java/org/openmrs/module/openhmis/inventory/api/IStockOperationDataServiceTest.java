package org.openmrs.module.openhmis.inventory.api;

import liquibase.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.commons.api.entity.search.BaseObjectTemplateSearch;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch;

import java.util.*;

public class IStockOperationDataServiceTest extends IMetadataDataServiceTest<IStockOperationDataService, StockOperation> {
	IStockOperationTypeDataService typeService;
	IStockRoomDataService stockRoomService;
	IItemDataService itemService;

	IItemDataServiceTest itemTest;

	@Override
	public void before() throws Exception {
		super.before();

		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(IStockRoomDataServiceTest.DATASET);

		typeService = Context.getService(IStockOperationTypeDataService.class);
		stockRoomService = Context.getService(IStockRoomDataService.class);
		itemService = Context.getService(IItemDataService.class);

		itemTest = new IItemDataServiceTest();
	}

	@Override
	protected StockOperation createEntity(boolean valid) {
		StockOperation op = new StockOperation();

		if (valid) {
			op.setInstanceType(WellKnownOperationTypes.getReceipt());
		}

		op.setDestination(stockRoomService.getById(0));
		op.setStatus(StockOperationStatus.PENDING);
		op.setOperationNumber("Operation Number");

		ReservedTransaction item = new ReservedTransaction();
		item.setItem(itemService.getById(0));
		item.setQuantity(5);

		ReservedTransaction item2 = new ReservedTransaction();
		item2.setItem(itemService.getById(2));
		item2.setQuantity(2);
		item2.setExpiration(new Date(2025, 01, 01));

		op.addReserved(item);
		op.addReserved(item2);

		return op;
	}

	@Override
	protected int getTestEntityCount() {
		return 3;
	}

	@Override
	protected void updateEntityFields(StockOperation op) {
		op.setInstanceType(WellKnownOperationTypes.getTransfer());
		op.setSource(stockRoomService.getById(0));
		op.setDestination(stockRoomService.getById(1));
		op.setOperationNumber(op.getOperationNumber() + " updated");

		Set<ReservedTransaction> items = op.getReserved();
		if (items.size() > 0) {
			// Update an existing item quantity
			Iterator<ReservedTransaction> iterator = items.iterator();
			ReservedTransaction item = iterator.next();
			item.setQuantity(item.getQuantity() + 1);

			if (items.size() > 1) {
				// Delete an existing item
				item = iterator.next();

				items.remove(item);
			}
		}

		// Add a new item
		ReservedTransaction item = new ReservedTransaction();
		item.setItem(itemService.getById(2));
		item.setQuantity(10);
		op.addReserved(item);

	}

	public static void assertStockOperation(StockOperation expected, StockOperation actual) {
		assertOpenmrsMetadata(expected, actual);

		Assert.assertEquals(expected.getOperationNumber(), actual.getOperationNumber());
		Assert.assertEquals(expected.getInstanceType(), actual.getInstanceType());
		Assert.assertEquals(expected.getStatus(), actual.getStatus());
		Assert.assertEquals(expected.getSource(), actual.getSource());
		Assert.assertEquals(expected.getDestination(), actual.getDestination());
		Assert.assertEquals(expected.getPatient(), actual.getPatient());

		assertCollection(expected.getReserved(), actual.getReserved(), new Action2<ReservedTransaction, ReservedTransaction>() {
			@Override
			public void apply(ReservedTransaction expected, ReservedTransaction actual) {
				assertOpenmrsObject(expected, actual);

				Assert.assertEquals(expected.getOperation().getId(), actual.getOperation().getId());
				Assert.assertEquals(expected.getItem().getId(), actual.getItem().getId());
				Assert.assertEquals(expected.getQuantity(), actual.getQuantity());
				Assert.assertEquals(expected.getAvailable(), actual.getAvailable());
				Assert.assertEquals(expected.getExpiration(), actual.getExpiration());
				Assert.assertEquals(expected.getCreator(), actual.getCreator());
				Assert.assertEquals(expected.getDateCreated(), actual.getDateCreated());
			}
		});

		assertCollection(expected.getTransactions(), actual.getTransactions(), new Action2<StockOperationTransaction, StockOperationTransaction>() {
			@Override
			public void apply(StockOperationTransaction expected, StockOperationTransaction actual) {
				assertOpenmrsObject(expected, actual);

				Assert.assertEquals(expected.getOperation().getId(), actual.getOperation().getId());
				Assert.assertEquals(expected.getItem().getId(), actual.getItem().getId());
				Assert.assertEquals(expected.getQuantity(), actual.getQuantity());
				Assert.assertEquals(expected.getStockRoom(), actual.getStockRoom());
				Assert.assertEquals(expected.getPatient(), actual.getPatient());
				Assert.assertEquals(expected.getExpiration(), actual.getExpiration());
				Assert.assertEquals(expected.getCreator(), actual.getCreator());
				Assert.assertEquals(expected.getDateCreated(), actual.getDateCreated());
			}
		});
	}

	@Override
	protected void assertEntity(StockOperation expected, StockOperation actual) {
		assertStockOperation(expected, actual);
	}

	@Test(expected = APIException.class)
	public void purge_shouldDeleteTheSpecifiedObject() throws Exception {
		super.purge_shouldDeleteTheSpecifiedObject();
	}

	/**
	 * @verifies not throw exception if transactions is null
	 * @see IStockOperationDataService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldNotThrowExceptionIfTransactionsIsNull() throws Exception {
		service.applyTransactions(null);
	}

	/**
	 * @verifies add source stockroom item stock if no item stock found
	 * @see IStockOperationDataService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldAddSourceStockroomItemStockIfNoItemStockFound() throws Exception {
		// Create a new item
		Item item = itemTest.createEntity(true);
		itemService.save(item);
		Item item2 = itemTest.createEntity(true);
		itemService.save(item2);
		Context.flushSession();

		// Get a stockroom
		StockRoom stockroom = stockRoomService.getById(0);

		// Ensure that the stockroom does not have any item stock for the created item2
		Assert.assertNull(stockRoomService.getItem(stockroom, item, null));
		Assert.assertNull(stockRoomService.getItem(stockroom, item2, null));

		// Create a new empty operation
		StockOperation operation = createEntity(true);
		if (operation.getTransactions() != null) operation.getTransactions().clear();
		if (operation.getReserved() != null) operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setDestination(stockroom);

		// Create the transactions
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockRoom(stockroom);
		tx.setQuantity(10);
		tx.setOperation(operation);
		StockOperationTransaction tx2 = new StockOperationTransaction();
		tx2.setItem(item2);
		tx2.setStockRoom(stockroom);
		tx2.setQuantity(20);
		tx2.setOperation(operation);

		operation.addTransaction(tx);
		operation.addTransaction(tx2);

		service.save(operation);
		service.applyTransactions(tx, tx2);
		Context.flushSession();

		// Check that the stockroom now has this item stock
		stockroom = stockRoomService.getById(0);
		StockRoomItem stock = stockRoomService.getItem(stockroom, item, null);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item, stock.getItem());
		Assert.assertEquals(10, stock.getQuantity());

		stock = stockRoomService.getItem(stockroom, item2, null);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item2, stock.getItem());
		Assert.assertEquals(20, stock.getQuantity());
	}

	/**
	 * @verifies update source stockroom item stock if item exists
	 * @see IStockOperationDataService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldUpdateSourceStockroomItemStockIfItemExists() throws Exception {
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Get a stockroom
		StockRoom stockroom = stockRoomService.getById(0);

		// Ensure that the stockroom has stock for the created items
		StockRoomItem stock = stockRoomService.getItem(stockroom, item, null);
		Assert.assertNotNull(stock);
		int qty = stock.getQuantity();
		Assert.assertTrue(qty > 0);
		StockRoomItem stock2 = stockRoomService.getItem(stockroom, item2, null);
		Assert.assertNotNull(stock2);
		int qty2 = stock2.getQuantity();
		Assert.assertTrue(qty2 > 0);

		// Create a new empty operation
		StockOperation operation = createEntity(true);
		if (operation.getTransactions() != null) operation.getTransactions().clear();
		if (operation.getReserved() != null) operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setDestination(stockroom);

		// Create the transactions
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockRoom(stockroom);
		tx.setQuantity(10);
		tx.setOperation(operation);
		StockOperationTransaction tx2 = new StockOperationTransaction();
		tx2.setItem(item2);
		tx2.setStockRoom(stockroom);
		tx2.setQuantity(20);
		tx2.setOperation(operation);

		operation.addTransaction(tx);
		operation.addTransaction(tx2);

		service.save(operation);
		service.applyTransactions(tx, tx2);
		Context.flushSession();

		// Check that the stockroom now has this item stock
		stockroom = stockRoomService.getById(0);
		stock = stockRoomService.getItem(stockroom, item, null);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item, stock.getItem());
		Assert.assertEquals(10 + qty, stock.getQuantity());

		stock = stockRoomService.getItem(stockroom, item2, null);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item2, stock.getItem());
		Assert.assertEquals(20 + qty2, stock.getQuantity());
	}

	/**
	 * @verifies add source stockroom item stock if no item with same expiration is found
	 * @see IStockOperationDataService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldAddSourceStockroomItemStockIfNoItemWithSameExpirationIsFound() throws Exception {
		// Get a stockroom
		StockRoom stockroom = stockRoomService.getById(0);

		Item item = itemService.getById(2);
		StockRoomItem stock = stockRoomService.getItem(stockroom, item, new Date(2025 - 1900, 0, 1));
		Assert.assertNotNull(stock);
		Assert.assertNotNull(stock.getExpiration());
		int qty = stock.getQuantity();
		Assert.assertTrue(qty > 0);

		// Ensure that the stockroom does not have any item stock for the created item2
		Assert.assertNull(stockRoomService.getItem(stockroom, item, null));

		// Create a new empty operation
		StockOperation operation = createEntity(true);
		if (operation.getTransactions() != null) operation.getTransactions().clear();
		if (operation.getReserved() != null) operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setDestination(stockroom);

		// Create the transactions
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockRoom(stockroom);
		tx.setQuantity(10);
		tx.setOperation(operation);

		// Add a day to the expiration
		Calendar c = Calendar.getInstance();
		c.setTime(stock.getExpiration());
		c.add(Calendar.DATE, 1);
		tx.setExpiration(c.getTime());

		operation.addTransaction(tx);

		service.save(operation);
		service.applyTransactions(tx);
		Context.flushSession();

		// Check that the stockroom now has this item stock
		stockroom = stockRoomService.getById(0);
		stock = stockRoomService.getItem(stockroom, item, c.getTime());
		Assert.assertNotNull(stock);
		Assert.assertEquals(item, stock.getItem());
		Assert.assertEquals(10, stock.getQuantity());
		Assert.assertEquals(c.getTime(), stock.getExpiration());
	}

	/**
	 * @verifies update source stockroom item stock if item with same expiration is found
	 * @see IStockOperationDataService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldUpdateSourceStockroomItemStockIfItemWithSameExpirationIsFound() throws Exception {
	// Get a stockroom
		StockRoom stockroom = stockRoomService.getById(0);

		Item item = itemService.getById(2);
		StockRoomItem stock = stockRoomService.getItem(stockroom, item, new Date(2025 - 1900, 0, 1));
		Assert.assertNotNull(stock);
		Date exp = stock.getExpiration();
		Assert.assertNotNull(exp);
		int qty = stock.getQuantity();
		Assert.assertTrue(qty > 0);

		// Ensure that the stockroom does not have any item stock for the created item2
		Assert.assertNull(stockRoomService.getItem(stockroom, item, null));

		// Create a new empty operation
		StockOperation operation = createEntity(true);
		if (operation.getTransactions() != null) operation.getTransactions().clear();
		if (operation.getReserved() != null) operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setDestination(stockroom);

		// Create the transactions
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockRoom(stockroom);
		tx.setQuantity(10);
		tx.setOperation(operation);
		tx.setExpiration(exp);

		operation.addTransaction(tx);

		service.save(operation);
		service.applyTransactions(tx);
		Context.flushSession();

		// Check that the stockroom now has this item stock
		stockroom = stockRoomService.getById(0);
		stock = stockRoomService.getItem(stockroom, item, exp);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item, stock.getItem());
		Assert.assertEquals(10 + qty, stock.getQuantity());
		Assert.assertEquals(exp, stock.getExpiration());
	}

	/**
	 * @verifies not throw exception if transactions is empty
	 * @see IStockOperationDataService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldNotThrowExceptionIfTransactionsIsEmpty() throws Exception {
		// Empty params list
		service.applyTransactions();

		// Empty list
		service.applyTransactions(new ArrayList<StockOperationTransaction>());
	}

	/**
	 * @verifies add source stockroom item stock with negative quantity when transaction quantity is negative and stock not found
	 * @see IStockOperationDataService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldAddSourceStockroomItemStockWithNegativeQuantityWhenTransactionQuantityIsNegativeAndStockNotFound() throws Exception {
		// Create a new item
		Item item = itemTest.createEntity(true);
		itemService.save(item);
		Item item2 = itemTest.createEntity(true);
		itemService.save(item2);
		Context.flushSession();

		// Get a stockroom
		StockRoom stockroom = stockRoomService.getById(0);

		// Ensure that the stockroom does not have any item stock for the created item2
		Assert.assertNull(stockRoomService.getItem(stockroom, item, null));
		Assert.assertNull(stockRoomService.getItem(stockroom, item2, null));

		// Create a new empty operation
		StockOperation operation = createEntity(true);
		if (operation.getTransactions() != null) operation.getTransactions().clear();
		if (operation.getReserved() != null) operation.getReserved().clear();
		operation.setStatus(StockOperationStatus.COMPLETED);

		// Create the transactions
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockRoom(stockroom);
		tx.setQuantity(-10);
		tx.setOperation(operation);
		StockOperationTransaction tx2 = new StockOperationTransaction();
		tx2.setItem(item2);
		tx2.setStockRoom(stockroom);
		tx2.setQuantity(-20);
		tx2.setOperation(operation);

		operation.addTransaction(tx);
		operation.addTransaction(tx2);

		service.save(operation);
		service.applyTransactions(tx, tx2);
		Context.flushSession();

		// Check that the stockroom now has the item stock with negative qty
		stockroom = stockRoomService.getById(0);
		StockRoomItem stock = stockRoomService.getItem(stockroom, item, null);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item, stock.getItem());
		Assert.assertEquals(-10, stock.getQuantity());

		stock = stockRoomService.getItem(stockroom, item2, null);
		Assert.assertNotNull(stock);
		Assert.assertEquals(item2, stock.getItem());
		Assert.assertEquals(-20, stock.getQuantity());
	}

	/**
	 * @verifies remove item stock if quantity is zero
	 * @see IStockOperationDataService#applyTransactions(java.util.Collection)
	 */
	@Test
	public void applyTransactions_shouldRemoveItemStockIfQuantityIsZero() throws Exception {
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Get a stockroom
		StockRoom stockroom = stockRoomService.getById(0);

		// Ensure that the stockroom has stock for the created items
		StockRoomItem stock = stockRoomService.getItem(stockroom, item, null);
		Assert.assertNotNull(stock);
		int qty = stock.getQuantity();
		Assert.assertTrue(qty > 0);
		StockRoomItem stock2 = stockRoomService.getItem(stockroom, item2, null);
		Assert.assertNotNull(stock2);
		int qty2 = stock2.getQuantity();
		Assert.assertTrue(qty2 > 0);

		// Create a new empty operation
		StockOperation operation = createEntity(true);
		if (operation.getTransactions() != null) operation.getTransactions().clear();
		if (operation.getReserved() != null) operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setDestination(stockroom);

		// Create the transactions to remove all stock for each item
		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(item);
		tx.setStockRoom(stockroom);
		tx.setQuantity(qty * -1);
		tx.setOperation(operation);
		StockOperationTransaction tx2 = new StockOperationTransaction();
		tx2.setItem(item2);
		tx2.setStockRoom(stockroom);
		tx2.setQuantity(qty2 * -1);
		tx2.setOperation(operation);

		operation.addTransaction(tx);
		operation.addTransaction(tx2);

		service.save(operation);
		service.applyTransactions(tx, tx2);
		Context.flushSession();

		// Check that the stockroom no longer has these items
		stockroom = stockRoomService.getById(0);
		stock = stockRoomService.getItem(stockroom, item, null);
		Assert.assertNull(stock);

		stock = stockRoomService.getItem(stockroom, item2, null);
		Assert.assertNull(stock);
	}

	@Test
	public void save_shouldAddMapRecordsToSourceAndDestinationStockRooms() throws Exception {
		StockOperation operation = new StockOperation();
		operation.setOperationNumber("123");
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setCreator(Context.getAuthenticatedUser());

		StockRoom source = stockRoomService.getById(0);
		StockRoom destination = stockRoomService.getById(1);
		Assert.assertFalse(source.getOperations().contains(operation));
		Assert.assertFalse(destination.getOperations().contains(operation));

		operation.setSource(source);
		operation.setDestination(destination);

		Assert.assertTrue(source.getOperations().contains(operation));
		Assert.assertTrue(destination.getOperations().contains(operation));

		service.save(operation);
		Context.flushSession();

		operation = service.getById(operation.getId());
		source = stockRoomService.getById(0);
		destination = stockRoomService.getById(1);

		Assert.assertTrue(source.getOperations().contains(operation));
		Assert.assertTrue(destination.getOperations().contains(operation));
	}

	@Test
	public void save_shouldRemoveMapRecordsFromNullSourceOrDestinationStockRooms() throws Exception {
		StockOperation operation = new StockOperation();
		operation.setOperationNumber("123");
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setCreator(Context.getAuthenticatedUser());

		StockRoom source = stockRoomService.getById(0);
		StockRoom destination = stockRoomService.getById(1);

		operation.setSource(source);
		operation.setDestination(destination);

		service.save(operation);
		Context.flushSession();

		source = stockRoomService.getById(0);
		destination = stockRoomService.getById(1);

		Assert.assertTrue(source.getOperations().contains(operation));
		Assert.assertTrue(destination.getOperations().contains(operation));

		operation.setSource(null);
		operation.setDestination(null);

		Assert.assertFalse(source.getOperations().contains(operation));
		Assert.assertFalse(destination.getOperations().contains(operation));
	}

	@Test
	public void save_shouldUpdatePreviousRoomWhenSourceOrDestinationIsChanged() throws Exception {
		StockOperation operation = new StockOperation();
		operation.setOperationNumber("123");
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setCreator(Context.getAuthenticatedUser());

		StockRoom source = stockRoomService.getById(0);
		StockRoom destination = stockRoomService.getById(1);

		operation.setSource(source);
		operation.setDestination(destination);

		service.save(operation);
		Context.flushSession();

		source = stockRoomService.getById(0);
		destination = stockRoomService.getById(1);

		Assert.assertTrue(source.getOperations().contains(operation));
		Assert.assertTrue(destination.getOperations().contains(operation));

		StockRoom newSource = stockRoomService.getById(2);

		operation.setSource(newSource);
		operation.setDestination(null);

		Assert.assertFalse(source.getOperations().contains(operation));
		Assert.assertTrue(newSource.getOperations().contains(operation));
		Assert.assertFalse(destination.getOperations().contains(operation));
	}

	/**
	 * @verifies return null if number is not found
	 * @see IStockOperationDataService#getOperationByNumber(String)
	 */
	@Test
	public void getOperationByNumber_shouldReturnNullIfNumberIsNotFound() throws Exception {
		StockOperation result = service.getOperationByNumber("Not a valid number");

		Assert.assertNull(result);
	}

	/**
	 * @verifies return operation with specified transaction number
	 * @see IStockOperationDataService#getOperationByNumber(String)
	 */
	@Test
	public void getOperationByNumber_shouldReturnOperationWithTheSpecifiedNumber() throws Exception {
		StockOperation operation = service.getById(0);

		StockOperation result = service.getOperationByNumber(operation.getOperationNumber());

		Assert.assertNotNull(result);
		assertEntity(operation, result);
	}

	/**
	 * @verifies throw IllegalArgumentException if number is null
	 * @see IStockOperationDataService#getOperationByNumber(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getOperationByNumber_shouldThrowIllegalArgumentExceptionIfNumberIsNull() throws Exception {
		service.getOperationByNumber(null);
	}

	/**
	 * @verifies throw IllegalArgumentException if number is empty
	 * @see IStockOperationDataService#getOperationByNumber(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getOperationByNumber_shouldThrowIllegalArgumentExceptionIfNumberIsEmpty() throws Exception {
		service.getOperationByNumber("");
	}

	/**
	 * @verifies throw IllegalArgumentException is number is longer than 255 characters
	 * @see IStockOperationDataService#getOperationByNumber(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getOperationByNumber_shouldThrowIllegalArgumentExceptionIsNumberIsLongerThan255Characters() throws Exception {
		service.getOperationByNumber(StringUtils.repeat("A", 256));
	}

	/**
	 * @verifies return operations for specified room
	 * @see IStockOperationDataService#getOperationsByRoom(StockRoom, PagingInfo)
	 */
	@Test
	public void getOperationsByRoom_shouldReturnOperationsForSpecifiedRoom() throws Exception {
		StockRoom room = stockRoomService.getById(2);

		List<StockOperation> results = service.getOperationsByRoom(room, null);

		assertCollection(room.getOperations(), results, new Action2<StockOperation, StockOperation>() {
			@Override
			public void apply(StockOperation expected, StockOperation actual) {
				assertEntity(expected, actual);
			}
		});
	}

	/**
	 * @verifies return empty list when no operations
	 * @see IStockOperationDataService#getOperationsByRoom(StockRoom, PagingInfo)
	 */
	@Test
	public void getOperationsByRoom_shouldReturnEmptyListWhenNoOperations() throws Exception {
		StockRoom room = new StockRoom();
		room.setLocation(Context.getLocationService().getLocation(1));
		room.setName("New Room");
		room.setCreator(Context.getAuthenticatedUser());
		room.setDateCreated(new Date());

		stockRoomService.save(room);
		Context.flushSession();

		List<StockOperation> results = service.getOperationsByRoom(room, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return paged operations when paging is specified
	 * @see IStockOperationDataService#getOperationsByRoom(StockRoom, PagingInfo)
	 */
	@Test
	public void getOperationsByRoom_shouldReturnPagedOperationsWhenPagingIsSpecified() throws Exception {
		StockRoom room = stockRoomService.getById(0);
		Assert.assertEquals(2, room.getOperations().size());

		// Only return a single result per page
		PagingInfo paging = new PagingInfo(1, 1);
		List<StockOperation> results = service.getOperationsByRoom(room, paging);
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(2, (long)paging.getTotalRecordCount());
		StockOperation[] roomTrans = new StockOperation[2];
		room.getOperations().toArray(roomTrans);
		assertEntity(roomTrans[0], results.get(0));

		// Get the next result
		paging.setPage(2);
		results = service.getOperationsByRoom(room, paging);
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(roomTrans[1], results.get(0));
	}

	/**
	 * @verifies return all operations when paging is null
	 * @see IStockOperationDataService#getOperationsByRoom(StockRoom, PagingInfo)
	 */
	@Test
	public void getOperationsByRoom_shouldReturnAllOperationsWhenPagingIsNull() throws Exception {
		StockRoom room = stockRoomService.getById(1);
		Assert.assertEquals(2, room.getOperations().size());

		List<StockOperation> results = service.getOperationsByRoom(room, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies return operations with any status
	 * @see IStockOperationDataService#getOperationsByRoom(StockRoom, PagingInfo)
	 */
	@Test
	public void getOperationsByRoom_shouldReturnOperationsWithAnyStatus() throws Exception {
		StockRoom room = stockRoomService.getById(1);
		Assert.assertEquals(2, room.getOperations().size());

		StockOperation[] roomTrans = new StockOperation[2];
		room.getOperations().toArray(roomTrans);
		Assert.assertEquals(StockOperationStatus.PENDING, roomTrans[0].getStatus());
		Assert.assertEquals(StockOperationStatus.COMPLETED, roomTrans[1].getStatus());

		List<StockOperation> results = service.getOperationsByRoom(room, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies throw IllegalArgumentException when stockroom is null
	 * @see IStockOperationDataService#getOperationsByRoom(StockRoom, PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getOperationsByRoom_shouldThrowIllegalArgumentExceptionWhenStockroomIsNull() throws Exception {
		service.getOperationsByRoom(null, null);
	}

	/**
	 * @verifies throw NullPointerException if operation search is null
	 * @see IStockOperationDataService#findOperations(StockOperationSearch, PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void findOperations_shouldThrowIllegalArgumentExceptionIfOperationSearchIsNull() throws Exception {
		service.findOperations(null, null);
	}

	/**
	 * @verifies throw NullPointerException if operation search template object is null
	 * @see IStockOperationDataService#findOperations(StockOperationSearch, PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void findOperations_shouldThrowIllegalArgumentExceptionIfOperationSearchTemplateObjectIsNull() throws Exception {
		service.findOperations(new StockOperationSearch(null), null);
	}

	/**
	 * @verifies return an empty list if no operations are found via the search
	 * @see IStockOperationDataService#findOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void findOperations_shouldReturnAnEmptyListIfNoOperationsAreFoundViaTheSearch() throws Exception {
		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setStatus(StockOperationStatus.CANCELLED);

		List<StockOperation> results = service.findOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return items filtered by number
	 * @see IStockOperationDataService#findOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void findOperations_shouldReturnItemsFilteredByNumber() throws Exception {
		StockOperation operation = service.getById(0);
		operation.setOperationNumber("ABCD-1234");

		service.save(operation);
		Context.flushSession();

		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setOperationNumber("ABCD-1234");

		List<StockOperation> results = service.findOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(operation, results.get(0));

		search.setOperationNumberComparisonType(BaseObjectTemplateSearch.StringComparisonType.LIKE);
		search.getTemplate().setOperationNumber("AB%");

		results = service.findOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(operation, results.get(0));
	}

	/**
	 * @verifies return items filtered by status
	 * @see IStockOperationDataService#findOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void findOperations_shouldReturnItemsFilteredByStatus() throws Exception {
		StockOperation operation = service.getById(0);
		operation.setStatus(StockOperationStatus.CANCELLED);

		service.save(operation);
		Context.flushSession();

		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setStatus(StockOperationStatus.CANCELLED);

		List<StockOperation> results = service.findOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(operation, results.get(0));
	}

	/**
	 * @verifies return items filtered by type
	 * @see IStockOperationDataService#findOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void findOperations_shouldReturnItemsFilteredByType() throws Exception {
		StockOperation operation = service.getById(0);
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());

		service.save(operation);
		Context.flushSession();

		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setInstanceType(WellKnownOperationTypes.getReceipt());

		List<StockOperation> results = service.findOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(operation, results.get(0));
	}

	/**
	 * @verifies return items filtered by source stock room
	 * @see IStockOperationDataService#findOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void findOperations_shouldReturnItemsFilteredBySourceStockRoom() throws Exception {
		StockOperation operation = service.getById(1);
		StockRoom room = operation.getSource();

		Context.flushSession();

		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setSource(room);

		Context.flushSession();

		List<StockOperation> test = service.getAll();
		Assert.assertNotNull(test);
		Assert.assertEquals(3, test.size());

		List<StockOperation> results = service.findOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(operation, results.get(0));
	}

	/**
	 * @verifies return items filtered by destination stock room
	 * @see IStockOperationDataService#findOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void findOperations_shouldReturnItemsFilteredByDestinationStockRoom() throws Exception {
		StockOperation operation = service.getById(0);
		StockRoom room = operation.getDestination();

		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setDestination(room);

		List<StockOperation> results = service.findOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(operation, results.get(0));
	}

	/**
	 * @verifies return all items if paging is null
	 * @see IStockOperationDataService#findOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void findOperations_shouldReturnAllItemsIfPagingIsNull() throws Exception {
		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setStatus(StockOperationStatus.COMPLETED);

		List<StockOperation> results = service.findOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies return paged items if paging is specified
	 * @see IStockOperationDataService#findOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void findOperations_shouldReturnPagedItemsIfPagingIsSpecified() throws Exception {
		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setStatus(StockOperationStatus.COMPLETED);

		PagingInfo pagingInfo = new PagingInfo(1, 1);
		List<StockOperation> results = service.findOperations(search, pagingInfo);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(2, (long)pagingInfo.getTotalRecordCount());
	}

	/**
	 * @verifies return items filtered by creation date
	 * @see IStockOperationDataService#findOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void findOperations_shouldReturnItemsFilteredByCreationDate() throws Exception {
		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setDateCreated(service.getById(0).getDateCreated());
		search.setDateCreatedComparisonType(BaseObjectTemplateSearch.DateComparisonType.GREATER_THAN_EQUAL);

		List<StockOperation> results = service.findOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies return items filtered by patient
	 * @see IStockOperationDataService#findOperations(org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void findOperations_shouldReturnItemsFilteredByPatient() throws Exception {
		StockOperationSearch search = new StockOperationSearch();
		Patient patient = Context.getPatientService().getPatient(1);
		search.getTemplate().setPatient(patient);

		List<StockOperation> results = service.findOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(service.getById(2), results.get(0));
	}

	/**
	 * @verifies return operations created by user
	 * @see IStockOperationDataService#getUserOperations(User, PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnOperationsCreatedByUser() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());
	}

	/**
	 * @verifies return all operations with the specified status for specified user
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnAllOperationsWithTheSpecifiedStatusForSpecifiedUser() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		Assert.assertEquals(2, (int)results.get(0).getId());
		Assert.assertEquals(0, (int)results.get(1).getId());
		Assert.assertEquals(1, (int)results.get(2).getId());

		results = service.getUserOperations(user, StockOperationStatus.COMPLETED, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());

		Assert.assertEquals(0, (int)results.get(0).getId());
		Assert.assertEquals(1, (int)results.get(1).getId());

		results = service.getUserOperations(user, StockOperationStatus.PENDING, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());

		Assert.assertEquals(2, (int)results.get(0).getId());
	}

	/**
	 * @verifies return specified operations created by user
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnSpecifiedOperationsCreatedByUser() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		results = service.getUserOperations(user, StockOperationStatus.COMPLETED, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());

		results = service.getUserOperations(user, StockOperationStatus.PENDING, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
	}

	/**
	 * @verifies return specified operations with user as attribute type user
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnSpecifiedOperationsWithUserAsAttributeTypeUser() throws Exception {
		User baseUser = Context.getUserService().getUser(0);
		User user = Context.getUserService().getUser(5506);

		StockOperation operation = createEntity(true);
		operation.setCreator(baseUser);
		operation.setInstanceType(WellKnownOperationTypes.getAdjustment());
		operation.getInstanceType().setUser(user);
		operation.setSource(stockRoomService.getById(0));

		typeService.save(operation.getInstanceType());
		service.save(operation);
		Context.flushSession();

		operation = service.getById(operation.getId());

		List<StockOperation> transactions = service.getUserOperations(user, null, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Assert.assertEquals(operation.getId(), transactions.get(0).getId());

		transactions = service.getUserOperations(user, StockOperationStatus.COMPLETED, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(0, transactions.size());

		transactions = service.getUserOperations(user, StockOperationStatus.PENDING, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Assert.assertEquals(operation.getId(), transactions.get(0).getId());
	}

	/**
	 * @verifies return specified operations with user role as attribute type role
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnSpecifiedOperationsWithUserRoleAsAttributeTypeRole() throws Exception {
		User baseUser = Context.getUserService().getUser(0);
		User user = Context.getUserService().getUser(5506);
		Set<Role> roles = user.getRoles();
		Role[] roleArray = new Role[roles.size()];
		roles.toArray(roleArray);

		StockOperation operation = createEntity(true);
		operation.setCreator(baseUser);
		operation.setInstanceType(WellKnownOperationTypes.getAdjustment());
		operation.getInstanceType().setRole(roleArray[0]);
		operation.setSource(stockRoomService.getById(0));

		service.save(operation);
		Context.flushSession();

		List<StockOperation> transactions = service.getUserOperations(user, null, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Assert.assertEquals(operation.getId(), transactions.get(0).getId());

		transactions = service.getUserOperations(user, StockOperationStatus.COMPLETED, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(0, transactions.size());

		transactions = service.getUserOperations(user, StockOperationStatus.PENDING, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Assert.assertEquals(operation.getId(), transactions.get(0).getId());
	}

	/**
	 * @verifies return specified operations with user role as child role of attribute type role
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnSpecifiedOperationsWithUserRoleAsChildRoleOfAttributeTypeRole() throws Exception {
		User baseUser = Context.getUserService().getUser(0);

		// This user has the Child Role which is a child of the Parent role
		User user = Context.getUserService().getUser(5506);

		StockOperation operation = createEntity(true);
		operation.setCreator(baseUser);
		operation.setInstanceType(WellKnownOperationTypes.getAdjustment());
		operation.getInstanceType().setRole(Context.getUserService().getRole("Parent"));
		operation.setSource(stockRoomService.getById(0));

		service.save(operation);
		Context.flushSession();

		List<StockOperation> transactions = service.getUserOperations(user, null, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Assert.assertEquals(operation.getId(), transactions.get(0).getId());

		transactions = service.getUserOperations(user, StockOperationStatus.COMPLETED, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(0, transactions.size());

		transactions = service.getUserOperations(user, StockOperationStatus.PENDING, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Assert.assertEquals(operation.getId(), transactions.get(0).getId());
	}

	/**
	 * @verifies return specified operations with user role as grandchild role of attribute type role
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnSpecifiedOperationsWithUserRoleAsGrandchildRoleOfAttributeTypeRole() throws Exception {
		User baseUser = Context.getUserService().getUser(0);
		User user = Context.getUserService().getUser(5506);

		Set<Role> roles = new HashSet<Role>();
		roles.add(Context.getUserService().getRole("Grandchild"));
		user.setRoles(roles);
		Context.getUserService().saveUser(user, "1wWhatever");
		Context.flushSession();

		StockOperation operation = createEntity(true);
		operation.setCreator(baseUser);
		operation.setInstanceType(WellKnownOperationTypes.getAdjustment());
		operation.getInstanceType().setRole(Context.getUserService().getRole("Parent"));
		operation.setSource(stockRoomService.getById(0));

		service.save(operation);
		Context.flushSession();

		List<StockOperation> transactions = service.getUserOperations(user, null, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Assert.assertEquals(operation.getId(), transactions.get(0).getId());

		transactions = service.getUserOperations(user, StockOperationStatus.COMPLETED, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(0, transactions.size());

		transactions = service.getUserOperations(user, StockOperationStatus.PENDING, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Assert.assertEquals(operation.getId(), transactions.get(0).getId());
	}

	/**
	 * @verifies not return operations when user role not descendant of attribute type role
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldNotReturnOperationsWhenUserRoleNotDescendantOfAttributeTypeRole() throws Exception {
		User baseUser = Context.getUserService().getUser(0);
		User user = Context.getUserService().getUser(5506);

		Set<Role> roles = new HashSet<Role>();
		roles.add(Context.getUserService().getRole("Other"));
		user.setRoles(roles);
		Context.getUserService().saveUser(user, "1wWhatever");
		Context.flushSession();

		StockOperation operation = createEntity(true);
		operation.setCreator(baseUser);
		operation.getInstanceType().setRole(Context.getUserService().getRole("Parent"));

		service.save(operation);
		Context.flushSession();

		List<StockOperation> transactions = service.getUserOperations(user, null, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(0, transactions.size());
	}

	/**
	 * @verifies not return operations when user role is parent of attribute type role
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldNotReturnOperationsWhenUserRoleIsParentOfAttributeTypeRole() throws Exception {
		User baseUser = Context.getUserService().getUser(0);
		User user = Context.getUserService().getUser(5506);

		Set<Role> roles = new HashSet<Role>();
		roles.add(Context.getUserService().getRole("Parent"));
		user.setRoles(roles);
		Context.getUserService().saveUser(user, "1wWhatever");
		Context.flushSession();

		StockOperation operation = createEntity(true);
		operation.setCreator(baseUser);
		operation.getInstanceType().setRole(Context.getUserService().getRole("Child"));

		service.save(operation);
		Context.flushSession();

		List<StockOperation> transactions = service.getUserOperations(user, null, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(0, transactions.size());
	}

	/**
	 * @verifies return empty list when no operations
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnEmptyListWhenNoOperations() throws Exception {
		User user = Context.getUserService().getUser(1);

		StockOperation operation = service.getById(2);
		operation.setStatus(StockOperationStatus.COMPLETED);

		service.save(operation);
		Context.flushSession();

		List<StockOperation> results = service.getUserOperations(user, StockOperationStatus.PENDING, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return paged operations when paging is specified
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnPagedOperationsWhenPagingIsSpecified() throws Exception {
		User user = Context.getUserService().getUser(1);
		StockOperation operation = service.getById(1);
		operation.setStatus(StockOperationStatus.PENDING);

		service.save(operation);
		Context.flushSession();

		PagingInfo paging = new PagingInfo(1, 1);
		List<StockOperation> results = service.getUserOperations(user, StockOperationStatus.PENDING, paging);
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(2, (long)paging.getTotalRecordCount());
		int id = results.get(0).getId();

		paging.setPage(2);
		results = service.getUserOperations(user, paging);
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertFalse(id == results.get(0).getId());
	}

	/**
	 * @verifies return all operations when paging is null
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnAllOperationsWhenPagingIsNull() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());
	}

	/**
	 * @verifies throw NullPointerException when user is null
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getUserOperations_shouldThrowIllegalArgumentExceptionWhenUserIsNull() throws Exception {
		service.getUserOperations(null, null, null);
	}

	/**
	 * @verifies return all operations for user when status is null
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnAllOperationsForUserWhenStatusIsNull() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());
	}

	/**
	 * @verifies update the source stockroom item stock quantities
	 * @see IStockOperationDataService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldUpdateTheSourceStockroomItemStockQuantities() throws Exception {
		// Get the patient this operation will be distributing to
		Patient patient = Context.getPatientService().getPatient(1);

		// Get the source stockroom
		StockRoom source = stockRoomService.getById(0);
		source.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Create the operation
		StockOperation operation = createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getDistribution());
		operation.setSource(source);
		operation.setPatient(patient);

		// Create the operation reservations
		ReservedTransaction tx = operation.addReserved(item, 1);
		ReservedTransaction tx2 = operation.addReserved(item2, 3);

		// Get the current stockroom item quantities
		int itemQty = stockRoomService.getItem(source, item, null).getQuantity();
		int item2Qty = stockRoomService.getItem(source, item2, null).getQuantity();

		// Submit the operation (this will apply the item reservations)
		service.submitOperation(operation);
		Context.flushSession();

		// Check the reservation quantities
		Assert.assertEquals(1, (int)tx.getQuantity());
		Assert.assertEquals(3, (int)tx2.getQuantity());

		// Check that the reservation quantities were removed from the source stock room
		Assert.assertEquals(itemQty - tx.getQuantity(), stockRoomService.getItem(source, item, null).getQuantity());
		Assert.assertEquals(item2Qty - tx2.getQuantity(), stockRoomService.getItem(source, item2, null).getQuantity());

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
			Assert.assertNotNull(operationTx.getStockRoom());

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
	 * @see IStockOperationDataService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldRemoveEmptyItemStockFromTheSourceStockroom() throws Exception {
		// Get the patient this operation will be distributing to
		Patient patient = Context.getPatientService().getPatient(1);

		// Get the source stockroom
		StockRoom source = stockRoomService.getById(0);
		source.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Create the operation
		StockOperation operation = createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getDistribution());
		operation.setSource(source);
		operation.setPatient(patient);

		// Get the current stockroom item quantities
		int itemQty = stockRoomService.getItem(source, item, null).getQuantity();
		int item2Qty = stockRoomService.getItem(source, item2, null).getQuantity();

		// Create the operation reservations for the total stockroom item quantities
		ReservedTransaction tx = operation.addReserved(item, itemQty);
		ReservedTransaction tx2 = operation.addReserved(item2, item2Qty);

		// Submit the operation (this will apply the item reservations)
		service.submitOperation(operation);
		Context.flushSession();

		// Check the reservation quantities
		Assert.assertEquals(itemQty, (int)tx.getQuantity());
		Assert.assertEquals(item2Qty, (int)tx2.getQuantity());

		// Check that the reservation quantities caused the item stock to be removed from the stockroom
		Assert.assertNull(stockRoomService.getItem(source, item, null));
		Assert.assertNull(stockRoomService.getItem(source, item2, null));
	}

	/**
	 * @verifies set the correct availability for the reserved stock quantity
	 * @see IStockOperationDataService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldSetTheCorrectAvailabilityForTheReservedStockQuantity() throws Exception {
		// Get the patient this operation will be distributing to
		Patient patient = Context.getPatientService().getPatient(1);

		// Get the source stockroom
		StockRoom source = stockRoomService.getById(0);
		source.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Create the operation
		StockOperation operation = createEntity(true);
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
	 * @see IStockOperationDataService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldUpdateTheDestinationStockroomItemStockQuantities() throws Exception {
		// Get the destination stockroom
		StockRoom stockroom = stockRoomService.getById(0);
		stockroom.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Get the current stockroom item quantities
		int itemQty = stockRoomService.getItem(stockroom, item, null).getQuantity();
		int item2Qty = stockRoomService.getItem(stockroom, item2, null).getQuantity();

		// Create the operation
		StockOperation operation = createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(stockroom);

		// Create the operation reservations
		ReservedTransaction tx = operation.addReserved(item, 1);
		ReservedTransaction tx2 = operation.addReserved(item2, 3);

		// Submit the operation (this will apply the item reservations)
		service.submitOperation(operation);
		Context.flushSession();

		// Check that the destination quantities have not yet been updated
		Assert.assertEquals(itemQty, stockRoomService.getItem(stockroom, item, null).getQuantity());
		Assert.assertEquals(item2Qty, stockRoomService.getItem(stockroom, item2, null).getQuantity());

		operation.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(operation);
		Context.flushSession();

		// Check the destination stockroom quantities have now been updated
		Assert.assertEquals(itemQty + 1, stockRoomService.getItem(stockroom, item, null).getQuantity());
		Assert.assertEquals(item2Qty + 3, stockRoomService.getItem(stockroom, item2, null).getQuantity());
	}

	/**
	 * @verifies add the destination stockroom item stock if not found
	 * @see IStockOperationDataService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void submitOperation_shouldAddTheDestinationStockroomItemStockIfNotFound() throws Exception {
		// Get the destination stockroom
		StockRoom stockroom = stockRoomService.getById(2);
		stockroom.getItems();

		// Get the items for the test operation
		Item item = itemService.getById(0);
		Item item2 = itemService.getById(1);

		// Ensure that there is not stock for these items
		Assert.assertNull(stockRoomService.getItem(stockroom, item, null));
		Assert.assertNull(stockRoomService.getItem(stockroom, item2, null));

		// Create the operation
		StockOperation operation = createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(stockroom);

		// Create the operation reservations
		ReservedTransaction tx = operation.addReserved(item, 1);
		ReservedTransaction tx2 = operation.addReserved(item2, 3);

		// Submit the operation (this will apply the item reservations)
		service.submitOperation(operation);
		Context.flushSession();

		// We didn't get an exception so it didn't try to update the source stockroom

		// Check that the destination stock has not yet been created
		Assert.assertNull(stockRoomService.getItem(stockroom, item, null));
		Assert.assertNull(stockRoomService.getItem(stockroom, item2, null));

		operation.setStatus(StockOperationStatus.COMPLETED);
		service.submitOperation(operation);
		Context.flushSession();

		// Check that the destination stockroom stock and quantity has now been created
		Assert.assertEquals(1, stockRoomService.getItem(stockroom, item, null).getQuantity());
		Assert.assertEquals(3, stockRoomService.getItem(stockroom, item2, null).getQuantity());
	}

	/**
	 * @verifies throw an IllegalArgumentException if the operation is null
	 * @see IStockOperationDataService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void submitOperation_shouldThrowAnIllegalArgumentExceptionIfTheOperationIsNull() throws Exception {
		service.submitOperation(null);
	}

	/**
	 * @verifies throw an APIException if the operation type is null
	 * @see IStockOperationDataService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = APIException.class)
	public void submitOperation_shouldThrowAnAPIExceptionIfTheOperationTypeIsNull() throws Exception {
		StockOperation operation = createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(null);

		service.submitOperation(operation);
	}

	/**
	 * @verifies throw an APIException if the operation has no reserved transactions
	 * @see IStockOperationDataService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = APIException.class)
	public void submitOperation_shouldThrowAnAPIExceptionIfTheOperationHasNoReservedTransactions() throws Exception {
		StockOperation operation = createEntity(true);
		operation.getReserved().clear();

		Assert.assertEquals(0, operation.getReserved().size());

		service.submitOperation(operation);
	}

	/**
	 * @verifies throw an APIException if the operation type requires a source and the source is null
	 * @see IStockOperationDataService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = APIException.class)
	public void submitOperation_shouldThrowAnAPIExceptionIfTheOperationTypeRequiresASourceAndTheSourceIsNull() throws Exception {
		StockOperation operation = createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getDistribution());
		operation.setSource(null);

		Item item = itemService.getById(0);
		operation.addReserved(item, 10);

		service.submitOperation(operation);
	}

	/**
	 * @verifies throw an APIException if the operation type requires a destination and the destination is null
	 * @see IStockOperationDataService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = APIException.class)
	public void submitOperation_shouldThrowAnAPIExceptionIfTheOperationTypeRequiresADestinationAndTheDestinationIsNull() throws Exception {
		StockOperation operation = createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(null);

		Item item = itemService.getById(0);
		operation.addReserved(item, 10);

		service.submitOperation(operation);
	}

	/**
	 * @verifies throw an APIException if the operation type requires a patient and the patient is null
	 * @see IStockOperationDataService#submitOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = APIException.class)
	public void submitOperation_shouldThrowAnAPIExceptionIfTheOperationTypeRequiresAPatientAndThePatientIsNull() throws Exception {
		WellKnownOperationTypes.getDistribution().setPatientRequired(true);

		StockOperation operation = createEntity(true);
		operation.getReserved().clear();
		operation.setInstanceType(WellKnownOperationTypes.getDistribution());
		operation.setSource(stockRoomService.getById(0));
		operation.setPatient(null);

		Item item = itemService.getById(0);
		operation.addReserved(item, 10);

		service.submitOperation(operation);
	}
}

