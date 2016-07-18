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

import org.openmrs.Patient;

/**
 * Model class that represents individual item stock actions performed by a {@link StockOperation}.
 */
public class StockOperationTransaction extends TransactionBase {
	public static final long serialVersionUID = 0L;

	private Stockroom stockroom;
	private Institution institution;
	private Patient patient;

	public StockOperationTransaction() {}

	public StockOperationTransaction(TransactionBase tx) {
		super(tx);
	}

	public Stockroom getStockroom() {
		return stockroom;
	}

	public void setStockroom(Stockroom stockroom) {
		this.stockroom = stockroom;
	}

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
}
