package org.openmrs.module.webservices.rest.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.IItemConceptSuggestionDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemConceptSuggestion;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.search.ItemConceptSuggestionSearchHandler;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

@Resource(name = ModuleRestConstants.ITEM_CONCEPT_SUGGESTION_RESOURCE, supportedClass = ItemConceptSuggestion.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class ItemConceptSuggestionResource
        extends BaseRestMetadataResource<ItemConceptSuggestion>
        implements IMetadataDataServiceResource<ItemConceptSuggestion> {

	@Override
	public ItemConceptSuggestion newDelegate() {
		return new ItemConceptSuggestion();
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.removeProperty("name");
		description.removeProperty("description");
		description.addProperty("item", Representation.REF);
		description.addProperty("conceptName");
		description.addProperty("conceptUuid");
		description.addProperty("conceptAccepted");

		return description;
	}

	@PropertySetter(value = "conceptAccepted")
	public void setConceptAccepted(ItemConceptSuggestion instance, Boolean conceptAccepted) {
		instance.setConceptAccepted(conceptAccepted);
	}

	@Override
	public Class<? extends IMetadataDataService<ItemConceptSuggestion>> getServiceClass() {
		return IItemConceptSuggestionDataService.class;
	}

	@Override
	protected PageableResult doGetAll(RequestContext context) {
		return ItemConceptSuggestionSearchHandler.doSearch(Context.getService(IItemConceptSuggestionDataService.class),
		    context);
	}

}
