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

/**
 * Model class the represents item stock information qualified by expiration date and batch operation.
 */
public class ItemStockDetail extends BaseOpenmrsObject {
	public static final long serialVersionUID = 0L;

	private Integer stockroomItemId;
	private ItemStock itemStock;
	private Stockroom stockroom;
	private Item item;
	private int quantity;
	private Date expiration;
	private StockOperation batchOperation;
	private boolean calculatedExpiration;
	private boolean calculatedBatch;

	/**
	 * Creates a new empty {@link ItemStockDetail} object.
	 */
	public ItemStockDetail() { }

	/**
	 * Create a new {@link ItemStockDetail} object from the specified {@link ItemStock} and
	 * {@link StockOperationTransaction} objects.
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
		this.setExpiration(tx.getExpiration());
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
		this.item = base.item;
		if (base.expiration != null) {
			this.expiration = (Date)base.expiration.clone();
		}
		this.batchOperation = base.batchOperation;
		this.calculatedBatch = base.calculatedBatch;
		this.calculatedExpiration = base.calculatedExpiration;
		this.quantity = base.quantity;
	}

	/**
	 * Gets the unique database record identifier.
	 * @return The record identifier or {@code null} if the object is new.
	 */
	@Override
	public Integer getId() {
		return stockroomItemId;
	}

	/**
	 * Sets the unique database record identifier.
	 * @return The record identifier or {@code null} if the object is new.
	 */
	@Override
	public void setId(Integer id) {
		stockroomItemId = id;
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

	/**
	 * Gets the item.
	 * @return The item.
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * Sets the item.
	 * @param item The item.
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * Gets the quantity.
	 * @return The quantity.
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Sets the quantity.
	 * @param quantity The quantity.
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Gets the item expiration date.
	 * @return The item expiration date or {@code null} if there is no expiration.
	 */
	public Date getExpiration() {
		return expiration;
	}

	/**
	 * Sets the item expiration date.
	 * @param expiration The item expiration date or {@code null} if there is no expiration.
	 */
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	/**
	 * Gets the batch {@link StockOperation} that brought this item stock into the system.
	 * @return The batch operation or {@code null} if it is unknown.
	 */
	public StockOperation getBatchOperation() {
		return batchOperation;
	}

	/**
	 * Sets the batch {@link StockOperation} that brought this item stock into the system.
	 * @param batchOperation The batch operation or {@code null} if it is unknown.
	 */
	public void setBatchOperation(StockOperation batchOperation) {
		this.batchOperation = batchOperation;
	}

	/**
	 * Gets whether the expiration date was calculated by the owning {@link StockOperation}.
	 *
	 * Details that are calculated can be recalculated when historical operations are added to the system.
	 * @return Whether the expiration date was calculated.
	 */
	public boolean isCalculatedExpiration() {
		return calculatedExpiration;
	}

	/**
	 * Sets whether the expiration date was calculated by the owning {@link StockOperation}.
	 *
	 * Details that are calculated can be recalculated when historical operations are added to the system.
	 * @oaran calculatedExpiration Whether the expiration date was calculated.
	 */
	public void setCalculatedExpiration(boolean calculatedExpiration) {
		this.calculatedExpiration = calculatedExpiration;
	}

	/**
	 * Gets whether the batch operation was calculated by the owning {@link StockOperation}.
	 *
	 * Details that are calculated can be recalculated when historical operations are added to the system.
	 * @return Whether the batch operation was calculated.
	 */
	public boolean isCalculatedBatch() {
		return calculatedBatch;
	}

	/**
	 * Sets whether the batch operation was calculated by the owning {@link StockOperation}.
	 *
	 * Details that are calculated can be recalculated when historical operations are added to the system.
	 * @param calculatedBatch Whether the batch operation was calculated.
	 */
	public void setCalculatedBatch(boolean calculatedBatch) {
		this.calculatedBatch = calculatedBatch;
	}
}
