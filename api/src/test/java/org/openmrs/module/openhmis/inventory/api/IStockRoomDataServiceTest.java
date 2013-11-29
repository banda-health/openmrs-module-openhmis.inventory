package org.openmrs.module.openhmis.inventory.api;

import com.google.common.collect.Iterators;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.commons.api.entity.search.BaseObjectTemplateSearch;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;

import java.math.BigDecimal;
import java.util.*;

public class IStockRoomDataServiceTest extends IMetadataDataServiceTest<IStockRoomDataService, StockRoom> {
	public static final String DATASET = TestConstants.BASE_DATASET_DIR + "StockRoomTest.xml";

	protected IItemDataService itemService;
	protected IDepartmentDataService departmentService;
	protected ICategoryDataService categoryService;
	protected IStockOperationDataService operationService;
	protected IItemStockDataService itemStockService;

	@Override
	public void before() throws Exception {
		super.before();

		itemService = Context.getService(IItemDataService.class);
		departmentService = Context.getService(IDepartmentDataService.class);
		categoryService = Context.getService(ICategoryDataService.class);
		operationService = Context.getService(IStockOperationDataService.class);
		itemStockService = Context.getService(IItemStockDataService.class);

		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IDepartmentDataServiceTest.DEPARTMENT_DATASET);
		executeDataSet(ICategoryDataServiceTest.CATEGORY_DATASET);
		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(DATASET);
	}

	@Override
	protected StockRoom createEntity(boolean valid) {
		StockRoom room = new StockRoom();

		if (valid) {
			room.setName("New Stock Room");
		}
		room.setDescription("This is a stock room. It is new.");
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

		StockOperationTransaction tx = new StockOperationTransaction();
		tx.setItem(itemService.getById(0));
		tx.setQuantity(5);

		operation.addTransaction(tx);
		room.addOperation(operation);

		ItemStock roomItem = new ItemStock();
		roomItem.setStockRoom(room);
		roomItem.setItem(tx.getItem());
		roomItem.setQuantity(5);

		ItemStockDetail detail = new ItemStockDetail();
		detail.setItemStock(roomItem);
		detail.setItem(tx.getItem());
		detail.setStockRoom(room);
		detail.setBatchOperation(operation);
		detail.setQuantity(5);

		roomItem.addDetail(detail);
		room.addItem(roomItem);

		return room;
	}

	@Override
	protected int getTestEntityCount() {
		return 3;
	}

	@Override
	protected void updateEntityFields(StockRoom room) {
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
				// Delete an existing stock room item
				item = iterator.next();

				items.remove(item);
			}
		}
	}

	public static void assertStockroom(StockRoom expected, StockRoom actual) {
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

		Assert.assertEquals(expected.getStockRoom().getId(), actual.getStockRoom().getId());
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

	public static void assertItemStockDetail(ItemStockDetail expected, ItemStockDetail actual) {
		assertOpenmrsObject(expected, actual);

		Assert.assertEquals(expected.getItemStock(), actual.getItemStock());
		Assert.assertEquals(expected.getItem(), actual.getItem());
		Assert.assertEquals(expected.getStockRoom(), actual.getStockRoom());
		Assert.assertEquals(expected.getItem(), actual.getItem());
		Assert.assertEquals(expected.getQuantity(), actual.getQuantity());
		Assert.assertEquals(expected.getExpiration(), actual.getExpiration());
		Assert.assertEquals(expected.getBatchOperation(), actual.getBatchOperation());
		Assert.assertEquals(expected.isCalculatedExpiration(), actual.isCalculatedExpiration());
		Assert.assertEquals(expected.isCalculatedBatch(), actual.isCalculatedBatch());
	}

	@Override
	protected void assertEntity(StockRoom expected, StockRoom actual) {
		assertStockroom(expected, actual);
	}

	/**
	 * @verifies return items filtered by template and stock room
	 * @see IStockRoomDataService#findItems(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void findItems_shouldReturnItemsFilteredByTemplateAndStockRoom() throws Exception {
		StockRoom stockRoom = service.getById(1);
		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setDepartment(departmentService.getById(0));

		List<ItemStock> results = service.findItems(stockRoom, search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		ItemStock item = results.get(0);
		item.getItem().setDepartment(departmentService.getById(1));
		item.getItem().setCategory(categoryService.getById(1));

		itemService.save(item.getItem());
		Context.flushSession();

		search.getTemplate().setDepartment(item.getItem().getDepartment());
		search.getTemplate().setCategory(item.getItem().getCategory());

		results = service.findItems(stockRoom, search, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(item.getId(), results.get(0).getId());
	}

	/**
	 * @verifies not return items for other stock rooms
	 * @see IStockRoomDataService#findItems(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void findItems_shouldNotReturnItemsForOtherStockRooms() throws Exception {
		StockRoom room0 = service.getById(0);
		StockRoom room1 = service.getById(1);

		ItemSearch search = new ItemSearch();
		search.setNameComparisonType(BaseObjectTemplateSearch.StringComparisonType.EQUAL);
		search.getTemplate().setName(itemService.getById(0).getName());

		List<ItemStock> results0 = service.findItems(room0, search, null);
		List<ItemStock> results1 = service.findItems(room1, search, null);
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
	 * @see IStockRoomDataService#findItems(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void findItems_shouldReturnAllFoundItemsIfPagingIsNull() throws Exception {
		StockRoom stockRoom = service.getById(1);
		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setDepartment(departmentService.getById(0));

		List<ItemStock> results = service.findItems(stockRoom, search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());
	}

	/**
	 * @verifies return paged items if paging is specified
	 * @see IStockRoomDataService#findItems(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void findItems_shouldReturnPagedItemsIfPagingIsSpecified() throws Exception {
		StockRoom stockRoom = service.getById(1);
		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setDepartment(departmentService.getById(0));

		PagingInfo paging = new PagingInfo(1, 1);
		List<ItemStock> results = service.findItems(stockRoom, search, paging);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(3, (long)paging.getTotalRecordCount());
	}

	/**
	 * @verifies return retired items from search unless specified
	 * @see IStockRoomDataService#findItems(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void findItems_shouldReturnRetiredItemsFromSearchUnlessSpecified() throws Exception {
		Item item = itemService.getById(0);
		itemService.retire(item, "Just cuz");
		Context.flushSession();

		StockRoom stockRoom = service.getById(1);
		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setDepartment(departmentService.getById(0));

		List<ItemStock> results = service.findItems(stockRoom, search, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		search.setIncludeRetired(null);
		search.getTemplate().setRetired(false);
		results = service.findItems(stockRoom, search, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies throw IllegalArgumentException if stock room is null
	 * @see IStockRoomDataService#findItems(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void findItems_shouldThrowIllegalArgumentExceptionIfStockRoomIsNull() throws Exception {
		service.findItems(null, new ItemSearch(new Item()), null);
	}

	/**
	 * @verifies throw IllegalArgumentException if item search is null
	 * @see IStockRoomDataService#findItems(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void findItems_shouldThrowIllegalArgumentExceptionIfItemSearchIsNull() throws Exception {
		StockRoom stockRoom = service.getById(1);
		service.findItems(stockRoom, null, null);
	}

	/**
	 * @verifies return the stock room item
	 * @see IStockRoomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item)
	 */
	@Test
	public void getItem_shouldReturnTheStockRoomItem() throws Exception {
		StockRoom room = service.getById(1);

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
	 * @verifies not return items from other stock rooms
	 * @see IStockRoomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item)
	 */
	@Test
	public void getItem_shouldNotReturnItemsFromOtherStockRooms() throws Exception {
		StockRoom room0 = service.getById(0);
		StockRoom room1 = service.getById(1);

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
	 * @see IStockRoomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item)
	 */
	@Test
	public void getItem_shouldReturnNullWhenItemIsNotFound() throws Exception {
		StockRoom room = service.getById(2);

		ItemStock result = service.getItem(room, itemService.getById(0));
		Assert.assertNull(result);
	}

	/**
	 * @verifies throw IllegalArgumentException when stock room is null
	 * @see IStockRoomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItem_shouldThrowIllegalArgumentExceptionWhenStockRoomIsNull() throws Exception {
		service.getItem(null, itemService.getById(0));
	}

	/**
	 * @verifies throw IllegalArgumentException when item is null
	 * @see IStockRoomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItem_shouldThrowIllegalArgumentExceptionWhenItemIsNull() throws Exception {
		StockRoom room = service.getById(0);
		service.getItem(room, null);
	}

	/**
	 * @verifies return all the items in the stock room ordered by item name
	 * @see IStockRoomDataService#getItemsByRoom(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemsByRoom_shouldReturnAllTheItemsInTheStockRoomOrderedByItemName() throws Exception {
		StockRoom stockRoom = service.getById(1);
		List<ItemStock> results = service.getItemsByRoom(stockRoom, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		Assert.assertEquals(0, (int)results.get(0).getItem().getId());
		Assert.assertEquals(1, (int)results.get(1).getItem().getId());
		Assert.assertEquals(2, (int)results.get(2).getItem().getId());
	}

	/**
	 * @verifies return an empty list if there are no items in the stock room
	 * @see IStockRoomDataService#getItemsByRoom(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemsByRoom_shouldReturnAnEmptyListIfThereAreNoItemsInTheStockRoom() throws Exception {
		// Create a new stockroom with no items
		StockRoom stockRoom = createEntity(true);
		stockRoom.getItems().clear();
		service.save(stockRoom);
		Context.flushSession();

		List<ItemStock> results = service.getItemsByRoom(stockRoom, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return paged items if paging is specified
	 * @see IStockRoomDataService#getItemsByRoom(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemsByRoom_shouldReturnPagedItemsIfPagingIsSpecified() throws Exception {
		StockRoom stockRoom = service.getById(1);
		PagingInfo paging = new PagingInfo(1, 1);
		List<ItemStock> results = service.getItemsByRoom(stockRoom, paging);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(3, (long)paging.getTotalRecordCount());
	}

	/**
	 * @verifies throw IllegalArgumentException if the stock room is null
	 * @see IStockRoomDataService#getItemsByRoom(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItemsByRoom_shouldThrowIllegalArgumentExceptionIfTheStockRoomIsNull() throws Exception {
		service.getItemsByRoom(null, null);
	}

	/**
	 * @verifies return the stock room item detail
	 * @see IStockRoomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date, org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void getStockroomItemDetail_shouldReturnTheStockRoomItemDetail() throws Exception {
		Item item = itemService.getById(0);
		StockRoom stockroom = service.getById(0);
		StockOperation batchOperation = operationService.getById(0);

		ItemStockDetail detail = service.getStockroomItemDetail(stockroom, item, null, batchOperation);

		Assert.assertNotNull(detail);
		Assert.assertEquals(stockroom, detail.getStockRoom());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(batchOperation, detail.getBatchOperation());
		Assert.assertNull(detail.getExpiration());
		Assert.assertEquals(4, detail.getQuantity());
	}

	/**
	 * @verifies not return details for other stock rooms
	 * @see IStockRoomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date, org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void getStockroomItemDetail_shouldNotReturnDetailsForOtherStockRooms() throws Exception {
		Item item = itemService.getById(0);
		StockRoom stockroom0 = service.getById(0);
		StockRoom stockroom1 = service.getById(1);
		StockOperation batchOperation = operationService.getById(0);

		ItemStockDetail detail0 = service.getStockroomItemDetail(stockroom0, item, null, batchOperation);
		ItemStockDetail detail1 = service.getStockroomItemDetail(stockroom1, item, null, batchOperation);

		Assert.assertNotNull(detail0);
		Assert.assertEquals(stockroom0, detail0.getStockRoom());
		Assert.assertEquals(item, detail0.getItem());
		Assert.assertEquals(batchOperation, detail0.getBatchOperation());
		Assert.assertNull(detail0.getExpiration());
		Assert.assertEquals(4, detail0.getQuantity());

		Assert.assertNotNull(detail1);
		Assert.assertEquals(stockroom1, detail1.getStockRoom());
		Assert.assertEquals(item, detail1.getItem());
		Assert.assertEquals(batchOperation, detail1.getBatchOperation());
		Assert.assertNull(detail1.getExpiration());
		Assert.assertEquals(5, detail1.getQuantity());

		Assert.assertNotEquals(detail0.getId(), detail1.getId());
	}

	/**
	 * @verifies return null when the details is not found
	 * @see IStockRoomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date, org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void getStockroomItemDetail_shouldReturnNullWhenTheDetailsIsNotFound() throws Exception {
		Item item = itemService.getById(0);
		StockRoom stockroom = service.getById(0);

		// This is not a valid batch operation
		StockOperation batchOperation = operationService.getById(1);

		ItemStockDetail detail = service.getStockroomItemDetail(stockroom, item, null, batchOperation);

		Assert.assertNull(detail);
	}

	/**
	 * @verifies return detail with expiration and batch when specified
	 * @see IStockRoomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date, org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void getStockroomItemDetail_shouldReturnDetailWithExpirationAndBatchWhenSpecified() throws Exception {
		// This is an item with an expiration
		Item item = itemService.getById(2);
		StockRoom stockroom = service.getById(0);
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
		Assert.assertEquals(stockroom, detail.getStockRoom());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(batchOperation, detail.getBatchOperation());
		Assert.assertEquals(stockDetail.getExpiration(), detail.getExpiration());
		Assert.assertEquals(stockDetail.getQuantity(), detail.getQuantity());
	}

	/**
	 * @verifies return detail without an expiration when not specified
	 * @see IStockRoomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date, org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void getStockroomItemDetail_shouldReturnDetailWithoutAnExpirationWhenNotSpecified() throws Exception {
		// This is an item with an expiration
		Item item = itemService.getById(2);
		StockRoom stockroom = service.getById(0);
		StockOperation batchOperation = operationService.getById(0);

		ItemStock stock = service.getItem(stockroom, item);
		ItemStockDetail stockDetail = Iterators.get(stock.getDetails().iterator(), 0);
		Assert.assertNotNull(stockDetail.getExpiration());

		// Add a new detail with a different expiration
		ItemStockDetail newDetail = new ItemStockDetail();
		newDetail.setBatchOperation(batchOperation);
		newDetail.setStockRoom(stockroom);
		newDetail.setQuantity(10);
		newDetail.setItem(item);
		newDetail.setItemStock(stock);
		newDetail.setExpiration(null);

		stock.addDetail(newDetail);

		// Save the new detail
		itemStockService.save(stock);
		Context.flushSession();

		// Check that using the expiration date returns the expected detail
		ItemStockDetail detail = service.getStockroomItemDetail(stockroom, item, stockDetail.getExpiration(), batchOperation);

		Assert.assertNotNull(detail);
		Assert.assertEquals(stockroom, detail.getStockRoom());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(batchOperation, detail.getBatchOperation());
		Assert.assertEquals(stockDetail.getExpiration(), detail.getExpiration());
		Assert.assertEquals(stockDetail.getQuantity(), detail.getQuantity());

		// Check using a null expiration
		detail = service.getStockroomItemDetail(stockroom, item, null, batchOperation);
		Assert.assertNotNull(detail);
		Assert.assertEquals(stockroom, detail.getStockRoom());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(batchOperation, detail.getBatchOperation());
		Assert.assertEquals(newDetail.getExpiration(), detail.getExpiration());
		Assert.assertEquals(newDetail.getQuantity(), detail.getQuantity());
	}

	/**
	 * @verifies return detail without a batch when not specified
	 * @see IStockRoomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date, org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void getStockroomItemDetail_shouldReturnDetailWithoutABatchWhenNotSpecified() throws Exception {
		Item item = itemService.getById(0);
		StockRoom stockroom = service.getById(0);

		ItemStock stock = service.getItem(stockroom, item);
		ItemStockDetail stockDetail = Iterators.get(stock.getDetails().iterator(), 0);

		// Add a new detail with no batch
		ItemStockDetail newDetail = new ItemStockDetail();
		newDetail.setBatchOperation(null);
		newDetail.setStockRoom(stockroom);
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
		Assert.assertEquals(stockroom, detail.getStockRoom());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertEquals(stockDetail.getBatchOperation(), detail.getBatchOperation());
		Assert.assertEquals(stockDetail.getExpiration(), detail.getExpiration());
		Assert.assertEquals(stockDetail.getQuantity(), detail.getQuantity());

		// Check using a null batch
		detail = service.getStockroomItemDetail(stockroom, item, null, null);
		Assert.assertNotNull(detail);
		Assert.assertEquals(stockroom, detail.getStockRoom());
		Assert.assertEquals(item, detail.getItem());
		Assert.assertNull(detail.getBatchOperation());
		Assert.assertEquals(newDetail.getExpiration(), detail.getExpiration());
		Assert.assertEquals(newDetail.getQuantity(), detail.getQuantity());
	}

	/**
	 * @verifies throw IllegalArgumentException when stock room is null
	 * @see IStockRoomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date, org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getStockroomItemDetail_shouldThrowIllegalArgumentExceptionWhenStockRoomIsNull() throws Exception {
		Item item = itemService.getById(0);
		StockOperation batchOperation = operationService.getById(0);

		service.getStockroomItemDetail(null, item, new Date(), batchOperation);
	}

	/**
	 * @verifies throw IllegalArgumentException when item is null
	 * @see IStockRoomDataService#getStockroomItemDetail(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date, org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getStockroomItemDetail_shouldThrowIllegalArgumentExceptionWhenItemIsNull() throws Exception {
		StockRoom stockroom = service.getById(0);
		StockOperation batchOperation = operationService.getById(0);

		service.getStockroomItemDetail(stockroom, null, new Date(), batchOperation);
	}

	/**
	 * @verifies return item stock sorted by item name
	 * @see IStockRoomDataService#getItemsByRoom(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.commons.api.PagingInfo)
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
		StockRoom room = service.getById(2);

		ItemStock stockX = new ItemStock();
		stockX.setStockRoom(room);
		stockX.setItem(newItemX);
		stockX.setQuantity(10);

		ItemStock stockA = new ItemStock();
		stockA.setStockRoom(room);
		stockA.setItem(newItemA);
		stockA.setQuantity(20);

		ItemStock stockZ = new ItemStock();
		stockZ.setStockRoom(room);
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
	 * @see IStockRoomDataService#findItems(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void findItems_shouldReturnItemStockSortedByItemName() throws Exception {
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
		StockRoom room = service.getById(2);

		ItemStock stockX = new ItemStock();
		stockX.setStockRoom(room);
		stockX.setItem(newItemX);
		stockX.setQuantity(10);

		ItemStock stockA = new ItemStock();
		stockA.setStockRoom(room);
		stockA.setItem(newItemA);
		stockA.setQuantity(20);

		ItemStock stockZ = new ItemStock();
		stockZ.setStockRoom(room);
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
		List<ItemStock> items = service.findItems(room, search, null);

		Assert.assertNotNull(items);
		Assert.assertEquals(3, items.size());

		Assert.assertEquals(stockA.getId(), items.get(0).getId());
		Assert.assertEquals(stockX.getId(), items.get(1).getId());
		Assert.assertEquals(stockZ.getId(), items.get(2).getId());
	}
}
