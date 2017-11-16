/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.api;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.BaseModuleContextTest;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockSummary;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;

public class IItemStockDetailDataServiceTest extends BaseModuleContextTest {
	private IItemStockDetailDataService service;
	private IItemDataService itemDataService;
	private IStockroomDataService stockroomDataService;
	private IStockOperationDataService stockOperationDataService;

	@Before
	public void before() throws Exception {
		this.service = Context.getService(IItemStockDetailDataService.class);
		this.itemDataService = Context.getService(IItemDataService.class);
		this.stockroomDataService = Context.getService(IStockroomDataService.class);
		this.stockOperationDataService = Context.getService(IStockOperationDataService.class);

		executeDataSet(TestConstants.CORE_DATASET);
		executeDataSet(IItemDataServiceTest.ITEM_DATASET);
		executeDataSet(IStockroomDataServiceTest.DATASET);
	}

	/**
	 * @verifies throw IllegalArgumentException if the stockroom is null
	 * @see IItemStockDetailDataService#getItemStockSummaryByStockroom(Stockroom, PagingInfo)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItemStockSummaryByStockroom_shouldThrowIllegalArgumentExceptionIfTheStockroomIsNull() throws Exception {
		PagingInfo pagingInfo = new PagingInfo(1, 1);
		service.getItemStockSummaryByStockroom(null, pagingInfo);
	}

	/**
	 * @verifies return an empty list if no records are found
	 * @see IItemStockDetailDataService#getItemStockSummaryByStockroom(Stockroom, PagingInfo)
	 */
	@Test
	public void getItemStockSummaryByStockroom_shouldReturnAnEmptyListIfNoRecordsAreFound() throws Exception {
		Stockroom newStockroom = new Stockroom();
		newStockroom.setName("new");

		stockroomDataService.save(newStockroom);
		Context.flushSession();

		List<ItemStockSummary> results = service.getItemStockSummaryByStockroom(newStockroom, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies return the item stock summary records
	 * @see IItemStockDetailDataService#getItemStockSummaryByStockroom(Stockroom, PagingInfo)
	 */
	@Test
	public void getItemStockSummaryByStockroom_shouldReturnTheItemStockSummaryRecords() throws Exception {
		Stockroom sr = new Stockroom();
		sr.setName("new");

		stockroomDataService.save(sr);
		Context.flushSession();

		Item item2 = itemDataService.getById(2);

		ItemStock stock = new ItemStock();
		stock.setItem(item2);
		stock.setStockroom(sr);
		stock.setQuantity(100);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 2);

		ItemStockDetail detail = new ItemStockDetail();
		detail.setItem(item2);
		detail.setExpiration(cal.getTime());
		detail.setBatchOperation(stockOperationDataService.getById(0));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(true);
		detail.setCalculatedBatch(true);
		detail.setQuantity(20);

		stock.addDetail(detail);

		detail = new ItemStockDetail();
		detail.setItem(item2);
		detail.setExpiration(cal.getTime());
		detail.setBatchOperation(stockOperationDataService.getById(1));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(true);
		detail.setCalculatedBatch(true);
		detail.setQuantity(20);

		stock.addDetail(detail);

		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.YEAR, 3);
		detail = new ItemStockDetail();
		detail.setItem(item2);
		detail.setExpiration(cal2.getTime());
		detail.setBatchOperation(stockOperationDataService.getById(0));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(true);
		detail.setCalculatedBatch(true);
		detail.setQuantity(60);

		stock.addDetail(detail);

		sr.addItem(stock);

		Item item0 = itemDataService.getById(0);

		stock = new ItemStock();
		stock.setItem(item0);
		stock.setStockroom(sr);
		stock.setQuantity(50);

		detail = new ItemStockDetail();
		detail.setItem(item0);
		detail.setExpiration(null);
		detail.setBatchOperation(stockOperationDataService.getById(0));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(null);
		detail.setCalculatedBatch(true);
		detail.setQuantity(20);

		stock.addDetail(detail);

		detail = new ItemStockDetail();
		detail.setItem(item0);
		detail.setExpiration(null);
		detail.setBatchOperation(stockOperationDataService.getById(1));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(null);
		detail.setCalculatedBatch(true);
		detail.setQuantity(30);

		stock.addDetail(detail);

		sr.addItem(stock);

		stockroomDataService.save(sr);
		Context.flushSession();

		List<ItemStockSummary> results = service.getItemStockSummaryByStockroom(sr, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		ItemStockSummary summary = results.get(0);
		Assert.assertEquals(item0, summary.getItem());
		Assert.assertEquals(50, (long)summary.getQuantity());
		Assert.assertNull(summary.getExpiration());

		summary = results.get(1);
		Assert.assertEquals(item2, summary.getItem());
		Assert.assertEquals(40, (long)summary.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(cal.getTime(), summary.getExpiration()));

		summary = results.get(2);
		Assert.assertEquals(item2, summary.getItem());
		Assert.assertEquals(60, (long)summary.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(cal2.getTime(), summary.getExpiration()));
	}

	/**
	 * @verifies return paged results if paging is specified
	 * @see IItemStockDetailDataService#getItemStockSummaryByStockroom(Stockroom, PagingInfo)
	 */
	@Test
	public void getItemStockSummaryByStockroom_shouldReturnPagedResultsIfPagingIsSpecified() throws Exception {
		Stockroom sr = new Stockroom();
		sr.setName("new");

		stockroomDataService.save(sr);
		Context.flushSession();

		Item item2 = itemDataService.getById(2);

		ItemStock stock = new ItemStock();
		stock.setItem(item2);
		stock.setStockroom(sr);
		stock.setQuantity(100);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 2);

		ItemStockDetail detail = new ItemStockDetail();
		detail.setItem(item2);
		detail.setExpiration(cal.getTime());
		detail.setBatchOperation(stockOperationDataService.getById(0));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(true);
		detail.setCalculatedBatch(true);
		detail.setQuantity(20);

		stock.addDetail(detail);

		detail = new ItemStockDetail();
		detail.setItem(item2);
		detail.setExpiration(cal.getTime());
		detail.setBatchOperation(stockOperationDataService.getById(1));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(true);
		detail.setCalculatedBatch(true);
		detail.setQuantity(20);

		stock.addDetail(detail);

		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.YEAR, 3);
		detail = new ItemStockDetail();
		detail.setItem(item2);
		detail.setExpiration(cal2.getTime());
		detail.setBatchOperation(stockOperationDataService.getById(0));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(true);
		detail.setCalculatedBatch(true);
		detail.setQuantity(60);

		stock.addDetail(detail);

		sr.addItem(stock);

		Item item0 = itemDataService.getById(0);

		stock = new ItemStock();
		stock.setItem(item0);
		stock.setStockroom(sr);
		stock.setQuantity(50);

		detail = new ItemStockDetail();
		detail.setItem(item0);
		detail.setExpiration(null);
		detail.setBatchOperation(stockOperationDataService.getById(0));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(null);
		detail.setCalculatedBatch(true);
		detail.setQuantity(20);

		stock.addDetail(detail);

		detail = new ItemStockDetail();
		detail.setItem(item0);
		detail.setExpiration(null);
		detail.setBatchOperation(stockOperationDataService.getById(1));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(null);
		detail.setCalculatedBatch(true);
		detail.setQuantity(30);

		stock.addDetail(detail);

		sr.addItem(stock);

		stockroomDataService.save(sr);
		Context.flushSession();

		PagingInfo pagingInfo = new PagingInfo(1, 1);
		List<ItemStockSummary> results = service.getItemStockSummaryByStockroom(sr, pagingInfo);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(3, (long)pagingInfo.getTotalRecordCount());

		ItemStockSummary summary = results.get(0);
		Assert.assertEquals(item0, summary.getItem());
		Assert.assertEquals(50, (long)summary.getQuantity());
		Assert.assertNull(summary.getExpiration());

		pagingInfo.setPage(2);
		results = service.getItemStockSummaryByStockroom(sr, pagingInfo);

		summary = results.get(0);
		Assert.assertEquals(item2, summary.getItem());
		Assert.assertEquals(40, (long)summary.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(cal.getTime(), summary.getExpiration()));

		pagingInfo.setPage(3);
		results = service.getItemStockSummaryByStockroom(sr, pagingInfo);

		summary = results.get(0);
		Assert.assertEquals(item2, summary.getItem());
		Assert.assertEquals(60, (long)summary.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(cal2.getTime(), summary.getExpiration()));
	}

	/**
	 * @verifies return correctly paged results when aggregate qty is zero
	 * @see IItemStockDetailDataService#getItemStockSummaryByStockroom(Stockroom, PagingInfo)
	 */
	@Test
	public void getItemStockSummaryByStockroom_shouldReturnCorrectlyPagedResultsWhenAggregateQtyIsZero() throws Exception {
		Stockroom sr = new Stockroom();
		sr.setName("new");

		stockroomDataService.save(sr);
		Context.flushSession();

		// Set up the stock so that exactly 5 item stock records should be returned
		Item item2 = itemDataService.getById(2);

		ItemStock stock = new ItemStock();
		stock.setItem(item2);
		stock.setStockroom(sr);
		stock.setQuantity(100);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 2);

		ItemStockDetail detail = new ItemStockDetail();
		detail.setItem(item2);
		detail.setExpiration(cal.getTime());
		detail.setBatchOperation(stockOperationDataService.getById(0));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(true);
		detail.setCalculatedBatch(true);
		detail.setQuantity(20);

		stock.addDetail(detail);

		detail = new ItemStockDetail();
		detail.setItem(item2);
		detail.setExpiration(cal.getTime());
		detail.setBatchOperation(stockOperationDataService.getById(1));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(true);
		detail.setCalculatedBatch(true);
		detail.setQuantity(20);

		stock.addDetail(detail);

		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.YEAR, 3);
		detail = new ItemStockDetail();
		detail.setItem(item2);
		detail.setExpiration(cal2.getTime());
		detail.setBatchOperation(stockOperationDataService.getById(0));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(true);
		detail.setCalculatedBatch(true);
		detail.setQuantity(60);

		stock.addDetail(detail);

		// These next two stock details will cancel themselves out and should not appear in the results but they also
		// should not be returned from the db
		detail = new ItemStockDetail();
		detail.setItem(item2);
		detail.setExpiration(null);
		detail.setBatchOperation(null);
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(true);
		detail.setCalculatedBatch(true);
		detail.setQuantity(-10);

		stock.addDetail(detail);

		detail = new ItemStockDetail();
		detail.setItem(item2);
		detail.setExpiration(null);
		detail.setBatchOperation(stockOperationDataService.getById(0));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(false);
		detail.setCalculatedBatch(true);
		detail.setQuantity(10);

		stock.addDetail(detail);

		sr.addItem(stock);

		Item item0 = itemDataService.getById(0);

		stock = new ItemStock();
		stock.setItem(item0);
		stock.setStockroom(sr);
		stock.setQuantity(50);

		detail = new ItemStockDetail();
		detail.setItem(item0);
		detail.setExpiration(null);
		detail.setBatchOperation(stockOperationDataService.getById(0));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(null);
		detail.setCalculatedBatch(true);
		detail.setQuantity(50);

		stock.addDetail(detail);

		sr.addItem(stock);

		Item item6 = itemDataService.getById(6);

		stock = new ItemStock();
		stock.setItem(item6);
		stock.setStockroom(sr);
		stock.setQuantity(50);

		Calendar cal3 = Calendar.getInstance();
		cal3.add(Calendar.YEAR, 4);

		detail = new ItemStockDetail();
		detail.setItem(item6);
		detail.setExpiration(cal3.getTime());
		detail.setBatchOperation(stockOperationDataService.getById(0));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(null);
		detail.setCalculatedBatch(true);
		detail.setQuantity(30);

		stock.addDetail(detail);

		Calendar cal4 = Calendar.getInstance();
		cal4.add(Calendar.YEAR, 5);

		detail = new ItemStockDetail();
		detail.setItem(item6);
		detail.setExpiration(cal4.getTime());
		detail.setBatchOperation(stockOperationDataService.getById(0));
		detail.setStockroom(sr);
		detail.setCalculatedExpiration(null);
		detail.setCalculatedBatch(true);
		detail.setQuantity(20);

		stock.addDetail(detail);

		sr.addItem(stock);

		stockroomDataService.save(sr);
		Context.flushSession();

		PagingInfo pagingInfo = new PagingInfo(1, 5);
		List<ItemStockSummary> results = service.getItemStockSummaryByStockroom(sr, pagingInfo);

		Assert.assertNotNull(results);
		Assert.assertEquals(5, results.size());
		Assert.assertEquals(5, (long)pagingInfo.getTotalRecordCount());

		ItemStockSummary summary = results.get(0);
		Assert.assertEquals(item0, summary.getItem());
		Assert.assertEquals(50, (long)summary.getQuantity());
		Assert.assertNull(summary.getExpiration());

		summary = results.get(1);
		Assert.assertEquals(item2, summary.getItem());
		Assert.assertEquals(40, (long)summary.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(cal.getTime(), summary.getExpiration()));

		summary = results.get(2);
		Assert.assertEquals(item2, summary.getItem());
		Assert.assertEquals(60, (long)summary.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(cal2.getTime(), summary.getExpiration()));

		summary = results.get(3);
		Assert.assertEquals(item6, summary.getItem());
		Assert.assertEquals(30, (long)summary.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(cal3.getTime(), summary.getExpiration()));

		summary = results.get(4);
		Assert.assertEquals(item6, summary.getItem());
		Assert.assertEquals(20, (long)summary.getQuantity());
		Assert.assertTrue(DateUtils.isSameDay(cal4.getTime(), summary.getExpiration()));
	}
}
