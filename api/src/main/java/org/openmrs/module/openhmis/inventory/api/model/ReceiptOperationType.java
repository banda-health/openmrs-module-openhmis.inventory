package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.module.openhmis.commons.api.f.Action2;

public class ReceiptOperationType extends StockOperationTypeBase {
	@Override
	public void onPending(StockOperation operation) {
		// Don't need to do anything
	}

	@Override
	public void onCancelled(StockOperation operation) {
		// Clear out the transactions for the operation
		operation.getReserved().clear();
	}

	@Override
	public void onCompleted(final StockOperation operation) {
		executeCopyReservedAndClear(operation, new Action2<ReservedTransaction, StockOperationTransaction>() {
			@Override
			public void apply(ReservedTransaction reserved, StockOperationTransaction tx) {
				tx.setStockroom(operation.getDestination());
			}
		});
	}
}

