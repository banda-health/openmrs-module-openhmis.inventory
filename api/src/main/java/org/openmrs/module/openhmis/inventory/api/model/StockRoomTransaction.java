/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.api.model;

import org.openmrs.Attributable;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.model.BaseCustomizableInstanceObject;
import org.openmrs.module.openhmis.inventory.api.IStockRoomTransactionDataService;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class StockRoomTransaction extends BaseCustomizableInstanceObject<StockRoomTransactionAttribute>
	implements Comparable<StockRoomTransaction>, Attributable<StockRoomTransaction> {
	public static final long serialVersionUID = 0L;

	private Integer stockRoomTransferId;
	private String transactionNumber;
	private StockRoomTransactionType transactionType;
	private StockRoomTransactionStatus status;
	private StockRoom source;
	private StockRoom destination;
	private Set<StockRoomTransactionItem> items;
	private Boolean isImportTransaction;
	private User creator;
	private Date dateCreated;

	@Override
	public Integer getId() {
		return stockRoomTransferId;
	}

	@Override
	public void setId(Integer id) {
		stockRoomTransferId = id;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public StockRoomTransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(StockRoomTransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public StockRoomTransactionStatus getStatus() {
		return status;
	}

	public void setStatus(StockRoomTransactionStatus status) {
		this.status = status;
	}

	public StockRoom getSource() {
		return source;
	}

	public void setSource(StockRoom newSource) {
		if (this.source == newSource) {
			return;
		}

		// If source exists then remove this transaction from it
		if (this.source != null && this.source != this.destination) {
			this.source.removeTransaction(this);
		}

		// Update the source
		this.source = newSource;

		// If the new source is not null then add this transaction to it
		if (this.source != null) {
			this.source.addTransaction(this);
		}
	}

	public StockRoom getDestination() {
		return destination;
	}

	public void setDestination(StockRoom newDestination) {
		if (this.destination == newDestination) {
			return;
		}

		// If destination exists then remove this transaction from it
		if (this.destination != null && this.destination != this.source) {
			this.destination.removeTransaction(this);
		}

		// Update the destination
		this.destination = newDestination;

		// If the new destination is not null then add this transaction to it
		if (this.destination != null) {
			this.destination.addTransaction(this);
		}
	}

	public Set<StockRoomTransactionItem> getItems() {
		return items;
	}

	public void setItems(Set<StockRoomTransactionItem> items) {
		this.items = items;
	}

	public void addItem(StockRoomTransactionItem item) {
		if (item != null) {
			if (items == null) {
				items = new TreeSet<StockRoomTransactionItem>();
			}

			item.setTransaction(this);
			items.add(item);
		}
	}

	public void removeItem(StockRoomTransactionItem item) {
		if (item != null) {
			if (items == null) {
				return;
			}

			item.setTransaction(null);
			items.remove(item);
		}
	}

	public Boolean isImportTransaction() {
		return getImportTransaction();
	}

	public Boolean getImportTransaction() {
		return isImportTransaction;
	}

	public void setImportTransaction(Boolean importTransaction) {
		isImportTransaction = importTransaction;
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
	public int compareTo(StockRoomTransaction o) {
		// The default sorting uses the transaction creation date
		if (this.getDateCreated() == null || this.getDateCreated() == null) {
			return 0;
		}
		
		return this.getDateCreated().compareTo(o.getDateCreated());
	}

	@Override
	public StockRoomTransaction hydrate(String s) {
		return Context.getService(IStockRoomTransactionDataService.class).getById(Integer.parseInt(s));
	}

	@Override
	public String serialize() {
		if (getId() != null)
			return "" + getId();
		return "";
	}

	@Override
	public List<StockRoomTransaction> getPossibleValues() { return null; }

	@Override
	public List<StockRoomTransaction> findPossibleValues(String searchText) { return null; }

	@Override
	public String getDisplayString() { return null; }
}
