package org.openmrs.module.openhmis.inventory.web.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.ModuleSettings;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.helper.IdgenHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Type of a RestController to check up setting values in the Inventory Module Settings.
 */
@Controller(value = "moduleSettings")
@RequestMapping(ModuleWebConstants.MODULE_SETTINGS_ROOT)
public class ModuleSettingsController {

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public SimpleObject get(@RequestParam("setting") String setting) {
		SimpleObject results = new SimpleObject();
		if (StringUtils.isNotEmpty(setting)) {
			AdministrationService adminService = Context.getAdministrationService();
			String property = adminService.getGlobalProperty(setting);
			results.put("results", property);
		}
		return results;
	}
}
