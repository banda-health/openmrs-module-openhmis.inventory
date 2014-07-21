package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.model.*;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface IStockOperationDataService extends IMetadataDataService<StockOperation> {
	/**
	 * Returns the {@link StockOperation} with the specified operation number.
	 * @param operationNumber The operation number.
	 * @return The {@link StockOperation} or {@code null} if there is no stockroom with the specified
	 * operation number.
	 * @should return null if number is not found
	 * @should return operation with the specified number
	 * @should throw IllegalArgumentException if number is null
	 * @should throw IllegalArgumentException if number is empty
	 * @should throw IllegalArgumentException is number is longer than 255 characters
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_OPERATIONS})
	StockOperation getOperationByNumber(String operationNumber) throws IllegalArgumentException, APIException;

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
	@Authorized( {PrivilegeConstants.VIEW_OPERATIONS, PrivilegeConstants.VIEW_STOCKROOMS})
	List<StockOperation> getOperationsByRoom(Stockroom stockroom, PagingInfo paging) throws IllegalArgumentException, APIException;

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
	@Authorized({PrivilegeConstants.VIEW_OPERATIONS})
	List<StockOperationItem> getItemsByOperation(StockOperation operation, PagingInfo paging) throws IllegalArgumentException, APIException;

	/**
	 * Returns the {@link StockOperation}s that are associated with the specified user.
	 * @param user The {@link org.openmrs.User}.
	 * @param paging The paging information of {@code null} to return all results.
	 * @return The operations associated with the specified user.
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_OPERATIONS})
	List<StockOperation> getUserOperations(User user, PagingInfo paging) throws IllegalArgumentException, APIException;

	/**
	 * Returns the {@link StockOperation}s with the specified status for the specified user.
	 * @param user The {@link User}.
	 * @param status The {@link StockOperationStatus}.
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
	@Authorized( {PrivilegeConstants.VIEW_OPERATIONS})
	List<StockOperation> getUserOperations(User user, StockOperationStatus status, PagingInfo paging) throws IllegalArgumentException, APIException;

	/**
	 * Finds all {@link StockOperation}s using the specified {@link StockOperationSearch} settings.
	 * @param search The transaction search settings.
	 * @return The transactions found or an empty list if no transactions were found.
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_OPERATIONS})
	List<StockOperation> findOperations(StockOperationSearch search) throws IllegalArgumentException, APIException;

	/**
	 * Finds all {@link StockOperation}s using the specified {@link StockOperationSearch} settings.
	 * @param search The operation search settings.
	 * @param paging The paging information.
	 * @return The operations found or an empty list if no transactions were found.
	 * @should throw IllegalArgumentException if operation search is null
	 * @should throw IllegalArgumentException if operation search template object is null
	 * @should return an empty list if no operations are found via the search
	 * @should return items filtered by number
	 * @should return items filtered by status
	 * @should return items filtered by type
	 * @should return items filtered by source stockroom
	 * @should return items filtered by destination stockroom
	 * @should return items filtered by creation date
	 * @should return all items if paging is null
	 * @should return paged items if paging is specified
	 * @should return items filtered by patient
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_OPERATIONS})
	List<StockOperation> findOperations(StockOperationSearch search, PagingInfo paging) throws IllegalArgumentException, APIException;
}

