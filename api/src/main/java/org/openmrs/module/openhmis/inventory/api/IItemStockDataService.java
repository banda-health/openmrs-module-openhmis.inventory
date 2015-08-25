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
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface that represents classes which perform data operations for {@link ItemStock}.
 */
@Transactional
public interface IItemStockDataService extends IObjectDataService<ItemStock> {
	/**
	 * Returns the {@link ItemStock} for the specified {@link Item}.
	 * @param item The item to find the item stock of.
	 * @param pagingInfo The paging information.
	 * @return The item stock for the specified item.
	 * @should return all item stock for the item ordered by stockroom name
	 * @should return paged item stock when paging is specified
	 * @should return an empty list if there is no item stock
	 * @should throw IllegalArgumentException if item is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_METADATA })
	List<ItemStock> getItemStockByItem(Item item, PagingInfo pagingInfo);
}
