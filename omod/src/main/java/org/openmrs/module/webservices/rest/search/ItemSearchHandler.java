/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
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
package org.openmrs.module.webservices.rest.search;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.search.BaseObjectTemplateSearch;
import org.openmrs.module.openhmis.inventory.api.ICategoryDataService;
import org.openmrs.module.openhmis.inventory.api.IDepartmentDataService;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.model.Category;
import org.openmrs.module.openhmis.inventory.api.model.Department;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.resource.AlreadyPagedWithLength;
import org.openmrs.module.webservices.rest.resource.PagingUtil;
import org.openmrs.module.webservices.rest.resource.search.BaseSearchHandler;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ItemSearchHandler
		extends BaseSearchHandler
		implements SearchHandler {
	private final SearchConfig searchConfig =
			new SearchConfig("default", ModuleRestConstants.ITEM_RESOURCE, Arrays.asList("1.9.*"),
					Arrays.asList(
							new SearchQuery.Builder(
									"Find an item by its name or code, optionally filtering by category and department")
									.withRequiredParameters("q")
									.withOptionalParameters("department_uuid", "category_uuid", "has_physical_inventory")
									.build()
					)
			);

	private IItemDataService service;
	private IDepartmentDataService departmentService;
	private ICategoryDataService categoryService;

	@Autowired
	public ItemSearchHandler(IItemDataService service, IDepartmentDataService departmentService,
			ICategoryDataService categoryService) {
		this.service = service;
		this.departmentService = departmentService;
		this.categoryService = categoryService;
	}

	@Override
	public PageableResult search(RequestContext context) throws ResponseException {
		String query = context.getParameter("q");
		query = query.isEmpty() ? null : query;

		String hasPhysicalInventoryString = context.getParameter("has_physical_inventory");
		Boolean hasPhysicalInventory = null;
		if (!StringUtils.isEmpty(hasPhysicalInventoryString)) {
			hasPhysicalInventory = Boolean.parseBoolean(hasPhysicalInventoryString);
		}

		Department department = getOptionalEntityByUuid(departmentService, context.getParameter("department_uuid"));
		Category category = getOptionalEntityByUuid(categoryService, context.getParameter("category_uuid"));

		List<Item> items = null;
		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);

		// If no parameters are specified first attempt a search by code (an exact match), then by name
		if (department == null && category == null && hasPhysicalInventory == null) {
			if (query != null) {
				// Try searching by code
				items = service.getItemsByCode(query, context.getIncludeAll(), pagingInfo);
			}

			if (items == null || items.size() == 0) {
				// If no items are found, search by name
				items = service.getByNameFragment(query, context.getIncludeAll(), pagingInfo);
			}
		} else {
			// Create the item search template with the specified parameters
			ItemSearch search = createSearchTemplate(context, query, department, category, hasPhysicalInventory);

			items = service.getItemsByItemSearch(search, pagingInfo);
		}

		return new AlreadyPagedWithLength<Item>(context, items, pagingInfo.hasMoreResults(),
				pagingInfo.getTotalRecordCount());
	}

	private ItemSearch createSearchTemplate(RequestContext context, String name, Department department,Category category,
			Boolean hasPhysicalInventory) {
		ItemSearch template = new ItemSearch();

		if (!StringUtils.isEmpty(name)) {
			template.setNameComparisonType(BaseObjectTemplateSearch.StringComparisonType.LIKE);
			template.getTemplate().setName(name + "%");
		}

		template.getTemplate().setDepartment(department);
		template.getTemplate().setCategory(category);
		template.getTemplate().setHasPhysicalInventory(hasPhysicalInventory);

		if (!context.getIncludeAll()) {
			template.getTemplate().setRetired(false);
		}

		return template;
	}

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}
}
