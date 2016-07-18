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
package org.openmrs.module.openhmis.inventory.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for the Generate Concept Mapping page.
 */
@Controller(value = "invItemConceptSuggestionPageController")
public class ItemConceptSuggestionPageController {

	private static final String ITEM_CONCEPT_SUGGESTION_BASE = "openhmis.inventory.itemConceptSuggestion";
	private static final String ITEM_CONCEPT_SUGGESTION_PAGE = "itemConceptSuggestion.form";
	private static final int REFEFFER_START_INDEX = 8;

	@RequestMapping(value = ModuleWebConstants.ITEM_CONCEPT_SUGGESTION_ROOT, method = RequestMethod.GET)
	public void render(ModelMap model, HttpServletRequest request) throws IOException {
		String returnUrl = getReturnUrl(request);

		model.addAttribute("returnUrl", returnUrl);
		model.addAttribute("modelBase", ITEM_CONCEPT_SUGGESTION_BASE);
	}

	private String getReturnUrl(HttpServletRequest request) {
		String returnUrl = ModuleWebConstants.INVENTORY_PAGE;
		String referer = request.getHeader("referer");
		if (!referer.contains(ITEM_CONCEPT_SUGGESTION_PAGE)) {
			int refererWithoutHostPrefixStartIndex = referer.indexOf('/', REFEFFER_START_INDEX);
			String refererWithoutHostPrefix = referer.substring(refererWithoutHostPrefixStartIndex);
			returnUrl = refererWithoutHostPrefix;
		}
		return returnUrl;
	}
}
