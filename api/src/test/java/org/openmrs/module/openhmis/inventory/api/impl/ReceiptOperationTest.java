package org.openmrs.module.openhmis.inventory.api.impl;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.ModuleSettings;
import org.openmrs.module.openhmis.inventory.api.WellKnownOperationTypes;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.Settings;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;

public class ReceiptOperationTest extends BaseOperationTest {
	@Test
	public void submitOperation_shouldAddNewBatchIfStockAdded_multipleBatch_noExpirationDate() throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		Stockroom destinationRoom = stockroomDataService.getById(0);

		Item item = itemTest.createEntity(true);
		item.setHasExpiration(false);
		item.setHasPhysicalInventory(true);

		itemDataService.save(item);

		ItemStock stock = new ItemStock();
		stock.setItem(item);

		stock.setStockroom(destinationRoom);
		stock.setQuantity(30);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(item);
		detail1.setStockroom(destinationRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));

		ItemStockDetail detail2 = new ItemStockDetail();
		detail2.setItem(item);
		detail2.setStockroom(destinationRoom);
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
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.NEW);
		operation.setDestination(destinationRoom);
		operation.setOperationNumber("A122");
		operation.setOperationOrder(new Integer(1));
		operation.setOperationDate(new Date());
		operation.addItem(item, 30);

		Collection<ItemStockDetail> details = stockOperationServiceImplTest.findDetails(stock, null);
		Assert.assertNotNull(details);
		Assert.assertEquals(2, details.size());

		Assert.assertEquals(2, stock.getDetails().size());

		Assert.assertEquals(StockOperationStatus.NEW, operation.getStatus());

		service.submitOperation(operation);
		Context.flushSession();

		Assert.assertEquals(StockOperationStatus.COMPLETED, operation.getStatus());

		stock = stockroomDataService.getItem(destinationRoom, item);
		Assert.assertNotNull(stock);
		Assert.assertNotNull(stock.getDetails());
		Assert.assertEquals(3, stock.getDetails().size());
		Assert.assertEquals(60, stock.getQuantity());

		Collection<ItemStockDetail> detailsAfterSubmit = stockOperationServiceImplTest.findDetails(stock, null);
		Assert.assertNotNull(detailsAfterSubmit);
		Assert.assertEquals(3, detailsAfterSubmit.size());

		Assert.assertEquals(1, operation.getTransactions().size());
	}

	@Test
	public void submitOperation_shouldAddNewBatchIfStockAdded_multipleBatch_specificExpirationDate() throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		Stockroom destinationRoom = stockroomDataService.getById(0);

		Item item = itemTest.createEntity(true);
		item.setHasExpiration(false);
		item.setHasPhysicalInventory(true);
		itemDataService.save(item);

		ItemStock stock = new ItemStock();
		stock.setItem(item);

		stock.setStockroom(destinationRoom);
		stock.setQuantity(30);

		//specify expiration date
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		String dateInString = "31-08-2016";
		Date expirationDate = sdf.parse(dateInString);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail1 = new ItemStockDetail();
		detail1.setItem(item);
		detail1.setStockroom(destinationRoom);
		detail1.setQuantity(10);
		detail1.setCalculatedBatch(false);
		detail1.setCalculatedExpiration(false);
		detail1.setBatchOperation(operationDataService.getById(2));
		detail1.setExpiration(expirationDate);

		ItemStockDetail detail2 = new ItemStockDetail();
		detail2.setItem(item);
		detail2.setStockroom(destinationRoom);
		detail2.setQuantity(20);
		detail2.setCalculatedBatch(false);
		detail2.setCalculatedExpiration(false);
		detail2.setBatchOperation(operationDataService.getById(1));
		detail2.setExpiration(expirationDate);

		stock.addDetail(detail1);
		stock.addDetail(detail2);

		itemStockDataService.save(stock);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.NEW);
		operation.setDestination(destinationRoom);
		operation.setOperationNumber("A122");
		operation.setOperationDate(new Date());
		operation.addItem(item, 30, expirationDate);

		Collection<ItemStockDetail> details = stockOperationServiceImplTest.findDetails(stock, expirationDate);
		Assert.assertNotNull(details);
		Assert.assertEquals(2, details.size());

		Assert.assertEquals(StockOperationStatus.NEW, operation.getStatus());

		service.submitOperation(operation);
		Context.flushSession();

		Assert.assertEquals(StockOperationStatus.COMPLETED, operation.getStatus());

		stock = stockroomDataService.getItem(destinationRoom, item);
		Assert.assertEquals(60, stock.getQuantity());

		Collection<ItemStockDetail> detailsAfterSubmit = stockOperationServiceImplTest.findDetails(stock, expirationDate);
		Assert.assertNotNull(detailsAfterSubmit);
		Assert.assertEquals(3, detailsAfterSubmit.size());

		Assert.assertEquals(1, operation.getTransactions().size());
	}

	@Test(expected = APIException.class)
	public void submitOperation_shouldThrowExceptionfDisposedQuantityIsNegative_singleBatch_noExpirationDate()
	        throws Exception {
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
		detail.setBatchOperation(operationDataService.getById(1));
		detail.setCalculatedBatch(false);
		detail.setCalculatedExpiration(false);

		stock.addDetail(detail);

		itemStockDataService.save(stock);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.NEW);
		operation.setSource(sourceRoom);
		operation.setOperationNumber("A122");
		operation.setOperationDate(new Date());
		operation.addItem(item, -6);

		detail = stockOperationServiceImplTest.findDetail(stock, null);
		Assert.assertNotNull(detail);
		Assert.assertEquals(new Integer(10), detail.getQuantity());

		Assert.assertEquals(StockOperationStatus.NEW, operation.getStatus());

		service.submitOperation(operation);
	}

	@Test
	public void submitOperation_shouldUpdateBatchIfStockAddedToNegtiveQuantityBatch_singleBatch_noExpirationDate()
	        throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		Stockroom destinationRoom = stockroomDataService.getById(0);

		Item item = itemTest.createEntity(true);
		item.setHasExpiration(false);
		item.setHasPhysicalInventory(true);
		itemDataService.save(item);

		ItemStock stock = new ItemStock();
		stock.setItem(item);

		stock.setStockroom(destinationRoom);
		stock.setQuantity(-10);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail = new ItemStockDetail();
		detail.setItem(item);
		detail.setStockroom(destinationRoom);
		detail.setQuantity(-10);
		detail.setCalculatedBatch(false);
		detail.setCalculatedExpiration(false);
		detail.setBatchOperation(null);

		stock.addDetail(detail);

		itemStockDataService.save(stock);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.NEW);
		operation.setDestination(destinationRoom);
		operation.setOperationNumber("A122");
		operation.setOperationDate(new Date());
		operation.addItem(item, 30);

		Collection<ItemStockDetail> findDetails = stockOperationServiceImplTest.findDetails(stock, null);
		Assert.assertNotNull(findDetails);
		Assert.assertEquals(1, findDetails.size());
		detail = stockOperationServiceImplTest.findDetail(stock, null);
		Assert.assertEquals(new Integer(-10), detail.getQuantity());
		Assert.assertNull(detail.getBatchOperation());
		Assert.assertNull(detail.getExpiration());

		Assert.assertEquals(StockOperationStatus.NEW, operation.getStatus());

		service.submitOperation(operation);
		Context.flushSession();

		Assert.assertEquals(StockOperationStatus.COMPLETED, operation.getStatus());

		stock = stockroomDataService.getItem(destinationRoom, item);
		Assert.assertEquals(20, stock.getQuantity());

		Collection<ItemStockDetail> detailsAfterSubmit = stockOperationServiceImplTest.findDetails(stock, null);
		Assert.assertNotNull(detailsAfterSubmit);
		Assert.assertEquals(1, findDetails.size());
		detail = stockOperationServiceImplTest.findDetail(stock, null);
		Assert.assertEquals("A122", detail.getBatchOperation().getOperationNumber());
		Assert.assertNull(detail.getExpiration());

		Assert.assertEquals(1, operation.getTransactions().size());
	}

	@Test
	public void submitOperation_shouldUpdateBatchIfStockAddedToNegtiveQuantityBatch_singleBatch_specificExpirationDate()
	        throws Exception {
		Settings settings = ModuleSettings.loadSettings();
		settings.setAutoCompleteOperations(true);
		ModuleSettings.saveSettings(settings);

		Stockroom destinationRoom = stockroomDataService.getById(0);

		Item item = itemTest.createEntity(true);
		item.setHasExpiration(false);
		item.setHasPhysicalInventory(true);
		itemDataService.save(item);

		ItemStock stock = new ItemStock();
		stock.setItem(item);

		stock.setStockroom(destinationRoom);
		stock.setQuantity(-10);

		//specify expiration date
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		String dateInString = "31-08-2016";
		Date expirationDate = sdf.parse(dateInString);

		// Add some item stock with different qualifiers to the source room
		ItemStockDetail detail = new ItemStockDetail();
		detail.setItem(item);
		detail.setStockroom(destinationRoom);
		detail.setQuantity(-10);
		detail.setCalculatedBatch(false);
		detail.setCalculatedExpiration(false);
		detail.setBatchOperation(null);

		stock.addDetail(detail);

		itemStockDataService.save(stock);
		Context.flushSession();

		// Create the stock operation
		StockOperation operation = new StockOperation();
		operation.setInstanceType(WellKnownOperationTypes.getReceipt());
		operation.setStatus(StockOperationStatus.NEW);
		operation.setDestination(destinationRoom);
		operation.setOperationNumber("A122");
		operation.setOperationDate(new Date());
		operation.addItem(item, 30, expirationDate);

		Collection<ItemStockDetail> details = stockOperationServiceImplTest.findDetails(stock, null);
		Assert.assertNotNull(details);
		Assert.assertEquals(1, details.size());
		detail = stockOperationServiceImplTest.findDetail(stock, null);
		Assert.assertNotNull(detail);
		Assert.assertEquals(new Integer(-10), detail.getQuantity());
		Assert.assertNull(detail.getBatchOperation());
		Assert.assertNull(detail.getExpiration());

		Assert.assertEquals(StockOperationStatus.NEW, operation.getStatus());

		service.submitOperation(operation);
		Context.flushSession();

		stock = stockroomDataService.getItem(destinationRoom, item);
		Assert.assertEquals(20, stock.getQuantity());

		Collection<ItemStockDetail> detailsAfterSubmit = stockOperationServiceImplTest.findDetails(stock, expirationDate);
		Assert.assertNotNull(detailsAfterSubmit);
		Assert.assertEquals(1, detailsAfterSubmit.size());
		ItemStockDetail detailAfterSubmit = stockOperationServiceImplTest.findDetail(stock, expirationDate);
		Assert.assertEquals("A122", detailAfterSubmit.getBatchOperation().getOperationNumber());
		Assert.assertEquals(new Integer(20), detailAfterSubmit.getQuantity());
		Assert.assertEquals(expirationDate, detailAfterSubmit.getExpiration());

		Assert.assertEquals(1, operation.getTransactions().size());
	}

}
