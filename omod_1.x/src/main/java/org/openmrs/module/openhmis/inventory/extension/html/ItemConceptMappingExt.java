/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.extension.html;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemCode;

/**
 * Extension to display item information on the concept page. Not currently enabled as this extension has not yet been
 * created on the concept page.
 */
public class ItemConceptMappingExt extends Extension {

	private String conceptId;
	private IItemDataService itemDataService;
	private ConceptService conceptService;

	@Override
	public MEDIA_TYPE getMediaType() {
		return MEDIA_TYPE.html;
	}

	@Override
	public void initialize(Map<String, String> parameters) {
		conceptId = parameters.get("conceptId");
		conceptService = Context.getConceptService();
		itemDataService = Context.getService(IItemDataService.class);
	}

	@Override
	public String getOverrideContent(String bodyContent) {
		String tableRow = "<script type='text/javascript'>"
		        + "$j(document).ready(function() {"
		        + "$j('#conceptTable > tbody:last').append('<tr><th>Items</th>";

		if (conceptId != null) {
			Concept concept = conceptService.getConcept(Integer
			        .valueOf(conceptId));
			List<Item> items = itemDataService.getItemsByConcept(concept);
			tableRow += "<td>";
			if (items.size() > 0) {
				for (Item item : items) {
					Set<ItemCode> codes = item.getCodes();
					tableRow += item.getName();
					if (codes.size() > 0) {
						tableRow += " - ItemCode: " + codes.iterator().next().getCode();
					}
					tableRow += "<br>";
				}
			} else {
				tableRow += "No Items";
			}
			tableRow += "</td>";
		}

		tableRow += "</tr>');})</script>";

		return tableRow;
	}

}
