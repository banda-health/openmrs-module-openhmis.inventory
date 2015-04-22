package org.openmrs.module.openhmis.inventory.api.impl;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
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
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class DisposeOperationTest extends BaseModuleContextSensitiveTest {

	IItemDataService itemDataService;
	IStockroomDataService stockroomDataService;
	IItemStockDataService itemStockDataService;
	IStockOperationDataService operationDataService;
	ITestableStockOperationService service;

	IItemDataServiceTest itemTest;
	IStockOperationDataServiceTest operationTest;
	IStockroomDataServiceTest stockroomTest;
	IItemStockDataServiceTest itemStockTest;

	StockOperationServiceImplTest stockOperationServiceImplTest;

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

		itemTest = new IItemDataServiceTest();
		operationTest = new IStockOperationDataServiceTest();
		stockroomTest = new IStockroomDataServiceTest();
		itemStockTest = new IItemStockDataServiceTest();
		stockOperationServiceImplTest = new StockOperationServiceImplTest();
	}

	@Test
	public void submitOperation_dispose_shouldReduceBatchQuantity_singleBatch_noExpirationDate() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);

		Item item = itemTest.createEntity(true);
		itemDataService.save(item);

		ItemStock stock = new ItemStock();
		stock.setItem(item);

		stock.setStockroom(sourceRoom);
		stock.setQuantity(10);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail = new ItemStockDetail();
		detail.setItem(item);
		detail.setStockroom(sourceRoom);
		detail.setQuantity(10);
		detail.setCalculatedBatch(false);
		detail.setCalculatedExpiration(false);
		detail.setBatchOperation(operationDataService.getById(2));

		stock.addDetail(detail);

		itemStockDataService.save(stock);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getDisposed());
		operation.setStatus(StockOperationStatus.NEW);
		operation.setSource(sourceRoom);
		operation.setOperationNumber("A122");
		operation.setOperationDate(new Date());
		operation.addItem(item, 6);

		detail = stockOperationServiceImplTest.findDetail(stock, null);
		Assert.assertNotNull(detail);
		Assert.assertEquals(new Integer(10), detail.getQuantity());
		Assert.assertEquals(operationDataService.getById(2), detail.getBatchOperation());

		service.submitOperation(operation);
		Context.flushSession();

		stock = stockroomDataService.getItem(sourceRoom, item);
		ItemStockDetail detailAfterSubmit = stockOperationServiceImplTest.findDetail(stock, null);
		Assert.assertNotNull(detailAfterSubmit);
		Assert.assertEquals(new Integer(4), detailAfterSubmit.getQuantity());
	}

	@Test
	public void submitOperation_dispose_shouldRemoveBatchWithZeroQuantity_singleBatch_noExpirationDate() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);

		Item item = itemTest.createEntity(true);
		itemDataService.save(item);

		ItemStock stock = new ItemStock();
		stock.setItem(item);

		stock.setStockroom(sourceRoom);
		stock.setQuantity(10);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail = new ItemStockDetail();
		detail.setItem(item);
		detail.setStockroom(sourceRoom);
		detail.setQuantity(10);
		detail.setCalculatedBatch(false);
		detail.setCalculatedExpiration(false);
		detail.setBatchOperation(operationDataService.getById(2));

		stock.addDetail(detail);

		itemStockDataService.save(stock);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getDisposed());
		operation.setStatus(StockOperationStatus.NEW);
		operation.setSource(sourceRoom);
		operation.setOperationNumber("A122");
		operation.setOperationDate(new Date());
		operation.addItem(item, 10);

		detail = stockOperationServiceImplTest.findDetail(stock, null);
		Assert.assertNotNull(detail);
		Assert.assertEquals(new Integer(10), detail.getQuantity());
		Assert.assertEquals(operationDataService.getById(2), detail.getBatchOperation());

		service.submitOperation(operation);
		Context.flushSession();

		stock = stockroomDataService.getItem(sourceRoom, item);
		ItemStockDetail detailAfterSubmit = stockOperationServiceImplTest.findDetail(stock, null);
		Assert.assertNull(detailAfterSubmit);
	}

	@Test
	public void submitOperation_dispose_shouldReduceFromNextBatchIfQuantityOfOneBatchIsNotEnough_multipleBatches_noExpirationDate() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);

		Item item = itemTest.createEntity(true);
		itemDataService.save(item);

		ItemStock stock = new ItemStock();
		stock.setItem(item);

		stock.setStockroom(sourceRoom);
		stock.setQuantity(30);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(item);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));

		ItemStockDetail detail2 = new ItemStockDetail();
		detail2.setItem(item);
		detail2.setStockroom(sourceRoom);
		detail2.setQuantity(20);
		detail2.setCalculatedBatch(false);
		detail2.setCalculatedExpiration(false);
		detail2.setBatchOperation(operationDataService.getById(1));

		stock.addDetail(detail1);
		stock.addDetail(detail2);

		itemStockDataService.save(stock);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getDisposed());
		operation.setStatus(StockOperationStatus.NEW);
		operation.setSource(sourceRoom);
		operation.setOperationNumber("A122");
		operation.setOperationDate(new Date());
		operation.addItem(item, 25);

		Collection<ItemStockDetail> findDetails = stockOperationServiceImplTest.findDetails(stock, null);
		Assert.assertNotNull(findDetails);
		Assert.assertEquals(2, findDetails.size());

		service.submitOperation(operation);
		Context.flushSession();

		Collection<ItemStockDetail> detailsAfterSubmit = stockOperationServiceImplTest.findDetails(stock, null);
		Assert.assertNotNull(detailsAfterSubmit);
		Assert.assertEquals(1, detailsAfterSubmit.size());
		stock = stockroomDataService.getItem(sourceRoom, item);
		ItemStockDetail detailAfterSubmit = stockOperationServiceImplTest.findDetail(stock, null);
		Assert.assertEquals(new Integer(5), detailAfterSubmit.getQuantity());
	}

	@Test
	public void submitOperation_dispose_shouldRemoveMultipleBatchesIfBatchQuantityIsZero_multipleBatches_noExpirationDate() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);

		Item item = itemTest.createEntity(true);
		itemDataService.save(item);

		ItemStock stock = new ItemStock();
		stock.setItem(item);

		stock.setStockroom(sourceRoom);
		stock.setQuantity(30);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(item);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));

		ItemStockDetail detail2 = new ItemStockDetail();
		detail2.setItem(item);
		detail2.setStockroom(sourceRoom);
		detail2.setQuantity(20);
		detail2.setCalculatedBatch(false);
		detail2.setCalculatedExpiration(false);
		detail2.setBatchOperation(operationDataService.getById(1));

		stock.addDetail(detail1);
		stock.addDetail(detail2);

		itemStockDataService.save(stock);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getDisposed());
		operation.setStatus(StockOperationStatus.NEW);
		operation.setSource(sourceRoom);
		operation.setOperationNumber("A122");
		operation.setOperationDate(new Date());
		operation.addItem(item, 30);

		Collection<ItemStockDetail> details = stockOperationServiceImplTest.findDetails(stock, null);
		Assert.assertNotNull(details);
		Assert.assertEquals(2, details.size());

		service.submitOperation(operation);
		Context.flushSession();

		Collection<ItemStockDetail> detailsAfterSubmit = stockOperationServiceImplTest.findDetails(stock, null);
		Assert.assertNull(detailsAfterSubmit);
	}

	@Test
	public void submitOperation_dispose_shouldCreateNewBatchWithNegativeQuantityIfDisposedQuantityIsMoreThanBatchHas_singleBatch_specificExpirationDate() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);

		Item item = itemTest.createEntity(true);
		itemDataService.save(item);

		ItemStock stock = new ItemStock();
		stock.setItem(item);

		stock.setStockroom(sourceRoom);
		stock.setQuantity(10);

		//specify expiration date
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		String dateInString = "31-08-2016";
		Date expirationDate = sdf.parse(dateInString);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail = new ItemStockDetail();
		detail.setItem(item);
		detail.setStockroom(sourceRoom);
		detail.setQuantity(10);
		detail.setCalculatedBatch(false);
		detail.setCalculatedExpiration(false);
		detail.setBatchOperation(operationDataService.getById(2));
		detail.setExpiration(expirationDate);

		stock.addDetail(detail);

		itemStockDataService.save(stock);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getDisposed());
		operation.setStatus(StockOperationStatus.NEW);
		operation.setSource(sourceRoom);
		operation.setOperationNumber("A122");
		operation.setOperationDate(new Date());
		operation.addItem(item, 60, expirationDate);

		detail = stockOperationServiceImplTest.findDetail(stock, expirationDate);
		Assert.assertNotNull(detail);
		Assert.assertEquals(new Integer(10), detail.getQuantity());
		Assert.assertEquals(operationDataService.getById(2), detail.getBatchOperation());
		Assert.assertEquals(expirationDate, detail.getExpiration());

		service.submitOperation(operation);
		Context.flushSession();

		stock = stockroomDataService.getItem(sourceRoom, item);
		Assert.assertEquals(-50, stock.getQuantity());
		Assert.assertEquals(1, stock.getDetails().size());

		Collection<ItemStockDetail> detailsAfterSubmit = stockOperationServiceImplTest.findDetails(stock, expirationDate);
		Assert.assertNull(detailsAfterSubmit);

		detailsAfterSubmit = stockOperationServiceImplTest.findDetails(stock, null);
		Assert.assertNotNull(detailsAfterSubmit);
		Assert.assertEquals(1, detailsAfterSubmit.size());

		ItemStockDetail detailAfterSubmit = stockOperationServiceImplTest.findDetail(stock, null);
		Assert.assertNotNull(detailAfterSubmit);
		Assert.assertEquals(new Integer(-50), detailAfterSubmit.getQuantity());
		Assert.assertNull(detailAfterSubmit.getBatchOperation());
		Assert.assertNull(detailAfterSubmit.getExpiration());
	}

	@Test
	public void submitOperation_dispose_shouldCreateBatchWithoutNumberAndExpIfTooMuchIsDeductedAndStockQuantityResultZero_multipleBatches_specificExpirationDate() throws Exception {
		Stockroom sourceRoom = stockroomDataService.getById(0);

		Item item = itemTest.createEntity(true);
		itemDataService.save(item);

		ItemStock stock = new ItemStock();
		stock.setItem(item);

		stock.setStockroom(sourceRoom);
		stock.setQuantity(30);

		//specify expiration date
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		String dateInString1 = "31-08-2016";
		Date expirationDate1 = sdf.parse(dateInString1);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(item);
		detail1.setStockroom(sourceRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));
		detail1.setExpiration(expirationDate1);

		//specify different expiration date
		String dateInString2 = "31-09-2016";
		Date expirationDate2 = sdf.parse(dateInString2);

		ItemStockDetail detail2 = new ItemStockDetail();
		detail2.setItem(item);
		detail2.setStockroom(sourceRoom);
		detail2.setQuantity(20);
		detail2.setCalculatedBatch(false);
		detail2.setCalculatedExpiration(false);
		detail2.setBatchOperation(operationDataService.getById(1));
		detail2.setExpiration(expirationDate2);

		stock.addDetail(detail1);
		stock.addDetail(detail2);

		itemStockDataService.save(stock);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getDisposed());
		operation.setStatus(StockOperationStatus.NEW);
		operation.setSource(sourceRoom);
		operation.setOperationNumber("A122");
		operation.setOperationDate(new Date());
		operation.addItem(item, 30, expirationDate1);

		Collection<ItemStockDetail> details = stockOperationServiceImplTest.findDetails(stock, expirationDate1);
		Assert.assertNotNull(details);
		Assert.assertEquals(1, details.size());

		details = stockOperationServiceImplTest.findDetails(stock, expirationDate2);
		Assert.assertNotNull(details);
		Assert.assertEquals(1, details.size());

		details = stockOperationServiceImplTest.findDetails(stock, null);
		Assert.assertNull(details);

		Assert.assertEquals(2, stock.getDetails().size());

		service.submitOperation(operation);
		Context.flushSession();

		stock = stockroomDataService.getItem(sourceRoom, item);
		Assert.assertNotNull(stock);
		Assert.assertEquals(0, stock.getQuantity());
		Assert.assertEquals(2, stock.getDetails().size());

		Collection<ItemStockDetail> detailsAfterSubmit = stockOperationServiceImplTest.findDetails(stock, expirationDate2);
		Assert.assertNotNull(detailsAfterSubmit);
		Assert.assertEquals(1, detailsAfterSubmit.size());

		ItemStockDetail detail = stockOperationServiceImplTest.findDetail(stock, expirationDate2);
		Assert.assertNotNull(detail);
		Assert.assertEquals(new Integer(20), detail.getQuantity());

		detailsAfterSubmit = stockOperationServiceImplTest.findDetails(stock, null);
		Assert.assertNotNull(detailsAfterSubmit);
		Assert.assertEquals(1, detailsAfterSubmit.size());

		detail = stockOperationServiceImplTest.findDetail(stock, null);
		Assert.assertNotNull(detail);
		Assert.assertEquals(new Integer(-20), detail.getQuantity());

		detailsAfterSubmit = stockOperationServiceImplTest.findDetails(stock, expirationDate1);
		Assert.assertNull(detailsAfterSubmit);
	}
}
