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

import org.openmrs.BaseOpenmrsObject;

/**
 * Base model class used by models that have item stock detail information.
 */
public class ItemStockDetailBase extends BaseOpenmrsObject {
	public static final long serialVersionUID = 0L;

	private Integer id;

	private Item item;
	private Integer quantity;
	private Date expiration;
	private StockOperation batchOperation;
	private Boolean calculatedExpiration;
	private Boolean calculatedBatch;

	/**
	 * Gets the unique database record identifier.
	 * @return The record identifier or {@code null} if the object is new.
	 */
	@Override
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the unique database record identifier.
	 * @return The record identifier or {@code null} if the object is new.
	 */
	@Override
	public void setId(Integer id) {
		this.id = id;
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
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * Sets the quantity.
	 * @param quantity The quantity.
	 */
	public void setQuantity(Integer quantity) {
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
	 * Gets the batch {@link org.openmrs.module.openhmis.inventory.api.model.StockOperation} that brought this item stock
	 * into the system.
	 * @return The batch operation or {@code null} if it is unknown.
	 */
	public StockOperation getBatchOperation() {
		return batchOperation;
	}

	/**
	 * Sets the batch {@link org.openmrs.module.openhmis.inventory.api.model.StockOperation} that brought this item stock
	 * into the system.
	 * @param batchOperation The batch operation or {@code null} if it is unknown.
	 */
	public void setBatchOperation(StockOperation batchOperation) {
		this.batchOperation = batchOperation;
	}

	/**
	 * Gets whether the expiration date was calculated by the owning
	 * {@link org.openmrs.module.openhmis.inventory.api.model.StockOperation}. Details that are calculated can be
	 * recalculated when historical operations are added to the system.
	 * @return Whether the expiration date was calculated.
	 */
	public Boolean isCalculatedExpiration() {
		return calculatedExpiration;
	}

	public Boolean getCalculatedExpiration() {
		return calculatedExpiration;
	}

	/**
	 * Sets whether the expiration date was calculated by the owning
	 * {@link org.openmrs.module.openhmis.inventory.api.model.StockOperation}. Details that are calculated can be
	 * recalculated when historical operations are added to the system.
	 * @oaran calculatedExpiration Whether the expiration date was calculated.
	 */
	public void setCalculatedExpiration(Boolean calculatedExpiration) {
		this.calculatedExpiration = calculatedExpiration;
	}

	/**
	 * Gets whether the batch operation was calculated by the owning
	 * {@link org.openmrs.module.openhmis.inventory.api.model.StockOperation}. Details that are calculated can be
	 * recalculated when historical operations are added to the system.
	 * @return Whether the batch operation was calculated.
	 */
	public Boolean isCalculatedBatch() {
		return calculatedBatch;
	}

	public Boolean getCalculatedBatch() {
		return calculatedBatch;
	}

	/**
	 * Sets whether the batch operation was calculated by the owning
	 * {@link org.openmrs.module.openhmis.inventory.api.model.StockOperation}. Details that are calculated can be
	 * recalculated when historical operations are added to the system.
	 * @param calculatedBatch Whether the batch operation was calculated.
	 */
	public void setCalculatedBatch(Boolean calculatedBatch) {
		this.calculatedBatch = calculatedBatch;
	}
}
