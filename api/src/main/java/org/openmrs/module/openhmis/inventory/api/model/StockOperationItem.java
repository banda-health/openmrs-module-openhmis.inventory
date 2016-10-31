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

/**
 * Model class that represents the item stock details being changed by the owning {@link StockOperation}.
 */
public class StockOperationItem extends ItemStockDetailBase {
	private StockOperation operation;

	public StockOperation getOperation() {
		return operation;
	}

	public void setOperation(StockOperation operation) {
		this.operation = operation;
	}
}
