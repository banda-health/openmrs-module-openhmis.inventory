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

import java.util.HashSet;
import java.util.Set;

/**
 * Model class that represents item stock in a stockroom.  The item stock quantity is the overall item quantity in the
 * stockroom and is not qualified (that is, broken down) by expiration or batch operation. Those quantities are stored
 * in the item stock details.
 */
public class ItemStock extends BaseOpenmrsObject
	implements Comparable<ItemStock> {
	public static final long serialVersionUID = 1L;

	private Integer stockRoomItemId;
	private StockRoom stockRoom;
	private Item item;
	private int quantity;
	private Set<ItemStockDetail> details;

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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public ItemStockDetail addDetail(ItemStockDetail detail) {
		if (detail == null) {
			throw new IllegalArgumentException("The detail record to add must be defined");
		}

		if (details == null) {
			details = new HashSet<ItemStockDetail>();
		}

		detail.setItemStock(this);
		details.add(detail);

		return detail;
	}

	public void removeDetail(ItemStockDetail detail) {
		if (detail!= null) {
			if (details == null) {
				return;
			}

			detail.setItemStock(null);
			details.remove(detail);
		}
	}

	public Set<ItemStockDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<ItemStockDetail> details) {
		this.details = details;
	}

	@Override
	public int compareTo(ItemStock o) {
		// The default sorting uses the item name and then uuid if the name is the same
		int result = this.getItem().getName().compareTo(o.getItem().getName());

		if (result == 0) {
			result = this.getUuid().compareTo(o.getUuid());
		}

		return result;
	}
}

