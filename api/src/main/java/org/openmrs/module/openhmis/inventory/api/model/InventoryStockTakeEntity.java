package org.openmrs.module.openhmis.inventory.api.model;


public class InventoryStockTakeEntity extends ItemStockDetail {

	private Integer actualQuantity;

	public Integer getActualQuantity() {
		return actualQuantity;
	}

	public void setActualQuantity(Integer actualQuantity) {
		this.actualQuantity = actualQuantity;
	}
}
