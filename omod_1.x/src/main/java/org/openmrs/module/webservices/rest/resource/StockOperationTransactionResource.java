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
package org.openmrs.module.webservices.rest.resource;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTransaction;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * REST resource representing a {@link StockOperationTransaction}.
 */
@Resource(name = ModuleRestConstants.OPERATION_TRANSACTION_RESOURCE, supportedClass = StockOperationTransaction.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*" })
public class StockOperationTransactionResource extends TransactionBaseResource<StockOperationTransaction> {

	private IStockroomDataService stockroomDataService;
	private IItemDataService itemDataService;

	public StockOperationTransactionResource() {
		this.stockroomDataService = Context.getService(IStockroomDataService.class);
		this.itemDataService = Context.getService(IItemDataService.class);
	}

	@Override
	public StockOperationTransaction newDelegate() {
		return new StockOperationTransaction();
	}

	@Override
	public Class<? extends IObjectDataService<StockOperationTransaction>> getServiceClass() {
		return null;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);

		description.addProperty("stockroom", Representation.REF);

		if (!(rep instanceof RefRepresentation)) {
			description.addProperty("patient", Representation.REF);
			description.addProperty("institution", Representation.REF);
		}

		return description;
	}

	@Override
	protected PageableResult doSearch(RequestContext context) {
		PageableResult result;

		String transactionItemUuid = context.getParameter("transactionItem_uuid");
		String stockroomUuid = context.getParameter("stockroom_uuid");
		if (StringUtils.isNotBlank(transactionItemUuid) && StringUtils.isNotBlank(stockroomUuid)) {
			Stockroom stockroom = stockroomDataService.getByUuid(stockroomUuid);
			Item item = itemDataService.getByUuid(transactionItemUuid);
			PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
			List<StockOperationTransaction> results =
			        stockroomDataService.getTransactionsByRoomAndItem(stockroom, item, pagingInfo);
			result =
			        new AlreadyPagedWithLength<StockOperationTransaction>(context, results, pagingInfo.hasMoreResults(),
			                pagingInfo.getTotalRecordCount());
		} else {
			result = super.doSearch(context);
		}

		return result;
	}
}
