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
package org.openmrs.module.webservices.rest.search;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationTransactionDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTransaction;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.resource.AlreadyPagedWithLength;
import org.openmrs.module.webservices.rest.resource.PagingUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Search handler for {@link StockOperationTransaction}s.
 */
@Component
public class StockOperationTransactionSearchHandler implements SearchHandler {
	private static final Log LOG = LogFactory.getLog(ItemStockSearchHandler.class);

	private final SearchConfig searchConfig = new SearchConfig("default",
	        ModuleRestConstants.OPERATION_TRANSACTION_RESOURCE,
	        Arrays.asList("*"),
	        Arrays.asList(
	                new SearchQuery.Builder("Find all transactions by stockroom or operation.")
	                        .withOptionalParameters("stockroom_uuid", "operation_uuid").build()
	                )
	        );

	private IStockroomDataService stockroomDataService;
	private IStockOperationDataService stockOperationDataService;
	private IStockOperationTransactionDataService stockOperationTransactionDataService;

	@Autowired
	public StockOperationTransactionSearchHandler(IStockroomDataService stockroomDataService,
	    IStockOperationDataService stockOperationDataService,
	    IStockOperationTransactionDataService stockOperationTransactionDataService) {
		this.stockroomDataService = stockroomDataService;
		this.stockOperationDataService = stockOperationDataService;
		this.stockOperationTransactionDataService = stockOperationTransactionDataService;
	}

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context) {
		return doSearch(stockroomDataService, stockOperationTransactionDataService, stockOperationDataService, context);
	}

	public static PageableResult doSearch(IStockroomDataService stockroomService,
	        IStockOperationTransactionDataService transactionService,
	        IStockOperationDataService operationService, RequestContext context) {
		String stockroomUuid = context.getParameter("stockroom_uuid");
		String operationUuid = context.getParameter("operation_uuid");

		if (StringUtils.isEmpty(stockroomUuid) && StringUtils.isEmpty(operationUuid)) {
			LOG.warn("Either stockroomUuid or operationUuid must be defined");
			LOG.warn("Could not find stockroom '" + stockroomUuid + "'");
			return new EmptySearchResult();
		}

		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
		List<StockOperationTransaction> transactions = null;

		if (StringUtils.isNotEmpty(stockroomUuid)) {
			Stockroom stockroom = stockroomService.getByUuid(stockroomUuid);
			transactions = stockroomService.getTransactionsByRoom(stockroom, pagingInfo);
		}

		if (StringUtils.isNotEmpty(operationUuid)) {
			StockOperation operation = operationService.getByUuid(operationUuid);
			transactions = transactionService.getTransactionByOperation(operation, pagingInfo);
		}

		if (transactions == null || transactions.size() == 0) {
			return new EmptySearchResult();
		} else {
			return new AlreadyPagedWithLength<StockOperationTransaction>(context, transactions,
			        pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
		}
	}
}
