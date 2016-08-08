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

import java.util.Set;
import java.util.TreeSet;

import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.model.BaseInstanceCustomizableType;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.inventory.api.IStockOperationService;

/**
 * Base model class used by models that represent a stock operation type.
 */
public abstract class StockOperationTypeBase extends BaseInstanceCustomizableType<StockOperationAttributeType>
        implements IStockOperationType {
	public static final long serialVersionUID = 0L;

	private Integer id;
	private Boolean hasSource;
	private Boolean hasDestination;
	private Boolean hasRecipient;
	private Boolean recipientRequired;
	private Boolean availableWhenReserved;
	private User user;
	private Role role;

	@Override
	public abstract void onPending(StockOperation operation);

	@Override
	public abstract void onCancelled(StockOperation operation);

	@Override
	public abstract void onCompleted(StockOperation operation);

	@Override
	public abstract boolean isNegativeItemQuantityAllowed();

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getHasSource() {
		return hasSource;
	}

	public void setHasSource(Boolean hasSource) {
		this.hasSource = hasSource;
	}

	public Boolean getHasDestination() {
		return hasDestination;
	}

	public void setHasDestination(Boolean hasDestination) {
		this.hasDestination = hasDestination;
	}

	public Boolean getHasRecipient() {
		return hasRecipient;
	}

	public void setHasRecipient(Boolean hasPatient) {
		this.hasRecipient = hasPatient;
	}

	public Boolean getRecipientRequired() {
		return recipientRequired;
	}

	public void setRecipientRequired(Boolean recipientRequired) {
		this.recipientRequired = recipientRequired;
	}

	public Boolean getAvailableWhenReserved() {
		return availableWhenReserved;
	}

	public void setAvailableWhenReserved(Boolean availableInTransit) {
		this.availableWhenReserved = availableInTransit;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean userCanProcess(User currentUser) {
		if (currentUser == null) {
			throw new IllegalArgumentException("The current user must be defined.");
		}

		// Users can process the type if they have either the role OR are the same user as defined for the type. If no
		// role or user are defined for the type then all users can process the type. Super users always have access to
		// process every type.

		if (currentUser.isSuperUser()) {
			return true;
		}

		// Assume that current user can process operation
		boolean canProcess = true;

		Role role = this.getRole();
		User user = this.getUser();

		// If operation type has role restriction
		if (role != null) {
			if (!currentUser.hasRole(role.getRole())) {
				canProcess = false;
			}
		}

		// If there is a user restriction and either the role test did not pass or if there is no role test
		if (user != null && ((role != null && !canProcess) || (role == null))) {
			if (currentUser.getUserId().equals(user.getUserId())) {
				canProcess = true;
			} else {
				canProcess = false;
			}
		}

		return canProcess;
	}

	protected Set<StockOperationTransaction> executeCopyReserved(StockOperation operation,
	        Action2<ReservedTransaction, StockOperationTransaction> action) {
		Set<StockOperationTransaction> transactions = new TreeSet<StockOperationTransaction>();

		// Loop through the reserved transactions
		for (ReservedTransaction inTransit : operation.getReserved()) {
			// Create a new operation transaction as a copy of the reserved transaction
			StockOperationTransaction tx = new StockOperationTransaction(inTransit);

			// Apply the action
			action.apply(inTransit, tx);

			// Add the operation transaction to the operation
			operation.addTransaction(tx);

			// Add the operation transaction to the copied transaction list
			transactions.add(tx);
		}

		IStockOperationService service = Context.getService(IStockOperationService.class);
		service.applyTransactions(transactions);

		return transactions;
	}

	protected Set<StockOperationTransaction> executeCopyReservedAndClear(StockOperation operation,
	        Action2<ReservedTransaction, StockOperationTransaction> action) {
		Set<StockOperationTransaction> result = executeCopyReserved(operation, action);

		// Clear out the transactions for the operation
		operation.getReserved().clear();

		return result;
	}
}
