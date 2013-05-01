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
package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.StockRoom;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomItem;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;

import java.util.Date;
import java.util.List;

public interface IStockRoomDataService extends IMetadataDataService<StockRoom> {
	/**
	 * Finds all the items in the stock room that match the {@link ItemSearch} settings.
 	 * @param stockRoom The {@link StockRoom} items to search.
	 * @param itemSearch The {@link ItemSearch} settings.
	 * @param paging The paging information.
	 * @return The stock room items found or and empty list if none were found.
	 */
	List<StockRoomItem> findItems(StockRoom stockRoom, ItemSearch itemSearch, PagingInfo paging);

	/**
	 * Gets the {@link StockRoomItem} for the specified {@link Item} with the optionally defined expiration.
	 * @param stockRoom The {@link StockRoom} items to search.
	 * @param item The {@link Item} to find.
	 * @param expiration The optional item expiration to search for.
	 * @return The {@link StockRoomItem} or {@code null} if not found.
	 */
	StockRoomItem getItem(StockRoom stockRoom, Item item, Date expiration);
}

