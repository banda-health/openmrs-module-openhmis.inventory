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
package org.openmrs.module.openhmis.inventory.api.search;

import org.openmrs.Patient;
import org.openmrs.module.openhmis.inventory.api.model.Recipient;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;

public class StockOperationTemplate extends StockOperation {
	@Override
	public void setSource(Stockroom newSource) {
		this.source = newSource;
	}

	@Override
	public void setDestination(Stockroom newDestination) {
		this.destination = newDestination;
	}

	@Override
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}
}
