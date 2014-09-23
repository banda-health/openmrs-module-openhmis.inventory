package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemStock;
import org.openmrs.module.openhmis.inventory.api.model.StockOperationItem;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

@Resource(name = ModuleRestConstants.OPERATION_ITEM_RESOURCE, supportedClass=StockOperationItem.class, supportedOpenmrsVersions={"1.9"})
public class StockOperationItemResource extends ItemStockDetailBaseResource<StockOperationItem> {
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description =  super.getRepresentationDescription(rep);
		if (!(rep instanceof RefRepresentation)) {
			description.addProperty("operation", Representation.DEFAULT);
		}

		return description;
	}

	@Override
	public StockOperationItem newDelegate() {
		return new StockOperationItem();
	}

	@Override
	public Class<? extends IObjectDataService<StockOperationItem>> getServiceClass() {
		return null;
	}
}
