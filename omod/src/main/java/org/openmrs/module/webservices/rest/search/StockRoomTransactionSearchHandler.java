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
import org.openmrs.module.openhmis.inventory.api.IStockRoomDataService;
import org.openmrs.module.openhmis.inventory.api.IStockRoomTransactionDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockRoom;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction;
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
public class StockRoomTransactionSearchHandler implements SearchHandler {
	protected Log log = LogFactory.getLog(getClass());

	private final SearchConfig searchConfig = new SearchConfig("default", ModuleRestConstants.TRANSACTION_RESOURCE,
			Arrays.asList("1.9.*"),
			Arrays.asList(
					new SearchQuery.Builder("Find all transactions by stock room.")
							.withRequiredParameters("stock_room_uuid").build()
			)
	);

	private IStockRoomDataService stockRoomDataService;
	private IStockRoomTransactionDataService transactionDataService;

	@Autowired
	public StockRoomTransactionSearchHandler(IStockRoomDataService stockRoomDataService,
	                                         IStockRoomTransactionDataService transactionDataService) {
		this.stockRoomDataService = stockRoomDataService;
		this.transactionDataService = transactionDataService;
	}

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context) throws ResponseException {
		String stockRoomUuid = context.getParameter("stock_room_uuid");
		StockRoom stockRoom = stockRoomDataService.getByUuid(stockRoomUuid);
		if (stockRoom == null) {
			log.warn("Could not find stock room '" + stockRoomUuid + "'");

			return new EmptySearchResult();
		}

		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
		List<StockRoomTransaction> transactions = transactionDataService.getTransactionsByRoom(stockRoom, pagingInfo);

		if (transactions == null || transactions.size() == 0) {
			return new EmptySearchResult();
		} else {
			return new AlreadyPagedWithLength<StockRoomTransaction>(context, transactions,
					pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
		}
	}
}
