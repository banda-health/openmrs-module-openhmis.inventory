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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.search.BaseObjectTemplateSearch;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.resource.AlreadyPagedWithLength;
import org.openmrs.module.webservices.rest.resource.PagingUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class ItemStockSearchHandler implements SearchHandler {
	private static Log log = LogFactory.getLog(ItemStockSearchHandler.class);

	private final SearchConfig searchConfig = new SearchConfig("default", ModuleRestConstants.ITEM_STOCK_RESOURCE,
			Arrays.asList("1.9.*"),
			Arrays.asList(
					new SearchQuery.Builder("Find item stock by stockroom and an optional name fragment.")
							.withRequiredParameters("stockroom_uuid")
							.withOptionalParameters("q", "item_uuid")
							.build()
			)
	);

	private IStockroomDataService stockroomDataService;
	private IItemDataService itemDataService;

	@Autowired
	public ItemStockSearchHandler(IStockroomDataService stockroomDataService, IItemDataService itemDataService) {
		this.stockroomDataService = stockroomDataService;
		this.itemDataService = itemDataService;
	}

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context) throws ResponseException {
		String query = context.getParameter("q");

		String itemUuid = context.getParameter("item_uuid");
		Item item = null;
		if (!StringUtils.isEmpty(itemUuid)) {
			item = itemDataService.getByUuid(itemUuid);
		}

		String stockroomUuid = context.getParameter("stockroom_uuid");
		Stockroom stockroom = stockroomDataService.getByUuid(stockroomUuid);

		if (stockroom == null) {
			log.warn("Could not find stockroom '" + stockroomUuid + "'");

			return new EmptySearchResult();
		}

		List<ItemStock> items;
		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);

		if (!StringUtils.isEmpty(query)) {
			ItemSearch search = new ItemSearch();
			search.setNameComparisonType(BaseObjectTemplateSearch.StringComparisonType.LIKE);
			search.getTemplate().setName(query + "%");
			items = stockroomDataService.findItems(stockroom, search, pagingInfo);
		} else if (item != null) {
			pagingInfo = null;

			items = new ArrayList<ItemStock>(1);
			items.add(stockroomDataService.getItem(stockroom, item));
		} else {
			items = stockroomDataService.getItemsByRoom(stockroom, pagingInfo);
		}

		if (items == null || items.size() == 0) {
			return new EmptySearchResult();
		} else {
			if (pagingInfo == null) {
				return new AlreadyPaged<ItemStock>(context, items, false);
			} else {
				return new AlreadyPagedWithLength<ItemStock>(context, items,
						pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
			}
		}
	}
}

