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
package org.openmrs.module.openhmis.inventory.api;

import java.util.List;

import org.openmrs.annotation.Authorized;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemConceptSuggestion;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface that represents classes which perform data operations for {@link ItemConceptSuggestion}s.
 */
@Transactional(readOnly = true)
public interface IItemConceptSuggestionDataService extends IMetadataDataService<ItemConceptSuggestion> {

	/**
	 * Returns a list containing the item concept suggestions.
	 * @return A list containing the item concept suggestions or an empty list if no items were found.
	 * @should only consider items where conceptAccepted is false
	 * @should find concepts that are like the item name
	 * @should return null for concept if no matching concept was found
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_ITEMS })
	List<ItemConceptSuggestion> getItemsWithConceptSuggestions();
}
