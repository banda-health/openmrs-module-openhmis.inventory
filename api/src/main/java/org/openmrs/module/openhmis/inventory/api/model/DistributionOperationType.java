package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.module.openhmis.commons.api.f.Action2;

public class DistributionOperationType extends StockOperationTypeBase {
	@Override
	public void onPending(final StockOperation operation) {
		executeCopyReserved(operation, new Action2<ReservedTransaction, StockOperationTransaction>() {
			@Override
			public void apply(ReservedTransaction reserved, StockOperationTransaction tx) {
				tx.setStockroom(operation.getSource());
				tx.setRecipient(operation.getRecipient());

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

