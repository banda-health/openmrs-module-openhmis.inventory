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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.openmrs.web.taglib.fieldgen.FieldGenHandlerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/fieldgenhandlers.json")
public class FieldGenHandlerController {
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Set<String>> fieldgenhandlers() {
		Map<String, Set<String>> container = new HashMap<String, Set<String>>();
		container.put("results", new TreeSet<String>(FieldGenHandlerFactory.getSingletonInstance().getHandlers().keySet()));
		return container;
	}
}
