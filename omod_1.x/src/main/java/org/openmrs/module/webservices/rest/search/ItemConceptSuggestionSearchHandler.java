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

import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.inventory.api.IItemConceptSuggestionDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemConceptSuggestion;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.resource.AlreadyPagedWithLength;
import org.openmrs.module.webservices.rest.resource.PagingUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.springframework.stereotype.Component;

/**
 * Search handler for {@link ItemConceptSuggestion}s.
 */
@Component
public class ItemConceptSuggestionSearchHandler implements SearchHandler {

	private static final int DEFAULT_PAGE_SIZE = 50;

	private final SearchConfig searchConfig = new SearchConfig("default",
	        ModuleRestConstants.ITEM_CONCEPT_SUGGESTION_RESOURCE,
	        Arrays.asList("*"),
	        Arrays.asList(
	                new SearchQuery.Builder("Find concept mappings for items that do not have a concept")
	                        .withOptionalParameters("q")
	                        .build()
	                )
	        );

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context) {
		return doSearch(Context.getService(IItemConceptSuggestionDataService.class), context);
	}

	public static PageableResult doSearch(IItemConceptSuggestionDataService service, RequestContext context) {
		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
		pagingInfo.setPageSize(DEFAULT_PAGE_SIZE);

		List<ItemConceptSuggestion> itemsWithConceptSuggestions = service.getItemsWithConceptSuggestions();
		Long totalRecordCount = new Long(itemsWithConceptSuggestions.size());
		pagingInfo.setTotalRecordCount(totalRecordCount);

		AlreadyPagedWithLength<ItemConceptSuggestion> results =
		        new AlreadyPagedWithLength<ItemConceptSuggestion>(context, itemsWithConceptSuggestions,
		                pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
		return results;
	}
}
