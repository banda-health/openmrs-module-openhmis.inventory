package org.openmrs.module.openhmis.inventory.extension.html;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;

public class ItemDrugMappingExt extends Extension {

    private String drugId;
    private IItemDataService itemDataService;
    private ConceptService conceptService;

    @Override
    public MEDIA_TYPE getMediaType() {
        return MEDIA_TYPE.html;
    }

    @Override
    public void initialize(Map<String, String> parameters) {
        drugId = parameters.get("drugId");
        conceptService = Context.getConceptService();
        itemDataService = Context.getService(IItemDataService.class);
    }

    @Override
    public String getOverrideContent(String bodyContent) {
        if (StringUtils.isBlank(drugId)) {
            return StringUtils.EMPTY;
        }

        String tableRow = "<script type='text/javascript'>" +
                          "$j(document).ready(function() {" +
                          "$j('#table > tbody:last').append('<tr><th>Items</th>";

        Drug drug = conceptService.getDrug(Integer.valueOf(drugId));
        List<Item> items = itemDataService.findItemsByDrug(drug);
        tableRow += "<td>";
        if(items.size() > 0) {
            for (Item item : items) {
                tableRow += item.getName() + " - ItemUuid: " + item.getUuid() + "<br>";
            }
        } else {
            tableRow += "No Items";
        }
        tableRow += "</td>";
        tableRow += "</tr>');})</script>";

        return tableRow;
    }

}
