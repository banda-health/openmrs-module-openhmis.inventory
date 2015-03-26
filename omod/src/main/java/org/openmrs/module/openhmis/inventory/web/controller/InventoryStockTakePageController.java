package org.openmrs.module.openhmis.inventory.web.controller;

import java.io.IOException;

import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.openmrs.module.openhmis.inventory.web.controller.util.Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(ModuleWebConstants.INVENTORY_STOCK_TAKE_ROOT)
public class InventoryStockTakePageController {
	@RequestMapping(method = RequestMethod.GET)
	public void inventory(ModelMap model) throws IOException {

		model.addAttribute("showStockTakeLink", Util.userCanPerformAdjustmentOperation(Context.getAuthenticatedUser()));
	}
}
