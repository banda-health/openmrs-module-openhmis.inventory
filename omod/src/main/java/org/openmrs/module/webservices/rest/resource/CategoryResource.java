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
package org.openmrs.module.webservices.rest.resource;

import org.openmrs.annotation.Handler;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.ICategoryDataService;
import org.openmrs.module.openhmis.inventory.api.IStockRoomDataService;
import org.openmrs.module.openhmis.inventory.api.model.Category;
import org.openmrs.module.openhmis.inventory.api.model.StockRoom;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

@Resource("category")
@Handler(supports = { Category.class }, order = 0)
public class CategoryResource extends BaseRestMetadataResource<Category> {

	@Override
	public Category newDelegate() {
		return new Category();
	}

	@Override
	public Class<? extends IMetadataDataService<Category>> getServiceClass() {
		return ICategoryDataService.class;
	}
}

