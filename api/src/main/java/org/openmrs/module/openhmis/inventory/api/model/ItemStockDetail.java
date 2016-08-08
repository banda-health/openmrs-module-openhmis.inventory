/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and
 * limitations under the License.
 *
 * Copyright (C) OpenHMIS.  All Rights Reserved.
 */
package org.openmrs.module.openhmis.inventory.api.model;

import java.util.Date;

/**
 * Model class the represents item stock information qualified by expiration date and batch operation.
 */
public class ItemStockDetail extends ItemStockDetailBase {
	public static final long serialVersionUID = 0L;

	private ItemStock itemStock;
	private Stockroom stockroom;

	/**
	 * Creates a new empty {@link ItemStockDetail} object.
	 */
	public ItemStockDetail() {}

	/**
	 * Create a new {@link ItemStockDetail} object from the specified {@link ItemStock} and {@link StockOperationTransaction}
	 * objects.
	 * @param stock The item stock this stock detail is related to.
	 * @param tx The operation transaction used to populate the detail properties.
	 */
	public ItemStockDetail(ItemStock stock, StockOperationTransaction tx) {
		if (stock == null) {
			throw new IllegalArgumentException("The item stock must be defined.");
		}
		if (tx == null) {
			throw new IllegalArgumentException("The stock operation transaction must be defined.");
		}

		this.setItemStock(stock);
		this.setStockroom(tx.getStockroom());
		this.setItem(tx.getItem());
		if (tx.getExpiration() != null) {
			this.setExpiration((Date)tx.getExpiration().clone());
		}
		this.setBatchOperation(tx.getBatchOperation());
		this.setCalculatedExpiration(tx.isCalculatedExpiration());
		this.setCalculatedBatch(tx.isCalculatedBatch());
		this.setQuantity(tx.getQuantity());
	}

	/**
	 * Create a copy of the specified {@link ItemStockDetail}.
	 * @param base The detail to copy.
	 */
	public ItemStockDetail(ItemStockDetail base) {
		if (base == null) {
			throw new IllegalArgumentException("The item stock detail to copy must be defined.");
		}

		this.itemStock = base.itemStock;
		this.stockroom = base.stockroom;
		setItem(base.getItem());
		if (base.getExpiration() != null) {
			this.setExpiration((Date)base.getExpiration().clone());
		}
		setBatchOperation(base.getBatchOperation());
		setCalculatedBatch(base.isCalculatedBatch());
		setCalculatedExpiration(base.isCalculatedExpiration());
		setQuantity(base.getQuantity());
	}

	/**
	 * Gets the parent {@link ItemStock} object.
	 * @return The item stock.
	 */
	public ItemStock getItemStock() {
		return itemStock;
	}

	/**
	 * Sets the parent {@link ItemStock} object.
	 * @param itemStock The item stock.
	 */
	public void setItemStock(ItemStock itemStock) {
		this.itemStock = itemStock;
	}

	/**
	 * Gets the stockroom.
	 * @return The stockroom.
	 */
	public Stockroom getStockroom() {
		return stockroom;
	}

	/**
	 * Sets the stockroom.
	 * @param stockroom The stockroom.
	 */
	public void setStockroom(Stockroom stockroom) {
		this.stockroom = stockroom;
	}

	public boolean isNullBatch() {
		return (this.getBatchOperation() == null && this.getExpiration() == null && this.getQuantity() < 0) ? true : false;
	}
}
