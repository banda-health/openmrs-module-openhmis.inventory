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

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Location;

import java.util.List;
import java.util.Set;

public class StockRoom extends BaseOpenmrsMetadata {
	public static final long serialVersionUID = 0L;

	private Integer storeRoomId;
	private Location location;
	private Set<StockRoomItem> items;
	private List<StockRoomTransfer> transfers;

	@Override
	public Integer getId() {
		return storeRoomId;
	}

	@Override
	public void setId(Integer id) {
		storeRoomId = id;
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

	public List<StockRoomTransfer> getTransfers() {
		return transfers;
	}

	public void setTransfers(List<StockRoomTransfer> transfers) {
		this.transfers = transfers;
	}
}
