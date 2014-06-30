package org.openmrs.module.openhmis.inventory.extension.html;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemCode;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
            List<Item> items = itemDataService.findItemsByConcept(concept);
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
