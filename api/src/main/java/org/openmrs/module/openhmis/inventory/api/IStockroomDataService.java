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
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface IStockroomDataService extends IMetadataDataService<Stockroom> {
	/**
	 * Gets all {@link ItemStock}'s in the specified {@link Stockroom}.
	 * @param stockroom The {@link Stockroom}.
	 * @param paging The paging information.
	 * @return A list containing all of the stock room items.
	 * @should return all the items in the stock room ordered by item name
	 * @should return an empty list if there are no items in the stock room
	 * @should return paged items if paging is specified
	 * @should return item stock sorted by item name
	 * @should throw IllegalArgumentException if the stock room is null
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_STOCKROOMS})
	List<ItemStock> getItemsByRoom(Stockroom stockroom, PagingInfo paging);

	/**
	 * Finds all the items in the stock room that match the {@link ItemSearch} settings.
 	 * @param stockroom The {@link Stockroom} items to search.
	 * @param itemSearch The {@link ItemSearch} settings.
	 * @param paging The paging information.
	 * @return The stock room items found or and empty list if none were found.
	 * @should return items filtered by template and stock room
	 * @should not return items for other stock rooms
	 * @should return all found items if paging is null
	 * @should return paged items if paging is specified
	 * @should return retired items from search unless specified
	 * @should return item stock sorted by item name
	 * @should throw IllegalArgumentException if stock room is null
	 * @should throw IllegalArgumentException if item search is null
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_STOCKROOMS})
	List<ItemStock> findItems(Stockroom stockroom, ItemSearch itemSearch, PagingInfo paging);

	/**
	 * Gets the {@link ItemStock} for the specified {@link Item} with the optionally defined expiration.
	 * @param stockroom The {@link Stockroom} items to search.
	 * @param item The {@link Item} to find.
	 * @return The {@link ItemStock} or {@code null} if not found.
	 * @should return the stock room item
	 * @should not return items from other stock rooms
	 * @should return null when item is not found
	 * @should throw IllegalArgumentException when stock room is null
	 * @should throw IllegalArgumentException when item is null
	 */
	@Transactional(readOnly = true)
	@Authorized({PrivilegeConstants.VIEW_STOCKROOMS})
	ItemStock getItem(Stockroom stockroom, Item item);

	/**
	 * Gets the {@link ItemStockDetail} for the specified {@link Item} and qualifiers.
	 * @param stockroom The stockroom item details to search.
	 * @param item The item to find.
	 * @param expiration The optional expiration date to search for.
	 * @param batchOperation The optional batch operation to search for.
	 * @return The item stock detail or {@code null} if not found.
	 * @should return the stock room item detail
	 * @should not return details for other stock rooms
	 * @should return null when the details is not found
	 * @should return detail with expiration and batch when specified
	 * @should return detail without an expiration when not specified
	 * @should return detail without a batch when not specified
	 * @should throw IllegalArgumentException when stock room is null
	 * @should throw IllegalArgumentException when item is null
	 */
	@Transactional(readOnly = true)
	@Authorized({PrivilegeConstants.VIEW_STOCKROOMS})
	ItemStockDetail getStockroomItemDetail(Stockroom stockroom, Item item, Date expiration, StockOperation batchOperation);
}

