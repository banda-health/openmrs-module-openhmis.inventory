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

import org.openmrs.api.context.Context;
import org.openmrs.module.jasperreport.JasperReportService;
import org.openmrs.module.openhmis.inventory.ModuleSettings;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.model.Settings;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
@RequestMapping(ModuleWebConstants.INVENTORY_REPORTS_ROOT)
public class InventoryReportsController {
	@RequestMapping(method = RequestMethod.GET)
	public void inventory(ModelMap model) throws IOException {
		Settings settings = ModuleSettings.loadSettings();
		JasperReportService reportService = Context.getService(JasperReportService.class);
		IStockroomDataService stockroomDataService = Context.getService(IStockroomDataService.class);

		Integer reportId = settings.getStockTakeReportId();
		if (reportId != null) {
			model.addAttribute("stockTakeReport", reportService.getJasperReport(reportId));
		}

		reportId = settings.getStockCardReportId();
		if (reportId != null) {
			model.addAttribute("stockCardReport", reportService.getJasperReport(reportId));
		}

		model.addAttribute("stockrooms", stockroomDataService.getAll());
	}
}
