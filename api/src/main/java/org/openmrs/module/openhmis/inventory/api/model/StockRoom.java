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

import org.openmrs.Location;
import org.openmrs.module.openhmis.commons.api.entity.model.BaseSerializableOpenmrsMetadata;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class StockRoom extends BaseSerializableOpenmrsMetadata {
	public static final long serialVersionUID = 0L;

	private Integer stockRoomId;
	private Location location;
	private Set<StockRoomItem> items;
	private Set<StockOperation> operations;

	@Override
	public Integer getId() {
		return stockRoomId;
	}

	@Override
	public void setId(Integer id) {
		stockRoomId = id;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Set<StockRoomItem> getItems() {
		return items;
	}

	public void setItems(Set<StockRoomItem> items) {
		this.items = items;
	}

	public void addItem(StockRoomItem item) {
		if (item != null) {
			if (items == null) {
				items = new TreeSet<StockRoomItem>();
			}

			item.setStockRoom(this);
			items.add(item);
		}
	}

	public void removeItem(StockRoomItem item) {
		if (item != null) {
			if (items == null) {
				return;
			}

			item.setStockRoom(null);
			items.remove(item);
		}
	}

	public Set<StockOperation> getOperations() {
		return operations;
	}

	public void setOperations(Set<StockOperation> operations) {
		this.operations = operations;
	}

	public void addOperation(StockOperation operation) {
		if (operation != null) {
			if (operations == null) {
				operations = new TreeSet<StockOperation>();
			}

			operations.add(operation);
		}
	}

	public void removeOperation(StockOperation operation) {
		if (operation != null) {
			if (operations == null) {
				return;
			}

			operations.remove(operation);
		}
	}
}
