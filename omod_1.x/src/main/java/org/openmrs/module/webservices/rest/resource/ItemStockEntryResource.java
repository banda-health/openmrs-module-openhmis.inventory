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

import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.model.ItemStockEntry;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

/**
 * REST resource representing an {@link ItemStockEntry}.
 */
@Resource(name = ModuleRestConstants.ITEM_STOCK_ENTRY_RESOURCE, supportedClass = ItemStockEntry.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*" })
public class ItemStockEntryResource extends ItemStockDetailBaseResource<ItemStockEntry> {
	@Override
	public ItemStockEntry save(ItemStockEntry item) {
		throw new APIException("Not implemented.");
	}

	@Override
	public ItemStockEntry newDelegate() {
		return new ItemStockEntry();
	}

	@Override
	public Class<? extends IObjectDataService<ItemStockEntry>> getServiceClass() {
		return null;
	}
}
