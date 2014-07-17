package org.openmrs.module.openhmis.inventory.api.model;

public class StockOperationItem extends ItemStockDetailBase {
	private StockOperation operation;

	public StockOperation getOperation() {
		return operation;
	}

	public void setOperation(StockOperation operation) {
		this.operation = operation;
	}
}
