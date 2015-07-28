package org.openmrs.module.openhmis.inventory.web.controller;

import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller @RequestMapping(value = ModuleWebConstants.INVENTORY_REPORT_ERROR_PAGE) public class ReportErrorController {

	@RequestMapping(method = RequestMethod.GET) public ModelAndView ReportError(Integer reportId) {
		ModelAndView modelAndView = new ModelAndView();
		/*modelAndView.addObject("reportName", reportName);*/
		modelAndView.addObject("reportId", reportId);
		modelAndView.setViewName(ModuleWebConstants.INVENTORY_REPORT_ERROR_ROOT);
		return modelAndView;
	}

}


