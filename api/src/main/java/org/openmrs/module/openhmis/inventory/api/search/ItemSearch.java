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
package org.openmrs.module.openhmis.inventory.api.search;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.openhmis.commons.api.entity.search.BaseMetadataTemplateSearch;
import org.openmrs.module.openhmis.inventory.api.model.Item;

/**
 * A search template class for the {@link Item} model.
 */
public class ItemSearch extends BaseMetadataTemplateSearch<Item> {
	public static final long serialVersionUID = 0L;

	public ItemSearch() {
		this(new Item(), StringComparisonType.EQUAL, false);
	}

	public ItemSearch(Item itemTemplate) {
		this(itemTemplate, StringComparisonType.EQUAL, false);
	}

	public ItemSearch(Item itemTemplate, Boolean includeRetired) {
		this(itemTemplate, StringComparisonType.EQUAL, includeRetired);
	}

	public ItemSearch(Item itemTemplate, StringComparisonType nameComparisonType, Boolean includeRetired) {
		super(itemTemplate, nameComparisonType, includeRetired);
	}

	private ComparisonType conceptComparisonType;

	public ComparisonType getConceptComparisonType() {
		return conceptComparisonType;
	}

	public void setConceptComparisonType(ComparisonType conceptComparisonType) {
		this.conceptComparisonType = conceptComparisonType;
	}

	@Override
	public void updateCriteria(Criteria criteria) {
		super.updateCriteria(criteria);

		Item item = getTemplate();
		if (item.getDepartment() != null) {
			criteria.add(Restrictions.eq("department", item.getDepartment()));
		}
		if (item.getConcept() != null || (conceptComparisonType != null && conceptComparisonType != ComparisonType.EQUAL)) {
			criteria.add(createCriterion("concept", item.getConcept(), conceptComparisonType));
		}
		if (item.getHasExpiration() != null) {
			criteria.add(Restrictions.eq("hasExpiration", item.getHasExpiration()));
		}
		if (item.getHasPhysicalInventory() != null) {
			criteria.add(Restrictions.eq("hasPhysicalInventory", item.getHasPhysicalInventory()));
		}
		if (item.getConceptAccepted() != null) {
			criteria.add(Restrictions.eq("conceptAccepted", item.getConceptAccepted()));
		}
	}
}
