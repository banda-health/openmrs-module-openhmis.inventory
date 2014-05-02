/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.api.search;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.openhmis.commons.api.entity.search.BaseObjectTemplateSearch;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;

public class StockOperationSearch extends BaseObjectTemplateSearch<StockOperationTemplate> {
	public StockOperationSearch() {
		this(new StockOperationTemplate());
	}

	public StockOperationSearch(StockOperationTemplate template) {
		super(template);
	}

	private StringComparisonType operationNumberComparisonType;
	private ComparisonType sourceComparisonType;
	private ComparisonType destinationComparisonType;
	private ComparisonType recipientComparisonType;
	private DateComparisonType dateCreatedComparisonType;

	public void setOperationNumberComparisonType(StringComparisonType operationNumberComparisonType) {
		this.operationNumberComparisonType = operationNumberComparisonType;
	}

	public StringComparisonType getOperationNumberComparisonType() {
		return operationNumberComparisonType;
	}

	public ComparisonType getSourceComparisonType() {
		return sourceComparisonType;
	}

	public void setSourceComparisonType(ComparisonType sourceComparisonType) {
		this.sourceComparisonType = sourceComparisonType;
	}

	public ComparisonType getDestinationComparisonType() {
		return destinationComparisonType;
	}

	public void setDestinationComparisonType(ComparisonType destinationComparisonType) {
		this.destinationComparisonType = destinationComparisonType;
	}

	public DateComparisonType getDateCreatedComparisonType() {
		return dateCreatedComparisonType;
	}

	public void setDateCreatedComparisonType(DateComparisonType dateCreatedComparisonType) {
		this.dateCreatedComparisonType = dateCreatedComparisonType;
	}

	public ComparisonType getPatientComparisonType() {
		return recipientComparisonType;
	}

	public void setPatientComparisonType(ComparisonType patientComparisonType) {
		this.recipientComparisonType = patientComparisonType;
	}

	@Override
	public void updateCriteria(Criteria criteria) {
		super.updateCriteria(criteria);

		StockOperation operation = getTemplate();
		if (operation.getOperationNumber() != null) {
			criteria.add(createCriterion("operationNumber", operation.getOperationNumber(), operationNumberComparisonType));
		}
		if (operation.getInstanceType() != null) {
			criteria.add(Restrictions.eq("instanceType", operation.getInstanceType()));
		}
		if (operation.getStatus() != null) {
			criteria.add(Restrictions.eq("status", operation.getStatus()));
		}
		if (operation.getSource() != null ||
				(sourceComparisonType != null && sourceComparisonType != ComparisonType.EQUAL)) {
			criteria.add(createCriterion("source", operation.getSource(), sourceComparisonType));
		}
		if (operation.getDestination() != null ||
				(destinationComparisonType != null && destinationComparisonType != ComparisonType.EQUAL)) {
			criteria.add(createCriterion("destination", operation.getDestination(), destinationComparisonType));
		}
		if (operation.getRecipient() != null ||
				(recipientComparisonType != null && recipientComparisonType != ComparisonType.EQUAL)) {
			criteria.add(createCriterion("recipient", operation.getRecipient(), recipientComparisonType));
		}
		if (operation.getDateCreated() != null ||
				(dateCreatedComparisonType != null && dateCreatedComparisonType != DateComparisonType.EQUAL)) {
			criteria.add(createCriterion("dateCreated", operation.getDateCreated(), dateCreatedComparisonType));
		}
	}
}
