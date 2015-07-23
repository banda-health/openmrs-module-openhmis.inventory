package org.openmrs.module.openhmis.inventory.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.api.context.Context;
import org.openmrs.module.jasperreport.JasperReportService;
import org.openmrs.module.openhmis.commons.api.util.ModuleUtil;
import org.openmrs.module.openhmis.commons.api.util.SafeIdgenUtil;
import org.openmrs.module.openhmis.inventory.ModuleSettings;
import org.openmrs.module.openhmis.inventory.api.model.Settings;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller(value="invSettingsController")
@RequestMapping(ModuleWebConstants.SETTINGS_ROOT)
public class SettingsController {
	@RequestMapping(method = RequestMethod.GET)
	public void render(ModelMap model) throws IOException {
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
	}

	@RequestMapping(method = RequestMethod.POST)
	public void submit(HttpServletRequest request, Settings settings, Errors errors, ModelMap model) throws IOException {
		ModuleSettings.saveSettings(settings);

		HttpSession session = request.getSession();
		session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "openhmis.inventory.settings.saved");

		render(model);
	}
}
