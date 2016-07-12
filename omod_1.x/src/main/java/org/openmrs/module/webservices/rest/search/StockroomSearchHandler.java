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
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
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
 * Search handler for {@link Stockroom}s.
 */
@Component
public class StockroomSearchHandler implements SearchHandler {

	private final SearchConfig searchConfig = new SearchConfig("default", ModuleRestConstants.STOCKROOM_RESOURCE,
	        Arrays.asList("*"), Arrays.asList(new SearchQuery.Builder(
	                "Find a stockroom by its name, optionally filtering by location").withRequiredParameters("q")
	                .withOptionalParameters("location_uuid").build()));

	@Override
	public PageableResult search(RequestContext context) {
		String query = context.getParameter("q");
		String locationUuid = context.getParameter("location_uuid");
		query = query.isEmpty() ? null : query;
		locationUuid = StringUtils.isEmpty(locationUuid) ? null : locationUuid;
		IStockroomDataService service = Context.getService(IStockroomDataService.class);
		List<Stockroom> stockrooms;

		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
		if (locationUuid == null) {
			// Search by name
			stockrooms = service.getByNameFragment(query, context.getIncludeAll(), pagingInfo);
		} else {
			LocationService locationService = Context.getLocationService();
			Location location = locationService.getLocationByUuid(locationUuid);

			if (query == null) {
				// Search by location
				stockrooms = service.getStockroomsByLocation(location, context.getIncludeAll(), pagingInfo);
			} else {
				// Search by name and location
				stockrooms = service.getStockrooms(location, query, context.getIncludeAll(), pagingInfo);
			}
		}

		return new AlreadyPagedWithLength<Stockroom>(context, stockrooms, pagingInfo.hasMoreResults(),
		        pagingInfo.getTotalRecordCount());
	}

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}
}
