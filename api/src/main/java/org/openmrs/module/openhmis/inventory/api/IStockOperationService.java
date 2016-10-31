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

import java.util.Collection;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTransaction;
import org.openmrs.module.openhmis.inventory.api.util.PrivilegeConstants;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface that represents classes which provide {@link StockOperation} services.
 */
public interface IStockOperationService extends OpenmrsService {
	/**
	 * Validates and saves the specified {@link org.openmrs.module.openhmis.inventory.api.model.StockOperation}, creating any
	 * required related objects. This will subtract the item quantities from the source stockroom, if one is defined. The
	 * operation status may be automatically completed if all the required attributes have been defined, otherwise the status
	 * will be PENDING. If the operation is completed the completion action, as defined by the
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
	 * @should update operation status to pending if status is new
	 * @should create new reservations from the operation items
	 * @should not recreate existing reservations if submitted multiple times
	 * @should properly process operation as submitted for each state change
	 * @should add the destination stockroom item stock if existing is negative
	 * @should remove item stock from destination stockroom if quantity becomes zero
	 * @should add stock if calculate expiration is false and expiration is null for an expirable item
	 * @should not include rollback operations when rolling back and reapplying subsequent operations
	 * @should throw APIException if the operation type is receipt and expiration is not defined for expirable items
	 * @should throw an IllegalArgumentException if the operation is null
	 * @should throw an APIException if the operation type is null
	 * @should throw an APIException if the operation has no operation items
	 * @should throw an APIException if the operation type requires a source and the source is null
	 * @should throw an APIException if the operation type requires a destination and the destination is null
	 * @should throw an APIException if the operation type requires a patient and the patient is null
	 * @should throw APIException if source stockroom is null and the expiration is not specified for an expirable item
	 */
	@Transactional
	@Authorized({ PrivilegeConstants.MANAGE_OPERATIONS })
	StockOperation submitOperation(StockOperation operation);

	/**
	 * Rolls back a completed operation from the system, re-applying any following operations.
	 * @param operation The operation to rollback
	 * @return The rolled back and saved stock operation.
	 * @should rollback the specified operation
	 * @should rollback and reapply any following operations
	 * @should set the operation status to Rollback
	 * @should throw APIException if operation status is not Completed
	 * @should throw IllegalArgumentException if operation is null
	 */
	@Transactional
	@Authorized({ PrivilegeConstants.ROLLBACK_OPERATIONS })
	StockOperation rollbackOperation(StockOperation operation);

	/**
	 * Applies the specified transactions against the referenced objects.
	 * @param transactions The transactions to apply.
	 * @should not throw exception if transactions is null
	 * @should not throw exception if transactions is empty
	 * @should add source stockroom item stock and detail if no item stock found
	 * @should update source stockroom item stock and detail if item exists
	 * @should update source stockroom item stock and create detail if needed
	 * @should add source stockroom item stock with negative quantity when transaction quantity is negative and stock not
	 *         found
	 * @should add item stock detail with no expiration or batch when item stock quantity is negative
	 * @should remove item stock if quantity is zero
	 * @should remove item stock detail if quantity is zero
	 */
	@Transactional
	@Authorized({ PrivilegeConstants.MANAGE_OPERATIONS })
	void applyTransactions(Collection<StockOperationTransaction> transactions);

	/**
	 * Applies the specified transactions against the referenced objects.
	 * @param transactions The transactions to apply.
	 */
	@Transactional
	@Authorized({ PrivilegeConstants.MANAGE_OPERATIONS })
	void applyTransactions(StockOperationTransaction... transactions);
}
