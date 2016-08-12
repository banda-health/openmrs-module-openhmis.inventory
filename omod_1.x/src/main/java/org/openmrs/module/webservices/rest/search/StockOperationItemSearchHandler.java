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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.resource.AlreadyPagedWithLength;
import org.openmrs.module.webservices.rest.resource.PagingUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Search handler for {@link StockOperationItem}s.
 */
@Component
public class StockOperationItemSearchHandler implements SearchHandler {
	private static final Log LOG = LogFactory.getLog(ItemStockSearchHandler.class);

	private final SearchConfig searchConfig = new SearchConfig("default", ModuleRestConstants.OPERATION_ITEM_RESOURCE,
	        Arrays.asList("*"),
	        Arrays.asList(
	                new SearchQuery.Builder("Find all operation items by operation.")
	                        .withRequiredParameters("operation_uuid").build()
	                )
	        );

	private IStockOperationDataService operationDataService;

	@Autowired
	public StockOperationItemSearchHandler(IStockOperationDataService operationDataService) {
		this.operationDataService = operationDataService;
	}

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context) {
		return doSearch(operationDataService, context);
	}

	public static PageableResult doSearch(IStockOperationDataService service, RequestContext context) {
		String operationUuid = context.getParameter("operation_uuid");
		StockOperation operation = service.getByUuid(operationUuid);

		if (operation == null) {
			LOG.warn("Could not find stock operation '" + operationUuid + "'");

			return new EmptySearchResult();
		}

		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
		List<StockOperationItem> items = service.getItemsByOperation(operation, pagingInfo);
		if (items == null || items.size() == 0) {
			return new EmptySearchResult();
		} else {
			return new AlreadyPagedWithLength<StockOperationItem>(context, items,
			        pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
		}
	}
}
