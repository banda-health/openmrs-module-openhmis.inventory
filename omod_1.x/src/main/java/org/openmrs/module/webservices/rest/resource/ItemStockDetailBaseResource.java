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

import java.util.Date;

import org.openmrs.module.openhmis.commons.api.Utility;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetailBase;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * Base REST resource for classes that represent an {@link ItemStockDetailBase}.
 */
public abstract class ItemStockDetailBaseResource<T extends ItemStockDetailBase> extends BaseRestObjectResource<T> {
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.addProperty("item", Representation.DEFAULT);
		description.addProperty("quantity", Representation.DEFAULT);
		description.addProperty("expiration", Representation.DEFAULT);
		description.addProperty("batchOperation", Representation.REF);
		description.addProperty("calculatedExpiration", Representation.DEFAULT);
		description.addProperty("calculatedBatch", Representation.DEFAULT);

		return description;
	}

	@PropertySetter("expiration")
	public void setExpiration(ItemStockDetailBase instance, String dateText) {
		Date date = Utility.parseOpenhmisDateString(dateText);
		if (date == null) {
			throw new IllegalArgumentException("Could not parse '" + dateText + "' as a date.");
		}

		instance.setExpiration(date);
	}
}
