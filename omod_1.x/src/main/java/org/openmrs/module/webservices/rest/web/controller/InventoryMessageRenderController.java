package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.module.openhmis.backboneforms.web.BackboneWebConstants;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.ResourceBundle;

@Controller
@RequestMapping(ModuleWebConstants.MESSAGE_PROPERTIES_JS_URI)
public class InventoryMessageRenderController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView MessageRenderController(HttpServletRequest request) {
		Locale locale = RequestContextUtils.getLocale(request);
		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
		return new ModelAndView(ModuleWebConstants.MESSAGE_PAGE, "keys", resourceBundle.getKeys());
	}
}
