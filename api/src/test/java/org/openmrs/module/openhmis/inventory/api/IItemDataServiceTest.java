/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.openhmis.inventory.api;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import liquibase.util.StringUtils;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataServiceTest;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.inventory.api.model.Category;
import org.openmrs.module.openhmis.inventory.api.model.Department;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemCode;
import org.openmrs.module.openhmis.inventory.api.model.ItemPrice;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;

public class IItemDataServiceTest extends IMetadataDataServiceTest<IItemDataService, Item> {
	IDepartmentDataService departmentService;
	ICategoryDataService categoryService;

	public static final String ITEM_DATASET = TestConstants.BASE_DATASET_DIR + "ItemTest.xml";

	@Override
	public void before() throws Exception {
		super.before();

		departmentService = Context.getService(IDepartmentDataService.class);
		categoryService = Context.getService(ICategoryDataService.class);

		executeDataSet(IDepartmentDataServiceTest.DEPARTMENT_DATASET);
		executeDataSet(ICategoryDataServiceTest.CATEGORY_DATASET);
		executeDataSet(ITEM_DATASET);
	}

	@Override
	protected int getTestEntityCount() {
		return 3;
	}

	@Override
	public Item createEntity(boolean valid) {
		if (departmentService == null) {
			departmentService = Context.getService(IDepartmentDataService.class);
		}
		if (categoryService == null) {
			categoryService = Context.getService(ICategoryDataService.class);
		}

		Item item = new Item();
		item.setDepartment(departmentService.getById(0));
		item.setCreator(Context.getAuthenticatedUser());

		if (valid) {
			item.setName("Test Name");
		}

		item.setDescription("Test Description");

		item.setCategory(categoryService.getById(0));
		item.setConcept(Context.getConceptService().getConcept(0));
		item.setHasPhysicalInventory(true);

		item.addCode("one", "Test Code 010");
		item.addCode("two", "Test Code 011");

		ItemPrice price = item.addPrice("default", BigDecimal.valueOf(100));
		item.addPrice("second", BigDecimal.valueOf(200));
		item.setDefaultPrice(price);

		return item;
	}

	@Override
	protected void updateEntityFields(Item item) {
		item.setDepartment(departmentService.getById(1));
		item.setDescription(item.getDescription() + " Updated");
		item.setName(item.getName() + " Updated");
		item.setHasPhysicalInventory(!item.hasPhysicalInventory());

		Set<ItemCode> codes = item.getCodes();
		if (codes.size() > 0) {
			// Update an existing code
			Iterator<ItemCode> iterator = codes.iterator();
			ItemCode code = iterator.next();
			code.setName(code.getName() + " Updated");
			code.setCode(code.getCode() + " Updated");

			if (codes.size() > 1) {
				// Delete an existing code
				code = iterator.next();

				item.removeCode(code);
			}
		}

		// Add a new code
		item.addCode("three", "Test Code 012");

		Set<ItemPrice> prices = item.getPrices();
		if (prices.size() > 0) {
			// Update n existing price
			Iterator<ItemPrice> iterator = prices.iterator();
			ItemPrice price = iterator.next();
			price.setName(price.getName() + " Updated");
			price.setPrice(price.getPrice().multiply(BigDecimal.valueOf(10)));

			if (prices.size() > 1) {
				// Delete an existing price
				price = iterator.next();

				item.removePrice(price);
			}
		}

		// Add a new price
		ItemPrice price = item.addPrice("third", BigDecimal.valueOf(3));

		item.setDefaultPrice(price);
	}

	@Override
	protected void assertEntity(Item expected, Item actual) {
		super.assertEntity(expected, actual);

		Assert.assertNotNull(expected.getDepartment());
		Assert.assertNotNull(actual.getDepartment());
		Assert.assertEquals(expected.getDepartment().getId(), actual.getDepartment().getId());
		Assert.assertEquals(expected.hasExpiration(), actual.hasExpiration());
		Assert.assertEquals(expected.hasPhysicalInventory(), actual.hasPhysicalInventory());

		if (expected.getConcept() == null) {
			Assert.assertNull(actual.getConcept());
		} else {
			Assert.assertEquals(expected.getConcept().getId(), actual.getConcept().getId());
		}

		if (expected.getCategory() == null) {
			Assert.assertNull(actual.getCategory());
		} else {
			Assert.assertEquals(expected.getCategory().getId(), actual.getCategory().getId());
		}

		assertCollection(expected.getCodes(), actual.getCodes(), new Action2<ItemCode, ItemCode>() {
			@Override
			public void apply(ItemCode expectedCode, ItemCode actualCode) {
				assertOpenmrsMetadata(expectedCode, actualCode);

				Assert.assertEquals(expectedCode.getName(), actualCode.getName());
				Assert.assertEquals(expectedCode.getCode(), actualCode.getCode());
			}
		});

		assertCollection(expected.getPrices(), actual.getPrices(), new Action2<ItemPrice, ItemPrice>() {
			@Override
			public void apply(ItemPrice expectedPrice, ItemPrice actualPrice) {
				assertOpenmrsMetadata(expectedPrice, actualPrice);

				Assert.assertEquals(expectedPrice.getName(), actualPrice.getName());
				Assert.assertEquals(expectedPrice.getPrice(), actualPrice.getPrice());
			}
		});
	}

	/**
	 * @verifies throw IllegalArgumentException if the item code is null
	 * @see IItemDataService#getItemByCode(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItemByCode_shouldThrowIllegalArgumentExceptionIfTheItemCodeIsNull() throws Exception {
		service.getItemByCode(null);
	}

	/**
	 * @verifies throw IllegalArgumentException if the item code is longer than 255 characters
	 * @see IItemDataService#getItemByCode(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getItemByCode_shouldThrowIllegalArgumentExceptionIfTheItemCodeIsLongerThan255Characters() throws Exception {
		service.getItemByCode(StringUtils.repeat("A", 256));
	}

	/**
	 * @verifies return the item with the specified item code
	 * @see IItemDataService#getItemByCode(String)
	 */
	@Test
	public void getItemByCode_shouldReturnTheItemWithTheSpecifiedItemCode() throws Exception {
		Item item = service.getItemByCode("item 1 code");
		Assert.assertNotNull(item);

		Item expected = service.getById(0);
		assertEntity(expected, item);
	}

	/**
	 * @verifies return null if the item code is not found
	 * @see IItemDataService#getItemByCode(String)
	 */
	@Test
	public void getItemByCode_shouldReturnNullIfTheItemCodeIsNotFound() throws Exception {
		Item item = service.getItemByCode("not a valid code");

		Assert.assertNull(item);
	}

	/**
	 * @verifies throw NullPointerException if the department is null
	 * @see IItemDataService#findItems(Department, String, boolean)
	 */
	@Test(expected = NullPointerException.class)
	public void findItems_shouldThrowNullPointerExceptionIfTheDepartmentIsNull() throws Exception {
		Department department = null;
		service.findItems(department, "something", false);
	}

	/**
	 * @verifies throw NullPointerException if the department is null
	 * @see IItemDataService#findItems(Category, String, boolean)
	 */
	@Test(expected = NullPointerException.class)
	public void findItems_shouldThrowNullPointerExceptionIfTheDepartmentIsDefinedAndCategoryIsNull() throws Exception {
		Department department = new Department();
		Category category = null;
		service.findItems(department, category, "something", false);
	}

	/**
	 * @verifies throw NullPointerException if the department is null
	 * @see IItemDataService#findItems(Category, String, boolean)
	 */
	@Test(expected = NullPointerException.class)
	public void findItems_shouldThrowNullPointerExceptionIfTheDepartmentIsNullAndCategoryIsDefined() throws Exception {
		Department department = null;
		Category category = new Category();
		service.findItems(department, category, "something", false);
	}

	/**
	 * @verifies throw NullPointerException if the department is null
	 * @see IItemDataService#findItems(Category, String, boolean)
	 */
	@Test(expected = NullPointerException.class)
	public void findItems_shouldThrowNullPointerExceptionIfTheCategoryIsNull() throws Exception {
		Category category= null;
		service.findItems(category, "something", false);
	}

	/**
	 * @verifies throw IllegalArgumentException if the name is null
	 * @see IItemDataService#findItems(Department, String, boolean)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void findItems_shouldThrowIllegalArgumentExceptionIfTheNameIsNull() throws Exception {
		service.findItems(departmentService.getById(0), null, false);
	}

	/**
	 * @verifies throw IllegalArgumentException if the name is empty
	 * @see IItemDataService#findItems(Department, String, boolean)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void findItems_shouldThrowIllegalArgumentExceptionIfTheNameIsEmpty() throws Exception {
		service.findItems(departmentService.getById(0), "", false);
	}

	/**
	 * @verifies throw IllegalArgumentException if the name is longer than 255 characters
	 * @see IItemDataService#findItems(Department, String, boolean)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void findItems_shouldThrowIllegalArgumentExceptionIfTheNameIsLongerThan255Characters() throws Exception {
		service.findItems(departmentService.getById(0), StringUtils.repeat("A", 256), false);
	}

	/**
	 * @verifies return an empty list if no items are found
	 * @see IItemDataService#findItems(Department, String, boolean)
	 */
	@Test
	public void findItems_shouldReturnAnEmptyListIfNoItemsAreFound() throws Exception {
		List<Item> items = service.findItems(departmentService.getById(0), "not a valid name", false);

		Assert.assertNotNull(items);
		Assert.assertEquals(0, items.size());
	}

	/**
	 * @verifies not return retired items unless specified
	 * @see IItemDataService#findItems(Department, String, boolean)
	 */
	@Test
	public void findItems_shouldNotReturnRetiredItemsUnlessSpecified() throws Exception {
		Item item = service.getById(0);
		service.retire(item, "test");

		Context.flushSession();

		Department department = departmentService.getById(0);
		List<Item> items = service.findItems(department, "t", false);
		Assert.assertNotNull(items);
		Assert.assertEquals(getTestEntityCount() - 1, items.size());

		items = service.findItems(department, "t", true);
		Assert.assertNotNull(items);
		Assert.assertEquals(getTestEntityCount(), items.size());
	}

	/**
	 * @verifies return items that start with the specified name
	 * @see IItemDataService#findItems(Department, String, boolean)
	 */
	@Test
	public void findItems_shouldReturnItemsThatStartWithTheSpecifiedName() throws Exception {
		List<Item> items = service.findItems(departmentService.getById(0), "test 1", false);
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());

		Item item = service.getById(0);
		assertEntity(item, items.get(0));
	}

	/**
	 * @verifies return items for only the specified department
	 * @see IItemDataService#findItems(Department, String, boolean)
	 */
	@Test
	public void findItems_shouldReturnItemsForOnlyTheSpecifiedDepartment() throws Exception {
		List<Item> items = service.findItems(departmentService.getById(0), "test", false);
		Assert.assertNotNull(items);
		Assert.assertEquals(3, items.size());

		items = service.findItems(departmentService.getById(1), "test", false);
		Assert.assertNotNull(items);
		Assert.assertEquals(0, items.size());
	}

	/**
	 * @verifies throw NullPointerException if the department is null
	 * @see IItemDataService#getItemsByDepartment(Department, boolean)
	 */
	@Test(expected = NullPointerException.class)
	public void getItemsByDepartment_shouldThrowNullPointerExceptionIfTheDepartmentIsNull() throws Exception {
		service.getItemsByDepartment(null, false);
	}

	/**
	 * @verifies return an empty list if the department has no items
	 * @see IItemDataService#getItemsByDepartment(Department, boolean)
	 */
	@Test
	public void getItemsByDepartment_shouldReturnAnEmptyListIfTheDepartmentHasNoItems() throws Exception {
		Department department = departmentService.getById(2);

		List<Item> results = service.getItemsByDepartment(department, false);

		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
	}

	/**
	 * @verifies not return retired items unless specified
	 * @see IItemDataService#getItemsByDepartment(Department, boolean)
	 */
	@Test
	public void getItemsByDepartment_shouldNotReturnRetiredItemsUnlessSpecified() throws Exception {
		Item retiredItem = service.getById(2);
		retiredItem.setRetired(true);
		retiredItem.setRetireReason("reason");

		service.save(retiredItem);
		Context.flushSession();

		Department department = departmentService.getById(0);

		List<Item> results = service.getItemsByDepartment(department, false);
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
		for (Item result : results) {
			if (result.getId().equals(retiredItem.getId())) {
				Assert.fail("The retired item was incorrectly returned.");
			}
		}

		results = service.getItemsByDepartment(department, true);
		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());
	}

	/**
	 * @verifies return all items for the specified department
	 * @see IItemDataService#getItemsByDepartment(Department, boolean)
	 */
	@Test
	public void getItemsByDepartment_shouldReturnAllItemsForTheSpecifiedDepartment() throws Exception {
		Department department0 = departmentService.getById(0);
		Department department2 = departmentService.getById(2);
		Item item = service.getById(2);
		item.setDepartment(department2);

		service.save(item);
		Context.flushSession();

		List<Item> results = service.getItemsByDepartment(department0, false);
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
		for (Item result : results) {
			if (result.getId().equals(item.getId())) {
				Assert.fail("The retired item was incorrectly returned.");
			}
		}

		results = service.getItemsByDepartment(department2, false);
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
	}

	@Test
	public void retire_shouldRetireAllChildCategories() {
		Item item = service.getById(0);

		// Everything is currently not retired
		Assert.assertNotNull(item);
		Assert.assertEquals(false, item.getRetired());
		for (ItemCode child : item.getCodes()) {
			Assert.assertEquals(false, child.getRetired());
		}
		for (ItemPrice child : item.getPrices()) {
			Assert.assertEquals(false, child.getRetired());
		}

		// Retire the item
		service.retire(item, "something");
		Context.flushSession();

		// The item and all related objects should now be retired
		Assert.assertEquals(true, item.getRetired());
		for (ItemCode child : item.getCodes()) {
			Assert.assertEquals(true, child.getRetired());
		}
		for (ItemPrice child : item.getPrices()) {
			Assert.assertEquals(true, child.getRetired());
		}

		item = service.getById(0);
		Assert.assertEquals(true, item.getRetired());
		for (ItemCode child : item.getCodes()) {
			Assert.assertEquals(true, child.getRetired());
		}
		for (ItemPrice child : item.getPrices()) {
			Assert.assertEquals(true, child.getRetired());
		}
	}

	@Test
	public void unretire_shouldUnretireAllChildCategories() {
		Item item = service.getById(0);

		service.retire(item, "something");
		Context.flushSession();

		// Ensure that parent and children are now retired
		item = service.getById(0);
		Assert.assertEquals(true, item.getRetired());
		for (ItemCode child : item.getCodes()) {
			Assert.assertEquals(true, child.getRetired());
		}
		for (ItemPrice child : item.getPrices()) {
			Assert.assertEquals(true, child.getRetired());
		}

		service.unretire(item);
		Context.flushSession();

		Assert.assertEquals(false, item.getRetired());
		for (ItemCode child : item.getCodes()) {
			Assert.assertEquals(false, child.getRetired());
		}
		for (ItemPrice child : item.getPrices()) {
			Assert.assertEquals(false, child.getRetired());
		}

		item = service.getById(0);
		Assert.assertEquals(false, item.getRetired());
		for (ItemCode child : item.getCodes()) {
			Assert.assertEquals(false, child.getRetired());
		}
		for (ItemPrice child : item.getPrices()) {
			Assert.assertEquals(false, child.getRetired());
		}
	}

	/**
	 * @verifies throw NullPointerException if item search is null
	 * @see IItemDataService#findItems(ItemSearch, PagingInfo)
	 */
	@Test(expected = NullPointerException.class)
	public void findItems_shouldThrowNullPointerExceptionIfItemSearchIsNull() throws Exception {
		service.findItems(null, null);
	}

	/**
	 * @verifies throw NullPointerException if item search template object is null
	 * @see IItemDataService#findItems(ItemSearch, PagingInfo)
	 */
	@Test(expected = NullPointerException.class)
	public void findItems_shouldThrowNullPointerExceptionIfItemSearchTemplateObjectIsNull() throws Exception {
		ItemSearch search = new ItemSearch(null);

		service.findItems(search, null);
	}

	/**
	 * @verifies return an empty list if no items are found via the search
	 * @see IItemDataService#findItems(ItemSearch, PagingInfo)
	 */
	@Test
	public void findItems_shouldReturnAnEmptyListIfNoItemsAreFoundViaTheSearch() throws Exception {
		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setConcept(Context.getConceptService().getConcept(0));

		List<Item> result = service.findItems(search, null);

		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}

	/**
	 * @verifies return items filtered by department
	 * @see IItemDataService#findItems(ItemSearch, PagingInfo)
	 */
	@Test
	public void findItems_shouldReturnItemsFilteredByDepartment() throws Exception {
		Department department2 = departmentService.getById(2);
		Item item = service.getById(2);
		item.setDepartment(department2);

		service.save(item);
		Context.flushSession();

		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setDepartment(department2);

		List<Item> results = service.findItems(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(item, results.get(0));
	}

	/**
	 * @verifies return items filtered by category
	 * @see IItemDataService#findItems(ItemSearch, PagingInfo)
	 */
	@Test
	public void findItems_shouldReturnItemsFilteredByCategory() throws Exception {
		Category category = categoryService.getById(0);
		Item item = service.getById(0);
		item.setCategory(category);

		service.save(item);
		Context.flushSession();

		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setCategory(category);

		List<Item> results = service.findItems(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(item, results.get(0));
	}

	/**
	 * @verifies return items filtered by concept
	 * @see IItemDataService#findItems(ItemSearch, PagingInfo)
	 */
	@Test
	public void findItems_shouldReturnItemsFilteredByConcept() throws Exception {
		Concept concept = Context.getConceptService().getConcept(0);

		Item item = service.getById(0);
		item.setConcept(concept);

		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setConcept(concept);

		List<Item> results = service.findItems(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		assertEntity(item, results.get(0));
	}

	/**
	 * @verifies return all items if paging is null
	 * @see IItemDataService#findItems(ItemSearch, PagingInfo)
	 */
	@Test
	public void findItems_shouldReturnAllItemsIfPagingIsNull() throws Exception {
		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setDepartment(departmentService.getById(0));

		List<Item> results = service.findItems(search, null);

		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());
	}

	/**
	 * @verifies return paged items if paging is specified
	 * @see IItemDataService#findItems(ItemSearch, PagingInfo)
	 */
	@Test
	public void findItems_shouldReturnPagedItemsIfPagingIsSpecified() throws Exception {
		PagingInfo pagingInfo = new PagingInfo(1, 1);

		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setDepartment(departmentService.getById(0));

		List<Item> results = service.findItems(search, pagingInfo);

		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(3, (long)pagingInfo.getTotalRecordCount());
	}

	/**
	 * @verifies not return retired items from search unless specified
	 * @see IItemDataService#findItems(ItemSearch, PagingInfo)
	 */
	@Test
	public void findItems_shouldNotReturnRetiredItemsFromSearchUnlessSpecified() throws Exception {
		Item item = service.getById(0);
		item.setRetired(true);
		item.setRetireReason("something");

		service.save(item);
		Context.flushSession();

		ItemSearch search = new ItemSearch(new Item());
		search.getTemplate().setDepartment(departmentService.getById(0));

		List<Item> results = service.findItems(search, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());

		search.setIncludeRetired(true);
		results = service.findItems(search, null);
		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());
	}
}
