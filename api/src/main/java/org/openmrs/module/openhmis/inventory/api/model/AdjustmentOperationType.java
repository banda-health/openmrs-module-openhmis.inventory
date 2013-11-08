package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.module.openhmis.commons.api.f.Action2;

public class AdjustmentOperationType extends StockOperationTypeBase {
	@Override
	public void onPending(final StockOperation operation) {
		executeCopyReserved(operation, new Action2<ReservedTransaction, StockOperationTransaction>() {
			@Override
			public void apply(ReservedTransaction reserved, StockOperationTransaction tx) {
				tx.setStockRoom(operation.getSource());

				// Note that the quantity is NOT negated here because the adjustment quantity is the difference
			}
		});
	}

	@Override
	public void onCancelled(final StockOperation operation) {
		executeCopyReservedAndClear(operation, new Action2<ReservedTransaction, StockOperationTransaction>() {
			@Override
			public void apply(ReservedTransaction reserved, StockOperationTransaction tx) {
				tx.setStockRoom(operation.getSource());

				// Negate the quantity because we want to undo the previously applied quantity difference
				tx.setQuantity(tx.getQuantity() * -1);
			}
		});
	}

	@Override
	public void onCompleted(StockOperation operation) {
		// Clear out the transactions for the operation
		operation.getReserved().clear();
	}
}
