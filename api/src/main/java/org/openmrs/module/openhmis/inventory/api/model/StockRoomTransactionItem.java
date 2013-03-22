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

import org.openmrs.BaseOpenmrsObject;

import java.util.Date;

public class StockRoomTransactionItem extends BaseOpenmrsObject {
	public static final long serialVersionUID = 0L;

	private Integer stockRoomTransactionItemId;
	private StockRoomTransaction transaction;
	private ImportBatch importBatch;
	private Item item;
	private int quantityOrdered;       // The item quantity requested for this transaction
	private int quantityTransferred;   // The transferred item quantity while the transaction is Pending
	private int quantityReserved;      // The reserved item quantity while the transaction is Pending
	private Date expiration;

	@Override
	public Integer getId() {
		return stockRoomTransactionItemId;
	}

	@Override
	public void setId(Integer id) {
		this.stockRoomTransactionItemId = id;
	}

	public StockRoomTransaction getTransaction() {
		return transaction;
	}

	public void setTransaction(StockRoomTransaction transaction) {
		this.transaction = transaction;
	}

	public ImportBatch getImportBatch() {
		return importBatch;
	}

	public void setImportBatch(ImportBatch importBatch) {
		this.importBatch = importBatch;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getQuantityOrdered() {
		return quantityOrdered;
	}

	public void setQuantityOrdered(int quantityOrdered) {
		this.quantityOrdered = quantityOrdered;
	}

	public int getQuantityTransferred() {
		return quantityTransferred;
	}

	public void setQuantityTransferred(int quantityTransferred) {
		this.quantityTransferred = quantityTransferred;
	}

	public int getQuantityReserved() {
		return quantityReserved;
	}

	public void setQuantityReserved(int quantityReserved) {
		this.quantityReserved = quantityReserved;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
}
