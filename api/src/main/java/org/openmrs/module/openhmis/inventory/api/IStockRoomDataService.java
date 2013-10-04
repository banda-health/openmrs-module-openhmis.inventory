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
package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.annotation.Authorized;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.StockRoom;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomItem;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface IStockRoomDataService extends IMetadataDataService<StockRoom> {
	/**
	 * Gets all {@link StockRoomItem}'s in the specified {@link StockRoom}.
	 * @param stockRoom The {@link StockRoom}.
	 * @param paging The paging information.
	 * @return A list containing all of the stock room items.
	 * @should return all the items in the stock room ordered by item name
	 * @should return an empty list if there are no items in the stock room
	 * @should return paged items if paging is specified
	 * @should throw NullReferenceException if the stock room is null
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_STOCK_ROOMS})
	List<StockRoomItem> getItemsByRoom(StockRoom stockRoom, PagingInfo paging);

	/**
	 * Finds all the items in the stock room that match the {@link ItemSearch} settings.
 	 * @param stockRoom The {@link StockRoom} items to search.
	 * @param itemSearch The {@link ItemSearch} settings.
	 * @param paging The paging information.
	 * @return The stock room items found or and empty list if none were found.
	 * @should return items filtered by template and stock room
	 * @should not return items for other stock rooms
	 * @should return all found items if paging is null
	 * @should return paged items if paging is specified
	 * @should return retired items from search unless specified
	 * @should throw NullReferenceException if stock room is null
	 * @should throw NullReferenceException if item search is null
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_STOCK_ROOMS})
	List<StockRoomItem> findItems(StockRoom stockRoom, ItemSearch itemSearch, PagingInfo paging);

	/**
	 * Gets the {@link StockRoomItem} for the specified {@link Item} with the optionally defined expiration.
	 * @param stockRoom The {@link StockRoom} items to search.
	 * @param item The {@link Item} to find.
	 * @param expiration The optional item expiration to search for.
	 * @return The {@link StockRoomItem} or {@code null} if not found.
	 * @should return the stock room item
	 * @should not return items from other stock rooms
	 * @should return null when item is not found
	 * @should return item with expiration when specified
	 * @should return the item without an expiration what not specified
	 * @should throw NullReferenceException when stock room is null
	 * @should throw NullReferenceException when item is null
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_STOCK_ROOMS})
	StockRoomItem getItem(StockRoom stockRoom, Item item, Date expiration);
}

