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

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.inventory.api.IInstitutionDataService;
import org.openmrs.module.openhmis.inventory.api.model.Institution;
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
 * Search handler for {@link Institution}s.
 */
@Component
public class InstitutionSearchHandler implements SearchHandler {

	private final SearchConfig searchConfig = new SearchConfig("default", ModuleRestConstants.INSTITUTION_RESOURCE,
	        Arrays.asList("*"),
	        Arrays.asList(
	                new SearchQuery.Builder("Find a institution by its name")
	                        .withRequiredParameters("q")
	                        .build()
	                )
	        );

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context) {
		String query = context.getParameter("q");
		IInstitutionDataService service = Context.getService(IInstitutionDataService.class);
		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);

		List<Institution> institution;
		if (StringUtils.isBlank(query)) {
			institution = service.getAll(context.getIncludeAll(), pagingInfo);
		} else {
			institution = service.getByNameFragment(query, context.getIncludeAll(), pagingInfo);
		}

		AlreadyPagedWithLength<Institution> results =
		        new AlreadyPagedWithLength<Institution>(context, institution, pagingInfo.hasMoreResults(),
		                pagingInfo.getTotalRecordCount());
		return results;
	}

}
