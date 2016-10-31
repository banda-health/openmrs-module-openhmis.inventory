package org.openmrs.module.openhmis.inventory.api;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataServiceTest;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;

import com.google.common.collect.Iterators;

public class IItemStockDataServiceTest extends IObjectDataServiceTest<IItemStockDataService, ItemStock> {
	private IItemDataService itemDataService;
	private IStockroomDataService stockroomDataService;
	private IStockOperationDataService stockOperationDataService;

	@Override
	public void before() throws Exception {
		super.before();

		this.itemDataService = Context.getService(IItemDataService.class);
		this.stockroomDataService = Context.getService(IStockroomDataService.class);
		this.stockOperationDataService = Context.getService(IStockOperationDataService.class);

		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(IStockroomDataServiceTest.DATASET);
	}

	@Override
	public ItemStock createEntity(boolean valid) {
		ItemStock stock = new ItemStock();

		Item item = itemDataService.getById(0);
		Stockroom stockroom = stockroomDataService.getById(0);
		StockOperation operation0 = stockOperationDataService.getById(0);
		StockOperation operation1 = stockOperationDataService.getById(1);

		if (valid) {
			stock.setItem(item);
		}

		stock.setStockroom(stockroom);
		stock.setQuantity(100);

		ItemStockDetail detail = new ItemStockDetail();
		detail.setStockroom(stockroom);
		detail.setItem(item);
		detail.setCalculatedBatch(false);
		detail.setBatchOperation(operation0);
		detail.setQuantity(50);

		stock.addDetail(detail);

		detail = new ItemStockDetail();
		detail.setStockroom(stockroom);
		detail.setItem(item);
		detail.setCalculatedBatch(false);
		detail.setBatchOperation(operation1);
		detail.setQuantity(50);

		stock.addDetail(detail);

		return stock;
	}

	@Override
	protected int getTestEntityCount() {
		return 6;
	}

	@Override
	protected void updateEntityFields(ItemStock stock) {
		stock.setItem(itemDataService.getById(1));
		stock.setStockroom(stockroomDataService.getById(1));
		stock.setQuantity(stock.getQuantity() + 100);

		// Update detail
		ItemStockDetail detail = Iterators.get(stock.getDetails().iterator(), 0);
		detail.setItem(stock.getItem());
		detail.setStockroom(stock.getStockroom());
		detail.setQuantity(detail.getQuantity() - 1);

		// Add detail
		detail = new ItemStockDetail();
		detail.setItem(stock.getItem());
		detail.setStockroom(stock.getStockroom());
		detail.setCalculatedBatch(false);
		detail.setBatchOperation(stockOperationDataService.getById(2));
		detail.setQuantity(75);

		stock.addDetail(detail);
	}

	/**
	 * @verifies return all item stock for the item ordered by stockroom name
	 * @see IItemStockDataService#getItemStockByItem(org.openmrs.module.openhmis.inventory.api.model.Item,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemStockByItem_shouldReturnAllItemStockForTheItemOrderedByStockroomName() throws Exception {
		Item item = itemDataService.getById(0);

		List<ItemStock> results = service.getItemStockByItem(item, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());

		ItemStock stock = Iterators.get(results.iterator(), 0);
		Assert.assertEquals(0, (int)stock.getStockroom().getId());

		stock = Iterators.get(results.iterator(), 1);
		Assert.assertEquals(1, (int)stock.getStockroom().getId());
	}

	/**
	 * @verifies return paged item stock when paging is specified
	 * @see IItemStockDataService#getItemStockByItem(org.openmrs.module.openhmis.inventory.api.model.Item,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemStockByItem_shouldReturnPagedItemStockWhenPagingIsSpecified() throws Exception {
		Item item = itemDataService.getById(0);

		PagingInfo pagingInfo = new PagingInfo(1, 1);
		List<ItemStock> results = service.getItemStockByItem(item, pagingInfo);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(2, (long)pagingInfo.getTotalRecordCount());

		ItemStock stock = Iterators.getOnlyElement(results.iterator());
		Assert.assertEquals(0, (int)stock.getStockroom().getId());

		pagingInfo.setPage(2);
		results = service.getItemStockByItem(item, pagingInfo);

		Assert.assertNotNull(results);
		stock = Iterators.getOnlyElement(results.iterator());
		Assert.assertEquals(1, (int)stock.getStockroom().getId());
	}

	/**
	 * @verifies return an empty list if there is no item stock
	 * @see IItemStockDataService#getItemStockByItem(org.openmrs.module.openhmis.inventory.api.model.Item,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test
	public void getItemStockByItem_shouldReturnAnEmptyListIfThereIsNoItemStock() throws Exception {
		IItemDataServiceTest itemTest = new IItemDataServiceTest();
		Item item = itemTest.createEntity(true);

		itemDataService.save(item);
		Context.flushSession();

		List<ItemStock> results = service.getItemStockByItem(item, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies throw IllegalArgumentException if item is null
	 * @see IItemStockDataService#getItemStockByItem(org.openmrs.module.openhmis.inventory.api.model.Item,
	 *      org.openmrs.module.openhmis.commons.api.PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItemStockByItem_shouldThrowIllegalArgumentExceptionIfItemIsNull() throws Exception {
		service.getItemStockByItem(null, null);
	}
}
