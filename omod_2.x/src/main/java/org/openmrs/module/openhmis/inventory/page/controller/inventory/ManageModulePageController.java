package org.openmrs.module.openhmis.inventory.page.controller.inventory;

import java.io.IOException;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.appframework.domain.Extension;
import org.openmrs.module.appframework.service.AppFrameworkService;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.PageRequest;
import org.springframework.stereotype.Controller;

@Controller
public class ManageModulePageController {
	/**
	 * Process requests to show the home page
	 *
	 * @param model
	 * @param appFrameworkService
	 * @param request
	 * @param ui
	 * @throws IOException
	 */
	public void get(PageModel model, PageRequest request, UiUtils ui) throws IOException {
		AppFrameworkService appFrameworkService = Context.getService(AppFrameworkService.class);
		List<Extension> extensions = appFrameworkService.getExtensionsForCurrentUser(ModuleWebConstants.MANAGE_MODULE_PAGE_EXTENSION_POINT_ID);
		model.addAttribute("extensions", extensions);
	}
}
