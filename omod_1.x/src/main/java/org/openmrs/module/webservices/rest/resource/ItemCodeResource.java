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

import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemCode;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * REST resource representing an {@link ItemCode}.
 */
@Resource(name = ModuleRestConstants.ITEM_CODE_RESOURCE, supportedClass = ItemCode.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*" })
public class ItemCodeResource extends BaseRestMetadataResource<ItemCode> implements IMetadataDataServiceResource<ItemCode> {
	@Override
	public ItemCode newDelegate() {
		return new ItemCode();
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.removeProperty("name");
		description.addProperty("code");

		return description;
	}

	@Override
	public Class<? extends IMetadataDataService<ItemCode>> getServiceClass() {
		return null;
	}
}
