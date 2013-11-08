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
package org.openmrs.module.webservices.rest.resource;

import org.openmrs.annotation.Handler;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

import java.util.Set;
import java.util.TreeSet;

@Resource(name = ModuleRestConstants.OPERATION_RESOURCE, supportedClass=StockOperation.class, supportedOpenmrsVersions={"1.9"})
@Handler(supports = { StockOperation.class }, order = 0)
public class StockRoomTransactionResource extends BaseRestObjectResource<StockOperation> {

	@Override
	public StockOperation newDelegate() {
		return new StockOperation();
	}

	@Override
	public Class<? extends IObjectDataService<StockOperation>> getServiceClass() {
		return IStockOperationDataService.class;
	}

	@Override
	protected DelegatingResourceDescription getDefaultRepresentationDescription() {
		DelegatingResourceDescription description = super.getDefaultRepresentationDescription();

		// TODO: Update for stock operation fields
		description.addProperty("transactionNumber", Representation.DEFAULT);
		description.addProperty("transactionType", Representation.DEFAULT);
		description.addProperty("status", Representation.DEFAULT);
		description.addProperty("dateCreated", Representation.DEFAULT);

		return description;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);

		// TODO: Update for stock operation fields
		if (rep instanceof FullRepresentation) {
			description.addProperty("source", Representation.REF);
			description.addProperty("destination", Representation.REF);
			description.addProperty("items", Representation.REF);
			description.addProperty("importTransaction", Representation.DEFAULT);
			description.addProperty("creator", Representation.REF);
		}

		return description;
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		// TODO: Update for stock operation fields

		DelegatingResourceDescription description = super.getCreatableProperties();

		description.addProperty("source");
		description.addProperty("destination");
		description.addProperty("items");
		description.addProperty("importTransaction");
		description.addProperty("creator");

		return description;
	}

	@PropertySetter(value="reserved")
	public void setReserved(StockOperation instance, Set<ReservedTransaction> reserved) {
		if (instance.getReserved() == null) {
			instance.setReserved(new TreeSet<ReservedTransaction>());
		}

		BaseRestDataResource.updateCollection(instance.getReserved(), reserved);

		for (ReservedTransaction tx : instance.getReserved()) {
			tx.setOperation(instance);
		}
	}
}

