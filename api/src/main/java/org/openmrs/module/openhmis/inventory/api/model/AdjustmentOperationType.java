package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.module.openhmis.commons.api.f.Action2;

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
