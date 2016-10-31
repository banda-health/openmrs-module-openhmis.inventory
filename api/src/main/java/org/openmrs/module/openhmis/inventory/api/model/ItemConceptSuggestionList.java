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
package org.openmrs.module.openhmis.inventory.api.model;

import java.util.List;

import org.openmrs.module.openhmis.commons.api.entity.model.BaseSerializableOpenmrsMetadata;

/**
 * Model class that represents a list of {@link ItemConceptSuggestion}s.
 */
public class ItemConceptSuggestionList extends BaseSerializableOpenmrsMetadata {

	private static final long serialVersionUID = 0L;

	private List<ItemConceptSuggestion> itemConceptSuggestions;

	public List<ItemConceptSuggestion> getItemConceptSuggestions() {
		return itemConceptSuggestions;
	}

	public void setItemConceptSuggestions(List<ItemConceptSuggestion> itemConceptSuggestions) {
		this.itemConceptSuggestions = itemConceptSuggestions;
	}

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public void setId(Integer id) {

	}

}
