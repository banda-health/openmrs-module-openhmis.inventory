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

import java.util.Date;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTransaction;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.api.search.ItemSearch;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface that represents classes which perform data operations for {@link Stockroom}s.
 */
@Transactional
public interface IStockroomDataService extends IMetadataDataService<Stockroom> {
	/**
	 * Gets all {@link ItemStock}'s in the specified {@link Stockroom}.
	 * @param stockroom The {@link Stockroom}.
	 * @param paging The paging information.
	 * @return A list containing all of the stockroom items.
	 * @should return all the items in the stockroom ordered by item name
	 * @should return an empty list if there are no items in the stockroom
	 * @should return paged items if paging is specified
	 * @should return item stock sorted by item name
	 * @should throw IllegalArgumentException if the stockroom is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	List<ItemStock> getItemsByRoom(Stockroom stockroom, PagingInfo paging);

	/**
	 * Gets all {@link StockOperationTransaction}'s in the specified {@link Stockroom}.
	 * @param stockroom The {@link Stockroom}.
	 * @param paging The paging information.
	 * @return A list containing all of the stockroom transactions.
	 * @should return all the transactions in the stockroom ordered by the transaction date
	 * @should return an empty list if there are no transactions
	 * @should return paged items if paging is specified
	 * @should throw IllegalArgumentException if the stockroom is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	List<StockOperationTransaction> getTransactionsByRoom(Stockroom stockroom, PagingInfo paging);

	/**
	 * Gets all {@link StockOperationTransaction}'s in the specified {@link Stockroom}.
	 * @param stockroom The {@link Stockroom}.
	 * @param item The {@link Item}.
	 * @param paging The paging information.
	 * @return A list containing all of the stockroom transactions that contain the given item.
	 * @should return all the transactions in the stockroom ordered by the transaction date
	 * @should return an empty list if there are no transactions
	 * @should return paged items if paging is specified
	 * @should throw IllegalArgumentException if the stockroom is null
	 * @should throw IllegalArgumentException if the item is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	List<StockOperationTransaction> getTransactionsByRoomAndItem(Stockroom stockroom, Item item, PagingInfo paging);

	/**
	 * Gets all the items in the stockroom that match the {@link ItemSearch} settings.
	 * @param stockroom The {@link Stockroom} items to search within.
	 * @param itemSearch The {@link ItemSearch} settings.
	 * @param paging The paging information.
	 * @return The stockroom items found or and empty list if none were found.
	 * @should return items filtered by template and stockroom
	 * @should not return items for other stockrooms
	 * @should return all found items if paging is null
	 * @should return paged items if paging is specified
	 * @should return retired items from search unless specified
	 * @should return item stock sorted by item name
	 * @should throw IllegalArgumentException if stockroom is null
	 * @should throw IllegalArgumentException if item search is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	List<ItemStock> getItems(Stockroom stockroom, ItemSearch itemSearch, PagingInfo paging);

	/**
	 * Gets all operations associated with the stockroom that match the {@link StockOperationSearch} settings.
	 * @param stockroom The {@link Stockroom} operations to search within.
	 * @param search The {@link StockOperationSearch} settings
	 * @param paging The paging information.
	 * @return The stock operations found or an empty list if none were found.
	 * @should return operations filtered by template and stockroom
	 * @should return paged operations if paging is specified
	 * @should return operations sorted by last modified date
	 * @should throw IllegalArgumentException if stockroom is null
	 * @should not throw IllegalArgumentException if operation search is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	List<StockOperation> getOperations(Stockroom stockroom, StockOperationSearch search, PagingInfo paging);

	/**
	 * Gets the {@link ItemStock} for the specified {@link Item} with the optionally defined expiration.
	 * @param stockroom The {@link Stockroom} items to search.
	 * @param item The {@link Item} to find.
	 * @return The {@link ItemStock} or {@code null} if not found.
	 * @should return the stockroom item
	 * @should not return items from other stockrooms
	 * @should return null when item is not found
	 * @should throw IllegalArgumentException when stockroom is null
	 * @should throw IllegalArgumentException when item is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	ItemStock getItem(Stockroom stockroom, Item item);

	/**
	 * Gets the {@link ItemStockDetail} for the specified {@link Item} and qualifiers.
	 * @param stockroom The stockroom item details to search.
	 * @param item The item to find.
	 * @param expiration The optional expiration date to search for.
	 * @param batchOperation The optional batch operation to search for.
	 * @return The item stock detail or {@code null} if not found.
	 * @should return the stockroom item detail
	 * @should not return details for other stockrooms
	 * @should return null when the details is not found
	 * @should return detail with expiration and batch when specified
	 * @should return detail without an expiration when not specified
	 * @should return detail without a batch when not specified
	 * @should throw IllegalArgumentException when stockroom is null
	 * @should throw IllegalArgumentException when item is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	ItemStockDetail getStockroomItemDetail(Stockroom stockroom, Item item, Date expiration, StockOperation batchOperation);

	/**
	 * Gets all the stockrooms for the specified {@link Location}.
	 * @param location The location.
	 * @param includeRetired Whether retired stockrooms should be included in the results.
	 * @return All stockrooms for the specified {@link Location}.
	 * @should throw NullPointerException if the location is null
	 * @should return an empty list if the location has no stockrooms
	 * @should not return retired stockrooms unless specified
	 * @should return all stockrooms for the specified location
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	List<Stockroom> getStockroomsByLocation(Location location, boolean includeRetired);

	/**
	 * Gets all the stockrooms for the specified {@link Location}.
	 * @param location The location.
	 * @param includeRetired Whether retired stockrooms should be included in the results.
	 * @param pagingInfo The paging information
	 * @return All stockrooms for the specified {@link location}.
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	List<Stockroom> getStockroomsByLocation(Location location, boolean includeRetired, PagingInfo pagingInfo);

	/**
	 * Gets all stockrooms in the specified {@link location} that start with the specified name.
	 * @param location The location to search within.
	 * @param name The stockroom name fragment.
	 * @param includeRetired Whether retired stockrooms should be included in the results.
	 * @return All stockrooms in the specified {@link org.openmrs.Location} that start with the specified name.
	 * @should throw NullPointerException if the location is null
	 * @should throw IllegalArgumentException if the name is null
	 * @should throw IllegalArgumentException if the name is empty
	 * @should throw IllegalArgumentException if the name is longer than 255 characters
	 * @should return an empty list if no stockrooms are found
	 * @should not return retired stockrooms unless specified
	 * @should return stockrooms that start with the specified name
	 * @should return stockrooms for only the specified location
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	List<Stockroom> getStockrooms(Location location, String name, boolean includeRetired);

	/**
	 * Gets all stockrooms in the specified {@link Location} that start with the specified name.
	 * @param location The location to search within.
	 * @param name The stockroom name fragment.
	 * @param includeRetired Whether retired stockrooms should be included in the results.
	 * @param pagingInfo The paging information.
	 * @return All stockrooms in the specified {@link org.openmrs.Location} that start with the specified name.
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_STOCKROOMS })
	List<Stockroom> getStockrooms(Location location, String name, boolean includeRetired, PagingInfo pagingInfo);
}
