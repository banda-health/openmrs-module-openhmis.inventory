package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.module.openhmis.commons.api.f.Action2;

public class TransferOperationType extends StockOperationTypeBase {
	@Override
	public void onPending(final StockOperation operation) {
		// Remove the item stock from the source stockroom
		executeCopyReserved(operation, new Action2<ReservedTransaction, StockOperationTransaction>() {
			@Override
			public void apply(ReservedTransaction reserved, StockOperationTransaction tx) {
				tx.setStockRoom(operation.getSource());

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
				tx.setStockRoom(operation.getSource());
			}
		});
	}

	@Override
	public void onCompleted(final StockOperation operation) {
		// Add the item stock to the destination stockroom
		executeCopyReservedAndClear(operation, new Action2<ReservedTransaction, StockOperationTransaction>() {
			@Override
			public void apply(ReservedTransaction reserved, StockOperationTransaction tx) {
				tx.setStockRoom(operation.getDestination());
			}
		});
	}
}
