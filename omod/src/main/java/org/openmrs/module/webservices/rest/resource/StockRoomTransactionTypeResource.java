package org.openmrs.module.webservices.rest.resource;

import org.openmrs.annotation.Handler;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.IStockRoomTransactionTypeDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransactionType;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

@Resource(name = ModuleRestConstants.TRANSACTION_TYPE_RESOURCE, supportedClass=StockRoomTransactionType.class, supportedOpenmrsVersions={"1.9"})
@Handler(supports = { StockRoomTransactionType.class }, order = 0)
public class StockRoomTransactionTypeResource extends BaseRestMetadataResource<StockRoomTransactionType> {
	@Override
	public StockRoomTransactionType newDelegate() {
		return new StockRoomTransactionType();
	}

	@Override
	public Class<? extends IMetadataDataService<StockRoomTransactionType>> getServiceClass() {
		return IStockRoomTransactionTypeDataService.class;
	}

	@Override
	protected DelegatingResourceDescription getDefaultRepresentationDescription() {
		DelegatingResourceDescription description =  super.getDefaultRepresentationDescription();

		description.addProperty("quantityType", Representation.DEFAULT);
		description.addProperty("sourceRequired", Representation.DEFAULT);
		description.addProperty("destinationRequired", Representation.DEFAULT);
		description.addProperty("authorized", Representation.DEFAULT);

		return description;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);

		if (!(rep instanceof RefRepresentation)) {
			description.addProperty("attributeTypes", Representation.REF);
		}

		return description;
	}
}
