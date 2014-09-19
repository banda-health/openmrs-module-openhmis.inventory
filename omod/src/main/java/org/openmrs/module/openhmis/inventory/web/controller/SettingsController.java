package org.openmrs.module.openhmis.inventory.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.openhmis.commons.model.RoleCreationViewModel;
import org.openmrs.module.openhmis.inventory.ModuleSettings;
import org.openmrs.module.openhmis.inventory.api.model.Settings;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller(value="invSettingsController")
@RequestMapping(ModuleWebConstants.SETTINGS_ROOT)
public class SettingsController {
	@RequestMapping(method = RequestMethod.GET)
	public void render(ModelMap model) throws IOException {
		model.addAttribute("settings", ModuleSettings.loadSettings());

		IdentifierSourceService service = Context.getService(IdentifierSourceService.class);
		model.addAttribute("sources", service.getAllIdentifierSources(false));
	}

	@RequestMapping(method = RequestMethod.POST)
	public void submit(HttpServletRequest request, Settings settings, Errors errors, ModelMap model) {
		ModuleSettings.saveSettings(settings);

		HttpSession session = request.getSession();
		session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "openhmis.inventory.settings.saved");
	}
}
