package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;
import org.openmrs.api.context.Context;

import java.util.Date;

public abstract class TransactionBase
		extends BaseOpenmrsObject
		implements Comparable<TransactionBase> {
	public static final long serialVersionUID = 0L;

	private Integer id;
	private StockOperation operation;

	private Item item;
	private Integer quantity;
	private Date expiration;
	private StockOperation batchOperation;
	private Boolean calculatedExpiration;
	private Boolean sourceCalculatedExpiration;
	private Boolean calculatedBatch;
	private Boolean sourceCalculatedBatch;

	private User creator;
	private Date dateCreated = new Date();

	protected TransactionBase() { }

	protected TransactionBase(TransactionBase tx) {
		operation = tx.operation;
		creator = tx.creator;

		item = tx.item;
		expiration = tx.expiration;
		batchOperation = tx.batchOperation;
		quantity = tx.quantity;
		calculatedExpiration = tx.calculatedExpiration;
		calculatedBatch = tx.calculatedBatch;
	}

	protected TransactionBase(StockOperationItem item) {
		this.item = item.getItem();
		this.expiration = item.getExpiration();
		this.batchOperation = item.getBatchOperation();
		this.quantity = item.getQuantity();
		this.calculatedBatch = item.isCalculatedBatch();
		this.calculatedExpiration = item.isCalculatedExpiration();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public StockOperation getOperation() {
		return operation;
	}

	public void setOperation(StockOperation operation) {
		this.operation = operation;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public StockOperation getBatchOperation() {
		return batchOperation;
	}

	public void setBatchOperation(StockOperation batchOperation) {
		this.batchOperation = batchOperation;
	}

	public Boolean isCalculatedExpiration() {
		return calculatedExpiration;
	}

	public void setCalculatedExpiration(Boolean calculatedExpiration) {
		this.calculatedExpiration = calculatedExpiration;
	}

	public Boolean isSourceCalculatedExpiration() {
		return sourceCalculatedExpiration;
	}

	public void setSourceCalculatedExpiration(Boolean sourceCalculatedExpiration) {
		this.sourceCalculatedExpiration = sourceCalculatedExpiration;
	}

	public Boolean isCalculatedBatch() {
		return calculatedBatch;
	}

	public void setCalculatedBatch(Boolean calculatedBatch) {
		this.calculatedBatch = calculatedBatch;
	}

	public Boolean isSourceCalculatedBatch() {
		return sourceCalculatedBatch;
	}

	public void setSourceCalculatedBatch(Boolean sourceCalculatedBatch) {
		this.sourceCalculatedBatch = sourceCalculatedBatch;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Override
	public int compareTo(TransactionBase o) {
		if (o == null) {
			return 1;
		}

		int result = 0;

		if (getId() != null && o.getId() != null) {
			result = getId().compareTo(o.getId());
		}

		if (result == 0) {
			result = getUuid().compareTo(o.getUuid());
		}

		return result;
	}
}
