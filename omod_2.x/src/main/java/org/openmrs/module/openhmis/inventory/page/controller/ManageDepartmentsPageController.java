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
package org.openmrs.module.openhmis.inventory.page.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.api.IDepartmentDataService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;

/**
 * TODO, no need of this if using angular since angular needs no controllers
 */
public class ManageDepartmentsPageController {
	public void controller(PageModel model, UiUtils ui) {
		model.addAttribute("departments", Context.getService(IDepartmentDataService.class).getAllDepartmentsSorted(false));
	}
}
