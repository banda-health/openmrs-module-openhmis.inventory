/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.1 (the "License"); you may not use this file except in
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
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.IStockRoomDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockRoom;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

@Resource(name = ModuleRestConstants.STOCK_ROOM_RESOURCE, supportedClass=StockRoom.class, supportedOpenmrsVersions={"1.9"})
@Handler(supports = { StockRoom.class }, order = 0)
public class StockRoomResource extends BaseRestMetadataResource<StockRoom> {
	@Override
	protected DelegatingResourceDescription getDefaultRepresentationDescription() {
		DelegatingResourceDescription desc = super.getDefaultRepresentationDescription();

		return desc;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);

		if (rep instanceof RefRepresentation) {
			description.addProperty("location", Representation.REF);
		} else if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description.addProperty("location", Representation.REF);
			//description.addProperty("items", Representation.REF);
			//description.addProperty("transactions", Representation.REF);
		}

		return description;
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = super.getCreatableProperties();
		description.addProperty("name");
		description.addProperty("location");

		return description;
	}

	@Override
	public StockRoom newDelegate() {
		return new StockRoom();
	}

	@Override
	public Class<? extends IMetadataDataService<StockRoom>> getServiceClass() {
		return IStockRoomDataService.class;
	}

	/*@PropertySetter(value="items")
	public void setItems(StockRoom instance, Set<StockRoomItem> items) {
		if (instance.getItems() == null) {
			instance.setItems(new TreeSet<StockRoomItem>());
		}

		BaseRestDataResource.updateCollection(instance.getItems(), items);

		for (StockRoomItem item : instance.getItems()) {
			item.setStockRoom(instance);
		}
	}*/

	/*@PropertySetter(value="transactions")
	public void setTransactions(StockRoom instance, Set<StockRoomTransaction> transactions) {
		if (instance.getTransactions() == null) {
			instance.setTransactions(new TreeSet<StockRoomTransaction>());
		}

		BaseRestDataResource.updateCollection(instance.getTransactions(), transactions);
	}*/
}
