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

import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.commons.api.entity.model.IInstanceType;
import org.openmrs.module.openhmis.inventory.api.model.IStockOperationType;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface that represents classes which perform data operations for {@link StockOperation}s.
 */
@Transactional
public interface IStockOperationDataService extends IMetadataDataService<StockOperation> {
	/**
	 * Returns the {@link StockOperation} with the specified operation number.
	 * @param operationNumber The operation number.
	 * @return The {@link StockOperation} or {@code null} if there is no stockroom with the specified operation number.
	 * @should return null if number is not found
	 * @should return operation with the specified number
	 * @should throw IllegalArgumentException if number is null
	 * @should throw IllegalArgumentException if number is empty
	 * @should throw IllegalArgumentException is number is longer than 255 characters
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	StockOperation getOperationByNumber(String operationNumber);

	/**
	 * Returns the {@link StockOperation}s for the specified {@link Stockroom}.
	 * @param stockroom The {@link Stockroom} that the transactions occurred in.
	 * @param paging The paging information or {@code null} to return all results.
	 * @return The operations for the specified stockroom.
	 * @should return operations for specified room
	 * @should return empty list when no operations
	 * @should return paged operations when paging is specified
	 * @should return all operations when paging is null
	 * @should return operations with any status
	 * @should throw IllegalArgumentException when stockroom is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS, PrivilegeConstants.VIEW_STOCKROOMS })
	List<StockOperation> getOperationsByRoom(Stockroom stockroom, PagingInfo paging);

	/**
	 * Returns the {@link StockOperationItem}s for the specified {@link StockOperation}.
	 * @param operation The {@link StockOperation}
	 * @param paging The paging information of {@code null} to return all results.
	 * @return The operation items for the stockroom operation.
	 * @should return items for the specified operation
	 * @should return empty list when no items
	 * @should return paged items when paging is specified
	 * @should return all items when paging is null
	 * @should throw IllegalArgumentException when operation is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	List<StockOperationItem> getItemsByOperation(StockOperation operation, PagingInfo paging);

	/**
	 * Returns the {@link StockOperation}s that are associated with the specified user.
	 * @param user The {@link org.openmrs.User}.
	 * @param paging The paging information of {@code null} to return all results.
	 * @return The operations associated with the specified user.
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	List<StockOperation> getUserOperations(User user, PagingInfo paging);

	/**
	 * Returns the {@link StockOperation}s with the specified status for the specified user.
	 * @param user The {@link User}.
	 * @param status The {@link StockOperationStatus}.
	 * @param stockOperationType {@link IInstanceType}
	 * @param item {@link Item}
	 * @param paging The paging information or {@code null} to return all results.
	 * @return The operations associated with the specified user with the specified status.
	 * @should return all operations with the specified status for specified user
	 * @should return specified operations created by user
	 * @should return specified operations with user as attribute type user
	 * @should return specified operations with user role as attribute type role
	 * @should return specified operations with user role as child role of attribute type role
	 * @should return specified operations with user role as grandchild role of attribute type role
	 * @should not return operations when user role not descendant of attribute type role
	 * @should not return operations when user role is parent of attribute type role
	 * @should return empty list when no operations
	 * @should return paged operations when paging is specified
	 * @should return all operations when paging is null
	 * @should throw IllegalArgumentException when user is null
	 * @should return all operations for user when status is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	List<StockOperation> getUserOperations(User user, StockOperationStatus status, IStockOperationType stockOperationType,
	        Item item, Stockroom stockroom, PagingInfo paging);

	/**
	 * Gets all {@link StockOperation}s using the specified {@link StockOperationSearch} settings.
	 * @param search The transaction search settings.
	 * @return The transactions found or an empty list if no transactions were found.
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	List<StockOperation> getOperations(StockOperationSearch search);

	/**
	 * Gets all {@link StockOperation}s using the specified {@link StockOperationSearch} settings.
	 * @param search The operation search settings.
	 * @param paging The paging information.
	 * @return The operations found or an empty list if no transactions were found.
	 * @should throw IllegalArgumentException if operation search is null
	 * @should throw IllegalArgumentException if operation search template object is null
	 * @should return an empty list if no operations are found via the search
	 * @should return operations filtered by number
	 * @should return operations filtered by status
	 * @should return operations filtered by type
	 * @should return operations filtered by source stockroom
	 * @should return operations filtered by destination stockroom
	 * @should return operations filtered by creation date
	 * @should return operations filtered by stockroom
	 * @should return all operations if paging is null
	 * @should return paged operations if paging is specified
	 * @should return operations filtered by patient
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	List<StockOperation> getOperations(StockOperationSearch search, PagingInfo paging);

	/**
	 * Gets all {@link StockOperation}s with an operation date past the specified date
	 * @param operationDate The starting operation date (not inclusive)
	 * @param paging The paging information.
	 * @return The operations found or an empty list
	 * @should throw IllegalArgumentException if operationDate is null
	 * @should return an empty list if no operations are found
	 * @should return operations with operation date past specified date
	 * @should return all results if paging is null
	 * @should return paged operations if paging is specified
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	List<StockOperation> getOperationsSince(Date operationDate, PagingInfo paging);

	/**
	 * Gets all {@link StockOperation}s with an operation date after the specified operation date, taking into account the
	 * operation order for operations that occurred on the same day.
	 * @param operation The starting operation.
	 * @param paging The paging information.
	 * @return The operations found or an empty list
	 * @should throw IllegalArgumentException if the operation is null
	 * @should return an empty list if no operations are found
	 * @should return operations with operation date past specified operation
	 * @should return operations with higher operation order when day is the same
	 * @should return operations by date then by operation order
	 * @should return all results if paging is null
	 * @should return paged results if paging is specified
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	List<StockOperation> getFutureOperations(StockOperation operation, PagingInfo paging);

	/**
	 * Gets all operations that occurred on the specified operation date. The time portion of the operation date is not
	 * considered.
	 * @param date The operation date.
	 * @param paging The paging information.
	 * @return The operations that occurred on the specified date
	 * @should throw IllegalArgumentException if the operation is null
	 * @should return an empty list if no operations are found
	 * @should return operations that occurred on the specified date regardless of time
	 * @should return operations ordered by operation order
	 * @should return all results if paging is null
	 * @should return paged results if paging is specified
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	List<StockOperation> getOperationsByDate(Date date, PagingInfo paging);

	/**
	 * Gets the last {@link StockOperation} (that is, with the largest operation order) on the specified date.
	 * @param date The operation date.
	 * @return The last stock operation or {@code null} if no operations occurred on the specified date.
	 * @should return the operation with the largest operation order on the specified date
	 * @should return the operation with the last creation date if the operation order is the same
	 * @should return null if no operations occurred on the specified date
	 * @should throw IllegalArgumentException if the date is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	StockOperation getLastOperationByDate(Date date);

	/**
	 * Gets the first {@link StockOperation} (that is, with the smallest operation order) on the specified date.
	 * @param date The operation date.
	 * @return The last stock operation or {@code null} if no operations occurred on the specified date.
	 * @should return the operation with the least operation order on the specified date
	 * @should return the operation with the first creation date if the operation order is the same
	 * @should return null if no operations occurred on the specified date
	 * @should throw IllegalArgumentException if the date is null
	 */
	@Transactional(readOnly = true)
	@Authorized({ PrivilegeConstants.VIEW_OPERATIONS })
	StockOperation getFirstOperationByDate(Date date);
}
