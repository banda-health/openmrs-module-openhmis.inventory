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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.impl.BaseObjectDataServiceImpl;
import org.openmrs.module.openhmis.commons.api.f.Action1;
import org.openmrs.module.openhmis.inventory.api.IStockOperationTransactionDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTransaction;
import org.openmrs.module.openhmis.inventory.api.security.BasicObjectAuthorizationPrivileges;
import org.openmrs.module.openhmis.inventory.api.util.HibernateCriteriaConstants;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data service implementation class for {@link StockOperationTransaction}.
 */
public class StockOperationTransactionDataServiceImpl
        extends BaseObjectDataServiceImpl<StockOperationTransaction, BasicObjectAuthorizationPrivileges>
        implements IStockOperationTransactionDataService {

	@Override
	protected BasicObjectAuthorizationPrivileges getPrivileges() {
		return new BasicObjectAuthorizationPrivileges();
	}

	@Override
	protected void validate(StockOperationTransaction object) {}

	@Override
	protected Order[] getDefaultSort() {
		return new Order[] {
		        Order.desc(HibernateCriteriaConstants.DATE_CREATED),
		        Order.desc(HibernateCriteriaConstants.ID)
		};
	}

	@Override
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	public List<StockOperationTransaction> getTransactionByOperation(final StockOperation operation, PagingInfo paging) {
		if (operation == null) {
			throw new IllegalArgumentException("The operation must be defined");
		}

		return executeCriteria(StockOperationTransaction.class, paging, new Action1<Criteria>() {
			@Override
			public void apply(Criteria criteria) {
				criteria.add(Restrictions.eq(HibernateCriteriaConstants.OPERATION, operation));
			}
		}, Order.desc(HibernateCriteriaConstants.DATE_CREATED), Order.desc(HibernateCriteriaConstants.ID));
	}
}
