package org.openmrs.module.openhmis.inventory.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

import com.google.common.collect.Iterators;

public class IStockOperationDataServiceTest extends IMetadataDataServiceTest<IStockOperationDataService, StockOperation> {
	IStockOperationTypeDataService typeService;
	IStockroomDataService stockroomService;
	IItemDataService itemService;

	IItemDataServiceTest itemTest;

	@Override
	public void before() throws Exception {
		super.before();

		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(IStockroomDataServiceTest.DATASET);

		typeService = Context.getService(IStockOperationTypeDataService.class);
		stockroomService = Context.getService(IStockroomDataService.class);
		itemService = Context.getService(IItemDataService.class);

		itemTest = new IItemDataServiceTest();
	}

	@Override
	public StockOperation createEntity(boolean valid) {
		if (stockroomService == null) {
			stockroomService = Context.getService(IStockroomDataService.class);
		}
		if (itemService == null) {
			itemService = Context.getService(IItemDataService.class);
		}

		StockOperation op = new StockOperation();

		if (valid) {
			op.setInstanceType(WellKnownOperationTypes.getReceipt());
		}

		op.setDestination(stockroomService.getById(0));
		op.setStatus(StockOperationStatus.NEW);
		op.setOperationNumber("Operation Number");
		op.setOperationDate(new Date());

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
		op.setSource(stockroomService.getById(0));
		op.setDestination(stockroomService.getById(1));
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
        Assert.assertEquals(expected.getInstitution(), actual.getInstitution());

		assertCollection(expected.getItems(), actual.getItems(), new Action2<StockOperationItem, StockOperationItem>() {
			@Override
			public void apply(StockOperationItem expectedItem, StockOperationItem actualItem) {
				IStockroomDataServiceTest.assertItemStockDetailBase(expectedItem, actualItem);

				Assert.assertEquals(expectedItem.getOperation(), actualItem.getOperation());
			}
		} );

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
				Assert.assertEquals(expected.getStockroom(), actual.getStockroom());
				Assert.assertEquals(expected.getPatient(), actual.getPatient());
				Assert.assertEquals(expected.getInstitution(), actual.getInstitution());
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

	@Test
	public void save_shouldAddMapRecordsToSourceAndDestinationStockRooms() throws Exception {
		StockOperation operation = new StockOperation();
		operation.setOperationNumber("123");
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setCreator(Context.getAuthenticatedUser());
		operation.setOperationDate(new Date());

		Stockroom source = stockroomService.getById(0);
		Stockroom destination = stockroomService.getById(1);
		Assert.assertFalse(source.getOperations().contains(operation));
		Assert.assertFalse(destination.getOperations().contains(operation));

		operation.setSource(source);
		operation.setDestination(destination);

		Assert.assertTrue(source.getOperations().contains(operation));
		Assert.assertTrue(destination.getOperations().contains(operation));

		service.save(operation);
		Context.flushSession();

		operation = service.getById(operation.getId());
		source = stockroomService.getById(0);
		destination = stockroomService.getById(1);

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
		operation.setOperationDate(new Date());

		Stockroom source = stockroomService.getById(0);
		Stockroom destination = stockroomService.getById(1);

		operation.setSource(source);
		operation.setDestination(destination);

		service.save(operation);
		Context.flushSession();

		source = stockroomService.getById(0);
		destination = stockroomService.getById(1);

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
		operation.setOperationDate(new Date());

		Stockroom source = stockroomService.getById(0);
		Stockroom destination = stockroomService.getById(1);

		operation.setSource(source);
		operation.setDestination(destination);

		service.save(operation);
		Context.flushSession();

		source = stockroomService.getById(0);
		destination = stockroomService.getById(1);

		Assert.assertTrue(source.getOperations().contains(operation));
		Assert.assertTrue(destination.getOperations().contains(operation));

		Stockroom newSource = stockroomService.getById(2);

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
	 * @see IStockOperationDataService#getOperationsByRoom(Stockroom, PagingInfo)
	 */
	@Test
	public void getOperationsByRoom_shouldReturnOperationsForSpecifiedRoom() throws Exception {
		Stockroom room = stockroomService.getById(2);

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
	 * @see IStockOperationDataService#getOperationsByRoom(Stockroom, PagingInfo)
	 */
	@Test
	public void getOperationsByRoom_shouldReturnEmptyListWhenNoOperations() throws Exception {
		Stockroom room = new Stockroom();
		room.setLocation(Context.getLocationService().getLocation(1));
		room.setName("New Room");
		room.setCreator(Context.getAuthenticatedUser());
		room.setDateCreated(new Date());

		stockroomService.save(room);
		Context.flushSession();

		List<StockOperation> results = service.getOperationsByRoom(room, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return paged operations when paging is specified
	 * @see IStockOperationDataService#getOperationsByRoom(Stockroom, PagingInfo)
	 */
	@Test
	public void getOperationsByRoom_shouldReturnPagedOperationsWhenPagingIsSpecified() throws Exception {
		Stockroom room = stockroomService.getById(0);
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
	 * @see IStockOperationDataService#getOperationsByRoom(Stockroom, PagingInfo)
	 */
	@Test
	public void getOperationsByRoom_shouldReturnAllOperationsWhenPagingIsNull() throws Exception {
		Stockroom room = stockroomService.getById(1);
		Assert.assertEquals(2, room.getOperations().size());

		List<StockOperation> results = service.getOperationsByRoom(room, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies return operations with any status
	 * @see IStockOperationDataService#getOperationsByRoom(Stockroom, PagingInfo)
	 */
	@Test
	public void getOperationsByRoom_shouldReturnOperationsWithAnyStatus() throws Exception {
		Stockroom room = stockroomService.getById(1);
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
	 * @see IStockOperationDataService#getOperationsByRoom(Stockroom, PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getOperationsByRoom_shouldThrowIllegalArgumentExceptionWhenStockroomIsNull() throws Exception {
		service.getOperationsByRoom(null, null);
	}

	/**
	 * @verifies throw NullPointerException if operation search is null
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getOperations_shouldThrowIllegalArgumentExceptionIfOperationSearchIsNull() throws Exception {
		service.getOperations(null, null);
	}

	/**
	 * @verifies throw NullPointerException if operation search template object is null
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getOperations_shouldThrowIllegalArgumentExceptionIfOperationSearchTemplateObjectIsNull() throws Exception {
		service.getOperations(new StockOperationSearch(null), null);
	}

	/**
	 * @verifies return an empty list if no operations are found via the search
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnAnEmptyListIfNoOperationsAreFoundViaTheSearch() throws Exception {
		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setStatus(StockOperationStatus.CANCELLED);

		List<StockOperation> results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return items filtered by number
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnItemsFilteredByNumber() throws Exception {
		StockOperation operation = service.getById(0);
		operation.setOperationNumber("ABCD-1234");

		service.save(operation);
		Context.flushSession();

		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setOperationNumber("ABCD-1234");

		List<StockOperation> results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(operation, results.get(0));

		search.setOperationNumberComparisonType(BaseObjectTemplateSearch.StringComparisonType.LIKE);
		search.getTemplate().setOperationNumber("AB%");

		results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(operation, results.get(0));
	}

	/**
	 * @verifies return items filtered by status
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnItemsFilteredByStatus() throws Exception {
		StockOperation operation = service.getById(0);
		operation.setStatus(StockOperationStatus.CANCELLED);

		service.save(operation);
		Context.flushSession();

		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setStatus(StockOperationStatus.CANCELLED);

		List<StockOperation> results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(operation, results.get(0));
	}

	/**
	 * @verifies return items filtered by type
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnItemsFilteredByType() throws Exception {
		StockOperation operation = service.getById(0);
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());

		service.save(operation);
		Context.flushSession();

		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setInstanceType(WellKnownOperationTypes.getReceipt());

		List<StockOperation> results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(operation, results.get(0));
	}

	/**
	 * @verifies return items filtered by source stockroom
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnItemsFilteredBySourceStockroom() throws Exception {
		StockOperation operation = service.getById(1);
		Stockroom room = operation.getSource();

		Context.flushSession();

		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setSource(room);

		Context.flushSession();

		List<StockOperation> test = service.getAll();
		Assert.assertNotNull(test);
		Assert.assertEquals(getTestEntityCount(), test.size());

		List<StockOperation> results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(operation, results.get(0));
	}

	/**
	 * @verifies return items filtered by destination stockroom
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnItemsFilteredByDestinationStockroom() throws Exception {
		StockOperation operation = service.getById(0);
		Stockroom room = operation.getDestination();

		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setDestination(room);

		List<StockOperation> results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(operation, results.get(0));
	}

	/**
	 * @verifies return all items if paging is null
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnAllItemsIfPagingIsNull() throws Exception {
		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setStatus(StockOperationStatus.COMPLETED);

		List<StockOperation> results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies return paged items if paging is specified
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnPagedItemsIfPagingIsSpecified() throws Exception {
		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setStatus(StockOperationStatus.COMPLETED);

		PagingInfo pagingInfo = new PagingInfo(1, 1);
		List<StockOperation> results = service.getOperations(search, pagingInfo);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(2, (long)pagingInfo.getTotalRecordCount());
	}

	/**
	 * @verifies return items filtered by creation date
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnItemsFilteredByCreationDate() throws Exception {
		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setDateCreated(service.getById(0).getDateCreated());
		search.setDateCreatedComparisonType(BaseObjectTemplateSearch.DateComparisonType.GREATER_THAN_EQUAL);

		List<StockOperation> results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies return items filtered by patient
	 * @see IStockOperationDataService#getOperations(org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnItemsFilteredByPatient() throws Exception {
        StockOperationSearch search = new StockOperationSearch();
        Patient patient = Context.getPatientService().getPatient(1);
        search.getTemplate().setPatient(patient);

        List<StockOperation> results = service.getOperations(search, null);

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
		Assert.assertEquals(getTestEntityCount(), results.size());
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
		Assert.assertEquals(getTestEntityCount(), results.size());

		results = service.getUserOperations(user, StockOperationStatus.COMPLETED, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());

		Assert.assertEquals(0, (int)results.get(0).getId());
		Assert.assertEquals(1, (int) results.get(1).getId());

		results = service.getUserOperations(user, StockOperationStatus.PENDING, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());

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
		Assert.assertEquals(getTestEntityCount(), results.size());

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
		operation.setSource(stockroomService.getById(0));

		typeService.save(operation.getInstanceType());
		service.save(operation);
		Context.flushSession();

		operation = service.getById(operation.getId());

		List<StockOperation> operations = service.getUserOperations(user, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());

		operations = service.getUserOperations(user, StockOperationStatus.COMPLETED, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(0, operations.size());

		operations = service.getUserOperations(user, StockOperationStatus.NEW, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());
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
		operation.setSource(stockroomService.getById(0));

		typeService.save(operation.getInstanceType());
		service.save(operation);
		Context.flushSession();

		List<StockOperation> operations = service.getUserOperations(user, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());

		operations = service.getUserOperations(user, StockOperationStatus.COMPLETED, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(0, operations.size());

		operations = service.getUserOperations(user, StockOperationStatus.NEW, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());
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
		operation.setSource(stockroomService.getById(0));

		typeService.save(operation.getInstanceType());
		service.save(operation);
		Context.flushSession();

		List<StockOperation> operations = service.getUserOperations(user, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());

		operations = service.getUserOperations(user, StockOperationStatus.COMPLETED, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(0, operations.size());

		operations = service.getUserOperations(user, StockOperationStatus.NEW, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());
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
		operation.setSource(stockroomService.getById(0));

		typeService.save(operation.getInstanceType());
		service.save(operation);
		Context.flushSession();

		List<StockOperation> operations = service.getUserOperations(user, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());

		operations = service.getUserOperations(user, StockOperationStatus.COMPLETED, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(0, operations.size());

		operations = service.getUserOperations(user, StockOperationStatus.NEW, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());
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

		List<StockOperation> operations = service.getUserOperations(user, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(0, operations.size());
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

		List<StockOperation> operations = service.getUserOperations(user, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(0, operations.size());
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

		List<StockOperation> results = service.getUserOperations(user, StockOperationStatus.REQUESTED, null);

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
		Assert.assertEquals(getTestEntityCount(), results.size());
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
		Assert.assertEquals(getTestEntityCount(), results.size());
	}

	/**
	 * @verifies return items for the specified operation
	 * @see IStockOperationDataService#getItemsByOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation,
	 * org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemsByOperation_shouldReturnItemsForTheSpecifiedOperation() throws Exception {
		StockOperation operation = service.getById(0);

		List<StockOperationItem> items = service.getItemsByOperation(operation, null);

		Assert.assertNotNull(items);
		Assert.assertEquals(3, items.size());

		StockOperationItem item = Iterators.get(items.iterator(), 0);
		Assert.assertEquals(0, (int)item.getId());

		item = Iterators.get(items.iterator(), 1);
		Assert.assertEquals(1, (int)item.getId());

		item = Iterators.get(items.iterator(), 2);
		Assert.assertEquals(2, (int)item.getId());
	}

	/**
	 * @verifies return empty list when no items
	 * @see IStockOperationDataService#getItemsByOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation,
	 * org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemsByOperation_shouldReturnEmptyListWhenNoItems() throws Exception {
		StockOperation newOperation = createEntity(true);
		if (newOperation.getItems() != null) {
			newOperation.getItems().clear();
		}

		service.save(newOperation);
		Context.flushSession();

		List<StockOperationItem> items = service.getItemsByOperation(newOperation, null);

		Assert.assertNotNull(items);
		Assert.assertEquals(0, items.size());
	}

	/**
	 * @verifies return paged items when paging is specified
	 * @see IStockOperationDataService#getItemsByOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation,
	 * org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemsByOperation_shouldReturnPagedItemsWhenPagingIsSpecified() throws Exception {
		PagingInfo pagingInfo = new PagingInfo(1, 1);

		StockOperation operation = service.getById(0);

		List<StockOperationItem> items = service.getItemsByOperation(operation, pagingInfo);

		Assert.assertNotNull(items);
		Assert.assertEquals(3, (long)pagingInfo.getTotalRecordCount());
		Assert.assertEquals(1, items.size());

		StockOperationItem item = Iterators.getOnlyElement(items.iterator());
		Assert.assertEquals(0, (int)item.getId());

		pagingInfo.setPage(2);
		items = service.getItemsByOperation(operation, pagingInfo);

		item = Iterators.getOnlyElement(items.iterator());
		Assert.assertEquals(1, (int)item.getId());

		pagingInfo.setPage(3);
		items = service.getItemsByOperation(operation, pagingInfo);

		item = Iterators.getOnlyElement(items.iterator());
		Assert.assertEquals(2, (int)item.getId());
	}

	/**
	 * @verifies return all items when paging is null
	 * @see IStockOperationDataService#getItemsByOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation,
	 * org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemsByOperation_shouldReturnAllItemsWhenPagingIsNull() throws Exception {
		StockOperation operation = service.getById(0);

		List<StockOperationItem> items = service.getItemsByOperation(operation, null);

		Assert.assertNotNull(items);
		Assert.assertEquals(3, items.size());

		StockOperationItem item = Iterators.get(items.iterator(), 0);
		Assert.assertEquals(0, (int)item.getId());

		item = Iterators.get(items.iterator(), 1);
		Assert.assertEquals(1, (int)item.getId());

		item = Iterators.get(items.iterator(), 2);
		Assert.assertEquals(2, (int)item.getId());
	}

	/**
	 * @verifies throw IllegalArgumentException when operation is null
	 * @see IStockOperationDataService#getItemsByOperation(org.openmrs.module.openhmis.inventory.api.model.StockOperation,
	 * org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItemsByOperation_shouldThrowIllegalArgumentExceptionWhenOperationIsNull() throws Exception {
		service.getItemsByOperation(null, new PagingInfo(1, 1));
	}
}

