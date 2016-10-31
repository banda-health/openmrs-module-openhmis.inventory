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

import java.util.Set;
import java.util.TreeSet;

import org.openmrs.Location;
import org.openmrs.module.openhmis.commons.api.entity.model.BaseSerializableOpenmrsMetadata;

/**
 * Model class that represents a physical location where item stock are stored.
 */
public class Stockroom extends BaseSerializableOpenmrsMetadata {
	public static final long serialVersionUID = 1L;

	private Integer stockroomId;
	private Location location;
	private Set<ItemStock> items;
	private Set<StockOperation> operations;

	/**
	 * Gets the unique database record identifier.
	 * @return The record identifier or {@code null} if the object is new.
	 */
	@Override
	public Integer getId() {
		return stockroomId;
	}

	/**
	 * Sets the unique database record identifier.
	 * @param id The record identifier.
	 */
	@Override
	public void setId(Integer id) {
		stockroomId = id;
	}

	/**
	 * Gets the optional {@link org.openmrs.Location} where this stockroom is located.
	 * @return The stockroom location.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Sets the optional {@link org.openmrs.Location} where this stockroom is located.
	 * @param location The stockroom location.
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * Gets the set of item stock for this stockroom.
	 * @return The item stock.
	 */
	public Set<ItemStock> getItems() {
		return items;
	}

	/**
	 * Sets the set of item stock for this stockroom.
	 * @param items The item stock set.
	 */
	public void setItems(Set<ItemStock> items) {
		this.items = items;
	}

	/**
	 * Adds new item stock to the stockroom. Note that this will result in the full item list being loaded and should only be
	 * used when that is needed. A better alternative to add item stock is to use the
	 * {@link org.openmrs.module.openhmis.inventory.api.IItemStockDataService#save(org.openmrs.OpenmrsObject)} method.
	 * @param item The item stock to add to this stockroom.
	 */
	public void addItem(ItemStock item) {
		if (item != null) {
			if (items == null) {
				items = new TreeSet<ItemStock>();
			}

			item.setStockroom(this);
			items.add(item);
		}
	}

	/**
	 * Removes item stock from the stockroom. Note that this will result in the full item list being loaded and should only
	 * be used when that is needed. A better alternative to remove item stock is to use the
	 * {@link org.openmrs.module.openhmis.inventory.api.IItemStockDataService#purge(org.openmrs.OpenmrsObject)} method.
	 * @param item The item stock to remove.
	 */
	public void removeItem(ItemStock item) {
		if (item != null) {
			if (items == null) {
				return;
			}

			item.setStockroom(null);
			items.remove(item);
		}
	}

	/**
	 * Gets the set of operations for this stockroom.
	 * @return The stockroom operations.
	 */
	public Set<StockOperation> getOperations() {
		return operations;
	}

	/**
	 * Sets the set of operations for this stockroom.
	 * @param operations The stockroom operations.
	 */
	public void setOperations(Set<StockOperation> operations) {
		this.operations = operations;
	}

	/**
	 * Adds an operation to this stockroom. This will not apply the operation or otherwise process it. To apply the operation
	 * use the
	 * {@link org.openmrs.module.openhmis.inventory.api.IStockOperationDataService#applyTransactions(java.util.Collection)}
	 * method. Note that this will result in the full set of operations being loaded and should only be used when that is
	 * needed. The correct way to add operations is to apply them and let the records be created through the normal
	 * processing.
	 * @param operation The operation to add to this stockroom.
	 */
	public void addOperation(StockOperation operation) {
		if (operation != null) {
			if (operations == null) {
				operations = new TreeSet<StockOperation>();
			}

			operations.add(operation);
		}
	}

	/**
	 * Removes an operation from this stockroom. Note that this will result in the full set of operations being loaded and
	 * should only be used when that is needed.
	 * @param operation The operation to remove from this stockroom.
	 */
	public void removeOperation(StockOperation operation) {
		if (operation != null) {
			if (operations == null) {
				return;
			}

			operations.remove(operation);
		}
	}
}
