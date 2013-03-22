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

public class ImportBatch extends BaseOpenmrsObject {
	public static final long serialVersionUID = 0L;

	private Integer importBatchId;
	private String batchNumber;
	private StockRoomTransaction transaction;

	@Override
	public Integer getId() {
		return importBatchId;
	}

	@Override
	public void setId(Integer id) {
		this.importBatchId = id;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public StockRoomTransaction getTransaction() {
		return transaction;
	}

	public void setTransaction(StockRoomTransaction transaction) {
		this.transaction = transaction;
	}
}
