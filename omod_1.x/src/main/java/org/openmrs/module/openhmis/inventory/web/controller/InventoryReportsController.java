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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.jasperreport.JasperReportService;
import org.openmrs.module.openhmis.commons.api.exception.ReportNotFoundException;
import org.openmrs.module.openhmis.inventory.ModuleSettings;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.WellKnownOperationTypes;
import org.openmrs.module.openhmis.inventory.api.model.Settings;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for the inventory reports page.
 */
@Controller
@RequestMapping(ModuleWebConstants.INVENTORY_REPORTS_ROOT)
public class InventoryReportsController {

	private static final Log LOG = LogFactory.getLog(InventoryReportsController.class);

	@RequestMapping(method = RequestMethod.GET)
	public void inventory(ModelMap model) throws IOException {
		Settings settings = ModuleSettings.loadSettings();
		IStockroomDataService stockroomDataService = Context.getService(IStockroomDataService.class);

		Integer reportId = settings.getStockTakeReportId();
		if (reportId != null) {
			handleReport(model, reportId, "stockTakeReport");
		}

		reportId = settings.getStockCardReportId();
		if (reportId != null) {
			handleReport(model, reportId, "stockCardReport");
		}

		reportId = settings.getStockOperationsByStockroomReportId();
		if (reportId != null) {
			handleReport(model, reportId, "stockOperationsByStockroomReport");
		}

		reportId = settings.getStockroomReportId();
		if (reportId != null) {
			handleReport(model, reportId, "stockroomReport");
		}

		reportId = settings.getExpiringStockReportId();
		if (reportId != null) {
			handleReport(model, reportId, "expiringStockReport");
		}

		model.addAttribute("showStockTakeLink", Context.getAuthenticatedUser() != null
		        && WellKnownOperationTypes.getAdjustment().userCanProcess(Context.getAuthenticatedUser()));
		model.addAttribute("stockrooms", stockroomDataService.getAll());
	}

	private void handleReport(ModelMap model, Integer reportId, String reportName) {
		JasperReportService reportService = Context.getService(JasperReportService.class);
		try {
			model.addAttribute(reportName, reportService.getJasperReport(reportId));
		} catch (NullPointerException e) {
			LOG.error("report with ID <" + reportId + "> not found", e);
			throw new ReportNotFoundException("The report could not be found. Check configuration under Inventory Settings");
		}
	}
}
