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

import org.openmrs.module.openhmis.commons.api.entity.model.BaseCustomizableInstanceMetadata;

import java.util.Set;

public class StockRoomTransaction extends BaseCustomizableInstanceMetadata<StockRoomTransactionAttribute> {
	public static final long serialVersionUID = 0L;

	private Integer stockRoomTransferId;
	private String transactionNumber;
	private StockRoomTransactionType transactionType;
	private StockRoomTransactionStatus status;
	private StockRoom source;
	private StockRoom destination;
	private Set<StockRoomTransactionItem> items;

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

	public void setSource(StockRoom source) {
		this.source = source;
	}

	public StockRoom getDestination() {
		return destination;
	}

	public void setDestination(StockRoom destination) {
		this.destination = destination;
	}

	public Set<StockRoomTransactionItem> getItems() {
		return items;
	}

	public void setItems(Set<StockRoomTransactionItem> items) {
		this.items = items;
	}
}
