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
package org.openmrs.module.openhmis.inventory.web.controller;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
@RequestMapping(ModuleWebConstants.ITEMS_ROOT)
public class ItemsController {
	@RequestMapping(method = RequestMethod.GET)
	public void items(ModelMap model) throws JsonGenerationException, JsonMappingException, IOException {
		model.addAttribute("modelBase", "openhmis.inventory.item");
		model.addAttribute("privileges", PrivilegeConstants.ITEM_PAGE_PRIVILEGES);
		model.addAttribute("itemPage", ModuleWebConstants.ITEMS_PAGE);
	}
}