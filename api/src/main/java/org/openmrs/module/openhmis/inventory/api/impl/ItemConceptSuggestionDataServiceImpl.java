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
package org.openmrs.module.openhmis.inventory.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.ConceptNameType;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseMetadataDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.entity.security.IMetadataAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.IItemConceptSuggestionDataService;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemConceptSuggestion;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data service implementation class for {@link ItemConceptSuggestion}s.
 */
@Transactional
public class ItemConceptSuggestionDataServiceImpl extends BaseMetadataDataServiceImpl<ItemConceptSuggestion>
        implements IItemConceptSuggestionDataService {
	private static final int DEFAULT_RESULT_LIMIT = 50;

	private IItemDataService itemDataService;

	@Override
	protected IMetadataAuthorizationPrivileges getPrivileges() {
		return null;
	}

	@Override
	protected void validate(ItemConceptSuggestion object) {
		return;
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_ITEMS })
	public List<ItemConceptSuggestion> getItemsWithConceptSuggestions() {
		int resultLimit = DEFAULT_RESULT_LIMIT;
		List<ItemConceptSuggestion> itemConceptSuggestions = getItemToConceptMatches(resultLimit);

		List<Integer> excludedItemsIds = new ArrayList<Integer>();
		for (ItemConceptSuggestion suggestion : itemConceptSuggestions) {
			excludedItemsIds.add(suggestion.getItemId());
		}

		if (itemConceptSuggestions.size() < resultLimit) {
			int reachDefaultReultLimit = resultLimit - itemConceptSuggestions.size();
			List<Item> itemsWithoutConcept =
			        itemDataService.getItemsWithoutConcept(excludedItemsIds, reachDefaultReultLimit);
			for (Item item : itemsWithoutConcept) {
				ItemConceptSuggestion itemConceptSuggestion = new ItemConceptSuggestion(item, null, null, false);
				itemConceptSuggestions.add(itemConceptSuggestion);
			}
		}
		return itemConceptSuggestions;
	}

	public List<ItemConceptSuggestion> getItemToConceptMatches(Integer resultLimit) {
		String itemKey = "0";
		String conceptNameKey = "1";
		String conceptUuidKey = "2";

		List<ItemConceptSuggestion> itemToConceptMatches = new ArrayList<ItemConceptSuggestion>();
		String queryString =
		        "select new map(item, MAX(concept_name.name), MAX(concept.uuid)) " + "from " + Item.class.getName()
		                + " as item, " + ConceptName.class.getName() + " as concept_name, " + Concept.class.getName()
		                + " as concept " + "where item.concept is null " + "and item.retired = false "
		                + "and item.conceptAccepted = false " + "and concept.retired = false "
		                + "and concept_name.concept = concept.conceptId "
		                + "and concept_name.conceptNameType != :conceptNameType "
		                + "and item.name like concat(concept_name.name, '%') " + "group by item.id ";
		Query query = getRepository().createQuery(queryString);
		query.setString("conceptNameType", ConceptNameType.SHORT.toString());
		query.setMaxResults(resultLimit);
		List<Map<String, Object>> results = (List<Map<String, Object>>)query.list();
		for (Map<String, Object> result : results) {
			Item item = (Item)result.get(itemKey);
			String conceptName = (String)result.get(conceptNameKey);
			String conceptUuid = (String)result.get(conceptUuidKey);
			ItemConceptSuggestion itemConceptSuggestion = new ItemConceptSuggestion(item, conceptName, conceptUuid, false);
			itemToConceptMatches.add(itemConceptSuggestion);
		}
		return itemToConceptMatches;
	}

	public void setItemDataService(IItemDataService itemDataService) {
		this.itemDataService = itemDataService;
	}
}
