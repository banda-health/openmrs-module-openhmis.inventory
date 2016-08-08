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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.search.BaseObjectTemplateSearch;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.IItemStockDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
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
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Search handler for {@link ItemStock}.
 */
@Component
public class ItemStockSearchHandler
        extends BaseSearchHandler
        implements SearchHandler {
	private static final Log LOG = LogFactory.getLog(ItemStockSearchHandler.class);

	private final SearchConfig searchConfig = new SearchConfig("default", ModuleRestConstants.ITEM_STOCK_RESOURCE,
	        Arrays.asList("*"),
	        Arrays.asList(
	                new SearchQuery.Builder("Find item stock by stockroom and an optional name fragment.")
	                        .withOptionalParameters("q", "item_uuid", "stockroom_uuid")
	                        .build()
	                )
	        );

	private IStockroomDataService stockroomDataService;
	private IItemDataService itemDataService;
	private IItemStockDataService itemStockDataService;

	@Autowired
	public ItemStockSearchHandler(IStockroomDataService stockroomDataService, IItemDataService itemDataService,
	    IItemStockDataService itemStockDataService) {
		this.stockroomDataService = stockroomDataService;
		this.itemDataService = itemDataService;
		this.itemStockDataService = itemStockDataService;
	}

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context) {
		String query = context.getParameter("q");
		Stockroom stockroom = getOptionalEntityByUuid(stockroomDataService, context.getParameter("stockroom_uuid"));

		List<ItemStock> items = null;
		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);

		if (!StringUtils.isEmpty(query)) {
			// Search for items starting with the specified query
			ItemSearch search = new ItemSearch();
			search.setNameComparisonType(BaseObjectTemplateSearch.StringComparisonType.LIKE);
			search.getTemplate().setName(query + "%");

			if (stockroom == null) {
				LOG.warn("Could not find stockroom '" + context.getParameter("stockroom_uuid") + "'");
			} else {
				items = stockroomDataService.getItems(stockroom, search, pagingInfo);
			}
		} else {
			Item item = getOptionalEntityByUuid(itemDataService, context.getParameter("item_uuid"));

			if (stockroom == null) {
				if (item == null) {
					LOG.warn("No query, stockroom or item search was specified.");
				} else {
					// Return all item stock for the specified item
					items = itemStockDataService.getItemStockByItem(item, pagingInfo);
				}
			} else {
				if (item == null) {
					// Return all item stock for the specified stockroom
					items = stockroomDataService.getItemsByRoom(stockroom, pagingInfo);
				} else {
					// Return the item stock record for the specified stockroom and item
					pagingInfo = null;

					items = new ArrayList<ItemStock>(1);
					items.add(stockroomDataService.getItem(stockroom, item));
				}
			}
		}

		if (items == null || items.size() == 0) {
			return new EmptySearchResult();
		}

		if (pagingInfo == null) {
			return new AlreadyPaged<ItemStock>(context, items, false);
		}

		return new AlreadyPagedWithLength<ItemStock>(context, items, pagingInfo.hasMoreResults(),
		        pagingInfo.getTotalRecordCount());
	}
}
