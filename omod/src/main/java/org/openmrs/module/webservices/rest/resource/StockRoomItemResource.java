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
package org.openmrs.module.webservices.rest.resource;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.IStockRoomDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomItem;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.search.StockRoomItemSearchHandler;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = ModuleRestConstants.STOCK_ROOM_ITEM_RESOURCE, supportedClass=StockRoomItem.class, supportedOpenmrsVersions={"1.9"})
@Handler(supports = { StockRoomItem.class }, order = 0)
public class StockRoomItemResource extends BaseRestObjectResource<StockRoomItem> {
	@Override
	public StockRoomItem newDelegate() {
		return new StockRoomItem();
	}

	@Override
	public Class<? extends IObjectDataService<StockRoomItem>> getServiceClass() {
		return null;
	}

	@Override
	protected DelegatingResourceDescription getDefaultRepresentationDescription() {
		DelegatingResourceDescription description = super.getDefaultRepresentationDescription();

		description.addProperty("stockRoom", Representation.DEFAULT);
		description.addProperty("importTransaction", Representation.DEFAULT);
		description.addProperty("item", Representation.DEFAULT);
		description.addProperty("quantity", Representation.DEFAULT);
		description.addProperty("expiration", Representation.DEFAULT);

		return description;
	}

	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return StockRoomItemSearchHandler.doSearch(Context.getService(IStockRoomDataService.class), context);
	}
}

