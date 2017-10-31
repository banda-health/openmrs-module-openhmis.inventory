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
import org.openmrs.module.openhmis.inventory.api.IItemStockDetailDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * REST resource representing an {@link ItemStockDetail}.
 */
@Resource(name = ModuleRestConstants.ITEM_STOCK_DETAIL_RESOURCE, supportedClass = ItemStockDetail.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*" })
public class ItemStockDetailResource extends ItemStockDetailBaseResource<ItemStockDetail> {

	private IStockroomDataService stockroomDataService;
	private IItemStockDetailDataService itemStockDetailDataService;

	public ItemStockDetailResource() {
		this.stockroomDataService = Context.getService(IStockroomDataService.class);
		this.itemStockDetailDataService = Context.getService(IItemStockDetailDataService.class);
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.addProperty("stockroom", Representation.DEFAULT);

		return description;
	}

	@Override
	protected PageableResult doSearch(RequestContext context) {
		PageableResult result;
		String stockroomUuid = context.getParameter("stockroom_uuid");
		if (StringUtils.isNotBlank(stockroomUuid)) {
			PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
			Stockroom stockroom = stockroomDataService.getByUuid(stockroomUuid);
			List<ItemStockDetail> itemStockDetails =
			        itemStockDetailDataService.getItemStockDetailsByStockroom(stockroom, pagingInfo);
			result =
			        new AlreadyPagedWithLength<ItemStockDetail>(context, itemStockDetails, pagingInfo.hasMoreResults(),
			                pagingInfo.getTotalRecordCount());
		} else {
			result = super.doSearch(context);
		}
		return result;
	}

	@Override
	public PageableResult doGetAll(RequestContext context) {
		return doSearch(context);
	}

	@Override
	public ItemStockDetail newDelegate() {
		return new ItemStockDetail();
	}

	@Override
	public Class<? extends IObjectDataService<ItemStockDetail>> getServiceClass() {
		return IItemStockDetailDataService.class;
	}
}
