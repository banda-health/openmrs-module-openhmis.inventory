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
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

@Component
public class ItemConceptSuggestionSearchHandler implements SearchHandler {

    private static final int DEFAULT_PAGE_SIZE = 50;

    private final SearchConfig searchConfig = new SearchConfig("default", ModuleRestConstants.ITEM_CONCEPT_SUGGESTION_RESOURCE, Arrays.asList("1.9.*"),
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
    public PageableResult search(RequestContext context) throws ResponseException {
         return doSearch(Context.getService(IItemConceptSuggestionDataService.class), context);
    }

    public static PageableResult doSearch(IItemConceptSuggestionDataService service, RequestContext context) throws ResponseException {
        PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
        pagingInfo.setPageSize(DEFAULT_PAGE_SIZE);

        List<ItemConceptSuggestion> itemsWithConceptSuggestions = service.getItemsWithConceptSuggestions();
        Long totalRecordCount = new Long(itemsWithConceptSuggestions.size());
        pagingInfo.setTotalRecordCount(totalRecordCount);

        AlreadyPagedWithLength<ItemConceptSuggestion> results = new AlreadyPagedWithLength<ItemConceptSuggestion>(context, itemsWithConceptSuggestions, pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
        return results;
    }
}
