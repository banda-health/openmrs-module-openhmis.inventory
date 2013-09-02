/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.1 (the "License"); you may not use this file except in
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
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction;

public class StockRoomTransactionSearch extends BaseObjectTemplateSearch<StockRoomTransactionTemplate> {
	public StockRoomTransactionSearch() {
		this(new StockRoomTransactionTemplate());
	}

	public StockRoomTransactionSearch(StockRoomTransactionTemplate template) {
		super(template);
	}

	private StringComparisonType transactionNumberComparisonType;
	private ComparisonType sourceComparisonType;
	private ComparisonType destinationComparisonType;
	private DateComparisonType dateCreatedComparisonType;

	public void setTransactionNumberComparisonType(StringComparisonType transactionNumberComparisonType) {
		this.transactionNumberComparisonType = transactionNumberComparisonType;
	}

	public StringComparisonType getTransactionNumberComparisonType() {
		return transactionNumberComparisonType;
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

	@Override
	public void updateCriteria(Criteria criteria) {
		super.updateCriteria(criteria);

		StockRoomTransaction tx = getTemplate();
		if (tx.getTransactionNumber() != null) {
			criteria.add(createCriterion("transactionNumber", tx.getTransactionNumber(), transactionNumberComparisonType));
		}
		if (tx.getTransactionType() != null) {
			criteria.add(Restrictions.eq("transactionType", tx.getTransactionType()));
		}
		if (tx.getStatus() != null) {
			criteria.add(Restrictions.eq("status", tx.getStatus()));
		}
		if (tx.getSource() != null ||
				(sourceComparisonType != null && sourceComparisonType != ComparisonType.EQUAL)) {
			criteria.add(createCriterion("source", tx.getSource(), sourceComparisonType));
		}
		if (tx.getDestination() != null ||
				(destinationComparisonType != null && destinationComparisonType != ComparisonType.EQUAL)) {
			criteria.add(createCriterion("destination", tx.getDestination(), destinationComparisonType));
		}
		if (tx.getDateCreated() != null ||
				(dateCreatedComparisonType != null && dateCreatedComparisonType != DateComparisonType.EQUAL)) {
			criteria.add(createCriterion("dateCreated", tx.getDateCreated(), dateCreatedComparisonType));
		}
		if (tx.isImportTransaction() != null) {
			criteria.add(Restrictions.eq("importTransaction", tx.isImportTransaction()));
		}
	}
}
