package org.openmrs.module.openhmis.inventory.api;

import java.util.List;

import org.openmrs.annotation.Authorized;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemConceptSuggestion;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

public interface IItemConceptSuggestionDataService extends IMetadataDataService<ItemConceptSuggestion> {

	/**
	 * Gets all itemConceptSuggestions .
	 * @return The itemConceptSuggestions found or an empty list if no items were found.
	 * @should only consider items where conceptAccepted is false
	 * @should find concepts that are like the item name
	 * @should return null for concept if no matching concept was found 
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_ITEMS})
    List<ItemConceptSuggestion> getItemsWithConceptSuggestions();
}
