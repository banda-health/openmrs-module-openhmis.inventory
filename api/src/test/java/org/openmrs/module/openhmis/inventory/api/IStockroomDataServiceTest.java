package org.openmrs.module.openhmis.inventory.api;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.commons.api.entity.search.BaseObjectTemplateSearch;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemPrice;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetailBase;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTransaction;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch;

import com.google.common.collect.Iterators;

public class IStockroomDataServiceTest extends IMetadataDataServiceTest<IStockroomDataService, Stockroom> {
	public static final String DATASET = TestConstants.BASE_DATASET_DIR + "StockroomTest.xml";

	protected IItemDataService itemService;
	protected IDepartmentDataService departmentService;
	protected IStockOperationDataService operationService;
	protected IItemStockDataService itemStockService;

	public static void assertStockroom(Stockroom expected, Stockroom actual) {
		assertOpenmrsMetadata(expected, actual);

		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getDescription(), actual.getDescription());
		if (expected.getLocation() == null) {
			Assert.assertNull(actual.getLocation());
		} else {
			Assert.assertEquals(expected.getLocation().getId(), actual.getLocation().getId());
		}

		assertCollection(expected.getOperations(), actual.getOperations(), new Action2<StockOperation, StockOperation>() {
			@Override
			public void apply(StockOperation expectedOp, StockOperation actualOp) {
				IStockOperationDataServiceTest.assertStockOperation(expectedOp, actualOp);
			}
		});

		assertItemStock(expected.getItems(), actual.getItems());
	}

	public static void assertItemStock(Collection<ItemStock> expected, Collection<ItemStock> actual) {
		assertCollection(expected, actual, new Action2<ItemStock, ItemStock>() {
			@Override
			public void apply(ItemStock expectedStockItem, ItemStock actualStockItem) {
				assertItemStock(expectedStockItem, actualStockItem);
			}
		});
	}

	public static void assertItemStock(ItemStock expected, ItemStock actual) {
		assertOpenmrsObject(expected, actual);

		Assert.assertEquals(expected.getStockroom().getId(), actual.getStockroom().getId());
		Assert.assertEquals(expected.getItem().getId(), actual.getItem().getId());
		Assert.assertEquals(expected.getQuantity(), actual.getQuantity());

		assertItemStockDetails(expected.getDetails(), actual.getDetails());
	}

	public static void assertItemStockDetails(Collection<ItemStockDetail> expected, Collection<ItemStockDetail> actual) {
		assertCollection(expected, actual, new Action2<ItemStockDetail, ItemStockDetail>() {
			@Override
			public void apply(ItemStockDetail expectedDetail, ItemStockDetail actualDetail) {
				assertItemStockDetail(expectedDetail, actualDetail);
			}
		});
	}

	public static void assertItemStockDetailBase(ItemStockDetailBase expected, ItemStockDetailBase actual) {
		assertOpenmrsObject(expected, actual);

		Assert.assertEquals(expected.getItem(), actual.getItem());
		Assert.assertEquals(expected.getQuantity(), actual.getQuantity());
		Assert.assertEquals(expected.getExpiration(), actual.getExpiration());
		Assert.assertEquals(expected.getBatchOperation(), actual.getBatchOperation());
		Assert.assertEquals(expected.isCalculatedExpiration(), actual.isCalculatedExpiration());
		Assert.assertEquals(expected.isCalculatedBatch(), actual.isCalculatedBatch());
	}

	public static void assertItemStockDetail(ItemStockDetail expected, ItemStockDetail actual) {
		assertItemStockDetailBase(expected, actual);

		Assert.assertEquals(expected.getItemStock(), actual.getItemStock());
		Assert.assertEquals(expected.getStockroom(), actual.getStockroom());
	}

	@Override
	public void before() throws Exception {
		super.before();

		itemService = Context.getService(IItemDataService.class);
		departmentService = Context.getService(IDepartmentDataService.class);
		operationService = Context.getService(IStockOperationDataService.class);
		itemStockService = Context.getService(IItemStockDataService.class);

		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IDepartmentDataServiceTest.DEPARTMENT_DATASET);
		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(DATASET);
	}

	@Override
	public Stockroom createEntity(boolean valid) {
		Stockroom room = new Stockroom();

		if (valid) {
			room.setName("New Stockroom");
		}
		room.setDescription("This is a stockroom. It is new.");
		room.setLocation(Context.getLocationService().getLocation(1));
		room.setCreator(Context.getAuthenticatedUser());
		room.setDateCreated(new Date());

		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(room);
		operation.setOperationNumber("op number");
		operation.setCreator(Context.getAuthenticatedUser());
		operation.setDateCreated(new Date());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setOperationDate(new Date());

		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(itemService.getById(0));
		tx.setQuantity(5);

		operation.addTransaction(tx);
		room.addOperation(operation);

		ItemStock roomItem = new ItemStock();
		roomItem.setStockroom(room);
		roomItem.setItem(tx.getItem());
		roomItem.setQuantity(5);

		ItemStockDetail detail = new ItemStockDetail();
		detail.setItemStock(roomItem);
		detail.setItem(tx.getItem());
		detail.setStockroom(room);
		detail.setBatchOperation(operation);
		detail.setQuantity(5);

		roomItem.addDetail(detail);
		room.addItem(roomItem);

		return room;
	}

	@Override
	protected int getTestEntityCount() {
		return 5;
	}

	@Override
	protected void updateEntityFields(Stockroom room) {
		room.setName(room.getName() + "updated");
		room.setDescription(room.getDescription() + "updated");
		room.setLocation(Context.getLocationService().getLocation(0));
		room.setChangedBy(Context.getAuthenticatedUser());
		room.setDateChanged(new Date());

		// Add a distribution transaction
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getDistribution());
		operation.setSource(room);
		operation.setOperationNumber("op number 2");
		operation.setCreator(Context.getAuthenticatedUser());
		operation.setDateCreated(new Date());
		operation.setStatus(StockOperationStatus.COMPLETED);
		operation.setOperationDate(new Date());

		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(itemService.getById(0));
		tx.setQuantity(3);

		operation.addTransaction(tx);
		room.addOperation(operation);

		Set<ItemStock> items = room.getItems();
		if (items.size() > 0) {
			// Update an existing item quantity
			Iterator<ItemStock> iterator = items.iterator();
			ItemStock item = iterator.next();
			item.setQuantity(item.getQuantity() + 1);

			// Update detail
			Set<ItemStockDetail> details = item.getDetails();
			if (details.size() > 0) {
				ItemStockDetail detail = Iterators.get(details.iterator(), 0);
				detail.setQuantity(detail.getQuantity() + 1);

				if (details.size() > 1) {
					details.remove(Iterators.get(details.iterator(), 1));
				}
			}

			if (items.size() > 1) {
				// Delete an existing stockroom item
				item = iterator.next();

				items.remove(item);
			}
		}
	}

	@Override
	protected void assertEntity(Stockroom expected, Stockroom actual) {
		assertStockroom(expected, actual);
	}

	/**
	 * @verifies return items filtered by template and stockroom
	 * @see IStockroomDataService#getItems(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItems_shouldReturnItemsFilteredByTemplateAndStockRoom() throws Exception {
		Stockroom stockRoom = service.getById(1);
		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setDepartment(departmentService.getById(0));

		List<ItemStock> results = service.getItems(stockRoom, search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		ItemStock item = results.get(0);
		item.getItem().setDepartment(departmentService.getById(1));

		itemService.save(item.getItem());
		Context.flushSession();

		search.getTemplate().setDepartment(item.getItem().getDepartment());

		results = service.getItems(stockRoom, search, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertItemStock(item, results.get(0));
	}

	/**
	 * @verifies not return items for other stockrooms
	 * @see IStockroomDataService#getItems(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItems_shouldNotReturnItemsForOtherStockRooms() throws Exception {
		Stockroom room0 = service.getById(0);
		Stockroom room1 = service.getById(1);

		ItemSearch search = new ItemSearch();
		search.setNameComparisonType(BaseObjectTemplateSearch.StringComparisonType.EQUAL);
		search.getTemplate().setName(itemService.getById(0).getName());

		List<ItemStock> results0 = service.getItems(room0, search, null);
		List<ItemStock> results1 = service.getItems(room1, search, null);
		Assert.assertNotNull(results0);
		Assert.assertNotNull(results1);
		Assert.assertEquals(1, results0.size());
		Assert.assertEquals(1, results1.size());

		ItemStock result0 = results0.get(0);
		ItemStock result1 = results1.get(0);

		Assert.assertNotEquals(result0, result1);
		Assert.assertNotEquals(result0.getId(), result1.getId());
		Assert.assertNotEquals(result0.getQuantity(), result1.getQuantity());
		Assert.assertEquals(result0.getItem(), result1.getItem());
	}

	/**
	 * @verifies return all found items if paging is null
	 * @see IStockroomDataService#getItems(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItems_shouldReturnAllFoundItemsIfPagingIsNull() throws Exception {
		Stockroom stockRoom = service.getById(1);
		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setDepartment(departmentService.getById(0));

		List<ItemStock> results = service.getItems(stockRoom, search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());
	}

	/**
	 * @verifies return paged items if paging is specified
	 * @see IStockroomDataService#getItems(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItems_shouldReturnPagedItemsIfPagingIsSpecified() throws Exception {
		Stockroom stockRoom = service.getById(1);
		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setDepartment(departmentService.getById(0));

		PagingInfo paging = new PagingInfo(1, 1);
		List<ItemStock> results = service.getItems(stockRoom, search, paging);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(3, (long)paging.getTotalRecordCount());
	}

	/**
	 * @verifies return retired items from search unless specified
	 * @see IStockroomDataService#getItems(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItems_shouldReturnRetiredItemsFromSearchUnlessSpecified() throws Exception {
		Item item = itemService.getById(0);
		itemService.retire(item, "Just cuz");
		Context.flushSession();

		Stockroom stockRoom = service.getById(1);
		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setDepartment(departmentService.getById(0));

		List<ItemStock> results = service.getItems(stockRoom, search, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		search.setIncludeRetired(null);
		search.getTemplate().setRetired(false);
		results = service.getItems(stockRoom, search, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies throw IllegalArgumentException if stockroom is null
	 * @see IStockroomDataService#getItems(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItems_shouldThrowIllegalArgumentExceptionIfStockRoomIsNull() throws Exception {
		service.getItems(null, new ItemSearch(new Item()), null);
	}

	/**
	 * @verifies throw IllegalArgumentException if item search is null
	 * @see IStockroomDataService#getItems(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItems_shouldThrowIllegalArgumentExceptionIfItemSearchIsNull() throws Exception {
		Stockroom stockRoom = service.getById(1);
		service.getItems(stockRoom, null, null);
	}

	/**
	 * @verifies return the stockroom item
	 * @see IStockroomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.model.Item)
	 */
	@Test
	public void getItem_shouldReturnTheStockRoomItem() throws Exception {
		Stockroom room = service.getById(1);

		ItemStock result = service.getItem(room, itemService.getById(0));
		Assert.assertNotNull(result);

		ItemStock item = null;
		for (ItemStock roomItem : room.getItems()) {
			if (roomItem.getItem().getId().equals(result.getItem().getId())) {
				item = roomItem;
				break;
			}
		}
		Assert.assertNotNull(item);

		assertItemStock(item, result);
	}

	/**
	 * @verifies not return items from other stockrooms
	 * @see IStockroomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.model.Item)
	 */
	@Test
	public void getItem_shouldNotReturnItemsFromOtherStockRooms() throws Exception {
		Stockroom room0 = service.getById(0);
		Stockroom room1 = service.getById(1);

		Item item = itemService.getById(2);

		ItemStock result0 = service.getItem(room0, item);
		ItemStock result1 = service.getItem(room1, item);
		Assert.assertNotNull(result0);
		Assert.assertNotNull(result1);

		Assert.assertNotEquals(result0, result1);
		Assert.assertNotEquals(result0.getId(), result1.getId());
		Assert.assertNotEquals(result0.getQuantity(), result1.getQuantity());
		Assert.assertEquals(result0.getItem(), result1.getItem());
	}

	/**
	 * @verifies return null when item is not found
	 * @see IStockroomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.model.Item)
	 */
	@Test
	public void getItem_shouldReturnNullWhenItemIsNotFound() throws Exception {
		Stockroom room = service.getById(2);

		ItemStock result = service.getItem(room, itemService.getById(0));
		Assert.assertNull(result);
	}

	/**
	 * @verifies throw IllegalArgumentException when stockroom is null
	 * @see IStockroomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.model.Item)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItem_shouldThrowIllegalArgumentExceptionWhenStockRoomIsNull() throws Exception {
		service.getItem(null, itemService.getById(0));
	}

	/**
	 * @verifies throw IllegalArgumentException when item is null
	 * @see IStockroomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.model.Item)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItem_shouldThrowIllegalArgumentExceptionWhenItemIsNull() throws Exception {
		Stockroom room = service.getById(0);
		service.getItem(room, null);
	}

	/**
	 * @verifies return all the items in the stockroom ordered by item name
	 * @see IStockroomDataService#getItemsByRoom(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemsByRoom_shouldReturnAllTheItemsInTheStockRoomOrderedByItemName() throws Exception {
		Stockroom stockRoom = service.getById(1);
		List<ItemStock> results = service.getItemsByRoom(stockRoom, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		assertItemStock(itemStockService.getById(3), results.get(0));
		assertItemStock(itemStockService.getById(5), results.get(1));
		assertItemStock(itemStockService.getById(4), results.get(2));
	}

	/**
	 * @verifies return an empty list if there are no items in the stockroom
	 * @see IStockroomDataService#getItemsByRoom(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemsByRoom_shouldReturnAnEmptyListIfThereAreNoItemsInTheStockRoom() throws Exception {
		// Create a new stockroom with no items
		Stockroom stockRoom = createEntity(true);
		stockRoom.getItems().clear();
		service.save(stockRoom);
		Context.flushSession();

		List<ItemStock> results = service.getItemsByRoom(stockRoom, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return paged items if paging is specified
	 * @see IStockroomDataService#getItemsByRoom(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemsByRoom_shouldReturnPagedItemsIfPagingIsSpecified() throws Exception {
		Stockroom stockRoom = service.getById(1);
		PagingInfo paging = new PagingInfo(1, 1);
		List<ItemStock> results = service.getItemsByRoom(stockRoom, paging);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertNotNull(paging.getTotalRecordCount());
		Assert.assertEquals(3, (long)paging.getTotalRecordCount());
		assertItemStock(itemStockService.getById(3), results.get(0));

		paging.setPage(2);
		results = service.getItemsByRoom(stockRoom, paging);
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(3, (long)paging.getTotalRecordCount());
		assertItemStock(itemStockService.getById(5), results.get(0));
	}

	/**
	 * @verifies throw IllegalArgumentException if the stockroom is null
	 * @see IStockroomDataService#getItemsByRoom(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItemsByRoom_shouldThrowIllegalArgumentExceptionIfTheStockRoomIsNull() throws Exception {
		service.getItemsByRoom(null, null);
	}

	/**
	 * @verifies return the stockroom item detail
	 * @see IStockroomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date,
	 *      org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void getStockroomItemDetail_shouldReturnTheStockRoomItemDetail() throws Exception {
		Item item = itemService.getById(0);
		Stockroom stockroom = service.getById(0);
		StockOperation batchOperation = operationService.getById(0);

		ItemStockDetail detail = service.getStockroomItemDetail(stockroom, item, null, batchOperation);

		Assert.assertNotNull(detail);
		Assert.assertEquals(stockroom, detail.getStockroom());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(batchOperation, detail.getBatchOperation());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(4, (int)detail.getQuantity());
	}

	/**
	 * @verifies not return details for other stockrooms
	 * @see IStockroomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date,
	 *      org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void getStockroomItemDetail_shouldNotReturnDetailsForOtherStockRooms() throws Exception {
		Item item = itemService.getById(0);
		Stockroom stockroom0 = service.getById(0);
		Stockroom stockroom1 = service.getById(1);
		StockOperation batchOperation = operationService.getById(0);

		ItemStockDetail detail0 = service.getStockroomItemDetail(stockroom0, item, null, batchOperation);
		ItemStockDetail detail1 = service.getStockroomItemDetail(stockroom1, item, null, batchOperation);

		Assert.assertNotNull(detail0);
		Assert.assertEquals(stockroom0, detail0.getStockroom());
		Assert.assertEquals(item, detail0.getItem());
		Assert.assertEquals(batchOperation, detail0.getBatchOperation());
		Assert.assertNull(detail0.getExpiration());
		Assert.assertEquals(4, (int)detail0.getQuantity());

		Assert.assertNotNull(detail1);
		Assert.assertEquals(stockroom1, detail1.getStockroom());
		Assert.assertEquals(item, detail1.getItem());
		Assert.assertEquals(batchOperation, detail1.getBatchOperation());
		Assert.assertNull(detail1.getExpiration());
		Assert.assertEquals(5, (int)detail1.getQuantity());

		Assert.assertNotEquals(detail0.getId(), detail1.getId());
	}

	/**
	 * @verifies return null when the details is not found
	 * @see IStockroomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date,
	 *      org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void getStockroomItemDetail_shouldReturnNullWhenTheDetailsIsNotFound() throws Exception {
		Item item = itemService.getById(0);
		Stockroom stockroom = service.getById(0);

		// This is not a valid batch operation
		StockOperation batchOperation = operationService.getById(1);

		ItemStockDetail detail = service.getStockroomItemDetail(stockroom, item, null, batchOperation);

		Assert.assertNull(detail);
	}

	/**
	 * @verifies return detail with expiration and batch when specified
	 * @see IStockroomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date,
	 *      org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void getStockroomItemDetail_shouldReturnDetailWithExpirationAndBatchWhenSpecified() throws Exception {
		// This is an item with an expiration
		Item item = itemService.getById(2);
		Stockroom stockroom = service.getById(0);
		StockOperation batchOperation = operationService.getById(0);

		ItemStock stock = service.getItem(stockroom, item);
		ItemStockDetail stockDetail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertNotNull(stockDetail.getExpiration());

		// First check that using without the expiration returns null
		ItemStockDetail detail = service.getStockroomItemDetail(stockroom, item, null, batchOperation);
		Assert.assertNull(detail);

		// Now check that using the proper expiration returns the expected detail
		detail = service.getStockroomItemDetail(stockroom, item, stockDetail.getExpiration(), batchOperation);

		Assert.assertNotNull(detail);
		Assert.assertEquals(stockroom, detail.getStockroom());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(batchOperation, detail.getBatchOperation());
		Assert.assertEquals(stockDetail.getExpiration(), detail.getExpiration());
		Assert.assertEquals(stockDetail.getQuantity(), detail.getQuantity());
	}

	/**
	 * @verifies return detail without an expiration when not specified
	 * @see IStockroomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date,
	 *      org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void getStockroomItemDetail_shouldReturnDetailWithoutAnExpirationWhenNotSpecified() throws Exception {
		// This is an item with an expiration
		Item item = itemService.getById(2);
		Stockroom stockroom = service.getById(0);
		StockOperation batchOperation = operationService.getById(0);

		ItemStock stock = service.getItem(stockroom, item);
		ItemStockDetail stockDetail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertNotNull(stockDetail.getExpiration());

		// Add a new detail with a different expiration
		ItemStockDetail newDetail = new ItemStockDetail();
		newDetail.setBatchOperation(batchOperation);
		newDetail.setStockroom(stockroom);
		newDetail.setQuantity(10);
		newDetail.setItem(item);
		newDetail.setItemStock(stock);
		newDetail.setExpiration(null);

		stock.addDetail(newDetail);

		// Save the new detail
		itemStockService.save(stock);
		Context.flushSession();

		// Check that using the expiration date returns the expected detail
		ItemStockDetail detail =
		        service.getStockroomItemDetail(stockroom, item, stockDetail.getExpiration(), batchOperation);

		Assert.assertNotNull(detail);
		Assert.assertEquals(stockroom, detail.getStockroom());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(batchOperation, detail.getBatchOperation());
		Assert.assertEquals(stockDetail.getExpiration(), detail.getExpiration());
		Assert.assertEquals(stockDetail.getQuantity(), detail.getQuantity());

		// Check using a null expiration
		detail = service.getStockroomItemDetail(stockroom, item, null, batchOperation);
		Assert.assertNotNull(detail);
		Assert.assertEquals(stockroom, detail.getStockroom());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(batchOperation, detail.getBatchOperation());
		Assert.assertEquals(newDetail.getExpiration(), detail.getExpiration());
		Assert.assertEquals(newDetail.getQuantity(), detail.getQuantity());
	}

	/**
	 * @verifies return detail without a batch when not specified
	 * @see IStockroomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date,
	 *      org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void getStockroomItemDetail_shouldReturnDetailWithoutABatchWhenNotSpecified() throws Exception {
		Item item = itemService.getById(0);
		Stockroom stockroom = service.getById(0);

		ItemStock stock = service.getItem(stockroom, item);
		ItemStockDetail stockDetail = Iterators.get(stock.getDetails().iterator(), 0);

		// Add a new detail with no batch
		ItemStockDetail newDetail = new ItemStockDetail();
		newDetail.setBatchOperation(null);
		newDetail.setStockroom(stockroom);
		newDetail.setQuantity(10);
		newDetail.setItem(item);
		newDetail.setItemStock(stock);
		newDetail.setExpiration(null);

		stock.addDetail(newDetail);

		// Save the new detail
		itemStockService.save(stock);
		Context.flushSession();

		// Check that using the batch returns the expected detail
		ItemStockDetail detail = service.getStockroomItemDetail(stockroom, item, null, stockDetail.getBatchOperation());

		Assert.assertNotNull(detail);
		Assert.assertEquals(stockroom, detail.getStockroom());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(stockDetail.getBatchOperation(), detail.getBatchOperation());
		Assert.assertEquals(stockDetail.getExpiration(), detail.getExpiration());
		Assert.assertEquals(stockDetail.getQuantity(), detail.getQuantity());

		// Check using a null batch
		detail = service.getStockroomItemDetail(stockroom, item, null, null);
		Assert.assertNotNull(detail);
		Assert.assertEquals(stockroom, detail.getStockroom());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertNull(detail.getBatchOperation());
		Assert.assertEquals(newDetail.getExpiration(), detail.getExpiration());
		Assert.assertEquals(newDetail.getQuantity(), detail.getQuantity());
	}

	/**
	 * @verifies throw IllegalArgumentException when stockroom is null
	 * @see IStockroomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date,
	 *      org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getStockroomItemDetail_shouldThrowIllegalArgumentExceptionWhenStockRoomIsNull() throws Exception {
		Item item = itemService.getById(0);
		StockOperation batchOperation = operationService.getById(0);

		service.getStockroomItemDetail(null, item, new Date(), batchOperation);
	}

	/**
	 * @verifies throw IllegalArgumentException when item is null
	 * @see IStockroomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date,
	 *      org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getStockroomItemDetail_shouldThrowIllegalArgumentExceptionWhenItemIsNull() throws Exception {
		Stockroom stockroom = service.getById(0);
		StockOperation batchOperation = operationService.getById(0);

		service.getStockroomItemDetail(stockroom, null, new Date(), batchOperation);
	}

	/**
	 * @verifies return item stock sorted by item name
	 * @see IStockroomDataService#getItemsByRoom(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemsByRoom_shouldReturnItemStockSortedByItemName() throws Exception {
		// Create some new items to work with
		Item newItemX = new Item();
		newItemX.setName("X new item");
		newItemX.setDepartment(departmentService.getById(0));
		ItemPrice price = new ItemPrice(BigDecimal.ZERO, "default");
		newItemX.addPrice(price);
		newItemX.setDefaultPrice(price);

		Item newItemA = new Item();
		newItemA.setName("A new item");
		newItemA.setDepartment(departmentService.getById(0));
		price = new ItemPrice(BigDecimal.ZERO, "default");
		newItemA.addPrice(price);
		newItemA.setDefaultPrice(price);

		Item newItemZ = new Item();
		newItemZ.setName("Z new item");
		newItemZ.setDepartment(departmentService.getById(0));
		price = new ItemPrice(BigDecimal.ZERO, "default");
		newItemZ.addPrice(price);
		newItemZ.setDefaultPrice(price);

		itemService.save(newItemX);
		Context.flushSession();
		itemService.save(newItemA);
		Context.flushSession();
		itemService.save(newItemZ);
		Context.flushSession();

		// Add some item stock for those new items
		Stockroom room = service.getById(2);

		ItemStock stockX = new ItemStock();
		stockX.setStockroom(room);
		stockX.setItem(newItemX);
		stockX.setQuantity(10);

		ItemStock stockA = new ItemStock();
		stockA.setStockroom(room);
		stockA.setItem(newItemA);
		stockA.setQuantity(20);

		ItemStock stockZ = new ItemStock();
		stockZ.setStockroom(room);
		stockZ.setItem(newItemZ);
		stockZ.setQuantity(30);

		room.addItem(stockX);
		service.save(room);
		Context.flushSession();
		room.addItem(stockA);
		service.save(room);
		Context.flushSession();
		room.addItem(stockZ);
		service.save(room);
		Context.flushSession();

		List<ItemStock> items = service.getItemsByRoom(room, null);

		Assert.assertNotNull(items);
		Assert.assertEquals(3, items.size());

		Assert.assertEquals(stockA.getId(), items.get(0).getId());
		Assert.assertEquals(stockX.getId(), items.get(1).getId());
		Assert.assertEquals(stockZ.getId(), items.get(2).getId());
	}

	/**
	 * @verifies return item stock sorted by item name
	 * @see IStockroomDataService#getItems(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItems_shouldReturnItemStockSortedByItemName() throws Exception {
		// Create some new items to work with
		Item newItemX = new Item();
		newItemX.setName("X new item");
		newItemX.setDepartment(departmentService.getById(0));
		ItemPrice price = new ItemPrice(BigDecimal.ZERO, "default");
		newItemX.addPrice(price);
		newItemX.setDefaultPrice(price);

		Item newItemA = new Item();
		newItemA.setName("A new item");
		newItemA.setDepartment(departmentService.getById(0));
		price = new ItemPrice(BigDecimal.ZERO, "default");
		newItemA.addPrice(price);
		newItemA.setDefaultPrice(price);

		Item newItemZ = new Item();
		newItemZ.setName("Z new item");
		newItemZ.setDepartment(departmentService.getById(0));
		price = new ItemPrice(BigDecimal.ZERO, "default");
		newItemZ.addPrice(price);
		newItemZ.setDefaultPrice(price);

		itemService.save(newItemX);
		Context.flushSession();
		itemService.save(newItemA);
		Context.flushSession();
		itemService.save(newItemZ);
		Context.flushSession();

		// Add some item stock for those new items
		Stockroom room = service.getById(2);

		ItemStock stockX = new ItemStock();
		stockX.setStockroom(room);
		stockX.setItem(newItemX);
		stockX.setQuantity(10);

		ItemStock stockA = new ItemStock();
		stockA.setStockroom(room);
		stockA.setItem(newItemA);
		stockA.setQuantity(20);

		ItemStock stockZ = new ItemStock();
		stockZ.setStockroom(room);
		stockZ.setItem(newItemZ);
		stockZ.setQuantity(30);

		room.addItem(stockX);
		service.save(room);
		Context.flushSession();
		room.addItem(stockA);
		service.save(room);
		Context.flushSession();
		room.addItem(stockZ);
		service.save(room);
		Context.flushSession();

		ItemSearch search = new ItemSearch();
		search.setNameComparisonType(BaseObjectTemplateSearch.StringComparisonType.LIKE);
		search.getTemplate().setName("%new%");
		List<ItemStock> items = service.getItems(room, search, null);

		Assert.assertNotNull(items);
		Assert.assertEquals(3, items.size());

		Assert.assertEquals(stockA.getId(), items.get(0).getId());
		Assert.assertEquals(stockX.getId(), items.get(1).getId());
		Assert.assertEquals(stockZ.getId(), items.get(2).getId());
	}

	/**
	 * @verifies return all the transactions in the stockroom ordered by the transaction date
	 * @see IStockroomDataService#getTransactionsByRoom(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getTransactionsByRoom_shouldReturnAllTheTransactionsInTheStockroomOrderedByTheTransactionDate()
	        throws Exception {
		Stockroom stockroom = service.getById(0);

		List<StockOperationTransaction> results = service.getTransactionsByRoom(stockroom, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(6, results.size());

		// THe order should be from the most recent transaction
		StockOperationTransaction tx = Iterators.get(results.iterator(), 0);
		Assert.assertEquals(4, (int)tx.getId());

		tx = Iterators.get(results.iterator(), 1);
		Assert.assertEquals(5, (int)tx.getId());

		tx = Iterators.get(results.iterator(), 2);
		Assert.assertEquals(3, (int)tx.getId());

		tx = Iterators.get(results.iterator(), 3);
		Assert.assertEquals(2, (int)tx.getId());

		tx = Iterators.get(results.iterator(), 4);
		Assert.assertEquals(1, (int)tx.getId());

		tx = Iterators.get(results.iterator(), 5);
		Assert.assertEquals(0, (int)tx.getId());
	}

	/**
	 * @verifies return an empty list if there are no transactions
	 * @see IStockroomDataService#getTransactionsByRoom(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getTransactionsByRoom_shouldReturnAnEmptyListIfThereAreNoTransactions() throws Exception {
		Stockroom newRoom = createEntity(true);

		service.save(newRoom);
		Context.flushSession();

		List<StockOperationTransaction> results = service.getTransactionsByRoom(newRoom, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return paged items if paging is specified
	 * @see IStockroomDataService#getTransactionsByRoom(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getTransactionsByRoom_shouldReturnPagedItemsIfPagingIsSpecified() throws Exception {
		Stockroom stockroom = service.getById(0);

		PagingInfo pagingInfo = new PagingInfo(1, 1);
		List<StockOperationTransaction> results = service.getTransactionsByRoom(stockroom, pagingInfo);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(6, (long)pagingInfo.getTotalRecordCount());
	}

	/**
	 * @verifies throw IllegalArgumentException if the stockroom is null
	 * @see IStockroomDataService#getTransactionsByRoom(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getTransactionsByRoom_shouldThrowIllegalArgumentExceptionIfTheStockroomIsNull() throws Exception {
		service.getTransactionsByRoom(null, null);
	}

	/**
	 * @verifies return operations filtered by template and stockroom
	 * @see IStockroomDataService#getOperations(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnOperationsFilteredByTemplateAndStockroom() throws Exception {
		Stockroom stockroom = service.getById(1);
		StockOperationSearch search = new StockOperationSearch();

		List<StockOperation> results = service.getOperations(stockroom, search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());

		StockOperation op = Iterators.get(results.iterator(), 0);
		Assert.assertEquals(2, (int)op.getId());

		op = Iterators.get(results.iterator(), 1);
		Assert.assertEquals(1, (int)op.getId());

		search.getTemplate().setStatus(StockOperationStatus.COMPLETED);
		results = service.getOperations(stockroom, search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());

		op = Iterators.get(results.iterator(), 0);
		Assert.assertEquals(1, (int)op.getId());

		search.getTemplate().setStatus(StockOperationStatus.PENDING);
		results = service.getOperations(stockroom, search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());

		op = Iterators.get(results.iterator(), 0);
		Assert.assertEquals(2, (int)op.getId());
	}

	/**
	 * @verifies return paged operations if paging is specified
	 * @see IStockroomDataService#getOperations(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnPagedOperationsIfPagingIsSpecified() throws Exception {
		Stockroom stockroom = service.getById(1);
		PagingInfo pagingInfo = new PagingInfo(1, 1);

		List<StockOperation> results = service.getOperations(stockroom, null, pagingInfo);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(2, (long)pagingInfo.getTotalRecordCount());

		StockOperation op = Iterators.get(results.iterator(), 0);
		Assert.assertEquals(2, (int)op.getId());
	}

	/**
	 * @verifies return operations sorted by last modified date
	 * @see IStockroomDataService#getOperations(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getOperations_shouldReturnOperationsSortedByLastModifiedDate() throws Exception {
		StockOperation op = operationService.getById(1);
		op.setDateChanged(new Date());

		operationService.save(op);
		Context.flushSession();

		List<StockOperation> results = service.getOperations(service.getById(1), null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());

		op = Iterators.get(results.iterator(), 0);
		Assert.assertEquals(1, (int)op.getId());

		op = Iterators.get(results.iterator(), 1);
		Assert.assertEquals(2, (int)op.getId());
	}

	/**
	 * @verifies throw IllegalArgumentException if stockroom is null
	 * @see IStockroomDataService#getOperations(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getOperations_shouldThrowIllegalArgumentExceptionIfStockroomIsNull() throws Exception {
		service.getOperations(null, new StockOperationSearch(), null);
	}

	/**
	 * @verifies not throw IllegalArgumentException if operation search is null
	 * @see IStockroomDataService#getOperations(org.openmrs.module.openhmis.inventory.api.model.Stockroom,
	 *      org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getOperations_shouldNotThrowIllegalArgumentExceptionIfOperationSearchIsNull() throws Exception {
		List<StockOperation> results = service.getOperations(service.getById(1), null, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}
}
