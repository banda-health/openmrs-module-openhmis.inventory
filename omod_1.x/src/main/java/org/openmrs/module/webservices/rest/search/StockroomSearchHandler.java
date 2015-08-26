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
