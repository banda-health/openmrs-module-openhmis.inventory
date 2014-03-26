package org.openmrs.module.webservices.rest.search;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.inventory.api.ICategoryDataService;
import org.openmrs.module.openhmis.inventory.api.model.Category;
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
public class CategorySearchHandler implements SearchHandler {

    private final SearchConfig searchConfig = new SearchConfig("default", ModuleRestConstants.CATEGORY_RESOURCE, Arrays.asList("1.9.*"),
            Arrays.asList(
                    new SearchQuery.Builder("Find a category by its name")
                            .withRequiredParameters("q")
                            .build()
            )
    );

    @Override
    public SearchConfig getSearchConfig() {
        return searchConfig;
    }

    @Override
    public PageableResult search(RequestContext context) throws ResponseException {
        String query = context.getParameter("q");
        ICategoryDataService service = Context.getService(ICategoryDataService.class);
        PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);

        List<Category> categories;
        if (StringUtils.isBlank(query)) {
            categories = service.getAll(context.getIncludeAll(), pagingInfo);
        } else {
            categories = service.findByName(query, context.getIncludeAll(), pagingInfo);
        }

        AlreadyPagedWithLength<Category> results = new AlreadyPagedWithLength<Category>(context, categories, pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
        return results;
    }
}
