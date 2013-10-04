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
package org.openmrs.module.webservices.rest.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.inventory.api.IStockRoomTransactionDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransactionStatus;
import org.openmrs.module.openhmis.inventory.api.search.StockRoomTransactionSearch;
import org.openmrs.module.openhmis.inventory.api.search.StockRoomTransactionTemplate;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.resource.AlreadyPagedWithLength;
import org.openmrs.module.webservices.rest.resource.PagingUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TransactionSearchHandler implements SearchHandler {
	protected Log log = LogFactory.getLog(getClass());

	private final SearchConfig searchConfig = new SearchConfig("trans", ModuleRestConstants.TRANSACTION_RESOURCE,
			Arrays.asList("1.9.*"),
			Arrays.asList(
					new SearchQuery.Builder("Find all transactions by optional status.")
							.withOptionalParameters("status").build()
			)
	);

	private IStockRoomTransactionDataService transactionDataService;

	@Autowired
	public TransactionSearchHandler(IStockRoomTransactionDataService transactionDataService) {
		this.transactionDataService = transactionDataService;
	}

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context) throws ResponseException {
		String statusText = context.getParameter("status");
		StockRoomTransactionStatus status = StockRoomTransactionStatus.valueOf(statusText);

		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
		StockRoomTransactionSearch search = new StockRoomTransactionSearch(new StockRoomTransactionTemplate());
		search.getTemplate().setStatus(status);
		List<StockRoomTransaction> transactions = transactionDataService.findTransactions(search, pagingInfo);

		if (transactions == null || transactions.size() == 0) {
			return new EmptySearchResult();
		} else {
			return new AlreadyPagedWithLength<StockRoomTransaction>(context, transactions,
					pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
		}
	}
}
