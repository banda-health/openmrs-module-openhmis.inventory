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
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTransaction;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface that represents classes which perform data operations for {@link StockOperationTransaction}s.
 */
@Transactional
public interface IStockOperationTransactionDataService extends IObjectDataService<StockOperationTransaction> {

	/**
	 * Gets all {@link StockOperationTransaction}'s for the specified {@link StockOperation}.
	 * @param operation The {@link StockOperation}.
	 * @param paging The paging information.
	 * @return A list containing all of the operation transactions.
	 * @should return all the transactions of the peration ordered by the transaction date
	 * @should return an empty list if there are no transactions
	 * @should return paged items if paging is specified
	 * @should throw IllegalArgumentException if the stockroom is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	List<StockOperationTransaction> getTransactionByOperation(StockOperation operation, PagingInfo paging);
}
