package org.openmrs.module.openhmis.inventory.web.controller;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


//The non-standard controller name is to avoid name conflicts with old versions of the cashier module
@Controller(value="invItemToConstantDrugMappingPageController")
@RequestMapping(ModuleWebConstants.ITEMS_TO_DRUG_CONCEPT_MAPPING_ROOT)
public class ConceptDrugItemMappingPageController {
    @RequestMapping(method = RequestMethod.GET)
    public void itemsDrugConceptMapping(ModelMap model) throws JsonGenerationException, JsonMappingException, IOException {
        model.addAttribute("modelBase", "openhmis.inventory.institution");
    }
}
