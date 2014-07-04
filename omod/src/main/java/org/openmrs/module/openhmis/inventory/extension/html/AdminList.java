/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
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
package org.openmrs.module.openhmis.inventory.extension.html;

import org.openmrs.module.Extension;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.openmrs.module.web.extension.AdministrationSectionExt;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdminList extends AdministrationSectionExt {
	/**
	 * @see AdministrationSectionExt#getMediaType()
	 */
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}

	/**
	 * @see AdministrationSectionExt#getTitle()
	 */
	public String getTitle() {
		return "openhmis.inventory.title";
	}

	/**
	 * @see AdministrationSectionExt#getLinks()
	 */
	public Map<String, String> getLinks() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put(ModuleWebConstants.ITEMS_PAGE, "openhmis.inventory.admin.items");
		map.put(ModuleWebConstants.ITEMS_TO_CONCEPT_MAPPING_PAGE, "openhmis.inventory.admin.items.concept.mapping");
		map.put(ModuleWebConstants.DEPARTMENTS_PAGE, "openhmis.inventory.admin.departments");
		map.put(ModuleWebConstants.CATEGORIES_PAGE, "openhmis.inventory.admin.categories");
		map.put(ModuleWebConstants.INSTITUTIONS_PAGE, "openhmis.inventory.admin.institutions");
		map.put(ModuleWebConstants.STOCKROOMS_PAGE, "openhmis.inventory.admin.stockrooms");
		map.put(ModuleWebConstants.OPERATION_TYPES_PAGE, "openhmis.inventory.admin.operationTypes");

		return map;
	}
}
