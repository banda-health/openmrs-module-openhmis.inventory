package org.openmrs.module.openhmis.inventory.model;

import java.util.List;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockSummary;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;

public class InventoryStockTake extends BaseOpenmrsObject {
	public static final long serialVersionUID = 0L;

	private String operationNumber;
	private Stockroom stockroom;
	private List<ItemStockSummary> itemStockSummaryList;

	public String getOperationNumber() {
		return operationNumber;
	}

	public void setOperationNumber(String operationNumber) {
		this.operationNumber = operationNumber;
	}

	public Stockroom getStockroom() {
		return stockroom;
	}

	public void setStockroom(Stockroom stockroom) {
		this.stockroom = stockroom;
	}

	public List<ItemStockSummary> getItemStockSummaryList() {
		return itemStockSummaryList;
	}

	public void setItemStockSummaryList(List<ItemStockSummary> itemStockSummaryList) {
		this.itemStockSummaryList = itemStockSummaryList;
	}

	@Override
	public Integer getId() {
		return null;
	}

	@Override
	public void setId(Integer id) {

	}
}
