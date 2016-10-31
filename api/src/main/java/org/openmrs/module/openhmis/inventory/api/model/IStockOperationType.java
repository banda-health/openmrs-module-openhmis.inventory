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
package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.module.openhmis.commons.api.entity.model.IInstanceType;

/**
 * Represents classes that can act as a stock operation type.
 */
public interface IStockOperationType extends OpenmrsMetadata, IInstanceType<StockOperationAttributeType> {
	/**
	 * Returns whether the operation type has a source {@link Stockroom}.
	 * @return {@code true} if the operation type has a source stockroom; otherwise, {@code false}.
	 */
	Boolean getHasSource();

	/**
	 * Sets whether the operation type has a source {@link Stockroom}.
	 * @param hasSource Whether the operation type has a source stockroom.
	 */
	void setHasSource(Boolean hasSource);

	/**
	 * Returns whether the operation type has a destination {@link Stockroom}.
	 * @return {@code true} if the operation type has a destination stockroom; otherwise, {@code false}.
	 */
	Boolean getHasDestination();

	/**
	 * Sets whether the operation type has a destination {@link Stockroom}.
	 * @param hasDestination Whether the operation type has a destination stockroom.
	 */
	void setHasDestination(Boolean hasDestination);

	/**
	 * Returns whether the operation type has an associated {@link org.openmrs.Patient}.
	 * @return {@code true} if the operation type has an associated patient; otherwise, {@code false}.
	 */
	Boolean getHasRecipient();

	/**
	 * Sets whether the operation type has an associated {@link org.openmrs.Patient}.
	 * @param hasRecipient Whether the operation type has an associated patient.
	 */
	void setHasRecipient(Boolean hasRecipient);

	/**
	 * Returns whether the operation type requires an associated recipient (a {@link Institution}, {@link Department}, or
	 * {@link org.openmrs.Patient}).
	 * @return {@code true} if the operation type requires and associated patient; otherwise, {@code false}.
	 */
	Boolean getRecipientRequired();

	/**
	 * Sets whether the operation type requires an associated recipient.
	 * @param required Whether the operation type requires an associated patient.
	 */
	void setRecipientRequired(Boolean required);

	/**
	 * Returns whether the operation items are available while reserved.
	 * @return {@code true} if the operation items are available; otherwise, {@code false}.
	 */
	Boolean getAvailableWhenReserved();

	/**
	 * Sets whether the operation items are available while reserved.
	 * @param available Whether the operation items are available while reserved.
	 */
	void setAvailableWhenReserved(Boolean available);

	/**
	 * Returns the {@link User} required to approve operations of this type.
	 * @return The user required to approve the operation.
	 */
	User getUser();

	/**
	 * Sets the {@link User} required to approve operations of this type.
	 * @param user The user required to approve the operation.
	 */
	void setUser(User user);

	/**
	 * Returns the user {@link Role} for users that are required to approve operations of this type.
	 * @return The role required to approve the operation.
	 */
	Role getRole();

	/**
	 * Sets the user {@link Role} for users that can approve operations of this type.
	 * @param role The role required to approve the operation.
	 */
	void setRole(Role role);

	/**
	 * Calculates if the specified {@link User} can process this type.
	 * @param user The user to check.
	 * @return {@code true} if the user can process the type; otherwise, {@code false}.
	 * @should return true when type has no role or user defined
	 * @should return false when type has different role than user
	 * @should return false when type has different user than user
	 * @should return true when type has same role or parent role as user
	 * @should return true when type has same user as user
	 * @should return true when type has user role and different user than user
	 * @should return true when type has different role and same user as user
	 * @should return true when type has different user and user is sys dev
	 * @should return true when type has different role and user is sys dev
	 * @should return true when type has different role and user and user is sys dev
	 * @should throw IllegalArgumentException if user is null
	 */
	boolean userCanProcess(User user);

	/**
	 * Called when the {@link StockOperation} status is initially created and the status is StockOperationStatus.PENDING.
	 * @param operation The associated stock operation.
	 */
	void onPending(StockOperation operation);

	/**
	 * Called when the {@link StockOperation} status is changed to StockOperationStatus.CANCELLED.
	 * @param operation The associated stock operation.
	 */
	void onCancelled(StockOperation operation);

	/**
	 * Called when the {@link StockOperation} status is changed to StockOperationStatus.COMPLETED.
	 * @param operation The associated stock operation.
	 */
	void onCompleted(StockOperation operation);

	/**
	 * Determines weather or not negative quantities for items are allowed
	 * @return true if negative quantities are allowed, else false
	 */
	boolean isNegativeItemQuantityAllowed();
}
