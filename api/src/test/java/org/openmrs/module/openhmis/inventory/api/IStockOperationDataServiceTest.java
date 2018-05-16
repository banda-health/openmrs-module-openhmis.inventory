package org.openmrs.module.openhmis.inventory.api;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.Utility;
import org.openmrs.module.openhmis.commons.api.compatibility.UserServiceCompatibility;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.commons.api.entity.search.BaseObjectTemplateSearch;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.inventory.api.model.IStockOperationType;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTransaction;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch;

import com.google.common.collect.Iterators;

import liquibase.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class IStockOperationDataServiceTest extends IMetadataDataServiceTest<IStockOperationDataService, StockOperation> {

	@Autowired
	private UserServiceCompatibility userServiceCompatibility;

	IStockOperationTypeDataService typeService;
	IStockroomDataService stockroomService;
	IItemDataService itemService;

	IItemDataServiceTest itemTest;

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
		});

		assertCollection(expected.getReserved(), actual.getReserved(),
		    new Action2<ReservedTransaction, ReservedTransaction>() {
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

		assertCollection(expected.getTransactions(), actual.getTransactions(),
		    new Action2<StockOperationTransaction, StockOperationTransaction>() {
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

		op.setTransactions(new HashSet<StockOperationTransaction>());

		return op;
	}

	@Override
	protected int getTestEntityCount() {
		return 4;
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
	public void getOperationByNumber_shouldThrowIllegalArgumentExceptionIsNumberIsLongerThan255Characters()
	        throws Exception {
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
		Assert.assertEquals(3, room.getOperations().size());

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
		Assert.assertEquals(3, room.getOperations().size());

		StockOperation[] roomTrans = new StockOperation[3];
		room.getOperations().toArray(roomTrans);
		Assert.assertEquals(StockOperationStatus.PENDING, roomTrans[0].getStatus());
		Assert.assertEquals(StockOperationStatus.COMPLETED, roomTrans[1].getStatus());
		Assert.assertEquals(StockOperationStatus.ROLLBACK, roomTrans[2].getStatus());

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
	 * @verifies return operations filtered by number
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnOperationsFilteredByNumber() throws Exception {
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
	 * @verifies return operations filtered by status
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnOperationsFilteredByStatus() throws Exception {
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
	 * @verifies return operations filtered by type
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnOperationsFilteredByType() throws Exception {
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
	 * @verifies return operations filtered by source stockroom
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnOperationsFilteredBySourceStockroom() throws Exception {
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
	 * @verifies return operations filtered by destination stockroom
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnOperationsFilteredByDestinationStockroom() throws Exception {
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
	 * @verifies return all operations if paging is null
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnAllOperationsIfPagingIsNull() throws Exception {
		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setStatus(StockOperationStatus.COMPLETED);

		List<StockOperation> results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies return paged operations if paging is specified
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnPagedOperationsIfPagingIsSpecified() throws Exception {
		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setStatus(StockOperationStatus.COMPLETED);

		PagingInfo pagingInfo = new PagingInfo(1, 1);
		List<StockOperation> results = service.getOperations(search, pagingInfo);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(2, (long)pagingInfo.getTotalRecordCount());
	}

	/**
	 * @verifies return operations filtered by creation date
	 * @see IStockOperationDataService#getOperations(StockOperationSearch, PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnOperationsFilteredByCreationDate() throws Exception {
		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setDateCreated(service.getById(0).getDateCreated());
		search.setDateCreatedComparisonType(BaseObjectTemplateSearch.DateComparisonType.GREATER_THAN_EQUAL);

		List<StockOperation> results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies return operations filtered by patient
	 * @see IStockOperationDataService#getOperations(org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnOperationsFilteredByPatient() throws Exception {
		StockOperationSearch search = new StockOperationSearch();
		Patient patient = Context.getPatientService().getPatient(1);
		search.getTemplate().setPatient(patient);

		List<StockOperation> results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
		assertEntity(service.getById(2), results.get(0));
		assertEntity(service.getById(3), results.get(1));
	}

	/**
	 * @verifies return operations filtered by stockroom
	 * @see IStockOperationDataService#getOperations(org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnOperationsFilteredByStockroom() throws Exception {
		StockOperation operation = service.getById(1);
		Stockroom room = operation.getSource();

		Context.flushSession();

		StockOperationSearch search = new StockOperationSearch();
		search.getTemplate().setStockroom(room);

		Context.flushSession();

		List<StockOperation> results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
		Assert.assertEquals(0, (int)results.get(0).getId());
		Assert.assertEquals(1, (int)results.get(1).getId());

		room = operation.getDestination();
		search = new StockOperationSearch();
		search.getTemplate().setStockroom(room);

		Context.flushSession();

		results = service.getOperations(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
		Assert.assertEquals(2, (int)results.get(0).getId());
		Assert.assertEquals(1, (int)results.get(1).getId());
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
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnAllOperationsWithTheSpecifiedStatusForSpecifiedUser() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null, null, null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(getTestEntityCount(), results.size());

		results = service.getUserOperations(user, StockOperationStatus.COMPLETED, null, null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());

		Assert.assertEquals(0, (int)results.get(0).getId());
		Assert.assertEquals(1, (int)results.get(1).getId());

		results = service.getUserOperations(user, StockOperationStatus.PENDING, null, null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());

	}

	/**
	 * @verifies return all operations with the specified operation type for specified user
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnAllOperationsWithTheSpecifiedOperationTypeForSpecifiedUser() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null, null, null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(getTestEntityCount(), results.size());

		results = service.getUserOperations(user, null, WellKnownOperationTypes.getInitial(), null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());

		results = service.getUserOperations(user, null, WellKnownOperationTypes.getDistribution(), null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
		Assert.assertEquals(2, (int)results.get(0).getId());
		Assert.assertEquals(3, (int)results.get(1).getId());

	}

	/**
	 * @verifies return all operations with the specified item for specified user
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnAllOperationsWithTheSpecifiedItemForSpecifiedUser() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null, null, null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(getTestEntityCount(), results.size());

		Item item = itemService.getById(0);
		Assert.assertNotNull(item);

		results = service.getUserOperations(user, null, null, item, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());
		Assert.assertEquals(2, (int)results.get(0).getId());
		Assert.assertEquals(0, (int)results.get(1).getId());
		Assert.assertEquals(1, (int)results.get(2).getId());

		item = itemService.getById(6);
		Assert.assertNotNull(item);

		results = service.getUserOperations(user, null, null, item, null, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(3, (int)results.get(0).getId());

		item = itemService.getById(5);
		Assert.assertNotNull(item);

		results = service.getUserOperations(user, null, null, item, null, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return all operations with the specified status and operation type for specified user
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnAllOperationsWithTheSpecifiedStatusAndOperationTypeForSpecifiedUser()
	        throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null, null, null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(getTestEntityCount(), results.size());

		results = service.getUserOperations(user, StockOperationStatus.COMPLETED, null, null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());

		Assert.assertEquals(0, (int)results.get(0).getId());
		Assert.assertEquals(1, (int)results.get(1).getId());

		results =
		        service.getUserOperations(user, StockOperationStatus.COMPLETED, WellKnownOperationTypes.getTransfer(),
		            null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(1, (int)results.get(0).getId());

	}

	/**
	 * @verifies return all operations with the specified status and item for specified user
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnAllOperationsWithTheSpecifiedStatusAndItemForSpecifiedUser() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null, null, null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(getTestEntityCount(), results.size());

		Item item = itemService.getById(1);
		Assert.assertNotNull(item);

		results = service.getUserOperations(user, StockOperationStatus.PENDING, null, item, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(2, (int)results.get(0).getId());

		results = service.getUserOperations(user, StockOperationStatus.COMPLETED, null, item, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
		Assert.assertEquals(0, (int)results.get(0).getId());
		Assert.assertEquals(1, (int)results.get(1).getId());

		item = itemService.getById(6);
		Assert.assertNotNull(item);

		results = service.getUserOperations(user, StockOperationStatus.ROLLBACK, null, item, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(3, (int)results.get(0).getId());

	}

	/**
	 * @verifies return all operations with the specified status and item for specified user
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnAllOperationsWithTheSpecifiedStatusAndOperationTypeAndItemForSpecifiedUser()
	        throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null, null, null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(getTestEntityCount(), results.size());

		Item item = itemService.getById(1);
		Assert.assertNotNull(item);

		results =
		        service.getUserOperations(user, StockOperationStatus.COMPLETED, WellKnownOperationTypes.getReceipt(), item,
		            null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(0, (int)results.get(0).getId());

		results =
		        service.getUserOperations(user, StockOperationStatus.COMPLETED, WellKnownOperationTypes.getInitial(), item,
		            null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());

	}

	/**
	 * @verifies return specified operations created by user
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnSpecifiedOperationsCreatedByUser() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null, null, null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(getTestEntityCount(), results.size());

		results = service.getUserOperations(user, StockOperationStatus.COMPLETED, null, null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());

		results = service.getUserOperations(user, StockOperationStatus.PENDING, null, null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
	}

	/**
	 * @verifies return specified operations with user as attribute type user
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
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

		List<StockOperation> operations = service.getUserOperations(user, null, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());

		operations = service.getUserOperations(user, StockOperationStatus.COMPLETED, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(0, operations.size());

		operations = service.getUserOperations(user, StockOperationStatus.NEW, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());
	}

	/**
	 * @verifies return specified operations with user role as attribute type role
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
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

		List<StockOperation> operations = service.getUserOperations(user, null, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());

		operations = service.getUserOperations(user, StockOperationStatus.COMPLETED, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(0, operations.size());

		operations = service.getUserOperations(user, StockOperationStatus.NEW, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());
	}

	/**
	 * @verifies return specified operations with user role as child role of attribute type role
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnSpecifiedOperationsWithUserRoleAsChildRoleOfAttributeTypeRole()
	        throws Exception {
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

		List<StockOperation> operations = service.getUserOperations(user, null, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());

		operations = service.getUserOperations(user, StockOperationStatus.COMPLETED, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(0, operations.size());

		operations = service.getUserOperations(user, StockOperationStatus.NEW, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());
	}

	/**
	 * @verifies return specified operations with user role as grandchild role of attribute type role
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnSpecifiedOperationsWithUserRoleAsGrandchildRoleOfAttributeTypeRole()
	        throws Exception {
		User baseUser = Context.getUserService().getUser(0);
		User user = Context.getUserService().getUser(5506);

		Set<Role> roles = new HashSet<Role>();
		roles.add(Context.getUserService().getRole("Grandchild"));
		user.setRoles(roles);
		userServiceCompatibility.saveUser(user, "1wWhatever");
		Context.flushSession();

		StockOperation operation = createEntity(true);
		operation.setCreator(baseUser);
		operation.setInstanceType(WellKnownOperationTypes.getAdjustment());
		operation.getInstanceType().setRole(Context.getUserService().getRole("Parent"));
		operation.setSource(stockroomService.getById(0));

		typeService.save(operation.getInstanceType());
		service.save(operation);
		Context.flushSession();

		List<StockOperation> operations = service.getUserOperations(user, null, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());

		operations = service.getUserOperations(user, StockOperationStatus.COMPLETED, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(0, operations.size());

		operations = service.getUserOperations(user, StockOperationStatus.NEW, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals(operation.getId(), operations.get(0).getId());
	}

	/**
	 * @verifies not return operations when user role not descendant of attribute type role
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldNotReturnOperationsWhenUserRoleNotDescendantOfAttributeTypeRole() throws Exception {
		User baseUser = Context.getUserService().getUser(0);
		User user = Context.getUserService().getUser(5506);

		Set<Role> roles = new HashSet<Role>();
		roles.add(Context.getUserService().getRole("Other"));
		user.setRoles(roles);
		userServiceCompatibility.saveUser(user, "1wWhatever");
		Context.flushSession();

		StockOperation operation = createEntity(true);
		operation.setCreator(baseUser);
		operation.getInstanceType().setRole(Context.getUserService().getRole("Parent"));

		service.save(operation);
		Context.flushSession();

		List<StockOperation> operations = service.getUserOperations(user, null, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(0, operations.size());
	}

	/**
	 * @verifies not return operations when user role is parent of attribute type role
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldNotReturnOperationsWhenUserRoleIsParentOfAttributeTypeRole() throws Exception {
		User baseUser = Context.getUserService().getUser(0);
		User user = Context.getUserService().getUser(5506);

		Set<Role> roles = new HashSet<Role>();
		roles.add(Context.getUserService().getRole("Parent"));
		user.setRoles(roles);
		userServiceCompatibility.saveUser(user, "1wWhatever");
		Context.flushSession();

		StockOperation operation = createEntity(true);
		operation.setCreator(baseUser);
		operation.getInstanceType().setRole(Context.getUserService().getRole("Child"));

		service.save(operation);
		Context.flushSession();

		List<StockOperation> operations = service.getUserOperations(user, null, null, null, null, null);

		Assert.assertNotNull(operations);
		Assert.assertEquals(0, operations.size());
	}

	/**
	 * @verifies return empty list when no operations
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnEmptyListWhenNoOperations() throws Exception {
		User user = Context.getUserService().getUser(1);

		StockOperation operation = service.getById(2);
		operation.setStatus(StockOperationStatus.COMPLETED);

		service.save(operation);
		Context.flushSession();

		List<StockOperation> results =
		        service.getUserOperations(user, StockOperationStatus.REQUESTED, null, null, null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return paged operations when paging is specified
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnPagedOperationsWhenPagingIsSpecified() throws Exception {
		User user = Context.getUserService().getUser(1);
		StockOperation operation = service.getById(1);
		operation.setStatus(StockOperationStatus.PENDING);

		service.save(operation);
		Context.flushSession();

		PagingInfo paging = new PagingInfo(1, 1);
		List<StockOperation> results =
		        service.getUserOperations(user, StockOperationStatus.PENDING, null, null, null, paging);
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
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnAllOperationsWhenPagingIsNull() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null, null, null, null, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(getTestEntityCount(), results.size());
	}

	/**
	 * @verifies throw NullPointerException when user is null
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getUserOperations_shouldThrowIllegalArgumentExceptionWhenUserIsNull() throws Exception {
		service.getUserOperations(null, null, null, null, null, null);
	}

	/**
	 * @verifies return all operations for user when status is null
	 * @see IStockOperationDataService#getUserOperations(User, StockOperationStatus, IStockOperationType, Item, Stockroom,
	 *      PagingInfo)
	 */
	@Test
	public void getUserOperations_shouldReturnAllOperationsForUserWhenStatusIsNull() throws Exception {
		User user = Context.getUserService().getUser(1);

		List<StockOperation> results = service.getUserOperations(user, null, null, null, null, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(getTestEntityCount(), results.size());
	}

	/**
	 * @verifies return items for the specified operation
	 * @see IStockOperationDataService#getItemsByOperation(StockOperation, PagingInfo)
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
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
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
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
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
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
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
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItemsByOperation_shouldThrowIllegalArgumentExceptionWhenOperationIsNull() throws Exception {
		service.getItemsByOperation(null, new PagingInfo(1, 1));
	}

	/**
	 * @verifies throw IllegalArgumentException if operationDate is null
	 * @see IStockOperationDataService#getOperationsSince(java.util.Date, PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getOperationsSince_shouldThrowIllegalArgumentExceptionIfOperationDateIsNull() throws Exception {
		service.getOperationsSince(null, new PagingInfo(1, 1));
	}

	/**
	 * @verifies return an empty list if no operations are found
	 * @see IStockOperationDataService#getOperationsSince(java.util.Date, PagingInfo)
	 */
	@Test
	public void getOperationsSince_shouldReturnAnEmptyListIfNoOperationsAreFound() throws Exception {
		List<StockOperation> results = service.getOperationsSince(new Date(), null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return operations with operation date past specified date
	 * @see IStockOperationDataService#getOperationsSince(java.util.Date, PagingInfo)
	 */
	@Test
	public void getOperationsSince_shouldReturnOperationsWithOperationDatePastSpecifiedDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);

		cal.add(Calendar.YEAR, 2);
		StockOperation operation = createEntity(true);
		operation.setOperationDate(cal.getTime());

		cal.add(Calendar.YEAR, 13);
		StockOperation operation2 = createEntity(true);
		operation2.setOperationDate(cal.getTime());

		cal.add(Calendar.YEAR, -3);
		StockOperation operation3 = createEntity(true);
		operation3.setOperationDate(cal.getTime());

		service.save(operation);
		service.save(operation2);
		service.save(operation3);
		Context.flushSession();

		List<StockOperation> results = service.getOperationsSince(new Date(), null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		// Results should be in order of operation date
		Assert.assertEquals(operation, results.get(0));
		Assert.assertEquals(operation3, results.get(1));
		Assert.assertEquals(operation2, results.get(2));
	}

	/**
	 * @verifies return all results if paging is null
	 * @see IStockOperationDataService#getOperationsSince(java.util.Date, PagingInfo)
	 */
	@Test
	public void getOperationsSince_shouldReturnAllResultsIfPagingIsNull() throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);

		cal.add(Calendar.YEAR, 2);
		StockOperation operation = createEntity(true);
		operation.setOperationDate(cal.getTime());

		cal.add(Calendar.YEAR, 13);
		StockOperation operation2 = createEntity(true);
		operation2.setOperationDate(cal.getTime());

		cal.add(Calendar.YEAR, -3);
		StockOperation operation3 = createEntity(true);
		operation3.setOperationDate(cal.getTime());

		service.save(operation);
		service.save(operation2);
		service.save(operation3);
		Context.flushSession();

		List<StockOperation> results = service.getOperationsSince(new Date(), null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		// Results should be in order of operation date
		Assert.assertEquals(operation, results.get(0));
		Assert.assertEquals(operation3, results.get(1));
		Assert.assertEquals(operation2, results.get(2));
	}

	/**
	 * @verifies return paged operations if paging is specified
	 * @see IStockOperationDataService#getOperationsSince(java.util.Date, PagingInfo)
	 */
	@Test
	public void getOperationsSince_shouldReturnPagedOperationsIfPagingIsSpecified() throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);

		cal.add(Calendar.YEAR, 2);
		StockOperation operation = createEntity(true);
		operation.setOperationDate(cal.getTime());

		cal.add(Calendar.YEAR, 13);
		StockOperation operation2 = createEntity(true);
		operation2.setOperationDate(cal.getTime());

		cal.add(Calendar.YEAR, -3);
		StockOperation operation3 = createEntity(true);
		operation3.setOperationDate(cal.getTime());

		service.save(operation);
		service.save(operation2);
		service.save(operation3);
		Context.flushSession();

		PagingInfo paging = new PagingInfo(1, 1);
		List<StockOperation> results = service.getOperationsSince(new Date(), paging);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(3, (long)paging.getTotalRecordCount());

		// Results should be in order of operation date
		Assert.assertEquals(operation, results.get(0));

		paging.setPage(2);
		results = service.getOperationsSince(new Date(), paging);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(operation3, results.get(0));

		paging.setPage(3);
		results = service.getOperationsSince(new Date(), paging);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(operation2, results.get(0));
	}

	/**
	 * @verifies return the operation with the largest operation order on the specified date
	 * @see IStockOperationDataService#getLastOperationByDate(java.util.Date)
	 */
	@Test
	public void getLastOperationByDate_shouldReturnTheOperationWithTheLargestOperationOrderOnTheSpecifiedDate()
	        throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);
		cal.add(Calendar.YEAR, 10);

		StockOperation op1 = createEntity(true);
		op1.setOperationDate(cal.getTime());
		op1.setOperationOrder(0);

		cal.add(Calendar.MINUTE, 10);
		StockOperation op2 = createEntity(true);
		op2.setOperationDate(cal.getTime());
		op2.setOperationOrder(1);

		cal.add(Calendar.HOUR_OF_DAY, 2);
		StockOperation op3 = createEntity(true);
		op3.setOperationDate(cal.getTime());
		op3.setOperationOrder(2);

		cal.add(Calendar.HOUR_OF_DAY, 1);
		StockOperation op4 = createEntity(true);
		op4.setOperationDate(cal.getTime());
		op4.setOperationOrder(3);

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op5 = createEntity(true);
		op5.setOperationDate(cal.getTime());
		op5.setOperationOrder(0);

		service.save(op1);
		service.save(op2);
		service.save(op3);
		service.save(op4);
		service.save(op5);

		Context.flushSession();

		StockOperation result = service.getLastOperationByDate(op1.getOperationDate());

		Assert.assertNotNull(result);
		Assert.assertEquals(op4, result);
	}

	/**
	 * @verifies return the operation with the last creation date if the operation order is the same
	 * @see IStockOperationDataService#getLastOperationByDate(java.util.Date)
	 */
	@Test
	public void getLastOperationByDate_shouldReturnTheOperationWithTheLastCreationDateIfTheOperationOrderIsTheSame()
	        throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);

		Calendar calCreate = Calendar.getInstance();

		cal.add(Calendar.YEAR, 10);
		StockOperation op1 = createEntity(true);
		op1.setOperationDate(cal.getTime());
		op1.setOperationOrder(0);
		op1.setDateCreated(calCreate.getTime());

		StockOperation op2 = createEntity(true);
		op2.setOperationDate(cal.getTime());
		op2.setOperationOrder(0);

		calCreate.add(Calendar.MINUTE, 10);
		op2.setDateCreated(calCreate.getTime());

		service.save(op1);
		service.save(op2);

		Context.flushSession();

		StockOperation result = service.getLastOperationByDate(cal.getTime());

		Assert.assertNotNull(result);
		Assert.assertEquals(op2, result);
	}

	/**
	 * @verifies return null if no operations occurred on the specified date
	 * @see IStockOperationDataService#getLastOperationByDate(java.util.Date)
	 */
	@Test
	public void getLastOperationByDate_shouldReturnNullIfNoOperationsOccurredOnTheSpecifiedDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 10);

		StockOperation result = service.getLastOperationByDate(cal.getTime());
		Assert.assertNull(result);
	}

	/**
	 * @verifies throw IllegalArgumentException if the date is null
	 * @see IStockOperationDataService#getLastOperationByDate(java.util.Date)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getLastOperationByDate_shouldThrowIllegalArgumentExceptionIfTheDateIsNull() throws Exception {
		service.getLastOperationByDate(null);
	}

	/**
	 * @verifies return the operation with the least operation order on the specified date
	 * @see IStockOperationDataService#getFirstOperationByDate(java.util.Date)
	 */
	@Test
	public void getFirstOperationByDate_shouldReturnTheOperationWithTheLeastOperationOrderOnTheSpecifiedDate()
	        throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);

		cal.add(Calendar.YEAR, 10);
		StockOperation op1 = createEntity(true);
		op1.setOperationDate(cal.getTime());
		op1.setOperationOrder(0);

		cal.add(Calendar.MINUTE, 10);
		StockOperation op2 = createEntity(true);
		op2.setOperationDate(cal.getTime());
		op2.setOperationOrder(1);

		cal.add(Calendar.HOUR_OF_DAY, 2);
		StockOperation op3 = createEntity(true);
		op3.setOperationDate(cal.getTime());
		op3.setOperationOrder(2);

		cal.add(Calendar.HOUR_OF_DAY, 1);
		StockOperation op4 = createEntity(true);
		op4.setOperationDate(cal.getTime());
		op4.setOperationOrder(3);

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op5 = createEntity(true);
		op5.setOperationDate(cal.getTime());
		op5.setOperationOrder(0);

		cal.add(Calendar.DAY_OF_MONTH, -2);
		StockOperation op6 = createEntity(true);
		op6.setOperationDate(cal.getTime());
		op6.setOperationOrder(0);

		service.save(op1);
		service.save(op2);
		service.save(op3);
		service.save(op4);
		service.save(op5);
		service.save(op6);

		Context.flushSession();

		StockOperation result = service.getFirstOperationByDate(op4.getOperationDate());

		Assert.assertNotNull(result);
		Assert.assertEquals(op1, result);
	}

	/**
	 * @verifies return the operation with the first creation date if the operation order is the same
	 * @see IStockOperationDataService#getFirstOperationByDate(java.util.Date)
	 */
	@Test
	public void getFirstOperationByDate_shouldReturnTheOperationWithTheFirstCreationDateIfTheOperationOrderIsTheSame()
	        throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);

		Calendar calCreate = Calendar.getInstance();

		cal.add(Calendar.YEAR, 10);
		StockOperation op1 = createEntity(true);
		op1.setOperationDate(cal.getTime());
		op1.setOperationOrder(0);
		op1.setDateCreated(calCreate.getTime());

		StockOperation op2 = createEntity(true);
		op2.setOperationDate(cal.getTime());
		op2.setOperationOrder(0);

		calCreate.add(Calendar.MINUTE, 10);
		op2.setDateCreated(calCreate.getTime());

		service.save(op1);
		service.save(op2);

		Context.flushSession();

		StockOperation result = service.getFirstOperationByDate(cal.getTime());

		Assert.assertNotNull(result);
		Assert.assertEquals(op1, result);
	}

	/**
	 * @verifies return null if no operations occurred on the specified date
	 * @see IStockOperationDataService#getFirstOperationByDate(java.util.Date)
	 */
	@Test
	public void getFirstOperationByDate_shouldReturnNullIfNoOperationsOccurredOnTheSpecifiedDate() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 10);

		StockOperation result = service.getFirstOperationByDate(cal.getTime());
		Assert.assertNull(result);
	}

	/**
	 * @verifies throw IllegalArgumentException if the date is null
	 * @see IStockOperationDataService#getFirstOperationByDate(java.util.Date)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getFirstOperationByDate_shouldThrowIllegalArgumentExceptionIfTheDateIsNull() throws Exception {
		service.getFirstOperationByDate(null);
	}

	/**
	 * @verifies throw IllegalArgumentException if the operation is null
	 * @see IStockOperationDataService#getFutureOperations(StockOperation, PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getFutureOperations_shouldThrowIllegalArgumentExceptionIfTheOperationIsNull() throws Exception {
		service.getFutureOperations(null, new PagingInfo(1, 1));
	}

	/**
	 * @verifies return an empty list if no operations are found
	 * @see IStockOperationDataService#getFutureOperations(StockOperation, PagingInfo)
	 */
	@Test
	public void getFutureOperations_shouldReturnAnEmptyListIfNoOperationsAreFound() throws Exception {
		List<StockOperation> results = service.getOperationsSince(new Date(), null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return operations with operation date past specified operation
	 * @see IStockOperationDataService#getFutureOperations(StockOperation, PagingInfo)
	 */
	@Test
	public void getFutureOperations_shouldReturnOperationsWithOperationDatePastSpecifiedOperation() throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);
		cal.add(Calendar.YEAR, 10);

		StockOperation op1 = createEntity(true);
		op1.setOperationDate(cal.getTime());
		op1.setOperationOrder(0);

		cal.add(Calendar.MINUTE, 10);
		StockOperation op2 = createEntity(true);
		op2.setOperationDate(cal.getTime());
		op2.setOperationOrder(1);

		cal.add(Calendar.HOUR_OF_DAY, 2);
		StockOperation op3 = createEntity(true);
		op3.setOperationDate(cal.getTime());
		op3.setOperationOrder(2);

		cal.add(Calendar.HOUR_OF_DAY, 1);
		StockOperation op4 = createEntity(true);
		op4.setOperationDate(cal.getTime());
		op4.setOperationOrder(3);

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op5 = createEntity(true);
		op5.setOperationDate(cal.getTime());
		op5.setOperationOrder(0);

		service.save(op1);
		service.save(op2);
		service.save(op3);
		service.save(op4);
		service.save(op5);

		Context.flushSession();

		List<StockOperation> results = service.getFutureOperations(op1, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(4, results.size());

		Assert.assertEquals(op2, results.get(0));
		Assert.assertEquals(op3, results.get(1));
		Assert.assertEquals(op4, results.get(2));
		Assert.assertEquals(op5, results.get(3));
	}

	/**
	 * @verifies return operations with higher operation order when day is the same
	 * @see IStockOperationDataService#getFutureOperations(StockOperation, PagingInfo)
	 */
	@Test
	public void getFutureOperations_shouldReturnOperationsWithHigherOperationOrderWhenDayIsTheSame() throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);

		cal.add(Calendar.YEAR, 2);
		StockOperation op1 = createEntity(true);
		op1.setOperationDate(cal.getTime());
		op1.setOperationOrder(0);

		cal.add(Calendar.HOUR_OF_DAY, 5);
		StockOperation op2 = createEntity(true);
		op2.setOperationDate(cal.getTime());
		op2.setOperationOrder(1);

		cal.add(Calendar.HOUR_OF_DAY, 5);
		cal.add(Calendar.MINUTE, 5);
		StockOperation op3 = createEntity(true);
		op3.setOperationDate(cal.getTime());
		op3.setOperationOrder(2);

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op4 = createEntity(true);
		op4.setOperationDate(cal.getTime());
		op4.setOperationOrder(0);

		StockOperation op5 = createEntity(true);
		op5.setOperationDate(op1.getOperationDate());
		op5.setOperationOrder(-1);

		service.save(op1);
		service.save(op2);
		service.save(op3);
		service.save(op4);
		service.save(op5);
		Context.flushSession();

		List<StockOperation> results = service.getFutureOperations(op1, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		// Results should be in order of operation date, then operation order
		Assert.assertEquals(op2, results.get(0));
		Assert.assertEquals(op3, results.get(1));
		Assert.assertEquals(op4, results.get(2));
	}

	/**
	 * @verifies return all results if paging is null
	 * @see IStockOperationDataService#getFutureOperations(StockOperation, PagingInfo)
	 */
	@Test
	public void getFutureOperations_shouldReturnAllResultsIfPagingIsNull() throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);
		cal.add(Calendar.YEAR, 10);

		StockOperation op1 = createEntity(true);
		op1.setOperationDate(cal.getTime());
		op1.setOperationOrder(0);

		cal.add(Calendar.MINUTE, 10);
		StockOperation op2 = createEntity(true);
		op2.setOperationDate(cal.getTime());
		op2.setOperationOrder(1);

		cal.add(Calendar.HOUR_OF_DAY, 2);
		StockOperation op3 = createEntity(true);
		op3.setOperationDate(cal.getTime());
		op3.setOperationOrder(2);

		cal.add(Calendar.HOUR_OF_DAY, 1);
		StockOperation op4 = createEntity(true);
		op4.setOperationDate(cal.getTime());
		op4.setOperationOrder(3);

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op5 = createEntity(true);
		op5.setOperationDate(cal.getTime());
		op5.setOperationOrder(0);

		service.save(op1);
		service.save(op2);
		service.save(op3);
		service.save(op4);
		service.save(op5);

		Context.flushSession();

		List<StockOperation> results = service.getFutureOperations(op1, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(4, results.size());
	}

	/**
	 * @verifies return paged results if paging is specified
	 * @see IStockOperationDataService#getFutureOperations(StockOperation, PagingInfo)
	 */
	@Test
	public void getFutureOperations_shouldReturnPagedResultsIfPagingIsSpecified() throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);
		cal.add(Calendar.YEAR, 10);

		StockOperation op1 = createEntity(true);
		op1.setOperationDate(cal.getTime());
		op1.setOperationOrder(0);

		cal.add(Calendar.MINUTE, 10);
		StockOperation op2 = createEntity(true);
		op2.setOperationDate(cal.getTime());
		op2.setOperationOrder(1);

		cal.add(Calendar.HOUR_OF_DAY, 2);
		StockOperation op3 = createEntity(true);
		op3.setOperationDate(cal.getTime());
		op3.setOperationOrder(2);

		cal.add(Calendar.HOUR_OF_DAY, 1);
		StockOperation op4 = createEntity(true);
		op4.setOperationDate(cal.getTime());
		op4.setOperationOrder(3);

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op5 = createEntity(true);
		op5.setOperationDate(cal.getTime());
		op5.setOperationOrder(0);

		service.save(op1);
		service.save(op2);
		service.save(op3);
		service.save(op4);
		service.save(op5);

		Context.flushSession();

		PagingInfo paging = new PagingInfo(1, 1);
		List<StockOperation> results = service.getFutureOperations(op1, paging);

		Assert.assertNotNull(results);
		Assert.assertEquals(4, (long)paging.getTotalRecordCount());
		Assert.assertEquals(1, results.size());

		Assert.assertEquals(op2, results.get(0));

		paging.setPage(2);
		results = service.getFutureOperations(op1, paging);
		Assert.assertEquals(op3, results.get(0));

		paging.setPage(3);
		results = service.getFutureOperations(op1, paging);
		Assert.assertEquals(op4, results.get(0));

		paging.setPage(4);
		results = service.getFutureOperations(op1, paging);
		Assert.assertEquals(op5, results.get(0));
	}

	/**
	 * @verifies return operations by operation order then by date
	 * @see IStockOperationDataService#getFutureOperations(StockOperation, PagingInfo)
	 */
	@Test
	public void getFutureOperations_shouldReturnOperationsByDateThenByOperationOrder() throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);

		cal.add(Calendar.YEAR, 2);
		StockOperation op1 = createEntity(true);
		op1.setOperationDate(cal.getTime());
		op1.setOperationOrder(0);

		cal.add(Calendar.HOUR_OF_DAY, 5);
		StockOperation op2 = createEntity(true);
		op2.setOperationDate(cal.getTime());
		op2.setOperationOrder(1);

		cal.add(Calendar.MINUTE, 5);
		StockOperation op3 = createEntity(true);
		op3.setOperationDate(cal.getTime());
		op3.setOperationOrder(2);

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op4 = createEntity(true);
		op4.setOperationDate(cal.getTime());
		op4.setOperationOrder(0);

		StockOperation op5 = createEntity(true);
		op5.setOperationDate(op1.getOperationDate());
		op5.setOperationOrder(-1);

		StockOperation op6 = createEntity(true);
		op6.setOperationDate(cal.getTime());
		op6.setOperationOrder(-1);

		cal.add(Calendar.HOUR_OF_DAY, 2);
		StockOperation op7 = createEntity(true);
		op7.setOperationDate(cal.getTime());
		op7.setOperationOrder(1);

		service.save(op1);
		service.save(op2);
		service.save(op3);
		service.save(op4);
		service.save(op5);
		service.save(op6);
		service.save(op7);
		Context.flushSession();

		List<StockOperation> results = service.getFutureOperations(op1, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(5, results.size());

		// Results should be in order of operation date
		Assert.assertEquals(op2, results.get(0));
		Assert.assertEquals(op3, results.get(1));
		Assert.assertEquals(op6, results.get(2));
		Assert.assertEquals(op4, results.get(3));
		Assert.assertEquals(op7, results.get(4));
	}

	/**
	 * @verifies throw IllegalArgumentException if the operation is null
	 * @see IStockOperationDataService#getOperationsByDate(java.util.Date, PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getOperationsByDate_shouldThrowIllegalArgumentExceptionIfTheOperationIsNull() throws Exception {
		service.getOperationsByDate(null, new PagingInfo(1, 1));
	}

	/**
	 * @verifies return an empty list if no operations are found
	 * @see IStockOperationDataService#getOperationsByDate(java.util.Date, PagingInfo)
	 */
	@Test
	public void getOperationsByDate_shouldReturnAnEmptyListIfNoOperationsAreFound() throws Exception {
		List<StockOperation> results = service.getOperationsByDate(new Date(), null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return operations that occurred on the specified date regardless of time
	 * @see IStockOperationDataService#getOperationsByDate(java.util.Date, PagingInfo)
	 */
	@Test
	public void getOperationsByDate_shouldReturnOperationsThatOccurredOnTheSpecifiedDateRegardlessOfTime() throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op1 = createEntity(true);
		op1.setOperationDate(cal.getTime());
		op1.setOperationOrder(0);

		cal.add(Calendar.HOUR_OF_DAY, 6);
		StockOperation op2 = createEntity(true);
		op2.setOperationDate(cal.getTime());
		op2.setOperationOrder(1);

		cal.add(Calendar.MINUTE, 20);
		StockOperation op3 = createEntity(true);
		op3.setOperationDate(cal.getTime());
		op3.setOperationOrder(2);

		cal.add(Calendar.HOUR_OF_DAY, 2);
		cal.add(Calendar.MINUTE, 15);
		StockOperation op4 = createEntity(true);
		op4.setOperationDate(cal.getTime());
		op4.setOperationOrder(3);

		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date test = cal.getTime();

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op5 = createEntity(true);
		op5.setOperationDate(cal.getTime());
		op5.setOperationOrder(0);

		service.save(op1);
		service.save(op2);
		service.save(op3);
		service.save(op4);
		service.save(op5);

		Context.flushSession();

		List<StockOperation> results = service.getOperationsByDate(test, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(4, results.size());

		Assert.assertEquals(op1, results.get(0));
		Assert.assertEquals(op2, results.get(1));
		Assert.assertEquals(op3, results.get(2));
		Assert.assertEquals(op4, results.get(3));
	}

	/**
	 * @verifies return operations ordered by operation order
	 * @see IStockOperationDataService#getOperationsByDate(java.util.Date, PagingInfo)
	 */
	@Test
	public void getOperationsByDate_shouldReturnOperationsOrderedByOperationOrder() throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op1 = createEntity(true);
		op1.setOperationDate(cal.getTime());
		op1.setOperationOrder(3);

		cal.add(Calendar.HOUR_OF_DAY, 6);
		StockOperation op2 = createEntity(true);
		op2.setOperationDate(cal.getTime());
		op2.setOperationOrder(2);

		cal.add(Calendar.MINUTE, 20);
		StockOperation op3 = createEntity(true);
		op3.setOperationDate(cal.getTime());
		op3.setOperationOrder(1);

		cal.add(Calendar.HOUR_OF_DAY, 2);
		cal.add(Calendar.MINUTE, 15);
		StockOperation op4 = createEntity(true);
		op4.setOperationDate(cal.getTime());
		op4.setOperationOrder(0);

		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date test = cal.getTime();

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op5 = createEntity(true);
		op5.setOperationDate(cal.getTime());
		op5.setOperationOrder(0);

		service.save(op1);
		service.save(op2);
		service.save(op3);
		service.save(op4);
		service.save(op5);

		Context.flushSession();

		List<StockOperation> results = service.getOperationsByDate(test, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(4, results.size());

		Assert.assertEquals(op4, results.get(0));
		Assert.assertEquals(op3, results.get(1));
		Assert.assertEquals(op2, results.get(2));
		Assert.assertEquals(op1, results.get(3));
	}

	/**
	 * @verifies return all results if paging is null
	 * @see IStockOperationDataService#getOperationsByDate(java.util.Date, PagingInfo)
	 */
	@Test
	public void getOperationsByDate_shouldReturnAllResultsIfPagingIsNull() throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op1 = createEntity(true);
		op1.setOperationDate(cal.getTime());
		op1.setOperationOrder(0);

		cal.add(Calendar.HOUR_OF_DAY, 6);
		StockOperation op2 = createEntity(true);
		op2.setOperationDate(cal.getTime());
		op2.setOperationOrder(1);

		cal.add(Calendar.MINUTE, 20);
		StockOperation op3 = createEntity(true);
		op3.setOperationDate(cal.getTime());
		op3.setOperationOrder(2);

		cal.add(Calendar.HOUR_OF_DAY, 2);
		cal.add(Calendar.MINUTE, 15);
		StockOperation op4 = createEntity(true);
		op4.setOperationDate(cal.getTime());
		op4.setOperationOrder(3);

		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date test = cal.getTime();

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op5 = createEntity(true);
		op5.setOperationDate(cal.getTime());
		op5.setOperationOrder(0);

		service.save(op1);
		service.save(op2);
		service.save(op3);
		service.save(op4);
		service.save(op5);

		Context.flushSession();

		List<StockOperation> results = service.getOperationsByDate(test, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(4, results.size());

		Assert.assertEquals(op1, results.get(0));
		Assert.assertEquals(op2, results.get(1));
		Assert.assertEquals(op3, results.get(2));
		Assert.assertEquals(op4, results.get(3));
	}

	/**
	 * @verifies return paged results if paging is specified
	 * @see IStockOperationDataService#getOperationsByDate(java.util.Date, PagingInfo)
	 */
	@Test
	public void getOperationsByDate_shouldReturnPagedResultsIfPagingIsSpecified() throws Exception {
		Calendar cal = Calendar.getInstance();
		Utility.clearCalendarTime(cal);

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op1 = createEntity(true);
		op1.setOperationDate(cal.getTime());
		op1.setOperationOrder(0);

		cal.add(Calendar.HOUR_OF_DAY, 6);
		StockOperation op2 = createEntity(true);
		op2.setOperationDate(cal.getTime());
		op2.setOperationOrder(1);

		cal.add(Calendar.MINUTE, 20);
		StockOperation op3 = createEntity(true);
		op3.setOperationDate(cal.getTime());
		op3.setOperationOrder(2);

		cal.add(Calendar.HOUR_OF_DAY, 2);
		cal.add(Calendar.MINUTE, 15);
		StockOperation op4 = createEntity(true);
		op4.setOperationDate(cal.getTime());
		op4.setOperationOrder(3);

		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date test = cal.getTime();

		cal.add(Calendar.DAY_OF_MONTH, 1);
		StockOperation op5 = createEntity(true);
		op5.setOperationDate(cal.getTime());
		op5.setOperationOrder(0);

		service.save(op1);
		service.save(op2);
		service.save(op3);
		service.save(op4);
		service.save(op5);

		Context.flushSession();

		PagingInfo pagingInfo = new PagingInfo(1, 1);
		List<StockOperation> results = service.getOperationsByDate(test, pagingInfo);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(4, (long)pagingInfo.getTotalRecordCount());

		Assert.assertEquals(op1, results.get(0));

		pagingInfo.setPage(2);
		results = service.getOperationsByDate(test, pagingInfo);
		Assert.assertEquals(op2, results.get(0));

		pagingInfo.setPage(3);
		results = service.getOperationsByDate(test, pagingInfo);
		Assert.assertEquals(op3, results.get(0));

		pagingInfo.setPage(4);
		results = service.getOperationsByDate(test, pagingInfo);
		Assert.assertEquals(op4, results.get(0));
	}
}
