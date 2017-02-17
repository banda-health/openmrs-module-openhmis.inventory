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
package org.openmrs.module.webservices.rest.search;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.search.BaseObjectTemplateSearch;
import org.openmrs.module.openhmis.inventory.ModuleSettings;
import org.openmrs.module.openhmis.inventory.api.IDepartmentDataService;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Search handler for {@link Item}s.
 */
@Component
public class ItemSearchHandler
        extends BaseSearchHandler
        implements SearchHandler {
	private final SearchConfig searchConfig =
	        new SearchConfig("default", ModuleRestConstants.ITEM_RESOURCE, Arrays.asList("*"),
	                Arrays.asList(
	                        new SearchQuery.Builder(
	                                "Find an item by its name or code, optionally filtering by department")
	                                .withRequiredParameters("q")
	                                .withOptionalParameters("department_uuid", "has_physical_inventory")
	                                .build()
	                        )
	        );

	private IItemDataService service;
	private IDepartmentDataService departmentService;

	@Autowired
	public ItemSearchHandler(IItemDataService service, IDepartmentDataService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	@Override
	public PageableResult search(RequestContext context) {
		String query = context.getParameter("q");
		query = query.isEmpty() ? null : query;

		String hasPhysicalInventoryString = context.getParameter("has_physical_inventory");
		Boolean hasPhysicalInventory = null;
		if (StringUtils.isNotEmpty(hasPhysicalInventoryString)) {
			hasPhysicalInventory = Boolean.parseBoolean(hasPhysicalInventoryString);
		}

		Department department = getOptionalEntityByUuid(departmentService, context.getParameter("department_uuid"));

		List<Item> items = null;
		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);

		// If no parameters are specified, check if the global 'wildcard item search' setting is enabled
		// and search by name, then by code. If the global 'wildcard' is not enabled, search by code (an exact match),
		// then by name
		if (department == null && hasPhysicalInventory == null) {
			if (query != null) {

				// Check if the global wildcard search is enabled
				if (ModuleSettings.useWildcardItemSearch()) {
					query = '%' + query + '%';
					items = service.getByNameFragment(query, context.getIncludeAll(), pagingInfo);
				}

				if (items == null || items.size() == 0) {
					// Try searching by code
					pagingInfo = PagingUtil.getPagingInfoFromContext(context);
					items = service.getItemsByCode(query, context.getIncludeAll(), pagingInfo);
				}
			}

			if (items == null || items.size() == 0) {
				//new paging info as otherwise the old one is used and paging does not work
				pagingInfo = PagingUtil.getPagingInfoFromContext(context);
				// If no items are found, search by name
				items = service.getByNameFragment(query, context.getIncludeAll(), pagingInfo);
			}
		} else {
			// Create the item search template with the specified parameters
			ItemSearch search = createSearchTemplate(context, query, department, hasPhysicalInventory);

			items = service.getItemsByItemSearch(search, pagingInfo);
		}

		return new AlreadyPagedWithLength<Item>(context, items, pagingInfo.hasMoreResults(),
		        pagingInfo.getTotalRecordCount());
	}

	private ItemSearch createSearchTemplate(RequestContext context, String name, Department department,
	        Boolean hasPhysicalInventory) {
		ItemSearch template = new ItemSearch();

		if (StringUtils.isNotEmpty(name)) {
			template.setNameComparisonType(BaseObjectTemplateSearch.StringComparisonType.LIKE);
			if (ModuleSettings.useWildcardItemSearch()) {
				template.getTemplate().setName("%" + name + "%");
			} else {
				template.getTemplate().setName(name + "%");
			}

		}

		template.getTemplate().setDepartment(department);
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
