package org.openmrs.module.openhmis.inventory.web.controller;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller(value="invMyOperationsController")
@RequestMapping(ModuleWebConstants.MY_OPERATIONS_ROOT)
public class MyOperationsController {
	@RequestMapping(method = RequestMethod.GET)
	public void render(ModelMap model) throws JsonGenerationException, JsonMappingException, IOException {
		model.addAttribute("modelBase", "openhmis.inventory.operation");
	}
}
