package org.openmrs.module.openhmis.inventory.api.impl;

import static org.openmrs.module.openhmis.inventory.ModuleSettings.RESTRICT_NEGATIVE_INVENTORY_STOCK_CREATION_FIELD;
import static org.openmrs.module.openhmis.inventory.ModuleSettings.isNegativeStockRestricted;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.BaseModuleContextTest;
import org.openmrs.module.openhmis.inventory.ModuleSettings;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.IItemDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.IItemStockDataService;
import org.openmrs.module.openhmis.inventory.api.IItemStockDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.ITestableStockOperationService;
import org.openmrs.module.openhmis.inventory.api.TestConstants;
import org.openmrs.module.openhmis.inventory.api.WellKnownOperationTypes;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction;
import org.openmrs.module.openhmis.inventory.api.model.Settings;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;

public class StockOperationServiceImplTest extends BaseModuleContextTest {
	IItemDataService itemDataService;
	IStockroomDataService stockroomDataService;
	IItemStockDataService itemStockDataService;
	IStockOperationDataService operationDataService;
	ITestableStockOperationService service;
	AdministrationService adminService;

	IItemDataServiceTest itemTest;
	IStockOperationDataServiceTest operationTest;
	IStockroomDataServiceTest stockroomTest;
	IItemStockDataServiceTest itemStockTest;

	@Before
	public void before() throws Exception {
		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(IStockroomDataServiceTest.DATASET);

		stockroomDataService = Context.getService(IStockroomDataService.class);
		itemDataService = Context.getService(IItemDataService.class);
		itemStockDataService = Context.getService(IItemStockDataService.class);
		operationDataService = Context.getService(IStockOperationDataService.class);
		service = Context.getService(ITestableStockOperationService.class);

		adminService = Context.getAdministrationService();

		itemTest = new IItemDataServiceTest();
		operationTest = new IStockOperationDataServiceTest();
		stockroomTest = new IStockroomDataServiceTest();
		itemStockTest = new IItemStockDataServiceTest();
	}

	protected ItemStockDetail findDetail(ItemStock stock, final Date expiration) {
		Collection<ItemStockDetail> results = findDetails(stock, expiration);
		return results == null ? null : Iterators.get(results.iterator(), 0);
	}

	protected Collection<ItemStockDetail> findDetails(ItemStock stock, final Date expiration) {
		if (stock == null || stock.getDetails() == null) {
			return null;
		}

		Collection<ItemStockDetail> results = Collections2.filter(stock.getDetails(), new Predicate<ItemStockDetail>() {
			@Override
			public boolean apply(@Nullable ItemStockDetail detail) {
				if (detail.getExpiration() == null || expiration == null) {
					return detail.getExpiration() == null && expiration == null;
				} else {
					return DateUtils.isSameDay(detail.getExpiration(), expiration);
				}
			}
		});

		return results.size() == 0 ? null : results;
	}

	/**
	 * @verifies use furthest expiration from the source stockroom
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldUseFurthestExpirationFromTheSourceStockroom() throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoSelectItemStockFurthestExpirationDate(true);
		ModuleSettings.saveSettings(settings);

		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item item0 = itemDataService.getById(0);
		Item item2 = itemDataService.getById(2);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(item2);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.YEAR, 15);
		detail1.setExpiration(calendar1.getTime());

		ItemStockDetail detail2 = new ItemStockDetail();
		detail2.setItem(item2);
		detail2.setStockroom(sourceRoom);
		detail2.setQuantity(20);
		detail2.setCalculatedBatch(false);
		detail2.setCalculatedExpiration(false);
		detail2.setBatchOperation(operationDataService.getById(1));
		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.YEAR, 1);
		detail2.setExpiration(calendar2.getTime());

		ItemStock stock2 = stockroomDataService.getItem(sourceRoom, item2);
		stock2.addDetail(detail1);
		stock2.addDetail(detail2);

		itemStockDataService.save(stock2);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		final ReservedTransaction tx = operation.addReserved(item0, 3);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);
		final ReservedTransaction tx2 = operation.addReserved(item2, 5);
		tx2.setCalculatedBatch(true);
		tx2.setCalculatedExpiration(true);

		// Calculate the reservations
		service.calculateReservations(operation);

		// Ensure that no new transactions were created
		Assert.assertEquals(2, operation.getReserved().size());

		// Ensure that no expiration was set as the item is not expirable
		ReservedTransaction testTx =
		        Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			        @Override
			        public boolean apply(@Nullable ReservedTransaction input) {
				        return input == tx;
			        }
		        });
		Assert.assertEquals(item0, testTx.getItem());
		Assert.assertNull(testTx.getExpiration());

		// Ensure that the furthest expiration stock was selected
		testTx = Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			@Override
			public boolean apply(@Nullable ReservedTransaction input) {
				return input == tx2;
			}
		});
		Assert.assertEquals(item2, testTx.getItem());
		Assert.assertTrue(DateUtils.isSameDay(calendar1.getTime(), testTx.getExpiration()));
	}

	/**
	 * @verifies should first deduct from the furthest expiration from source stockroom
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldFirstDeductFromFurthestExpirationFromTheSourceStockroom() throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoSelectItemStockFurthestExpirationDate(true);
		ModuleSettings.saveSettings(settings);

		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item item0 = itemDataService.getById(0);
		Item item2 = itemDataService.getById(2);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(item2);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.YEAR, 15);
		detail1.setExpiration(calendar1.getTime());

		ItemStockDetail detail2 = new ItemStockDetail();
		detail2.setItem(item2);
		detail2.setStockroom(sourceRoom);
		detail2.setQuantity(20);
		detail2.setCalculatedBatch(false);
		detail2.setCalculatedExpiration(false);
		detail2.setBatchOperation(operationDataService.getById(1));
		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.YEAR, 1);
		detail2.setExpiration(calendar2.getTime());

		ItemStock stock2 = stockroomDataService.getItem(sourceRoom, item2);
		stock2.addDetail(detail1);
		stock2.addDetail(detail2);

		itemStockDataService.save(stock2);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		final ReservedTransaction tx = operation.addReserved(item0, 3);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);
		final ReservedTransaction tx2 = operation.addReserved(item2, -11);
		tx2.setCalculatedBatch(true);
		tx2.setCalculatedExpiration(true);

		// Calculate the reservations
		service.calculateReservations(operation);

		// Ensure that a new transaction has been created
		Assert.assertEquals(3, operation.getReserved().size());

		// Ensure that no expiration was set as the item is not expirable
		ReservedTransaction testTx =
		        Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			        @Override
			        public boolean apply(@Nullable ReservedTransaction input) {
				        return input == tx;
			        }
		        });
		Assert.assertEquals(item0, testTx.getItem());
		Assert.assertNull(testTx.getExpiration());

		// Ensure that the furthest expiration stock was selected
		testTx = Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			@Override
			public boolean apply(@Nullable ReservedTransaction input) {
				return input == tx2;
			}
		});
		Assert.assertEquals(item2, testTx.getItem());

		// transaction quantity should be negative
		Integer deductedQuantity = detail1.getQuantity() * -1;

		Assert.assertEquals(deductedQuantity, testTx.getQuantity());
	}

	/**
	 * @verifies use furthest null expiration from the source stockroom
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldUseFurthestNullExpirationFromTheSourceStockroom() throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoSelectItemStockFurthestExpirationDate(true);
		ModuleSettings.saveSettings(settings);

		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item item0 = itemDataService.getById(0);
		Item item2 = itemDataService.getById(2);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(item2);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));

		ItemStockDetail detail2 = new ItemStockDetail();
		detail2.setItem(item2);
		detail2.setStockroom(sourceRoom);
		detail2.setQuantity(20);
		detail2.setCalculatedBatch(false);
		detail2.setCalculatedExpiration(false);
		detail2.setBatchOperation(operationDataService.getById(1));
		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.YEAR, 1);
		detail2.setExpiration(calendar2.getTime());

		ItemStock stock2 = stockroomDataService.getItem(sourceRoom, item2);
		stock2.addDetail(detail1);
		stock2.addDetail(detail2);

		itemStockDataService.save(stock2);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		final ReservedTransaction tx = operation.addReserved(item0, 3);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);
		final ReservedTransaction tx2 = operation.addReserved(item2, 5);
		tx2.setCalculatedBatch(true);
		tx2.setCalculatedExpiration(true);

		// Calculate the reservations
		service.calculateReservations(operation);

		// Ensure that no new transactions were created
		Assert.assertEquals(2, operation.getReserved().size());

		// Ensure that no expiration was set as the item is not expirable
		ReservedTransaction testTx =
		        Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			        @Override
			        public boolean apply(@Nullable ReservedTransaction input) {
				        return input == tx;
			        }
		        });
		Assert.assertEquals(item0, testTx.getItem());
		Assert.assertNull(testTx.getExpiration());

		// Ensure that the furthest expiration stock was selected
		testTx = Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			@Override
			public boolean apply(@Nullable ReservedTransaction input) {
				return input == tx2;
			}
		});
		Assert.assertEquals(item2, testTx.getItem());
		Assert.assertNull(testTx.getExpiration());
	}

	/**
	 * @verifies use closest expiration from the source stockroom
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldUseClosestExpirationFromTheSourceStockroom() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item item0 = itemDataService.getById(0);
		Item item2 = itemDataService.getById(2);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(item2);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.YEAR, 5);
		detail1.setExpiration(calendar1.getTime());

		ItemStockDetail detail2 = new ItemStockDetail();
		detail2.setItem(item2);
		detail2.setStockroom(sourceRoom);
		detail2.setQuantity(20);
		detail2.setCalculatedBatch(false);
		detail2.setCalculatedExpiration(false);
		detail2.setBatchOperation(operationDataService.getById(1));
		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.YEAR, 1);
		detail2.setExpiration(calendar2.getTime());

		ItemStock stock2 = stockroomDataService.getItem(sourceRoom, item2);
		stock2.addDetail(detail1);
		stock2.addDetail(detail2);

		itemStockDataService.save(stock2);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		final ReservedTransaction tx = operation.addReserved(item0, 3);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);
		final ReservedTransaction tx2 = operation.addReserved(item2, 10);
		tx2.setCalculatedBatch(true);
		tx2.setCalculatedExpiration(true);

		// Calculate the reservations
		service.calculateReservations(operation);

		// Ensure that no new transactions were created
		Assert.assertEquals(2, operation.getReserved().size());

		// Ensure that no expiration was set as the item is not expirable
		ReservedTransaction testTx =
		        Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			        @Override
			        public boolean apply(@Nullable ReservedTransaction input) {
				        return input == tx;
			        }
		        });
		Assert.assertEquals(item0, testTx.getItem());
		Assert.assertNull(testTx.getExpiration());

		// Ensure that the closest expiration stock was selected
		testTx = Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			@Override
			public boolean apply(@Nullable ReservedTransaction input) {
				return input == tx2;
			}
		});
		Assert.assertEquals(item2, testTx.getItem());
		Assert.assertTrue(DateUtils.isSameDay(calendar2.getTime(), testTx.getExpiration()));
	}

	/**
	 * @verifies use oldest batch operation with the calculated expiration
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldUseOldestBatchOperationWithTheCalculatedExpiration() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item item0 = itemDataService.getById(0);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(item0);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));

		ItemStockDetail detail2 = new ItemStockDetail();
		detail2.setItem(item0);
		detail2.setStockroom(sourceRoom);
		detail2.setQuantity(20);
		detail2.setCalculatedBatch(false);
		detail2.setCalculatedExpiration(false);
		detail2.setBatchOperation(operationDataService.getById(1));

		ItemStock stock2 = stockroomDataService.getItem(sourceRoom, item0);
		stock2.addDetail(detail1);
		stock2.addDetail(detail2);

		itemStockDataService.save(stock2);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		ReservedTransaction tx = operation.addReserved(item0, 3);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);

		service.calculateReservations(operation);

		// Ensure that no new transactions were created
		Assert.assertEquals(1, operation.getReserved().size());

		// Ensure that the correct (oldest) batch operation was set
		tx = Iterators.get(operation.getReserved().iterator(), 0);
		Assert.assertEquals(item0, tx.getItem());
		Assert.assertEquals(1, (int)tx.getBatchOperation().getId());
	}

	/**
	 * @verifies set the expiration to null if no valid item stock can be found
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldSetTheExpirationToNullIfNoValidItemStockCanBeFound() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(true);

		itemDataService.save(newItem);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());

		ReservedTransaction tx = operation.addReserved(newItem, 3);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);

		service.calculateReservations(operation);

		Assert.assertEquals(1, operation.getReserved().size());
		Assert.assertNull(tx.getExpiration());
	}

	/**
	 * @verifies set the batch to null if no valid item stock can be found
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldSetTheBatchToNullIfNoValidItemStockCanBeFound() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(true);

		itemDataService.save(newItem);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());

		ReservedTransaction tx = operation.addReserved(newItem, 3);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);

		service.calculateReservations(operation);

		Assert.assertEquals(1, operation.getReserved().size());
		Assert.assertNull(tx.getBatchOperation());
	}

	/**
	 * @verifies throw IllegalArgumentException if operation is null
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void calculateReservations_shouldThrowIllegalArgumentExceptionIfOperationIsNull() throws Exception {
		service.calculateReservations(null);
	}

	/**
	 * @verifies use date and time for expiration calculation
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldUseDateAndTimeForExpirationCalculation() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item item2 = itemDataService.getById(2);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(item2);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.YEAR, 5);
		detail1.setExpiration(calendar1.getTime());

		ItemStockDetail detail2 = new ItemStockDetail();
		detail2.setItem(item2);
		detail2.setStockroom(sourceRoom);
		detail2.setQuantity(20);
		detail2.setCalculatedBatch(false);
		detail2.setCalculatedExpiration(false);
		detail2.setBatchOperation(operationDataService.getById(1));
		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.YEAR, 1);
		calendar2.add(Calendar.MINUTE, 20);
		detail2.setExpiration(calendar2.getTime());

		ItemStockDetail detail3 = new ItemStockDetail();
		detail3.setItem(item2);
		detail3.setStockroom(sourceRoom);
		detail3.setQuantity(20);
		detail3.setCalculatedBatch(false);
		detail3.setCalculatedExpiration(false);
		detail3.setBatchOperation(operationDataService.getById(1));
		Calendar calendar3 = Calendar.getInstance();
		calendar3.add(Calendar.YEAR, 1);
		detail3.setExpiration(calendar3.getTime());

		ItemStock stock2 = stockroomDataService.getItem(sourceRoom, item2);
		stock2.addDetail(detail1);
		stock2.addDetail(detail2);
		stock2.addDetail(detail3);

		itemStockDataService.save(stock2);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		ReservedTransaction tx = operation.addReserved(item2, 10);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);

		service.calculateReservations(operation);

		// Ensure that no new transactions were created
		Assert.assertEquals(1, operation.getReserved().size());

		// Ensure that the closest expiration stock was selected
		tx = Iterators.get(operation.getReserved().iterator(), 0);
		Assert.assertEquals(item2, tx.getItem());
		Assert.assertTrue(DateUtils.isSameDay(calendar3.getTime(), tx.getExpiration()));
	}

	/**
	 * @verifies create additional transactions when when multiple details are need to fulfill request
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldCreateAdditionalTransactionsWhenWhenMultipleDetailsAreNeedToFulfillRequest()
	        throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item item2 = itemDataService.getById(2);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(item2);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.YEAR, 5);
		detail1.setExpiration(calendar1.getTime());

		ItemStockDetail detail2 = new ItemStockDetail();
		detail2.setItem(item2);
		detail2.setStockroom(sourceRoom);
		detail2.setQuantity(20);
		detail2.setCalculatedBatch(false);
		detail2.setCalculatedExpiration(false);
		detail2.setBatchOperation(operationDataService.getById(1));
		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.YEAR, 1);
		detail2.setExpiration(calendar2.getTime());

		ItemStock stock2 = stockroomDataService.getItem(sourceRoom, item2);
		stock2.addDetail(detail1);
		stock2.addDetail(detail2);

		itemStockDataService.save(stock2);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		final ReservedTransaction tx = operation.addReserved(item2, 25);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);

		service.calculateReservations(operation);

		// Ensure that a new transaction was created
		Assert.assertEquals(2, operation.getReserved().size());

		// Ensure that the closest expiration stock was selected
		ReservedTransaction testTx =
		        Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			        @Override
			        public boolean apply(@Nullable ReservedTransaction input) {
				        return input == tx;
			        }
		        });
		Assert.assertEquals(item2, testTx.getItem());
		Assert.assertTrue(DateUtils.isSameDay(calendar2.getTime(), testTx.getExpiration()));
		Assert.assertEquals(1, (int)testTx.getBatchOperation().getId());
		Assert.assertEquals(20, (int)testTx.getQuantity());

		// And then the next closest
		testTx = Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			@Override
			public boolean apply(@Nullable ReservedTransaction input) {
				return input != tx;
			}
		});
		Assert.assertEquals(item2, testTx.getItem());
		Assert.assertTrue(DateUtils.isSameDay(calendar1.getTime(), testTx.getExpiration()));
		Assert.assertEquals(2, (int)testTx.getBatchOperation().getId());
		Assert.assertEquals(5, (int)testTx.getQuantity());
	}

	/**
	 * @verifies create additional null qualifier transaction when there is not enough valid item stock to fulfill request
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldCreateAdditionalNullQualifierTransactionWhenThereIsNotEnoughValidItemStockToFulfillRequest()
	        throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(true);
		itemDataService.save(newItem);
		Context.flushSession();

		// Add some item stock with different qualifiers to the source room
		ItemStock stock = new ItemStock();
		stock.setItem(newItem);
		stock.setStockroom(sourceRoom);
		stock.setQuantity(10);
		itemStockDataService.save(stock);
		Context.flushSession();

		ItemStockDetail detail1 = new ItemStockDetail();
		stock.addDetail(detail1);
		detail1.setItem(newItem);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.YEAR, 5);
		detail1.setExpiration(calendar1.getTime());

		itemStockDataService.save(stock);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		final ReservedTransaction tx = operation.addReserved(newItem, 25);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);

		service.calculateReservations(operation);

		// Ensure that a new transaction was created
		Assert.assertEquals(2, operation.getReserved().size());

		// Ensure that the existing stock detail was used

		ReservedTransaction testTx =
		        Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			        @Override
			        public boolean apply(@Nullable ReservedTransaction input) {
				        return tx == input;
			        }
		        });
		Assert.assertEquals(newItem, testTx.getItem());
		Assert.assertTrue(DateUtils.isSameDay(calendar1.getTime(), testTx.getExpiration()));
		Assert.assertEquals(2, (int)testTx.getBatchOperation().getId());
		Assert.assertEquals(10, (int)testTx.getQuantity());

		// Ensure that another reservation was created with no expiration or batch for the remaining items
		testTx = Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			@Override
			public boolean apply(@Nullable ReservedTransaction input) {
				return tx != input;
			}
		});
		Assert.assertEquals(newItem, testTx.getItem());
		Assert.assertTrue(testTx.isCalculatedExpiration());
		Assert.assertNull(testTx.getExpiration());
		Assert.assertTrue(testTx.isCalculatedBatch());
		Assert.assertNull(testTx.getBatchOperation());
		Assert.assertEquals(15, (int)testTx.getQuantity());
	}

	/**
	 * @verifies copy source calculation settings into source calculation fields
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldCopySourceCalculationSettingsIntoSourceCalculationFields() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(true);
		itemDataService.save(newItem);
		Context.flushSession();

		// Add some item stock with different qualifiers to the source room
		ItemStock stock = new ItemStock();
		stock.setItem(newItem);
		stock.setStockroom(sourceRoom);
		stock.setQuantity(100);
		itemStockDataService.save(stock);
		Context.flushSession();

		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(newItem);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(100);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(true);
		detail1.setBatchOperation(operationDataService.getById(2));
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.YEAR, 5);
		detail1.setExpiration(calendar1.getTime());

		ItemStock stock2 = stockroomDataService.getItem(sourceRoom, newItem);
		stock2.addDetail(detail1);

		itemStockDataService.save(stock2);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		ReservedTransaction tx = operation.addReserved(newItem, 25);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);

		service.calculateReservations(operation);

		// Ensure that no new transactions were created
		Assert.assertEquals(1, operation.getReserved().size());

		Assert.assertEquals(newItem, tx.getItem());
		Assert.assertFalse(tx.isSourceCalculatedBatch());
		Assert.assertTrue(tx.isSourceCalculatedExpiration());
	}

	/**
	 * @verifies set the batch operation to the specified operation if there is no source stockroom
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldSetTheBatchOperationToTheSpecifiedOperationIfThereIsNoSourceStockroom()
	        throws Exception {
		Stockroom destRoom = stockroomDataService.getById(1);

		Item newItem = itemTest.createEntity(true);
		itemDataService.save(newItem);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		operation.setStatus(StockOperationStatus.PENDING);
		ReservedTransaction tx = operation.addReserved(newItem, 25);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);

		service.calculateReservations(operation);

		// Ensure that no new transactions were created
		Assert.assertEquals(1, operation.getReserved().size());

		Assert.assertEquals(newItem, tx.getItem());
		Assert.assertEquals(operation, tx.getBatchOperation());
	}

	/**
	 * @verifies combine transactions for the same item stock and qualifiers
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldCombineTransactionsForTheSameItemStockAndQualifiers() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(true);

		itemDataService.save(newItem);
		Context.flushSession();

		// Add some item stock with different qualifiers to the source room
		ItemStock stock = new ItemStock();
		stock.setItem(newItem);
		stock.setStockroom(sourceRoom);
		stock.setQuantity(100);
		itemStockDataService.save(stock);
		Context.flushSession();

		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(newItem);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(100);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(true);
		detail1.setBatchOperation(operationDataService.getById(2));
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.YEAR, 5);
		detail1.setExpiration(calendar1.getTime());

		ItemStock stock2 = stockroomDataService.getItem(sourceRoom, newItem);
		stock2.addDetail(detail1);

		itemStockDataService.save(stock2);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		operation.setStatus(StockOperationStatus.PENDING);
		ReservedTransaction tx = operation.addReserved(newItem, 25);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);
		tx = operation.addReserved(newItem, 30);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);

		service.calculateReservations(operation);

		// Ensure that the two transactions were combined into one
		Assert.assertEquals(1, operation.getReserved().size());

		tx = Iterators.get(operation.getReserved().iterator(), 0);
		Assert.assertEquals(newItem, tx.getItem());
		Assert.assertEquals(55, (int)tx.getQuantity());
	}

	/**
	 * @verifies handle multiple transactions for the same item but with different qualifiers
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldHandleMultipleTransactionsForTheSameItemButWithDifferentQualifiers()
	        throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(true);
		itemDataService.save(newItem);
		Context.flushSession();

		// Add some item stock with different qualifiers to the source room
		ItemStock stock = new ItemStock();
		stock.setItem(newItem);
		stock.setStockroom(sourceRoom);
		stock.setQuantity(10);
		itemStockDataService.save(stock);
		Context.flushSession();

		ItemStockDetail detail1 = new ItemStockDetail();
		stock.addDetail(detail1);
		detail1.setItem(newItem);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.YEAR, 5);
		detail1.setExpiration(calendar1.getTime());

		itemStockDataService.save(stock);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		final ReservedTransaction tx = operation.addReserved(newItem, 15);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);
		final ReservedTransaction tx2 = operation.addReserved(newItem, 5, calendar1.getTime());
		tx2.setCalculatedBatch(true);
		tx2.setCalculatedExpiration(false);

		service.calculateReservations(operation);

		// Ensure that there are now 3 transactions
		Assert.assertEquals(3, operation.getReserved().size());

		ReservedTransaction testTx =
		        Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			        @Override
			        public boolean apply(@Nullable ReservedTransaction input) {
				        return input == tx;
			        }
		        });
		Assert.assertEquals(newItem, testTx.getItem());
		Assert.assertEquals(5, (int)testTx.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(calendar1.getTime(), testTx.getExpiration()));
		Assert.assertTrue(testTx.isCalculatedExpiration());

		testTx = Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			@Override
			public boolean apply(@Nullable ReservedTransaction input) {
				return input == tx2;
			}
		});
		Assert.assertEquals(newItem, testTx.getItem());
		Assert.assertEquals(5, (int)testTx.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(calendar1.getTime(), testTx.getExpiration()));
		Assert.assertFalse(testTx.isCalculatedExpiration());

		testTx = Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			@Override
			public boolean apply(@Nullable ReservedTransaction input) {
				return input != tx && input != tx2;
			}
		});
		Assert.assertEquals(newItem, testTx.getItem());
		Assert.assertEquals(10, (int)testTx.getQuantity());
		Assert.assertNull(testTx.getExpiration());
		Assert.assertTrue(testTx.isCalculatedExpiration());
	}

	/**
	 * @verifies set the transaction source calculated flags if the source was calculated
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldSetTheTransactionSourceCalculatedFlagsIfTheSourceWasCalculated()
	        throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(true);
		itemDataService.save(newItem);
		Context.flushSession();

		// Add some item stock with different qualifiers to the source room
		ItemStock stock = new ItemStock();
		stock.setItem(newItem);
		stock.setStockroom(sourceRoom);
		stock.setQuantity(10);
		itemStockDataService.save(stock);
		Context.flushSession();

		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(newItem);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(true);
		detail1.setCalculatedExpiration(true);
		detail1.setBatchOperation(operationDataService.getById(2));
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.YEAR, 5);
		detail1.setExpiration(calendar1.getTime());

		ItemStock stock2 = stockroomDataService.getItem(sourceRoom, newItem);
		stock2.addDetail(detail1);

		itemStockDataService.save(stock2);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		ReservedTransaction tx = operation.addReserved(newItem, 5);
		tx.setExpiration(detail1.getExpiration());
		tx.setBatchOperation(detail1.getBatchOperation());
		tx.setCalculatedBatch(false);
		tx.setCalculatedExpiration(false);

		service.calculateReservations(operation);

		Assert.assertEquals(1, operation.getReserved().size());

		Assert.assertEquals(newItem, tx.getItem());
		Assert.assertEquals(5, (int)tx.getQuantity());
		Assert.assertTrue(tx.isSourceCalculatedBatch());
		Assert.assertTrue(tx.isSourceCalculatedExpiration());
	}

	/**
	 * @verifies process non-calculated transactions before calculated transactions
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldProcessNoncalculatedTransactionsBeforeCalculatedTransactions() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);
		Stockroom destRoom = stockroomDataService.getById(1);

		Item newItem = itemTest.createEntity(true);
		newItem.setHasExpiration(true);
		itemDataService.save(newItem);
		Context.flushSession();

		// Add some item stock with different qualifiers to the source room
		ItemStock stock = new ItemStock();
		stock.setItem(newItem);
		stock.setStockroom(sourceRoom);
		stock.setQuantity(10);
		itemStockDataService.save(stock);
		Context.flushSession();

		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(newItem);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(20);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));
		Calendar calendar1 = Calendar.getInstance();
		calendar1.add(Calendar.YEAR, 5);
		detail1.setExpiration(calendar1.getTime());

		ItemStockDetail detail2 = new ItemStockDetail();
		detail2.setItem(newItem);
		detail2.setStockroom(sourceRoom);
		detail2.setQuantity(10);
		detail2.setCalculatedBatch(false);
		detail2.setCalculatedExpiration(false);
		detail2.setBatchOperation(operationDataService.getById(2));
		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.YEAR, 10);
		detail2.setExpiration(calendar2.getTime());

		ItemStock stock2 = stockroomDataService.getItem(sourceRoom, newItem);
		stock2.addDetail(detail1);
		stock2.addDetail(detail2);

		itemStockDataService.save(stock2);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getTransfer());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setSource(sourceRoom);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");
		operation.setOperationDate(new Date());
		final ReservedTransaction tx = operation.addReserved(newItem, 5);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);

		final ReservedTransaction tx2 = operation.addReserved(newItem, 10);
		tx2.setExpiration(calendar2.getTime());
		tx2.setBatchOperation(detail1.getBatchOperation());
		tx2.setCalculatedBatch(false);
		tx2.setCalculatedExpiration(false);

		service.calculateReservations(operation);

		Assert.assertEquals(2, operation.getReserved().size());

		ReservedTransaction testTx =
		        Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			        @Override
			        public boolean apply(@Nullable ReservedTransaction input) {
				        return input == tx;
			        }
		        });
		Assert.assertEquals(newItem, testTx.getItem());
		Assert.assertEquals(5, (int)testTx.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(calendar1.getTime(), testTx.getExpiration()));
		Assert.assertTrue(testTx.isCalculatedBatch());
		Assert.assertTrue(testTx.isCalculatedExpiration());

		testTx = Iterators.find(operation.getReserved().iterator(), new Predicate<ReservedTransaction>() {
			@Override
			public boolean apply(@Nullable ReservedTransaction input) {
				return input == tx2;
			}
		});
		Assert.assertEquals(newItem, testTx.getItem());
		Assert.assertEquals(10, (int)testTx.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(calendar2.getTime(), testTx.getExpiration()));
		Assert.assertFalse(testTx.isCalculatedBatch());
		Assert.assertFalse(testTx.isCalculatedExpiration());
	}

	/**
	 * @verifies set batch operation to past operations before future operations
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldSetBatchOperationToPastOperationsBeforeFutureOperations() throws Exception {
		Stockroom destRoom = stockroomDataService.getById(1);

		Item newItem = itemTest.createEntity(true);
		itemDataService.save(newItem);
		Context.flushSession();

		// Create a stock operation in the future
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.PENDING);
		operation.setDestination(destRoom);
		operation.setOperationNumber("A123");

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 100);
		operation.setOperationDate(cal.getTime());

		operationDataService.save(operation);

		ReservedTransaction tx = operation.addReserved(newItem, 25);
		tx.setCalculatedBatch(true);
		tx.setCalculatedExpiration(true);

		service.calculateReservations(operation);

		// Ensure that no new transactions were created
		Assert.assertEquals(1, operation.getReserved().size());

		Assert.assertEquals(newItem, tx.getItem());
		Assert.assertEquals(operation, tx.getBatchOperation());
	}

	@Test
	public void calculateReservations_shouldCreateSingleTransactionWhenNegativeSourceStockAndRemoving() throws Exception {
		// Create a new expirable item
		Item item = itemTest.createEntity(true);
		item.setHasExpiration(true);
		itemDataService.save(item);
		Context.flushSession();

		// Create a negative item stock (and detail) for the item in a stockroom
		Stockroom sr = stockroomDataService.getById(0);

		ItemStock stock = new ItemStock();
		stock.setItem(item);
		stock.setQuantity(-10);

		ItemStockDetail detail = new ItemStockDetail();
		detail.setStockroom(sr);
		detail.setItem(item);
		detail.setBatchOperation(null);
		detail.setCalculatedBatch(true);
		detail.setCalculatedExpiration(true);
		detail.setExpiration(null);
		detail.setQuantity(-10);

		stock.addDetail(detail);
		sr.addItem(stock);

		stockroomDataService.save(sr);
		Context.flushSession();

		// Create a new operation to deduct more stock from the stockroom
		StockOperation op = operationTest.createEntity(true);
		op.getReserved().clear();
		op.setStatus(StockOperationStatus.NEW);
		op.setInstanceType(WellKnownOperationTypes.getDistribution());
		op.setSource(sr);
		op.setOperationDate(new Date());
		op.setDepartment(item.getDepartment());
		op.addItem(item, 15);

		// Set up the reservation transactions (this is normally done in submitOperation)
		for (StockOperationItem itemStock : op.getItems()) {
			ReservedTransaction tx = new ReservedTransaction(itemStock);
			tx.setCreator(Context.getAuthenticatedUser());
			tx.setDateCreated(new Date());

			op.addReserved(tx);
		}

		// Now calculate the actual reservations
		service.calculateReservations(op);

		// Check the generated pending transactions
		Set<ReservedTransaction> transactions = op.getReserved();
		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());

		ReservedTransaction tx = Iterators.getOnlyElement(transactions.iterator());
		Assert.assertEquals(15, (long)tx.getQuantity());
		Assert.assertNull(tx.getBatchOperation());
		Assert.assertNull(tx.getExpiration());
	}

	@Test
	public void calculateReservations_shouldThrowErrorWhenGlobalPropertyEnabledAndNetSourceStockWillBeNegativeAndRemoving()
	        throws Exception {
		//Enable Global Property
		adminService.saveGlobalProperty(new GlobalProperty(RESTRICT_NEGATIVE_INVENTORY_STOCK_CREATION_FIELD,
		        "true"));

		// Create a new expirable item
		Item item = itemTest.createEntity(true);
		item.setHasExpiration(true);
		itemDataService.save(item);
		Context.flushSession();

		// Create a negative item stock (and detail) for the item in a stockroom
		Stockroom sr = stockroomDataService.getById(0);

		ItemStock stock = new ItemStock();
		stock.setItem(item);
		stock.setQuantity(10);

		ItemStockDetail detail = new ItemStockDetail();
		detail.setStockroom(sr);
		detail.setItem(item);
		detail.setBatchOperation(null);
		detail.setCalculatedBatch(true);
		detail.setCalculatedExpiration(true);
		detail.setExpiration(null);
		detail.setQuantity(10);

		stock.addDetail(detail);
		sr.addItem(stock);

		stockroomDataService.save(sr);
		Context.flushSession();

		// Create a new operation to deduct more stock from the stockroom
		StockOperation op = operationTest.createEntity(true);
		op.getReserved().clear();
		op.setStatus(StockOperationStatus.NEW);
		op.setInstanceType(WellKnownOperationTypes.getDistribution());
		op.setSource(sr);
		op.setOperationDate(new Date());
		op.setDepartment(item.getDepartment());
		op.addItem(item, 55);

		//Ensure that global property is properly set and readable
		Assert.assertTrue(isNegativeStockRestricted());

		try {

			// Set up the reservation transactions (this is normally done in submitOperation)
			for (StockOperationItem itemStock : op.getItems()) {
				ReservedTransaction tx = new ReservedTransaction(itemStock);
				tx.setCreator(Context.getAuthenticatedUser());
				tx.setDateCreated(new Date());

				op.addReserved(tx);
			}

			// Now calculate the actual reservations
			service.calculateReservations(op);

		} catch (Exception ex) {
			//Catching the expected exception
			Assert.assertNotNull(ex);
			//Ensuring that the exception is the one we want
			Assert.assertTrue(ex.getMessage().contains("Resource stockroom does not have sufficient stock."));
		}

		// Check the generated pending transactions
		Set<ReservedTransaction> transactions = op.getReserved();
		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());

		//Disable Global Property just in case
		adminService.saveGlobalProperty(new GlobalProperty(RESTRICT_NEGATIVE_INVENTORY_STOCK_CREATION_FIELD,
		        "false"));
		//Ensure that global property is properly disabled
		Assert.assertFalse(isNegativeStockRestricted());
	}

	@Test
	public void calculateReservations_shouldCreateSingleTransactionWhenNegativeSourceStockAndAdding() throws Exception {
		// Create a new expirable item
		Item item = itemTest.createEntity(true);
		item.setHasExpiration(true);
		itemDataService.save(item);
		Context.flushSession();

		// Create a negative item stock (and detail) for the item in a stockroom
		Stockroom sr = stockroomDataService.getById(0);

		ItemStock stock = new ItemStock();
		stock.setItem(item);
		stock.setQuantity(-10);

		ItemStockDetail detail = new ItemStockDetail();
		detail.setStockroom(sr);
		detail.setItem(item);
		detail.setBatchOperation(null);
		detail.setCalculatedBatch(true);
		detail.setCalculatedExpiration(true);
		detail.setExpiration(null);
		detail.setQuantity(-10);

		stock.addDetail(detail);
		sr.addItem(stock);

		stockroomDataService.save(sr);
		Context.flushSession();

		// Create a new operation to add stock to the stockroom
		StockOperation op = operationTest.createEntity(true);
		op.getReserved().clear();
		op.setStatus(StockOperationStatus.NEW);
		op.setInstanceType(WellKnownOperationTypes.getReceipt());
		op.setDestination(sr);
		op.setOperationDate(new Date());
		op.setDepartment(item.getDepartment());

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		op.addItem(item, 15, cal.getTime());

		// Set up the reservation transactions (this is normally done in submitOperation)
		for (StockOperationItem itemStock : op.getItems()) {
			ReservedTransaction tx = new ReservedTransaction(itemStock);
			tx.setCreator(Context.getAuthenticatedUser());
			tx.setDateCreated(new Date());

			op.addReserved(tx);
		}

		// Now calculate the actual reservations
		service.calculateReservations(op);

		// Check the generated pending transactions
		Set<ReservedTransaction> transactions = op.getReserved();
		Assert.assertNotNull(transactions);
		Assert.assertEquals(1, transactions.size());

		ReservedTransaction tx = Iterators.getOnlyElement(transactions.iterator());
		Assert.assertEquals(15, (long)tx.getQuantity());
		Assert.assertEquals(op, tx.getBatchOperation());
		Assert.assertTrue(DateUtils.isSameDay(cal.getTime(), tx.getExpiration()));
	}

	/**
	 * @verifies support item change to have expiration after nonexpirable stock exists
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldSupportItemChangeToHaveExpirationAfterNonexpirableStockExists()
	        throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		Item item = itemTest.createEntity(true);
		item.setHasExpiration(false);
		item.setHasPhysicalInventory(true);

		itemDataService.save(item);
		Context.flushSession();

		Stockroom sr = stockroomDataService.getById(0);

		StockOperation op = operationTest.createEntity(true);
		op.getReserved().clear();
		op.setStatus(StockOperationStatus.NEW);
		op.setInstanceType(WellKnownOperationTypes.getReceipt());
		op.setDestination(sr);
		op.addItem(item, 100);

		service.submitOperation(op);
		Context.flushSession();

		item.setHasExpiration(true);
		itemDataService.save(item);
		Context.flushSession();

		StockOperation op2 = operationTest.createEntity(true);
		op2.getReserved().clear();
		op2.setStatus(StockOperationStatus.NEW);
		op2.setInstanceType(WellKnownOperationTypes.getReceipt());
		op2.setDestination(sr);
		op2.setOperationDate(op.getOperationDate());
		op2.setOperationOrder(op.getOperationOrder());
		Date dt = new Date();
		op2.addItem(item, 200, dt);

		service.submitOperation(op2);
		Context.flushSession();

		ItemStock stock = stockroomDataService.getItem(sr, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(300, stock.getQuantity());
		Assert.assertEquals(2, stock.getDetails().size());

		ItemStockDetail detail = findDetail(stock, null);
		Assert.assertEquals(100, (long)detail.getQuantity());
		Assert.assertNull(detail.getExpiration());

		detail = findDetail(stock, dt);
		Assert.assertEquals(200, (long)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(dt, detail.getExpiration()));
	}

	/**
	 * @verifies support item change to not have expiration after expirable stock exists
	 * @see StockOperationServiceImpl#calculateReservations(org.openmrs.module.openhmis.inventory.api.model.StockOperation)
	 */
	@Test
	public void calculateReservations_shouldSupportItemChangeToNotHaveExpirationAfterExpirableStockExists()
	        throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		Item item = itemTest.createEntity(true);
		item.setHasExpiration(true);
		item.setHasPhysicalInventory(true);

		itemDataService.save(item);
		Context.flushSession();

		Stockroom sr = stockroomDataService.getById(0);

		StockOperation op = operationTest.createEntity(true);
		op.getReserved().clear();
		op.setStatus(StockOperationStatus.NEW);
		op.setInstanceType(WellKnownOperationTypes.getReceipt());
		op.setDestination(sr);
		Date dt = new Date();
		op.addItem(item, 100, dt);

		service.submitOperation(op);
		Context.flushSession();

		item.setHasExpiration(false);
		itemDataService.save(item);
		Context.flushSession();

		StockOperation op2 = operationTest.createEntity(true);
		op2.getReserved().clear();
		op2.setStatus(StockOperationStatus.NEW);
		op2.setInstanceType(WellKnownOperationTypes.getReceipt());
		op2.setDestination(sr);
		op2.setOperationDate(op.getOperationDate());
		op2.setOperationOrder(op.getOperationOrder());
		op2.addItem(item, 200);

		service.submitOperation(op2);
		Context.flushSession();

		ItemStock stock = stockroomDataService.getItem(sr, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(300, stock.getQuantity());
		Assert.assertEquals(2, stock.getDetails().size());

		ItemStockDetail detail = findDetail(stock, dt);
		Assert.assertEquals(100, (long)detail.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(dt, detail.getExpiration()));

		detail = findDetail(stock, null);
		Assert.assertEquals(200, (long)detail.getQuantity());
		Assert.assertNull(detail.getExpiration());
	}

}
