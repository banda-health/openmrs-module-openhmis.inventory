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
import org.openmrs.module.openhmis.commons.api.entity.search.BaseObjectTemplateSearch;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;

/**
 * A search template class for the {@link StockOperation} model.
 */
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
	private ComparisonType patientComparisonType;
	private ComparisonType institutionComparisonType;
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
		return patientComparisonType;
	}

	public void setPatientComparisonType(ComparisonType patientComparisonType) {
		this.patientComparisonType = patientComparisonType;
	}

	public ComparisonType getInstitutionComparisonType() {
		return institutionComparisonType;
	}

	public void setInstitutionComparisonType(ComparisonType institutionComparisonType) {
		this.institutionComparisonType = institutionComparisonType;
	}

	@Override
	public void updateCriteria(Criteria criteria) {
		super.updateCriteria(criteria);

		StockOperationTemplate operation = getTemplate();
		if (operation.getItem() != null) {
			criteria.createAlias("items", "items").add(Restrictions.eq("items.item", operation.getItem()));
		}
		if (operation.getOperationNumber() != null) {
			criteria.add(createCriterion("operationNumber", operation.getOperationNumber(), operationNumberComparisonType));
		}
		if (operation.getInstanceType() != null) {
			criteria.add(Restrictions.eq("instanceType", operation.getInstanceType()));
		}
		if (operation.getStatus() != null) {
			criteria.add(Restrictions.eq("status", operation.getStatus()));
		}
		if (operation.getSource() != null || (sourceComparisonType != null
		        && sourceComparisonType != ComparisonType.EQUAL)) {
			criteria.add(createCriterion("source", operation.getSource(), sourceComparisonType));
		}
		if (operation.getDestination() != null
		        || (destinationComparisonType != null && destinationComparisonType != ComparisonType.EQUAL)) {
			criteria.add(createCriterion("destination", operation.getDestination(), destinationComparisonType));
		}
		if (operation.getPatient() != null
		        || (patientComparisonType != null && patientComparisonType != ComparisonType.EQUAL)) {
			criteria.add(createCriterion("patient", operation.getPatient(), patientComparisonType));
		}
		if (operation.getInstitution() != null
		        || (institutionComparisonType != null && institutionComparisonType != ComparisonType.EQUAL)) {
			criteria.add(createCriterion("institution", operation.getInstitution(), institutionComparisonType));
		}
		if (operation.getDateCreated() != null
		        || (dateCreatedComparisonType != null && dateCreatedComparisonType != DateComparisonType.EQUAL)) {
			criteria.add(createCriterion("dateCreated", operation.getDateCreated(), dateCreatedComparisonType));
		}
		if (operation.getStockroom() != null) {
			criteria.add(Restrictions.or(Restrictions.eq("source", operation.getStockroom()),
			    Restrictions.eq("destination", operation.getStockroom())));
		}
	}
}
