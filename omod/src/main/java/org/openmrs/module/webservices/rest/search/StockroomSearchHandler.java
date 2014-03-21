package org.openmrs.module.webservices.rest.search;

import java.util.Arrays;

import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

@Component
public class StockroomSearchHandler implements SearchHandler {

    private final SearchConfig searchConfig = new SearchConfig("default", ModuleRestConstants.STOCKROOM_RESOURCE, Arrays.asList("1.9.*"),
            Arrays.asList(
                    new SearchQuery.Builder("Find a stockroom by its name or code, optionally filtering by location")
                            .withRequiredParameters("q")
                            .withOptionalParameters("location_uuid").build()
            )
    );

    @Override
    public SearchConfig getSearchConfig() {
        return searchConfig;
    }

    @Override
    public PageableResult search(RequestContext context) throws ResponseException {
    	System.out.println("search");
    	return null;
    }

}
