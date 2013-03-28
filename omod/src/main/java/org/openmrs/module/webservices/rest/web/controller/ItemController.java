/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.resource.ItemResource;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/rest/item")
public class ItemController extends BaseCrudController<ItemResource> {
	/**
	 * @param query
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = {"q", "department_uuid"})
	@ResponseBody
	public SimpleObject search(@RequestParam("q") String query, @RequestParam("department_uuid") String department_uuid,
	                           HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		ItemResource itemResource;
		try {
			itemResource = (ItemResource) getResource();
		}
		catch (ClassCastException ex) {
			throw new ResourceDoesNotSupportOperationException(getResource().getClass().getSimpleName()
			        + " is not Searchable", null);
		}
		RequestContext context = RestUtil.getRequestContext(request, Representation.REF);
		if (query.isEmpty())
			return itemResource.searchByDepartment(department_uuid, context);
		else if (department_uuid.isEmpty())
			return itemResource.search(query, context);
		else
			return itemResource.search(query, department_uuid, context);
	}
}

