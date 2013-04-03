package org.openmrs.module.openhmis.inventory.api;

import org.junit.Assert;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.inventory.api.model.*;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class IStockRoomDataServiceTest extends IMetadataDataServiceTest<IStockRoomDataService, StockRoom> {
	public static final String DATASET = TestConstants.BASE_DATASET_DIR + "StockRoomTest.xml";

	protected IItemDataService itemService;
	protected IStockRoomTransactionDataService transactionService;
	protected IStockRoomTransactionTypeDataService transactionTypeService;

	@Override
	public void before() throws Exception {
		super.before();

		itemService = Context.getService(IItemDataService.class);
		transactionService = Context.getService(IStockRoomTransactionDataService.class);
		transactionTypeService = Context.getService(IStockRoomTransactionTypeDataService.class);

		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IDepartmentDataServiceTest.DEPARTMENT_DATASET);
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

		StockRoomTransaction transaction = new StockRoomTransaction();
		transaction.setTransactionType(transactionTypeService.getById(0));
		transaction.setDestination(room);
		transaction.setTransactionNumber("something");
		transaction.setCreator(Context.getAuthenticatedUser());
		transaction.setDateCreated(new Date());
		transaction.setStatus(StockRoomTransactionStatus.COMPLETED);

		StockRoomTransactionItem transactionItem = new StockRoomTransactionItem();
		transactionItem.setImportTransaction(transaction);
		transactionItem.setItem(itemService.getById(0));
		transactionItem.setQuantityOrdered(5);

		transaction.addItem(transactionItem);
		room.addTransaction(transaction);

		StockRoomItem roomItem = new StockRoomItem();
		roomItem.setStockRoom(room);
		roomItem.setImportTransaction(transaction);
		roomItem.setItem(transactionItem.getItem());
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
		StockRoomTransaction transaction = new StockRoomTransaction();
		transaction.setTransactionType(transactionTypeService.getById(2));
		transaction.setSource(room);
		transaction.setTransactionNumber("something2");
		transaction.setCreator(Context.getAuthenticatedUser());
		transaction.setDateCreated(new Date());
		transaction.setStatus(StockRoomTransactionStatus.COMPLETED);

		StockRoomTransactionItem transactionItem = new StockRoomTransactionItem();
		transactionItem.setItem(itemService.getById(0));
		transactionItem.setQuantityOrdered(3);
		transactionItem.setImportTransaction(transactionService.getById(0));

		transaction.addItem(transactionItem);
		room.addTransaction(transaction);

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

		assertCollection(expected.getTransactions(), actual.getTransactions(), new Action2<StockRoomTransaction, StockRoomTransaction>() {
			@Override
			public void apply(StockRoomTransaction expectedTrans, StockRoomTransaction actualTrans) {
				assertOpenmrsObject(expectedTrans, actualTrans);

				Assert.assertEquals(expectedTrans.getTransactionType(), actualTrans.getTransactionType());
				Assert.assertEquals(expectedTrans.getTransactionNumber(), actualTrans.getTransactionNumber());
				Assert.assertEquals(expectedTrans.getStatus(), actualTrans.getStatus());
				Assert.assertEquals(expectedTrans.getSource(), actualTrans.getSource());
				Assert.assertEquals(expectedTrans.getDestination(), actualTrans.getDestination());
				Assert.assertEquals(expectedTrans.getTransactionType(), actualTrans.getTransactionType());

				assertCollection(expectedTrans.getItems(), actualTrans.getItems(), new Action2<StockRoomTransactionItem, StockRoomTransactionItem>() {
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
		});

		assertCollection(expected.getItems(), actual.getItems(), new Action2<StockRoomItem, StockRoomItem>() {
			@Override
			public void apply(StockRoomItem expectedStockItem, StockRoomItem actualStockItem) {
				assertOpenmrsObject(expectedStockItem, actualStockItem);

				Assert.assertEquals(expectedStockItem.getStockRoom().getId(), actualStockItem.getStockRoom().getId());
				Assert.assertEquals(expectedStockItem.getImportTransaction().getId(), actualStockItem.getImportTransaction().getId());
				Assert.assertEquals(expectedStockItem.getItem().getId(), actualStockItem.getItem().getId());
				Assert.assertEquals(expectedStockItem.getQuantity(), actualStockItem.getQuantity());
				Assert.assertEquals(expectedStockItem.getExpiration(), actualStockItem.getExpiration());
			}
		});
	}
}
