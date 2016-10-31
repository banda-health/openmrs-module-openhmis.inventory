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
 * Model class that represents a transfer stock operation type. Transfers move item stock from one stockroom to another.
 */
public class TransferOperationType extends StockOperationTypeBase {

	@Override
	public boolean isNegativeItemQuantityAllowed() {
		return false;
	}

	@Override
	public void onPending(final StockOperation operation) {
		// Remove the item stock from the source stockroom
		executeCopyReserved(operation, new Action2<ReservedTransaction, StockOperationTransaction>() {
			@Override
			public void apply(ReservedTransaction reserved, StockOperationTransaction tx) {
				tx.setStockroom(operation.getSource());

				// Negate the quantity because the item stock needs to be removed from the source stockroom
				tx.setQuantity(tx.getQuantity() * -1);
			}
		});
	}

	@Override
	public void onCancelled(final StockOperation operation) {
		// Re-add the previously removed item stock back into the source stockroom
		executeCopyReservedAndClear(operation, new Action2<ReservedTransaction, StockOperationTransaction>() {
			@Override
			public void apply(ReservedTransaction reserved, StockOperationTransaction tx) {
				tx.setStockroom(operation.getSource());
			}
		});
	}

	@Override
	public void onCompleted(final StockOperation operation) {
		// Add the item stock to the destination stockroom
		executeCopyReservedAndClear(operation, new Action2<ReservedTransaction, StockOperationTransaction>() {
			@Override
			public void apply(ReservedTransaction reserved, StockOperationTransaction tx) {
				tx.setStockroom(operation.getDestination());
			}
		});
	}
}
