package org.openmrs.module.openhmis.inventory.api;

import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationStatus;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTransaction;
import org.openmrs.module.openhmis.inventory.api.model.StockRoom;
import org.openmrs.module.openhmis.inventory.api.search.StockOperationSearch;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface IStockOperationDataService extends IMetadataDataService<StockOperation> {
	/**
	 * Validates and saves the specified {@link StockOperation}, creating any required related objects. This will
	 * subtract the item quantities from the source stockroom, if one is defined.  The operation status may be automatically
	 * completed if all the required attributes have been defined, otherwise the status will be PENDING.  If the
	 * operation is completed the completion action, as defined by the
	 * {@link org.openmrs.module.openhmis.inventory.api.model.IStockOperationType} will be executed.
	 * @param operation The operation to submit.
	 * @return The submitted and saved stock operation.
	 * @should update the source stockroom item stock quantities
	 * @should remove empty item stock from the source stockroom
	 * @should set the correct availability for the reserved stock quantity
	 * @should update the destination stockroom item stock quantities
	 * @should add the destination stockroom item stock if not found
	 * @should calculate expiration if not defined for expirable item
	 * @should calculate batch operation if not defined
	 * @should throw APIException if the operation type is receipt and expiration is not defined for expirable items
	 * @should throw an IllegalArgumentException if the operation is null
	 * @should throw an APIException if the operation type is null
	 * @should throw an APIException if the operation has no reserved transactions
	 * @should throw an APIException if the operation type requires a source and the source is null
	 * @should throw an APIException if the operation type requires a destination and the destination is null
	 * @should throw an APIException if the operation type requires a patient and the patient is null
	 */
	@Transactional
	@Authorized( {PrivilegeConstants.MANAGE_OPERATIONS})
	StockOperation submitOperation(StockOperation operation) throws IllegalArgumentException, APIException;

	/**
	 * Applies the specified transactions against the referenced objects.
	 * @param transactions The transactions to apply.
	 */
	@Transactional
	@Authorized( {PrivilegeConstants.MANAGE_OPERATIONS})
	void applyTransactions(StockOperationTransaction... transactions) throws APIException;

	/**
	 * Applies the specified transactions against the referenced objects.
	 * @param transactions The transactions to apply.
	 * @should not throw exception if transactions is null
	 * @should not throw exception if transactions is empty
	 * @should add source stockroom item stock and detail if no item stock found
	 * @should update source stockroom item stock and detail if item exists
	 * @should update source stockroom item stock and create detail if needed
	 * @should add source stockroom item stock with negative quantity when transaction quantity is negative and stock not found
	 * @should add item stock detail with no expiration or batch when item stock quantity is negative
	 * @should remove item stock if quantity is zero
	 * @should remove item stock detail if quantity is zero
	 */
	@Authorized( {PrivilegeConstants.MANAGE_OPERATIONS})
	void applyTransactions(Collection<StockOperationTransaction> transactions) throws APIException;

	/**
	 * Returns the {@link StockOperation} with the specified operation number.
	 * @param operationNumber The operation number.
	 * @return The {@link StockOperation} or {@code null} if there is no stock room with the specified
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
	 * Returns the {@link StockOperation}s for the specified {@link StockRoom}.
	 * @param stockroom The {@link StockRoom} that the transactions occurred in.
	 * @param paging The paging information or {@code null} to return all results.
	 * @return The operations for the specified stock room.
	 * @should return operations for specified room
	 * @should return empty list when no operations
	 * @should return paged operations when paging is specified
	 * @should return all operations when paging is null
	 * @should return operations with any status
	 * @should throw IllegalArgumentException when stockroom is null
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_OPERATIONS, PrivilegeConstants.VIEW_STOCKROOMS})
	List<StockOperation> getOperationsByRoom(StockRoom stockroom, PagingInfo paging) throws IllegalArgumentException, APIException;

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
	 * @should return items filtered by source stock room
	 * @should return items filtered by destination stock room
	 * @should return items filtered by creation date
	 * @should return all items if paging is null
	 * @should return paged items if paging is specified
	 * @should return items filtered by patient
	 */
	@Transactional(readOnly = true)
	@Authorized( {PrivilegeConstants.VIEW_OPERATIONS})
	List<StockOperation> findOperations(StockOperationSearch search, PagingInfo paging) throws IllegalArgumentException, APIException;
}

