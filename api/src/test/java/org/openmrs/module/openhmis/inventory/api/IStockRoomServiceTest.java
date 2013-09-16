package org.openmrs.module.openhmis.inventory.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class IStockRoomServiceTest extends BaseModuleContextSensitiveTest {
	protected IStockRoomService service;

	protected IItemDataService itemService;
	protected IStockRoomDataService stockRoomDataService;
	protected IStockRoomTransactionDataService transactionService;
	protected IStockRoomTransactionTypeDataService transactionTypeService;

	@Before
	public void before() throws Exception {
		service = Context.getService(IStockRoomService.class);

		itemService = Context.getService(IItemDataService.class);
		stockRoomDataService = Context.getService(IStockRoomDataService.class);
		transactionService = Context.getService(IStockRoomTransactionDataService.class);
		transactionTypeService = Context.getService(IStockRoomTransactionTypeDataService.class);

		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IDepartmentDataServiceTest.DEPARTMENT_DATASET);
		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(IStockRoomDataServiceTest.DATASET);
	}

	/**
	 * @verifies return all the stock rooms
	 * @see IStockRoomService#getStockRooms()
	 */
	@Test
	public void getStockRooms_shouldReturnAllTheStockRooms() throws Exception {
		List<StockRoom> results = service.getStockRooms();

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());
	}

	/**
	 * @verifies return an empty list if there are no stock rooms
	 * @see IStockRoomService#getStockRooms()
	 */
	@Test
	public void getStockRooms_shouldReturnAnEmptyListIfThereAreNoStockRooms() throws Exception {
		List<StockRoom> rooms = stockRoomDataService.getAll();
		for (StockRoom room :rooms) {
			stockRoomDataService.purge(room);
		}
		Context.flushSession();

		List<StockRoom> results = service.getStockRooms();

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return all the transaction types
	 * @see IStockRoomService#getTransactionTypes()
	 */
	@Test
	public void getTransactionTypes_shouldReturnAllTheTransactionTypes() throws Exception {
		List<StockRoomTransactionType> results = service.getTransactionTypes();

		Assert.assertNotNull(results);
		Assert.assertEquals(4, results.size());
	}

	/**
	 * @verifies return all the transactions for the stock room
	 * @see IStockRoomService#getTransactions(org.openmrs.module.openhmis.inventory.api.model.StockRoom)
	 */
	@Test
	public void getTransactions_shouldReturnAllTheTransactionsForTheStockRoom() throws Exception {
		StockRoom room = stockRoomDataService.getById(1);
		List<StockRoomTransaction> results = service.getTransactions(room);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies not return transactions for other stock rooms
	 * @see IStockRoomService#getTransactions(org.openmrs.module.openhmis.inventory.api.model.StockRoom)
	 */
	@Test
	public void getTransactions_shouldNotReturnTransactionsForOtherStockRooms() throws Exception {
		StockRoom room = stockRoomDataService.getById(2);
		List<StockRoomTransaction> results = service.getTransactions(room);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());

		for (StockRoomTransaction trans : results) {
			if (trans.getId() == 0) {
				Assert.fail("Unexpected stock room found.");
			}
		}
	}

	/**
	 * @verifies return an empty list if there are no transactions
	 * @see IStockRoomService#getTransactions(org.openmrs.module.openhmis.inventory.api.model.StockRoom)
	 */
	@Test
	public void getTransactions_shouldReturnAnEmptyListIfThereAreNoTransactions() throws Exception {
		StockRoom room = stockRoomDataService.getById(0);
		List<StockRoomTransaction> results = service.getTransactions(room);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies throw a NullReferenceException if the stock room is null
	 * @see IStockRoomService#getTransactions(org.openmrs.module.openhmis.inventory.api.model.StockRoom)
	 */
	@Test(expected = NullPointerException.class)
	public void getTransactions_shouldThrowANullReferenceExceptionIfTheStockRoomIsNull() throws Exception {
		service.getTransactions(null);
	}

	/**
	 * @verifies create a new stock room transaction with the specified settings
	 * @see IStockRoomService#createTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransactionType, org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.StockRoom)
	 */
	@Test
	public void createTransaction_shouldCreateANewStockRoomTransactionWithTheSpecifiedSettings() throws Exception {
		StockRoom source = stockRoomDataService.getById(0);
		StockRoom destination = stockRoomDataService.getById(1);

		StockRoomTransaction result = service.createTransaction(WellKnownTransactionTypes.getTransfer(), source, destination);

		Assert.assertNotNull(result);
		Assert.assertNull(result.getId());
		Assert.assertNull(result.getItems());
		Assert.assertEquals(WellKnownTransactionTypes.getTransfer(), result.getInstanceType());
		Assert.assertEquals(source, result.getSource());
		Assert.assertEquals(destination, result.getDestination());
		Assert.assertEquals(StockRoomTransactionStatus.PENDING, result.getStatus());
		Assert.assertEquals(Context.getAuthenticatedUser(), result.getCreator());
		Assert.assertNotNull(result.getDateCreated());
		Assert.assertEquals(false, result.isImportTransaction());

		result = service.createTransaction(WellKnownTransactionTypes.getIntake(), null, destination);

		Assert.assertNotNull(result);
		Assert.assertNull(result.getSource());
		Assert.assertEquals(destination, result.getDestination());
		Assert.assertEquals(true, result.isImportTransaction());
	}

	/**
	 * @verifies throw a NullReferenceException if the transaction type is null
	 * @see IStockRoomService#createTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransactionType, org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.StockRoom)
	 */
	@Test(expected = NullPointerException.class)
	public void createTransaction_shouldThrowANullReferenceExceptionIfTheTransactionTypeIsNull() throws Exception {
		StockRoom source = stockRoomDataService.getById(0);
		StockRoom destination = stockRoomDataService.getById(1);

		service.createTransaction(null, source, destination);
	}

	/**
	 * @verifies throw an APIException if the type requires a source and the source is null
	 * @see IStockRoomService#createTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransactionType, org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.StockRoom)
	 */
	@Test(expected = APIException.class)
	public void createTransaction_shouldThrowAnAPIExceptionIfTheTypeRequiresASourceAndTheSourceIsNull() throws Exception {
		StockRoom destination = stockRoomDataService.getById(1);

		service.createTransaction(WellKnownTransactionTypes.getTransfer(), null, destination);
	}

	/**
	 * @verifies throw an APIException if the type requires a destination and the destination is null
	 * @see IStockRoomService#createTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransactionType, org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.StockRoom)
	 */
	@Test(expected =  APIException.class)
	public void createTransaction_shouldThrowAnAPIExceptionIfTheTypeRequiresADestinationAndTheDestinationIsNull() throws Exception {
		StockRoom source = stockRoomDataService.getById(0);

		service.createTransaction(WellKnownTransactionTypes.getIntake(), source, null);
	}

	/**
	 * @verifies update the source stock room item quantities
	 * @see IStockRoomService#submitTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test
	public void submitTransaction_shouldUpdateTheSourceStockRoomItemQuantities() throws Exception {
		StockRoom source = stockRoomDataService.getById(1);
		source.getItems();

		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getDistribution(), source, null);

		StockRoomItem item0 = stockRoomDataService.getItem(source, itemService.getById(0), null);
		Calendar cal = Calendar.getInstance();
		cal.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		StockRoomItem item2 = stockRoomDataService.getItem(source, itemService.getById(2), cal.getTime());

		StockRoomTransactionItem txItem0 = tx.addItem(item0, 6);
		StockRoomTransactionItem txItem2 = tx.addItem(item2, 8);

		int item0Qty = item0.getQuantity();
		int item2Qty = item2.getQuantity();

		service.submitTransaction(tx);
		Context.flushSession();

		Assert.assertEquals(6, txItem0.getQuantityOrdered());
		Assert.assertEquals(8, txItem2.getQuantityOrdered());
		Assert.assertEquals(item0Qty - 6, item0.getQuantity());
		Assert.assertEquals(item2Qty - 8, item2.getQuantity());

	}

	/**
	 * @verifies remove empty items from the source stock room
	 * @see IStockRoomService#submitTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test
	public void submitTransaction_shouldRemoveEmptyItemsFromTheSourceStockRoom() throws Exception {
		StockRoom source = stockRoomDataService.getById(1);
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getDistribution(), source, null);
		StockRoomItem item0 = stockRoomDataService.getItem(source, itemService.getById(0), null);
		StockRoomTransactionItem txItem0 = tx.addItem(item0, item0.getQuantity());
		int item0Qty = item0.getQuantity();

		service.submitTransaction(tx);
		Context.flushSession();

		Assert.assertEquals(item0Qty, txItem0.getQuantityOrdered());
		Assert.assertFalse(source.getItems().contains(item0));
	}


	/**
	 * @verifies set the correct quantity type for the transaction item quantity
	 * @see IStockRoomService#submitTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test
	public void submitTransaction_shouldSetTheCorrectQuantityTypeForTheTransactionItemQuantity() throws Exception {
		StockRoom source = stockRoomDataService.getById(1);
		StockRoom destination = stockRoomDataService.getById(2);
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getDistribution(), source, null);
		StockRoomItem item0 = stockRoomDataService.getItem(source, itemService.getById(0), null);
		StockRoomTransactionItem txItem0 = tx.addItem(item0, 6);

		service.submitTransaction(tx);
		Context.flushSession();

		Assert.assertEquals(6, txItem0.getQuantityOrdered());
		Assert.assertEquals(6, txItem0.getQuantityReserved());
		Assert.assertEquals(0, txItem0.getQuantityTransferred());

		tx = service.createTransaction(WellKnownTransactionTypes.getTransfer(), source, destination);
		StockRoomItem item1 = stockRoomDataService.getItem(source, itemService.getById(1), null);
		StockRoomTransactionItem txItem1 = tx.addItem(item1, 6);

		service.submitTransaction(tx);
		Context.flushSession();

		Assert.assertEquals(6, txItem1.getQuantityOrdered());
		Assert.assertEquals(0, txItem1.getQuantityReserved());
		Assert.assertEquals(6, txItem1.getQuantityTransferred());
	}

	/**
	 * @verifies not attempt to update the source stock room if none is defined
	 * @see IStockRoomService#submitTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test
	public void submitTransaction_shouldNotAttemptToUpdateTheSourceStockRoomIfNoneIsDefined() throws Exception {
		StockRoom destination = stockRoomDataService.getById(1);
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getIntake(), null, destination);
		tx.addItem(itemService.getById(0), null, 6);

		service.submitTransaction(tx);
		Context.flushSession();
	}

	/**
	 * @verifies throw an APIException if the transaction type is null
	 * @see IStockRoomService#submitTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test(expected = APIException.class)
	public void submitTransaction_shouldThrowAnAPIExceptionIfTheTransactionTypeIsNull() throws Exception {
		StockRoom source = stockRoomDataService.getById(1);
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getDistribution(), source, null);
		tx.setInstanceType(null);
		tx.addItem(itemService.getById(0), null, 10);

		service.submitTransaction(tx);
	}

	/**
	 * @verifies throw an APIException if the transaction has no items
	 * @see IStockRoomService#submitTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test(expected = APIException.class)
	public void submitTransaction_shouldThrowAnAPIExceptionIfTheTransactionHasNoItems() throws Exception {
		StockRoom source = stockRoomDataService.getById(1);
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getDistribution(), source, null);

		service.submitTransaction(tx);
	}

	/**
	 * @verifies throw an APIException if the transaction type requires a source and the source is null
	 * @see IStockRoomService#submitTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test(expected = APIException.class)
	public void submitTransaction_shouldThrowAnAPIExceptionIfTheTransactionTypeRequiresASourceAndTheSourceIsNull() throws Exception {
		StockRoom source = stockRoomDataService.getById(1);
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getDistribution(), null, null);

		StockRoomItem item0 = stockRoomDataService.getItem(source, itemService.getById(0), null);
		tx.addItem(item0, 10);

		service.submitTransaction(tx);
	}

	/**
	 * @verifies throw an APIException if the transaction type requires a destination and the destination is null
	 * @see IStockRoomService#submitTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test(expected = APIException.class)
	public void submitTransaction_shouldThrowAnAPIExceptionIfTheTransactionTypeRequiresADestinationAndTheDestinationIsNull() throws Exception {
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getIntake(), null, null);
		tx.addItem(itemService.getById(0), null, 10);

		service.submitTransaction(tx);
	}

	/**
	 * @verifies update the destination stock room item quantities
	 * @see IStockRoomService#completeTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test
	public void completeTransaction_shouldUpdateTheDestinationStockRoomItemQuantities() throws Exception {
		StockRoom destination = stockRoomDataService.getById(1);
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getIntake(), null, destination);

		Calendar cal = Calendar.getInstance();
		cal.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);

		StockRoomTransactionItem txItem0 = tx.addItem(itemService.getById(0), null, 6);
		StockRoomTransactionItem txItem2 = tx.addItem(itemService.getById(2), cal.getTime(), 8);

		StockRoomItem item0 = stockRoomDataService.getItem(destination, txItem0.getItem(), null);
		StockRoomItem item2 = stockRoomDataService.getItem(destination, txItem2.getItem(), txItem2.getExpiration());
		int item0Qty = item0.getQuantity();
		int item2Qty = item2.getQuantity();

		service.submitTransaction(tx);
		Context.flushSession();

		Assert.assertEquals(6, txItem0.getQuantityOrdered());
		Assert.assertEquals(8, txItem2.getQuantityOrdered());

		service.completeTransaction(tx);
		Context.flushSession();

		Assert.assertEquals(item0Qty + 6, item0.getQuantity());
		Assert.assertEquals(item2Qty + 8, item2.getQuantity());
	}

	/**
	 * @verifies create a new item if the destination stock room does not have the item
	 * @see IStockRoomService#completeTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test
	public void completeTransaction_shouldCreateANewItemIfTheDestinationStockRoomDoesNotHaveTheItem() throws Exception {
		StockRoom destination = stockRoomDataService.getById(0);
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getIntake(), null, destination);

		Calendar cal = Calendar.getInstance();
		cal.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);

		StockRoomTransactionItem txItem0 = tx.addItem(itemService.getById(0), null, 6);
		StockRoomTransactionItem txItem2 = tx.addItem(itemService.getById(2), cal.getTime(), 8);

		service.completeTransaction(tx);
		Context.flushSession();

		Assert.assertEquals(6, txItem0.getQuantityOrdered());
		Assert.assertEquals(8, txItem2.getQuantityOrdered());

		Set<StockRoomItem> items = destination.getItems();
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());

		StockRoomItem item0 = stockRoomDataService.getItem(destination, txItem0.getItem(), null);
		Assert.assertNotNull(item0);
		Assert.assertNull(item0.getExpiration());
		Assert.assertEquals(6, item0.getQuantity());

		StockRoomItem item2 = stockRoomDataService.getItem(destination, txItem2.getItem(), txItem2.getExpiration());
		Assert.assertNotNull(item2);
		Assert.assertEquals(cal.getTime(), item2.getExpiration());
		Assert.assertEquals(8, item2.getQuantity());
	}

	/**
	 * @verifies set the transaction item quantity to zero
	 * @see IStockRoomService#completeTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test
	public void completeTransaction_shouldSetTheTransactionItemQuantityToZero() throws Exception {
		StockRoom destination = stockRoomDataService.getById(1);
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getIntake(), null, destination);

		Calendar cal = Calendar.getInstance();
		cal.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);

		StockRoomTransactionItem txItem0 = tx.addItem(itemService.getById(0), null, 6);
		StockRoomTransactionItem txItem2 = tx.addItem(itemService.getById(2), cal.getTime(), 8);

		service.submitTransaction(tx);
		Context.flushSession();

		Assert.assertEquals(6, txItem0.getQuantityOrdered());
		Assert.assertEquals(6, txItem0.getQuantityReserved());
		Assert.assertEquals(0, txItem0.getQuantityTransferred());

		Assert.assertEquals(8, txItem2.getQuantityOrdered());
		Assert.assertEquals(8, txItem2.getQuantityReserved());
		Assert.assertEquals(0, txItem2.getQuantityTransferred());

		service.completeTransaction(tx);
		Context.flushSession();

		Assert.assertEquals(6, txItem0.getQuantityOrdered());
		Assert.assertEquals(0, txItem0.getQuantityReserved());
		Assert.assertEquals(0, txItem0.getQuantityTransferred());

		Assert.assertEquals(8, txItem2.getQuantityOrdered());
		Assert.assertEquals(0, txItem2.getQuantityReserved());
		Assert.assertEquals(0, txItem2.getQuantityTransferred());
	}

	/**
	 * @verifies not attempt to update the destination stock room if none is defined
	 * @see IStockRoomService#completeTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test
	public void completeTransaction_shouldNotAttemptToUpdateTheDestinationStockRoomIfNoneIsDefined() throws Exception {
		StockRoom source = stockRoomDataService.getById(1);
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getDistribution(), source, null);

		Calendar cal = Calendar.getInstance();
		cal.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);

		StockRoomItem item0 = stockRoomDataService.getItem(source, itemService.getById(0), null);
		StockRoomItem item2 = stockRoomDataService.getItem(source, itemService.getById(2), cal.getTime());

		tx.addItem(item0, 6);
		tx.addItem(item2, 8);

		service.submitTransaction(tx);
		Context.flushSession();

		service.completeTransaction(tx);
		Context.flushSession();
	}

	/**
	 * @verifies throw an APIException if the transaction type is null
	 * @see IStockRoomService#completeTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test(expected = APIException.class)
	public void completeTransaction_shouldThrowAnAPIExceptionReferenceExceptionIfTheTransactionTypeIsNull() throws Exception {
		StockRoom destination = stockRoomDataService.getById(1);
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getIntake(), null, destination);
		tx.setInstanceType(null);

		tx.addItem(itemService.getById(0), null, 10);

		service.completeTransaction(tx);
	}

	/**
	 * @verifies throw an APIException if the transaction has no items
	 * @see IStockRoomService#completeTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test(expected = APIException.class)
	public void completeTransaction_shouldThrowAnAPIExceptionIfTheTransactionHasNoItems() throws Exception {
		StockRoom destination = stockRoomDataService.getById(1);
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getIntake(), null, destination);

		service.completeTransaction(tx);
	}

	/**
	 * @verifies throw an APIException if the transaction type requires a source and the source is null
	 * @see IStockRoomService#completeTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test(expected = APIException.class)
	public void completeTransaction_shouldThrowAnAPIExceptionIfTheTransactionTypeRequiresASourceAndTheSourceIsNull() throws Exception {
		StockRoom source = stockRoomDataService.getById(1);
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getDistribution(), null, null);

		StockRoomItem item0 = stockRoomDataService.getItem(source, itemService.getById(0), null);
		tx.addItem(item0, 10);

		service.submitTransaction(tx);
	}

	/**
	 * @verifies throw an APIException if the transaction type requires a destination and the destination is null
	 * @see IStockRoomService#completeTransaction(org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction)
	 */
	@Test(expected = APIException.class)
	public void completeTransaction_shouldThrowAnAPIExceptionIfTheTransactionTypeRequiresADestinationAndTheDestinationIsNull() throws Exception {
		StockRoomTransaction tx = service.createTransaction(WellKnownTransactionTypes.getIntake(), null, null);
		tx.addItem(itemService.getById(0), null, 10);

		service.submitTransaction(tx);
	}
}
