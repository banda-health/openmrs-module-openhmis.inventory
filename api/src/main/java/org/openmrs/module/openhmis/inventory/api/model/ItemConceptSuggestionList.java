package org.openmrs.module.openhmis.inventory.api.model;

import java.util.List;

import org.openmrs.module.openhmis.commons.api.entity.model.BaseSerializableOpenmrsMetadata;

public class ItemConceptSuggestionList extends BaseSerializableOpenmrsMetadata {

	private static final long serialVersionUID = 0L;
	
	private List<ItemConceptSuggestion> itemConceptSuggestions;
	
	public List<ItemConceptSuggestion> getItemConceptSuggestions() {
	    return itemConceptSuggestions;
    }
	
	public void setItemConceptSuggestions(List<ItemConceptSuggestion> itemConceptSuggestions) {
	    this.itemConceptSuggestions = itemConceptSuggestions;
    }
	
	@Override
    public Integer getId() {
	    return null;
    }

	@Override
    public void setId(Integer id) {
	    
    }
	
}
