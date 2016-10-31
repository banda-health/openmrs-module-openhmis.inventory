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

import org.openmrs.module.openhmis.commons.api.f.Action2;

/**
 * Model class that represents a distribution stock operation type. Distribution operations remove item stock from the system
 * and record who received it.
 */
public class DistributionOperationType extends StockOperationTypeBase {

	@Override
	public boolean isNegativeItemQuantityAllowed() {
		return false;
	}

	@Override
	public void onPending(final StockOperation operation) {
		executeCopyReserved(operation, new Action2<ReservedTransaction, StockOperationTransaction>() {
			@Override
			public void apply(ReservedTransaction reserved, StockOperationTransaction tx) {
				tx.setStockroom(operation.getSource());
				tx.setPatient(operation.getPatient());
				tx.setInstitution(operation.getInstitution());

				// Negate the quantity because the item stock needs to be removed from the source stockroom
				tx.setQuantity(tx.getQuantity() * -1);
			}
		});
	}

	@Override
	public void onCancelled(final StockOperation operation) {
		executeCopyReservedAndClear(operation, new Action2<ReservedTransaction, StockOperationTransaction>() {
			@Override
			public void apply(ReservedTransaction reserved, StockOperationTransaction tx) {
				tx.setStockroom(operation.getSource());
			}
		});
	}

	@Override
	public void onCompleted(StockOperation operation) {
		// Clear out the transactions for the operation
		operation.getReserved().clear();
	}
}
