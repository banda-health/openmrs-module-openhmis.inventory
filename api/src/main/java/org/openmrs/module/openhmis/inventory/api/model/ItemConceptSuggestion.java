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

import org.openmrs.BaseOpenmrsMetadata;

/**
 * Model class that represents an item to concept mapping suggestion.
 */
public class ItemConceptSuggestion extends BaseOpenmrsMetadata {

	private static final long serialVersionUID = 0L;

	private Item item;
	private String conceptName;
	private String conceptUuid;
	private boolean conceptAccepted;

	public ItemConceptSuggestion() {}

	public ItemConceptSuggestion(Item item, String conceptName, String conceptUuid, boolean conceptAccepted) {
		this.item = item;
		this.conceptName = conceptName;
		this.conceptUuid = conceptUuid;
		this.conceptAccepted = conceptAccepted;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public String getConceptName() {
		return conceptName;
	}

	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}

	public String getConceptUuid() {
		return conceptUuid;
	}

	public void setConceptUuid(String conceptUuid) {
		this.conceptUuid = conceptUuid;
	}

	public Boolean getConceptAccepted() {
		return conceptAccepted;
	}

	public boolean isConceptAccepted() {
		return conceptAccepted;
	}

	public void setConceptAccepted(Boolean conceptAccepted) {
		this.conceptAccepted = conceptAccepted == null ? false : conceptAccepted;
	}

	public Integer getItemId() {
		return item.getId();
	}

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public void setId(Integer id) {

	}

}
