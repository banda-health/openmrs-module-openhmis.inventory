package org.openmrs.module.webservices.rest.search;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.inventory.api.IDepartmentDataService;
import org.openmrs.module.openhmis.inventory.api.model.Department;
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
public class DepartmentSearchHandler implements SearchHandler {

    private final SearchConfig searchConfig = new SearchConfig("default", ModuleRestConstants.DEPARTMENT_RESOURCE, Arrays.asList("1.9.*"),
            Arrays.asList(
                    new SearchQuery.Builder("Find a department by its name")
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
        IDepartmentDataService service = Context.getService(IDepartmentDataService.class);
        PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);

        List<Department> departments;
        if (StringUtils.isBlank(query)) {
            departments = service.getAll(context.getIncludeAll(), pagingInfo);
        } else {
            departments = service.findByName(query, context.getIncludeAll(), pagingInfo);
        }

        AlreadyPagedWithLength<Department> results = new AlreadyPagedWithLength<Department>(context, departments, pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
        return results;
    }

}
