package org.openmrs.module.webservices.rest.search;

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
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class StockroomSearchHandler implements SearchHandler {

    private final SearchConfig searchConfig = new SearchConfig("default", ModuleRestConstants.STOCKROOM_RESOURCE, Arrays.asList("1.9.*"),
            Arrays.asList(
                    new SearchQuery.Builder("Find a stockroom by its name, optionally filtering by location")
                            .withRequiredParameters("q")
                            .withOptionalParameters("location_uuid").build()
            )
    );

    @Override
    public PageableResult search(RequestContext context) throws ResponseException {
        String query = context.getParameter("q");
        String location_uuid = context.getParameter("location_uuid");
        query = query.isEmpty() ? null : query;
        location_uuid = StringUtils.isEmpty(location_uuid) ? null : location_uuid;
        IStockroomDataService service = Context.getService(IStockroomDataService.class);

        if (location_uuid == null) {
            // Do a name search
            PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
            List<Stockroom> stockrooms = service.getByNameFragment(query, context.getIncludeAll(), pagingInfo);
            AlreadyPagedWithLength<Stockroom> results = new AlreadyPagedWithLength<Stockroom>(context, stockrooms, pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
            return results;
        }
        else {
            LocationService locationService = Context.getLocationService();
            Location location = locationService.getLocationByUuid(location_uuid);
            // Get all items in the department if no name query is given
            if (query == null) {
                return searchByLocation(location_uuid, context);
            }
            // Do a name + department search
            PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
            List<Stockroom> stockrooms = service.getStockrooms(location, query, context.getIncludeAll(), pagingInfo);
            PageableResult results = new AlreadyPagedWithLength<Stockroom>(context, stockrooms, pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
            return results;
        }
    }

    public PageableResult searchByLocation(String location_uuid, RequestContext context) throws ResponseException {
        LocationService locationService = Context.getLocationService();
        Location location = locationService.getLocationByUuid(location_uuid);
        IStockroomDataService service = Context.getService(IStockroomDataService.class);

        PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
        List<Stockroom> stockrooms = service.getStockroomsByLocation(location, context.getIncludeAll(), pagingInfo);
        PageableResult results = new AlreadyPagedWithLength<Stockroom>(context, stockrooms, pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
        return results;
    }

    @Override
    public SearchConfig getSearchConfig() {
        return searchConfig;
    }
}
