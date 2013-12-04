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

import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.commons.api.f.Action2;
import org.openmrs.module.openhmis.inventory.api.IStockOperationDataService;
import org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

@Resource(name = ModuleRestConstants.OPERATION_RESOURCE, supportedClass=StockOperation.class, supportedOpenmrsVersions={"1.9"})
public class StockOperationResource extends BaseRestObjectResource<StockOperation> {

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

		description.addProperty("status", Representation.DEFAULT);
		description.addProperty("reserved", Representation.DEFAULT);
		description.addProperty("transactions", Representation.DEFAULT);
		description.addProperty("operationNumber", Representation.DEFAULT);
		description.addProperty("source", Representation.DEFAULT);
		description.addProperty("destination", Representation.DEFAULT);
		description.addProperty("patient", Representation.DEFAULT);

		return description;
	}

	@PropertySetter(value="reserved")
	public void setReserved(final StockOperation instance, Set<ReservedTransaction> reserved) {
		if (instance.getReserved() == null) {
			instance.setReserved(new TreeSet<ReservedTransaction>());
		}

		BaseRestDataResource.syncCollection(instance.getReserved(), reserved,
				new Action2<Collection<ReservedTransaction>, ReservedTransaction>() {
					@Override
					public void apply(Collection<ReservedTransaction> collection, ReservedTransaction reserved) {
						instance.addReserved(reserved);
					}
				},
				new Action2<Collection<ReservedTransaction>, ReservedTransaction>() {
					@Override
					public void apply(Collection<ReservedTransaction> collection, ReservedTransaction reserved) {
						instance.removeReserved(reserved);
					}
				});
	}
}

