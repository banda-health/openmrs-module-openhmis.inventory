package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.openhmis.inventory.api.model.TransactionBase;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * Base REST resource for classes that represent a {@link TransactionBase}.
 */
public abstract class TransactionBaseResource<T extends TransactionBase> extends BaseRestObjectResource<T> {
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.addProperty("dateCreated", Representation.DEFAULT);
		description.addProperty("operation", Representation.REF);
		description.addProperty("batchOperation", Representation.REF);
		description.addProperty("item", Representation.REF);
		description.addProperty("quantity", Representation.DEFAULT);
		description.addProperty("expiration", Representation.DEFAULT);

		return description;
	}
}
