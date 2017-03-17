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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.IItemStockDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * REST resource representing an {@link ItemStock}.
 */
@Resource(name = ModuleRestConstants.ITEM_STOCK_RESOURCE, supportedClass = ItemStock.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.*" })
public class ItemStockResource extends BaseRestObjectResource<ItemStock> {
	@Override
	public ItemStock newDelegate() {
		return new ItemStock();
	}

	@Override
	public Class<? extends IObjectDataService<ItemStock>> getServiceClass() {
		return IItemStockDataService.class;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.addProperty("stockroom", Representation.REF);
		description.addProperty("item", Representation.REF);
		description.addProperty("quantity", Representation.DEFAULT);
		description.addProperty("details", Representation.REF);

		return description;
	}

	@PropertyGetter("details")
	public Collection<ItemStockDetail> getItemStockDetails(ItemStock stock) {
		if (stock.getDetails() == null || stock.getDetails().size() == 0) {
			return new ArrayList<ItemStockDetail>();
		} else if (stock.getDetails().size() == 1) {
			return stock.getDetails();
		}

		List<ItemStockDetail> details = new ArrayList<ItemStockDetail>(stock.getDetails());
		Collections.sort(details, new Comparator<ItemStockDetail>() {
			@Override
			public int compare(ItemStockDetail o1, ItemStockDetail o2) {
				Date e1 = o1.getExpiration() == null ? new Date(0L) : o1.getExpiration();
				Date e2 = o2.getExpiration() == null ? new Date(0L) : o2.getExpiration();
				int result = e1.compareTo(e2);
				if (result == 0) {
					if (o1.getBatchOperation() != null) {
						result = o1.getBatchOperation().compareTo(o2.getBatchOperation());
					} else if (o2.getBatchOperation() != null) {
						// The first operation has no batch while the second does so make the first op, first
						return -1;
					}
				}

				return result;
			}
		});

		return details;
	}
}
