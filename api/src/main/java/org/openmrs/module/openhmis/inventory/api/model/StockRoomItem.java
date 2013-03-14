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

public class StockRoomItem extends BaseOpenmrsObject {
	public static final long serialVersionUID = 0L;

	private Integer stockRoomItemId;
	private StockRoom stockRoom;
	private Item item;
	private Long quantity;

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

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
}

