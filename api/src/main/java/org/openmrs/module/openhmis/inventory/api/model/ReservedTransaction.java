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
 * Model class that represents individual item stock actions while an operation is pending.
 */
public class ReservedTransaction extends TransactionBase {
	public static final long serialVersionUID = 0L;

	private Boolean available;

	public ReservedTransaction() {}

	public ReservedTransaction(TransactionBase tx) {
		super(tx);
	}

	public ReservedTransaction(StockOperationItem item) {
		super(item);
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}
}
