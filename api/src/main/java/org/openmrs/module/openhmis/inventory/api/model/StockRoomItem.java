/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
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

public class StockRoomItem extends BaseOpenmrsObject
	implements Comparable<StockRoomItem> {
	public static final long serialVersionUID = 0L;

	private Integer stockRoomItemId;
	private StockRoom stockRoom;
	private StockRoomTransaction importTransaction;
	private Item item;
	private int quantity;
	private Date expiration;

	@Override
	public Integer getId() {
		return stockRoomItemId;
	}

	@Override
	public void setId(Integer id) {
		stockRoomItemId = id;
	}

	public StockRoom getStockRoom() {
		return stockRoom;
	}

	public void setStockRoom(StockRoom stockRoom) {
		this.stockRoom = stockRoom;
	}

	public StockRoomTransaction getImportTransaction() {
		return importTransaction;
	}

	public void setImportTransaction(StockRoomTransaction importTransaction) {
		this.importTransaction = importTransaction;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	@Override
	public int compareTo(StockRoomItem o) {
		// The default sorting uses the item name
		return this.getItem().getName().compareTo(o.getItem().getName());
	}
}

