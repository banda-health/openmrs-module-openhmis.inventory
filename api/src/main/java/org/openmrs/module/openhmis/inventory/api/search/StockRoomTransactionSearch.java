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
import org.openmrs.module.openhmis.commons.api.entity.search.BaseObjectTemplateSearch;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction;

public class StockRoomTransactionSearch extends BaseObjectTemplateSearch<StockRoomTransaction> {
	private StringComparisonType transactionNumberComparisonType;

	public StockRoomTransactionSearch(StockRoomTransaction template) {
		super(template);
	}

	@Override
	public void updateCriteria(Criteria criteria) {
		super.updateCriteria(criteria);

		StockRoomTransaction tx = getTemplate();
	}

	public void setTransactionNumberComparisonType(StringComparisonType transactionNumberComparisonType) {
		this.transactionNumberComparisonType = transactionNumberComparisonType;
	}

	public StringComparisonType getTransactionNumberComparisonType() {
		return transactionNumberComparisonType;
	}
}
