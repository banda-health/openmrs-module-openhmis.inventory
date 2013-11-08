package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.Patient;

public class StockOperationTransaction extends TransactionBase {
	public static final long serialVersionUID = 0L;

	private StockRoom stockRoom;
	private Patient patient;

	public StockOperationTransaction() {	}

	public StockOperationTransaction(TransactionBase tx) {
		super(tx);
	}

	public StockRoom getStockRoom() {
		return stockRoom;
	}

	public void setStockRoom(StockRoom stockRoom) {
		this.stockRoom = stockRoom;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
}
