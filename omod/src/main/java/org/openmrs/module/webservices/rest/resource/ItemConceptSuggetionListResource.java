package org.openmrs.module.webservices.rest.resource;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.entity.IMetadataDataService;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.openmrs.module.openhmis.inventory.api.model.ItemConceptSuggestion;
import org.openmrs.module.openhmis.inventory.api.model.ItemConceptSuggestionList;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

import java.util.ArrayList;
import java.util.List;

@Resource(name= ModuleRestConstants.ITEM_CONCEPT_SUGGESTION_LIST_RESOURCE, supportedClass=ItemConceptSuggestionList.class,
		supportedOpenmrsVersions={"1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class ItemConceptSuggetionListResource
		extends BaseRestMetadataResource<ItemConceptSuggestionList>
		implements IMetadataDataServiceResource<ItemConceptSuggestionList> {

	@Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep)  {
        DelegatingResourceDescription description = super.getRepresentationDescription(rep);
        description.removeProperty("name");
        description.removeProperty("description");
        description.addProperty("itemConceptSuggestions");

        return description;
    }
	
	@Override
    public ItemConceptSuggestionList newDelegate() {
	    return new ItemConceptSuggestionList();
    }

	@Override
    public Class<? extends IMetadataDataService<ItemConceptSuggestionList>> getServiceClass() {
	    return null;
    }
	
	@PropertySetter("itemConceptSuggestions")
	public void setItemConceptSuggestionList(ItemConceptSuggestionList instance, List<ItemConceptSuggestion> itemConceptSuggestions) {
		instance.setItemConceptSuggestions(itemConceptSuggestions);
	}
	
	@Override
	public ItemConceptSuggestionList save(ItemConceptSuggestionList itemConceptSuggestionList) {
		List<Item> acceptedItemConceptMappings = processAcceptedItems(itemConceptSuggestionList);
		IItemDataService itemDataService = Context.getService(IItemDataService.class);
		itemDataService.saveAll(acceptedItemConceptMappings);
	    return null;
	}

	private List<Item> processAcceptedItems(ItemConceptSuggestionList itemConceptSuggestionList) {
	    List<Item> itemsToSave = new ArrayList<Item>();
		ConceptService conceptService = Context.getConceptService();
		for (ItemConceptSuggestion itemConceptSuggestion : itemConceptSuggestionList.getItemConceptSuggestions()) {
			if (itemConceptSuggestion.isConceptAccepted()) {
				Item itemToSave = itemConceptSuggestion.getItem();
				Concept concept = conceptService.getConceptByUuid(itemConceptSuggestion.getConceptUuid());
				itemToSave.setConcept(concept);
				itemToSave.setConceptAccepted(true);
				itemsToSave.add(itemToSave);
			}
        }
		return itemsToSave;
    }
	
}
