package org.openmrs.module.webservices.rest.resource;

import org.openmrs.annotation.Handler;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransaction;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransactionItem;
import org.openmrs.module.openhmis.inventory.api.model.StockRoomTransactionType;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

import java.util.Set;
import java.util.TreeSet;

@Resource(name = ModuleRestConstants.TRANSACTION_RESOURCE, supportedClass=StockRoomTransaction.class, supportedOpenmrsVersions={"1.9"})
@Handler(supports = { StockRoomTransaction.class }, order = 0)
public class StockRoomTransactionResource extends BaseRestObjectResource<StockRoomTransaction> {

	@Override
	public StockRoomTransaction newDelegate() {
		return new StockRoomTransaction();
	}

	@Override
	public Class<? extends IObjectDataService<StockRoomTransaction>> getServiceClass() {
		return null;
	}

	@Override
	protected DelegatingResourceDescription getDefaultRepresentationDescription() {
		DelegatingResourceDescription description = super.getDefaultRepresentationDescription();

		description.addProperty("transactionNumber", Representation.DEFAULT);
		description.addProperty("transactionType", Representation.DEFAULT);
		description.addProperty("status", Representation.DEFAULT);
		description.addProperty("dateCreated", Representation.DEFAULT);

		return description;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);

		if (!(rep instanceof RefRepresentation)) {
			description.addProperty("source", Representation.REF);
			description.addProperty("destination", Representation.REF);
			description.addProperty("items", Representation.REF);
			description.addProperty("isImportTransaction", Representation.DEFAULT);
			description.addProperty("creator", Representation.REF);
		}

		return description;
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = super.getCreatableProperties();

		description.addProperty("source");
		description.addProperty("destination");
		description.addProperty("items");
		description.addProperty("isImportTransaction");
		description.addProperty("creator");

		return description;
	}

	@PropertySetter(value="items")
	public void setItems(StockRoomTransaction instance, Set<StockRoomTransactionItem> items) {
		if (instance.getItems() == null) {
			instance.setItems(new TreeSet<StockRoomTransactionItem>());
		}

		BaseRestDataResource.updateCollection(instance.getItems(), items);

		for (StockRoomTransactionItem item : instance.getItems()) {
			item.setTransaction(instance);
		}
	}
}

