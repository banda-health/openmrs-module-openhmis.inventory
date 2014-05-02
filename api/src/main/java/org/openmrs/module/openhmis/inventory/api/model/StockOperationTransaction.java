package org.openmrs.module.openhmis.inventory.api.model;


public class StockOperationTransaction extends TransactionBase {
	public static final long serialVersionUID = 0L;

	private Stockroom stockroom;
	private Recipient recipient;

	public StockOperationTransaction() {	}

	public StockOperationTransaction(TransactionBase tx) {
		super(tx);
	}

	public Stockroom getStockroom() {
		return stockroom;
	}

	public void setStockroom(Stockroom stockroom) {
		this.stockroom = stockroom;
	}

	public Recipient getRecipient() {
		return recipient;
	}

	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}
}
