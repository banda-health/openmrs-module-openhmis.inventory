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

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.IStockOperationTypeDataService;
import org.openmrs.module.openhmis.inventory.api.model.IStockOperationType;
import org.openmrs.module.openhmis.inventory.api.model.StockOperation;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationAttribute;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationAttributeType;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationTypeBase;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

@Resource(name = ModuleRestConstants.OPERATION_TYPE_RESOURCE, supportedClass=IStockOperationType.class,
		supportedOpenmrsVersions={"1.9.*", "1.10.*"})
public class StockOperationTypeResource
		extends BaseRestInstanceTypeResource<IStockOperationType, StockOperation, StockOperationAttributeType, StockOperationAttribute> {
	@Override
	public StockOperationTypeBase newDelegate() {
		return null;
	}

	@Override
	public Class<? extends IMetadataDataService<IStockOperationType>> getServiceClass() {
		return IStockOperationTypeDataService.class;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.addProperty("hasSource", Representation.DEFAULT);
		description.addProperty("hasDestination", Representation.DEFAULT);
		description.addProperty("hasRecipient", Representation.DEFAULT);
		description.addProperty("recipientRequired", Representation.DEFAULT);
		description.addProperty("availableWhenReserved", Representation.DEFAULT);

		if (!(rep instanceof RefRepresentation)) {
			description.addProperty("user", Representation.REF);
			description.addProperty("role", Representation.REF);

			description.addProperty("canProcess", findMethod("userCanProcess"));
		}

		return description;
	}

	// Workaround to fix the TypeVariable issue on base generic property
	@PropertySetter("attributeTypes")
	public void setAttributeTypes(IStockOperationType instance, List<StockOperationAttributeType> attributeTypes) {
		super.setAttributeTypes(instance, attributeTypes);
	}

	public static Boolean userCanProcess(IStockOperationType operationType) {
		return operationType.userCanProcess(Context.getAuthenticatedUser());
	}
}

