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
 *
 */

package org.openmrs.module.openhmis.inventory.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.jasperreport.JasperReportService;
import org.openmrs.module.openhmis.commons.api.util.ModuleUtil;
import org.openmrs.module.openhmis.commons.api.util.SafeIdgenUtil;
import org.openmrs.module.openhmis.commons.web.controller.HeaderController;
import org.openmrs.module.openhmis.inventory.ModuleSettings;
import org.openmrs.module.openhmis.inventory.api.model.Settings;
import org.openmrs.web.WebConstants;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Base Controller for the Inventory Settings 2.x page.
 */
public abstract class SettingsControllerBase {
	@RequestMapping(method = RequestMethod.GET)
	public void render(ModelMap model, HttpServletRequest request) throws IOException {
		if (ModuleUtil.isLoaded(ModuleUtil.IDGEN_MODULE_ID)) {
			model.addAttribute("hasIdgenModule", true);
			model.addAttribute("sources", SafeIdgenUtil.getAllIdentifierSourceInfo());
		} else {
			model.addAttribute("hasIdgenModule", false);
			model.addAttribute("sources", null);
		}

		JasperReportService reportService = Context.getService(JasperReportService.class);
		model.addAttribute("reports", reportService.getJasperReports());
		model.addAttribute("settings", ModuleSettings.loadSettings());

		HeaderController.render(model, request);

	}

	@RequestMapping(method = RequestMethod.POST)
	public void submit(HttpServletRequest request, Settings settings, Errors errors, ModelMap model) throws IOException {
		ModuleSettings.saveSettings(settings);

		HttpSession session = request.getSession();
		session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "openhmis.inventory.settings.saved");

		render(model, request);
	}
}
