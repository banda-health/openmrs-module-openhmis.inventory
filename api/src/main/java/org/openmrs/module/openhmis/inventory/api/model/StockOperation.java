package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.model.BaseCustomizableInstanceMetadata;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents an operation performed on item stock.  Examples of an operation are things like transferring
 * item stock from one stockroom to another or selling item stock to a patient.  Each of these types of operations is
 * modeled by a specific {@link IStockOperationType} class that defines what happens when a stock operation is created
 * (PENDING), cancelled, or completed.
 *
 * A stock operation is composed of stock transactions, each of which is a change in item stock for a specific
 * stockroom.  When a stock operation is created transactions the item stock to be operated upon are added to the
 * {@code reserved} set.  As the stock operation status changes the item stock is updated as per the settings for the
 * operation type.
 */
public class StockOperation
		extends BaseCustomizableInstanceMetadata<IStockOperationType, StockOperationAttribute>
		implements Comparable<StockOperation> {
	public static final long serialVersionUID = 0L;

	private Integer id;
	private StockOperationStatus status;
	private Set<ReservedTransaction> reserved;
	private Set<StockOperationTransaction> transactions;

	private String operationNumber;
	private Date operationDate;
	protected StockRoom source;
	protected StockRoom destination;
	protected Patient patient;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public StockOperationStatus getStatus() {
		return status;
	}

	public void setStatus(StockOperationStatus status) {
		this.status = status;
	}

	public String getOperationNumber() {
		return operationNumber;
	}

	public void setOperationNumber(String operationNumber) {
		this.operationNumber = operationNumber;
	}

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public StockRoom getSource() {
		return source;
	}

	public void setSource(StockRoom newSource) {
		if (this.source == newSource) {
			return;
		}

		// If source exists then remove this operation from it
		if (this.source != null && this.source != this.destination) {
			this.source.removeOperation(this);
		}

		// Update the source
		this.source = newSource;

		// If the new source is not null then add this operation to it
		if (this.source != null) {
			this.source.addOperation(this);
		}
	}

	public StockRoom getDestination() {
		return destination;
	}

	public void setDestination(StockRoom newDestination) {
		if (this.destination == newDestination) {
			return;
		}

		// If destination exists then remove this operation from it
		if (this.destination != null && this.destination != this.source) {
			this.destination.removeOperation(this);
		}

		// Update the destination
		this.destination = newDestination;

		// If the new destination is not null then add this operation to it
		if (this.destination != null) {
			this.destination.addOperation(this);
		}
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public ReservedTransaction addReserved(Item item, int quantity) {
		return addReserved(item, quantity, null);
	}

	/**
	 * Adds a new reserved item.
	 * @param item The item to add.
	 * @param quantity The item quantity.
	 * @param expiration The item expiration or {@code null} if none.
	 * @return The newly created reserved item transaction.
	 */
	public ReservedTransaction addReserved(Item item, int quantity, Date expiration) {
		if (item == null) {
			throw new IllegalArgumentException("The item must be defined.");
		}

		ReservedTransaction tx = new ReservedTransaction();
		tx.setItem(item);
		tx.setQuantity(quantity);
		tx.setExpiration(expiration);
		tx.setCreator(Context.getAuthenticatedUser());
		tx.setDateCreated(new Date());

		return addReserved(tx);
	}

	/**
	 * Adds a new reserved item.
	 * @param tx The reserved item transaction to add.
	 * @return The saved reserved item transaction.
	 */
	public ReservedTransaction addReserved(ReservedTransaction tx) {
		if (tx == null) {
			throw new IllegalArgumentException("The transaction to add must be defined.");
		}

		if (reserved == null) {
			reserved = new HashSet<ReservedTransaction>();
		}

		tx.setOperation(this);
		tx.setAvailable(getInstanceType() != null ? getInstanceType().getAvailableWhenReserved() : false);

		reserved.add(tx);

		return tx;
	}

	public void removeReserved(ReservedTransaction tx) {
		if (tx != null) {
			if (reserved == null) {
				return;
			}

			reserved.remove(tx);
		}
	}

	public Set<ReservedTransaction> getReserved() {
		return reserved;
	}

	public void setReserved(Set<ReservedTransaction> reserved) {
		this.reserved = reserved;
	}

	public StockOperationTransaction addTransaction(StockOperationTransaction tx) {
		if (tx == null) {
			throw new IllegalArgumentException("The transaction to add must be defined.");
		}
		if (tx.getItem() == null) {
			throw new IllegalArgumentException("The transaction item must be defined.");
		}
		if (getInstanceType() == null) {
			throw new IllegalArgumentException("The operation type must be defined before adding transactions");
		}

		if (transactions == null) {
			transactions = new HashSet<StockOperationTransaction>();
		}

		tx.setOperation(this);

		transactions.add(tx);

		return tx;
	}

	public Set<StockOperationTransaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<StockOperationTransaction> transactions) {
		this.transactions = transactions;
	}

	@Override
	public int compareTo(StockOperation o) {
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

