package org.openmrs.module.openhmis.inventory.api.model;

public class ReservedTransaction extends TransactionBase {
	public static final long serialVersionUID = 0L;

	private Boolean available;

	public ReservedTransaction() { }
	public ReservedTransaction(TransactionBase tx) {
		super(tx);
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}
}
