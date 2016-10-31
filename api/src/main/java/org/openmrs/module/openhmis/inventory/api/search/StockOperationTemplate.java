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
package org.openmrs.module.openhmis.inventory.api.search;

import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.module.openhmis.inventory.api.model.Institution;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;

/**
 * Search template to override {@link StockOperation} model properties that can cause issues when performing a template
 * search.
 */
public class StockOperationTemplate extends StockOperation {
	private Stockroom stockroom;

	@Override
	public void setSource(Stockroom newSource) {
		this.source = newSource;
	}

	@Override
	public void setDestination(Stockroom newDestination) {
		this.destination = newDestination;
	}

	@Override
	public void setPatient(Patient newPatient) {
		this.patient = newPatient;
	}

	@Override
	public void setInstitution(Institution newInstitution) {
		this.institution = newInstitution;
	}

	public Stockroom getStockroom() {
		return stockroom;
	}

	public void setStockroom(Stockroom stockroom) {
		this.stockroom = stockroom;
	}

	public void setItem(Item item) {
		addItem(item, 0);
	}

	public Item getItem() {
		Set<StockOperationItem> items = getItems();
		Item item = null;
		if (items != null && items.size() > 0) {
			for (StockOperationItem stockOperationItem : items) {
				item = stockOperationItem.getItem();
				break;
			}
		}
		return item;
	}

}
