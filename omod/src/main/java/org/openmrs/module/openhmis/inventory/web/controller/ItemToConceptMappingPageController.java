/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.web.controller;
import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.openmrs.Concept;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.web.ModuleWebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller(value="invItemToConceptMappingPageController")
public class ItemToConceptMappingPageController {

    private IItemDataService itemDataService;

    @Autowired
    public ItemToConceptMappingPageController(IItemDataService itemDataService) {
    	this.itemDataService = itemDataService;
    }

    @RequestMapping(value=ModuleWebConstants.ITEM_TO_CONCEPT_MAPPING_ROOT, method = RequestMethod.GET)
    public void itemToConceptMapping(ModelMap model) throws JsonGenerationException, JsonMappingException, IOException {
        Map<Item, Concept> itemsWithConceptSuggestions = itemDataService.getItemsWithConceptSuggestions();
        model.addAttribute("itemConcepts", itemsWithConceptSuggestions);
        model.addAttribute("modelBase", "openhmis.inventory.institution");
    }

    @RequestMapping(value=ModuleWebConstants.ITEM_TO_CONCEPT_MAPPING_ROOT + "/getConcept", method = RequestMethod.GET)
    public void getConcepts() {
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX GET STUFF");
    }

    @RequestMapping(value=ModuleWebConstants.ITEM_TO_CONCEPT_MAPPING_ROOT, method = RequestMethod.POST)
    public void onSubmit(ModelMap model) {
        System.out.println("##################### FORM SUBMIT");
    }

}

