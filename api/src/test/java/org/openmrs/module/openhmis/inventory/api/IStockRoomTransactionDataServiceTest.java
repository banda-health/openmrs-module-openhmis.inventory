package org.openmrs.module.openhmis.inventory.api;

import liquibase.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataServiceTest;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.inventory.api.model.*;

import java.util.*;

public class IStockRoomTransactionDataServiceTest
		extends IObjectDataServiceTest<IStockRoomTransactionDataService, StockRoomTransaction> {
	protected IStockRoomDataService stockRoomService;
	protected IStockRoomTransactionTypeDataService typeService;
	protected IItemDataService itemService;

	@Override
	public void before() throws Exception {
		super.before();

		itemService = Context.getService(IItemDataService.class);
		typeService = Context.getService(IStockRoomTransactionTypeDataService.class);
		stockRoomService = Context.getService(IStockRoomDataService.class);

		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IDepartmentDataServiceTest.DEPARTMENT_DATASET);
		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(IStockRoomDataServiceTest.DATASET);
	}

	@Override
	protected StockRoomTransaction createEntity(boolean valid) {
		StockRoomTransaction transaction = new StockRoomTransaction();

		if (valid) {
			transaction.setTransactionNumber(UUID.randomUUID().toString());
		}

		transaction.setTransactionType(WellKnownTransactionTypes.getIntake());
		transaction.setDestination(stockRoomService.getById(0));
		transaction.setStatus(StockRoomTransactionStatus.PENDING);

		StockRoomTransactionItem transactionItem = new StockRoomTransactionItem();
		transactionItem.setImportTransaction(service.getById(0));
		transactionItem.setItem(itemService.getById(0));
		transactionItem.setQuantityOrdered(5);
		transactionItem.setQuantityReserved(5);

		StockRoomTransactionItem transactionItem2 = new StockRoomTransactionItem();
		transactionItem2.setImportTransaction(service.getById(0));
		transactionItem2.setItem(itemService.getById(1));
		transactionItem2.setQuantityOrdered(2);
		transactionItem2.setQuantityReserved(2);

		transaction.addItem(transactionItem);
		transaction.addItem(transactionItem2);

		return transaction;
	}

	@Override
	protected int getTestEntityCount() {
		return 3;
	}

	@Override
	protected void updateEntityFields(StockRoomTransaction transaction) {
		transaction.setSource(stockRoomService.getById(1));
		transaction.setDestination(stockRoomService.getById(2));

		Set<StockRoomTransactionItem> items = transaction.getItems();
		if (items.size() > 0) {
			// Update an existing item quantity
			Iterator<StockRoomTransactionItem> iterator = items.iterator();
			StockRoomTransactionItem item = iterator.next();
			item.setQuantityOrdered(item.getQuantityOrdered() + 1);
			item.setQuantityReserved(item.getQuantityReserved() + 1);

			if (items.size() > 1) {
				// Delete an existing item
				item = iterator.next();

				items.remove(item);
			}
		}

		// Add a new item
		StockRoomTransactionItem transactionItem = new StockRoomTransactionItem();
		transactionItem.setImportTransaction(service.getById(0));
		transactionItem.setItem(itemService.getById(2));
		transactionItem.setQuantityOrdered(3);
		transactionItem.setQuantityReserved(3);

		transaction.addItem(transactionItem);
	}

	@Override
	protected void assertEntity(StockRoomTransaction expected, StockRoomTransaction actual) {
		super.assertEntity(expected, actual);

		Assert.assertEquals(expected.getTransactionNumber(), actual.getTransactionNumber());
		Assert.assertEquals(expected.getTransactionType(), actual.getTransactionType());
		Assert.assertEquals(expected.getStatus(), actual.getStatus());
		Assert.assertEquals(expected.getSource(), actual.getSource());
		Assert.assertEquals(expected.getDestination(), actual.getDestination());
		Assert.assertEquals(expected.isImportTransaction(), actual.isImportTransaction());

		assertCollection(expected.getItems(), actual.getItems(), new Action2<StockRoomTransactionItem, StockRoomTransactionItem>() {
			@Override
			public void apply(StockRoomTransactionItem expectedTransItem, StockRoomTransactionItem actualTransItem) {
				assertOpenmrsObject(expectedTransItem, actualTransItem);

				Assert.assertEquals(expectedTransItem.getTransaction().getId(), actualTransItem.getTransaction().getId());
				Assert.assertEquals(expectedTransItem.getImportTransaction().getId(), actualTransItem.getImportTransaction().getId());
				Assert.assertEquals(expectedTransItem.getItem().getId(), actualTransItem.getItem().getId());
				Assert.assertEquals(expectedTransItem.getQuantityOrdered(), actualTransItem.getQuantityOrdered());
				Assert.assertEquals(expectedTransItem.getQuantityReserved(), actualTransItem.getQuantityReserved());
				Assert.assertEquals(expectedTransItem.getQuantityTransferred(), actualTransItem.getQuantityTransferred());
				Assert.assertEquals(expectedTransItem.getExpiration(), actualTransItem.getExpiration());
			}
		});
	}

	@Override
	public void purge_shouldDeleteTheSpecifiedObject() throws Exception {
		StockRoomTransaction entity = createEntity(true);
		service.save(entity);
		Context.flushSession();

		entity = service.getById(entity.getId());
		Assert.assertNotNull(entity);

		service.purge(entity);
		Context.flushSession();

		entity = service.getById(entity.getId());
		Assert.assertNull(entity);
	}

	@Override
	public void getAll_shouldReturnAnEmptyListIfThereAreNoObjects() throws Exception {
		List<StockRoom> rooms = stockRoomService.getAll();
		for (StockRoom room : rooms) {
			stockRoomService.purge(room);
		}

		Context.flushSession();

		super.getAll_shouldReturnAnEmptyListIfThereAreNoObjects();
	}

	@Test
	public void save_shouldAddMapRecordsToSourceAndDestinationStockRooms() throws Exception {
		StockRoomTransaction transaction = new StockRoomTransaction();
		transaction.setTransactionNumber("123");
		transaction.setTransactionType(WellKnownTransactionTypes.getTransfer());
		transaction.setStatus(StockRoomTransactionStatus.PENDING);
		transaction.setCreator(Context.getAuthenticatedUser());

		service.save(transaction);
		Context.flushSession();

		StockRoom source = stockRoomService.getById(0);
		StockRoom destination = stockRoomService.getById(1);

		Assert.assertFalse(source.getTransactions().contains(transaction));
		Assert.assertFalse(destination.getTransactions().contains(transaction));

		transaction.setSource(source);
		transaction.setDestination(destination);

		Assert.assertTrue(source.getTransactions().contains(transaction));
		Assert.assertTrue(destination.getTransactions().contains(transaction));

		service.save(transaction);
		Context.flushSession();

		source = stockRoomService.getById(0);
		destination = stockRoomService.getById(1);

		Assert.assertTrue(source.getTransactions().contains(transaction));
		Assert.assertTrue(destination.getTransactions().contains(transaction));
	}

	@Test
	public void save_shouldRemoveMapRecordsFromNullSourceOrDestinationStockRooms() throws Exception {
		StockRoomTransaction transaction = new StockRoomTransaction();
		transaction.setTransactionNumber("123");
		transaction.setTransactionType(WellKnownTransactionTypes.getTransfer());
		transaction.setStatus(StockRoomTransactionStatus.PENDING);
		transaction.setCreator(Context.getAuthenticatedUser());

		StockRoom source = stockRoomService.getById(0);
		StockRoom destination = stockRoomService.getById(1);

		transaction.setSource(source);
		transaction.setDestination(destination);

		service.save(transaction);
		Context.flushSession();

		source = stockRoomService.getById(0);
		destination = stockRoomService.getById(1);

		Assert.assertTrue(source.getTransactions().contains(transaction));
		Assert.assertTrue(destination.getTransactions().contains(transaction));

		transaction.setSource(null);
		transaction.setDestination(null);

		Assert.assertFalse(source.getTransactions().contains(transaction));
		Assert.assertFalse(destination.getTransactions().contains(transaction));

		service.save(transaction);

		source = stockRoomService.getById(0);
		destination = stockRoomService.getById(1);

		Assert.assertFalse(source.getTransactions().contains(transaction));
		Assert.assertFalse(destination.getTransactions().contains(transaction));
	}

	/**
	 * @verifies return null if transaction number not found
	 * @see IStockRoomTransactionDataService#getTransactionByNumber(String)
	 */
	@Test
	public void getTransactionByNumber_shouldReturnNullIfTransactionNumberNotFound() throws Exception {
		StockRoomTransaction result = service.getTransactionByNumber("Not a valid number");

		Assert.assertNull(result);
	}

	/**
	 * @verifies return transaction with specified transaction number
	 * @see IStockRoomTransactionDataService#getTransactionByNumber(String)
	 */
	@Test
	public void getTransactionByNumber_shouldReturnTransactionWithSpecifiedTransactionNumber() throws Exception {
		StockRoomTransaction trans = service.getById(0);

		StockRoomTransaction result = service.getTransactionByNumber(trans.getTransactionNumber());

		Assert.assertNotNull(result);
		assertEntity(trans, result);
	}

	/**
	 * @verifies throw IllegalArgumentException if transaction number is null
	 * @see IStockRoomTransactionDataService#getTransactionByNumber(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getTransactionByNumber_shouldThrowIllegalArgumentExceptionIfTransactionNumberIsNull() throws Exception {
		service.getTransactionByNumber(null);
	}

	/**
	 * @verifies throw IllegalArgumentException if transaction number is empty
	 * @see IStockRoomTransactionDataService#getTransactionByNumber(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getTransactionByNumber_shouldThrowIllegalArgumentExceptionIfTransactionNumberIsEmpty() throws Exception {
		service.getTransactionByNumber("");
	}

	/**
	 * @verifies throw IllegalArgumentException is transaction number is longer than 50 characters
	 * @see IStockRoomTransactionDataService#getTransactionByNumber(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getTransactionByNumber_shouldThrowIllegalArgumentExceptionIsTransactionNumberIsLongerThan50Characters() throws Exception {
		service.getTransactionByNumber(StringUtils.repeat("A", 51));
	}

	/**
	 * @verifies return transactions for specified room
	 * @see IStockRoomTransactionDataService#getTransactionsByRoom(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getTransactionsByRoom_shouldReturnTransactionsForSpecifiedRoom() throws Exception {
		StockRoom room = stockRoomService.getById(2);

		List<StockRoomTransaction> results = service.getTransactionsByRoom(room, null);

		assertCollection(room.getTransactions(), results, new Action2<StockRoomTransaction, StockRoomTransaction>() {
			@Override
			public void apply(StockRoomTransaction expected, StockRoomTransaction actual) {
				assertEntity(expected, actual);
			}
		});
	}

	/**
	 * @verifies return empty list when no transactions
	 * @see IStockRoomTransactionDataService#getTransactionsByRoom(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getTransactionsByRoom_shouldReturnEmptyListWhenNoTransactions() throws Exception {
		StockRoom room = new StockRoom();
		room.setLocation(Context.getLocationService().getLocation(1));
		room.setName("New Room");
		room.setCreator(Context.getAuthenticatedUser());
		room.setDateCreated(new Date());

		stockRoomService.save(room);
		Context.flushSession();

		List<StockRoomTransaction> results = service.getTransactionsByRoom(room, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return paged transactions when paging is specified
	 * @see IStockRoomTransactionDataService#getTransactionsByRoom(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getTransactionsByRoom_shouldReturnPagedTransactionsWhenPagingIsSpecified() throws Exception {
		StockRoom room = stockRoomService.getById(2);
		Assert.assertEquals(2, room.getTransactions().size());

		// Only return a single result per page
		PagingInfo paging = new PagingInfo(1, 1);
		List<StockRoomTransaction> results = service.getTransactionsByRoom(room, paging);
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(2, (long)paging.getTotalRecordCount());
		StockRoomTransaction[] roomTrans = new StockRoomTransaction[2];
		room.getTransactions().toArray(roomTrans);
		assertEntity(roomTrans[0], results.get(0));

		// Get the next result
		paging.setPage(2);
		results = service.getTransactionsByRoom(room, paging);
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(roomTrans[1], results.get(0));
	}

	/**
	 * @verifies return all transactions when paging is null
	 * @see IStockRoomTransactionDataService#getTransactionsByRoom(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getTransactionsByRoom_shouldReturnAllTransactionsWhenPagingIsNull() throws Exception {
		StockRoom room = stockRoomService.getById(2);
		Assert.assertEquals(2, room.getTransactions().size());

		List<StockRoomTransaction> results = service.getTransactionsByRoom(room, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies return transactions with any status
	 * @see IStockRoomTransactionDataService#getTransactionsByRoom(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getTransactionsByRoom_shouldReturnTransactionsWithAnyStatus() throws Exception {
		StockRoom room = stockRoomService.getById(2);
		Assert.assertEquals(2, room.getTransactions().size());

		StockRoomTransaction[] roomTrans = new StockRoomTransaction[2];
		room.getTransactions().toArray(roomTrans);
		Assert.assertEquals(StockRoomTransactionStatus.COMPLETED, roomTrans[0].getStatus());
		Assert.assertEquals(StockRoomTransactionStatus.PENDING, roomTrans[1].getStatus());

		List<StockRoomTransaction> results = service.getTransactionsByRoom(room, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies throw NullPointerException when stock room is null
	 * @see IStockRoomTransactionDataService#getTransactionsByRoom(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = NullPointerException.class)
	public void getTransactionsByRoom_shouldThrowNullPointerExceptionWhenStockRoomIsNull() throws Exception {
		service.getTransactionsByRoom(null, null);
	}

	/**
	 * @verifies return all pending transactions for specified user
	 * @see IStockRoomTransactionDataService#getUserPendingTransactions(org.openmrs.User, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getUserPendingTransactions_shouldReturnAllPendingTransactionsForSpecifiedUser() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockRoomTransaction> results = service.getUserPendingTransactions(user, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());

		assertEntity(service.getById(2), results.get(0));
	}

	/**
	 * @verifies not return any completed or cancelled transactions
	 * @see IStockRoomTransactionDataService#getUserPendingTransactions(org.openmrs.User, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getUserPendingTransactions_shouldNotReturnAnyCompletedOrCancelledTransactions() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockRoomTransaction> results = service.getUserPendingTransactions(user, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(StockRoomTransactionStatus.PENDING, results.get(0).getStatus());
	}

	/**
	 * @verifies return empty list when no transactions
	 * @see IStockRoomTransactionDataService#getUserPendingTransactions(org.openmrs.User, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getUserPendingTransactions_shouldReturnEmptyListWhenNoTransactions() throws Exception {
		User user = Context.getUserService().getUser(1);

		StockRoomTransaction transaction = service.getById(2);
		transaction.setStatus(StockRoomTransactionStatus.COMPLETED);

		service.save(transaction);
		Context.flushSession();

		List<StockRoomTransaction> results = service.getUserPendingTransactions(user, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return paged transactions when paging is specified
	 * @see IStockRoomTransactionDataService#getUserPendingTransactions(org.openmrs.User, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getUserPendingTransactions_shouldReturnPagedTransactionsWhenPagingIsSpecified() throws Exception {
		User user = Context.getUserService().getUser(1);
		StockRoomTransaction transaction = service.getById(1);
		transaction.setStatus(StockRoomTransactionStatus.PENDING);

		service.save(transaction);
		Context.flushSession();

		PagingInfo paging = new PagingInfo(1, 1);
		List<StockRoomTransaction> results = service.getUserPendingTransactions(user, paging);
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(2, (long)paging.getTotalRecordCount());
		int id = results.get(0).getId();

		paging.setPage(2);
		results = service.getUserPendingTransactions(user, paging);
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertFalse(id == results.get(0).getId());
	}

	/**
	 * @verifies return all transactions when paging is null
	 * @see IStockRoomTransactionDataService#getUserPendingTransactions(org.openmrs.User, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getUserPendingTransactions_shouldReturnAllTransactionsWhenPagingIsNull() throws Exception {
		User user = Context.getUserService().getUser(1);
		StockRoomTransaction transaction = service.getById(1);
		transaction.setStatus(StockRoomTransactionStatus.PENDING);

		service.save(transaction);
		Context.flushSession();

		List<StockRoomTransaction> results = service.getUserPendingTransactions(user, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies throw NullPointerException when user is null
	 * @see IStockRoomTransactionDataService#getUserPendingTransactions(org.openmrs.User, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = NullPointerException.class)
	public void getUserPendingTransactions_shouldThrowNullPointerExceptionWhenUserIsNull() throws Exception {
		service.getUserPendingTransactions(null, null);
	}

	/**
	 * @verifies return pending transactions created by user
	 * @see IStockRoomTransactionDataService#getUserPendingTransactions(org.openmrs.User, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getUserPendingTransactions_shouldReturnPendingTransactionsCreatedByUser() throws Exception {
		User user = Context.getUserService().getUser(5506);

		StockRoomTransaction transaction = createEntity(true);
		transaction.setCreator(user);

		service.save(transaction);
		Context.flushSession();

		List<StockRoomTransaction> transactions = service.getUserPendingTransactions(user, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Assert.assertEquals(transaction.getId(), transactions.get(0).getId());
	}

	/**
	 * @verifies return pending transactions with user as attribute type user
	 * @see IStockRoomTransactionDataService#getUserPendingTransactions(org.openmrs.User, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getUserPendingTransactions_shouldReturnPendingTransactionsWithUserAsAttributeTypeUser() throws Exception {
		User baseUser = Context.getUserService().getUser(0);
		User user = Context.getUserService().getUser(5506);

		StockRoomTransaction transaction = createEntity(true);
		transaction.setCreator(baseUser);

		StockRoomTransactionTypeAttributeType type = new StockRoomTransactionTypeAttributeType();
		type.setName("user");
		type.setUser(user);
		type.setRequired(true);
		type.setAttributeOrder(0);
		type.setOwner(transaction.getTransactionType());

		transaction.getTransactionType().addAttributeType(type);

		typeService.save(transaction.getTransactionType());
		service.save(transaction);
		Context.flushSession();

		transaction = service.getById(transaction.getId());

		List<StockRoomTransaction> transactions = service.getUserPendingTransactions(user, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Assert.assertEquals(transaction.getId(), transactions.get(0).getId());
	}

	/**
	 * @verifies return pending transactions with user role as attribute type role
	 * @see IStockRoomTransactionDataService#getUserPendingTransactions(org.openmrs.User, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getUserPendingTransactions_shouldReturnPendingTransactionsWithUserRoleAsAttributeTypeRole() throws Exception {
		User baseUser = Context.getUserService().getUser(0);
		User user = Context.getUserService().getUser(5506);
		Set<Role> roles = user.getRoles();
		Role[] roleArray = new Role[roles.size()];
		roles.toArray(roleArray);

		StockRoomTransaction transaction = createEntity(true);
		transaction.setCreator(baseUser);

		StockRoomTransactionTypeAttributeType type = new StockRoomTransactionTypeAttributeType();
		type.setName("role");
		type.setRole(roleArray[0]);
		type.setRequired(true);
		type.setAttributeOrder(0);

		transaction.getTransactionType().addAttributeType(type);

		service.save(transaction);
		Context.flushSession();

		List<StockRoomTransaction> transactions = service.getUserPendingTransactions(user, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Assert.assertEquals(transaction.getId(), transactions.get(0).getId());
	}

	/**
	 * @verifies return pending transactions with user role as child role of attribute type role
	 * @see IStockRoomTransactionDataService#getUserPendingTransactions(org.openmrs.User, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getUserPendingTransactions_shouldReturnPendingTransactionsWithUserRoleAsChildRoleOfAttributeTypeRole() throws Exception {
		User baseUser = Context.getUserService().getUser(0);

		// This user has the Child Role which is a child of the Parent role
		User user = Context.getUserService().getUser(5506);

		StockRoomTransaction transaction = createEntity(true);
		transaction.setCreator(baseUser);

		// Set up this transaction type to be for users of the Parent role
		StockRoomTransactionTypeAttributeType type = new StockRoomTransactionTypeAttributeType();
		type.setName("role");
		type.setRole(Context.getUserService().getRole("Parent"));
		type.setRequired(true);
		type.setAttributeOrder(0);

		transaction.getTransactionType().addAttributeType(type);

		service.save(transaction);
		Context.flushSession();

		List<StockRoomTransaction> transactions = service.getUserPendingTransactions(user, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Assert.assertEquals(transaction.getId(), transactions.get(0).getId());
	}

	/**
	 * @verifies return pending transactions with user role as grandchild role of attribute type role
	 * @see IStockRoomTransactionDataService#getUserPendingTransactions(org.openmrs.User, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getUserPendingTransactions_shouldReturnPendingTransactionsWithUserRoleAsGrandchildRoleOfAttributeTypeRole() throws Exception {
		User baseUser = Context.getUserService().getUser(0);
		User user = Context.getUserService().getUser(5506);

		Set<Role> roles = new HashSet<Role>();
		roles.add(Context.getUserService().getRole("Grandchild"));
		user.setRoles(roles);
		Context.getUserService().saveUser(user, "1wWhatever");
		Context.flushSession();

		StockRoomTransaction transaction = createEntity(true);
		transaction.setCreator(baseUser);

		// Set up this transaction type to be for users of the Parent role
		StockRoomTransactionTypeAttributeType type = new StockRoomTransactionTypeAttributeType();
		type.setName("role");
		type.setRole(Context.getUserService().getRole("Parent"));
		type.setRequired(true);
		type.setAttributeOrder(0);

		transaction.getTransactionType().addAttributeType(type);

		service.save(transaction);
		Context.flushSession();

		List<StockRoomTransaction> transactions = service.getUserPendingTransactions(user, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());
		Assert.assertEquals(transaction.getId(), transactions.get(0).getId());
	}

	/**
	 * @verifies not return transactions when user role not descendant of attribute type role
	 * @see IStockRoomTransactionDataService#getUserPendingTransactions(org.openmrs.User, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getUserPendingTransactions_shouldNotReturnTransactionsWhenUserRoleNotDescendantOfAttributeTypeRole() throws Exception {
		User baseUser = Context.getUserService().getUser(0);
		User user = Context.getUserService().getUser(5506);

		Set<Role> roles = new HashSet<Role>();
		roles.add(Context.getUserService().getRole("Other"));
		user.setRoles(roles);
		Context.getUserService().saveUser(user, "1wWhatever");
		Context.flushSession();

		StockRoomTransaction transaction = createEntity(true);
		transaction.setCreator(baseUser);

		// Set up this transaction type to be for users of the Parent role
		StockRoomTransactionTypeAttributeType type = new StockRoomTransactionTypeAttributeType();
		type.setName("role");
		type.setRole(Context.getUserService().getRole("Parent"));
		type.setRequired(true);
		type.setAttributeOrder(0);

		transaction.getTransactionType().addAttributeType(type);

		service.save(transaction);
		Context.flushSession();

		List<StockRoomTransaction> transactions = service.getUserPendingTransactions(user, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(0, transactions.size());
	}

	/**
	 * @verifies not return transactions when user role is parent of attribute type role
	 * @see IStockRoomTransactionDataService#getUserPendingTransactions(org.openmrs.User, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getUserPendingTransactions_shouldNotReturnTransactionsWhenUserRoleIsParentOfAttributeTypeRole() throws Exception {
		User baseUser = Context.getUserService().getUser(0);
		User user = Context.getUserService().getUser(5506);

		Set<Role> roles = new HashSet<Role>();
		roles.add(Context.getUserService().getRole("Parent"));
		user.setRoles(roles);
		Context.getUserService().saveUser(user, "1wWhatever");
		Context.flushSession();

		StockRoomTransaction transaction = createEntity(true);
		transaction.setCreator(baseUser);

		// Set up this transaction type to be for users of the Parent role
		StockRoomTransactionTypeAttributeType type = new StockRoomTransactionTypeAttributeType();
		type.setName("role");
		type.setRole(Context.getUserService().getRole("Child"));
		type.setRequired(true);
		type.setAttributeOrder(0);

		transaction.getTransactionType().addAttributeType(type);

		service.save(transaction);
		Context.flushSession();

		List<StockRoomTransaction> transactions = service.getUserPendingTransactions(user, null);

		Assert.assertNotNull(transactions);
		Assert.assertEquals(0, transactions.size());
	}
}
