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
 * Model class that represents an adjustment stock operation type. Adjustment operations directly alter the item stock for a
 * given stockroom and are used to correct item stock mistakes.
 */
public class AdjustmentOperationType extends StockOperationTypeBase {
	/**
	 * Specifies whether the quantity should be negated when it is applied. This allows sub-classes to change the default
	 * adjustment behavior.
	 * @return
	 */
	protected boolean negateAppliedQuantity() {
		// Note that the quantity is NOT negated because the adjustment quantity is the difference
		return false;
	}

	@Override
	public boolean isNegativeItemQuantityAllowed() {
		return true;
	}

	@Override
	public void onPending(final StockOperation operation) {
		executeCopyReserved(operation, new Action2<ReservedTransaction, StockOperationTransaction>() {
			@Override
			public void apply(ReservedTransaction reserved, StockOperationTransaction tx) {
				tx.setStockroom(operation.getSource());

				if (negateAppliedQuantity()) {
					tx.setQuantity(tx.getQuantity() * -1);
				}
			}
		});
	}

	@Override
	public void onCancelled(final StockOperation operation) {
		executeCopyReservedAndClear(operation, new Action2<ReservedTransaction, StockOperationTransaction>() {
			@Override
			public void apply(ReservedTransaction reserved, StockOperationTransaction tx) {
				tx.setStockroom(operation.getSource());

				// Undo the previously applied transaction by setting the quantity to the opposite of the pending transaction
				if (!negateAppliedQuantity()) {
					tx.setQuantity(tx.getQuantity() * -1);
				}
			}
		});
	}

	@Override
	public void onCompleted(StockOperation operation) {
		// Clear out the transactions for the operation
		operation.getReserved().clear();
	}
}
