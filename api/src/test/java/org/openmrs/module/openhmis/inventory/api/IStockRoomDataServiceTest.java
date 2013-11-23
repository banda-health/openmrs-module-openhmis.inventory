package org.openmrs.module.openhmis.inventory.api;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;

import java.util.*;

public class IStockRoomDataServiceTest extends IMetadataDataServiceTest<IStockRoomDataService, StockRoom> {
	public static final String DATASET = TestConstants.BASE_DATASET_DIR + "StockRoomTest.xml";

	protected IItemDataService itemService;
	protected IDepartmentDataService departmentService;
	protected ICategoryDataService categoryService;

	@Override
	public void before() throws Exception {
		super.before();

		itemService = Context.getService(IItemDataService.class);
		departmentService = Context.getService(IDepartmentDataService.class);
		categoryService = Context.getService(ICategoryDataService.class);

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

		StockRoomItem roomItem = new StockRoomItem();
		roomItem.setStockRoom(room);
		roomItem.setBatchOperation(operation);
		roomItem.setItem(tx.getItem());
		roomItem.setQuantity(5);

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

		Set<StockRoomItem> items = room.getItems();
		if (items.size() > 0) {
			// Update an existing item quantity
			Iterator<StockRoomItem> iterator = items.iterator();
			StockRoomItem item = iterator.next();
			item.setQuantity(item.getQuantity() + 1);

			if (items.size() > 1) {
				// Delete an existing stock room item
				item = iterator.next();

				items.remove(item);
			}
		}
	}

	@Override
	protected void assertEntity(StockRoom expected, StockRoom actual) {
		super.assertEntity(expected, actual);

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

		assertCollection(expected.getItems(), actual.getItems(), new Action2<StockRoomItem, StockRoomItem>() {
			@Override
			public void apply(StockRoomItem expectedStockItem, StockRoomItem actualStockItem) {
				assertOpenmrsObject(expectedStockItem, actualStockItem);

				Assert.assertEquals(expectedStockItem.getStockRoom().getId(), actualStockItem.getStockRoom().getId());
				// TODO: Reimplement batch operation tracking
				//Assert.assertEquals(expectedStockItem.getBatchOperation().getId(), actualStockItem.getBatchOperation().getId());
				Assert.assertEquals(expectedStockItem.getItem().getId(), actualStockItem.getItem().getId());
				Assert.assertEquals(expectedStockItem.getQuantity(), actualStockItem.getQuantity());
				Assert.assertEquals(expectedStockItem.getExpiration(), actualStockItem.getExpiration());
			}
		});
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

		List<StockRoomItem> results = service.findItems(stockRoom, search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		StockRoomItem item = results.get(0);
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
		StockRoom stockRoom = service.getById(2);
		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setDepartment(departmentService.getById(0));

		List<StockRoomItem> results = service.findItems(stockRoom, search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
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

		List<StockRoomItem> results = service.findItems(stockRoom, search, null);

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
		List<StockRoomItem> results = service.findItems(stockRoom, search, paging);

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

		List<StockRoomItem> results = service.findItems(stockRoom, search, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		search.setIncludeRetired(null);
		search.getTemplate().setRetired(false);
		results = service.findItems(stockRoom, search, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	/**
	 * @verifies throw NullReferenceException if stock room is null
	 * @see IStockRoomDataService#findItems(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = NullPointerException.class)
	public void findItems_shouldThrowNullReferenceExceptionIfStockRoomIsNull() throws Exception {
		service.findItems(null, new ItemSearch(new Item()), null);
	}

	/**
	 * @verifies throw NullReferenceException if item search is null
	 * @see IStockRoomDataService#findItems(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.search.ItemSearch, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = NullPointerException.class)
	public void findItems_shouldThrowNullReferenceExceptionIfItemSearchIsNull() throws Exception {
		StockRoom stockRoom = service.getById(1);
		service.findItems(stockRoom, null, null);
	}

	/**
	 * @verifies return the stock room item
	 * @see IStockRoomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date)
	 */
	@Test
	public void getItem_shouldReturnTheStockRoomItem() throws Exception {
		StockRoom room = service.getById(1);

		StockRoomItem result = service.getItem(room, itemService.getById(0), null);

		Assert.assertNotNull(result);

		StockRoomItem item = null;
		for (StockRoomItem roomItem : room.getItems()) {
			if (roomItem.getItem().getId().equals(result.getItem().getId())) {
				item = roomItem;
				break;
			}
		}

		Assert.assertNotNull(item);
		Assert.assertEquals(item.getId(), result.getId());
		Assert.assertEquals(item.getExpiration(), result.getExpiration());
		Assert.assertEquals(item.getQuantity(), result.getQuantity());
	}

	/**
	 * @verifies not return items from other stock rooms
	 * @see IStockRoomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date)
	 */
	@Test
	public void getItem_shouldNotReturnItemsFromOtherStockRooms() throws Exception {
		StockRoom room = service.getById(2);

		Calendar cal = Calendar.getInstance();
		cal.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		StockRoomItem result = service.getItem(room, itemService.getById(2), cal.getTime());
		Assert.assertNotNull(result);

		StockRoomItem item = null;
		for (StockRoomItem roomItem : room.getItems()) {
			if (roomItem.getItem().getId().equals(result.getItem().getId())) {
				item = roomItem;
				break;
			}
		}

		Assert.assertNotNull(item);
		Assert.assertEquals(item.getId(), result.getId());
		Assert.assertEquals(item.getExpiration(), result.getExpiration());
		Assert.assertEquals(item.getQuantity(), result.getQuantity());
	}

	/**
	 * @verifies return null when item is not found
	 * @see IStockRoomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date)
	 */
	@Test
	public void getItem_shouldReturnNullWhenItemIsNotFound() throws Exception {
		StockRoom room = service.getById(2);

		StockRoomItem result = service.getItem(room, itemService.getById(0), null);
		Assert.assertNull(result);
	}

	/**
	 * @verifies return item with expiration when specified
	 * @see IStockRoomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date)
	 */
	@Test
	public void getItem_shouldReturnItemWithExpirationWhenSpecified() throws Exception {
		StockRoom room = service.getById(1);

		StockRoomItem result = service.getItem(room, itemService.getById(2), null);
		Assert.assertNull(result);

		Calendar cal = Calendar.getInstance();
		cal.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		result = service.getItem(room, itemService.getById(2), cal.getTime());
		Assert.assertNotNull(result);

		StockRoomItem item = null;
		for (StockRoomItem roomItem : room.getItems()) {
			if (roomItem.getItem().getId().equals(result.getItem().getId())) {
				item = roomItem;
				break;
			}
		}

		Assert.assertNotNull(item);
		Assert.assertEquals(item.getId(), result.getId());
		Assert.assertEquals(item.getExpiration(), result.getExpiration());
		Assert.assertEquals(item.getQuantity(), result.getQuantity());
	}

	/**
	 * @verifies return the item without an expiration what not specified
	 * @see IStockRoomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date)
	 */
	@Test
	public void getItem_shouldReturnTheItemWithoutAnExpirationWhatNotSpecified() throws Exception {
		StockRoom room = service.getById(1);

		Calendar cal = Calendar.getInstance();
		cal.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		StockRoomItem result = service.getItem(room, itemService.getById(1), cal.getTime());
		Assert.assertNull(result);

		result = service.getItem(room, itemService.getById(1), null);
		Assert.assertNotNull(result);

		StockRoomItem item = null;
		for (StockRoomItem roomItem : room.getItems()) {
			if (roomItem.getItem().getId().equals(result.getItem().getId())) {
				item = roomItem;
				break;
			}
		}

		Assert.assertNotNull(item);
		Assert.assertEquals(item.getId(), result.getId());
		Assert.assertEquals(item.getExpiration(), result.getExpiration());
		Assert.assertEquals(item.getQuantity(), result.getQuantity());
	}

	/**
	 * @verifies throw NullReferenceException when stock room is null
	 * @see IStockRoomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date)
	 */
	@Test(expected = NullPointerException.class)
	public void getItem_shouldThrowNullReferenceExceptionWhenStockRoomIsNull() throws Exception {
		service.getItem(null, itemService.getById(0), null);
	}

	/**
	 * @verifies throw NullReferenceException when item is null
	 * @see IStockRoomDataService#getItem(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.inventory.api.model.Item, java.util.Date)
	 */
	@Test(expected = NullPointerException.class)
	public void getItem_shouldThrowNullReferenceExceptionWhenItemIsNull() throws Exception {
		StockRoom room = service.getById(0);
		service.getItem(room, null, null);
	}

	/**
	 * @verifies return all the items in the stock room ordered by item name
	 * @see IStockRoomDataService#getItemsByRoom(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemsByRoom_shouldReturnAllTheItemsInTheStockRoomOrderedByItemName() throws Exception {
		StockRoom stockRoom = service.getById(1);
		List<StockRoomItem> results = service.getItemsByRoom(stockRoom, null);

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

		List<StockRoomItem> results = service.getItemsByRoom(stockRoom, null);

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
		List<StockRoomItem> results = service.getItemsByRoom(stockRoom, paging);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(3, (long)paging.getTotalRecordCount());
	}

	/**
	 * @verifies throw NullReferenceException if the stock room is null
	 * @see IStockRoomDataService#getItemsByRoom(org.openmrs.module.openhmis.inventory.api.model.StockRoom, org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = NullPointerException.class)
	public void getItemsByRoom_shouldThrowNullReferenceExceptionIfTheStockRoomIsNull() throws Exception {
		service.getItemsByRoom(null, null);
	}
}
