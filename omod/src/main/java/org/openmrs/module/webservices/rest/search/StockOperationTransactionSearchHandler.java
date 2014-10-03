package org.openmrs.module.webservices.rest.search;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
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

@Component
public class StockOperationTransactionSearchHandler implements SearchHandler {
	private static Log log = LogFactory.getLog(ItemStockSearchHandler.class);

	private final SearchConfig searchConfig = new SearchConfig("default", ModuleRestConstants.OPERATION_TRANSACTION_RESOURCE,
			Arrays.asList("1.9.*"),
			Arrays.asList(
					new SearchQuery.Builder("Find all transactions by stockroom.")
							.withRequiredParameters("stockroom_uuid").build()
			)
	);

	private IStockroomDataService stockroomDataService;

	@Autowired
	public StockOperationTransactionSearchHandler(IStockroomDataService stockroomDataService) {
		this.stockroomDataService = stockroomDataService;
	}

	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}

	@Override
	public PageableResult search(RequestContext context) {
		return doSearch(stockroomDataService, context);
	}

	public static PageableResult doSearch(IStockroomDataService service, RequestContext context) {
		String stockroomUuid = context.getParameter("stockroom_uuid");
		Stockroom stockroom = service.getByUuid(stockroomUuid);

		if (stockroom == null) {
			log.warn("Could not find stockroom '" + stockroomUuid + "'");

			return new EmptySearchResult();
		}

		PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
		List<StockOperationTransaction> transactions = service.getTransactionsByRoom(stockroom, pagingInfo);
		if (transactions == null || transactions.size() == 0) {
			return new EmptySearchResult();
		} else {
			return new AlreadyPagedWithLength<StockOperationTransaction>(context, transactions,
					pagingInfo.hasMoreResults(), pagingInfo.getTotalRecordCount());
		}
	}
}
