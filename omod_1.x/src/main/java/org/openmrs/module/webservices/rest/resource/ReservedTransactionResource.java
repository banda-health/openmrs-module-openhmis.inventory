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
package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.ReservedTransaction;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * REST resource representing a {@link ReservedTransaction}.
 */
@Resource(name = ModuleRestConstants.RESERVATION_TRANSACTION_RESOURCE, supportedClass = ReservedTransaction.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*" })
public class ReservedTransactionResource extends TransactionBaseResource<ReservedTransaction> {
	@Override
	public ReservedTransaction newDelegate() {
		return new ReservedTransaction();
	}

	@Override
	public Class<? extends IObjectDataService<ReservedTransaction>> getServiceClass() {
		return null;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.addProperty("available", Representation.DEFAULT);

		return description;
	}
}
