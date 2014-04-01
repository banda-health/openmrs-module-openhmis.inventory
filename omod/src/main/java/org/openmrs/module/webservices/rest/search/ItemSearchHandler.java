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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.inventory.api.ICategoryDataService;
import org.openmrs.module.openhmis.inventory.api.IDepartmentDataService;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.model.Category;
import org.openmrs.module.openhmis.inventory.api.model.Department;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.resource.AlreadyPagedWithLength;
import org.openmrs.module.webservices.rest.resource.PagingUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

@Component
public class ItemSearchHandler implements SearchHandler {
	private final SearchConfig searchConfig = new SearchConfig("default", ModuleRestConstants.ITEM_RESOURCE, Arrays.asList("1.9.*"),
			Arrays.asList(
					new SearchQuery.Builder("Find an item by its name or code, optionally filtering by category and department")
							.withRequiredParameters("q")
							.withOptionalParameters("department_uuid", "category_uuid").build()
			)
	);

	@Override
	public PageableResult search(RequestContext context) throws ResponseException {

		String query = context.getParameter("q");
		String department_uuid = context.getParameter("department_uuid");
		String category_uuid = context.getParameter("category_uuid");

		query = query.isEmpty() ? null : query;
		department_uuid = StringUtils.isEmpty(department_uuid) ? null : department_uuid;
		category_uuid = StringUtils.isEmpty(category_uuid) ? null : category_uuid;
		IItemDataService service = Context.getService(IItemDataService.class);

		// Try searching by code
		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
		List<Item> items = service.getItemsByCode(query, context.getIncludeAll(), pagingInfo);
		if (items.size() > 0) {
			return new AlreadyPagedWithLength<Item>(context, items, pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
		}

		if (department_uuid == null && category_uuid == null) {
			return doNameSearch(context, query, service, pagingInfo);
		}
		else {
			return doParameterSearch(context, query, department_uuid, category_uuid, service, pagingInfo);
		}
	}

	private PageableResult doParameterSearch(RequestContext context, String query, String department_uuid, String category_uuid, IItemDataService service, PagingInfo pagingInfo) {

		IDepartmentDataService deptService = Context.getService(IDepartmentDataService.class);
		ICategoryDataService categoryService = Context.getService(ICategoryDataService.class);
		Department department = StringUtils.isBlank(department_uuid) ? null : deptService.getByUuid(department_uuid);
		Category category = StringUtils.isBlank(category_uuid) ? null : categoryService.getByUuid(category_uuid);

		// Get all items in the department or category if no name query is given
		if (query == null) {
			List<Item> items;
			if (department != null && category != null) {
				items = service.getItemsByDepartmentAndCategory(department, category, context.getIncludeAll(), pagingInfo);
			} else if (department != null) {
				items = service.getItemsByDepartment(department, context.getIncludeAll(), pagingInfo);
			} else {
				items = service.getItemsByCategory(category, context.getIncludeAll(), pagingInfo);
			}
			PageableResult results = new AlreadyPagedWithLength<Item>(context, items, pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
			return results;
		} else {
			List<Item> items;
			if (department != null && category != null) {
				items = service.findItems(department, category, query, context.getIncludeAll(), pagingInfo);
			} else if (department != null) {
				items = service.findItems(department, query, context.getIncludeAll(), pagingInfo);
			} else {
				items = service.findItems(category, query, context.getIncludeAll(), pagingInfo);
			}
			PageableResult results = new AlreadyPagedWithLength<Item>(context, items, pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
			return results;
		}
	}

	private PageableResult doNameSearch(RequestContext context, String query, IItemDataService service, PagingInfo pagingInfo) {
		List<Item> items = service.findByName(query, context.getIncludeAll(), pagingInfo);
		AlreadyPagedWithLength<Item> results = new AlreadyPagedWithLength<Item>(context, items, pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
		return results;
	}

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}
}

