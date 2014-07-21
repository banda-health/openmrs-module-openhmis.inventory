package org.openmrs.module.openhmis.inventory.api;

import java.util.List;

import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemConceptSuggestion;

public interface IItemConceptSuggestionDataService extends IMetadataDataService<ItemConceptSuggestion> {

    List<ItemConceptSuggestion> getItemsWithConceptSuggestions();
}
