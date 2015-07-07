package org.openmrs.module.openhmis.inventory.page.controller;

import java.io.IOException;
import java.util.List;

import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.stereotype.Controller;
import org.openmrs.annotation.OpenmrsProfile;

@Controller
@OpenmrsProfile(modules = { "uiframework:*.*" })
public class InventoryLandingPageController {

	/**
	 * Process requests to show the home page
	 *
	 * @param model
	 * @param appFrameworkService
	 * @param request
	 * @param ui
	 * @throws IOException
	 */
	public void get(PageModel model, @SpringBean("appFrameworkService") AppFrameworkService appFrameworkService,
			PageRequest request, UiUtils ui) throws IOException {
		List<Extension> extensions = appFrameworkService.getExtensionsForCurrentUser(ModuleWebConstants.LANDING_PAGE_EXTENSION_POINT_ID);
		model.addAttribute("extensions", extensions);
	}
}
